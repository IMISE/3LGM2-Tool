package de.imise.tool3lgm.graphtools.metamodel;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * Dieses Interface müssen alle {@link MetaModelDefinition} implementieren, die man zum Modellieren im Baukasten einsetzen will.
 * MetaModel-Definitionen, die z.B. nur für den Import gebraucht werden, sollten das nicht tun. Es dient also nur zur Unterscheidung, ob man das
 * definierte Metamodell im Baukasten zum Modellieren anbieten soll oder nicht.
 *
 * @author AXS (7 Jun 2019)
 */
public interface RegularMetaModelDefinition {

    /**
     * Liefert die Map, die von alten Elementklassen auf die neuen mappt. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     *
     * @return
     */
    public Map<String, String> getOldToNewClassNameMap();

    /////////////////////
    // PathsDefinition //
    /////////////////////

    /**
     * Unterklassen können diese Funktion überschreiben und damit eine eigene Definition anlegen.
     *
     * @return
     */
    public Class<? extends MetaPathDefinition> getMetaPathsDefinitionClass();

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    public Class<? extends GraphViewDefinition> getGraphViewDefinitionClass();

    //////////////////////
    // CopyDependencies //
    //////////////////////

    public CopyDependencies getCopyDependencies();

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    public Class<? extends AnalysesDefinition> getAnalysesDefinitionClass();

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    public Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass();

    ////////////
    // Node //
    ////////////

    /** Alle Node der FE als Array */
    public Class<? extends ModelElement>[] getAllDomainLayerNodes();

    /**
     * Alle abstracten Klassen, die im Baum auf der FE auftauchen sollen. Werden hier irgendwelche
     * Unterklassen von ModelElement angegeben, die Oberklassen von einer instanziierbaren Klasse
     * der Ebene sind, dann wird der Klassenknoten der instanziierbaren Klasse im Baum nicht direkt
     * unter den Layer-Knoten gehängt, sondern unter den (oder die) Zwischenknoten mit den Oberklassen.
     */
    public Class<? extends ModelElement>[] getTreeDomainLayerVisibleAbstractNodes();

    /** Alle Node zw. FE und LWE als Array */
    public Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes();

    public Class<? extends ModelElement>[] getAllLogicalLayerNodes();

    /**
     * Alle abstracten Klassen, die im Baum auf der LWE auftauchen sollen. Werden hier irgendwelche
     * Unterklassen von ModelElement angegeben, die Oberklassen von einer instanziierbaren Klasse
     * der Ebene sind, dann wird der Klassenknoten der instanziierbaren Klasse im Baum nicht direkt
     * unter den Layer-Knoten gehängt, sondern unter den (oder die) Zwischenknoten mit den Oberklassen.
     */
    public Class<? extends ModelElement>[] getTreeLogicalLayerVisibleAbstractNodes();

    /** Alle Node zw. LWE und PWE als Array */
    public Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes();

    /** Alle Node der PWE als Array */
    public Class<? extends ModelElement>[] getAllPhysicalLayerNodes();

    /**
     * Alle abstracten Klassen, die im Baum auf der LWE auftauchen sollen. Werden hier irgendwelche
     * Unterklassen von ModelElement angegeben, die Oberklassen von einer instanziierbaren Klasse
     * der Ebene sind, dann wird der Klassenknoten der instanziierbaren Klasse im Baum nicht direkt
     * unter den Layer-Knoten gehängt, sondern unter den (oder die) Zwischenknoten mit den Oberklassen.
     */
    public Class<? extends ModelElement>[] getTreePhysicalLayerVisibleAbstractNodes();

    public Class<? extends ModelElement>[] getAllNodes();

    /**
     * Liefert alle Elementklassen, die nur im Baum angezeigt werden sollen, wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>true</code> gestellt ist.
     * ACHTUNG: hier wird nur mit contains(class) gerpüft -> immer auch die Oberklassen, die versteckt werden sollen reinschreiben
     *
     * @return alle Elementklassen, die nur im ExpertMode im Baum angezeigt werden
     */
    public Set<Class<? extends ModelElement>> getOnlyExpertModeVisibleNodes();

    /**
     * Liefert alle Elementklassen, die nur im ExpertMode ({@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE} = true) angelegt und verändert werden
     * können.
     *
     * @return alle Elementklassen, die nur im ExpertMode geändert werden können
     */
    public Set<Class<? extends ModelElement>> getOnlyExpertModeEditableNodes();

    ////////////
    // Kanten //
    ////////////

    /** Alle Kanten als Array */
    public Class<? extends Edge>[] getAllEdges();

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    public Set<Class<? extends ModelElement>> getImportableNodes();

    /**
     * Liefert die {@link #elementClassToRemovedEdgeClassesForStartClass}
     *
     * @return
     */
    public Multimap<Class<? extends ModelElement>, Class<? extends Edge>> getElementClassToRemovedEdgeClassesForStartClass();

    /**
     * Liefert die {@link #elementClassToRemovedEdgeClassesForEndClass}
     *
     * @return
     */
    public Multimap<Class<? extends ModelElement>, Class<? extends Edge>> getElementClassToRemovedEdgeClassesForEndClass();

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    /** Liefert ein Set aller Elementklassen, bei denen der Name nicht vom Nutzer eingegeben sondern generiert wird. */
    public Set<Class<? extends ModelElement>> getGenerateNameClasses();

}