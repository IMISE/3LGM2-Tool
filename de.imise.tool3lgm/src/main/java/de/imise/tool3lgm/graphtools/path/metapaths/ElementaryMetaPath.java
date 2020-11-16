package de.imise.tool3lgm.graphtools.path.metapaths;

import static de.imise.util.ReflectionUtils.getMostSpecialClass;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.PropertyTransferEdge;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.ReflectionUtils;

/**
 * Elementarer MetaPfad, aus dem alle anderen MetaPfade aufgebaut sind.
 *
 * @author AXS
 * @create 12.10.2010
 */
public final class ElementaryMetaPath extends MetaPathImpl implements SequenceMetaPath {

    /**
     * Mögliche Arten eines {@link ElementaryMetaPath}.
     */
    public static enum Type {
        /**
         * Regulärer Elementarmetapfad mit einer Startelementklasse, einer
         * Endelementklasse und einer dazwischen liegenden Kantenklasse
         */
        ELEMENT_EDGE_ELEMENT,
        /** Typ für Metapfade, die nur eine einzelne Elementart beschreiben */
        SINGLE_ELEMENT,
        /** Typ für MetaPfade, die bei einer Kantenklasse starten */
        START_WITH_EDGE,
        /** Typ für MetaPfade, die auf einer Kantenklasse enden */
        END_WITH_EDGE;

        public Type getOtherDirection() {
            return this == START_WITH_EDGE ? END_WITH_EDGE : this == END_WITH_EDGE ? START_WITH_EDGE : this;
        }
    }

    private static enum InvalidReason {
        INVALID_START_CLASS,
        INVALID_END_CLASS,
        INVALID_EDGE_CLASS,
        INVALID_DIRECTION,
        INVALID_TYPE,
        INVALID_EDGE_STARTCLASS_AND_METAPATH_STARTCLASS,
        INVALID_EDGE_STARTCLASS_AND_METAPATH_ENDCLASS,
        INVALID_EDGE_ENDCLASS_AND_METAPATH_STARTCLASS,
        INVALID_EDGE_ENDCLASS_AND_METAPATH_ENDCLASS,
    }

    /**
     * Klasse, bei der der Pfad startet. Sie darf in einem validen Pfad niemals
     * <code>null</code> sein und muss eine Ober- oder Unterklasse des
     * jeweiligen Kantenendes sein, auf das sich die Startklasse laut
     * Richtungsangabe bezieht.
     */
    private final Class<? extends ModelElement> startClass;

    /**
     * Klasse, bei der der Pfad endet. Sie darf in einem validen Pfad niemals
     * <code>null</code> sein und muss eine Ober- oder Unterklasse des
     * jeweiligen Kantenendes sein, auf das sich die Endklasse laut
     * Richtungsangabe bezieht.
     */
    private final Class<? extends ModelElement> endClass;

    /**
     * Kantenklasse des Pfades
     */
    private final Class<? extends Edge> edgeClass;

    /**
     * Richtung, die festlegt, wie herum die Kantenklasse gelesen werden soll.
     * Direction.FORWARD bedeutet, dass die {@link #startClass} des Pfades als
     * Startklasse der Kantenklasse verstanden wird und die Endklasse der Kante
     * auch Endklasse des Pfades ist. Bei Direction.BACKWARD wird es andersherum
     * verstanden.
     */
    private final Direction direction;

    /**
     * Richtung, die die Kante ausgehend von der durch die
     * <code>direction</code> festgelegten Richtung haben soll. Dieser Parameter
     * ist nur bei DoubleMeaningEdges relevant.
     */
    private final ConnectionState connectionState;

    /**
     * Typ des MetaPfades. Ist er nach dem setzen der Richtung immer noch
     * <code>null</code>, dann ist der Pfad nicht valide.
     */
    private final Type type;

    /**
     * Liste die als einziges Element <code>this</code> enthält.
     */
    private List<ElementaryMetaPath> elementaryMetaPaths;

    /**
     * @see {@link #isDirected()}
     */
    private final boolean directed;

    /**
     * Ein MetaPfad kann nur aus einer Elementklasse bestehen. Dies beschreibt
     * den Pfad zu allen Elementen dieser Klasse. Intern sind bei diesem Pfad
     * sowohl die Start- als auch die Endklasse auf diese Elementklasse gesetzt
     * und es gibt weder eine Kantenklasse noch eine Richtung.
     *
     * @param metaModel
     * @param elementClass
     */
    ElementaryMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> elementClass) {
        super(metaModel, elementClass, elementClass);
        startClass = elementClass;
        endClass = elementClass;
        edgeClass = null;
        direction = null;
        connectionState = null;
        otherDirection = this;
        directed = false;
        type = Type.SINGLE_ELEMENT;
    }

    /**
     * @param metaModel
     * @param edgeClass
     * @param direction
     */
    ElementaryMetaPath(final MetaModel metaModel, final Class<? extends Edge> edgeClass, final Direction direction) {
        this(metaModel, edgeClass, direction, null);
    }

    /**
     * Legt einen neuen Elementarmetapfad an, bei dem die Start- und Zielklasse
     * denen der Kante in der angegebenen Richtung entsprechen.
     *
     * @param metaModel
     * @param edgeClass
     * @param direction
     */
    ElementaryMetaPath(final MetaModel metaModel, final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState) {
        this(metaModel, (Class<? extends ModelElement>) null, edgeClass, (Class<? extends ModelElement>) null, getDirection(direction), connectionState, Type.ELEMENT_EDGE_ELEMENT);
    }

    /**
     * Legt einen neuen Elementarmetapfad an, bei dem die Start- und Zielklasse
     * des übergebenen Original-Metapfades durch die übergebenen Klasse ersetzt
     * werden.
     *
     * @param metaModel
     * @param startClass
     * @param originalElementaryMetaPath
     * @param endClass
     */
    ElementaryMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final ElementaryMetaPath originalElementaryMetaPath, final Class<? extends ModelElement> endClass) {
        this(metaModel, startClass, originalElementaryMetaPath.edgeClass, endClass, originalElementaryMetaPath.direction, originalElementaryMetaPath.connectionState, originalElementaryMetaPath.type);
    }

    /**
     * @param startClass
     * @param edgeClass
     * @param direction
     * @param type
     * @return
     */
    private static Class<? extends ModelElement> getStartClass(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Direction direction, final Type type) {
        Class<? extends ModelElement> resultStartClass = startClass; //startClass bleibt erhalten, wenn keiner der folgenden type-Fälle eintritt
        if (type == Type.ELEMENT_EDGE_ELEMENT || type == Type.END_WITH_EDGE) {
            Class<? extends ModelElement> directedEdgeStartClass = getStartClass(edgeClass, direction);
            resultStartClass = startClass == null ? directedEdgeStartClass : getMostSpecialClass(startClass, directedEdgeStartClass);
        }
        return resultStartClass;
    }

    /**
     * @param endClass
     * @param edgeClass
     * @param direction
     * @param type
     * @return
     */
    private static Class<? extends ModelElement> getEndClass(final Class<? extends ModelElement> endClass, final Class<? extends Edge> edgeClass, final Direction direction, final Type type) {
        Class<? extends ModelElement> resultEndClass = endClass; //endClass bleibt erhalten, wenn keiner der folgenden type-Fälle eintritt
        if (type == Type.ELEMENT_EDGE_ELEMENT || type == Type.START_WITH_EDGE) {
            Class<? extends ModelElement> directedEdgeEndClass = getEndClass(edgeClass, direction);
            resultEndClass = endClass == null ? directedEdgeEndClass : getMostSpecialClass(endClass, directedEdgeEndClass);
        }
        return resultEndClass;
    }

    /**
     * @param metaModel
     * @param startClass
     * @param edgeClass
     * @param endClass
     * @param direction
     * @param connectionState
     * @param type
     */
    ElementaryMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass, final Direction direction, final ConnectionState connectionState,
            final Type type) {
        super(metaModel, getStartClass(startClass, edgeClass, direction, type), getEndClass(endClass, edgeClass, direction, type));
        this.startClass = super.getStartClass();
        this.endClass = super.getEndClass();
        this.edgeClass = edgeClass;
        this.direction = direction;
        this.connectionState = DoubleMeaningEdge.class.isAssignableFrom(edgeClass) ? connectionState : null; //nur bei DoubleMeaningEdges darf ein gültiger connectionState gesetzt werden, sonst muss er null sein!
        this.type = type;
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        directed = this.startClass != this.endClass && elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass) != elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass);
        //TODO: hier müsste noch der invalidReason geprüft werden!
    }

    private static final Direction getDirection(final Direction direction) {
        return direction != null ? direction : Direction.FORWARD;
    }

    /**
     * Liefert die Startklasse eines {@link ElementaryMetaPath}, der sich aus
     * der Kante und der übergebenen Richtung ergeben würde.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    public static Class<? extends ModelElement> getStartClass(final Class<? extends Edge> edgeClass, final Direction direction) {
        return direction == Direction.BACKWARD ? Edge.getEndClass(edgeClass) : Edge.getStartClass(edgeClass);
    }

    /**
     * Liefert die Endklasse eines {@link ElementaryMetaPath}, der sich aus der
     * Kante und der übergebenen Richtung ergeben würde.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    public static Class<? extends ModelElement> getEndClass(final Class<? extends Edge> edgeClass, final Direction direction) {
        return direction == Direction.BACKWARD ? Edge.getStartClass(edgeClass) : Edge.getEndClass(edgeClass);
    }

    @Override
    public ElementaryMetaPath getOtherDirection() {
        if (otherDirection == null) {
            if (type == Type.SINGLE_ELEMENT) {
                otherDirection = this;
            } else {
                Type otherDirectionType = type.getOtherDirection();
                //TODO: testen, ob man den ConnectionState hier auch umdrehen muss!?
                otherDirection = new ElementaryMetaPath(metaModel, endClass, edgeClass, startClass, direction.getOther(), connectionState, otherDirectionType);
                otherDirection.otherDirection = this;
            }
        }
        return (ElementaryMetaPath) otherDirection;
    }

    /**
     * @return the startClass
     */
    @Override
    public Class<? extends ModelElement> getStartClass() {
        return startClass;
    }

    /**
     * @return the endClass
     */
    @Override
    public Class<? extends ModelElement> getEndClass() {
        return endClass;
    }

    /**
     * @return the edgeClass
     */
    public Class<? extends Edge> getEdgeClass() {
        return edgeClass;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse eine Ober-
     * oder Unterklasse der im Elementarpfad enthaltenen Kantenklasse ist.
     *
     * @param edgeClass Kantenklasse, deren Zuweisungskompatibilität
     * @return
     */
    public boolean hasEdgeClass(final Class<? extends Edge> edgeClass) {
        return ReflectionUtils.isAssignable(this.edgeClass, edgeClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine
     * Startklasse dieses Metapfades ist.
     *
     * @param elementClass Elementklasse, die als Startklasse geprüft werden
     *            soll
     * @return
     */
    @Override
    public final boolean isStartClass(final Class<? extends ModelElement> elementClass) {
        boolean isStartClass = false;
        if (type == Type.ELEMENT_EDGE_ELEMENT || type == Type.END_WITH_EDGE) {
            isStartClass = direction == Direction.FORWARD ? CoreMetaModel.isStartClass(edgeClass, elementClass) : CoreMetaModel.isEndClass(edgeClass, elementClass);
        } else if (type == Type.START_WITH_EDGE) {
            isStartClass = ReflectionUtils.isAssignable(edgeClass, elementClass);
        }
        if (isStartClass) {
            Class<? extends ModelElement> startClass = getStartClass();
            isStartClass = ReflectionUtils.isAssignable(startClass, elementClass);
        }
        return isStartClass;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse genau eine
     * Endklasse dieses MetaPfades ist.
     *
     * @param elementClass Elementklasse, die als Endklasse geprüft werden soll
     * @return
     */
    @Override
    public final boolean isEndClass(final Class<? extends ModelElement> elementClass) {
        boolean isEndClass = false;
        if (type == Type.ELEMENT_EDGE_ELEMENT || type == Type.START_WITH_EDGE) {
            isEndClass = direction == Direction.FORWARD ? CoreMetaModel.isEndClass(edgeClass, elementClass) : CoreMetaModel.isStartClass(edgeClass, elementClass);
        } else if (type == Type.END_WITH_EDGE) {
            isEndClass = ReflectionUtils.isAssignable(edgeClass, elementClass);
        }
        if (isEndClass) {
            Class<? extends ModelElement> endClass = getEndClass();
            isEndClass = ReflectionUtils.isAssignable(endClass, elementClass);
        }
        return isEndClass;
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Liefert <code>true</code>, wenn die im Elementarpfad enthaltene Kante die
     * Richtung vorwärts hat, also wenn direction == Direction.FORWARD ist,
     * sonst <code>false</code>.
     *
     * @return
     */
    public boolean hasDirectionForward() {
        return direction != Direction.BACKWARD;
    }

    /**
     * @param direction
     * @return
     */
    public final boolean hasDirection(final Direction direction) {
        return this.direction == direction;
    }

    /**
     * @return the connectionState
     */
    public ConnectionState getConnectionState() {
        return connectionState;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param other
     * @return only <code>true</code> if this and the other metapath have an
     *         assignable start class, an assignable end class, an assignable
     *         edge class, the same direction and the same type. Assignable only
     *         means that one of the class must be a subclass of the other
     *         (which is sub and which super dosn't matters).
     */
    @Override
    public boolean isAssignable(final MetaPath otherMetaPath) {
        List<ElementaryMetaPath> elementaryMetaPaths = otherMetaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.size() != 1) {
            return false;
        }
        ElementaryMetaPath other = elementaryMetaPaths.get(0);
        if (!isStartClass(other.startClass)) {
            return false;
        }
        if (!isEndClass(other.endClass)) {
            return false;
        }
        if (!hasEdgeClass(other.edgeClass)) {
            return false;
        }
        if (!hasDirection(other.direction)) {
            return false;
        }
        if (connectionState != other.connectionState) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (connectionState == null ? 0 : connectionState.hashCode());
        result = prime * result + (direction == null ? 0 : direction.hashCode());
        result = prime * result + (edgeClass == null ? 0 : edgeClass.hashCode());
        result = prime * result + (endClass == null ? 0 : endClass.hashCode());
        result = prime * result + (directed ? 1231 : 1237);
        result = prime * result + (startClass == null ? 0 : startClass.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ElementaryMetaPath other = (ElementaryMetaPath) obj;
        if (connectionState != other.connectionState) {
            return false;
        }
        if (direction != other.direction) {
            return false;
        }
        if (edgeClass == null) {
            if (other.edgeClass != null) {
                return false;
            }
        } else if (!edgeClass.equals(other.edgeClass)) {
            return false;
        }
        if (endClass == null) {
            if (other.endClass != null) {
                return false;
            }
        } else if (!endClass.equals(other.endClass)) {
            return false;
        }
        if (directed != other.directed) {
            return false;
        }
        if (startClass == null) {
            if (other.startClass != null) {
                return false;
            }
        } else if (!startClass.equals(other.startClass)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public InvalidityCheckResult getInvalidityCheckResult() {
        if (super.getInvalidityCheckResult().invalidReason == null) {
            InvalidReason invalidReason = null;
            if (type == null) {
                invalidReason = InvalidReason.INVALID_TYPE;
            } else if (startClass == null || de.imise.tool3lgm.graphtools.path.metapaths.Direction.class.isAssignableFrom(startClass)) {
                invalidReason = InvalidReason.INVALID_START_CLASS;
            } else if (endClass == null || de.imise.tool3lgm.graphtools.path.metapaths.Direction.class.isAssignableFrom(endClass)) {
                invalidReason = InvalidReason.INVALID_END_CLASS;
            } else if (direction != null && edgeClass == null) {
                invalidReason = InvalidReason.INVALID_DIRECTION;
            } else if (type == Type.ELEMENT_EDGE_ELEMENT) {
                if (direction == Direction.FORWARD) {
                    if (!CoreMetaModel.isStartClass(edgeClass, startClass)) {
                        invalidReason = InvalidReason.INVALID_EDGE_STARTCLASS_AND_METAPATH_STARTCLASS;
                    } else if (!CoreMetaModel.isEndClass(edgeClass, endClass)) {
                        invalidReason = InvalidReason.INVALID_EDGE_ENDCLASS_AND_METAPATH_ENDCLASS;
                    }
                } else { //direction == Direction.BACKWARD
                    if (!CoreMetaModel.isStartClass(edgeClass, endClass)) {
                        invalidReason = InvalidReason.INVALID_EDGE_STARTCLASS_AND_METAPATH_ENDCLASS;
                    } else if (!CoreMetaModel.isEndClass(edgeClass, startClass)) {
                        invalidReason = InvalidReason.INVALID_EDGE_ENDCLASS_AND_METAPATH_STARTCLASS;
                    }
                }
            } else if (type == Type.START_WITH_EDGE) {
                if (startClass != edgeClass) {
                    invalidReason = InvalidReason.INVALID_START_CLASS;
                } else if (endClass == edgeClass) {
                    invalidReason = InvalidReason.INVALID_END_CLASS;
                }
                if (direction == Direction.FORWARD) {
                    if (!CoreMetaModel.isEndClass(edgeClass, endClass)) {
                        invalidReason = InvalidReason.INVALID_END_CLASS;
                    }
                } else { //direction == Direction.BACKWARD
                    if (!CoreMetaModel.isStartClass(edgeClass, endClass)) {
                        invalidReason = InvalidReason.INVALID_END_CLASS;
                    }
                }
            } else if (type == Type.END_WITH_EDGE) {
                if (endClass != edgeClass) {
                    invalidReason = InvalidReason.INVALID_END_CLASS;
                } else if (startClass == edgeClass) {
                    invalidReason = InvalidReason.INVALID_START_CLASS;
                }
                if (direction == Direction.FORWARD) {
                    if (!CoreMetaModel.isStartClass(edgeClass, startClass)) {
                        invalidReason = InvalidReason.INVALID_START_CLASS;
                    }
                } else { //direction == Direction.BACKWARD
                    if (!CoreMetaModel.isEndClass(edgeClass, startClass)) {
                        invalidReason = InvalidReason.INVALID_START_CLASS;
                    }
                }
            }
            invalidityCheckResult = new InvalidityCheckResult(invalidReason);
        }
        return invalidityCheckResult;
    }

    @Override
    public String createName() {
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        String name = elementsNameBuilder.getMetaAssociationName(edgeClass, direction, connectionState);
        return name;
    }

    @Override
    public final boolean isCreatable(final boolean checkEndCreateElement) {
        //Nur ElementarMetaPfade, die eine Kante zwischen 2 Knoten repräsentieren, wobei die Kantenklasse nicht abstract sein darf, sind anlegtbar. Alle
        //anderen nicht. Die Elementklassen können abstract sein. Ob sie anlegbar sind, ist hier (bei einem einzelnen Elementarpfad) egal, da es nur um
        //die Anlegbarkeit der Kante geht.
        if (type != Type.ELEMENT_EDGE_ELEMENT) {
            return false;
        }
        if (!isValid()) {
            return false;
        }
        if (Modifier.isAbstract(edgeClass.getModifiers())) {
            return false;
        }
        if (metaModel.getConditionMetaPath(edgeClass) != null) {
            return false;
        }
        if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
            if (InstanciationEdge.INSTANCE_TO_MASTER_DIRECTION.equals(direction)) {
                return false;
            }
        }
        if (checkEndCreateElement) {
            if (!metaModel.isCreatable(endClass, this, null)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isRemoveable(final boolean checkEndElement) {
        if (isFirstPathElementDependent()) {
            return false;
        }
        if (checkEndElement) {
            if (isLastPathElementDependent()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public final List<ElementaryMetaPath> getElementaryMetaPaths() {
        if (elementaryMetaPaths == null) {
            elementaryMetaPaths = ImmutableList.of(this);
        }
        return elementaryMetaPaths;
    }

    @Override
    public final List<MetaPath> getSubMetaPaths() {
        return getSubMetaPaths(true);
    }

    @Override
    public final boolean containsPropertyTransferEdge() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        return edgeClass != null && PropertyTransferEdge.class.isAssignableFrom(getEdgeClass());
    }

    /**
     * @return Cardinality des Pfades in Vorwärtsrichtung, also vom Start- zum
     *         EndElement.
     */
    public EdgeCardinality getForwardCardinality() {
        if (type == Type.START_WITH_EDGE || type == Type.END_WITH_EDGE) {
            return EdgeCardinality.ONE_ONE;
        }
        if (type == Type.SINGLE_ELEMENT) {
            return EdgeCardinality.ZERO_UNLIMITED;
        }
        //Wird die Kante in Vorwärtsrichtung gelesen, dann ist es auch die Vorwärtskardinalität der Kante, sonst die Rückwärtskardinalität
        return direction == Direction.BACKWARD ? CoreMetaModel.getBackwardCardinality(edgeClass) : CoreMetaModel.getForwardCardinality(edgeClass);
    }

    /**
     * @return Cardinality des Pfades in Rückwärtsrichtung
     */
    public EdgeCardinality getBackwardCardinality() {
        //Wird die Kante in Vorwärtsrichtung gelesen, dann ist es die Rückwärtskardinalität der Kante, sonst die Vorwärtskardinalität
        return direction == Direction.BACKWARD ? CoreMetaModel.getForwardCardinality(edgeClass) : CoreMetaModel.getBackwardCardinality(edgeClass);
    }

    @Override
    public boolean canBeRecursive() {
        return metaModel.isRecursiveForElementClass(edgeClass, endClass);
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public final List<ModelElement> getConnectedElements(final Collection<ModelElement> modelElements, final boolean multiple) {
        List<ModelElement> returnList = new ArrayList<>();
        Direction direction = getDirection();
        Class<? extends Edge> edgeClass = getEdgeClass();
        for (ModelElement me : modelElements) {
            List<ModelElement> connectedElements = me.getConnectedElements(edgeClass, direction);
            if (multiple) {
                returnList.addAll(connectedElements);
            } else {
                for (ModelElement connected : connectedElements) {
                    if (!returnList.contains(connected)) {
                        returnList.add(connected);
                    }
                }
            }
            return returnList;
        }
        PathResultTreeModel resultTree = getResultTree(modelElements);
        return resultTree.getConnectedElements(multiple);
    }

    @Override
    public final List<ElementContainer> getConnectedContainer(final ModelElement me, final GraphDocument doc, final boolean forlast) {
        List<ElementContainer> returnList = new ArrayList<>();
        if (forlast) { //das hier ist nicht sinnvoll (Elementarpfad und von den verbundenen den vorletzten = das übergebene Element), muss aber der Vollständigkeit halber sein
            ElementContainer ec = me.getContainer(doc);
            if (ec != null) {
                returnList.add(ec);
            }
            return returnList;
        }
        ElementaryMetaPath elementaryMetaPath = this;
        Direction direction = elementaryMetaPath.getDirection();
        Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
        List<ElementContainer> connectedContainers = me.getConnectedContainers(doc, edgeClass, direction);
        for (ElementContainer connectedContainer : connectedContainers) {
            ModelElement connected = connectedContainer.getElement();
            Class<? extends ModelElement> connectedClass = connected.getClass();
            if (isEndClass(connectedClass) && !returnList.contains(connectedContainer)) {
                returnList.add(connectedContainer);
            }
        }
        return returnList;
    }

}
