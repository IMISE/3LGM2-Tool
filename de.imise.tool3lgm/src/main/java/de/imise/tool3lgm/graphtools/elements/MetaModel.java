package de.imise.tool3lgm.graphtools.elements;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewDefinition;
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
     * Nach einem Refactoring von Knoten- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     */
    protected abstract Map<String, String> getOldToNewClassName();

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    public abstract GraphViewDefinition getGraphViewDefinition();

    ////////////
    // Knoten //
    ////////////

    /** Alle Knoten der FE als Array */
    public abstract Class[] getAllDomainLayerNodes();

    /** Alle Knoten, die im Baum sichtbar auf der FE sichtbar sind */
    public abstract Class[] getTreeDomainLayerNodes();

    /** Alle Knotenklassen der FE, die man im Baum neu erzeugen kann */
    public abstract Class[] getTreeCreatableDomainLayerNodes();

    /** Alle Knoten zw. FE und LWE als Array */
    public abstract Class[] getAllInterDomainLogicalLayerNodes();

    public abstract Class[] getAllLogicalLayerNodes();

    public abstract Class[] getTreeLogicalLayerNodes();

    public abstract Class[] getTreeCreatableLogicalLayerNodes();

    /** Alle Knoten zw. LWE und PWE als Array */
    public abstract Class[] getAllInterLogicalPhysicalLayerNodes();

    /** Alle Knoten der PWE als Array */
    public abstract Class[] getAllPhysicalLayerNodes();

    /** Alle Knoten der PWE im Baum als Array */
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
    public abstract Class<? extends Kante>[] getAllDomainLayerEdges();

    /** Alle Kanten zw. FE und LWE als Array */
    public abstract Class<? extends Kante>[] getAllInterDomainLogicalLayerEdges();

    /** Alle Kanten der LWE als Array */
    public abstract Class<? extends Kante>[] getAllLogicalLayerEdges();

    /** Alle Kanten zw. LWE und PWE als Array */
    public abstract Class<? extends Kante>[] getAllInterLogicalPhysicalLayerEdges();

    /** Alle Kanten der PWE als Array */
    public abstract Class<? extends Kante>[] getAllPhysicalLayerEdges();

    private Class<? extends Kante>[] allEdges = null;

    @SuppressWarnings("unchecked")
    public Class<? extends Kante>[] getAllEdges() {
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
    public abstract Set<Class<? extends Knoten>> getUniqueNodes();

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

    /**
     * Mappt von Elementklassen auf alle Kantenklassen, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben.
     */
    public final Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> getElementClassToSortedEdges() {
        ImmutableMap.Builder<Class<? extends ModelElement>, Set<Class<? extends Kante>>> mapBuilder = ImmutableMap.builder();
        Iterable<Class<? extends Kante>> sortedEdges = getSortedEdges();
        for (Class<? extends ModelElement> elementClass : getAllNodes()) {
            ImmutableSet.Builder<Class<? extends Kante>> sortedEdgesForElementClass = new ImmutableSet.Builder<>();
            for (Class<? extends Kante> edgeClass : sortedEdges) {
                if (Kante.isStartClass(edgeClass, elementClass)) {
                    sortedEdgesForElementClass.add(edgeClass);
                }
            }
            ImmutableSet<Class<? extends Kante>> sortedEdgesSet = sortedEdgesForElementClass.build();
            if (!sortedEdgesSet.isEmpty()) {
                mapBuilder.put(elementClass, sortedEdgesForElementClass.build());
            }
        }
        return mapBuilder.build();
    }

    private Set<Class<? extends Kante>> getSortedEdges() {
        ImmutableSet.Builder<Class<? extends Kante>> sortedEdges = new ImmutableSet.Builder<>();
        for (Class<? extends Kante> edgeClass : getAllEdges()) {
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
    public final Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> getElementClassToSortedEdges(final int i) {
        Set<Class<? extends Kante>> processSortedEdgeClasses = ImmutableSet.<Class<? extends Kante>> of(PrzAufVerbindung.class);
        Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> elementClassToSortedEdges = ImmutableMap.<Class<? extends ModelElement>, Set<Class<? extends Kante>>> of(Prozess.class, processSortedEdgeClasses);
        return elementClassToSortedEdges;
    }

    /**
     * Liste aller Kantenklassen, die eigentlich 2 gerichtete Assoziationen im Metamodell sein müssten, aber aus Unwissenheit beim Entwurf des
     * Metamodells fehlerhafterweise in eine Assoziation verpackt wurden, bei denen die Richtung der Kante
     * (Doppelkante.FORWARD, Doppelkante.BACKWARD, Doppelkante.DOUBLE) die Bedeutung angibt. Nur wegen den 4 braucht man den ganzen
     * Doppelkanten-Richtungsquatsch. Wenn sie grafisch dargestellt werden, dann werden sie als eine Kante dargestellt werden, die
     * je nach Bedeutung eine der Richtungen oder beide als Pfeile darstellt. Hier wurde also das Model misbraucht, um im View diese Assoziationen
     * zusammenzufassen.
     */
    public abstract Set<Class<? extends Kante>> getDoubleMeaningEdgeClasses();

    /**
     * Menge aller Kantenklassen, die nur in Vorwärtsrichtung verbunden werden und somit immer nur in dieser Richtung in
     * der Grafik dargestelt werden.
     */
    public abstract Set<Class<? extends Kante>> getForwardConnectedEdgeClasses();

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    /** Array aller Pfade, die in der grafischen Ansicht als Interebenenbeziehungen dargestellt werden. */
    public abstract MetaPath[] getInterLayerConnectedElementPathes();

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

}