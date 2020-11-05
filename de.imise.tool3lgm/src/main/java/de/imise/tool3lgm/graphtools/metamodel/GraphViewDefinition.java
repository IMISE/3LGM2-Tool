package de.imise.tool3lgm.graphtools.metamodel;

import java.awt.Color;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.util.pair.Pair;

/**
 * @author AXS
 */
public abstract class GraphViewDefinition {

    /** Zu Grunde liegendes MetaModel */
    protected final MetaModel metaModel;

    /**
     * Liefert eine Liste aller metamodellabhängigen Knoten, die in der Grafik
     * dargestellt werden. Die Reihenfolge in dieser Liste legt fest, in welcher
     * Reihenfolge die Elemente in dem gloabeln LayoutEditor angezeigt werden
     *
     * @return
     */
    protected abstract Class<? extends ModelElement>[] getPaintableNodes();

    /**
     * Liste aller Knoten, die in der Grafik sichtbar sind. Das sind alle Knoten
     * metammodellabhängigen Knoten aus paintableNodes plus die
     * metamodellunabhängigen Knickpunkte, Layer und Textfelder.
     */
    private final Set<Class<? extends ModelElement>> allPaintableNodes;

    /**
     * Liste aller metmodellabhängigen Knoten, die in der Grafik angezeigt
     * werden
     */
    private final List<Class<? extends ModelElement>> metaModelSpecificPaintableNodes;

    private Map<Class<? extends ModelElement>, SimpleMetaPath> classToConfigurationPaths = null;

    /**
     * @param metaModel
     */
    public GraphViewDefinition(final MetaModel metaModel) {
        this.metaModel = metaModel;
        ImmutableSet.Builder<Class<? extends ModelElement>> allPaintableNodesSetBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
        metaModelSpecificPaintableNodes = ImmutableList.copyOf(getPaintableNodes());
        for (Class<? extends ModelElement> paintableNodeClass : metaModelSpecificPaintableNodes) {
            //alle Paintbale-Klassen müssen instanziierbar sein. Hier dürfen keine abstrakten Klassen angegeben werden
            if (Modifier.isAbstract(paintableNodeClass.getModifiers())) {
                throw new Error("Only non abstract classes are allowed as paintable element classes! " + paintableNodeClass.getName() + " is abstract!");
            }
            allPaintableNodesSetBuilder.add(paintableNodeClass);
        }
        //Diese Klassen müssen noch hinzugefügt werden, da sie auch dargestellt werden
        allPaintableNodesSetBuilder.add(Bendpoint.class);
        allPaintableNodesSetBuilder.add(Textfield.class);
        allPaintableNodes = allPaintableNodesSetBuilder.build();
    }

    /**
     * Liefert <code>true</code>, wenn die Elementklasse nicht in der Grafik
     * dargestellt wird.
     *
     * @param elementClass
     * @return
     */
    public final boolean isPaintable(final Class<? extends ModelElement> elementClass) {
        //Achtung: bei Kantenklassen, die auf abstrakten Knotenklassen definiert sind, die selbst nicht paintable sind, kommt false zurück, auch wenn die
        //konkreten Unterklassen painable sind und es die Kante somit auch wäre. Hast man ein Edge-Object (und nicht nur die Kantenklasse) dann kann man
        //auf der Egde isPainable() aufrufen und bekommt das richtige Ergebnis!
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            return isPaintable(Edge.getStartClass(edgeClass)) && isPaintable(Edge.getEndClass(edgeClass));
        }
        return allPaintableNodes.contains(elementClass);
    }

    /**
     * @return Liste aller metmodellabhängigen Knoten, die in der Grafik
     *         angezeigt werden
     */
    public List<Class<? extends ModelElement>> getMetaModelSpecificPaintableNodes() {
        return metaModelSpecificPaintableNodes;
    }

    /**
     * Liefert die MetaPfade, die als Interebenenbeziehungen dargestellt werden
     * sollen
     *
     * @return
     */
    protected abstract SimpleMetaPath[] getConfigurationPaths();

    /**
     * Liefert den MetaPfade, der als Interebenenbeziehung dargestellt werden
     * soll, wenn es einen solchen gibt.
     *
     * @param me
     * @return
     */
    public final SimpleMetaPath getInterLayerMetaPath(final ModelElement me) {
        return getInterLayerMetaPath(me.getClass());
    }

    /**
     * Liefert den MetaPfade, der als Interebenenbeziehung dargestellt werden
     * soll, wenn es einen solchen gibt.
     *
     * @param elementClass
     * @return
     */
    public final SimpleMetaPath getInterLayerMetaPath(final Class<? extends ModelElement> elementClass) {
        //es muss ein lazy-init sein, weil es sonst zu einer Init-Exception in der Reflection-Methode Edge.getStartClass(...)
        if (classToConfigurationPaths == null) {
            classToConfigurationPaths = new HashMap<>();
            //Map mit den Klassen zu ihren Konfigurationspfaden speichern
            for (SimpleMetaPath metaPath : getConfigurationPaths()) {
                Class<? extends ModelElement> startClass = metaPath.getStartClass();
                for (Class<? extends ModelElement> instanciableElementClass : metaModel.getInstanciableAssignableClasses(startClass)) {
                    classToConfigurationPaths.put(instanciableElementClass, metaPath);
                }
            }
        }
        return classToConfigurationPaths.get(elementClass);
    }

    /**
     * Liefert alle Elementklassen, die man in der Grafik ausblenden kann, wenn
     * sie über keine Kante der zugehörigen Kantenart verfügen.
     *
     * @return
     */
    public List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> getHidableIfNotConnected() {
        return null;
    }

    private final void initDefaultElementLayoutInternal() {
        setDefaultLayout(Textfield.class, GraphElementLayout.SHAPE.rechteck, new Color(0, 0, 0, 0));
        defaultElementsLayoutDefinition.setStandardSize(Bendpoint.class, 10, 10);
    }

    /** Initialisiert die Defaults für das Layout der Elemente */
    protected abstract void initDefaultElementLayout();

    private ElementsLayoutDefinition defaultElementsLayoutDefinition;

    public ElementsLayoutDefinition getDefaultElementsLayout() {
        if (defaultElementsLayoutDefinition == null) {
            defaultElementsLayoutDefinition = new ElementsLayoutDefinition(null);
            initDefaultElementLayoutInternal();
            initDefaultElementLayout();
        }
        return defaultElementsLayoutDefinition;
    }

    protected final void setDefaultLayout(final Class<? extends ModelElement> elementClass, final GraphElementLayout.SHAPE defaultShape, final Color defaultBackground) {
        setDefaultLayout(elementClass, defaultShape, defaultBackground, GraphElementLayout.STANDARD_WIDTH, GraphElementLayout.STANDARD_HEIGHT);
    }

    protected final void setDefaultLayout(final Class<? extends ModelElement> elementClass, final GraphElementLayout.SHAPE defaultShape, final Color defaultBackground, final int defaultWidth, final int defaultHeight) {
        defaultElementsLayoutDefinition.setStandardForm(elementClass, defaultShape);
        defaultElementsLayoutDefinition.setStandardBackGroundColor(elementClass, defaultBackground);
        defaultElementsLayoutDefinition.setStandardSize(elementClass, defaultWidth, defaultHeight);
    }

    /**
     * Leere Implementierrung der {@link GraphViewDefinition}
     *
     * @author AXS (6 Jun 2019)
     */
    public static class DefaultGraphViewDefinitionAdapter extends GraphViewDefinition {

        /**
         * @param metaModel
         */
        public DefaultGraphViewDefinitionAdapter(final MetaModel metaModel) {
            super(metaModel);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Class<? extends ModelElement>[] getPaintableNodes() {
            return new Class[0];
        }

        @Override
        protected SimpleMetaPath[] getConfigurationPaths() {
            return new SimpleMetaPath[0];
        }

        @Override
        protected void initDefaultElementLayout() {
        }
    }

}
