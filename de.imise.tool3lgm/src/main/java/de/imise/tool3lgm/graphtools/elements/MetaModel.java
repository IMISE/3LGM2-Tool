package de.imise.tool3lgm.graphtools.elements;

import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewDefinition;

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

    ////////////
    // Kanten //
    ////////////

    /** Alle Kanten der FE als Array */
    public abstract Class[] getAllDomainLayerEdges();

    /** Alle Kanten zw. FE und LWE als Array */
    public abstract Class[] getAllInterDomainLogicalLayerEdges();

    /** Alle Kanten der LWE als Array */
    public abstract Class[] getAllLogicalLayerEdges();

    /** Alle Kanten zw. LWE und PWE als Array */
    public abstract Class[] getAllInterLogicalPhysicalLayerEdges();

    /** Alle Kanten der PWE als Array */
    public abstract Class[] getAllPhysicalLayerEdges();

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
    public abstract Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> getElementClassToOrderedEdges();

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