package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.UNLIMITED;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO;
import static de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge.getMaxMasterToSlaveCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge.getMinMasterToSlaveCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
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
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import javax.swing.Action;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmMain;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.IsPartOfEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement_Textfield_Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfeld;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * @author N.N., AXS
 */
public final class ModelConstants {

    private static MetaModel metaModel = initMetaModel();

    public static MetaModel initMetaModel() {
        try {
            return Tool3lgmMain.metaModelClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     */
    private static Map<String, String> OLD_TO_NEW_CLASS_NAME = metaModel.getOldToNewClassName();

    /**
     * Leeres Array als Standardrückgabetyp für zu überschreibende Funktionen.
     */
    @SuppressWarnings("unchecked")
    public static final Class<? extends ModelElement>[] EMPTY_ELEMENT_CLASS_ARRAY = new Class[0];

    @SuppressWarnings("unchecked")
    public static final Class<? extends Edge>[] EMPTY_EDGE_CLASS_ARRAY = new Class[0];

    @SuppressWarnings("unchecked")
    public static final Class<? extends CompositionEdge>[] EMPTY_COMPOSITION_CLASS_ARRAY = new Class[0];

    //Bei Gelegenheit mal ersetzen (Das ist aber schon etwas mehr Arbeit)
    //public enum LAYER {NO_LAYER, PHYSICAL_LAYER, INTER_LOGICAL_PHYSICAL_LAYER, LOGICAL_LAYER, INTER_DOMAIN_LOGICAL_LAYER, DOMAIN_LAYER};
    public static final int NO_LAYER = -1;

    public static final int PHYSICAL_LAYER = 0;

    public static final int INTER_LOGICAL_PHYSICAL_LAYER = 1;

    public static final int LOGICAL_LAYER = 2;

    public static final int INTER_DOMAIN_LOGICAL_LAYER = 3;

    public static final int DOMAIN_LAYER = 4;

    public static final int[] LAYERS = {
            PHYSICAL_LAYER,
            INTER_LOGICAL_PHYSICAL_LAYER,
            LOGICAL_LAYER,
            INTER_DOMAIN_LOGICAL_LAYER,
            DOMAIN_LAYER
    };

    public static final int[] VISIBLE_LAYERS = {
            DOMAIN_LAYER,
            LOGICAL_LAYER,
            PHYSICAL_LAYER
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

    private static final String PLURAL_NAME_RES_KEY_SUFFIX = "_p";

    /**
     * Mappt von den Knotenklassen auf den zugehörigen Short-Name für die HashString der Elemente. Diese 3-Buchstabigen Klassenkürzel sind nicht
     * zwangsläufig eindeutig und diesen lediglich der besseren Lesbarkeit von Hash-Strings, denen sie immer
     * Vorangestellt werden.
     */
    private static HashMap<Class<? extends ModelElement>, String> elementClassToHashShortName = null;

    /**
     * Liste aller geöffneten Dialoge
     */
    public static final ArrayList<ElementPropertyDialog> dialogs = new ArrayList<>();

    /**
     * Standardrückgabewert bei Fehlern = -1 ;
     */
    public static final int STANDARD_ERROR_INT_VALUE = new Integer(-1);

    /** Alle Node der FE als Array */
    public static final Class<? extends ModelElement>[] ALL_DOMAIN_LAYER_NODES = metaModel.getAllDomainLayerNodes();

    /** Alle Node der FE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_DOMAIN_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_DOMAIN_LAYER_NODES));

    /** Alle Node zw. FE und LWE als Array */
    public static final Class<? extends ModelElement>[] ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES = metaModel.getAllInterDomainLogicalLayerNodes();

    /** Alle Node zw. FE und LWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES));

    /** Alle Node der LWE als Array */
    public static final Class<? extends ModelElement>[] ALL_LOGICAL_LAYER_NODES = metaModel.getAllLogicalLayerNodes();

    /** Alle Node der LWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_LOGICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_LOGICAL_LAYER_NODES));

    /** Alle Node zw. LWE und PWE als Array */
    public static final Class<? extends ModelElement>[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES = metaModel.getAllInterLogicalPhysicalLayerNodes();

    /** Alle Node zw. LWE und PWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES));

    /** Alle Node der PWE als Array */
    public static final Class<? extends ModelElement>[] ALL_PHYSICAL_LAYER_NODES = metaModel.getAllPhysicalLayerNodes();

    /** Alle Node der PWE als HashSet */
    public static final Set<Class<? extends ModelElement>> ALL_PHYSICAL_LAYER_NODES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_PHYSICAL_LAYER_NODES));

    /** Set aller Knotenklassen */
    public static final Set<Class<? extends ModelElement>> ALL_NODES_SET = ImmutableSet.<Class<? extends ModelElement>> builder().addAll(ALL_DOMAIN_LAYER_NODES_SET).addAll(ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES_SET).addAll(ALL_LOGICAL_LAYER_NODES_SET)
            .addAll(ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES_SET).addAll(ALL_PHYSICAL_LAYER_NODES_SET).build();

    /** Array aller Knotenklassen */
    @SuppressWarnings("unchecked")
    public static final Class<? extends ModelElement>[] ALL_NODES = new Class[ALL_NODES_SET.size()];
    static {
        System.arraycopy(ALL_NODES_SET.toArray(), 0, ALL_NODES, 0, ALL_NODES.length);
    }

    ////////////
    // Kanten //
    ////////////

    /** Kanten FE */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_DOMAIN_LAYER_EDGES = metaModel.getAllDomainLayerEdges();

    /** Kanten Inter FE -LWE */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES = metaModel.getAllInterDomainLogicalLayerEdges();

    /** Kanten LWE */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_LOGICAL_LAYER_EDGES = metaModel.getAllLogicalLayerEdges();

    /** Kanten Inter LWE - PWE */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES = metaModel.getAllInterLogicalPhysicalLayerEdges();

    /** Kanten PWE */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_PHYSICAL_LAYER_EDGES = metaModel.getAllPhysicalLayerEdges();

    public static final Set<Class<? extends Edge>> ALL_DOMAIN_LAYER_EDGES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_DOMAIN_LAYER_EDGES));

    public static final Set<Class<? extends Edge>> ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES));

    public static final Set<Class<? extends Edge>> ALL_LOGICAL_LAYER_EDGES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_LOGICAL_LAYER_EDGES));

    public static final Set<Class<? extends Edge>> ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES));

    public static final Set<Class<? extends Edge>> ALL_PHYSICAL_LAYER_EDGES_SET = ImmutableSet.copyOf(Arrays.asList(ALL_PHYSICAL_LAYER_EDGES));

    /** Set aller Kantenklassen */
    public static final Set<Class<? extends Edge>> ALL_EDGES_SET = ImmutableSet.<Class<? extends Edge>> builder().addAll(ALL_DOMAIN_LAYER_EDGES_SET).addAll(ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES_SET).addAll(ALL_LOGICAL_LAYER_EDGES_SET)
            .addAll(ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES_SET).addAll(ALL_PHYSICAL_LAYER_EDGES_SET).build();

    /** Array aller Kantenklassen */
    @SuppressWarnings("unchecked")
    public static final Class<? extends Edge>[] ALL_EDGES = new Class[ALL_EDGES_SET.size()];
    static {
        System.arraycopy(ALL_EDGES_SET.toArray(), 0, ALL_EDGES, 0, ALL_EDGES.length);
    }

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

    public static final boolean hasSortedEdgesToPaintable(final Class<? extends ModelElement> elementClass) {
        return getGraphViewDefinition().hasSortedEdgeClassesToPaintable(elementClass);
    }

    /**
     * Extrahiert aus den übergebenen Knoten alle, die im Baum angezeigt werden.
     *
     * @param elementClasses Elementklassen, die gefiltert werden sollen
     * @param creatableOnly wenn <code>true</code>, werden von den anzuzeigenden Knoten nur die übrig gelassen, die man auch ohne ein anderes Element
     *            anlegen kann
     * @return
     */
    private static Class<? extends ModelElement>[] getTreeVisibleNodes(final Class<? extends ModelElement>[] elementClasses, final boolean creatableOnly) {
        ImmutableSet.Builder<Class<? extends ModelElement>> creatableNodes = new ImmutableSet.Builder<>();
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (!ModelConstants.isEdgeType(elementClass)) {
                if (!creatableOnly || creatableOnly && !ModelConstants.isSlaveType(elementClass)) {
                    if (!ModelConstants.isAbstract(elementClass)) {
                        if (!ModelConstants.isExistenceDependent(elementClass, true)) {
                            creatableNodes.add(elementClass);
                        }
                    }
                }
            }
        }
        ImmutableSet<Class<? extends ModelElement>> treeVisibleClassesSet = creatableNodes.build();
        @SuppressWarnings("unchecked")
        Class<? extends ModelElement>[] treeVisibleClasses = new Class[treeVisibleClassesSet.size()];
        System.arraycopy(treeVisibleClassesSet.toArray(), 0, treeVisibleClasses, 0, treeVisibleClasses.length);
        return treeVisibleClasses;
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
     * Liste aller Kantenklassen, die eigentlich 2 gerichtete Assoziationen im Metamodell sein müssten, aber aus Unwissenheit beim Entwurf des
     * Metamodells fehlerhafterweise in eine Assoziation verpackt wurden, bei denen die Richtung der Edge
     * (Doppelkante.FORWARD, Doppelkante.BACKWARD, Doppelkante.DOUBLE) die Bedeutung angibt. Nur wegen den 4 braucht man den ganzen
     * Doppelkanten-Richtungsquatsch. Wenn sie grafisch dargestellt werden, dann werden sie als eine Edge dargestellt werden, die
     * je nach Bedeutung eine der Richtungen oder beide als Pfeile darstellt. Hier wurde also das Model misbraucht, um im View diese Assoziationen
     * zusammenzufassen.
     */
    private static final Set<Class<? extends Edge>> DOUBLE_MEANING_EDGE_CLASSES = metaModel.getDoubleMeaningEdgeClasses();

    /**
     * Prüft, ob die übergebene Klasse eine Kantenklasse mit mehreren Bedeutungen ist, also die Richtung der Edge die Bedeutung angibt.
     *
     * @see #DOUBLE_MEANING_EDGE_CLASSES
     * @param edgeClass
     * @return
     */
    public static final boolean isDoubleMeaningEdge(final Class<?> edgeClass) {
        return DOUBLE_MEANING_EDGE_CLASSES.contains(edgeClass);
    }

    //    /**
    //     * Prüft, ob bei der Edge die Richtung egal ist bzw. immer DOUBLE sein sollte, damit auch alle Verbindungen
    //     * zwischen den Elementen gefunden werden. Das gilt für alle einfachen Doppelkanten, die dieselben
    //     * Elementarten verbinden sowie keine DoubleMeaningEdges, keine IsPartOfEdgeen und keine Compositions sind.
    //     *
    //     * @return
    //     */
    //    public static final boolean isAlwaysDoubleConnectedEdge(final Class<? extends Edge> edgeClass) {
    //        //        return Edge.getStartClass(edgeClass) == Edge.getEndClass(edgeClass) && !(isDoubleMeaningEdge(edgeClass) || IsPartOfEdge.class.isAssignableFrom(edgeClass) || CompositionEdge.class.isAssignableFrom(edgeClass));
    //        //nochmal geändert: wenn die Kante dieselbe Elementart verbindet und in beide Richtungen gleich heißt -> immer doppelt
    //        //return Edge.getStartClass(edgeClass) == Edge.getEndClass(edgeClass) && getForwardMetaAssociationName(edgeClass).equals(getBackwardMetaAssociationName(edgeClass));
    //        //return !isDirectedEdge(edgeClass);
    //    }

    /**
     * Prüft, ob Kante gerichtet ist. Das ist sie, wenn sie nicht dieselben Elementarten verbindet und in beide Richtungen einen unterwchiedlichen
     * Anzeigenamen hat.
     *
     * @param edgeClass
     * @return
     */
    public static final boolean isDirectedEdge(final Class<? extends Edge> edgeClass) {
        return Edge.getStartClass(edgeClass) != Edge.getEndClass(edgeClass) || !getForwardMetaAssociationName(edgeClass).equals(getBackwardMetaAssociationName(edgeClass));
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    /**
     * Mappt von einer Elementklasse auf das Array aller instanziierbaren und zu dieser Klasse zuweisungskompatiblen ModelElement-Klassen.
     */
    public static final HashMap<Class<? extends ModelElement>, Class<? extends ModelElement>[]> ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES = new HashMap<>();

    /**
     * Liefert alle nichtabstrakten, zur übergebenen Klasse zuweisungskompatiblen Element- oder Kantenklassen. Die übergebene Klasse selbst ist in den
     * Rückgabewerten enthalten, wenn sie nichtabstract ist.
     *
     * @param elementClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final Class<? extends ModelElement>[] getInstanciableAssignableClasses(final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement>[] elementClasses = ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.get(elementClass);
        if (elementClasses != null) {
            return elementClasses;
        }
        HashSet<Class<? extends ModelElement>> al = new HashSet<>();
        for (Class<? extends ModelElement> clazz : ALL_NODES) {
            if (elementClass.isAssignableFrom(clazz) && !isAbstract(clazz)) {
                al.add(clazz);
            }
        }
        for (Class<? extends ModelElement> clazz : ALL_EDGES) {
            if (elementClass.isAssignableFrom(clazz) && !isAbstract(clazz)) {
                al.add(clazz);
            }
        }
        if (al.size() == 0) {
            ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.put(elementClass, EMPTY_ELEMENT_CLASS_ARRAY);
            return EMPTY_ELEMENT_CLASS_ARRAY;
        }
        elementClasses = new Class[al.size()];
        System.arraycopy(al.toArray(), 0, elementClasses, 0, elementClasses.length);
        ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES.put(elementClass, elementClasses);
        return elementClasses;
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
        for (Class<? extends Edge> edgeClass : ALL_EDGES) {
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
     * Liefert <code>true</code>, wenn die übergebene Elementklasse die übergebene Kantenart hat.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    public static final boolean hasEdgeType(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return CollectionUtils.arrayContains(getEdgeTypes(elementClass), edgeClass);
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
    public static final Class<? extends ModelElement>[] TREE_DOMAIN_LAYER_NODES = getTreeVisibleNodes(ALL_DOMAIN_LAYER_NODES, false);

    /** Alle im Baum auf der FE anlegbaren Node */
    public static final Class<? extends ModelElement>[] CREATABLE_DOMAIN_LAYER_NODES = getTreeVisibleNodes(TREE_DOMAIN_LAYER_NODES, true);

    /** Alle im Baum auf der LWE sichtbaren Node */
    public static final Class<? extends ModelElement>[] TREE_LOGICAL_LAYER_NODES = getTreeVisibleNodes(ALL_LOGICAL_LAYER_NODES, false);

    /** Alle im Baum auf der FE anlegbaren Node */
    public static final Class<? extends ModelElement>[] CREATABLE_LOGICAL_LAYER_NODES = getTreeVisibleNodes(TREE_LOGICAL_LAYER_NODES, true);

    /** Alle im Baum auf der PWE sichtbaren Node */
    public static final Class<? extends ModelElement>[] TREE_PHYSICAL_LAYER_NODES = getTreeVisibleNodes(ALL_PHYSICAL_LAYER_NODES, false);

    /** Alle im Baum auf der PWE anlegbaren Node */
    public static final Class<? extends ModelElement>[] CREATABLE_PHYSICAL_LAYER_NODES = getTreeVisibleNodes(TREE_PHYSICAL_LAYER_NODES, true);

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

    static {
        //    	System.err.println(MetaPathDefinitions.AUFGABE_BEARBEITET_OBJEKTTYP.toString());
        //    	System.err.println(MetaPathDefinitions.AUFGABE_WIRD_ERLEDIGT_IN_ORGANISATIONSEINHEIT.toString());
        //    	System.err.println(MetaPathDefinitions.AUFGABE_WIRD_UNTERSTUETZT_DURCH_PHYSICHER_DV_BAUSTEIN.toString());
        //    	System.err.println(MetaPathDefinitions.TESTPFAD.toString());
        //    	System.err.println(MetaPathDefinitions.OBJEKTTYP_WIRD_GESPEICHERT_VON_LOGICSCHEM_SPEICHER.toString());
        //    	System.exit(0);
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
     * Liefert <code>true</code>, wenn die übergebene Elementklasse in der Grafik darstellbar ist.
     *
     * @param elementClass
     * @return
     */
    public static final boolean isPaintable(final Class<? extends ModelElement> elementClass) {
        return getGraphViewDefinition().isPaintable(elementClass);
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
        return Knickpunkt.class.isAssignableFrom(elementClass) || BendpointContainer.class.isAssignableFrom(elementClass);
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

    /**
     * Liefert aus der <code>HashMap oldToNewName</code> den aktuellen Klassennamen für den übergebenen alten Klassennamen. <br>
     * Ist in <code>oldToNewName</code> kein Eintrag für den übergebenen alten Klassennamen vorhanden, wird davon ausgegangen, dass der alte Name der
     * aktuelle ist.
     *
     * @param oldName
     * @return
     */
    private static final String getActualClassName(String oldName) {
        String newName = OLD_TO_NEW_CLASS_NAME.get(oldName);
        //wenn kein Eintrag für den alten Namen gefunden wurde, ist der alte
        // Namen der aktuelle
        if (newName == null) {
            return oldName;
        }
        //solange immer nach neuen Ersetzungen suchen, bis es keine mehr gibt
        // -> den letzten
        //gefundenen Namen zurückgeben
        while (true) {
            oldName = newName;
            newName = OLD_TO_NEW_CLASS_NAME.get(oldName);
            if (newName == null) {
                return oldName;
            }
        }
    }

    /** Mappt vom Klassennamen auf die Klasse. Ist der Cache für die Funktion {@link #getClassForName(String)} */
    private static final Map<String, Class<? extends ModelElement>> CLASS_NAME_TO_CLASS_MAP = new HashMap<>();

    /** Alle Modellelementklassen, die instanziierbar sind und in jedem Metamodell automatisch enthalten sind */
    private static final Set<Class<? extends ModelElement>> META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES = ImmutableSet.of(Knickpunkt.class, Textfeld.class, ModelElement_Textfield_Edge.class);

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

    public static final String NODE_PACKAGE_NAME = ALL_NODES.length > 0 ? ALL_NODES[0].getPackage().getName() + "." : "";

    public static final String EDGE_PACKAGE_NAME = ALL_EDGES.length > 0 ? ALL_EDGES[0].getPackage().getName() + "." : "";

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
                    String actualClassName = getActualClassName(simpleClassName);
                    if (!actualClassName.equals(simpleClassName)) {
                        clazz = getClassForName(actualClassName);
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

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param clazz Klasse für die der anzeigbare Name geliefert werden soll
     * @param plural wenn true, wird der Pluralname zurück gegeben, sonst der Singular
     * @return String aus dem geladenen ResourcenBundle
     */
    private static final String getDisplayableName(Class<? extends ModelElement> clazz, final boolean plural) {
        if (clazz == null) {
            return null;
        }
        while (ModelElement.class.isAssignableFrom(clazz)) {
            try {
                String resKey = clazz.getSimpleName();
                if (plural) {
                    resKey += PLURAL_NAME_RES_KEY_SUFFIX;
                }
                return getResString(resKey);
            } catch (MissingResourceException mre) {
                clazz = clazz.getSuperclass().asSubclass(ModelElement.class);
            }
        }
        return null;
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse in der Mehrzahl (Plural) aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param clazz Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public static final String getDisplayablePluralName(final Class<? extends ModelElement> clazz) {
        return getDisplayableName(clazz, true);
    }

    /**
     * Gibt den anzeigbaren Namen einer Klasse aus den Resoucen zurück.<br>
     * Wird <code>null</code> übergeben, wird der Name für ein Modell (im Deutschen also "Modell" zurück gegeben.)
     *
     * @param clazz Klasse für die der anzeigbare Name geliefert werden soll
     * @return String aus dem geladenen ResourcenBundle
     */
    public static final String getDisplayableName(final Class<? extends ModelElement> clazz) {
        return getDisplayableName(clazz, false);
    }

    /**
     * Liefert den Standardanzeigenamen für ein Modelelement der übergebenen Art
     *
     * @param me
     * @return
     * @see #getDisplayableName(Class)
     */
    public static final String getDisplayableName(final ModelElement me) {
        return getDisplayableName(me.getClass());
    }

    /*************************/

    /**
     * @param edgeClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getForwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Vorwärtsrichtung ohne die Elementartnamen, die die Edge verbindet.
     *
     * @param edgeClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getFullForwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getForwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getForwardMetaAssociationName(edgeClass, DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Edge> edgeClass, final int connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getMetaAssociationName(edgeClass, false, connectionState, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getBackwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Rückwärtsrichtung mit den Elementartnamen, die die Edge verbindet.
     *
     * @param edgeClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getFullBackwardMetaAssociationName(final Class<? extends Edge> edgeClass) {
        return getBackwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getBackwardMetaAssociationName(edgeClass, DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Edge> edgeClass, final int connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getMetaAssociationName(edgeClass, true, connectionState, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param direction
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getFullMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final int direction) {
        return getMetaAssociationName(edgeClass, switchDefinedDirection, direction, true, true);
    }

    /**
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse
     *
     * @param edgeClass
     * @param switchDefinedDirection gibt an, ob die Bedeutung der Edge von der Startklasse zur Endklasse (<code>false</code>) oder von der Endklasse
     *            zur Startklasse (<code>true</code>) zurück gegeben werden soll. Mit Start- und Endklasse sind hier die
     *            beiden Elementklasse gemeint, die in der Kantenklasse in dieser Reihenfolge definiert sind
     * @param connectionState Doppelkante.FORWARD, Doppelkante.BACKWARD oder Doppelkante.DOUBLE - Bei allen Assoziationen, die in jede Richtung nur
     *            eine Bedeutung haben, ist dieser Parameter egal. Bei Assoziationen, die mehr als eine Bedeutung haben, kann hier
     *            die Richtung angegeben werden für die die bedeutung zurück gegeben werden soll.<br>
     *            Alle Assoziationen im aktuellen Metamodell haben maximal 2 Bedeutungen. Das ist bei allen Assoziationen der Fall, die eigentlich 2
     *            Assoziationen sind, aber aus allerlei Gründen in eine gepackt wurden.<br>
     *            Beispiel 1: AufObjVerbindung = Assoziation zw. Startklasse Aufgabe und Endklasse Objekttyp.<br>
     *            <ul>
     *            <li>
     *            <code>switchDefinedDirection == false</code>, <code>direction == {@link Doppelkante}.FORWARD</code> heißt
     *            "Aufgabe bearbeitet Objekttyp"</li>
     *            <li>
     *            <code>switchDefinedDirection == false</code>, <code>direction == {@link Doppelkante}.BACKWARD</code> heißt
     *            "Aufgabe interpretiert Objekttyp"</li>
     *            <li>
     *            <code>switchDefinedDirection == true</code>, <code>direction == {@link Doppelkante}.FORWARD</code> heißt
     *            "Objekttyp wird interpretiert von Aufgabe "</li>
     *            <li>
     *            <code>switchDefinedDirection == true</code>, <code>direction == {@link Doppelkante}.BACKWARD</code> heißt
     *            "Objekttyp wird bearbeitet von Aufgabe"</li>
     *            </ul>
     * @return
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final int connectionState) {
        return getMetaAssociationName(edgeClass, switchDefinedDirection, connectionState, false, false);
    }

    /**
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse
     *
     * @param edgeClass
     * @param switchDefinedDirection gibt an, ob die Bedeutung der Edge von der Startklasse zur Endklasse (<code>false</code>) oder von der Endklasse
     *            zur Startklasse (<code>true</code>) zurück gegeben werden soll. Mit Start- und Endklasse sind hier die
     *            beiden Elementklasse gemeint, die in der Kantenklasse in dieser Reihenfolge definiert sind
     * @param connectionState Doppelkante.FORWARD, Doppelkante.BACKWARD oder Doppelkante.DOUBLE - Bei allen Assoziationen, die in jede Richtung nur
     *            eine Bedeutung haben, ist dieser Parameter egal. Bei Assoziationen, die mehr als eine Bedeutung haben, kann hier
     *            die Richtung angegeben werden für die die bedeutung zurück gegeben werden soll.<br>
     *            Alle Assoziationen im aktuellen Metamodell haben maximal 2 Bedeutungen. Das ist bei allen Assoziationen der Fall, die eigentlich 2
     *            Assoziationen sind, aber aus allerlei Gründen in eine gepackt wurden.<br>
     *            Beispiel 1: AufObjVerbindung = Assoziation zw. Startklasse Aufgabe und Endklasse Objekttyp.<br>
     *            <ul>
     *            <li>
     *            <code>switchDefinedDirection == false</code>, <code>direction == {@link Doppelkante}.FORWARD</code> heißt
     *            "Aufgabe bearbeitet Objekttyp"</li>
     *            <li>
     *            <code>switchDefinedDirection == false</code>, <code>direction == {@link Doppelkante}.BACKWARD</code> heißt
     *            "Aufgabe interpretiert Objekttyp"</li>
     *            <li>
     *            <code>switchDefinedDirection == true</code>, <code>direction == {@link Doppelkante}.FORWARD</code> heißt
     *            "Objekttyp wird interpretiert von Aufgabe "</li>
     *            <li>
     *            <code>switchDefinedDirection == true</code>, <code>direction == {@link Doppelkante}.BACKWARD</code> heißt
     *            "Objekttyp wird bearbeitet von Aufgabe"</li>
     *            </ul>
     * @param doubleMeaningEdgeDelimiter String der bei Kanten mit doppelter Bedeutung, bei denen beide Bedeutungen gleichzeitig ausgegeben werden
     *            sollen zwischen die beiden Bedeutungen geschrieben wird.
     * @return
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final int connectionState, final String doubleMeaningEdgeDelimiter) {
        //alle Kantenamen müssen mit SimplerKantenklassenName_f oder SimplerKantenklassenName_b angegeben sein oder bei Kanten mit doppelter Bedeutung SimplerKantenklassenName_f_f,
        //SimplerKantenklassenName_f_b, SimplerKantenklassenName_b_f und SimplerKantenklassenName_b_b
        String edgeClassName = edgeClass.getSimpleName();
        final String mainEdgeDirection = !switchDefinedDirection ? "_f" : "_b";
        String edgeName = getEdgeName(edgeClassName, mainEdgeDirection);
        if (edgeName != null) {
            return edgeName;
        }
        if (edgeName == null) {
            String interpretedDirection = connectionState == FORWARD ? "_f" : connectionState == BACKWARD ? "_b" : null;
            if (interpretedDirection != null) {
                edgeName = getEdgeName(edgeClassName, mainEdgeDirection, interpretedDirection);
                if (edgeName != null) {
                    return edgeName;
                }
            } else {
                edgeName = getEdgeName(edgeClassName, mainEdgeDirection, "_f");
                if (edgeName != null) {
                    //wenn es einen Vorwärtsnamen gibt, dann muss auch ein Rückwärtsname angegeben sein!
                    return edgeName + doubleMeaningEdgeDelimiter + getEdgeName(edgeClassName, mainEdgeDirection, "_b");
                }
            }
        }
        //wenn für den aktuellen Klassennamen kein Name gefunden wurde -> nimm die Oberklasse -> irgendwann kommt man bei Edge.class an, für die auf jeden Fall ein Namen ex.
        return getMetaAssociationName(edgeClass.getSuperclass().asSubclass(Edge.class), switchDefinedDirection, connectionState, doubleMeaningEdgeDelimiter);
    }

    private static final String getEdgeName(final String simpleEdgeClassName, final String mainEdgeDirection) {
        return getEdgeName(simpleEdgeClassName, mainEdgeDirection, null);
    }

    private static final String getEdgeName(final String simpleEdgeClassName, final String mainEdgeDirection, final String interpretedDirection) {
        try {
            if (interpretedDirection == null) {
                return getResString(simpleEdgeClassName + mainEdgeDirection);
            }
            return getResString(simpleEdgeClassName + mainEdgeDirection + interpretedDirection);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final int connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getMetaAssociationName(edgeClass, switchDefinedDirection, connectionState, appendPostfixClass, appendPrefixClass, " / ");
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @param doubleMeaningEdgeDelimiter String der bei Kanten mit doppelter Bedeutung, bei denen beide Bedeutungen gleichzeitig ausgegeben werden
     *            sollen zwischen die beiden Bedeutungen geschrieben wird.
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getMetaAssociationName(final Class<? extends Edge> edgeClass, final boolean switchDefinedDirection, final int connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass,
            final String doubleMeaningEdgeDelimiter) {
        if (!appendPrefixClass && !appendPostfixClass) {
            return getMetaAssociationName(edgeClass, switchDefinedDirection, connectionState, doubleMeaningEdgeDelimiter);
        }
        StringBuilder sb = new StringBuilder();
        if (appendPrefixClass) {
            sb.append(getDisplayableName(!switchDefinedDirection ? getStartClass(edgeClass) : getEndClass(edgeClass)));
            sb.append(" ");
        }
        sb.append(getMetaAssociationName(edgeClass, switchDefinedDirection, connectionState, doubleMeaningEdgeDelimiter));
        if (appendPostfixClass) {
            sb.append(" ");
            sb.append(getDisplayableName(!switchDefinedDirection ? getEndClass(edgeClass) : getStartClass(edgeClass)));
        }
        return sb.toString();
    }

    /**
     * Mappt von einer Elementart auf die Klassen der {@link IsPartOfEdge}en, über die der Elementart Teilemente untergeordnet werden kann.
     */
    private static final Map<Class<? extends ModelElement>, Class<? extends IsPartOfEdge>[]> ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES = new HashMap<>(5);

    /**
     * Mappt von einer Elementart auf die Klassen der {@link IsPartOfEdge}en, über die die Elementart als Teilement untergeordnet werden kann.
     */
    private static final Map<Class<? extends ModelElement>, Class<? extends IsPartOfEdge>[]> ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES = new HashMap<>(5);
    //die Funktion mit dem komischen Namen ist nur dazu da, dass die @SuppressWarnings("unchecked") nicht über die
    //gesamt Datei geschrieben werden muss (wenn man den Funktionsinhalt einfach in einen static-Block schreibt,
    //kann man die Warnungen nur für die ganze Datei unterdrücken
    static {
        fill_ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES_and_ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES();
    }

    @SuppressWarnings("unchecked")
    private static final void fill_ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES_and_ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES() {
        for (int i = 0; i < ALL_NODES.length; i++) {
            //Hole alle Kantenklassen der Zielklasse und suche alle IsPartOfEdgeen
            for (Class<? extends Edge> c : getEdgeTypes(ALL_NODES[i])) {
                if (IsPartOfEdge.class.isAssignableFrom(c)) {
                    Class<? extends IsPartOfEdge>[] edgeClasses = null;
                    Class<? extends IsPartOfEdge> poClass = c.asSubclass(IsPartOfEdge.class);
                    if (IsPartOfEdge.isParentClass(poClass, ALL_NODES[i])) {
                        edgeClasses = ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.get(ALL_NODES[i]);
                        if (edgeClasses == null) {
                            edgeClasses = new Class[1];
                            edgeClasses[0] = poClass;
                            ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.put(ALL_NODES[i], edgeClasses);
                        } else {
                            Class<? extends IsPartOfEdge>[] newEdgeClasses = new Class[edgeClasses.length + 1];
                            System.arraycopy(edgeClasses, 0, newEdgeClasses, 0, edgeClasses.length);
                            newEdgeClasses[edgeClasses.length] = poClass;
                            ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.put(ALL_NODES[i], newEdgeClasses);
                        }
                    }
                    if (IsPartOfEdge.isPartClass(poClass, ALL_NODES[i])) {
                        edgeClasses = ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.get(ALL_NODES[i]);
                        if (edgeClasses == null) {
                            edgeClasses = new Class[1];
                            edgeClasses[0] = poClass;
                            ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.put(ALL_NODES[i], edgeClasses);
                        } else {
                            Class<? extends IsPartOfEdge>[] newEdgeClasses = new Class[edgeClasses.length + 1];
                            System.arraycopy(edgeClasses, 0, newEdgeClasses, 0, edgeClasses.length);
                            newEdgeClasses[edgeClasses.length] = poClass;
                            ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.put(ALL_NODES[i], newEdgeClasses);
                        }
                    }
                }
            }
        }
        //		System.err.println("ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES");
        //		for (Class<? extends ModelElement> meClass : ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.keySet()){
        //			System.err.println(meClass.getSimpleName() + " " + Arrays.asList(ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.get(meClass)));
        //		}
        //		System.err.println();
        //		System.err.println("ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES");
        //		for (Class<? extends ModelElement> meClass : ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.keySet()){
        //			System.err.println(meClass.getSimpleName() + " " + Arrays.asList(ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.get(meClass)));
        //		}

    }

    public static final boolean canHaveParts(final Class<? extends ModelElement> elementClass) {
        return getHasPartsEdgeClasses(elementClass).length > 0;
    }

    public static final boolean canHaveParents(final Class<? extends ModelElement> elementClass) {
        return getIsPartOfEdgeClasses(elementClass).length > 0;
    }

    public static final boolean isPartOfEdge(final Class<? extends Edge> edgeClass) {
        return IsPartOfEdge.class.isAssignableFrom(edgeClass);
    }

    /**
     * Liefert die Klassen von <code>IsPartOfEdge</code>, über die der übergebenen Elementart andere Elemente als Teile untergeordnet werden
     * können.
     *
     * @return Leeres Array, wenn es keine {@link IsPartOfEdge} gibt, aosnsten ein Array aller dieser Kantenklassen
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends IsPartOfEdge>[] getHasPartsEdgeClasses(final Class<? extends ModelElement> elementClass) {
        Class<? extends IsPartOfEdge>[] hasPartEdgeClasses = ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.get(elementClass);
        return hasPartEdgeClasses == null ? new Class[0] : hasPartEdgeClasses;
    }

    /**
     * Liefert die Klassen von <code>IsPartOfEdge</code>, über die die übergebenen Elementart anderen Elementen als Teilelement untergeordnet
     * werden kann.
     *
     * @return Leeres Array, wenn es keine {@link IsPartOfEdge} gibt, aosnsten ein Array aller dieser Kantenklassen
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends IsPartOfEdge>[] getIsPartOfEdgeClasses(final Class<? extends ModelElement> elementClass) {
        Class<? extends IsPartOfEdge>[] isPartOfEdgeClasses = ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.get(elementClass);
        return isPartOfEdgeClasses == null ? new Class[0] : isPartOfEdgeClasses;
    }

    /*******************/

    /**
     * Gibt Namenskuerzel einer Elementklasse zurueck. Diese Namenskürzel garantieren nicht, dass man von ihnen auf die Klasse zurückschließen kann.
     * Sie dienen lediglich dazu, die Hash-Strings der Modellelemente im Baukasten und der XML-Datei etwas
     * lesbarer zu gestalten.
     *
     * @param elementClass Elementklasse für die das Kürzel zurück gegeben werden soll.
     * @return String mit Namenskuerzel
     */
    public static final String getShortName(final Class<? extends ModelElement> elementClass) {

        //HashMap mit den ShortNames der Klassen initialisieren (einmal statisch)
        if (elementClassToHashShortName == null) {
            elementClassToHashShortName = new HashMap<>();
            //Set in das alle bisher gefundenen ShortNames eingetragen werden, um zu prüfen, ob ein shortName bereits existiert
            HashSet<String> allShortNames = new HashSet<>();
            loop1: for (int i = 0; i < ALL_NODES.length; i++) {
                String s = ALL_NODES[i].getSimpleName();
                //wenn der Klassenname aus weniger als 4 Zeichen besteht
                if (s.length() <= 3) {
                    elementClassToHashShortName.put(ALL_NODES[i], s.toUpperCase());
                    continue;
                }
                //mehr als 3 Zeichen
                StringBuilder shortName = new StringBuilder(3);
                for (int j = 0; j < s.length(); j++) {
                    //suche Großbuchstaben -> sie werden bevorzugt in den Shortname aufgenommen
                    String character = s.substring(j, j + 1);
                    if (character.toUpperCase().equals(character)) {
                        shortName.append(character);
                        //wenn 3 Großbuchstaben gefunden wurden
                        if (shortName.length() == 3) {
                            String sn = shortName.toString();
                            //wenn es den ShortName noch nicht gibt
                            if (!allShortNames.contains(sn)) {
                                allShortNames.add(sn);
                                elementClassToHashShortName.put(ALL_NODES[i], sn);
                                continue loop1;
                            }
                            //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter nach Großbuchstanben suchen
                            shortName.deleteCharAt(2);
                        }
                    }
                }
                //hier kommt er nur hin, wenn keine 3 Großbuchstaben gefunden wurden
                //short name hat 0 bis 2 Zeichen

                //wenn genau 2 Großbuchstaben gefunden wurden
                if (shortName.length() == 2) {
                    int lastUpperCharInClassName = 0;
                    for (int j = 0; j < shortName.length(); j++) {
                        char shortNameChar = shortName.charAt(j);
                        for (; lastUpperCharInClassName < s.length(); lastUpperCharInClassName++) {
                            if (s.charAt(lastUpperCharInClassName) == shortNameChar) {
                                break;
                            }
                        }
                    }
                    //lastUpperCharInClassName hat jetzt den Index des letzten Großbuchstaben in shortName

                    //solange hinter dem letzten Großbuchstaben noch Zeichen kommen, einfach solange diese Zeichen anhängen,
                    //bis ein eindeutiger 3-Zeichen-shortName gefunden wurde
                    while (++lastUpperCharInClassName < s.length()) {
                        shortName.append(s.charAt(lastUpperCharInClassName));
                        String sn = shortName.toString().toUpperCase();
                        //wenn es den ShortName noch nicht gibt
                        if (!allShortNames.contains(sn)) {
                            allShortNames.add(sn);
                            elementClassToHashShortName.put(ALL_NODES[i], sn);
                            continue loop1;
                        }
                        //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter suchen
                        shortName.deleteCharAt(2);
                    }
                }

                //es wurden keine 3 eindutigen Buchstaben nach Großbuchstaben gefunden -> Nimm einfach die ersten beiden
                //Buchstaben und suche einen Folgebuchstaben bis 3 eindeutige Zeichen gefunden werden (das geht immer gut,
                //wenn die Klassennamen eindeutig sind (was immer der Fall ist, wenn sie im selben package liegen) und hier
                ///unten fest steht, dass der Name mind. 4 Zeichen lang ist)
                shortName.setLength(0);
                shortName.append(s.charAt(0));
                shortName.append(s.charAt(1));
                for (int j = 2; j < s.length(); j++) {
                    shortName.append(s.charAt(j));
                    String sn = shortName.toString().toUpperCase();
                    // wenn es den ShortName noch nicht gibt
                    if (!allShortNames.contains(sn)) {
                        allShortNames.add(sn);
                        elementClassToHashShortName.put(ALL_NODES[i], sn);
                        continue loop1;
                    }
                    //es gibt den ShortName bereits -> letztes Zeichen löschen und weiter suchen
                    shortName.deleteCharAt(2);
                }

                //wenn auch das nicht gekalppt hat (der Fall dürfte nicht eintreten, wenn die Klassen alle im gleichen Package liegen,
                //da sie dann alle etwas eindeutiges bei s = class.getShortName() geliefert haben)
                //-> nimm einfach die ersten 3 Zeichen ohne noch einmal irgendwelche Eindeutigkeit zu prüfen;
                String sn = s.substring(0, 3).toUpperCase();
                allShortNames.add(sn); //kann man sich wahrscheinlich sparen, weil auch diese Kombination schon oben durchprobiert wurde, aber sicher ist sicher
                elementClassToHashShortName.put(ALL_NODES[i], sn);
            }
        }

        //Node
        if (Node.class.isAssignableFrom(elementClass)) {
            Object o = elementClassToHashShortName.get(elementClass);
            //ist null bei Layerknoten. Die brauchen aber auch keinen lesbaren Hash
            if (o == null) {
                return NO_MODEL_ELEMENT_SHORT_NAME;
            }
            return elementClassToHashShortName.get(elementClass).toString();
            //Kanten
        } else if (Edge.class.isAssignableFrom(elementClass)) {
            return EDGE_SHORT_NAME;
        }
        return NO_MODEL_ELEMENT_SHORT_NAME;
    }

    private static Map<Class<? extends ModelElement>, Integer> ELEMENT_CLASS_TO_LAYER = createELEMENT_CLASS_TO_LAYER_MAP();

    private static Map<Class<? extends ModelElement>, Integer> createELEMENT_CLASS_TO_LAYER_MAP() {
        //        ImmutableMap.Builder<Class<? extends ModelElement>, Integer> map = new ImmutableMap.Builder<>();
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
        for (Class<? extends ModelElement> elementClass : ALL_DOMAIN_LAYER_EDGES_SET) {
            map.put(elementClass, DOMAIN_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES_SET) {
            map.put(elementClass, INTER_DOMAIN_LOGICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_LOGICAL_LAYER_EDGES_SET) {
            map.put(elementClass, LOGICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES_SET) {
            map.put(elementClass, INTER_LOGICAL_PHYSICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : ALL_PHYSICAL_LAYER_EDGES_SET) {
            map.put(elementClass, PHYSICAL_LAYER);
        }
        //nicht über den Builder gehen, weil die key-Klassen mehrfach in den Sets vorkommen können. Der Builder beendet dann mit einem Error.
        ImmutableMap<Class<? extends ModelElement>, Integer> returnMap = ImmutableMap.copyOf(map);
        return returnMap;
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
     * Liefert true, wenn die übergebenen Klasse wenigstens eine Kante beseitzt, bei der die minimale Kardinalität zu der
     * verbundenen Klasse > 0 ist. Die Existenz des übergebenen Elementes ist also abhängig von dem anderen Element.
     * Das tifft automatisch bei allen {@link CompositionEdge}s zu, bei denen das übergebene Element der Slave ist, aber kann
     * auch bei allen anderen Kanten zutreffen.
     *
     * @param elementClass
     * @param ignoreCompositions wenn true, werden Kanten nicht beachtet, bei denen die übergebene Elementklasse Master einer Composition ist
     * @return
     */
    public static boolean isExistenceDependent(final Class<? extends ModelElement> elementClass, final boolean ignoreCompositions) {
        for (Class<? extends Edge> edgeClass : ModelConstants.getEdgeTypes(elementClass)) {
            if (!ignoreCompositions || ignoreCompositions && !isComposition(edgeClass)) {
                if (Edge.getMinCardinality(elementClass, edgeClass) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Liefert für die übergebene Elementklasse alle Elementtypen, die ihr untergeordnet sind und die nicht unendlich oft
     * an ihr hängen dürfen. Diese müssen beim Join ebenfalls zusammengeführt werden. Z.B. darf ein Rechanwendungsbaustein
     * laut Metamodell nur ein Datenbanksystem besitzen. Werden zwei Rechanwendungsbausteine mit jeweils einem Datenbanksystem
     * gejoined, dann müssen auch die Datenbanksysteme gejoined werden.
     *
     * @param elementClass
     * @return
     */
    public static final Set<Class<? extends ModelElement>> getSubordinatedJoinbleTypes(final Class<? extends ModelElement> elementClass) {
        ImmutableSet.Builder<Class<? extends ModelElement>> subordinatedJoinbleTypes = ImmutableSet.<Class<? extends ModelElement>> builder();
        Class<? extends CompositionEdge>[] compositionEdgeTypes = getCompositionEdgeTypes(elementClass, true);
        for (Class<? extends CompositionEdge> compositionEdgeType : compositionEdgeTypes) {
            if (getMaxMasterToSlaveCardinality(compositionEdgeType) < UNLIMITED) {
                Class<? extends ModelElement> slaveType = CompositionEdge.getSlaveType(compositionEdgeType);
                Class<? extends ModelElement>[] instanciableAssignableClasses = getInstanciableAssignableClasses(slaveType);
                for (Class<? extends ModelElement> instanciableAssignableClass : instanciableAssignableClasses) {
                    subordinatedJoinbleTypes.add(instanciableAssignableClass);
                }
            }
        }
        return subordinatedJoinbleTypes.build();
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
                if (getMinMasterToSlaveCardinality(compositionEdgeType) > ZERO) {
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
     * Liefert die {@link PathsDefinition} des Metamodells
     *
     * @return
     */
    public static final PathsDefinition getPathsDefinition() {
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

}