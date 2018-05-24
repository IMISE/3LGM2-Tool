package de.imise.tool3lgm.graphtools.metamodel;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.util.Pair;

/**
 * @author AXS
 */
public abstract class GraphViewDefinition {

    protected abstract Class<? extends ModelElement>[] getPaintableNodes();

    private final Set<Class<? extends ModelElement>> allPaintableNodes;

    private Map<Class<? extends ModelElement>, MetaPath> classToConfigurationPaths = null;

    public GraphViewDefinition() {
        ImmutableSet.Builder<Class<? extends ModelElement>> allPaintableNodesSetBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
        for (Class<? extends ModelElement> paintableNodeClass : getPaintableNodes()) {
            allPaintableNodesSetBuilder.add(paintableNodeClass);
        }
        //Diese Klassen müssen noch hinzugefügt werden, da sie auch dargestellt werden
        allPaintableNodesSetBuilder.add(Knickpunkt.class);
        allPaintableNodesSetBuilder.add(Textfield.class);
        allPaintableNodes = allPaintableNodesSetBuilder.build();
    }

    /**
     * Liefert <code>true</code>, wenn die Elementklasse nicht in der Grafik dargestellt wird.
     *
     * @param elementClass
     * @return
     */
    public final boolean isPaintable(final Class<? extends ModelElement> elementClass) {
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            return isPaintable(Edge.getStartClass(edgeClass)) && isPaintable(Edge.getEndClass(edgeClass));
        }
        return allPaintableNodes.contains(elementClass);
    }

    /**
     * Liefert die Anzahl der nicht dargestellten instanziierbaren Unterklassen von {@link ModelElement}
     *
     * @return
     */
    public final int getPaintableNodesCount() {
        return allPaintableNodes.size();
    }

    /**
     * Liefert die MetaPfade, die als Interebenenbeziehungen dargestellt werden sollen
     *
     * @return
     */
    protected abstract MetaPath[] getConfigurationPaths();

    public final MetaPath getInterLayerMetaPath(final Class<? extends ModelElement> elementClass) {
        //es muss ein lazy-init sein, weil es sonst zu einer Init-Exception in der Reflection-Methode Edge.getStartClass(...)
        if (classToConfigurationPaths == null) {
            classToConfigurationPaths = new HashMap<>();
            //Map mit den Klassen zu ihren Konfigurationspfaden speichern
            for (MetaPath metaPath : getConfigurationPaths()) {
                Class<? extends ModelElement>[] instanciableAssignableClasses = ModelConstants.getInstanciableAssignableClasses(metaPath.getStartClass());
                for (Class<? extends ModelElement> instanciableElementClass : instanciableAssignableClasses) {
                    classToConfigurationPaths.put(instanciableElementClass, metaPath);
                }
            }
        }
        return classToConfigurationPaths.get(elementClass);
    }

    /**
     * Liefert alle Elementklassen, die man in der Grafik ausblenden kann, wenn sie über keine Kante der zugehörigen
     * Kantenart verfügen.
     *
     * @return
     */
    public List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> getHidableIfNotConnected() {
        return null;
    }

    private final void initDefaultElementLayoutInternal() {
        setDefaultLayout(Textfield.class, GraphElementLayout.SHAPE.rechteck, new Color(0, 0, 0, 0));
        defaultElementsLayoutDefinition.setStandardSize(Knickpunkt.class, 10, 10);
    }

    /** Initialisiert die Defaults für das Layout der Elemente */
    protected abstract void initDefaultElementLayout();

    private ElementsLayoutDefinition defaultElementsLayoutDefinition;

    public ElementsLayoutDefinition getDefaultElementsLayout() {
        if (defaultElementsLayoutDefinition == null) {
            defaultElementsLayoutDefinition = new ElementsLayoutDefinition(false);
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
}
