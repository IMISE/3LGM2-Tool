package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.STANDARD_ERROR_INT_VALUE;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.metamodel.elements.SubordinationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.ReflectionUtils;

/**
 * Encapsulates all static functions for an metamodel that is independent
 * from a specific metamodel instance.
 *
 * @author AXS (02.04.2020)
 */
public abstract class CoreMetaModel implements MetaModelSpecific {

    /**
     * Metamodel context of this metamodel
     */
    private final MetaModelContext metaModelContext;

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
    protected static final Set<Class<? extends ModelElement>> META_MODEL_INDEPENDENT_MODEL_ELEMENT_TYPES = ImmutableSet.of(Bendpoint.class, Textfield.class);

    /**
     * @param metaModelContext
     */
    public CoreMetaModel(final MetaModelContext metaModelContext) {
        this.metaModelContext = metaModelContext;
    }

    @Override
    public MetaModelContext getMetaModelContext() {
        return metaModelContext;
    }

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return metaModelContext.getMetaModelDefinitionClass();
    }

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

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
     * @param edgeClass
     * @return
     */
    public static final boolean isHasPartEdge(final Class<? extends Edge> edgeClass) {
        return HasPartEdge.class.isAssignableFrom(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final boolean isSubordination(final Class<? extends Edge> edgeClass) {
        return SubordinationEdge.class.isAssignableFrom(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final boolean isComposition(final Class<? extends Edge> edgeClass) {
        return CompositionEdge.class.isAssignableFrom(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final boolean isInstanciation(final Class<? extends Edge> edgeClass) {
        return InstanciationEdge.class.isAssignableFrom(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
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

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Slave-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public static final boolean isSubordinationSlaveType(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isSubordination(edgeClass) && isEndClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Master-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public static final boolean isSubordinationMasterType(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isSubordination(edgeClass) && isStartClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Slave-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public static final boolean isCompositionSlaveType(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isComposition(edgeClass) && isEndClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Master-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public static final boolean isCompositionMasterType(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isComposition(edgeClass) && isStartClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn es sich bei der übergebenen Kantenklasse um eine {@link InstanciationEdge} handelt und die übergebene
     * Elementklasse davon das StartElement - also das instanziierbare Element ist und nicht die Instanz.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isInstanciationMaster(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isInstanciation(edgeClass) && isStartClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn es sich bei der übergebenen Kantenklasse um eine {@link InstanciationEdge} handelt und die übergebene
     * Elementklasse davon das EndElement - also die Instanz ist und nicht das instanziierbare Element.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isInstanciationInstance(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isInstanciation(edgeClass) && isEndClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der Edge oder eine Unterklasse davon ist und die Kantenklasse
     * für diese Elementart nicht entfernt wurde.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isStartClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
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
    public static final boolean isEndClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
        return endClass.isAssignableFrom(elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Startklasse der Edge oder eine Unterklasse oder eine Oberklasse davon ist
     * und die Kantenklasse für diese Elementart nicht entfernt wurde.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isStartClassOrStartClassSuperclass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement> startClass = Edge.getStartClass(edgeClass);
        return startClass.isAssignableFrom(elementClass) || elementClass.isAssignableFrom(startClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Endklasse der Edge oder eine Unterklasse oder eine Oberlasse davon ist
     * und die Kantenklasse für diese Elementart nicht entfernt wurde.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isEndClassOrEndClassSuperclass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
        return endClass.isAssignableFrom(elementClass) || elementClass.isAssignableFrom(endClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Klasse die Start- oder Endklasse der Edge oder eine Ober- oder Unterklasse davon ist.
     *
     * @param edgeClass
     * @param elementClass
     * @return
     */
    public static final boolean isStartOrEndClass(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass) {
        return isStartClass(edgeClass, elementClass) || isEndClass(edgeClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse Elemente der angegebenen Arten miteinander verbindet. Je nach
     * Richtung vorwärst (FORWARD), rückwärts (BACKWARD) oder irgendwie (null)
     *
     * @param edgeClass
     * @param elementClass1
     * @param elementClass2
     * @param direction
     * @return
     */
    public static final boolean isConnecting(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2, final Direction direction) {
        if (direction == Direction.FORWARD) {
            return isConnectingForward(edgeClass, elementClass1, elementClass2);
        }
        if (direction == Direction.BACKWARD) {
            return isConnectingForward(edgeClass, elementClass2, elementClass1);
        }
        return isConnecting(edgeClass, elementClass1, elementClass2);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse Elemente der angegebenen Arten miteinander verbindet.
     *
     * @param edgeClass
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    public static final boolean isConnecting(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
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
    public static final boolean isConnectingForward(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        return isStartClass(edgeClass, startElementClass) && isEndClass(edgeClass, endElementClass);
    }

    /**
     * Wenn die übergebene Elementklasse durch eine Edge der angegebenen Art mit anderen Elementen verbunden sein kann, dann wird die Elementklasse
     * dieser anderen Elemente zurück gegeben. Passen Edge und Elementklasse nicht zusammen, kommt <code>null</code> zurück.
     *
     * @param edgeClass Kantanklasse, von der die andere verbundene Elementklasse zurück gegeben werden soll
     * @param meClass Elementklasse der Edge, deren Gegenelementklasse zurück gegeben werden soll
     * @return die andere Elementklasse der Edge, als die übergebene Klasse oder <code>null</code>, wenn die Klasse gar nicht passt
     */
    public static final Class<? extends ModelElement> getOther(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> meClass) {
        if (isStartClass(edgeClass, meClass)) {
            return Edge.getEndClass(edgeClass);
        }
        if (isEndClass(edgeClass, meClass)) {
            return Edge.getStartClass(edgeClass);
        }
        return null;
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
    public static final EdgeCardinality getCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
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
    public static final int getMinCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
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
    public static final int getMaxCardinality(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        EdgeCardinality cardinality = getCardinality(elementClass, edgeClass);
        return cardinality != null ? cardinality.max() : STANDARD_ERROR_INT_VALUE;
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final EdgeCardinality getForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final EdgeCardinality getBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false).min();
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxForwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, false).max();
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true).min();
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return getCardinality(edgeClass, true).max();
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxMasterToSlaveCardinality(final Class<? extends CompositionEdge> edgeClass) {
        return getMaxForwardCardinality(edgeClass);
    }

}
