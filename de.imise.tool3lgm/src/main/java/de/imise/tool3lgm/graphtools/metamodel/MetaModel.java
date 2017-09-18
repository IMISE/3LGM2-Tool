package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.Edge.isStartClass;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Prozess;
import de.imise.util.collections.CollectionUtils;

/**
 * @author N.N., AXS
 */
@SuppressWarnings({
        "rawtypes"
})
public abstract class MetaModel {

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     */
    protected abstract Map<String, String> getOldToNewClassName();

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    public abstract GraphViewDefinition getGraphViewDefinition();

    //////////////////////
    // CopyDependencies //
    //////////////////////

    public abstract CopyDependencies getCopyDependencies();

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
            } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
                analysisDefinition = new AnalysisDefinition() {
                };
            }
        }
        return analysisDefinition;
    }

    ////////////
    // Node //
    ////////////

    /** Alle Node der FE als Array */
    public abstract Class[] getAllDomainLayerNodes();

    /** Alle Node, die im Baum sichtbar auf der FE sichtbar sind */
    public abstract Class[] getTreeDomainLayerNodes();

    /** Alle Knotenklassen der FE, die man im Baum neu erzeugen kann */
    public abstract Class[] getTreeCreatableDomainLayerNodes();

    /** Alle Node zw. FE und LWE als Array */
    public abstract Class[] getAllInterDomainLogicalLayerNodes();

    public abstract Class[] getAllLogicalLayerNodes();

    public abstract Class[] getTreeLogicalLayerNodes();

    public abstract Class[] getTreeCreatableLogicalLayerNodes();

    /** Alle Node zw. LWE und PWE als Array */
    public abstract Class[] getAllInterLogicalPhysicalLayerNodes();

    /** Alle Node der PWE als Array */
    public abstract Class[] getAllPhysicalLayerNodes();

    /** Alle Node der PWE im Baum als Array */
    public abstract Class[] getTreePhysicalLayerNodes();

    public abstract Class[] getTreeCreatablePhysicalLayerNodes();

    private Class[] allNodes = null;

    @SuppressWarnings("unchecked")
    public Class<? extends ModelElement>[] getAllNodes() {
        //muss lazy initialisiert werden, um ExceptionInInitializerError zu verhindern
        if (allNodes == null) {
            allNodes = CollectionUtils.joinArrays(getAllDomainLayerNodes(), getAllInterDomainLogicalLayerNodes(), getAllLogicalLayerNodes(), getAllInterLogicalPhysicalLayerNodes(), getAllPhysicalLayerNodes());
        }
        return allNodes;
    }

    ////////////
    // Kanten //
    ////////////

    /** Alle Kanten der FE als Array */
    public abstract Class<? extends Edge>[] getAllDomainLayerEdges();

    /** Alle Kanten zw. FE und LWE als Array */
    public abstract Class<? extends Edge>[] getAllInterDomainLogicalLayerEdges();

    /** Alle Kanten der LWE als Array */
    public abstract Class<? extends Edge>[] getAllLogicalLayerEdges();

    /** Alle Kanten zw. LWE und PWE als Array */
    public abstract Class<? extends Edge>[] getAllInterLogicalPhysicalLayerEdges();

    /** Alle Kanten der PWE als Array */
    public abstract Class<? extends Edge>[] getAllPhysicalLayerEdges();

    private Class<? extends Edge>[] allEdges = null;

    @SuppressWarnings("unchecked")
    public Class<? extends Edge>[] getAllEdges() {
        //muss lazy initialisiert werden, um ExceptionInInitializerError zu verhindern
        if (allEdges == null) {
            allEdges = CollectionUtils.joinArrays(getAllDomainLayerEdges(), getAllInterDomainLogicalLayerEdges(), getAllLogicalLayerEdges(), getAllInterLogicalPhysicalLayerEdges(), getAllPhysicalLayerEdges());
        }
        return allEdges;
    }

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    public abstract Class[] getImportableNodes();

    /** Alle Knotenklassen, die in jedem Teilmodell vorkommen, also nicht in jedem Teilmodell einen eigenen Container besitzen. */
    public abstract Set<Class<? extends Node>> getUniqueNodes();

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
            if (SortedEdge.class.isAssignableFrom(edgeClass)) {
                sortedEdges.add(edgeClass);
            }
        }
        return sortedEdges.build();
    }

    /**
     * Mappt von Elementklassen auf alle Kantenklassen, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben.
     */
    public final Map<Class<? extends ModelElement>, Set<Class<? extends Edge>>> getElementClassToSortedEdges(final int i) {
        Set<Class<? extends Edge>> processSortedEdgeClasses = ImmutableSet.<Class<? extends Edge>> of(PrzAufVerbindung.class);
        Map<Class<? extends ModelElement>, Set<Class<? extends Edge>>> elementClassToSortedEdges = ImmutableMap.<Class<? extends ModelElement>, Set<Class<? extends Edge>>> of(Prozess.class, processSortedEdgeClasses);
        return elementClassToSortedEdges;
    }

    /**
     * Liste aller Kantenklassen, die eigentlich 2 gerichtete Assoziationen im Metamodell sein müssten, aber aus Unwissenheit beim Entwurf des
     * Metamodells fehlerhafterweise in eine Assoziation verpackt wurden, bei denen die Richtung der Edge
     * (Doppelkante.FORWARD, Doppelkante.BACKWARD, Doppelkante.DOUBLE) die Bedeutung angibt. Nur wegen den 4 braucht man den ganzen
     * Doppelkanten-Richtungsquatsch. Wenn sie grafisch dargestellt werden, dann werden sie als eine Edge dargestellt werden, die
     * je nach Bedeutung eine der Richtungen oder beide als Pfeile darstellt. Hier wurde also das Model misbraucht, um im View diese Assoziationen
     * zusammenzufassen.
     */
    public abstract Set<Class<? extends Edge>> getDoubleMeaningEdgeClasses();

    /**
     * Menge aller Kantenklassen, die nur in Vorwärtsrichtung verbunden werden und somit immer nur in dieser Richtung in
     * der Grafik dargestelt werden.
     */
    public abstract Set<Class<? extends Edge>> getForwardConnectedEdgeClasses();

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    //    /**
    //     * Um Festzustellen, ob ein gegebener Klassenname bereits voll qulaifiziert ist, wird geschaut, ob der Klassenname mit
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

    public abstract String getResourceBaseName();

}