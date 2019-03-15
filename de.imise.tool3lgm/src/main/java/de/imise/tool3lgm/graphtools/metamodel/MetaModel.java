package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartClass;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.collections.CollectionUtils;

/**
 * @author N.N., AXS
 */
@SuppressWarnings({
        "rawtypes"
})
public abstract class MetaModel {

    public MetaModel() {
        putOldToNewClassNames();
        initCreatableMetaPaths();
    }

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     */
    private final Map<String, String> oldToNewClassName = new HashMap<>();

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     */
    protected void putOldToNewClassNames() {
    }

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     *
     * @param oldName
     * @param newName
     */
    protected final void putOldToNewClassName(final String oldName, final String newName) {
        oldToNewClassName.put(oldName, newName);
    }

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

    /////////////////////
    // PathsDefinition //
    /////////////////////

    private MetaPathDefinition pathsDefinition;

    public final MetaPathDefinition getPathsDefinition() {
        //immer lazy initialisieren, weil die PathsDefinition die kompeltten ModelConstants braucht, um sich selbst
        //zu initialisieren und die ModelConstants aber dieses Metamodel initialisieren -> wenn nicht lazy => InitializationException
        if (pathsDefinition == null) {
            pathsDefinition = createPathsDefinition();
        }
        return pathsDefinition;
    }

    /**
     * Unterklassen können diese Funktion überschreiben und damit eine eigene Definition anlegen.
     *
     * @return
     */
    protected MetaPathDefinition createPathsDefinition() {
        return new MetaPathDefinition() {
        };
    }

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    private GraphViewDefinition graphViewDefinition;

    public final GraphViewDefinition getGraphViewDefinition() {
        if (graphViewDefinition == null) {
            graphViewDefinition = createGraphViewDefinition();
        }
        return graphViewDefinition;
    }

    protected abstract GraphViewDefinition createGraphViewDefinition();

    //////////////////////
    // CopyDependencies //
    //////////////////////

    private CopyDependencies copyDependencies;

    public final CopyDependencies getCopyDependencies() {
        if (copyDependencies == null) {
            copyDependencies = createCopyDependencies();
        }
        return copyDependencies;
    }

    protected abstract CopyDependencies createCopyDependencies();

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    protected Class<? extends AnalysisDefinition> getAnalysisDefinitionClass() {
        return null;
    }

    private AnalysisDefinition analysisDefinition;

    public final AnalysisDefinition getAnalysisDefinition() {
        //der lazy-init ist notwendig, da es sonst zu einem InitializingError kommt, da die ModelConstants noch nicht durchinitialisiert sind
        if (analysisDefinition == null) {
            try {
                analysisDefinition = getAnalysisDefinitionClass().newInstance();
            } catch (Exception e) {
                analysisDefinition = new AnalysisDefinition() {
                };
            }
        }
        return analysisDefinition;
    }

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    protected Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass() {
        return null;
    }

    public final Action[] getExtrasActions(final boolean plugins) {
        try {
            ExtrasActionsDefinition extrasActionsDefinition = getExtrasActionsDefinitionClass().newInstance();
            return plugins ? extrasActionsDefinition.getPluginActions() : extrasActionsDefinition.getActions();
        } catch (Exception e) {
        }
        return new Action[0];
    }

    ////////////
    // Node //
    ////////////

    /** Alle Node der FE als Array */
    protected abstract Class<? extends ModelElement>[] getAllDomainLayerNodes();

    /**
     * Alle abstracten Klassen, die im Baum auf der FE auftauchen sollen. Werden hier irgendwelche
     * Unterklassen von ModelElement angegeben, die Oberklassen von einer instanziierbaren Klasse
     * der Ebene sind, dann wird der Klassenknoten der instanziierbaren Klasse im Baum nicht direkt
     * unter den Layer-Knoten gehängt, sondern unter den (oder die) Zwischenknoten mit den Oberklassen.
     */
    public Class<? extends ModelElement>[] getTreeDomainLayerVisibleAbstractNodes() {
        return ModelConstants.EMPTY_ELEMENT_CLASS_ARRAY;
    }

    /** Alle Node zw. FE und LWE als Array */
    public abstract Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes();

    public abstract Class<? extends ModelElement>[] getAllLogicalLayerNodes();

    /**
     * Alle abstracten Klassen, die im Baum auf der LWE auftauchen sollen. Werden hier irgendwelche
     * Unterklassen von ModelElement angegeben, die Oberklassen von einer instanziierbaren Klasse
     * der Ebene sind, dann wird der Klassenknoten der instanziierbaren Klasse im Baum nicht direkt
     * unter den Layer-Knoten gehängt, sondern unter den (oder die) Zwischenknoten mit den Oberklassen.
     */
    public Class<? extends ModelElement>[] getTreeLogicalLayerVisibleAbstractNodes() {
        return ModelConstants.EMPTY_ELEMENT_CLASS_ARRAY;
    }

    /** Alle Node zw. LWE und PWE als Array */
    public abstract Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes();

    /** Alle Node der PWE als Array */
    public abstract Class<? extends ModelElement>[] getAllPhysicalLayerNodes();

    /**
     * Alle abstracten Klassen, die im Baum auf der LWE auftauchen sollen. Werden hier irgendwelche
     * Unterklassen von ModelElement angegeben, die Oberklassen von einer instanziierbaren Klasse
     * der Ebene sind, dann wird der Klassenknoten der instanziierbaren Klasse im Baum nicht direkt
     * unter den Layer-Knoten gehängt, sondern unter den (oder die) Zwischenknoten mit den Oberklassen.
     */
    public Class<? extends ModelElement>[] getTreePhsicalLayerVisibleAbstractNodes() {
        return ModelConstants.EMPTY_ELEMENT_CLASS_ARRAY;
    }

    private Class[] allNodes = null;

    @SuppressWarnings("unchecked")
    public Class<? extends ModelElement>[] getAllNodes() {
        //muss lazy initialisiert werden, um ExceptionInInitializerError zu verhindern
        if (allNodes == null) {
            allNodes = CollectionUtils.joinArrays(getAllDomainLayerNodes(), getAllInterDomainLogicalLayerNodes(), getAllLogicalLayerNodes(), getAllInterLogicalPhysicalLayerNodes(), getAllPhysicalLayerNodes());
        }
        return allNodes;
    }

    /**
     * Liefert alle Elementklassen, die nur im Baum angezeigt werden sollen, wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>true</code> gestellt ist.
     *
     * @return alle Elementklassen, die nur im ExpertMode im Baum angezeigt werden
     */
    public Set<Class<? extends ModelElement>> getOnlyExpertModeVisibleNodes() {
        return ImmutableSet.of();
    }

    /**
     * Liefert alle Elementklassen, die nur im ExpertMode ({@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE} = true) angelegt und verändert werden
     * können.
     *
     * @return alle Elementklassen, die nur im ExpertMode geändert werden können
     */
    public Set<Class<? extends ModelElement>> getOnlyExpertModeEditableNodes() {
        return ImmutableSet.of();
    }

    ////////////
    // Kanten //
    ////////////

    /** Alle Kanten als Array */
    public abstract Class<? extends Edge>[] getAllEdges();

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    public abstract Class<? extends ModelElement>[] getImportableNodes();

    /**
     * Alle Knotenklassen, die in jedem Teilmodell vorkommen, also nicht in jedem Teilmodell einen eigenen Container besitzen.
     * Das sind alle nicht-abstrakten Knotenklassen (nicht Kante), die in der GraphViewDefinition nicht als paintable eingetragen sind.
     */
    public final Set<Class<? extends Node>> getUniqueNodes() {
        ImmutableSet.Builder<Class<? extends Node>> uniqueNodes = new ImmutableSet.Builder<>();
        for (Class<? extends ModelElement> elementClass : getAllNodes()) {
            //keine abstrakten Klassen zu diesem Set hinzufügen
            if (!Modifier.isAbstract(elementClass.getModifiers())) {
                //nur Knotenklassen nehmen (dort können auch Assoziationsklassen drin sein)
                if (Node.class.isAssignableFrom(elementClass)) {
                    if (!ModelConstants.hasLayout(elementClass)) {
                        uniqueNodes.add(elementClass.asSubclass(Node.class));
                    }
                }
            }
        }
        return uniqueNodes.build();
    }

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

    /**
     * Mappt von Elementklassen auf alle Kantenklassen, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben. Elementklasse ohne wenigestens eine solche Edge werden hier nicht eingtragen. D.h. es kommt <code>null</code> zurück, wenn
     * man nach solcher Elementklasse in der Map sucht und kein leeres Set.
     */
    public final Map<Class<? extends ModelElement>, Set<Class<? extends Edge>>> getElementClassToSortedEdges() {
        ImmutableMap.Builder<Class<? extends ModelElement>, Set<Class<? extends Edge>>> mapBuilder = ImmutableMap.builder();
        Iterable<Class<? extends Edge>> sortedEdges = getSortedEdges();
        for (Class<? extends ModelElement> elementClass : getAllNodes()) {
            ImmutableSet.Builder<Class<? extends Edge>> sortedEdgesForElementClass = new ImmutableSet.Builder<>();
            for (Class<? extends Edge> edgeClass : sortedEdges) {
                if (isStartClass(edgeClass, elementClass)) {
                    sortedEdgesForElementClass.add(edgeClass);
                }
            }
            ImmutableSet<Class<? extends Edge>> sortedEdgesSet = sortedEdgesForElementClass.build();
            //Elementklasse nur eintragen, wenn es wenigstens eine Edge gibt, bei der die Reihenfolge relevant ist
            if (!sortedEdgesSet.isEmpty()) {
                mapBuilder.put(elementClass, sortedEdgesForElementClass.build());
            }
        }
        return mapBuilder.build();
    }

    private Set<Class<? extends Edge>> getSortedEdges() {
        ImmutableSet.Builder<Class<? extends Edge>> sortedEdges = new ImmutableSet.Builder<>();
        for (Class<? extends Edge> edgeClass : getAllEdges()) {
            if (MultipleEdge.class.isAssignableFrom(edgeClass)) {
                sortedEdges.add(edgeClass);
            }
        }
        return sortedEdges.build();
    }

    /**
     * Liefert für die übergebene Kantenklasse den MetaPfad, über den die verbindbaren Elemente ebenfalls bereits verbunden sein müssen.
     * Dieser Mechanismus ist dafür gedacht, verbindbare Elemente einzuschränken auf bestimmte Elemente.
     *
     * @param edgeClass
     * @return
     */
    public SimpleMetaPath getConditionPath(final Class<? extends Edge> edgeClass) {
        //aus Performancegründen sollte hier keine Map zum Einsatz kommen. Es wird für die allerwenigsten Kanten einen solchen Pfad geben
        //und Unterklasse sollten das einfach über eine if-then-Abfrage regeln
        return null;
    }

    private static final Iterable<SimpleMetaPath> EMPTY_SIMPLE_META_PATH_ITERABLE = ImmutableList.of();

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
     *
     * @param instanciationEdgeClass
     * @return
     */
    public Iterable<SimpleMetaPath> getInstanciableMetaPath(final Class<? extends InstanciationEdge> instanciationEdgeClass) {
        return EMPTY_SIMPLE_META_PATH_ITERABLE;
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    //    /**
    //     * Um Festzustellen, ob ein gegebener Klassenname bereits voll qualifiziert ist, wird geschaut, ob der Klassenname mit
    //     * diesem Prefix beginnt. Ein Metamodell dessen Element-Klassen außerhalb von "de.imise.tool3lgm." liegen, müsste über
    //     * diese Funktion den tatsächlichen Prefix ausgeben. Da das aber in absehbarer Zeit nicht passieren wird, ist diese
    //     * Funktion hier ertsmal final.
    //     *
    //     * @return
    //     */
    //    public final String getFullQualifiedClassNamePrefix() {
    //        return "de.imise.tool3lgm.";
    //    }
    //

    /** Liefert ein Set aller Elementklassen, bei denen der Name nicht vom Nutzer eingegeben sondern generiert wird. */
    public abstract Set<Class<? extends ModelElement>> getGenerateNameClasses();

    /**
     * @return Liefert eine Sammlung aller {@link SimpleMetaPath}, die man zwischen 2 Elementen anlegen kann, wobei die Zwischenelemente ebenfalls neu
     *         angelegt werden. Diese Pfade werden im Kontextmenü bei Mehrfachselektion oder Einfachselektion angeboten.
     */
    protected abstract Collection<SimpleMetaPath> getCreatablePaths();

    private final Multimap<Class<? extends ModelElement>, SimpleMetaPath> elementClassToCreatableMetaPaths = ArrayListMultimap.create();

    private final void initCreatableMetaPaths() {
        Collection<SimpleMetaPath> creatablePaths = getCreatablePaths();
        if (creatablePaths != null) {
            for (SimpleMetaPath metaPath : creatablePaths) {
                elementClassToCreatableMetaPaths.put(metaPath.getStartClass(), metaPath);
                elementClassToCreatableMetaPaths.put(metaPath.getEndClass(), metaPath.getOtherDirection());
            }
        }
    }

    /**
     * Liefert alle anlegbaren MetaPfade die für die übergebene Elementart im Metamodell definiert sind.
     *
     * @param elementClass
     */
    public Collection<SimpleMetaPath> getCreatableMetaPaths(final Class<? extends ModelElement> elementClass) {
        Collection<SimpleMetaPath> creatablePaths = elementClassToCreatableMetaPaths.get(elementClass);
        return creatablePaths == null ? ImmutableList.of() : creatablePaths;
    }

}