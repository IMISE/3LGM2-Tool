package de.imise.tool3lgm.graphtools.metamodel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

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
        addRemovedEdgeClasses();
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

    private CopyDependencies copyDependencies;

    public final CopyDependencies getCopyDependencies() {
        if (copyDependencies == null) {
            copyDependencies = createCopyDependencies();
        }
        return copyDependencies;
    }

    /**
     * Erzeugt standardmäßig eine 'leere' CopyDependency
     *
     * @return 'leere' CopyDependency
     */
    protected CopyDependencies createCopyDependencies() {
        return new CopyDependencies();
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

    ////////////
    // Node //
    ////////////

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
     * Liefert alle Elementklassen, die nur im Baum angezeigt werden sollen, wenn die Option {@link BooleanProperty#OPTION_ENABLE_EXPERT_MODE}
     * auf <code>true</code> gestellt ist.
     * ACHTUNG: hier wird nur mit contains(class) gerpüft -> immer auch die Oberklassen, die versteckt werden sollen reinschreiben
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
    public abstract Set<Class<? extends ModelElement>> getImportableNodes();

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
    private final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToRemovedEdgeClassesForStartClass = ArrayListMultimap.create();
    private final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToRemovedEdgeClassesForEndClass = ArrayListMultimap.create();

    @SafeVarargs
    protected final void addRemovedEdgeClassesForStartClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge>... edgeClasses) {
        elementClassToRemovedEdgeClassesForStartClass.putAll(elementClass, Arrays.asList(edgeClasses));
    }

    @SafeVarargs
    protected final void addRemovedEdgeClassesForEndClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge>... edgeClasses) {
        elementClassToRemovedEdgeClassesForEndClass.putAll(elementClass, Arrays.asList(edgeClasses));
    }

    @SafeVarargs
    protected final void addRemovedEdgeClassesForStartAndEndClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge>... edgeClasses) {
        addRemovedEdgeClassesForStartClass(elementClass, edgeClasses);
        addRemovedEdgeClassesForEndClass(elementClass, edgeClasses);
    }

    /**
     * Diese Funktion können Unterklassen überschreiben und darin dann {@link #addRemovedEdgeClasses(Class, Class...)} aufrufen.
     */
    protected void addRemovedEdgeClasses() {

    }

    /**
     * Liefert die {@link #elementClassToRemovedEdgeClassesForStartClass}
     *
     * @return
     */
    public final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> getElementClassToRemovedEdgeClassesForStartClass() {
        return elementClassToRemovedEdgeClassesForStartClass;
    }

    /**
     * Liefert die {@link #elementClassToRemovedEdgeClassesForEndClass}
     *
     * @return
     */
    public final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> getElementClassToRemovedEdgeClassesForEndClass() {
        return elementClassToRemovedEdgeClassesForEndClass;
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