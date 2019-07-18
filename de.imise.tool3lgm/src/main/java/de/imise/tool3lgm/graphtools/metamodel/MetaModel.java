package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.STANDARD_ERROR_INT_VALUE;

import java.lang.reflect.Constructor;
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.LayerNode;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElementInstanceCreator;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.SubordinationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.ReflectionUtils;
import de.imise.util.Sys;
import de.imise.util.collections.CollectionUtils;

/**
 * Dieses Objekt kapselt ein Metamodel. Diese Klasse hier nimmt eine {@link MetaModelDefinition} und füllt damit alle Listen, Sets, Arrays usw. um
 * dann alle Fragen über ein konkretes Metamodell zu beantworten.<br>
 * Das hier ist aus der ehemals statischen Klasse ModelConstants entstanden, als es nicht mehr nur ein MetaModel sondern mehrere gleichzeitig geben
 * sollte.
 *
 * @author AXS (30 Apr 2019)
 */
public final class MetaModel {

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

    /** Alle Modellelementklassen, die instanziierbar sind und in jedem Metamodell automatisch enthalten sind */
    private static final Set<Class<? extends ModelElement>> META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES = ImmutableSet.of(Bendpoint.class, Textfield.class);

    /** Der Context, der dieses MetaModell erzeugt hat und hält. Er wird nur gebraucht, um an die Resourcen zu kommen. */
    private final MetaModelContext metaModelContext;

    /**
     * Hilfsklasse zum Anlegen neuer Elemente, die sicher stellt, dass das richtige MetaModel für die Elemente gesetzt wird und dann nicht mehr von
     * außen geändert werden kann.
     */
    private final ModelElementInstanceCreator modelElementInstanceCreator;

    /** Handler für das einfache und nicht redundante Anlegen von Elementar-Metapfaden */
    private final ElementaryMetaPathHandler elementaryMetaPathHandler;

    ////////////
    // Knoten //
    ////////////

    /** Alle Node der FE als Set */
    public final Set<Class<? extends ModelElement>> allDomainLayerNodesSet;

    /** Alle Node zw. FE und LWE als Set */
    public final Set<Class<? extends ModelElement>> allInterDomainLogicalLayerNodesSet;

    /** Alle Node der LWE als Set */
    public final Set<Class<? extends ModelElement>> allLogicalLayerNodesSet;

    /** Alle Node zw. LWE und PWE als Set */
    public final Set<Class<? extends ModelElement>> allInterLogicalPhysicalLayerNodesSet;

    /** Alle Node der PWE als Set */
    public final Set<Class<? extends ModelElement>> allPhysicalLayerNodesSet;

    /** Set aller Knotenklassen */
    public final Set<Class<? extends ModelElement>> allNodesSet;

    ////////////
    // Kanten //
    ////////////

    /** Set aller Kantenklassen */
    public final Set<Class<? extends Edge>> allEdgesSet;

    /////////////////////////
    // alle Elementklassen //
    /////////////////////////

    /** Set aller Elementklassen */
    public final Set<Class<? extends ModelElement>> allElementsSet;

    /**
     * Sammlung, die alle Elementklassen inklusive aller Kantenklassen enthält einschließlich aller
     * ihrer Oberklassen bis hin zu ModelElement.class.
     */
    public final Set<Class<? extends ModelElement>> allModelElementClassesWithSuperClasses;

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    /**
     * Mappt von Elementklassen auf alle Kantenklasse, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben.
     */
    private final SetMultimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToSortedEdges;

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    private final Set<Class<? extends ModelElement>> importableNodes;

    /** Alle Knotenklassen, die in jedem Teilmodell vorkommen, also nicht in jedem Teilmodell einen eigenen Container besitzen. */
    public final Set<Class<? extends Node>> uniqueNodes;

    /** Alle Knotenklassen, bei denen in der Grafik zusätzlich zum eigenen Namen noch die Namen verbundener Elemente angezeigt werden sollen */
    public final Set<Class<? extends ModelElement>> elementClassesWithNameExtensions;

    /** Alle Elementklassen, die nur im ExpertMode ({@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE} = true) angelegt und verändert werden können. */
    private final Set<Class<? extends ModelElement>> onlyExpertModeEditableNodes;

    /**
     * Elementklassen, die nur im Baum angezeigt werden sollen, wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>true</code> gestellt ist.
     * ACHTUNG: hier wird nur mit contains(class) gerpüft -> immer auch die Oberklassen, die versteckt werden sollen reinschreiben
     */
    private final Set<Class<? extends ModelElement>> onlyExpertModeVisibleNodes;

    /** Alle Elementklassen, die ein Layout brauchen, weil die selbst oder an anderen Elementen in der Grafik dargstellt werden */
    private final Set<Class<? extends ModelElement>> elementClassesWithLayout;

    /**
     * Alle Elementklassen, die sortierte Kanten zu Elementen haben, die in der Grafik zu sehen sind (z.B. Prozesse können sortierte Kanten zu
     * Aufgaben haben)
     */
    private final Set<Class<? extends ModelElement>> elementClassesWithSortedEdgesToPaintable;

    /** Alle abstracten Klassen, die im Baum aus der FE auftauchen sollen */
    private final Class<? extends ModelElement>[] treeDomainLayerVisibleAbstractNodes;

    /** Alle abstracten Klassen, die im Baum aus der LWE auftauchen sollen */
    private final Class<? extends ModelElement>[] treeLogicalLayerVisibleAbstractNodes;

    /** Alle abstracten Klassen, die im Baum aus der LWE auftauchen sollen */
    private final Class<? extends ModelElement>[] treePhysicalLayerVisibleAbstractNodes;

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     */
    private final Map<String, String> oldToNewClassName;

    /**
     * Mappt von einer Elementklasse auf das Array aller instanziierbaren und zu dieser Klasse zuweisungskompatiblen ModelElement-Klassen.
     */
    public final Multimap<Class<? extends ModelElement>, Class<? extends ModelElement>> elementClassToNonAbstractAssignableElementClasses = HashMultimap.create();

    /**
     * Mappt von einer Elementklasse auf alle Kanten, die diese Elementklasse selbst besitzt oder von einer ihrer Oberklassen erbt.
     */
    private final Map<Class<? extends ModelElement>, Class<? extends Edge>[]> elementClassToEdgeClasses = new HashMap<>();

    /**
     * Mappt vomn 2 Elementklassen auf ein Array von Kantenklassen mappt. Das Array der Kantenklassen enthält alle Kanten, die zwischen den beiden
     * Schlüsselelementklassen vorhanden sein können.<br />
     * Für ein Paar von Schlüsselelementklassen ist immer dasselbe Kanteklassen-Array abgelegt - egal in welcher Reihenfolge man die Elementeklassen
     * als Schlüssel einsetzt.
     */
    private final Table<Class<? extends ModelElement>, Class<? extends ModelElement>, Class<? extends Edge>[]> elementClassesToEdgeClasses = HashBasedTable.create();

    /**
     * Mappt von einer Elementklasse auf alle Kantenklassen, die eine ab dieser Klasse nicht mehr für diese Elementart gelten sollen. Damit können
     * ererbte Kanten abgeschaltet werden. Z.B. wenn man eine Unterklasse einer bestehenden Metamodellklasse definiert, die aber nicht mehr wie die
     * Oberklasse in Teilelemente zerlegt werden könen soll, dann muss man hier die Unterklasse und die 'abzuschaltende' HatTeil-Kante angeben.
     * Es müssen alle konkreten Element-Klassen angegeben werden, für die eine konkrete Kantenklasse nicht gelten soll. D.h. die Klassen hier werden
     * auf Identität geprüft und nicht auf Unterklassen.
     * Die Richtung ist wichtig, weil man nur so ausdrücken kann, dass z.B. eine Element zwar Teil eines Oberelementes von einer Oberklasse sein kann,
     * aber selbst nicht mehr in Teile zerlegt werden darf. Das gilt auch für andere als HasPart-Kantenarten, die zwischen einer Elementart und einer
     * Unterklasse bestehen, bei der die Kante für die Unterklasse nicht mehr gelten soll.
     */
    private final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToRemovedEdgeClassesForStartClass;
    private final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToRemovedEdgeClassesForEndClass;

    /**
     * Mappt vom Klassennamen auf die Klasse. Es ist immer der SimpleName und der FullName der Klasse in der Map. Dies ist der Cache für die Funktion
     * {@link #getClassForName(String)}
     */
    private final Map<String, Class<? extends ModelElement>> elementClassNameToElementClass;

    /**
     * Mappt von einer Kantenklasse auf den MetaPfad, über den die verbindbaren Elemente ebenfalls bereits verbunden sein müssen.
     * Dieser Mechanismus ist dafür gedacht, verbindbare Elemente einzuschränken auf bestimmte Elemente.
     */
    private final Map<Class<? extends Edge>, SimpleMetaPath> edgeClassToConditionMetaPath;

    /**
     * Sammlung aller Pfade, die ausgehend vom Startelement dieser Kante ebenfalls angelegt werden sollen, wenn eine Instanziierung über diese
     * Kantenklasse durchgeführt wird. <br>
     * Jeder der Pfade muss zwingend bei derselben Klasse starten, bei der diese Kante startet.<br>
     * Der Pfad hat nur einen Effekt, wenn seine Startklasse zur Startklasse dieser Kante zuweisungskompatibel ist und er mind. eine
     * {@link InstanciationEdge} enthält. Der hiermit verbundene Mechanismus geht durch die Kantenklassen des Pfades. Ist die aktuelle
     * Kantenklasse keine {@link InstanciationEdge}, dann suche von den aktuellen Elementen ausgehend (am Anfang ist das das Startelement dieser
     * Kante) alle damit über diese Kantenart verbundenen Elemente und nimmt sie für den nächsten Schritt als Startelemente. Sobald im Pfad eine
     * {@link InstanciationEdge} auftaucht, werden alle Elementarten und Kanten der dahinter liegenden Pfadschritte kompeltt neu erzeugt und die
     * entstehenden Elemente immer mit den vorherigen verbunden. Wenn der Pfad mit einer Klasse endet (was er in den meisten Fällen tun wird, damit
     * das ganze sinnvoll ist), die zuweisungskompatibel zur Endklasse dieser Kante ist (also zum durch diese Kante neu erzeugten Element), dann wird
     * die letzte Verbindung bzw. die letzte Kante hin zum EndElementdieser Kante erzeugt und nicht nochmal ein Element der Endelementart angelegt.
     * Damit kann man "Nebenbedingungspfade" für das Startelement gleich mit anlegen, wenn man das Startelement über diese Kante hier intsanziiert.
     */
    private final Multimap<Class<? extends InstanciationEdge>, SimpleMetaPath> instanciationEdgeToAdditionalInstanciationMetaPaths;

    /**
     * Mappt von einer ElementKlasse auf eine Sammlung aller {@link SimpleMetaPath}, die man zwischen ihr und anderen Elementen anlegen kann, wobei
     * die Zwischenelemente ebenfalls neu angelegt werden. Diese Pfade werden im Kontextmenü bei Mehrfachselektion oder Einfachselektion angeboten.
     */
    private final Multimap<Class<? extends ModelElement>, SimpleMetaPath> elementClassToCreatableMetaPaths;

    /**
     * Mappt von Elementklassen, bei denen der Name verbundendener Elemente in der Grafik in Klammern unter der eigentlichen Elementart angezeigt
     * werden soll, auf den MetaPfad zu den anzuzeigenden, verbundenen Elementen.
     */
    private final Map<Class<? extends ModelElement>, AbstractMetaPath> elementClassToNameExtensionPath;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Die folgenden Arrays müssen hier unten initialisiert werden nachdem die Maps mit den Edges gefüllt sind, sonst InitialException //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Alle im Baum auf der FE sichtbaren Node */
    public final Iterable<Class<? extends ModelElement>> treeDomainLayerNodes;

    /** Alle im Baum auf der FE anlegbaren Node */
    public final Iterable<Class<? extends ModelElement>> creatableDomainLayerNodes;

    /** Alle im Baum auf der LWE sichtbaren Node */
    public final Iterable<Class<? extends ModelElement>> treeLogicalLayerNodes;

    /** Alle im Baum auf der FE anlegbaren Node */
    public final Iterable<Class<? extends ModelElement>> creatableLogicalLayerNodes;

    /** Alle im Baum auf der PWE sichtbaren Node */
    public final Iterable<Class<? extends ModelElement>> treePhysicalLayerNodes;

    /** Alle im Baum auf der PWE anlegbaren Node */
    public final Iterable<Class<? extends ModelElement>> creatablePhysicalLayerNodes;

    /** Alle Elementklassen, die Teile über eine HasPartEdge haben können */
    private final Set<Class<? extends ModelElement>> elementClassesWithHasPartEdgeClasses;

    /** Alle Elementklassen, die Teile über eine HasPartEdge sein können */
    private final Set<Class<? extends ModelElement>> elementClassesWithPartOfEdgeClasses;

    /** Mappt von einer Elementklasse auf den Layer, auf dem diese ELementklasse liegt (wenn er eindeutig ist) */
    private final Map<Class<? extends ModelElement>, Integer> elementClassToLayer;

    /** Mappt von einer Elementklasse auf alle Kantenklassen, mit denen der Elementklasse andere Elemente untergeordnet werden können */
    private final SetMultimap<Class<? extends ModelElement>, Class<? extends Edge>> initialSubtypes;

    /** Alle Elementklassen, deren Name generiert und nicht vom Nutzer eingegeben wird */
    private final Set<Class<? extends ModelElement>> generateNameClasses;

    //////////////////////////
    // Weitere Definitionen //
    //////////////////////////

    /** Beschreibung aller abhängigen Elemente, die beim Kopieren mitkopiert werden müssen */
    private final CopyDependencies copyDependencies;

    /** {@link MetaPathDefinition} des Metamodells */
    private final MetaPathDefinition metaPathsDefinition;

    /** {@link GraphViewDefinition} des Metamodells */
    private final GraphViewDefinition graphViewDefinition;

    /** {@link AnalysesDefinition} des Metamodells */
    private final AnalysesDefinition analysesDefinition;

    /** Actions, die für das spezielle Metamodell in das Extras-Menü eingetragen werden sollen */
    private final ExtrasActionsDefinition extrasActionsDefinition;

    /**
     * @param metaModelContext
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public MetaModel(final MetaModelContext metaModelContext) throws InstantiationException, IllegalAccessException {
        this.metaModelContext = metaModelContext;
        modelElementInstanceCreator = new ModelElementInstanceCreator(this);
        elementaryMetaPathHandler = new ElementaryMetaPathHandler(this);
        Class<? extends MetaModelDefinition> metaModelClass = metaModelContext.getMetaModelDefinitionClass();
        MetaModelDefinition metaModelDefinition = metaModelClass.newInstance();
        //Knoten
        allDomainLayerNodesSet = ImmutableSet.copyOf(Arrays.asList(metaModelDefinition.getAllDomainLayerNodes()));
        allInterDomainLogicalLayerNodesSet = ImmutableSet.copyOf(Arrays.asList(metaModelDefinition.getAllInterDomainLogicalLayerNodes()));
        allLogicalLayerNodesSet = ImmutableSet.copyOf(Arrays.asList(metaModelDefinition.getAllLogicalLayerNodes()));
        allInterLogicalPhysicalLayerNodesSet = ImmutableSet.copyOf(Arrays.asList(metaModelDefinition.getAllInterLogicalPhysicalLayerNodes()));
        allPhysicalLayerNodesSet = ImmutableSet.copyOf(Arrays.asList(metaModelDefinition.getAllPhysicalLayerNodes()));
        allNodesSet = ImmutableSet.<Class<? extends ModelElement>> builder().addAll(allDomainLayerNodesSet).addAll(allInterDomainLogicalLayerNodesSet).addAll(allLogicalLayerNodesSet).addAll(allInterLogicalPhysicalLayerNodesSet)
                .addAll(allPhysicalLayerNodesSet).build();
        //Kanten
        allEdgesSet = ImmutableSet.copyOf(Arrays.asList(metaModelDefinition.getAllEdges()));
        //alle Elementklassen
        allElementsSet = ImmutableSet.<Class<? extends ModelElement>> builder().addAll(allNodesSet).addAll(allEdgesSet).build();
        allModelElementClassesWithSuperClasses = CollectionUtils.ensureImmutable(getAllElementClassesWithSuperClasses());
        elementClassNameToElementClass = CollectionUtils.ensureImmutable(getElementClassNameToElementClass());

        //jetzt die GraphViewDefinition, weil die gleich gebraucht wird
        graphViewDefinition = getInstance(metaModelDefinition.getGraphViewDefinitionClass());
        //spezielle Knoteneigenschaften
        onlyExpertModeEditableNodes = CollectionUtils.ensureImmutable(metaModelDefinition.getOnlyExpertModeEditableNodes());
        onlyExpertModeVisibleNodes = CollectionUtils.ensureImmutable(metaModelDefinition.getOnlyExpertModeVisibleNodes());
        treeDomainLayerVisibleAbstractNodes = metaModelDefinition.getTreeDomainLayerVisibleAbstractNodes();
        treeLogicalLayerVisibleAbstractNodes = metaModelDefinition.getTreeLogicalLayerVisibleAbstractNodes();
        treePhysicalLayerVisibleAbstractNodes = metaModelDefinition.getTreePhysicalLayerVisibleAbstractNodes();
        oldToNewClassName = CollectionUtils.ensureImmutable(metaModelDefinition.getOldToNewClassNameMap());
        elementClassToRemovedEdgeClassesForStartClass = CollectionUtils.ensureImmutable(metaModelDefinition.getElementClassToRemovedEdgeClassesForStartClass());
        elementClassToRemovedEdgeClassesForEndClass = CollectionUtils.ensureImmutable(metaModelDefinition.getElementClassToRemovedEdgeClassesForEndClass());
        elementClassToSortedEdges = getElementClassToSortedEdges(); //muss nach den elementClassToRemovedEdgeClasses... und vor elementClassesWithSortedEdgesToPaintable, da für dessen init notwendig!
        elementClassesWithSortedEdgesToPaintable = CollectionUtils.ensureImmutable(getElementClassesWithSortedEdgeClassesToPaintable()); //muss vor elementClassesWithLayout, da für dessen init notwendig!
        elementClassesWithLayout = CollectionUtils.ensureImmutable(getElementClassesWithLayout());
        // Die folgenden Arrays müssen hier unten initialisiert werden nachdem die Maps mit den Edges gefüllt sind, sonst InitialException
        treeDomainLayerNodes = CollectionUtils.ensureImmutable(getTreeVisibleNodes(allDomainLayerNodesSet, false));
        creatableDomainLayerNodes = CollectionUtils.ensureImmutable(getTreeVisibleNodes(treeDomainLayerNodes, true));
        treeLogicalLayerNodes = CollectionUtils.ensureImmutable(getTreeVisibleNodes(allLogicalLayerNodesSet, false));
        creatableLogicalLayerNodes = CollectionUtils.ensureImmutable(getTreeVisibleNodes(treeLogicalLayerNodes, true));
        treePhysicalLayerNodes = CollectionUtils.ensureImmutable(getTreeVisibleNodes(allPhysicalLayerNodesSet, false));
        creatablePhysicalLayerNodes = CollectionUtils.ensureImmutable(getTreeVisibleNodes(treePhysicalLayerNodes, true));
        elementClassesWithHasPartEdgeClasses = CollectionUtils.ensureImmutable(getElementClassesWithPartEdges(HasPartEdge.PARENT_TO_PART_DIRECTION));
        elementClassesWithPartOfEdgeClasses = CollectionUtils.ensureImmutable(getElementClassesWithPartEdges(HasPartEdge.PART_TO_PARENT_DIRECTION));
        elementClassToLayer = CollectionUtils.ensureImmutable(getElementClassToLayerMap());
        initialSubtypes = CollectionUtils.ensureImmutable(getInitialSubtypes());
        generateNameClasses = CollectionUtils.ensureImmutable(metaModelDefinition.getGenerateNameClasses());
        importableNodes = CollectionUtils.ensureImmutable(metaModelDefinition.getImportableNodes());
        uniqueNodes = getUniqueNodes(); //ist schon immutable
        copyDependencies = metaModelDefinition.getCopyDependencies();
        analysesDefinition = getInstance(metaModelDefinition.getAnalysesDefinitionClass());
        extrasActionsDefinition = getInstance(metaModelDefinition.getExtrasActionsDefinitionClass());
        // Die MetaPathsDefinition und die darauffolgend zu initialisierenden Maps
        metaPathsDefinition = getInstance(metaModelDefinition.getMetaPathsDefinitionClass());
        edgeClassToConditionMetaPath = CollectionUtils.ensureImmutable(metaPathsDefinition.getConditionPaths());
        instanciationEdgeToAdditionalInstanciationMetaPaths = CollectionUtils.ensureImmutable(getInstanciationEdgeToAdditionalInstanciationNonAbstractMetaPaths(metaPathsDefinition.getInstanciationEdgeToAdditionalInstanciationMetaPaths()));
        elementClassToCreatableMetaPaths = CollectionUtils.ensureImmutable(getCreatableMetaPathsMap(metaPathsDefinition.getCreatablePaths()));
        elementClassToNameExtensionPath = CollectionUtils.ensureImmutable(metaPathsDefinition.getElementClassToNameExtensionPath());
        elementClassesWithNameExtensions = CollectionUtils.ensureImmutable(elementClassToNameExtensionPath.keySet());
    }

    /**
     * Liefert aus dem ResourceBundle des Contextes den String mit dem übergeben Key. Kommt er darin nicht vor, wird im Bundle des Tools gesucht.
     *
     * @param key
     * @return
     * @see MetaModelContext#getResString(String)
     */
    public final String getResString(final String key) {
        return metaModelContext.getResString(key);
    }

    /**
     * Wenn der Key nicht in den Resoucen gefunden wird, kommt einfach der key selsbt zurück und es wird keine MissingResourceException
     * ausgelöst.
     *
     * @param key
     * @return
     */
    public final String getResStringWithoutError(final String key) {
        try {
            return getResString(key);
        } catch (Exception e) {
            return key;
        }
    }

    /**
     * Liefert den Handler, über die alle Knoten- und Kantenklassennamen generiert werden, also die Anzeigenamen in Ein- und Mehrzahl und bei den
     * Kanten die gerichteten Namen.
     *
     * @return
     * @see MetaModelContext#getElementsNameBuilder()
     */
    public ElementsNameBuilder getElementsNameBuilder() {
        return metaModelContext.getElementsNameBuilder();
    }

    /**
     * Liefert die ID der Metamodellklasse. Dies ist ein String aus dem SimpleClassName + "@" + serialVersionUID. Damit sollte die die
     * Metamodellklasse immer eindeutig identifizierbar sein.
     *
     * @return
     * @see de.imise.tool3lgm.MetaModelContext#getMetaModelID()
     */
    public final String getMetaModelID() {
        return metaModelContext.getMetaModelID();
    }

    /**
     * @return
     */
    public final MetaModelContext getMetaModelContext() {
        return metaModelContext;
    }

    /**
     * Hanlder für das einfache und nicht redundante Anlegen von Elementar-Metapfaden für dieses MetaModel
     *
     * @return
     */
    public final ElementaryMetaPathHandler getElementaryMetaPathHandler() {
        return elementaryMetaPathHandler;
    }

    /**
     * Berechnet aus den vorher initialiserten {@link #allNodesSet} und allEdgesSet ein Set, das alle Elementklassen inklusive aller abstracten
     * Oberklassen bis
     * einschließlich {@link ModelElement} enthält.
     *
     * @return
     */
    private Set<Class<? extends ModelElement>> getAllElementClassesWithSuperClasses() {
        //Menge aller Elementklassen und aller ihrer Oberklassen bis hin zu ModelElement zusammenbauen
        ArrayList<Class<? extends ModelElement>> allElementClasses = new ArrayList<>(allNodesSet.size() + allEdgesSet.size() + 20);
        allElementClasses.addAll(allNodesSet);
        allElementClasses.addAll(allEdgesSet);
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
        return ImmutableSet.copyOf(allElementClasses);
    }

    /**
     * Berechnet aus den vorher initialiserten {@link #allModelElementClassesWithSuperClasses} eine Map vom SimpleClassName und dem FullClassName
     * jeweils auf die Klasse.
     *
     * @return
     */
    public Map<String, Class<? extends ModelElement>> getElementClassNameToElementClass() {
        ImmutableMap.Builder<String, Class<? extends ModelElement>> elementClassNameToElementClass = ImmutableMap.builder();
        for (Class<? extends ModelElement> elementClass : CollectionUtils.getCommonIterable(allModelElementClassesWithSuperClasses, META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES)) {
            elementClassNameToElementClass.put(elementClass.getSimpleName(), elementClass);
            elementClassNameToElementClass.put(elementClass.getName(), elementClass);
        }
        return elementClassNameToElementClass.build();
    }

    /**
     * Mappt von Elementklassen auf alle Kantenklassen, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben. Elementklasse ohne wenigestens eine solche Edge werden hier nicht eingtragen. D.h. es kommt <code>null</code> zurück, wenn
     * man nach solcher Elementklasse in der Map sucht und kein leeres Set.
     */
    public final ImmutableSetMultimap<Class<? extends ModelElement>, Class<? extends Edge>> getElementClassToSortedEdges() {
        ImmutableSetMultimap.Builder<Class<? extends ModelElement>, Class<? extends Edge>> mapBuilder = ImmutableSetMultimap.builder();
        Iterable<Class<? extends Edge>> sortedEdges = getSortedEdges();
        for (Class<? extends ModelElement> elementClass : allNodesSet) {
            for (Class<? extends Edge> edgeClass : sortedEdges) {
                if (isStartClass(edgeClass, elementClass)) {
                    mapBuilder.put(elementClass, edgeClass);
                }
            }
        }
        return mapBuilder.build();
    }

    private ImmutableSet<Class<? extends Edge>> getSortedEdges() {
        ImmutableSet.Builder<Class<? extends Edge>> sortedEdges = new ImmutableSet.Builder<>();
        for (Class<? extends Edge> edgeClass : allEdgesSet) {
            if (MultipleEdge.class.isAssignableFrom(edgeClass)) {
                sortedEdges.add(edgeClass);
            }
        }
        return sortedEdges.build();
    }

    /**
     * Berechnet aus den vorher initialiserten {@link #allNodesSet} und der {@link GraphViewDefinition} ein Set, das alle Elementklassen enthält, die
     * ein Layout brauchen.
     *
     * @return
     */
    private Set<Class<? extends ModelElement>> getElementClassesWithLayout() {
        ImmutableSet.Builder<Class<? extends ModelElement>> elementClassesWithLayoutBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
        //LayerKnoten
        elementClassesWithLayoutBuilder.add(LayerNode.class);
        //alle Knoten die Paintable sind oder ihre Kantennummern an andere Knoten schreiben
        GraphViewDefinition graphViewDefinition = getGraphViewDefinition();
        for (Class<? extends ModelElement> clazz : allNodesSet) {
            if (graphViewDefinition.isPaintable(clazz)) {
                elementClassesWithLayoutBuilder.add(clazz);
                continue;
            }
            if (hasSortedEdgeClassesToPaintable(clazz)) {
                elementClassesWithLayoutBuilder.add(clazz);
                continue;
            }
        }
        return elementClassesWithLayoutBuilder.build();
    }

    /**
     * Berechnet aus den vorher initialiserten {@link #allNodesSet} und der {@link GraphViewDefinition} ein Set, das alle Elementklassen enthält, die
     * Kanten, bei denen die Reihenfolge relevant ist, zu anderen Elementarten hat, die selbst paintable sind. Wenn das der Fall ist, dann kann an
     * diese anderen Elemente die Nummer(n) der Kanten geschrieben werden. In welcher Farbe das geschieht, bestimmt das Layout des Ausgangselementes.
     *
     * @return
     */
    private final Set<Class<? extends ModelElement>> getElementClassesWithSortedEdgeClassesToPaintable() {
        ImmutableSet.Builder<Class<? extends ModelElement>> elementClassesWithSortedEdgesBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
        for (Class<? extends ModelElement> clazz : allNodesSet) {
            Set<Class<? extends Edge>> sortedEdgeClasses = getSortedEdgeClasses(clazz);
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
        return elementClassesWithSortedEdgesBuilder.build();
    }

    /**
     * Erzeugt aus der in der {@link MetaModelDefinition} angegebenen Map von den InstanciateionEdges auf die ebenfalls mitzuinstanziierenden
     * MetaPfade die gleichartige endgültige Map, bei der die originalen Metapfade durch die Funktion
     * {@link SimpleMetaPathCreator#getSimpleMetaPathsNonAbstract(Iterable)} in alle Metapfade umgewandelt werden, die keine abstrakten
     * Zwischenklassen mehr enthalten und somit dann tatsächlich anlegbar sind.
     *
     * @param metaModelMap
     * @return
     */
    private static final Multimap<Class<? extends InstanciationEdge>, SimpleMetaPath> getInstanciationEdgeToAdditionalInstanciationNonAbstractMetaPaths(final Multimap<Class<? extends InstanciationEdge>, SimpleMetaPath> metaModelMap) {
        ImmutableListMultimap.Builder<Class<? extends InstanciationEdge>, SimpleMetaPath> builder = ImmutableListMultimap.builder();
        for (Class<? extends InstanciationEdge> instanciationEdge : metaModelMap.keySet()) {
            Collection<SimpleMetaPath> simpleMetaPaths = metaModelMap.get(instanciationEdge);
            Iterable<SimpleMetaPath> simpleMetaPathsNonAbstract = SimpleMetaPathCreator.getSimpleMetaPathsNonAbstract(simpleMetaPaths);
            builder.putAll(instanciationEdge, simpleMetaPathsNonAbstract);
        }
        return builder.build();
    }

    /**
     * Erzeugt aus der Sammlung aller anlegbaren Pfade die Map die von einer ElementKlasse auf eine Sammlung aller {@link SimpleMetaPath} mappt, die
     * man zwischen ihr und anderen Elementen anlegen kann, wobei die Zwischenelemente ebenfalls neu angelegt werden. Diese Pfade werden im
     * Kontextmenü bei Mehrfachselektion oder Einfachselektion angeboten.
     *
     * @param creatablePaths
     * @return
     */
    private final Multimap<Class<? extends ModelElement>, SimpleMetaPath> getCreatableMetaPathsMap(final Collection<SimpleMetaPath> creatablePaths) {
        ImmutableListMultimap.Builder<Class<? extends ModelElement>, SimpleMetaPath> builder = ImmutableListMultimap.builder();
        if (creatablePaths != null) {
            for (SimpleMetaPath definedCreateableMetaPath : creatablePaths) {
                Collection<SimpleMetaPath> createableMetaPathsNonAbstract = SimpleMetaPathCreator.getSimpleMetaPathsNonAbstract(definedCreateableMetaPath);
                if (createableMetaPathsNonAbstract.isEmpty()) {
                    Sys.errn(2, "Createable Path is not valid: " + definedCreateableMetaPath + " (" + definedCreateableMetaPath.getFullPathString() + ")");
                }
                for (SimpleMetaPath metaPath : createableMetaPathsNonAbstract) {
                    Collection<Class<? extends ModelElement>> startClasses = getInstanciableAssignableClasses(metaPath.getStartClass());
                    for (Class<? extends ModelElement> startClass : startClasses) {
                        builder.put(startClass, metaPath);
                    }
                    //Gegenrichtung des Pfades für die Endklasse als Startklasse hinzufügen
                    metaPath = metaPath.getOtherDirection();
                    Collection<Class<? extends ModelElement>> endClasses = getInstanciableAssignableClasses(metaPath.getStartClass());
                    for (Class<? extends ModelElement> endClass : endClasses) {
                        builder.put(endClass, metaPath);
                    }
                }
            }
        }
        ImmutableListMultimap<Class<? extends ModelElement>, SimpleMetaPath> classToCreatablePaths = builder.build();
        return classToCreatablePaths;
    }

    /**
     * Extrahiert aus den übergebenen Knoten alle, die im Baum angezeigt werden.
     *
     * @param elementClasses Elementklassen, die gefiltert werden sollen
     * @param creatableOnly wenn <code>true</code>, werden von den anzuzeigenden Knoten nur die übrig gelassen, die man auch ohne ein anderes Element
     *            anlegen kann
     * @return
     */
    private Set<Class<? extends ModelElement>> getTreeVisibleNodes(final Iterable<Class<? extends ModelElement>> elementClasses, final boolean creatableOnly) {
        ImmutableSet.Builder<Class<? extends ModelElement>> creatableNodes = new ImmutableSet.Builder<>();
        for (Class<? extends ModelElement> elementClass : elementClasses) {
            if (!isEdgeType(elementClass)) {
                if (!isAbstract(elementClass)) {
                    if (!creatableOnly || !isExistenceDependent(elementClass)) {
                        creatableNodes.add(elementClass);
                    }
                }
            }
        }
        return creatableNodes.build();
    }

    /**
     * Liefert je nach angegebener Richtung ein Set aller Elementklasse, die Teile haben können oder selbst Teile sind.
     *
     * @param direction
     */
    private Set<Class<? extends ModelElement>> getElementClassesWithPartEdges(final Edge.Direction direction) {
        ImmutableSet.Builder<Class<? extends ModelElement>> elementClassesWithPartEdges = ImmutableSet.builder();
        for (Class<? extends ModelElement> elementClass : allNodesSet) {
            //Hole alle Kantenklassen der Zielklasse und suche alle HasPartEdges
            for (Class<? extends Edge> c : getEdgeTypes(elementClass)) {
                if (HasPartEdge.class.isAssignableFrom(c)) {
                    Class<? extends HasPartEdge> hasPartEdgeClass = c.asSubclass(HasPartEdge.class);
                    boolean isHasPartOrPartOfClass = direction == HasPartEdge.PARENT_TO_PART_DIRECTION;
                    isHasPartOrPartOfClass = isHasPartOrPartOfClass ? HasPartEdge.isParentClass(hasPartEdgeClass, elementClass) : HasPartEdge.isPartClass(hasPartEdgeClass, elementClass);
                    if (!isRemovedEdgeClass(elementClass, hasPartEdgeClass, direction == Direction.FORWARD)) {
                        elementClassesWithPartEdges.add(elementClass);
                        break;
                    }
                }
            }
        }
        return elementClassesWithPartEdges.build();
    }

    /** Baut die Map zusammen, die von einer Elementklasse auf den Layer der Klasse mappt, wenn dieser eindeutig ist */
    private Map<Class<? extends ModelElement>, Integer> getElementClassToLayerMap() {
        Map<Class<? extends ModelElement>, Integer> map = new HashMap<>();
        for (Class<? extends ModelElement> elementClass : allDomainLayerNodesSet) {
            map.put(elementClass, ModelConstants.DOMAIN_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : allInterDomainLogicalLayerNodesSet) {
            map.put(elementClass, ModelConstants.INTER_DOMAIN_LOGICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : allLogicalLayerNodesSet) {
            map.put(elementClass, ModelConstants.LOGICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : allInterLogicalPhysicalLayerNodesSet) {
            map.put(elementClass, ModelConstants.INTER_LOGICAL_PHYSICAL_LAYER);
        }
        for (Class<? extends ModelElement> elementClass : allPhysicalLayerNodesSet) {
            map.put(elementClass, ModelConstants.PHYSICAL_LAYER);
        }
        for (Class<? extends Edge> edgeClass : allEdgesSet) {
            if (map.get(edgeClass) == null) {
                int layer = getEdgeLayer(map, Edge.getStartClass(edgeClass), Edge.getEndClass(edgeClass));
                if (layer != ModelConstants.NO_LAYER) {
                    map.put(edgeClass, layer);
                }
            }
        }
        //nicht über den Builder gehen, weil die key-Klassen mehrfach in den Sets vorkommen können. Der Builder beendet dann mit einem Error.
        ImmutableMap<Class<? extends ModelElement>, Integer> returnMap = ImmutableMap.copyOf(map);
        return returnMap;
    }

    /**
     * Liefert für eine Elementklasse alle Elementklassen, die ihr untergeordnet sind (also über eine Komposition mit
     * ihr verbunden sind, bei der sie der Master ist) und die minimale Kardinlität der Unterklassen > 0 ist.
     *
     * @param elementClass
     */
    private final SetMultimap<Class<? extends ModelElement>, Class<? extends Edge>> getInitialSubtypes() {
        ImmutableSetMultimap.Builder<Class<? extends ModelElement>, Class<? extends Edge>> initialSubtypes = ImmutableSetMultimap.builder();
        for (Class<? extends ModelElement> elementClass : allElementsSet) {
            Class<? extends CompositionEdge>[] compositionEdgeTypes = getCompositionEdgeTypes(elementClass, true);
            for (Class<? extends CompositionEdge> compositionEdgeType : compositionEdgeTypes) {
                if (getMinForwardCardinality(compositionEdgeType) > ZERO) {
                    initialSubtypes.put(elementClass, compositionEdgeType);
                }
            }
        }
        return initialSubtypes.build();
    }

    /**
     * Alle Knotenklassen, die in jedem Teilmodell vorkommen, also nicht in jedem Teilmodell einen eigenen Container besitzen.
     * Das sind alle nicht-abstrakten Knotenklassen (nicht Kante), die in der GraphViewDefinition nicht als paintable eingetragen sind.
     */
    public final Set<Class<? extends Node>> getUniqueNodes() {
        ImmutableSet.Builder<Class<? extends Node>> uniqueNodes = new ImmutableSet.Builder<>();
        for (Class<? extends ModelElement> elementClass : allNodesSet) {
            //keine abstrakten Klassen zu diesem Set hinzufügen
            if (!Modifier.isAbstract(elementClass.getModifiers())) {
                //nur Knotenklassen nehmen (dort können auch Assoziationsklassen drin sein)
                if (Node.class.isAssignableFrom(elementClass)) {
                    if (!hasLayout(elementClass)) {
                        uniqueNodes.add(elementClass.asSubclass(Node.class));
                    }
                }
            }
        }
        return uniqueNodes.build();
    }

    ////////////////////////////////////////////
    // Ende private Initialisierungfunktionen //
    ////////////////////////////////////////////

    /**
     * Liefert alle Elementklassen, die nur im Baum angezeigt werden sollen, wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>true</code> gestellt ist.
     *
     * @return alle Elementklassen, die nur im ExpertMode im Baum angezeigt werden
     * @see MetaModelDefinition#getOnlyExpertModeVisibleNodes()
     */
    public final Set<Class<? extends ModelElement>> getOnlyExpertModeVisibleNodes() {
        return onlyExpertModeVisibleNodes;
    }

    /**
     * Liefert <code>true</code>, wenn die Klasse nicht angezeigt werden soll, also wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>false</code> gestellt ist und die Klasse nur im Expert-Mode angezeigt werden soll.
     *
     * @return <code>true</code>, wenn die Klasse gerade nicht sichtbar sein soll
     * @see #getOnlyExpertModeVisibleNodes()
     */
    public final boolean isHiddenClass(final Class<? extends ModelElement> elementClass) {
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
     * Liefert <code>true</code>, wenn alle übergebenen Klasse aktuell editierbar ist. Das ist sie, wenn sich der Baukasten im ExpertMode befindet
     * oder wenn er sich nicht im ExpertMode befindet und die Klasse keine Klasse aus den {@link #getOnlyExpertModeEditableNodes()} ist.
     *
     * @param elementClass
     * @return
     */
    @SafeVarargs
    public final boolean isEditable(final Class<? extends ModelElement>... elementClasses) {
        if (!Static.isExpertMode()) {
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
    public final boolean isEditable(final SimpleMetaPath... simpleMetaPaths) {
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

    /**
     * Liefert <code>true</code>, wenn ein Panel mit diesem Pfad angezeigt werden soll. Ob es angezeigt werden soll entscheidet sich anhand der
     * Zielklasse des Pfades. Ist diese nur im Expert-Mode anzuzeigen, der Modeus aber nicht an, dann sollte ein Panel mit diesem MetaPath nicht
     * angezeigt werden.
     *
     * @param metaPath
     * @return
     */
    public final boolean isVisible(final SimpleMetaPath metaPath) {
        if (Static.isExpertMode()) {
            return true;
        }
        for (Class<? extends ModelElement> endClass : metaPath.getEndClasses()) {
            if (isHiddenClass(endClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse für die übergebene Elementklasse in Vorwärtsrichtung nicht mehr gelten soll.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    public boolean isRemovedEdgeClassForStartClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return isRemovedEdgeClass(elementClass, edgeClass, true);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse für die übergebene Elementklasse in Rückwärtsrichtung nicht mehr gelten soll.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    public boolean isRemovedEdgeClassForEndClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return isRemovedEdgeClass(elementClass, edgeClass, false);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse für die übergebene Elementklasse in Vorwärts- und Rückwärtsrichtung nicht mehr
     * gelten soll.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    public boolean isRemovedEdgeClassForStartAndEndClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return isRemovedEdgeClass(elementClass, edgeClass, true) && isRemovedEdgeClass(elementClass, edgeClass, false);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse für die übergebene Elementklasse als nicht mehr gültig definiert wurde.
     *
     * @param elementClass
     * @param edgeClass
     * @param asStartClass
     * @return
     */
    private boolean isRemovedEdgeClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass, final boolean asStartClass) {
        Multimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToRemovedEdgeClasses = asStartClass ? elementClassToRemovedEdgeClassesForStartClass : elementClassToRemovedEdgeClassesForEndClass;
        Collection<Class<? extends Edge>> removedEdgeClasses = elementClassToRemovedEdgeClasses.get(elementClass);
        if (removedEdgeClasses != null) {
            if (removedEdgeClasses.contains(edgeClass)) {
                return true;
            }
        }
        return false;
    }

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    /**
     * Liefert ein Set aller Kantenklassen, die für die übergebene Elementklasse "geordnet sind", d. h. dass für Elemente der übergebenen Klasse die
     * Reihenfolge der Instanzen der zurück gelieferten Kantenklassen in ihrem Kantenvektor eine Bedeutung hat
     * (z. B. Reihenfolge von Aufgaben in einem Prozess -> Verbindung zwischen Prozessen und Aufgaben sind für den Prozess geordnet).
     *
     * @param elementClass
     */
    public final Set<Class<? extends Edge>> getSortedEdgeClasses(final Class<? extends ModelElement> elementClass) {
        return elementClassToSortedEdges.get(elementClass);
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

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse in der Grafik als Knoten oder Kante gezeichnet wird.
     *
     * @param elementClass
     * @return
     */
    public final boolean isPaintable(final Class<? extends ModelElement> elementClass) {
        return getGraphViewDefinition().isPaintable(elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse ein Layout besitzt (und damit nicht unique ist).
     * Das trifft auf alle Elemenklassen zu, die paintable sind oder ihre Kantennummern an paintable-Elemente schreiben.
     * Außerdem brauchen die Layer-Knoten ein Layout.
     *
     * @param elementClass
     * @return
     */
    public final boolean hasLayout(final Class<? extends ModelElement> elementClass) {
        return elementClassesWithLayout.contains(elementClass) || Textfield.class.isAssignableFrom(elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse Kanten bei denen die Reihenfolge relevant ist, zu anderen Elementarten hat, die selbst
     * paintable sind. Wenn das der Fall ist, dann kann an diese anderen Elemente die Nummer(n) der Kanten geschrieben werden. In welcher Farbe das
     * geschieht, bestimmt das Layout des Ausgangselementes.
     *
     * @return
     */
    public final boolean hasSortedEdgeClassesToPaintable(final Class<? extends ModelElement> elementClass) {
        return elementClassesWithSortedEdgesToPaintable.contains(elementClass);
    }

    /** Alle abstracten Klassen, die im Baum aus der FE auftauchen sollen */
    public Class<? extends ModelElement>[] getTreeDomainLayerVisibleAbstractNodes() {
        return treeDomainLayerVisibleAbstractNodes;
    }

    /** Alle abstracten Klassen, die im Baum aus der LWE auftauchen sollen */
    public Class<? extends ModelElement>[] getTreeLogicalLayerVisibleAbstractNodes() {
        return treeLogicalLayerVisibleAbstractNodes;
    }

    /** Alle abstracten Klassen, die im Baum aus der LWE auftauchen sollen */
    public Class<? extends ModelElement>[] getTreePhysicalLayerVisibleAbstractNodes() {
        return treePhysicalLayerVisibleAbstractNodes;
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
    public final boolean isDirectedEdge(final Class<? extends Edge> edgeClass) {
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
        if (Edge.getStartClass(edgeClass) != Edge.getEndClass(edgeClass) || isDoubleMeaningEdge(edgeClass)) {
            return true;
        }
        ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
        return !elementsNameBuilder.getForwardMetaAssociationName(edgeClass).equals(elementsNameBuilder.getBackwardMetaAssociationName(edgeClass));
    }

    /**
     * Liefert für die übergebene Kantenklasse den MetaPfad, über den die verbindbaren Elemente ebenfalls bereits verbunden sein müssen.
     * Dieser Mechanismus ist dafür gedacht, verbindbare Elemente einzuschränken auf bestimmte Elemente.
     *
     * @param edgeClass
     * @return
     */
    public SimpleMetaPath getConditionPath(final Class<? extends Edge> edgeClass) {
        return edgeClassToConditionMetaPath.get(edgeClass);
    }

    /**
     * Sammlung aller Pfade, die ausgehend vom Startelement dieser Kante ebenfalls angelegt werden sollen, wenn eine Instanziierung über diese
     * Kantenklasse durchgeführt wird.
     *
     * @param instanciationEdgeClass
     * @return
     * @see MetaModelDefinition#getInstanciableMetaPaths(Class)
     */
    public Iterable<SimpleMetaPath> getInstanciablePath(final Class<? extends InstanciationEdge> instanciationEdgeClass) {
        Iterable<SimpleMetaPath> instanciableMetaPaths = instanciationEdgeToAdditionalInstanciationMetaPaths.get(instanciationEdgeClass);
        return SimpleMetaPathCreator.getSimpleMetaPathsNonAbstract(instanciableMetaPaths);
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    /**
     * Liefert aus der <code>HashMap oldToNewName</code> den aktuellen Klassennamen für den übergebenen alten Klassennamen. <br>
     * Ist in <code>oldToNewName</code> kein Eintrag für den übergebenen alten Klassennamen vorhanden, wird davon ausgegangen, dass der alte Name der
     * aktuelle ist.
     *
     * @param oldName
     * @return
     */
    public final String getCurrentClassName(String oldName) {
        String newName = oldToNewClassName.get(oldName);
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
            newName = oldToNewClassName.get(oldName);
            if (newName == null) {
                return oldName;
            }
        }
    }

    /**
     * Liefert alle nichtabstrakten, zur übergebenen Klasse zuweisungskompatiblen Element- oder Kantenklassen. Die übergebene Klasse selbst ist in den
     * Rückgabewerten enthalten, wenn sie nichtabstract ist.
     *
     * @param elementClass
     * @return
     */
    public final Collection<Class<? extends ModelElement>> getInstanciableAssignableClasses(final Class<? extends ModelElement> elementClass) {
        if (elementClassToNonAbstractAssignableElementClasses.containsKey(elementClass)) {
            Collection<Class<? extends ModelElement>> classes = elementClassToNonAbstractAssignableElementClasses.get(elementClass);
            if (classes.size() == 1 && classes.iterator().next() == null) {
                return EMPTY_ELEMENT_CLASS_COLLECTION;
            }
            return classes;
        }
        for (Class<? extends ModelElement> clazz : allElementsSet) {
            if (elementClass.isAssignableFrom(clazz) && !isAbstract(clazz)) {
                elementClassToNonAbstractAssignableElementClasses.put(elementClass, clazz);
            }
        }
        if (!elementClassToNonAbstractAssignableElementClasses.containsKey(elementClass)) {
            elementClassToNonAbstractAssignableElementClasses.put(elementClass, null);
        }
        return getInstanciableAssignableClasses(elementClass);
    }

    /**
     * Liefert für eine Elementklasse alle Kantenklassen dieser Klasse zu anderen Elementklassen
     *
     * @param elementClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public Class<? extends Edge>[] getEdgeTypes(final Class<? extends ModelElement> elementClass) {
        Class<? extends Edge>[] edgeClasses = elementClassToEdgeClasses.get(elementClass);
        if (edgeClasses != null) {
            return edgeClasses;
        }
        List<Class<? extends Edge>> elementClassEdgeClasses = new ArrayList<>();
        for (Class<? extends Edge> edgeClass : allEdgesSet) {
            if (isStartOrEndClass(edgeClass, elementClass)) {
                if (!isRemovedEdgeClassForStartAndEndClass(elementClass, edgeClass)) {
                    elementClassEdgeClasses.add(edgeClass);
                }
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
        elementClassToEdgeClasses.put(elementClass, returnClasses);
        return returnClasses;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse die übergebene Kantenart hat. Es wird Zuweisungskompatibilität gerpüft.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    public final boolean hasEdgeType(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        Class<? extends Edge>[] edgeTypes = getEdgeTypes(elementClass);
        for (Class<? extends Edge> edgeType : edgeTypes) {
            if (edgeClass.isAssignableFrom(edgeType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert ein Array aller Kantenklassen, die zwischen den beiden übergebenen Elementklassen existieren können. Gibt es keine Kantenklasse
     * zwischen den Elementen so kommt ein leeres Array (length==0) zurück.
     *
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    @SuppressWarnings("unchecked")
    public final Class<? extends Edge>[] getEdgeTypes(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        Class<? extends Edge>[] edgeClasses = elementClassesToEdgeClasses.get(elementClass1, elementClass2);
        if (edgeClasses != null) {
            return edgeClasses;
        }
        ArrayList<Class<? extends Edge>> resultEdgeClasses = new ArrayList<>();
        Class<? extends Edge>[] edgeTypes = getEdgeTypes(elementClass1);
        for (Class<? extends Edge> edgeClass : edgeTypes) {
            if (isConnecting(edgeClass, elementClass1, elementClass2)) {
                if (!isRemovedEdgeClassForStartAndEndClass(elementClass2, edgeClass)) { // !isRemovedEdgeClass(elementClass1, edgeClass) wird schon in getEdgeTypes(elementClass1) geprüft
                    resultEdgeClasses.add(edgeClass);
                }
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
        //jetzt das gefundenen Kantenklassen-Array für beide Elementklassenkombinationen ablegen
        elementClassesToEdgeClasses.put(elementClass1, elementClass2, returnClasses);
        elementClassesToEdgeClasses.put(elementClass2, elementClass1, returnClasses);
        return returnClasses;
    }

    public final Iterable<Class<? extends ModelElement>> getCreatableLayerNodes(final int layer) {
        if (layer == ModelConstants.DOMAIN_LAYER) {
            return creatableDomainLayerNodes;
        }
        if (layer == ModelConstants.LOGICAL_LAYER) {
            return creatableLogicalLayerNodes;
        }
        if (layer == ModelConstants.PHYSICAL_LAYER) {
            return creatablePhysicalLayerNodes;
        }
        return EMPTY_ELEMENT_CLASS_COLLECTION;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebenen Klasse eine Knotenklassen ist, die in jedem Teilmodell vorkommt, also nicht in jedem Teilmodell
     * einen eigenen Container besitzt.
     */
    public final boolean isUnique(final Class<?> elementClass) {
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            return isUnique(Edge.getStartClass(edgeClass)) || isUnique(Edge.getEndClass(edgeClass));
        }
        return uniqueNodes.contains(elementClass);
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
    public final boolean isAssociationClass(final Class<? extends ModelElement> elementClass) {
        if (!Edge.class.isAssignableFrom(elementClass)) {
            return false;
        }
        Class<? extends Edge>[] edgeTypes = getEdgeTypes(elementClass);
        return edgeTypes != null && edgeTypes.length != 0;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse Startklasse eines Interebenenmetapfades ist.
     *
     * @param me
     * @return
     */
    public final boolean hasInterLayerStartClass(final ModelElement me) {
        return getGraphViewDefinition().getInterLayerMetaPath(me) != null;
    }

    /**
     * Liefert <code>true</code>, wenn es sich bei der übergebenen Kantenklasse um eine {@link InstanciationEdge} handelt und die übergebene
     * Elementklasse davon das StartElement - also das instanziierbare Element ist und nicht die Instanz.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    public boolean isInstanciationEdgeMaster(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return InstanciationEdge.class.isAssignableFrom(edgeClass) && isStartClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der Edge oder eine Unterklasse davon ist und die Kantenklasse
     * für diese Elementart nicht entfernt wurde.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public final boolean isStartClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        if (isRemovedEdgeClassForStartClass(elementClass, edgeClass)) {
            return false;
        }
        Class<? extends ModelElement> startClass = Edge.getStartClass(edgeClass);
        return startClass.isAssignableFrom(elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Endklasse der Edge oder eine Unterklasse davon ist und die Kantenklasse
     * für diese Elementart nicht entfernt wurde.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public final boolean isEndClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        if (isRemovedEdgeClassForEndClass(elementClass, edgeClass)) {
            return false;
        }
        Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
        return endClass.isAssignableFrom(elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Start- oder Endklasse der Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public final boolean isStartOrEndClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isStartClass(edgeClass, elementClass) || isEndClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse Elemente der angegebenen Arten miteinander verbindet.
     *
     * @param edgeClass
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    public final boolean isConnecting(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        return isConnectingForward(edgeClass, elementClass1, elementClass2) || isConnectingForward(edgeClass, elementClass2, elementClass1);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse Elemente der angegebenen Arten in Vorwärtsrichtung miteinander verbindet. Also
     * <code>startElementClass</code> die Startklasse der Kantenklasse oder eine Unterklasse davon ist und <code>endElementClass</code> die Endklasse
     * der Kantenklasse oder eine Unterklasse davon ist.
     *
     * @param edgeClass
     * @param startElementClass
     * @param endElementClass
     * @return
     */
    public final boolean isConnectingForward(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        return isStartClass(edgeClass, startElementClass) && isEndClass(edgeClass, endElementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die Start- und Endelemente von derselben Klasse sein können, d.h. wenn die beiden Klassen
     * gleich sind oder eine eine Oberklasse der anderen ist.
     *
     * @param edgeClass
     * @return
     */
    public boolean isRecursive(final Class<? extends Edge> edgeClass) {
        Class<? extends ModelElement> startClass = Edge.getStartClass(edgeClass);
        Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
        if (startClass.isAssignableFrom(endClass)) {
            return !isRemovedEdgeClassForEndClass(endClass, edgeClass);
        } else if (endClass.isAssignableFrom(startClass)) {
            return !isRemovedEdgeClassForStartClass(startClass, edgeClass);
        }
        return false;
    }

    /**
     * Wenn die übergebene Elementklasse durch eine Edge der angegebenen Art mit anderen Elementen verbunden sein kann, dann wird die Elementklasse
     * dieser anderen Elemente zurück gegeben. Passen Edge und Elementklasse nicht zusammen, kommt <code>null</code> zurück.
     *
     * @param edgeClass Kantanklasse, von der die andere verbundene Elementklasse zurück gegeben werden soll
     * @param meClass Elementklasse der Edge, deren Gegenelementklasse zurück gegeben werden soll
     * @return die andere Elementklasse der Edge, als die übergebene Klasse oder <code>null</code>, wenn die Klasse gar nicht passt
     */
    public final Class<? extends ModelElement> getOther(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> meClass) {
        if (isStartClass(edgeClass, meClass)) {
            return Edge.getEndClass(edgeClass);
        }
        if (isEndClass(edgeClass, meClass)) {
            return Edge.getStartClass(edgeClass);
        }
        return null;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Slave-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public final boolean isSlaveType(final Class<? extends CompositionEdge> compositionClass, final Class<? extends ModelElement> elementClass) {
        return isEndClass(compositionClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Master-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public final boolean isMasterType(final Class<? extends CompositionEdge> compositionClass, final Class<? extends ModelElement> elementClass) {
        return isStartClass(compositionClass, elementClass);
    }

    ////////////////////
    // Kardinalitäten //
    ////////////////////

    /**
     * @param edgeClass
     * @param backward
     * @return
     */
    private static final EdgeCardinality getCardinality(final Class<? extends Edge> edgeClass, final boolean backward) {
        String fieldName = backward ? Edge.START_CARDINALITY_FIELD_NAME : Edge.END_CARDINALITY_FIELD_NAME;
        return ReflectionUtils.getField(edgeClass, ModelElement.class, fieldName, EdgeCardinality.class);
    }

    /**
     * Liefert die Kardinalität für Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen hat.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public final EdgeCardinality getCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        if (isStartClass(edgeClass, elementClass)) {
            return getCardinality(edgeClass, false);
        }
        if (isEndClass(edgeClass, elementClass)) {
            return getCardinality(edgeClass, true);
        }
        return null;
    }

    /**
     * Liefert die minimale Anzahl von Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen haben muss.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public final int getMinCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        EdgeCardinality cardinality = getCardinality(elementClass, edgeClass);
        return cardinality != null ? cardinality.min() : STANDARD_ERROR_INT_VALUE;
    }

    /**
     * Liefert die maximale Anzahl von Kanten der übergebenen Art, die ein Element der übergebenen Art zu anderen Elementen haben kann.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public final int getMaxCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        EdgeCardinality cardinality = getCardinality(elementClass, edgeClass);
        return cardinality != null ? cardinality.max() : STANDARD_ERROR_INT_VALUE;
    }

    /**
     * @param edgeClass
     * @return
     */
    public final EdgeCardinality getForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final EdgeCardinality getBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMinForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false).min();
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMaxForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false).max();
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMinBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true).min();
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMaxBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true).max();
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMinMasterToSlaveCardinality(final Class<? extends CompositionEdge> edgeClass) {
        return getMinForwardCardinality(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMaxMasterToSlaveCardinality(final Class<? extends CompositionEdge> edgeClass) {
        return getMaxForwardCardinality(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMinSlaveToMasterCardinality(final Class<? extends CompositionEdge> edgeClass) {
        return getMinBackwardCardinality(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final int getMaxSlaveToMasterCardinality(final Class<? extends CompositionEdge> edgeClass) {
        return getMaxBackwardCardinality(edgeClass);
    }

    /////////////////////////////////
    // Namen auflösen oder liefern //
    /////////////////////////////////

    /**
     * Gibt die Klasse zu einem Klassennamen zurück. Der Klassenname kann voll qualifiziert sein oder aber nur aus dem simplen Klassenamen bestehen.
     *
     * @param classname String mit der Klassenbezeichnung
     * @return Class
     */
    public final Class<? extends ModelElement> getClassForName(final String classname) {
        return elementClassNameToElementClass.get(classname);
    }

    public final boolean canHaveParts(final Class<? extends ModelElement> elementClass) {
        return elementClassesWithHasPartEdgeClasses.contains(elementClass);
    }

    public final boolean canHaveParents(final Class<? extends ModelElement> elementClass) {
        return elementClassesWithPartOfEdgeClasses.contains(elementClass);
    }

    public final boolean canHavePartsOrParents(final Class<? extends ModelElement> elementClass) {
        return canHaveParts(elementClass) || canHaveParents(elementClass);
    }

    public static final boolean isHasPartEdge(final Class<? extends Edge> edgeClass) {
        return HasPartEdge.class.isAssignableFrom(edgeClass);
    }

    public boolean isRecursiveHasPartEdge(final Class<? extends Edge> edgeClass) {
        return isHasPartEdge(edgeClass) && isRecursive(edgeClass);
    }

    public boolean isRecursiveSubordination(final Class<? extends Edge> edgeClass) {
        return SubordinationEdge.class.isAssignableFrom(edgeClass) && isRecursive(edgeClass);
    }

    /*******************/

    /**
     * Liefert den Layer der Kante, wenn die Kante die übergebenen Klassen verbindet
     *
     * @param edgeStartClass
     * @param edgeEndClass
     * @return
     */
    public final int getEdgeLayer(final Class<? extends ModelElement> edgeStartClass, final Class<? extends ModelElement> edgeEndClass) {
        return getEdgeLayer(elementClassToLayer, edgeStartClass, edgeEndClass);
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
        int startElementLayer = startLayer == null ? ModelConstants.NO_LAYER : startLayer;
        int endElementLayer = endLayer == null ? ModelConstants.NO_LAYER : endLayer;
        int layer = startElementLayer;
        //eine Kante gehört immer zu einem Zwischenlayer, wenn das Start- oder Endelement zu einem Zwischenlayer gehören
        //wenn beide zu Zwischeenlayer oder beide zu normalen Ebenen gehören, dann gehört die Kante immer zur jeweils höhreren Ebene
        //wenn einer der Layer NO_LAYER ist, dann liegt die Kante auf dem anderen. Wenn beide Layer NO_LAYER sind, ist auch die Kanze NO_LAYER
        if (layer == ModelConstants.NO_LAYER) {
            layer = endElementLayer;
        } else if (endElementLayer != ModelConstants.NO_LAYER && startElementLayer != endElementLayer) {
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
    public final int layerFor(final Class<? extends ModelElement> elementClass) {
        Integer layer = elementClassToLayer.get(elementClass);
        return layer == null ? ModelConstants.NO_LAYER : layer.intValue();
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
    private Class<? extends CompositionEdge>[] getCompositionEdgeTypes(final Class<? extends ModelElement> elementClass, final boolean isMaster) {
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
    //      for (Class<? extends ModelElement> c : ALL_NODES){
    //          System.err.println(c.getSimpleName() + "\n\t" + Arrays.toString(getSubordinationEdgeTypesAsMaster(c)));
    //      }
    //    }

    /**
     * Liefert ein Array aller Kantenklassen, durch die die übergebene Elementart einer anderen übergeordnet wird. Dies sind alle Kantenklasse, die
     * Kompositionen sind und bei denen mindestens eine Startklasse zuweisungskompatibel zur übergebenen
     * Elementklasse ist.
     *
     * @param elementClass
     * @return Array von Kantenklassen, die die übergebene Elementart überordnen
     */
    public Class<? extends CompositionEdge>[] getCompositionEdgeTypesForMaster(final Class<? extends ModelElement> elementClass) {
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
    public Class<? extends CompositionEdge>[] getCompositionEdgeTypesForSlave(final Class<? extends ModelElement> elementClass) {
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
    public final Class<? extends ModelElement>[] getSlaveElementTypes(final Class<? extends ModelElement> masterElementClass) {
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
    public boolean isSlaveType(final Class<? extends ModelElement> elementClass) {
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
    private boolean isExistenceDependent(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends Edge> edgeClass : getEdgeTypes(elementClass)) {
            //System.err.print(elementClass.getSimpleName() + "  --->  " + edgeClass.getSimpleName() + "  --->  " + Edge.getMinCardinality(elementClass, edgeClass) + "  --->  ");
            //minimale Kardinalität von 1 zu anderen Elementen -> dieses Element braucht mind. ein anderes, damit es konsistent ist
            if (getMinCardinality(elementClass, edgeClass) > 0) {
                //wenn das andere, benötigte Element aber mit einer Compostion untergeordnet ist, dann wird dieses benötigte, untergeordnete Element in der GDCollection-Funktion createInitialSubtypes(...) auomatisch erzeugt und somit die Konsistenz automatisch hergestellt und damit gilt dieses Element nicht als anhängig
                if (!isComposition(edgeClass) || isEndClass(edgeClass, elementClass)) {
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
    public final Set<Class<? extends Edge>> getSubordinatedJoinbleTypes(final Class<? extends ModelElement> elementClass) {
        Set<Class<? extends Edge>> edgeClassesToSubordinatedJoinbleTypes = new HashSet<>();
        for (Class<? extends Edge> edgeClass : getEdgeTypes(elementClass)) {
            getOther(edgeClass, elementClass);
            int maxCardinality = getMaxCardinality(elementClass, edgeClass);
            if (maxCardinality == 1) {
                edgeClassesToSubordinatedJoinbleTypes.add(edgeClass);
            }
        }
        return edgeClassesToSubordinatedJoinbleTypes;
    }

    /**
     * Liefert für eine Elementklasse alle Elementklassen, die ihr untergeordnet sind (also über eine Komposition mit
     * ihr verbunden sind, bei der sie der Master ist) und die minimale Kardinlität der Unterklassen > 0 ist.
     *
     * @param elementClass
     */
    public final Set<Class<? extends Edge>> getInitialSubtypes(final Class<? extends ModelElement> elementClass) {
        return initialSubtypes.get(elementClass);
    }

    /**
     * dieser boolean muss in allen Node auf true gesetzt werden, die eine eigene toString() besitzen, welche aus anderen Modellelementen den Namen
     * generiert (siehe AufOrgKombination, EtntEtdtKombination)
     *
     * @return
     */
    public final boolean isGenerateName(Class<? extends ModelElement> elementClass) {
        while (elementClass != ModelElement.class) {
            if (generateNameClasses.contains(elementClass)) {
                return true;
            }
            elementClass = elementClass.getSuperclass().asSubclass(ModelElement.class);
        }
        return false;
    }

    /**
     * Liefert alle anlegbaren MetaPfade, bei denen für selektierte Elemente der übergebenen Elementart im Kontextmenü eine Liste aller existierenden
     * Elemente angeboten werden soll, zu denen ein Pfad angelegt werden soll.
     *
     * @param elementClass
     */
    public Collection<SimpleMetaPath> getCreatableMetaPaths(final Class<? extends ModelElement> elementClass) {
        Collection<SimpleMetaPath> creatablePaths = elementClassToCreatableMetaPaths.get(elementClass);
        return creatablePaths == null ? ImmutableList.of() : creatablePaths;
    }

    /**
     * Liefert alle anlegbaren MetaPfade die zwischen den übergebenen Elementarten im Metamodell definiert sind. Alle diese Pfade werden als
     * verbindbare Pfade im Kontextmenü angeboten, wenn das zuletzt markierte Element und ein anderes markiertes Element zuwesiungskompatibel zu den
     * übergebenen Elementklassen sind. Diese Pfade werden dann mit allen Zwischenelementen zwischen den beiden Elementen angelegt.
     *
     * @param elementClass1
     * @param elementClass2
     */
    public Collection<SimpleMetaPath> getCreatableMetaPaths(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        ImmutableList.Builder<SimpleMetaPath> creatableMetaPaths = ImmutableList.builder();
        for (SimpleMetaPath metaPath : getCreatableMetaPaths(elementClass1)) {
            Class<? extends ModelElement> endClass = metaPath.getEndClass();
            if (endClass.isAssignableFrom(elementClass2)) {
                creatableMetaPaths.add(metaPath);
            }
        }
        return creatableMetaPaths.build();
    }

    /**
     * Liefert für alle Elementklassen, bei denen der Name verbundendener Elemente in der Grafik in Klammern unter der eigentlichen Elementart
     * angezeigt werden soll, den MetaPfad zu den anzuzeigenden verbundenen Elementen.
     *
     * @return
     */
    public AbstractMetaPath getNameExtensionPath(final Class<? extends ModelElement> elementClass) {
        return elementClassToNameExtensionPath.get(elementClass);
    }

    /**
     * @return Iterable über alle Klassen, bei denen verbundene Elemente an den Namen angehängt werden sollen
     */
    public Collection<Class<? extends ModelElement>> getElementClassesWithNameExtensionPath() {
        return elementClassesWithNameExtensions;
    }

    // die weiteren zum Metamodell gehörigen Definitionen

    public Collection<Class<? extends ModelElement>> getCopyDependencies(final Class<? extends ModelElement> elementClass) {
        return copyDependencies.get(elementClass);
    }

    public boolean avoidDuplicates(final Class<? extends ModelElement> elementClass) {
        return copyDependencies.avoidDuplicates(elementClass);
    }

    /**
     * @return {@link MetaPathDefinition} des Metamodells
     */
    public final MetaPathDefinition getMetaPathsDefinition() {
        return metaPathsDefinition;
    }

    /**
     * @return {@link GraphViewDefinition} des Metamodells
     */
    public final GraphViewDefinition getGraphViewDefinition() {
        return graphViewDefinition;
    }

    /**
     * @return {@link AnalysesDefinition} des Metamodells
     */
    public final AnalysesDefinition getAnalysesDefinition() {
        return analysesDefinition;
    }

    /**
     * Instanziiert die übergebene Klasse mit einem Kontruktor, der als Parmeter eine Instanz der Klasse {@link MetaModel} erwartet. Geht
     * dabei irgendwas schief, versucht sie es mit dem leeren Constructor. Geht dabei auch etwas schief, dann kommt ohne Exception <code>null</code>
     * zurück.
     *
     * @param metaModelDependentClass
     * @return
     */
    private <T> T getInstance(final Class<? extends T> metaModelDependentClass) {
        T instance = null;
        try {
            Constructor<? extends T> constructor = metaModelDependentClass.getConstructor(MetaModel.class);
            instance = constructor.newInstance(this);
        } catch (Exception e) {
            try {
                instance = metaModelDependentClass.newInstance();
            } catch (Exception e2) {
            }
        }
        return instance;
    }

    /**
     * Liefert die Actions, die für das spezielle Metamodell in das Extras-Menü eingetragen werden sollen
     *
     * @param plugins
     * @return
     */
    public final Action[] getExtrasActions(final boolean plugins) {
        return extrasActionsDefinition.getActions(plugins);
    }

    // Anlegen neuer Elemente //

    /**
     * Erzeugt eine neue Instanz eines Modellelementes.<br>
     * Loggt eine Fehlermedung, wenn Objekt nicht erzeugt werden konnte.
     *
     * @param elementClass Unterklasse von <code>ModelElement</code>
     * @return
     */
    public final <T extends ModelElement> T createElement(final Class<? extends T> elementClass) {
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
    public final <T extends ModelElement> T createElement(final Class<? extends T> elementClass, final boolean log) {
        return modelElementInstanceCreator.createElement(elementClass, log);
    }

    /**
     * Erzeugt eine neues ModelElement der gleichen Art wie das übergebene
     *
     * @return neues ModelElement der übergebenen Art oder im Fehlerfall <code>null</code>
     * @param me ModelElement, das die Klasse des neu zu erzeugenden Elementes vorgibt
     * @param log wenn <code>true</code> wird ein eventuell auftretender Fehler geloggt
     * @return neues ModelElement oder <code>null</code>
     */
    public final ModelElement createElement(final ModelElement me, final boolean log) {
        return createElement(me.getClass(), log);
    }

}
