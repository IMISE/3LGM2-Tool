package de.imise.tool3lgm.graphtools.metamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.consistency.ModelValidatorDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.DefaultGraphViewDefinitionAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.collections.CollectionUtils;

/**
 * @author N.N., AXS
 */
@SuppressWarnings({
        "rawtypes"
})
public abstract class MetaModelDefinition implements Serializable {

    public MetaModelDefinition() {
        putOldToNewClassNames();
    }

    /**
     * Mappt von alten Elementklassen auf die neuen. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     */
    private final Map<String, String> oldToNewClassName = new HashMap<>();

    /**
     * liefert die Map, die von alten Elementklassen auf die neuen mappt. <br>
     * Nach einem Refactoring von Node- oder Kantenklassen muss man in diese Map jeweils als <code>String</code> als Schlüssel den alten Namen und
     * als Value den neuen Namen des Elementes eintragen, damit die Elemente von alten Modellen noch korrekt
     * eingelesen werden können.
     *
     * @return
     */
    public Map<String, String> getOldToNewClassNameMap() {
        return oldToNewClassName;
    }

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

    /////////////////////
    // PathsDefinition //
    /////////////////////

    /**
     * Unterklassen können diese Funktion überschreiben und damit eine eigene Definition anlegen.
     *
     * @return
     */
    public Class<? extends MetaPathDefinition> getMetaPathsDefinitionClass() {
        return MetaPathDefinition.class;
    }

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    public Class<? extends GraphViewDefinition> getGraphViewDefinitionClass() {
        return DefaultGraphViewDefinitionAdapter.class;
    }

    //////////////////////
    // CopyDependencies //
    //////////////////////

    /**
     * Unterklassen können diese Funktion überschreiben und damit eine eigene Definition anlegen.
     *
     * @return
     */
    public Class<? extends CopyDependencies> getCopyDependenciesClass() {
        return CopyDependencies.class;
    }

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    public Class<? extends AnalysesDefinition> getAnalysesDefinitionClass() {
        return AnalysesDefinition.class;
    }

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    public Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass() {
        return ExtrasActionsDefinition.class;
    }

    //////////////////////////////
    // ModelValidatorDefinition //
    //////////////////////////////

    public Class<? extends ModelValidatorDefinition> getModelValidatorDefinitionClass() {
        return ModelValidatorDefinition.class;
    }

    ///////////
    // Nodes //
    ///////////

    /** Alle Node der FE als Array */
    public abstract Class<? extends ModelElement>[] getAllDomainLayerNodes();

    /**
     * Alle abstracten Klassen, die im Baum auf der FE auftauchen sollen. Werden hier irgendwelche
     * Unterklassen von ModelElement angegeben, die Oberklassen von einer instanziierbaren Klasse
     * der Ebene sind, dann wird der Klassenknoten der instanziierbaren Klasse im Baum nicht direkt
     * unter den Layer-Knoten gehängt, sondern unter den (oder die) Zwischenknoten mit den Oberklassen.
     */
    public Class<? extends ModelElement>[] getTreeDomainLayerVisibleAbstractNodes() {
        return MetaModel.EMPTY_ELEMENT_CLASS_ARRAY;
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
        return MetaModel.EMPTY_ELEMENT_CLASS_ARRAY;
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
    public Class<? extends ModelElement>[] getTreePhysicalLayerVisibleAbstractNodes() {
        return MetaModel.EMPTY_ELEMENT_CLASS_ARRAY;
    }

    private Class[] allNodes = null;

    @SuppressWarnings("unchecked")
    public final Class<? extends ModelElement>[] getAllNodes() {
        //muss lazy initialisiert werden, um ExceptionInInitializerError zu verhindern
        if (allNodes == null) {
            allNodes = CollectionUtils.joinArrays(getAllDomainLayerNodes(), getAllInterDomainLogicalLayerNodes(), getAllLogicalLayerNodes(), getAllInterLogicalPhysicalLayerNodes(), getAllPhysicalLayerNodes());
        }
        return allNodes;
    }

    /**
     * Returns all element classes which are not in the set {@link #getPureTemplateSourceNodes()}
     * but also only visible if the expert mode is enabled.
     * ({@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE} = true)
     * ACHTUNG: hier wird nur mit contains(class) gerpüft -> immer auch die Oberklassen, die versteckt werden sollen reinschreiben
     *
     * @return alle Elementklassen, die nur im ExpertMode im Baum angezeigt werden
     */
    public Set<Class<? extends ModelElement>> getOnlyExpertModeVisibleNodes() {
        return ImmutableSet.of();
    }

    /**
     * Returns all element classes which can be only created in a model by copy existing
     * elements from a template to a model.
     * You can make this elements also visible in the model explorer and changeable by
     * enabling the ExpertMode ({@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE} = true).
     *
     * @return all element classes form templates which can only changed via model browser
     *         in expert mode
     */
    public Set<Class<? extends ModelElement>> getPureTemplateSourceNodes() {
        return ImmutableSet.of();
    }

    ///////////
    // Edges //
    ///////////

    /** Alle Kanten als Array */
    public abstract Class<? extends Edge>[] getAllEdges();

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    public abstract Set<Class<? extends ModelElement>> getImportableNodes();

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
     * Adapter, der alle abstrakten Funktionen mit leeren Arrays und Sets überschreibt.
     *
     * @author AXS (6 Jun 2019)
     */
    @SuppressWarnings("unchecked")
    public static class DefaultMetaModelDefinitionAdapter extends MetaModelDefinition {

        @Override
        public Class<? extends ModelElement>[] getAllDomainLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllLogicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllPhysicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends Edge>[] getAllEdges() {
            return new Class[0];
        }

        @Override
        public Set<Class<? extends ModelElement>> getImportableNodes() {
            return ImmutableSet.of();
        }

        @Override
        public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
            return ImmutableSet.of();
        }

    }

}