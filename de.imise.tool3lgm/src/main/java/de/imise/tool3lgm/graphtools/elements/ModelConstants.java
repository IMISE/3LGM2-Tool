package de.imise.tool3lgm.graphtools.elements;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.util.collections.CollectionUtils;

/**
 * @author N.N., AXS
 */
public final class ModelConstants {

    private static MetaModel metaModel = new TLGMOriginalMetaModel();

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Knoten- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
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
    public static final Class<? extends Kante>[] EMPTY_EDGE_CLASS_ARRAY = new Class[0];
    @SuppressWarnings("unchecked")
    public static final Class<? extends Composition>[] EMPTY_COMPOSITION_CLASS_ARRAY = new Class[0];

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

    public static final boolean isInterLayer(final int layerIndex) {
        return layerIndex % 2 == 1;
    }

    /** Short-Name für den beginn des HashStrings bei allen Kanten */
    public static final String EDGE_SHORT_NAME = "DLK";

    /**
     * Short-Name der zurückgegeben wird, wenn die an <code>getShortName(Class)</code> übergebene Klasse weder eine gültige Knoten noch Kantenklasse
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
    public static final ArrayList<ElementPropertyDialog> dialogs = new ArrayList<ElementPropertyDialog>();

    /**
     * Unendlich als maximaler Integer
     */
    public static final Integer UNLIMITED = new Integer(Integer.MAX_VALUE);

    /**
     * Null als Integer
     */
    public static final Integer ZERO = new Integer(0);

    /**
     * Eins als Integer
     */
    public static final Integer ONE = new Integer(1);

    /**
     * Standardrückgabewert bei Fehlern = -1 ;
     */
    public static final int STANDARD_ERROR_INT_VALUE = new Integer(-1);

    /** Alle Knoten der FE als Array */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_DOMAIN_LAYER_NODES = metaModel.getAllDomainLayerNodes();

    /** Alle im Baum sichtbaren Knoten */
    @SuppressWarnings("rawtypes")
    public static final Class[] TREE_DOMAIN_LAYER_NODES = metaModel.getTreeDomainLayerNodes();

    /** Alle Knoten der FE als HashSet */
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends ModelElement>> ALL_DOMAIN_LAYER_NODES_SET = new HashSet(Arrays.asList(ALL_DOMAIN_LAYER_NODES));

    /** Alle im Baum auf der FE anlegbaren Knoten */
    @SuppressWarnings("rawtypes")
    public static final Class[] TREE_CREATABLE_DOMAIN_LAYER_NODES = metaModel.getTreeCreatableDomainLayerNodes();

    /** Alle Knoten zw. FE und LWE als Array */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES = metaModel.getAllInterDomainLogicalLayerNodes();

    /** Alle Knoten zw. FE und LWE als HashSet */
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends ModelElement>> ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES_SET = new HashSet(Arrays.asList(ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES));

    /** Alle Knoten der LWE als Array */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_LOGICAL_LAYER_NODES = metaModel.getAllLogicalLayerNodes();

    /** Alle Knoten der LWE als HashSet */
    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    public static final Set<Class<? extends ModelElement>> ALL_LOGICAL_LAYER_NODES_SET = new HashSet(Arrays.asList(ALL_LOGICAL_LAYER_NODES));

    @SuppressWarnings("rawtypes")
    public static final Class[] TREE_LOGICAL_LAYER_NODES = metaModel.getTreeLogicalLayerNodes();

    @SuppressWarnings("rawtypes")
    public static final Class[] TREE_CREATABLE_LOGICAL_LAYER_NODES = metaModel.getTreeCreatableLogicalLayerNodes();

    /** Alle Knoten zw. LWE und PWE als Array */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES = metaModel.getAllInterLogicalPhysicalLayerNodes();

    /** Alle Knoten zw. LWE und PWE als HashSet */
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends ModelElement>> ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES_SET = new HashSet(Arrays.asList(ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES));

    /** Alle Knoten der PWE als Array */
    @SuppressWarnings("rawtypes")
    public static final Class[] ALL_PHYSICAL_LAYER_NODES = metaModel.getAllPhysicalLayerNodes();

    /** Alle Knoten der PWE als HashSet */
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends ModelElement>> ALL_PHYSICAL_LAYER_NODES_SET = new HashSet(Arrays.asList(ALL_PHYSICAL_LAYER_NODES));

    /** Alle Knoten der PWE im Baum als Array */
    @SuppressWarnings("rawtypes")
    public static final Class[] TREE_PHYSICAL_LAYER_NODES = metaModel.getTreePhysicalLayerNodes();

    @SuppressWarnings("rawtypes")
    public static final Class[] TREE_CREATABLE_PHYSICAL_LAYER_NODES = metaModel.getTreeCreatablePhysicalLayerNodes();

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

    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends Kante>> ALL_DOMAIN_LAYER_EDGES_SET = new HashSet(Arrays.asList(ALL_DOMAIN_LAYER_EDGES));
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends Kante>> ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES_SET = new HashSet(Arrays.asList(ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES));
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends Kante>> ALL_LOGICAL_LAYER_EDGES_SET = new HashSet(Arrays.asList(ALL_LOGICAL_LAYER_EDGES));
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends Kante>> ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES_SET = new HashSet(Arrays.asList(ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES));
    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    public static final Set<Class<? extends Kante>> ALL_PHYSICAL_LAYER_EDGES_SET = new HashSet(Arrays.asList(ALL_PHYSICAL_LAYER_EDGES));

    /** Set aller Kantenklassen */
    public static final Set<Class<? extends Kante>> ALL_EDGES_SET = ImmutableSet.<Class<? extends Kante>> builder().addAll(ALL_DOMAIN_LAYER_EDGES_SET).addAll(ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES_SET).addAll(ALL_LOGICAL_LAYER_EDGES_SET)
            .addAll(ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES_SET).addAll(ALL_PHYSICAL_LAYER_EDGES_SET).build();

    /** Array aller Kantenklassen */
    @SuppressWarnings("unchecked")
    public static final Class<? extends Kante>[] ALL_EDGES = new Class[ALL_EDGES_SET.size()];
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

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    @SuppressWarnings({
            "rawtypes"
    })
    public static final Class[] IMPORTABLE_NODES = metaModel.getImportableNodes();

    /** Alle Knotenklassen, die in jedem Teilmodell vorkommen, also nicht in jedem Teilmodell einen eigenen Container besitzen. */
    public static final Set<Class<? extends Knoten>> UNIQUE_NODES = metaModel.getUniqueNodes();

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

    /**
     * Mappt von Elementklassen auf alle Kantenklasse, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben.
     */
    private static final Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> ELEMENT_CLASS_TO_ORDERED_EDGES = metaModel.getElementClassToOrderedEdges();

    /**
     * Liefert ein Set aller Kantenklassen, die für die übergebene Elementklasse "geordnet sind", d. h. dass für Elemente der übergebenen Klasse die
     * Reihenfolge der Instanzen der zurück gelieferten Kantenklassen in ihrem Kantenvektor eine Bedeutung hat
     * (z. B. Reihenfolge von Aufgaben in einem Prozess -> Verbindung zwischen Prozessen und Aufgaben sind für den Prozess geordnet).
     *
     * @param elementClass
     */
    public static final Set<Class<? extends Kante>> getOrderedEdgeClasses(final Class<? extends ModelElement> elementClass) {
        return ELEMENT_CLASS_TO_ORDERED_EDGES.get(elementClass);
    }

    /**
     * Prüft, ob für die übergebene Elementklasse Reihenfolge der Kanten der übergebenen Kantenklasse relevant ist.
     *
     * @param elementClass Elementklasse, für die Kanten der edgeClass in einer bestimmten Reihenfolge sein müssen
     * @param edgeClass Kantenklasse, die für Elemente der elementClass in der richtigen Reihenfolge sein müssen
     * @return
     */
    public static final boolean isOrderedEdgeClass(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        return getOrderedEdgeClasses(elementClass).contains(edgeClass);
    }

    /**
     * Set aller Kanten, bei denen dieselben 2 Elemente merhfach über dieselbe Kantenart miteinander verbunden sein können.
     */
    private static final Set<Class<? extends Kante>> MULTIPLE_EDGE_CLASSES = metaModel.getMultipleEdgeClasses();

    /**
     * Liefert <code>true</code>, wenn über Kanten der übergebenen Kantenklasse dieselben 2 Elemente merhfach verbunden sein können.
     *
     * @param edgeClass
     * @return
     */
    public static final boolean isMultipleEdgeClass(final Class<? extends Kante> edgeClass) {
        return MULTIPLE_EDGE_CLASSES.contains(edgeClass);
    }

    /**
     * Liste aller Kantenklassen, die eigentlich 2 gerichtete Assoziationen im Metamodell sein müssten, aber aus Unwissenheit beim Entwurf des
     * Metamodells fehlerhafterweise in eine Assoziation verpackt wurden, bei denen die Richtung der Kante
     * (Doppelkante.FORWARD, Doppelkante.BACKWARD, Doppelkante.DOUBLE) die Bedeutung angibt. Nur wegen den 4 braucht man den ganzen
     * Doppelkanten-Richtungsquatsch. Wenn sie grafisch dargestellt werden, dann werden sie als eine Kante dargestellt werden, die
     * je nach Bedeutung eine der Richtungen oder beide als Pfeile darstellt. Hier wurde also das Model misbraucht, um im View diese Assoziationen
     * zusammenzufassen.
     */
    private static final Set<Class<? extends Kante>> DOUBLE_MEANING_EDGE_CLASSES = metaModel.getDoubleMeaningEdgeClasses();

    /**
     * Prüft, ob die übergebene Klasse eine Kantenklasse mit mehreren Bedeutungen ist, also die Richtung der Kante die Bedeutung angibt.
     *
     * @see #DOUBLE_MEANING_EDGE_CLASSES
     * @param edgeClass
     * @return
     */
    public static final boolean isDoubleMeaningEdge(final Class<?> edgeClass) {
        return DOUBLE_MEANING_EDGE_CLASSES.contains(edgeClass);
    }

    /**
     * Liste aller Kantenklassen, die nur in Vorwärtsrichtung verbunden werden und somit immer nur in dieser Richtung in
     * der Grafik dargestelt werden.
     */
    private static final Set<Class<? extends Kante>> FORWARD_CONNECTED_EDGE_CLASSES = ImmutableSet.of();

    /**
     * Prüft, ob die übergebene Klasse eine Kantenklasse ist, die immer nur in Vorwärtsrichtung verbunden werden kann
     * und somit auch in der Grafik nur in dieser Richtung dargestellt wird.
     *
     * @see #FORWARD_CONNECTED_EDGE_CLASSES
     * @param edgeClass
     * @return
     */
    public static final boolean isForwardConnectedEdge(final Class<?> edgeClass) {
        return FORWARD_CONNECTED_EDGE_CLASSES.contains(edgeClass);
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    /**
     * Mappt von einer Elementklasse auf das Array aller instanziierbaren und zu dieser Klasse zuweisungskompatiblen ModelElement-Klassen.
     */
    public static final HashMap<Class<? extends ModelElement>, Class<? extends ModelElement>[]> ELEMENT_CLASS_TO_NON_ABSTRACT_ASSIGNABLE_ELEMENT_CLASSES = new HashMap<Class<? extends ModelElement>, Class<? extends ModelElement>[]>();

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
        HashSet<Class<? extends ModelElement>> al = new HashSet<Class<? extends ModelElement>>();
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
    private static final HashMap<Class<? extends ModelElement>, Class<? extends Kante>[]> ELEMENT_CLASS_TO_EDGE_CLASSES = new HashMap<Class<? extends ModelElement>, Class<? extends Kante>[]>();

    /**
     * Liefert für eine Elementklasse alle Kantenklassen dieser Klasse zu anderen Elementklassen
     *
     * @param elementClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Kante>[] getEdgeTypes(final Class<? extends ModelElement> elementClass) {
        Class<? extends Kante>[] edgeClasses = ELEMENT_CLASS_TO_EDGE_CLASSES.get(elementClass);
        if (edgeClasses != null) {
            return edgeClasses;
        }
        ArrayList<Class<? extends Kante>> elementClassEdgeClasses = new ArrayList<Class<? extends Kante>>();
        for (Class<? extends Kante> edgeClass : ALL_EDGES) {
            if (Kante.isStartOrEndClass(edgeClass, elementClass)) {
                elementClassEdgeClasses.add(edgeClass);
            }
        }
        int size = elementClassEdgeClasses.size();
        Class<? extends Kante>[] returnClasses = null;
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
    public static final boolean hasEdgeType(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        return CollectionUtils.arrayContains(getEdgeTypes(elementClass), edgeClass);
    }

    /**
     * Mappt für eine Elementklasse auf eine weitere Map, die von einer Elementklasse auf ein Array von Kantenklassen mappt. Das Array der
     * Kantenklassen enthält alle Kanten, die zwischen den beiden Schlüsselelementklassen vorhanden sein können.<br />
     * In der äußeren und allen inneren HashMaps sind immer dieselben Schlüsselelemente enthalten. Für ein Paar von Schlüsselelementklassen ist immer
     * dasselbe Kanteklassen-Array abgelegt - egal in welcher Reihenfolge man die Elementeklassen als Schlüssel
     * einsetzt.
     */
    private static final HashMap<Class<? extends ModelElement>, HashMap<Class<? extends ModelElement>, Class<? extends Kante>[]>> ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES = new HashMap<Class<? extends ModelElement>, HashMap<Class<? extends ModelElement>, Class<? extends Kante>[]>>();

    /**
     * Liefert ein Array aller Kantenklassen, die zwischen den beiden übergebenen Elementklassen existieren können. Gibt es keine Kantenklasse
     * zwischen den Elementen so kommt ein leeres Array (length==0) zurück.
     *
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final Class<? extends Kante>[] getEdgeTypes(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        HashMap<Class<? extends ModelElement>, Class<? extends Kante>[]> elementClassToEdgeClass = ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES.get(elementClass1);
        if (elementClassToEdgeClass != null) {
            Class<? extends Kante>[] edgeClasses = elementClassToEdgeClass.get(elementClass2);
            if (edgeClasses != null) {
                return edgeClasses;
            }
        } else {
            elementClassToEdgeClass = new HashMap<Class<? extends ModelElement>, Class<? extends Kante>[]>();
            ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES.put(elementClass1, elementClassToEdgeClass);
        }
        ArrayList<Class<? extends Kante>> resultEdgeClasses = new ArrayList<Class<? extends Kante>>();
        for (Class<? extends Kante> edgeClass : getEdgeTypes(elementClass1)) {
            if (Kante.isConnecting(edgeClass, elementClass1, elementClass2)) {
                resultEdgeClasses.add(edgeClass);
            }
        }
        Class<? extends Kante>[] returnClasses = null;
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
            elementClassToEdgeClass = new HashMap<Class<? extends ModelElement>, Class<? extends Kante>[]>();
            ELEMENT_CLASS_TO_MAP_FROM_ELEMENT_CLASS_TO_EDGE_CLASSES.put(elementClass2, elementClassToEdgeClass);
        }
        elementClassToEdgeClass.put(elementClass1, returnClasses);
        return returnClasses;
    }

    //	static {
    //		HashSet<Class<? extends ModelElement>> allElements= new HashSet<Class<? extends ModelElement>>(ALL_NODES_SET.size() + ALL_EDGES_SET.size());
    //		allElements.addAll(ALL_NODES_SET);
    //		allElements.addAll(ALL_EDGES_SET);
    //		for (Class<? extends ModelElement> elementClass1 : allElements) {
    //			for (Class<? extends ModelElement> elementClass2 : allElements) {
    //				Class<? extends Kante>[] et = getEdgeTypes(elementClass1, elementClass2);
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
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Knoten} oder {@link NodeContainer} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse ein Knotentyps ist, sonst <code>false</code>.
     */
    public static final boolean isNodeType(final Class<?> elementClass) {
        return Knoten.class.isAssignableFrom(elementClass) || NodeContainer.class.isAssignableFrom(elementClass);
    }

    /**
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Knoten} oder {@link NodeContainer} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse ein Knotentyps ist, sonst <code>false</code>.
     */
    public static final boolean isBendpointType(final Class<?> elementClass) {
        return Knickpunkt.class.isAssignableFrom(elementClass) || BendpointContainer.class.isAssignableFrom(elementClass);
    }

    /**
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Knoten} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse ein Knotentyps ist, sonst <code>false</code>.
     */
    public static final boolean isRealNodeType(final Class<?> elementClass) {
        return isNodeType(elementClass) && !isBendpointType(elementClass);
    }

    /**
     * Geprüft wird, ob sich die übergebene Klasse eine Unterklasse von {@link Kante} ist.
     *
     * @param elementClass
     * @return <code>true</code>, wenn die übergebene Klasse eine Assoziation ist, sonst <code>false</code>.
     */
    public static final boolean isEdgeType(final Class<?> elementClass) {
        return Kante.class.isAssignableFrom(elementClass) || EdgeContainer.class.isAssignableFrom(elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse selbst Assoziationen zu anderen Elementen haben kann - also eine Assoziationsklasse
     * ist.
     *
     * @param elementClass
     * @return
     */
    public static final boolean isAssociationClass(final Class<?> elementClass) {
        if (!Kante.class.isAssignableFrom(elementClass)) {
            return false;
        }
        Class<? extends Kante>[] edgeTypes = getEdgeTypes(elementClass.asSubclass(ModelElement.class));
        return edgeTypes != null && edgeTypes.length != 0;
    }

    /**
     * Array aller Pfade, die in der grafischen Ansicht als Interebenenbeziehungen dargestellt werden.
     */
    @SuppressWarnings("unchecked")
    public static final MetaPath[] INTER_LAYER_CONNECTED_ELEMENT_PATHES = metaModel.getInterLayerConnectedElementPathes();

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse Startklasse eines Interebenenmetapfades ist.
     *
     * @param elementClass
     * @return
     */
    public final static boolean isInterLayerStartClass(final Class<? extends ModelElement> elementClass) {
        for (MetaPath mp : INTER_LAYER_CONNECTED_ELEMENT_PATHES) {
            if (mp.getStartClass().isAssignableFrom(elementClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt alle Startklassen zurück, die über eine Interebenenbeziehung der spezifizierten Endklasse verfügen.
     *
     * @param endClass Endklasse der Interebenenbeziehung
     * @return
     */
    public final static Set<Class<? extends ModelElement>> getInterLayerStartClasses(final Class<? extends ModelElement> endClass) {
        Set<Class<? extends ModelElement>> startClasses = new HashSet<Class<? extends ModelElement>>();
        for (MetaPath path : INTER_LAYER_CONNECTED_ELEMENT_PATHES) {
            if (path.getEndClass().isAssignableFrom(endClass)) {
                startClasses.add(path.getStartClass());
            }
        }
        return startClasses;
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
    private static final Map<String, Class<? extends ModelElement>> CLASS_NAME_TO_CLASS_MAP = new HashMap<String, Class<? extends ModelElement>>();

    /** Alle Modellelementklassen, die instaziierbar sind und in jedem Metamodell automatisch enthalten sind */
    private static final Set<String> META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES = ImmutableSet.of(Knickpunkt.class.getSimpleName(), TextfeldFach.class.getSimpleName(), TextfeldLog.class.getSimpleName(), TextfeldPhy.class.getSimpleName());

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
            if (META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES.contains(classname)) {
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
        while (clazz != ModelElement.class) {
            try {
                String resKey = clazz.getSimpleName();
                if (plural) {
                    resKey += PLURAL_NAME_RES_KEY_SUFFIX;
                }
                return Tool3lgmConstants.getResString(resKey);
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
    public static String getForwardMetaAssociationName(final Class<? extends Kante> edgeClass) {
        return getForwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Vorwärtsrichtung ohne die Elementartnamen, die die Kante verbindet.
     *
     * @param edgeClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getFullForwardMetaAssociationName(final Class<? extends Kante> edgeClass) {
        return getForwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Kante> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getForwardMetaAssociationName(edgeClass, Doppelkante.DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getForwardMetaAssociationName(final Class<? extends Kante> edgeClass, final int connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getMetaAssociationName(edgeClass, false, connectionState, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Kante> edgeClass) {
        return getBackwardMetaAssociationName(edgeClass, false, false);
    }

    /**
     * Liefert den Meta-Namen der Kanteklasse für die Rückwärtsrichtung mit den Elementartnamen, die die Kante verbindet.
     *
     * @param edgeClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getFullBackwardMetaAssociationName(final Class<? extends Kante> edgeClass) {
        return getBackwardMetaAssociationName(edgeClass, true, true);
    }

    /**
     * @param edgeClass
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Kante> edgeClass, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getBackwardMetaAssociationName(edgeClass, Doppelkante.DOUBLE, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getBackwardMetaAssociationName(final Class<? extends Kante> edgeClass, final int connectionState, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        return getMetaAssociationName(edgeClass, true, connectionState, appendPrefixClass, appendPostfixClass);
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param direction
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getFullMetaAssociationName(final Class<? extends Kante> edgeClass, final boolean switchDefinedDirection, final int direction) {
        return getMetaAssociationName(edgeClass, switchDefinedDirection, direction, true, true);
    }

    /**
     * Liefert in Abhängigkeit von der Richtung den Meta-Namen der Kanteklasse
     *
     * @param edgeClass
     * @param switchDefinedDirection gibt an, ob die Bedeutung der Kante von der Startklasse zur Endklasse (<code>false</code>) oder von der Endklasse
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
    public static String getMetaAssociationName(final Class<? extends Kante> edgeClass, final boolean switchDefinedDirection, final int connectionState) {
        StringBuilder sb = new StringBuilder();
        if (!switchDefinedDirection) {
            try {
                sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_f"));
            } catch (Exception e) {
                if (connectionState == Doppelkante.FORWARD) {
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_f_f"));
                } else if (connectionState == Doppelkante.BACKWARD) {
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_f_b"));
                } else {
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_f_f"));
                    sb.append(" / ");
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_f_b"));
                }
            }
        } else {
            try {
                sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_b"));
            } catch (Exception e) {
                if (connectionState == Doppelkante.FORWARD) {
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_b_f"));
                } else if (connectionState == Doppelkante.BACKWARD) {
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_b_b"));
                } else {
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_b_f"));
                    sb.append(" / ");
                    sb.append(Tool3lgmConstants.getResString(edgeClass.getSimpleName() + "_b_b"));
                }
            }
        }
        return sb.toString();
    }

    /**
     * @param edgeClass
     * @param switchDefinedDirection
     * @param direction
     * @param appendPrefixClass
     * @param appendPostfixClass
     * @return
     * @see #getMetaAssociationName(Class, boolean, int)
     */
    public static String getMetaAssociationName(final Class<? extends Kante> edgeClass, final boolean switchDefinedDirection, final int direction, final boolean appendPrefixClass, final boolean appendPostfixClass) {
        if (!appendPrefixClass && !appendPostfixClass) {
            return getMetaAssociationName(edgeClass, switchDefinedDirection, direction);
        }
        StringBuilder sb = new StringBuilder();
        if (appendPrefixClass) {
            sb.append(getDisplayableName(!switchDefinedDirection ? Kante.getStartClass(edgeClass) : Kante.getEndClass(edgeClass)));
            sb.append(" ");
        }
        sb.append(getMetaAssociationName(edgeClass, switchDefinedDirection, direction));
        if (appendPostfixClass) {
            sb.append(" ");
            sb.append(getDisplayableName(!switchDefinedDirection ? Kante.getEndClass(edgeClass) : Kante.getStartClass(edgeClass)));
        }
        return sb.toString();
    }

    /**
     * Mappt von einer Elementart auf die Klassen der {@link PartOfBeziehung}en, über die der Elementart Teilemente untergeordnet werden kann.
     */
    private static final Map<Class<? extends ModelElement>, Class<? extends PartOfBeziehung>[]> ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES = new HashMap<Class<? extends ModelElement>, Class<? extends PartOfBeziehung>[]>(5);
    /**
     * Mappt von einer Elementart auf die Klassen der {@link PartOfBeziehung}en, über die die Elementart als Teilement untergeordnet werden kann.
     */
    private static final Map<Class<? extends ModelElement>, Class<? extends PartOfBeziehung>[]> ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES = new HashMap<Class<? extends ModelElement>, Class<? extends PartOfBeziehung>[]>(5);
    //die Funktion mit dem komischen Namen ist nur dazu da, dass die @SuppressWarnings("unchecked") nicht über die
    //gesamt Datei geschrieben werden muss (wenn man den Funktionsinhalt einfach in einen static-Block schreibt,
    //kann man die Warnungen nur für die ganze Datei unterdrücken
    static {
        fill_ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES_and_ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES();
    }

    @SuppressWarnings("unchecked")
    private static final void fill_ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES_and_ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES() {
        for (int i = 0; i < ALL_NODES.length; i++) {
            //Hole alle Kantenklassen der Zielklasse und suche alle PartOfBeziehungen
            for (Class<? extends Kante> c : getEdgeTypes(ALL_NODES[i])) {
                if (PartOfBeziehung.class.isAssignableFrom(c)) {
                    Class<? extends PartOfBeziehung>[] edgeClasses = null;
                    Class<? extends PartOfBeziehung> poClass = c.asSubclass(PartOfBeziehung.class);
                    if (PartOfBeziehung.isParentClass(poClass, ALL_NODES[i])) {
                        edgeClasses = ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.get(ALL_NODES[i]);
                        if (edgeClasses == null) {
                            edgeClasses = new Class[1];
                            edgeClasses[0] = poClass;
                            ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.put(ALL_NODES[i], edgeClasses);
                        } else {
                            Class<? extends PartOfBeziehung>[] newEdgeClasses = new Class[edgeClasses.length + 1];
                            System.arraycopy(edgeClasses, 0, newEdgeClasses, 0, edgeClasses.length);
                            newEdgeClasses[edgeClasses.length] = poClass;
                            ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.put(ALL_NODES[i], newEdgeClasses);
                        }
                    }
                    if (PartOfBeziehung.isPartClass(poClass, ALL_NODES[i])) {
                        edgeClasses = ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.get(ALL_NODES[i]);
                        if (edgeClasses == null) {
                            edgeClasses = new Class[1];
                            edgeClasses[0] = poClass;
                            ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.put(ALL_NODES[i], edgeClasses);
                        } else {
                            Class<? extends PartOfBeziehung>[] newEdgeClasses = new Class[edgeClasses.length + 1];
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

    /**
     * Liefert die Klassen von <code>PartOfBeziehung</code>, über die der übergebenen Elementart andere Elemente als Teile untergeordnet werden
     * können.
     *
     * @return Leeres Array, wenn es keine {@link PartOfBeziehung} gibt, aosnsten ein Array aller dieser Kantenklassen
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends PartOfBeziehung>[] getHasPartsEdgeClasses(final Class<? extends ModelElement> elementClass) {
        Class<? extends PartOfBeziehung>[] hasPartEdgeClasses = ELEMENT_CLASS_TO_HAS_PART_EDGE_CLASSES.get(elementClass);
        return hasPartEdgeClasses == null ? new Class[0] : hasPartEdgeClasses;
    }

    /**
     * Liefert die Klassen von <code>PartOfBeziehung</code>, über die die übergebenen Elementart anderen Elementen als Teilelement untergeordnet
     * werden kann.
     *
     * @return Leeres Array, wenn es keine {@link PartOfBeziehung} gibt, aosnsten ein Array aller dieser Kantenklassen
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends PartOfBeziehung>[] getIsPartOfEdgeClasses(final Class<? extends ModelElement> elementClass) {
        Class<? extends PartOfBeziehung>[] isPartOfEdgeClasses = ELEMENT_CLASS_TO_PART_OF_EDGE_CLASSES.get(elementClass);
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
            elementClassToHashShortName = new HashMap<Class<? extends ModelElement>, String>();
            //Set in das alle bisher gefundenen ShortNames eingetragen werden, um zu prüfen, ob ein shortName bereits existiert
            HashSet<String> allShortNames = new HashSet<String>();
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

        //Knoten
        if (Knoten.class.isAssignableFrom(elementClass)) {
            Object o = elementClassToHashShortName.get(elementClass);
            //ist null bei Layerknoten. Die brauchen aber auch keinen lesbaren Hash
            if (o == null) {
                return NO_MODEL_ELEMENT_SHORT_NAME;
            }
            return elementClassToHashShortName.get(elementClass).toString();
            //Kanten
        } else if (Kante.class.isAssignableFrom(elementClass)) {
            return EDGE_SHORT_NAME;
        }
        return NO_MODEL_ELEMENT_SHORT_NAME;
    }

    /**
     * gibt die Ebene eine Objekttypes zurueck
     *
     * @param type Typkonstante, die den Objekttypen spezifiziert
     * @return int Ebene
     */
    public static final int layerFor(final Class<? extends ModelElement> elementClass) {
        if (Knoten.class.isAssignableFrom(elementClass)) {
            if (ALL_DOMAIN_LAYER_NODES_SET.contains(elementClass)) {
                return DOMAIN_LAYER;
            }
            if (ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES_SET.contains(elementClass)) {
                return INTER_DOMAIN_LOGICAL_LAYER;
            }
            if (ALL_LOGICAL_LAYER_NODES_SET.contains(elementClass)) {
                return LOGICAL_LAYER;
            }
            if (ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES_SET.contains(elementClass)) {
                return INTER_LOGICAL_PHYSICAL_LAYER;
            }
            if (ALL_PHYSICAL_LAYER_NODES_SET.contains(elementClass)) {
                return PHYSICAL_LAYER;
            }
        } else {
            if (ALL_DOMAIN_LAYER_EDGES_SET.contains(elementClass)) {
                return DOMAIN_LAYER;
            }
            if (ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES_SET.contains(elementClass)) {
                return INTER_DOMAIN_LOGICAL_LAYER;
            }
            if (ALL_LOGICAL_LAYER_EDGES_SET.contains(elementClass)) {
                return LOGICAL_LAYER;
            }
            if (ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES_SET.contains(elementClass)) {
                return INTER_LOGICAL_PHYSICAL_LAYER;
            }
            if (ALL_PHYSICAL_LAYER_EDGES_SET.contains(elementClass)) {
                return PHYSICAL_LAYER;
            }

        }
        if (elementClass == TextfeldFach.class) {
            return DOMAIN_LAYER;
        }
        if (elementClass == TextfeldLog.class) {
            return LOGICAL_LAYER;
        }
        if (elementClass == TextfeldPhy.class) {
            return PHYSICAL_LAYER;
        }
        return NO_LAYER;
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

    public static final boolean isComposition(final Class<? extends Kante> edgeClass) {
        return Composition.class.isAssignableFrom(edgeClass);
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
    private static Class<? extends Composition>[] getCompositionEdgeTypes(final Class<? extends ModelElement> elementClass, final boolean isMaster) {
        Class<? extends Kante>[] elementClassEdges = getEdgeTypes(elementClass);
        ArrayList<Class<? extends Kante>> subEdgeTypes = new ArrayList<Class<? extends Kante>>(elementClassEdges.length);
        for (Class<? extends Kante> edgeClass : elementClassEdges) {
            if (isComposition(edgeClass)) {
                if (isMaster) {
                    if (Kante.isStartClass(edgeClass, elementClass)) {
                        subEdgeTypes.add(edgeClass);
                    }
                } else {
                    if (Kante.isEndClass(edgeClass, elementClass)) {
                        subEdgeTypes.add(edgeClass);
                    }
                }
            }
        }
        int size = subEdgeTypes.size();
        if (size == 0) {
            return EMPTY_COMPOSITION_CLASS_ARRAY;
        }
        Class<? extends Composition>[] returnClasses = new Class[size];
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
    public static Class<? extends Composition>[] getCompositionEdgeTypesForMaster(final Class<? extends ModelElement> elementClass) {
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
    public static Class<? extends Composition>[] getCompositionEdgeTypesForSlave(final Class<? extends ModelElement> elementClass) {
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
        Class<? extends Composition>[] compositions = getCompositionEdgeTypesForMaster(masterElementClass);
        if (compositions.length == 0) {
            return EMPTY_ELEMENT_CLASS_ARRAY;
        }
        ArrayList<Class<? extends ModelElement>> slaveElementClasses = new ArrayList<Class<? extends ModelElement>>(compositions.length);
        for (Class<? extends Composition> compClass : compositions) {
            Class<? extends ModelElement> slaveType = Composition.getSlaveType(compClass);
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
        for (Class<? extends Kante> edgeClass : getEdgeTypes(elementClass)) {
            if (isComposition(edgeClass) && Kante.isEndClass(edgeClass, elementClass)) {
                return true;
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
        Class<? extends Composition>[] compositionEdgeTypes = getCompositionEdgeTypes(elementClass, true);
        for (Class<? extends Composition> compositionEdgeType : compositionEdgeTypes) {
            if (Composition.getMaxMasterToSlaveCardinality(compositionEdgeType) < UNLIMITED) {
                Class<? extends ModelElement> slaveType = Composition.getSlaveType(compositionEdgeType);
                Class<? extends ModelElement>[] instanciableAssignableClasses = getInstanciableAssignableClasses(slaveType);
                for (Class<? extends ModelElement> instanciableAssignableClass : instanciableAssignableClasses) {
                    subordinatedJoinbleTypes.add(instanciableAssignableClass);
                }
            }
        }
        return subordinatedJoinbleTypes.build();
    }

    /** Cache für die Funktion {@link #getInitialSubtypes(Class)} */
    private static Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> INITIAL_SUBTYPES = Maps.newHashMap();

    /**
     * Liefert für eine Elementklasse alle Elementklassen, die ihr untergeordnet sind (also über eine Komposition mit
     * ihr verbunden sind, bei der sie der Master ist) und die minimale Kardinlität der Unterklassen > 0 ist.
     *
     * @param elementClass
     */
    public static final Set<Class<? extends Kante>> getInitialSubtypes(final Class<? extends ModelElement> elementClass) {
        Set<Class<? extends Kante>> initialSubtypes = INITIAL_SUBTYPES.get(elementClass);
        if (initialSubtypes == null) {
            ImmutableSet.Builder<Class<? extends Kante>> initialSubtypesBuilder = ImmutableSet.<Class<? extends Kante>> builder();
            Class<? extends Composition>[] compositionEdgeTypes = getCompositionEdgeTypes(elementClass, true);
            for (Class<? extends Composition> compositionEdgeType : compositionEdgeTypes) {
                if (Composition.getMinMasterToSlaveCardinality(compositionEdgeType) > ZERO) {
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
     * dieser boolean muss in allen Knoten auf true gesetzt werden, die eine eigene toString() besitzen, welche aus anderen Modellelementen den Namen
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

}