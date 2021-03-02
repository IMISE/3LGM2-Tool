package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.InterLayerLineRenderType.LINE_TYPE_SOLID;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.dialog.LayoutEditor;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.graph.DefaultElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.LayoutColor;
import de.imise.tool3lgm.graphtools.view.graph.Shape;
import de.imise.util.pair.Pair;

/**
 * @author AXS
 */
public abstract class GraphViewDefinition {

    /**
     * @author AXS (13.01.2021)
     */
    public static final class AdditionalGraphShapeData {

        /**
         *
         */
        public final MetaPath metaPath;

        /**
         *
         */
        public final Shape shape;

        /**
         * @param metaPath
         * @param shape
         */
        public AdditionalGraphShapeData(final MetaPath metaPath, final Shape shape) {
            this.metaPath = metaPath;
            this.shape = shape;
        }
    }

    /**
     * The way configurations (interplane relationships in the graph) should be
     * rendered. That is, how the line should be drawn.
     *
     * @author AXS (27.01.2021)
     */
    public enum InterLayerLineRenderType {
        LINE_TYPE_SOLID {
            @Override
            public Stroke getStroke() {
                return GraphElementLayout.NORMAL_STROKE;
            }
        },
        LINE_TYPE_DASHED {
            @Override
            public Stroke getStroke() {
                return GraphElementLayout.NORMAL_STROKE_DASHED;
            }
        },
        LINE_TYPE_DOTTED {
            @Override
            public Stroke getStroke() {
                return GraphElementLayout.NORMAL_STROKE_DOTTED;
            }
        };

        /**
         * @return the stroke this type should be rendered
         */
        public abstract Stroke getStroke();

        /**
         * @return the stroke this type should be rendered if the configuration
         *         is an analysis result
         */
        public Stroke getAnalysisResultStroke() {
            Stroke stroke = getStroke();
            if (!(stroke instanceof BasicStroke)) {
                return stroke;
            }
            BasicStroke s = (BasicStroke) getStroke();
            //ANalysis resualt has the same line style but 4 px more line width
            s = new BasicStroke(s.getLineWidth() + 4, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());
            return s;
        }

    }

    /** Zu Grunde liegendes MetaModel */
    protected final MetaModel metaModel;

    /**
     * Set of all node classes visible in the graphic. These are all nodes
     * metamodel-dependent nodes plus the metamodel-independent breakpoints,
     * layers and text fields.
     */
    private final Set<Class<? extends ModelElement>> allPaintableNodes;

    /**
     * List of all nodes classes on {@link #paintableNodesSet}, but in the order
     * they were specified when they were defined. This is the order in which
     * they are displayed in the {@link LayoutEditor}.
     */
    private final List<Class<? extends ModelElement>> metaModelSpecificPaintableNodes;

    /**
     * Maps from a class to the metapath which should be rendered to elements on
     * the next layer.
     */
    private Map<Class<? extends ModelElement>, MetaPath> classToInterLayerMetaPath;

    /**
     * Maps from a metapath of values from {@link #classToInterLayerMetaPath} to
     * the RenderType that specifies how such an interlayer relationship should
     * be drawn.
     */
    private Map<MetaPath, InterLayerLineRenderType> interLayerMetaPathToRenderType;

    /**
     * @param metaModel
     */
    public GraphViewDefinition(final MetaModel metaModel) {
        this.metaModel = metaModel;
        metaModelSpecificPaintableNodes = new ArrayList<>();
        getDefaultElementsLayout(); //initializes the defaultElementsLayoutDefinition and fills the metaModelSpecificPaintableNodes

        ImmutableSet.Builder<Class<? extends ModelElement>> allPaintableNodesSetBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
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
    protected abstract MetaPath[] getInterLayerMetaPaths();

    /**
     * Returns an array of {@link InterLayerLineRenderType}. All metapaths on
     * {@link #getInterLayerMetaPaths()} will be drawn with one of the specified
     * render types in the order they are defined.
     *
     * @return the InterLayerLineRenderTypes
     */
    protected InterLayerLineRenderType[] getInterLayerLineRenderTypes() {
        return new InterLayerLineRenderType[] {
                LINE_TYPE_SOLID
        };
    }

    /**
     * Liefert den MetaPfade, der als Interebenenbeziehung dargestellt werden
     * soll, wenn es einen solchen gibt.
     *
     * @param me
     * @return
     */
    public final MetaPath getInterLayerMetaPath(final ModelElement me) {
        return getInterLayerMetaPath(me.getClass());
    }

    /**
     * We need this to fill the maps {@link #classToInterLayerMetaPath} and
     * {@link #interLayerMetaPathToRenderType} with the same metapath objects.
     * It will be initialized in {@link #getInterLayerMetaPath(Class)} and used
     * in {@link #getInterLayerLineRenderType(MetaPath)}
     */
    private MetaPath[] originalDefinedInterLayerMetaPathObjects;

    /**
     * Returns the MetaPath for the given element class, which should be
     * rendered as an interlevel relationship, if there is one.
     *
     * @param elementClass
     * @return
     */
    public final MetaPath getInterLayerMetaPath(final Class<? extends ModelElement> elementClass) {
        //es muss ein lazy-init sein, weil es sonst zu einer Init-Exception in der Reflection-Methode Edge.getStartClass(...)
        if (classToInterLayerMetaPath == null) {
            classToInterLayerMetaPath = new HashMap<>();
            // lazy init here too!
            if (originalDefinedInterLayerMetaPathObjects == null) {
                originalDefinedInterLayerMetaPathObjects = getInterLayerMetaPaths();
            }
            //Map mit den Klassen zu ihren Konfigurationspfaden speichern
            for (MetaPath metaPath : originalDefinedInterLayerMetaPathObjects) {
                Class<? extends ModelElement> startClass = metaPath.getStartClass();
                for (Class<? extends ModelElement> instanciableElementClass : metaModel.getInstanciableAssignableClasses(startClass)) {
                    classToInterLayerMetaPath.put(instanciableElementClass, metaPath);
                }
            }
        }
        return classToInterLayerMetaPath.get(elementClass);
    }

    /**
     * Returns the render type for a metapath drawn as an interlayer
     * relationship.
     *
     * @param metaPath
     * @return
     */
    public final InterLayerLineRenderType getInterLayerLineRenderType(final MetaPath metaPath) {
        //must be lazy-init!
        if (interLayerMetaPathToRenderType == null) {
            interLayerMetaPathToRenderType = new HashMap<>();
            InterLayerLineRenderType[] interLayerLineRenderTypes = getInterLayerLineRenderTypes();
            // lazy init here too!
            int j = 0;
            for (int i = 0; i < originalDefinedInterLayerMetaPathObjects.length; i++) {
                interLayerMetaPathToRenderType.put(originalDefinedInterLayerMetaPathObjects[i], interLayerLineRenderTypes[j++]);
                if (j == interLayerLineRenderTypes.length) {
                    j = 0;
                }
            }
        }
        return interLayerMetaPathToRenderType.get(metaPath);
    }

    /**
     * @return
     */
    protected List<AdditionalGraphShapeData> getAdditionalGraphShapeData() {
        return ImmutableList.of();
    }

    /**
     * @param me
     * @return
     */
    public final AdditionalGraphShapeData getAdditionalGraphShapeData(final ModelElement me) {
        return getAdditionalGraphShapeData(me.getClass());
    }

    /**
     * @param elementClass
     * @return
     */
    public final AdditionalGraphShapeData getAdditionalGraphShapeData(final Class<? extends ModelElement> elementClass) {
        List<AdditionalGraphShapeData> additionalGraphShapeData = getAdditionalGraphShapeData();
        for (AdditionalGraphShapeData data : additionalGraphShapeData) {
            MetaPath metaPath = data.metaPath;
            if (metaPath.isStartClass(elementClass)) {
                return data;
            }
        }
        return null;
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

    /**
     *
     */
    private final void initDefaultElementLayoutInternal() {
        //do not add this via the setDefaultLayout(...) fuctions because this would add the Textfield or Bendpoits to the metaModelSpecificPaintableNodes
        defaultElementsLayoutDefinition.setStandardForm(Textfield.class, Shape.rechteck);
        defaultElementsLayoutDefinition.setStandardBackGroundColor(Textfield.class, new Color(0, 0, 0, 0));
        defaultElementsLayoutDefinition.setStandardSize(Textfield.class, GraphElementLayout.STANDARD_WIDTH, GraphElementLayout.STANDARD_HEIGHT);
        defaultElementsLayoutDefinition.setStandardSize(Bendpoint.class, 10, 10);
    }

    /**
     * Initializes the defaults for the layout of the elements. The order in
     * which a DefaultLayout is added to an element class via the
     * {@link #setDefaultLayout(Class, Shape, Color, int, int)} function of an
     * element class determines the order in which the elements are displayed in
     * the global layout editor.
     */
    protected abstract void initDefaultElementLayout();

    /**
     *
     */
    private DefaultElementsLayoutDefinition defaultElementsLayoutDefinition;

    /**
     * @return
     */
    public DefaultElementsLayoutDefinition getDefaultElementsLayout() {
        if (defaultElementsLayoutDefinition == null) {
            defaultElementsLayoutDefinition = new DefaultElementsLayoutDefinition(null);
            initDefaultElementLayoutInternal();
            initDefaultElementLayout();
        }
        return defaultElementsLayoutDefinition;
    }

    /**
     * @param elementClass
     * @param defaultShape
     * @param defaultBackground
     */
    protected final void setDefaultLayout(final Class<? extends ModelElement> elementClass, final Shape defaultShape, final LayoutColor defaultBackground) {
        setDefaultLayout(elementClass, defaultShape, defaultBackground.awtColor(), GraphElementLayout.STANDARD_WIDTH, GraphElementLayout.STANDARD_HEIGHT);
    }

    /**
     * @param elementClass
     * @param defaultShape
     * @param defaultBackground
     */
    protected final void setDefaultLayout(final Class<? extends ModelElement> elementClass, final Shape defaultShape, final Color defaultBackground) {
        setDefaultLayout(elementClass, defaultShape, defaultBackground, GraphElementLayout.STANDARD_WIDTH, GraphElementLayout.STANDARD_HEIGHT);
    }

    /**
     * @param elementClass
     * @param defaultShape
     * @param defaultBackground
     * @param defaultWidth
     * @param defaultHeight
     */
    protected final void setDefaultLayout(final Class<? extends ModelElement> elementClass, final Shape defaultShape, final LayoutColor defaultBackground, final int defaultWidth, final int defaultHeight) {
        setDefaultLayout(elementClass, defaultShape, defaultBackground.awtColor(), defaultWidth, defaultHeight);
    }
    /**
     * @param elementClass
     * @param defaultShape
     * @param defaultBackground
     * @param defaultWidth
     * @param defaultHeight
     */
    protected final void setDefaultLayout(final Class<? extends ModelElement> elementClass, final Shape defaultShape, final Color defaultBackground, final int defaultWidth, final int defaultHeight) {
        //alle Paintbale-Klassen müssen instanziierbar sein. Hier dürfen keine abstrakten Klassen angegeben werden
        if (Modifier.isAbstract(elementClass.getModifiers())) {
            throw new Error("Only non abstract classes are allowed as paintable element classes! " + elementClass.getName() + " is abstract!");
        }
        if (!metaModelSpecificPaintableNodes.contains(elementClass)) { //the first add of an element class decides the order in this list -> the order for the LayoutEditor
            metaModelSpecificPaintableNodes.add(elementClass);
        }
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

        @Override
        protected SimpleMetaPath[] getInterLayerMetaPaths() {
            return new SimpleMetaPath[0];
        }

        @Override
        protected void initDefaultElementLayout() {
        }
    }

}
