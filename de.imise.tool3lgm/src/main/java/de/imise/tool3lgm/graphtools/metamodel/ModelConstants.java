package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getOther;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isConnecting;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartOrEndClass;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.LayerKnoten;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.SubordinationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * @author N.N., AXS
 */
public final class ModelConstants {

    private static MetaModel metaModel = initMetaModel();

    public static MetaModel initMetaModel() {
        try {
            Class<? extends MetaModel> metaModelClass = Tool3lgmMetaModelContext.getMetaModelClass();
            MetaModel metaModel = metaModelClass.newInstance();
            return metaModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Leeres Array als Standardrückgabetyp für zu überschreibende Funktionen.
     */
    @SuppressWarnings("unchecked")
    public static final Class<? extends ModelElement>[] EMPTY_ELEMENT_CLASS_ARRAY = new Class[0];

    @SuppressWarnings("unchecked")
    public static final Class<? extends Edge>[] EMPTY_EDGE_CLASS_ARRAY = new Class[0];

    @SuppressWarnings("unchecked")
    public static final Class<? extends CompositionEdge>[] EMPTY_COMPOSITION_CLASS_ARRAY = new Class[0];

    public static final Collection<Class<? extends ModelElement>> EMPTY_ELEMENT_CLASS_COLLECTION = ImmutableList.of();

    //Bei Gelegenheit mal ersetzen (Das ist aber schon etwas mehr Arbeit)
    //public enum LAYER {NO_LAYER, PHYSICAL_LAYER, INTER_LOGICAL_PHYSICAL_LAYER, LOGICAL_LAYER, INTER_DOMAIN_LOGICAL_LAYER, DOMAIN_LAYER};
    public static final int NO_LAYER = -1;

    public static final int PHYSICAL_LAYER = 0;

    public static final int INTER_LOGICAL_PHYSICAL_LAYER = 1;

    public static final int LOGICAL_LAYER = 2;

    public static final int INTER_DOMAIN_LOGICAL_LAYER = 3;

    public static final int DOMAIN_LAYER = 4;

    public static final int[] LAYERS = {
            PHYSICAL_LAYER, INTER_LOGICAL_PHYSICAL_LAYER, LOGICAL_LAYER, INTER_DOMAIN_LOGICAL_LAYER, DOMAIN_LAYER
    };

    public static final int[] VISIBLE_LAYERS = {
            DOMAIN_LAYER, LOGICAL_LAYER, PHYSICAL_LAYER
    };

    public static final int MIN_LAYER_INDEX = PHYSICAL_LAYER;

    public static final int MAX_LAYER_INDEX = DOMAIN_LAYER;

    public static final int LAYER_COUNT = LAYERS.length;

    public static final boolean isInterLayer(final int layerIndex) {
        return layerIndex % 2 == 1;
    }

    /** Short-Name für den beginn des HashStrings bei allen Kanten */
    public static final String EDGE_SHORT_NAME = "DLK";

    /**
     * Short-Name der zurückgegeben wird, wenn die an <code>getShortName(Class)</code> übergebene Klasse weder eine gültige Node noch Kantenklasse
     * ist.
     */
    public static final String NO_MODEL_ELEMENT_SHORT_NAME = "NME";

    public static final String PLURAL_NAME_RES_KEY_SUFFIX = "_p";

    /**
     * Liste aller geöffneten Dialoge
     */
    public static final ArrayList<ElementPropertyDialog> dialogs = new ArrayList<>();

    /**
     * Standardrückgabewert bei Fehlern = -1 ;
     */
    public static final int STANDARD_ERROR_INT_VALUE = new Integer(-1);

    /** Alle Node der FE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_DOMAIN_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(metaModel.getAllDomainLayerNodes()));

    /** Alle Node zw. FE und LWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(metaModel.getAllInterDomainLogicalLayerNodes()));

    /** Alle Node der LWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_LOGICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(metaModel.getAllLogicalLayerNodes()));

    /** Alle Node zw. LWE und PWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(metaModel.getAllInterLogicalPhysicalLayerNodes()));

    /** Alle Node der PWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_PHYSICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(metaModel.getAllPhysicalLayerNodes()));

    /** Set aller Knotenklassen */
    public static final Set<Class<? extends ModelElement>> ALL_NODES_SET = ImmutableSet.<Class<? extends ModelElement>> builder().addAll(ALL_DOMAIN_LAYER_NODES_SET).addAll(ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES_SET).addAll(ALL_LOGICAL_LAYER_NODES_SET)
            .addAll(ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES_SET).addAll(ALL_PHYSICAL_LAYER_NODES_SET).build();

    /** Array aller Knotenklassen */
    @SuppressWarnings("unchecked")
    public static final Class<? extends ModelElement>[] ALL_NODES = new Class[ALL_NODES_SET.size()];
    static {
        System.arraycopy(ALL_NODES_SET.toArray(), 0, ALL_NODES, 0, ALL_NODES.length);
    }

    /**
     * Liefert alle Elementklassen, die nur im Baum angezeigt werden sollen, wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>true</code> gestellt ist.
     *
     * @return alle Elementklassen, die nur im ExpertMode im Baum angezeigt werden
     * @see MetaModel#getOnlyExpertModeVisibleNodes()
     */
    public static final Set<Class<? extends ModelElement>> getOnlyExpertModeVisibleNodes() {
        return metaModel.getOnlyExpertModeVisibleNodes();
    }

    /**
     * Liefert <code>true</code>, wenn die Klasse nicht angezeigt werden soll, also wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>false</code> gestellt ist und die Klasse nur im Expert-Mode angezeigt werden soll.
     *
     * @return <code>true</code>, wenn die Klasse gerade nicht sichtbar sein soll
     * @see #getOnlyExpertModeVisibleNodes()
     */
    public static final boolean isHiddenClass(final Class<? extends ModelElement> elementClass) {
        //im ExperMode ist nichts versteckt
        if (UserProperties.is(BooleanProperty.OPTION_ENABLE_EXPERT_MODE)) {
            return false;
        }
        Set<Class<? extends ModelElement>> onlyExpertModeVisibleNodes = getOnlyExpertModeVisibleNodes();
        if (onlyExpertModeVisibleNodes.contains(elementClass)) {
            return true;
        }
        //bei Kanten prüfen, ob die Start- oder Endklasse eine versteckte Klasse ist
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            if (isHiddenClass(Edge.getStartClass(edgeClass)) || isHiddenClass(Edge.getEndClass(edgeClass))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert alle Elementklassen, die nur im ExpertMode ({@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE} = true) angelegt und verändert werden
     * können.
     *
     * @return alle Elementklassen, die nur im ExpertMode geändert werden können
     * @see MetaModel#getOnlyExpertModeEditableNodes()
     */
    private static Set<Class<? extends ModelElement>> getOnlyExpertModeEditableNodes() {
        return metaModel.getOnlyExpertModeEditableNodes();
    }

    /**
     * Liefert <code>true</code>, wenn alle übergebenen Klasse aktuell editierbar ist. Das ist sie, wenn sich der Baukasten im ExpertMode befindet
     * oder wenn er sich nicht im ExpertMode befindet und die Klasse keine Klasse aus den {@link #getOnlyExpertModeEditableNodes()} ist.
     *
     * @param elementClass
     * @return
     */
    @SafeVarargs
    public static final boolean isEditable(final Class<? extends ModelElement>... elementClasses) {
        if (!Static.isExpertMode()) {
            Set<Class<? extends ModelElement>> onlyExpertModeEditableNodes = getOnlyExpertModeEditableNodes();
            for (Class<? extends ModelElement> elementClass : elementClasses) {
                if (onlyExpertModeEditableNodes.contains(elementClass)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prüft, ob Verbindungen über diesen Pfad im nicht-ExperMode geändert werden dürfen. Das dürfen sie, wenn keine Kante des Pfades ausschließlich
     * Elemente verbindet, die nur im ExpertMode geändert werden dürfen.
     *
     * @param metaPath
     * @return
     */
    public static final boolean isEditable(final SimpleMetaPath... simpleMetaPaths) {
        if (!Static.isExpertMode()) {
            for (SimpleMetaPath simpleMetaPath : simpleMetaPaths) {
                List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
                for (ElementaryMetaPath elementaryMetaPath : elementaryMetaPaths) {
                    //bei wenigstens einer Kante im Pfad sind Start- und Endklasse nur im ExpertMode editierbar
                    if (!isEditable(elementaryMetaPath.getStartClass(), elementaryMetaPath.getEndClass())) {
                        return false;
                    }
                    //die Kante ist eine InstanciantionEdge, die von der Insstanz auf das Klassenelement (Template) zeigt
                    if (InstanciationEdge.class.isAssignableFrom(elementaryMetaPath.getEdgeClass()) && InstanciationEdge.INSTANCE_TO_TEMPLATE_MASTER_DIRECTION.equals(elementaryMetaPath.getDirection())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    ////////////
    // Kanten //
    ////////////

    /** Set aller Kantenklassen */
    public static final Set<Class<? extends Edge>> ALL_EDGES_SET = ImmutableSet.<Class<? extends Edge>> builder().addAll(Arrays.asList(metaModel.getAllEdges())).build();

    /////////////////////////
    // alle Elementklassen //
    /////////////////////////

    /** Set aller Elementklassen */
    public static final Set<Class<? extends ModelElement>> ALL_ELEMENTS_SET = ImmutableSet.<Class<? extends ModelElement>> builder().addAll(ALL_NODES_SET).addAll(ALL_EDGES_SET).build();

    /** Array aller Elementklassen */
    @SuppressWarnings("unchecked")
    public static final Class<? extends ModelElement>[] ALL_ELEMENTS = new Class[ALL_ELEMENTS_SET.size()];
    static {
        System.arraycopy(ALL_ELEMENTS_SET.toArray(), 0, ALL_ELEMENTS, 0, ALL_ELEMENTS.length);
    }

    /**
     * Sammlung, die alle Elementklassen inklusive aller Kantenklassen enthält einschließlich aller
     * ihrer Oberklassen bis hin zu ModelElement.class.
     */
    public static final Set<Class<? extends ModelElement>> ALL_MODELELEMENT_CLASSES_WITH_SUPER_CLASSES = new HashSet<>();
    static {
        //Menge aller Elementklassen und aller ihrer Oberklassen bis hin zu ModelElement zusammenbauen
        ArrayList<Class<? extends ModelElement>> allElementClasses = new ArrayList<>(ModelConstants.ALL_NODES_SET.size() + ModelConstants.ALL_EDGES_SET.size() + 20);
        allElementClasses.addAll(ModelConstants.ALL_NODES_SET);
        allElementClasses.addAll(ModelConstants.ALL_EDGES_SET);
        for (int i = 0; i < allElementClasses.size(); i++) {
            Class<? extends ModelElement> elementClass = allElementClasses.get(i);
            do {
                if (!allElementClasses.contains(elementClass)) {
                    allElementClasses.add(elementClass);
                }
                elementClass = elementClass.getSuperclass().asSubclass(ModelElement.class);
            } while (elementClass != ModelElement.class);
        }
        allElementClasses.add(ModelElement.class);
        ALL_MODELELEMENT_CLASSES_WITH_SUPER_CLASSES.addAll(allElementClasses);
    }

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    /**
     * Mappt von Elementklassen auf alle Kantenklasse, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben.
     */
    private static final Map<Class<? extends ModelElement>, Set<Class<? extends Edge>>> ELEMENT_CLASS_TO_SORTED_EDGES = metaModel.getElementClassToSortedEdges();

    /**
     * Liefert ein Set aller Kantenklassen, die für die übergebene Elementklasse "geordnet sind", d. h. dass für Elemente der übergebenen Klasse die
     * Reihenfolge der Instanzen der zurück gelieferten Kantenklassen in ihrem Kantenvektor eine Bedeutung hat
     * (z. B. Reihenfolge von Aufgaben in einem Prozess -> Verbindung zwischen Prozessen und Aufgaben sind für den Prozess geordnet).
     *
     * @param elementClass
     */
    public static final Set<Class<? extends Edge>> getSortedEdgeClasses(final Class<? extends ModelElement> elementClass) {
        return ELEMENT_CLASS_TO_SORTED_EDGES.get(elementClass);
    }

    /**
     * Prüft, ob für die übergebene Elementklasse Reihenfolge der Kanten der übergebenen Kantenklasse relevant ist.
     *
     * @param elementClass Elementklasse, für die Kanten der edgeClass in einer bestimmten Reihenfolge sein müssen
     * @param edgeClass Kantenklasse, die für Elemente der elementClass in der richtigen Reihenfolge sein müssen
     * @return
     */
    public static final boolean isSortedEdgeClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return MultipleEdge.class.isAssignableFrom(edgeClass);
        //        return SortedEdge.class.isAssignableFrom(edgeClass) && isStartClass(edgeClass, elementClass);
        //return getSortedEdgeClasses(elementClass).contains(edgeClass); //das kommt auf dasselbe raus wie das oben. Ich konnte mich nicht entscheiden, was besser ist -> daher nur auskommentiert
    }

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    public static final Class<? extends ModelElement>[] IMPORTABLE_NODES = metaModel.getImportableNodes();

    /** Alle Knotenklassen, die in jedem Teilmodell vorkommen, also nicht in jedem Teilmodell einen eigenen Container besitzen. */
    public static final Set<Class<? extends Node>> UNIQUE_NODES = metaModel.getUniqueNodes();

    public static final Set<Class<? extends ModelElement>> ELEMENTS_WITH_NAME_EXTENSIONS = ReflectionUtils.hasMethod(ModelElement.GET_NAME_EXTENSION_METHOD_NAME, ALL_ELEMENTS);

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse in der Grafik als Knoten oder Kante gezeichnet wird.
     *
     * @param elementClass
     * @return
     */
    public static final boolean isPaintable(final Class<? extends ModelElement> elementClass) {
        return getGraphViewDefinition().isPaintable(elementClass);
    }

    private static Set<Class<? extends ModelElement>> elementClassesWithLayout;

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse ein Layout besitzt (und damit nicht unique ist).
     * Das trifft auf alle Elemenklassen zu, die paintable sind oder ihre Kantennummern an paintable-Elemente schreiben.
     * Auérdem brauchen die Layer-Knoten ein Layout.
     *
     * @param elementClass
     * @return
     */
    public static final boolean hasLayout(final Class<? extends ModelElement> elementClass) {
        if (elementClassesWithLayout == null) {
            ImmutableSet.Builder<Class<? extends ModelElement>> elementClassesWithLayoutBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
            //LayerKnoten
            elementClassesWithLayoutBuilder.add(LayerKnoten.class);
            //alle Knoten die Paintable sind oder ihre Kantennummern an andere Knoten schreiben
            GraphViewDefinition graphViewDefinition = getGraphViewDefinition();
            for (Class<? extends ModelElement> clazz : ALL_NODES) {
                if (graphViewDefinition.isPaintable(clazz)) {
                    elementClassesWithLayoutBuilder.add(clazz);
                    continue;
                }
                if (hasSortedEdgeClassesToPaintable(clazz)) {
                    elementClassesWithLayoutBuilder.add(clazz);
                    continue;
                }
            }
            elementClassesWithLayout = elementClassesWithLayoutBuilder.build();
        }
        return elementClassesWithLayout.contains(elementClass) || Textfield.class.isAssignableFrom(elementClass);
    }

    private static Set<Class<? extends ModelElement>> elementClassesWithSortedEdges;

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse Kanten bei denen die Reihenfolge relevant ist,
     * zu anderen Elementarten hat, die selbst paintable sind. Wenn das der Fall ist, dann kann an diese
     * anderen Elemente die Nummer(n) der Kanten geschrieben werden. In welcher Farbe das geschieht, bestimmt
     * das Layout des Ausgangselementes.
     *
     * @return
     */
    public static final boolean hasSortedEdgeClassesToPaintable(final Class<? extends ModelElement> elementClass) {
        if (elementClassesWithSortedEdges == null) {
            ImmutableSet.Builder<Class<? extends ModelElement>> elementClassesWithSortedEdgesBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
            for (Class<? extends ModelElement> clazz : ALL_NODES) {
                Set<Class<? extends Edge>> sortedEdgeClasses = ModelConstants.getSortedEdgeClasses(clazz);
                if (sortedEdgeClasses != null) {
                    GraphViewDefinition graphViewDefinition = getGraphViewDefinition();
                    for (Class<? extends Edge> edgeClass : sortedEdgeClasses) {
                        Class<? extends ModelElement> other = getOther(edgeClass, clazz);
                        if (graphViewDefinition.isPaintable(other)) {
                            elementClassesWithSortedEdgesBuilder.add(clazz);
                            break;
                        }
                    }
                }
            }
            elementClassesWithSortedEdges = elementClassesWithSortedEdgesBuilder.build();
        }
        return elementClassesWithSortedEdges.contains(elementClass);
    }

    /**
     * Extrahiert aus den übergebenen Knoten alle, die im Baum angezeigt werden.
     *
     * @param elementClasses Elementklassen, die gefiltert werden sollen
     * @param creatableOnly wenn <code>true</code>, werden von den anzuzeigenden Knoten nur die übrig gelassen, die man auch ohne ein anderes Element
     *            anlegen kann
     * @return
     */
    private static Set<Class<? extends ModelElement>> getTreeVisibleNodes(final Iterable<Class<? extends ModelElement>> elementClasses, final boolean creatableOnly) {
        ImmutableSet.Builder<Class<? extends ModelElement>> creatableNodes = new ImmutableSet.Builder<>();
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (!ModelConstants.isEdgeType(elementClass)) {
                if (!ModelConstants.isAbstract(elementClass)) {
                    if (!creatableOnly || !ModelConstants.isExistenceDependent(elementClass)) {
                        creatableNodes.add(elementClass);
                    }
                }
            }
        }
        return creatableNodes.build();
    }

    /** Alle abstracten Klassen, die im Baum aus der FE auftauchen sollen */
    public static Class<? extends ModelElement>[] getTreeDomainLayerVisibleAbstractNodes() {
        return metaModel.getTreeDomainLayerVisibleAbstractNodes();
    }

    /** Alle abstracten Klassen, die im Baum aus der LWE auftauchen sollen */
    public static Class<? extends ModelElement>[] getTreeLogicalLayerVisibleAbstractNodes() {
        return metaModel.getTreeLogicalLayerVisibleAbstractNodes();
    }

    /** Alle abstracten Klassen, die im Baum aus der LWE auftauchen sollen */
    public static Class<? extends ModelElement>[] getTreePhysicalLayerVisibleAbstractNodes() {
        return metaModel.getTreePhsicalLayerVisibleAbstractNodes();
    }

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

    public static final boolean isMultipleEdgeClass(final Class<? extends Edge> edgeClass) {
        return MultipleEdge.class.isAssignableFrom(edgeClass);
    }

    /**
     * Prüft, ob die übergebene Klasse eine Kantenklasse mit mehreren Bedeutungen ist, also die Richtung der Edge die Bedeutung angibt.
     *
     * @param edgeClass
     * @return
     */
    public static final boolean isDoubleMeaningEdge(final Class<?> edgeClass) {
        return DoubleMeaningEdge.class.isAssignableFrom(edgeClass);
    }

    //    private static final boolean DEBUG = true;

    /**
     * Prüft, ob Kante gerichtet ist. Das ist sie, wenn sie nicht dieselben Elementarten verbindet oder in beide Richtungen einen unterschiedlichen
     * Anzeigenamen hat oder eine doppelte Bedeutung hat. Im originalen Metamodell heißen die KommBeziehungen in beide Richtungen gleich, haben
     * aber eine doppelte Bedeutung und sollen somit gerichtet dargestellt werden.
     *
     * @param edgeClass
     * @return
     */
    public static final boolean isDirectedEdge(final Class<? extends Edge> edgeClass) {
        //man muss explizit auf Kanten mit doppelter Bedeutung testen!
        //        if (DEBUG) {
        //            Class<? extends ModelElement> startClass = Edge.getStartClass(edgeClass);
        //            Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
        //            boolean isDoubleMeaningEdge = isDoubleMeaningEdge(edgeClass);
        //            String forwardMetaAssociationName = ElementsNameBuilder.getForwardMetaAssociationName(edgeClass);
        //            String backwardMetaAssociationName = ElementsNameBuilder.getBackwardMetaAssociationName(edgeClass);
        //            Sys.err1("isDirectedEdge:   edgeClass=" + edgeClass.getSimpleName() + "   startClass=" + startClass + "   endClass=" + endClass + "   isDoubleMeaningEdge=" + isDoubleMeaningEdge + "   forwardMetaAssociationName='" + forwardMetaAssociationName
        //                    + "'   backwardMetaAssociationName='" + backwardMetaAssociationName + "'\n\t-> " + (startClass != endClass) + " || " + isDoubleMeaningEdge + " || " + !forwardMetaAssociationName.equals(backwardMetaAssociationName));
        //        }
        return Edge.getStartClass(edgeClass) != Edge.getEndClass(edgeClass) || isDoubleMeaningEdge(edgeClass) || !ElementsNameBuilder.getForwardMetaAssociationName(edgeClass).equals(ElementsNameBuilder.getBackwardMetaAssociationName(edgeClass));
    }

    /**
     * Liefert für die übergebene Kantenklasse den MetaPfad, über den die verbindbaren Elemente ebenfalls bereits verbunden sein müssen.
     * Dieser Mechanismus ist dafür gedacht, verbindbare Elemente einzuschränken auf bestimmte Elemente.
     *
     * @param edgeClass
     * @return
     */
    public static SimpleMetaPath getConditionPath(final Class<? extends Edge> edgeClass) {
        return metaModel.getConditionPath(edgeClass);
    }

    /**
     * Sammlung aller Pfade, die ausgehend vom Startelement dieser Kante ebenfalls angelegt werden sollen, wenn eine Instanziierung über diese
     * Kantenklasse durchgeführt wird.
     *
     * @param instanciationEdgeClass
     * @return
     * @see MetaModel#getInstanciableMetaPaths(Class)
     */
    public static Iterable<SimpleMetaPath> getInstanciablePath(final Class<? extends InstanciationEdge> instanciationEdgeClass) {
        Iterable<SimpleMetaPath> instanciableMetaPaths = metaModel.getInstanciableMetaPaths(instanciationEdgeClass);
        return SimpleMetaPathCreator.getSimpleMetaPathsNonAbstract(instanciableMetaPaths);
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    /**
     * Mappt von einer Elementklasse auf das Array aller instanziierbaren und zu dieser Klasse zuweisungskompatiblen ModelElement-Klassen.
     */
    public static final Multimap<Class<? extends ModelElement>, Class<? extends ModelElement>> ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES = HashMultimap.create();

    /**
     * Liefert alle nichtabstrakten, zur übergebenen Klasse zuweisungskompatiblen Element- oder Kantenklassen. Die übergebene Klasse selbst ist in den
     * Rückgabewerten enthalten, wenn sie nichtabstract ist.
     *
     * @param elementClass
     * @return
     */
    public static final Collection<Class<? extends ModelElement>> getInstanciableAssignableClasses(final Class<? extends ModelElement> elementClass) {
        if (ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.containsKey(elementClass)) {
            Collection<Class<? extends ModelElement>> classes = ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.get(elementClass);
            if (classes.size() == 1 && classes.iterator().next() == null) {
                return EMPTY_ELEMENT_CLASS_COLLECTION;
            }
            return classes;
        }
        for (Class<? extends ModelElement> clazz : ALL_ELEMENTS) {
            if (elementClass.isAssignableFrom(clazz) && !isAbstract(clazz)) {
                ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.put(elementClass, clazz);
            }
        }
        if (!ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.containsKey(elementClass)) {
            ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.put(elementClass, null);
        }
        return getInstanciableAssignableClasses(elementClass);
    }

    /**
     * Mappt von einer Elementklasse auf alle Kanten, die diese Elementklasse selbst besitzt oder von einer ihrer Oberklassen erbt.
     */
    private static final HashMap<Class<? extends ModelElement>, Class<? extends Edge>[]> ELEMENT_CLASS_TO_EDGE_CLASSES = new HashMap<>();

    /**
     * Liefert für eine Elementklasse alle Kantenklassen dieser Klasse zu anderen Elementklassen
     *
     * @param elementClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Edge>[] getEdgeTypes(final Class<? extends ModelElement> elementClass) {
        Class<? extends Edge>[] edgeClasses = ELEMENT_CLASS_TO_EDGE_CLASSES.get(elementClass);
        if (edgeClasses != null) {
            return edgeClasses;
        }
        ArrayList<Class<? extends Edge>> elementClassEdgeClasses = new ArrayList<>();
        for (Class<? extends Edge> edgeClass : ALL_EDGES_SET) {
            if (isStartOrEndClass(edgeClass, elementClass)) {
                elementClassEdgeClasses.add(edgeClass);
            }
        }
        int size = elementClassEdgeClasses.size();
        Class<? extends Edge>[] returnClasses = null;
        if (size == 0) {
            returnClasses = EMPTY_EDGE_CLASS_ARRAY;
        } else {
            returnClasses = new Class[size];
            System.arraycopy(elementClassEdgeClasses.toArray(), 0, returnClasses, 0, size);
        }
        ELEMENT_CLASS_TO_EDGE_CLASSES.put(elementClass, returnClasses);
        return returnClasses;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse die übergebene Kantenart hat. Es wird Zuweisungskompatibilität gerpüft.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    public static final boolean hasEdgeType(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        Class<? extends Edge>[] edgeTypes = getEdgeTypes(elementClass);
        for (Class<? extends Edge> edgeType : edgeTypes) {
            if (edgeClass.isAssignableFrom(edgeType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mappt für eine Elementklasse auf eine weitere Map, die von einer Elementklasse auf ein Array von Kantenklassen mappt. Das Array der
     * Kantenklassen enthält alle Kanten, die zwischen den beiden Schlüsselelementklassen vorhanden sein können.<br />
     * In der äußeren und allen inneren HashMaps sind immer dieselben Schlüsselelemente enthalten. Für ein Paar von Schlüsselelementklassen ist immer
     * dasselbe Kanteklassen-Array abgelegt - egal in welcher Reihenfolge man die Elementeklassen als Schlüssel
     * einsetzt.
     */
    private static final HashMap<Class<? extends ModelElement>, HashMap<Class<? extends ModelElement>, Class<? extends Edge>[]>> ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES = new HashMap<>();

    /**
     * Liefert ein Array aller Kantenklassen, die zwischen den beiden übergebenen Elementklassen existieren können. Gibt es keine Kantenklasse
     * zwischen den Elementen so kommt ein leeres Array (length==0) zurück.
     *
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final Class<? extends Edge>[] getEdgeTypes(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        HashMap<Class<? extends ModelElement>, Class<? extends Edge>[]> elementClassToEdgeClass = ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES.get(elementClass1);
        if (elementClassToEdgeClass != null) {
            Class<? extends Edge>[] edgeClasses = elementClassToEdgeClass.get(elementClass2);
            if (edgeClasses != null) {
                return edgeClasses;
            }
        } else {
            elementClassToEdgeClass = new HashMap<>();
            ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES.put(elementClass1, elementClassToEdgeClass);
        }
        ArrayList<Class<? extends Edge>> resultEdgeClasses = new ArrayList<>();
        for (Class<? extends Edge> edgeClass : getEdgeTypes(elementClass1)) {
            if (isConnecting(edgeClass, elementClass1, elementClass2)) {
                resultEdgeClasses.add(edgeClass);
            }
        }
        Class<? extends Edge>[] returnClasses = null;
        int size = resultEdgeClasses.size();
        if (size == 0) {
            returnClasses = EMPTY_EDGE_CLASS_ARRAY;
        } else {
            returnClasses = new Class[resultEdgeClasses.size()];
            System.arraycopy(resultEdgeClasses.toArray(), 0, returnClasses, 0, size);
        }
        //jetzt das gefundenen Kantenklassen-Array für beide Elementklassenkombinationen in den inneren HashMaps ablegen
        elementClassToEdgeClass.put(elementClass2, returnClasses);
        elementClassToEdgeClass = ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES.get(elementClass2);
        if (elementClassToEdgeClass == null) {
            elementClassToEdgeClass = new HashMap<>();
            ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES.put(elementClass2, elementClassToEdgeClass);
        }
        elementClassToEdgeClass.put(elementClass1, returnClasses);
        return returnClasses;
    }

    // Die folgenden Arrays müssen hier unten initialisiert werden nachdem die Maps mit den Edges gefüllt sind, sonst InitialException

    /** Alle im Baum auf der FE sichtbaren Node */
    public static final Iterable<Class<? extends ModelElement>> TREE_DOMAIN_LAYER_NODES = getTreeVisibleNodes(ALL_DOMAIN_LAYER_NODES_SET, false);

    /** Alle im Baum auf der FE anlegbaren Node */
    public static final Iterable<Class<? extends ModelElement>> CREATABLE_DOMAIN_LAYER_NODES = getTreeVisibleNodes(TREE_DOMAIN_LAYER_NODES, true);

    /** Alle im Baum auf der LWE sichtbaren Node */
    public static final Iterable<Class<? extends ModelElement>> TREE_LOGICAL_LAYER_NODES = getTreeVisibleNodes(ALL_LOGICAL_LAYER_NODES_SET, false);

    /** Alle im Baum auf der FE anlegbaren Node */
    public static final Iterable<Class<? extends ModelElement>> CREATABLE_LOGICAL_LAYER_NODES = getTreeVisibleNodes(TREE_LOGICAL_LAYER_NODES, true);

    /** Alle im Baum auf der PWE sichtbaren Node */
    public static final Iterable<Class<? extends ModelElement>> TREE_PHYSICAL_LAYER_NODES = getTreeVisibleNodes(ALL_PHYSICAL_LAYER_NODES_SET, false);

    /** Alle im Baum auf der PWE anlegbaren Node */
    public static final Iterable<Class<? extends ModelElement>> CREATABLE_PHYSICAL_LAYER_NODES = getTreeVisibleNodes(TREE_PHYSICAL_LAYER_NODES, true);

    //	static {
    //		HashSet<Class<? extends ModelElement>> allElements= new HashSet<Class<? extends ModelElement>>(ALL_NODES_SET.size() + ALL_EDGES_SET.size());
    //		allElements.addAll(ALL_NODES_SET);
    //		allElements.addAll(ALL_EDGES_SET);
    //		for (Class<? extends ModelElement> elementClass1 : allElements) {
    //			for (Class<? extends ModelElement> elementClass2 : allElements) {
    //				Class<? extends Edge>[] et = getEdgeTypes(elementClass1, elementClass2);
    //				if (et.length==0)
    //					continue;
    //				System.err.println(elementClass1.getSimpleName() + "\t-\t" + elementClass2.getSimpleName());
    //				System.err.println("\t" + Arrays.asList(et));
    //			}
    //		}
    //	}

    public static final Iterable<Class<? extends ModelElement>> getCreatableLayerNodes(final int layer) {
        if (layer == DOMAIN_LAYER) {
            return CREATABLE_DOMAIN_LAYER_NODES;
        }
        if (layer == LOGICAL_LAYER) {
            return CREATABLE_LOGICAL_LAYER_NODES;
        }
        if (layer == PHYSICAL_LAYER) {
            return CREATABLE_PHYSICAL_LAYER_NODES;
        }
        return EMPTY_ELEMENT_CLASS_COLLECTION;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebenen Klasse eine Knotenklassen ist, die in jedem Teilmodell vorkommt, also nicht in jedem Teilmodell
     * einen eigenen Container besitzt.
     */
    public static final boolean isUnique(final Class<?> elementClass) {
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            return isUnique(Edge.getStartClass(edgeClass)) || isUnique(Edge.getEndClass(edgeClass));
        }
        return UNIQUE_NODES.contains(elementClass);
    }

    /**
     * Prüft, ob die übergebene Klasse <code>abstract</code> ist.
     *
     * @param elementClass
     * @return
     */
    public static final boolean isAbstract(final Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Node} oder {@link NodeContainer} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse ein Knotentyps ist, sonst <code>false</code>.
     */
    public static final boolean isNodeType(final Class<?> elementClass) {
        return Node.class.isAssignableFrom(elementClass) || NodeContainer.class.isAssignableFrom(elementClass);
    }

    /**
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Node} oder {@link NodeContainer} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse ein Knotentyps ist, sonst <code>false</code>.
     */
    public static final boolean isBendpointType(final Class<?> elementClass) {
        return Bendpoint.class.isAssignableFrom(elementClass) || BendpointContainer.class.isAssignableFrom(elementClass);
    }

    /**
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Node} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse ein Knotentyps ist, sonst <code>false</code>.
     */
    public static final boolean isRealNodeType(final Class<?> elementClass) {
        return isNodeType(elementClass) && !isBendpointType(elementClass);
    }

    /**
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Edge} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse eine Assoziation ist, sonst <code>false</code>.
     */
    public static final boolean isEdgeType(final Class<?> elementClass) {
        return Edge.class.isAssignableFrom(elementClass) || EdgeContainer.class.isAssignableFrom(elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse selbst Assoziationen zu anderen Elementen haben kann - also eine Assoziationsklasse
     * ist.
     *
     * @param elementClass
     * @return
     */
    public static final boolean isAssociationClass(final Class<?> elementClass) {
        if (!Edge.class.isAssignableFrom(elementClass)) {
            return false;
        }
        Class<? extends Edge>[] edgeTypes = getEdgeTypes(elementClass.asSubclass(ModelElement.class));
        return edgeTypes != null && edgeTypes.length != 0;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse Startklasse eines Interebenenmetapfades ist.
     *
     * @param elementClass
     * @return
     */
    public final static boolean isInterLayerStartClass(final Class<? extends ModelElement> elementClass) {
        return getGraphViewDefinition().getInterLayerMetaPath(elementClass) != null;
    }

    /**
     * Erzeugt eine neue Instanz eines Modellelementes.<br>
     * Loggt eine Fehlermedung, wenn Objekt nicht erzeugt werden konnte.
     *
     * @param elementClass Unterklasse von <code>ModelElement</code>
     * @return
     */
    public static final ModelElement createElement(final Class<? extends ModelElement> elementClass) {
        return createElement(elementClass, true);
    }

    /**
     * Erzeugt eine neue Instanz eines Modellelementes.<br>
     * Loggt eine Fehlermedung, wenn Objekt nicht erzeugt werden konnte und <code>log</code> mit <code>true</code> übergeben wurde.
     *
     * @param elementClass Unterklasse von <code>ModelElement</code>
     * @param log wenn <code>true</code> wird ein eventuell auftretender Fehler geloggt
     * @return neues ModelElement der übergebenen Klasse oder <code>null</code>
     */
    public static final ModelElement createElement(final Class<? extends ModelElement> elementClass, final boolean log) {
        try {
            return elementClass.newInstance();
        } catch (Exception e) {
            if (log) {
                Log.show(Log.ERROR, "Konnte Klasse " + elementClass.getName() + " nicht erstellen.", e);
            }
            return null;
        }
    }

    /**
     * Erzeugt eine neues ModelElement der gleichen Art wie das übergebene
     *
     * @return neues ModelElement der übergebenen Art oder im Fehlerfall <code>null</code>
     * @param me ModelElement, das die Klasse des neu zu erzeugenden Elementes vorgibt
     * @param log wenn <code>true</code> wird ein eventuell auftretender Fehler geloggt
     * @return neues ModelElement oder <code>null</code>
     */
    public static final ModelElement createElement(final ModelElement me, final boolean log) {
        return createElement(me.getClass(), log);
    }

    /** Mappt vom Klassennamen auf die Klasse. Ist der Cache für die Funktion {@link #getClassForName(String)} */
    private static final Map<String, Class<? extends ModelElement>> CLASS_NAME_TO_CLASS_MAP = new HashMap<>();

    /** Alle Modellelementklassen, die instanziierbar sind und in jedem Metamodell automatisch enthalten sind */
    private static final Set<Class<? extends ModelElement>> META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES = ImmutableSet.of(Bendpoint.class, Textfield.class);

    /** Klassennamen aller Modellelementklassen, die instanziierbar sind und in jedem Metamodell automatisch enthalten sind */
    private static final Set<String> META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPE_NAMES = CollectionUtils.getSimpleClassNames(META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES);

    /**
     * Prüft, ob in dem übergebenen className mindestens 2 Punkte stehen.
     *
     * @param className
     * @return
     */
    private static final boolean isFullQualifiedClassName(final String className) {
        int firstPoint = className.indexOf('.');
        if (firstPoint < 0) {
            return false;
        }
        int secondPoint = className.lastIndexOf('.');
        return firstPoint < secondPoint;
    }

    public static final String NODE_PACKAGE_NAME = ALL_NODES_SET.size() > 0 ? ALL_NODES_SET.iterator().next().getPackage().getName() + "." : "";

    public static final String EDGE_PACKAGE_NAME = ALL_EDGES_SET.size() > 0 ? ALL_EDGES_SET.iterator().next().getPackage().getName() + "." : "";

    /**
     * Gibt die Klasse zu einem Klassennamen zurück. Der Klassenname kann voll qualifiziert sein oder aber nur aus dem simplen Klassenamen bestehen.
     *
     * @param classname String mit der Klassenbezeichnung
     * @return Class
     */
    public static final Class<? extends ModelElement> getClassForName(final String classname) {
        if (classname == null || classname.trim().equals("")) {
            return null;
        }

        Class<? extends ModelElement> classObject = CLASS_NAME_TO_CLASS_MAP.get(classname);
        if (classObject != null) {
            return classObject;
        }

        String fullClassName = null;
        if (!isFullQualifiedClassName(classname)) {
            //die allgemeinen (nicht metamodellabhängigen)Klassen liegen in einem anderen
            //package als alle Metamodellklassen. Das hier sollte eigentlich nicht die
            //ModelConstants wissen, sondern das sind Tool3lgmConstants, da die Klassen
            //nicht modellabhängig sind
            if (META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPE_NAMES.contains(classname)) {
                fullClassName = Tool3lgmConstants.ELEMENTS_PACKAGE_NAME + classname;
            } else {
                fullClassName = NODE_PACKAGE_NAME + classname;
            }
        } else {
            fullClassName = classname;
        }

        Class<? extends ModelElement> clazz = null;

        try {
            clazz = Class.forName(fullClassName).asSubclass(ModelElement.class);
        } catch (Exception e) {
            try {
                fullClassName = EDGE_PACKAGE_NAME + classname;
                clazz = Class.forName(fullClassName).asSubclass(ModelElement.class);
            } catch (Exception ex) {
                try {
                    fullClassName = Tool3lgmConstants.GD_PACKAGE_NAME + classname;
                    clazz = Class.forName(fullClassName).asSubclass(ModelElement.class);
                } catch (Exception exc) {
                    String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
                    String currentClassName = metaModel.getCurrentClassName(simpleClassName);
                    if (!currentClassName.equals(simpleClassName)) {
                        clazz = getClassForName(currentClassName);
                        CLASS_NAME_TO_CLASS_MAP.put(simpleClassName, clazz);
                        CLASS_NAME_TO_CLASS_MAP.put(fullClassName, clazz);
                        return clazz;
                    }
                    return null;
                }
            }
        }

        CLASS_NAME_TO_CLASS_MAP.put(classname, clazz);
        CLASS_NAME_TO_CLASS_MAP.put(fullClassName, clazz);

        return clazz;
    }

    public static final Set<Class<? extends ModelElement>> ELEMENT_CLASSES_WITH_HAS_PART_EDGE_CLASSES = new HashSet<>();

    public static final Set<Class<? extends ModelElement>> ELEMENT_CLASSES_WITH_PART_OF_EDGE_CLASSES = new HashSet<>();

    static {
        fill_ELEMENT_CLASSES_WITH_PART_OF_EDGE_CLASSES_and_ELEMENT_CLASSES_WITH_HAS_PART_EDGE_CLASSES();
    }

    private static final void fill_ELEMENT_CLASSES_WITH_PART_OF_EDGE_CLASSES_and_ELEMENT_CLASSES_WITH_HAS_PART_EDGE_CLASSES() {
        for (int i = 0; i < ALL_NODES.length; i++) {
            //Hole alle Kantenklassen der Zielklasse und suche alle HasPartEdges
            for (Class<? extends Edge> c : getEdgeTypes(ALL_NODES[i])) {
                if (HasPartEdge.class.isAssignableFrom(c)) {
                    Class<? extends HasPartEdge> poClass = c.asSubclass(HasPartEdge.class);
                    if (HasPartEdge.isParentClass(poClass, ALL_NODES[i])) {
                        ELEMENT_CLASSES_WITH_HAS_PART_EDGE_CLASSES.add(ALL_NODES[i]);
                    }
                    if (HasPartEdge.isPartClass(poClass, ALL_NODES[i])) {
                        ELEMENT_CLASSES_WITH_PART_OF_EDGE_CLASSES.add(ALL_NODES[i]);
                        break;
                    }
                }
            }
        }
    }

    public static final boolean canHaveParts(final Class<? extends ModelElement> elementClass) {
        return ELEMENT_CLASSES_WITH_HAS_PART_EDGE_CLASSES.contains(elementClass);
    }

    public static final boolean canHaveParents(final Class<? extends ModelElement> elementClass) {
        return ELEMENT_CLASSES_WITH_PART_OF_EDGE_CLASSES.contains(elementClass);
    }

    public static final boolean canHavePartsOrParents(final Class<? extends ModelElement> elementClass) {
        return canHaveParts(elementClass) || canHaveParents(elementClass);
    }

    public static final boolean isHasPartEdge(final Class<? extends Edge> edgeClass) {
        return HasPartEdge.class.isAssignableFrom(edgeClass);
    }

    public static boolean isRecursive(final Class<? extends Edge> edgeClass) {
        return Edge.isRecursive(edgeClass);
    }

    public static boolean isRecursiveHasPartEdge(final Class<? extends Edge> edgeClass) {
        return SubordinationEdge.class.isAssignableFrom(edgeClass) && isRecursive(edgeClass);
    }

    public static boolean isRecursiveSubordination(final Class<? extends Edge> edgeClass) {
        return SubordinationEdge.class.isAssignableFrom(edgeClass) && isRecursive(edgeClass);
    }

    /*******************/

    private static Map<Class<? extends ModelElement>, Integer> ELEMENT_CLASS_TO_LAYER = createELEMENT_CLASS_TO_LAYER_MAP();

    private static Map<Class<? extends ModelElement>, Integer> createELEMENT_CLASS_TO_LAYER_MAP() {
        Map<Class<? extends ModelElement>, Integer> map = new HashMap<>();
        for (Class<? extends ModelElement> elementClass : ALL_DOMAIN_LAYER_NODES_SET) {
            map.put(elementClass, DOMAIN_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES_SET) {
            map.put(elementClass, INTER_DOMAIN_LOGICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_LOGICAL_LAYER_NODES_SET) {
            map.put(elementClass, LOGICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES_SET) {
            map.put(elementClass, INTER_LOGICAL_PHYSICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_PHYSICAL_LAYER_NODES_SET) {
            map.put(elementClass, PHYSICAL_LAYER);
        }
        for (Class<? extends Edge> edgeClass : ALL_EDGES_SET) {
            if (map.get(edgeClass) == null) {
                int layer = getEdgeLayer(map, Edge.getStartClass(edgeClass), Edge.getEndClass(edgeClass));
                if (layer != NO_LAYER) {
                    map.put(edgeClass, layer);
                }
            }
        }
        //nicht über den Builder gehen, weil die key-Klassen mehrfach in den Sets vorkommen können. Der Builder beendet dann mit einem Error.
        ImmutableMap<Class<? extends ModelElement>, Integer> returnMap = ImmutableMap.copyOf(map);
        return returnMap;
    }

    /**
     * Liefert den Layer der Kante, wenn die Kante die übergebenen Klassen verbindet
     *
     * @param edgeStartClass
     * @param edgeEndClass
     * @return
     */
    public static final int getEdgeLayer(final Class<? extends ModelElement> edgeStartClass, final Class<? extends ModelElement> edgeEndClass) {
        return getEdgeLayer(ELEMENT_CLASS_TO_LAYER, edgeStartClass, edgeEndClass);
    }

    /**
     * Liefert den Layer der Kante, wenn die Kante die übergebenen Klassen verbindet
     *
     * @param map Map mit den Einträgen der Layerwerte alle Elementklassen, die keine Kante sind
     * @param edgeStartClass
     * @param edgeEndClass
     * @return
     */
    private static final int getEdgeLayer(final Map<Class<? extends ModelElement>, Integer> map, final Class<? extends ModelElement> edgeStartClass, final Class<? extends ModelElement> edgeEndClass) {
        Integer startLayer = map.get(edgeStartClass);
        Integer endLayer = map.get(edgeEndClass);
        int startElementLayer = startLayer == null ? NO_LAYER : startLayer;
        int endElementLayer = endLayer == null ? NO_LAYER : endLayer;
        int layer = startElementLayer;
        //eine Kante gehört immer zu einem Zwischenlayer, wenn das Start- oder Endelement zu einem Zwischenlayer gehören
        //wenn beide zu Zwischeenlayer oder beide zu normalen Ebenen gehören, dann gehört die Kante immer zur jeweils höhreren Ebene
        //wenn einer der Layer NO_LAYER ist, dann liegt die Kante auf dem anderen. Wenn beide Layer NO_LAYER sind, ist auch die Kanze NO_LAYER
        if (layer == NO_LAYER) {
            layer = endElementLayer;
        } else if (endElementLayer != NO_LAYER && startElementLayer != endElementLayer) {
            if (startElementLayer % 2 == 1) {
                if (endElementLayer % 2 == 1 && startElementLayer < endElementLayer) {
                    layer = endElementLayer;
                }
            } else if (endElementLayer % 2 == 1) {
                layer = endElementLayer;
            } else if (startElementLayer < endElementLayer) {
                layer = endElementLayer;
            }
        }
        return layer;
    }

    /**
     * gibt die Ebene eine Objekttypes zurueck
     *
     * @param type Typkonstante, die den Objekttypen spezifiziert
     * @return int Ebene
     */
    public static final int layerFor(final Class<? extends ModelElement> elementClass) {
        Integer layer = ELEMENT_CLASS_TO_LAYER.get(elementClass);
        return layer == null ? NO_LAYER : layer.intValue();
    }

    /**
     * Überprueft, ob fuer ein Objekt schon ein Dialog existiert und gibt diesen ggf. zurück
     *
     * @param obj Dialog zu diesem Objekt
     * @return ModelElement obj, wenn schon ein Dialog existiert, null sonst
     */
    public static ElementPropertyDialog hasObjektDialog(final ModelElement obj) {
        for (ElementPropertyDialog pd : dialogs) {
            if (obj == pd.getModelElement()) {
                return pd;
            }
        }
        return null;
    }

    /**
     * Gibt Verctor mit allen geoeffneten Dialogen zurueck
     *
     * @return ArrayList mit allen geoeffneten Dialogen
     */
    public static final ArrayList<ElementPropertyDialog> getDialogs() {
        return dialogs;
    }

    /**
     * entfernt einen Dialog aus dem ArrayList mit allen geoeffneten Dialogen
     *
     * @param modelElement Element dessen Dialog aus dem ArrayList entfernt werden soll
     */
    public static final void removeDialog(final ModelElement modelElement) {
        for (int n = 0; n < dialogs.size(); n++) {
            if (modelElement == dialogs.get(n).getModelElement()) {
                dialogs.remove(n--);
            }
        }
    }

    public static final boolean isComposition(final Class<? extends Edge> edgeClass) {
        return CompositionEdge.class.isAssignableFrom(edgeClass);
    }

    /**
     * Liefert ein Array aller Kantenklassen, durch die die übergebene Elementart einer anderen untergeordnet (<code>isMaster==false</code>) oder
     * übergeordnet (<code>isMaster==true</code>) wird. Dies sind alle Kantenklasse, die Kompositionen sind und bei
     * denen mindestens eine Endklasse (bei <code>isMaster==false</code>) oder eine Startklasse (bei <code>isMaster==true</code>) zuweisungskompatibel
     * zur übergebenen Elementklasse ist.
     *
     * @param elementClass
     * @param isMaster wenn <code>true</code> soll die übergebene Elementart die übergeordnete sein, sonst die untergeordnete
     * @return Array von Kantenklassen, die die übergebene Elementart unterordnen
     */
    @SuppressWarnings("unchecked")
    private static Class<? extends CompositionEdge>[] getCompositionEdgeTypes(final Class<? extends ModelElement> elementClass, final boolean isMaster) {
        Class<? extends Edge>[] elementClassEdges = getEdgeTypes(elementClass);
        ArrayList<Class<? extends Edge>> subEdgeTypes = new ArrayList<>(elementClassEdges.length);
        for (Class<? extends Edge> edgeClass : elementClassEdges) {
            if (isComposition(edgeClass)) {
                if (isMaster) {
                    if (isStartClass(edgeClass, elementClass)) {
                        subEdgeTypes.add(edgeClass);
                    }
                } else {
                    if (isEndClass(edgeClass, elementClass)) {
                        subEdgeTypes.add(edgeClass);
                    }
                }
            }
        }
        int size = subEdgeTypes.size();
        if (size == 0) {
            return EMPTY_COMPOSITION_CLASS_ARRAY;
        }
        Class<? extends CompositionEdge>[] returnClasses = new Class[size];
        System.arraycopy(subEdgeTypes.toArray(), 0, returnClasses, 0, size);
        return returnClasses;
    }

    //    static{
    //    	for (Class<? extends ModelElement> c : ALL_NODES){
    //    		System.err.println(c.getSimpleName() + "\n\t" + Arrays.toString(getSubordinationEdgeTypesAsMaster(c)));
    //    	}
    //    }

    /**
     * Liefert ein Array aller Kantenklassen, durch die die übergebene Elementart einer anderen übergeordnet wird. Dies sind alle Kantenklasse, die
     * Kompositionen sind und bei denen mindestens eine Startklasse zuweisungskompatibel zur übergebenen
     * Elementklasse ist.
     *
     * @param elementClass
     * @return Array von Kantenklassen, die die übergebene Elementart überordnen
     */
    public static Class<? extends CompositionEdge>[] getCompositionEdgeTypesForMaster(final Class<? extends ModelElement> elementClass) {
        return getCompositionEdgeTypes(elementClass, true);
    }

    /**
     * Liefert ein Array aller Kantenklassen, durch die die übergebene Elementart einer anderen untergeordnet wird. Dies sind alle Kantenklasse, die
     * Kompositionen sind und bei denen mindestens eine Endklasse zuweisungskompatibel zur übergebenen
     * Elementklasse ist.
     *
     * @param elementClass
     * @return Array von Kantenklassen, die die übergebene Elementart unterordnen
     */
    public static Class<? extends CompositionEdge>[] getCompositionEdgeTypesForSlave(final Class<? extends ModelElement> elementClass) {
        return getCompositionEdgeTypes(elementClass, false);
    }

    /**
     * Liefert alle Elementarten, die der übergebenen Elementart über eine Komposition untergeordnet sind.
     *
     * @param masterElementClass Elementart, für die alle anderen Elementarten ermittelt werden sollen, die mit ihr über eine Komposition verbunden
     *            sein können.
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final Class<? extends ModelElement>[] getSlaveElementTypes(final Class<? extends ModelElement> masterElementClass) {
        Class<? extends CompositionEdge>[] compositions = getCompositionEdgeTypesForMaster(masterElementClass);
        if (compositions.length == 0) {
            return EMPTY_ELEMENT_CLASS_ARRAY;
        }
        ArrayList<Class<? extends ModelElement>> slaveElementClasses = new ArrayList<>(compositions.length);
        for (Class<? extends CompositionEdge> compClass : compositions) {
            Class<? extends ModelElement> slaveType = CompositionEdge.getSlaveType(compClass);
            if (!slaveElementClasses.contains(slaveType)) {
                slaveElementClasses.add(slaveType);
            }
        }
        int size = slaveElementClasses.size();
        Class<? extends ModelElement>[] returnClasses = new Class[size];
        System.arraycopy(slaveElementClasses.toArray(), 0, returnClasses, 0, size);
        return returnClasses;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mindestens einer anderen Klasse untergeordnet ist. Das erkennt man daran, dass die
     * übergebene Klasse zuweisungskompatibel zu einer Endklasse einer Kantenklasse ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn das übergebene ein untergeordnetes Element ist
     */
    public static boolean isSlaveType(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends Edge> edgeClass : getEdgeTypes(elementClass)) {
            if (isComposition(edgeClass) && isEndClass(edgeClass, elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementart der Slave einer {@link CompositionEdge} ist
     *
     * @param elementClass
     * @return
     */
    private static boolean isExistenceDependent(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends Edge> edgeClass : ModelConstants.getEdgeTypes(elementClass)) {
            //System.err.print(elementClass.getSimpleName() + "  --->  " + edgeClass.getSimpleName() + "  --->  " + Edge.getMinCardinality(elementClass, edgeClass) + "  --->  ");
            //minimale Kardinalität von 1 zu anderen Elementen -> dieses Element braucht mind. ein anderes, damit es konsistent ist
            if (Edge.getMinCardinality(elementClass, edgeClass) > 0) {
                //wenn das andere, benötigte Element aber mit einer Compostion untergeordnet ist, dann wird dieses benötigte, untergeordnete Element in der GDCollection-Funktion createInitialSubtypes(...) auomatisch erzeugt und somit die Konsistenz automatisch hergestellt und damit gilt dieses Element nicht als anhängig
                if (!isComposition(edgeClass) || CompositionEdge.isSlaveType(edgeClass.asSubclass(CompositionEdge.class), elementClass)) {
                    //System.err.println(true);
                    return true;
                }
            }
            //System.err.println(false);
        }
        return false;
    }

    /**
     * Liefert für die übergebene Elementklasse alle Kantenklassen, die nur einmal an ihr hängen dürfen. Diese müssen beim Join ebenfalls
     * zusammengeführt werden. Z.B. darf ein Rechanwendungsbaustein laut Metamodell nur ein Datenbanksystem besitzen. Werden zwei
     * Rechanwendungsbausteine mit jeweils einem Datenbanksystem gejoined, dann müssen auch die Datenbanksysteme gejoined werden.
     *
     * @param elementClass
     * @return
     */
    public static final Set<Class<? extends Edge>> getSubordinatedJoinbleTypes(final Class<? extends ModelElement> elementClass) {
        Set<Class<? extends Edge>> edgeClassesToSubordinatedJoinbleTypes = new HashSet<>();
        for (Class<? extends Edge> edgeClass : getEdgeTypes(elementClass)) {
            Edge.getOther(edgeClass, elementClass);
            int maxCardinality = Edge.getMaxCardinality(elementClass, edgeClass);
            if (maxCardinality == 1) {
                edgeClassesToSubordinatedJoinbleTypes.add(edgeClass);
            }
        }
        return edgeClassesToSubordinatedJoinbleTypes;
    }

    /** Cache für die Funktion {@link #getInitialSubtypes(Class)} */
    private static Map<Class<? extends ModelElement>, Set<Class<? extends Edge>>> INITIAL_SUBTYPES = new HashMap<>();

    /**
     * Liefert für eine Elementklasse alle Elementklassen, die ihr untergeordnet sind (also über eine Komposition mit
     * ihr verbunden sind, bei der sie der Master ist) und die minimale Kardinlität der Unterklassen > 0 ist.
     *
     * @param elementClass
     */
    public static final Set<Class<? extends Edge>> getInitialSubtypes(final Class<? extends ModelElement> elementClass) {
        Set<Class<? extends Edge>> initialSubtypes = INITIAL_SUBTYPES.get(elementClass);
        if (initialSubtypes == null) {
            ImmutableSet.Builder<Class<? extends Edge>> initialSubtypesBuilder = ImmutableSet.<Class<? extends Edge>> builder();
            Class<? extends CompositionEdge>[] compositionEdgeTypes = getCompositionEdgeTypes(elementClass, true);
            for (Class<? extends CompositionEdge> compositionEdgeType : compositionEdgeTypes) {
                if (CompositionEdge.getMinMasterToSlaveCardinality(compositionEdgeType) > ZERO) {
                    initialSubtypesBuilder.add(compositionEdgeType);
                }
            }
            initialSubtypes = initialSubtypesBuilder.build();
            INITIAL_SUBTYPES.put(elementClass, initialSubtypes);
        }
        return initialSubtypes;
    }

    private final static Set<Class<? extends ModelElement>> GENERATE_NAME_CLASSES = metaModel.getGenerateNameClasses();

    /**
     * dieser boolean muss in allen Node auf true gesetzt werden, die eine eigene toString() besitzen, welche aus anderen Modellelementen den Namen
     * generiert (siehe AufOrgKombination, EtntEtdtKombination)
     *
     * @return
     */
    public final static boolean isGenerateName(Class<? extends ModelElement> elementClass) {
        while (elementClass != ModelElement.class) {
            if (GENERATE_NAME_CLASSES.contains(elementClass)) {
                return true;
            }
            elementClass = elementClass.getSuperclass().asSubclass(ModelElement.class);
        }
        return false;
    }

    public static final String getVisibleLayerName(final int layer) {
        String resKey = "layer";
        int reskeyLayerNumber = -1;
        for (int i = 0; i < VISIBLE_LAYERS.length; i++) {
            if (layer == VISIBLE_LAYERS[i]) {
                reskeyLayerNumber = i + 1;
                break;
            }
        }
        //das auskommentierte geht eigentlich genausogut, aber das hier ist lesbarer
        //        int visibleLayers = LAYER_COUNT / 2 + 1; // = 3
        //        // 4 = 1 -> 4 / 2 = 2 - visibleLayers = -1 * -1 = 1
        //        // 2 = 2 -> 2 / 2 = 1 - visibleLayers = -2 * -1 = 2
        //        // 0 = 3 -> 0 / 2 = 0 - visibleLayers = -3 * -1 = 3
        //        int reskeyLayerNumber = -(layer / 2 - visibleLayers);
        try {
            return getResString(resKey + reskeyLayerNumber);
        } catch (Exception e) {
            return getResString(resKey) + reskeyLayerNumber;
        }
    }

    /**
     * Liefert die {@link MetaPathDefinition} des Metamodells
     *
     * @return
     */
    public static final MetaPathDefinition getPathsDefinition() {
        return metaModel.getPathsDefinition();
    }

    /**
     * Liefert die {@link GraphViewDefinition} des Metamodells
     *
     * @return
     */
    public static final GraphViewDefinition getGraphViewDefinition() {
        return metaModel.getGraphViewDefinition();
    }

    private static CopyDependencies copyDependencies = metaModel.getCopyDependencies();

    public static Collection<Class<? extends ModelElement>> getCopyDependencies(final Class<? extends ModelElement> elementClass) {
        return copyDependencies.get(elementClass);
    }

    public static boolean avoidDuplicates(final Class<? extends ModelElement> elementClass) {
        return copyDependencies.avoidDuplicates(elementClass);
    }

    /**
     * Liefert die {@link AnalysisDefinition} des Metamodells
     *
     * @return
     */
    public static final AnalysisDefinition getAnalysisDefinition() {
        return metaModel.getAnalysisDefinition();
    }

    /**
     * Liefert die Actions, die für das spezielle Metamodell in das Extras-Menü eingetragen werden sollen
     *
     * @param plugins
     * @return
     */
    public static final Action[] getExtrasActions(final boolean plugins) {
        return metaModel.getExtrasActions(plugins);
    }

    /**
     * Liefert alle anlegbaren MetaPfade, bei denen für selektierte Elemente der übergebenen Elementart im Kontextmenü eine Liste aller existierenden
     * Elemente angeboten werden soll, zu denen ein Pfad angelegt werden soll.
     *
     * @param elementClass
     */
    public static Collection<SimpleMetaPath> getCreatableMetaPaths(final Class<? extends ModelElement> elementClass) {
        return metaModel.getCreatableMetaPaths(elementClass);
    }

    /**
     * Liefert alle anlegbaren MetaPfade die zwischen den übergebenen Elementarten im Metamodell definiert sind. Alle diese Pfade werden als
     * verbindbare Pfade im Kontextmenü angeboten, wenn das zuletzt markierte Element und ein anderes markiertes Element zuwesiungskompatibel zu den
     * übergebenen Elementklassen sind. Diese Pfade werden dann mit allen Zwischenelementen zwischen den beiden Elementen angelegt.
     *
     * @param elementClass1
     * @param elementClass2
     */
    public static Collection<SimpleMetaPath> getCreatableMetaPaths(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        ImmutableList.Builder<SimpleMetaPath> creatableMetaPaths = ImmutableList.builder();
        for (SimpleMetaPath metaPath : metaModel.getCreatableMetaPaths(elementClass1)) {
            Class<? extends ModelElement> endClass = metaPath.getEndClass();
            if (endClass.isAssignableFrom(elementClass2)) {
                creatableMetaPaths.add(metaPath);
            }
        }
        return creatableMetaPaths.build();
    }

}