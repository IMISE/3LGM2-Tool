package de.imise.tool3lgm.graphtools.path.meta;

import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getFullBackwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getFullForwardMetaAssociationName;

import java.lang.reflect.Modifier;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;

/**
 * Elementarer MetaPfad, aus dem alle anderen MetaPfade aufgebaut sind.
 *
 * @author AXS
 * @create 12.10.2010
 */
public final class ElementaryMetaPath extends AbstractMetaPath {

    /**
     * Mögliche Arten eines {@link ElementaryMetaPath}.
     */
    public static enum Type {
        /** Regulärer Elementarmetapfad mit einer Startelementklasse, einer Endelementklasse und einer dazwischen liegenden Kantenklasse */
        ELEMENT_EDGE_ELEMENT,
        /** Typ für Metapfade, die nur eine einzelne Elementart beschreiben */
        SINGLE_ELEMENT,
        /** Typ für MetaPfade, die bei einer Kantenklasse starten */
        START_WITH_EDGE,
        /** Typ für MetaPfade, die auf einer Kantenklasse enden */
        END_WITH_EDGE,
    }

    private static enum InvalidReason {
        INVALID_START_CLASS,
        INVALID_END_CLASS,
        INVALID_EDGE_CLASS,
        INVALID_DIRECTION,
        INVALID_TYPE
    }

    /**
     * Klasse, bei der der Pfad startet. Sie darf in einem validen Pfad niemals <code>null</code> sein und muss eine
     * Ober- oder Unterklasse des jeweiligen Kantenendes sein, auf das sich die Startklasse laut Richtungsangabe bezieht.
     */
    private final Class<? extends ModelElement> startClass;

    /**
     * Klasse, bei der der Pfad endet. Sie darf in einem validen Pfad niemals <code>null</code> sein und muss eine
     * Ober- oder Unterklasse des jeweiligen Kantenendes sein, auf das sich die Endklasse laut Richtungsangabe bezieht.
     */
    private final Class<? extends ModelElement> endClass;

    /**
     * Kantenklasse des Pfades
     */
    private final Class<? extends Edge> edgeClass;

    /**
     * Richtung, die festlegt, wie herum die Kantenklasse gelesen werden soll.
     * Direction.FORWARD bedeutet, dass die {@link #startClass} des Pfades als Startklasse
     * der Kantenklasse verstanden wird und die Endklasse der Kante auch Endklasse des Pfades ist.
     * Bei Direction.BACKWARD wird es andersherum verstanden.
     */
    private final Direction direction;

    /**
     * Richtung, die die Kante ausgehend von der durch die <code>direction</code> festgelegten Richtung haben soll. Dieser Parameter
     * ist nur bei DoubleMeaningEdges relevant.
     */
    private final ConnectionState connectionState;

    /**
     * Typ des MetaPfades. Ist er nach dem setzen der Richtung immer noch <code>null</code>, dann ist der
     * Pfad nicht valide.
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
     * @see #isCreateable()
     */
    private final boolean createable;

    /**
     * Ein MetaPfad kann nur aus einer Elementklasse bestehen. Dies beschreibt den Pfad zu allen Elementen dieser Klasse.
     * Intern sind bei diesem Pfad sowohl die Start- als auch die Endklasse auf diese Elementklasse gesetzt und es gibt
     * weder eine Kantenklasse noch eine Richtung.
     *
     * @param elementClass
     */
    ElementaryMetaPath(final Class<? extends ModelElement> elementClass) {
        super(elementClass, elementClass);
        startClass = elementClass;
        endClass = elementClass;
        edgeClass = null;
        direction = null;
        connectionState = null;
        otherDirection = this;
        directed = false;
        createable = false;
        type = Type.SINGLE_ELEMENT;
    }

    /**
     * @param edgeClass
     * @param direction
     */
    ElementaryMetaPath(final Class<? extends Edge> edgeClass, final Direction direction) {
        this(edgeClass, direction, null);
    }

    /**
     * Legt einen neuen Elementarmetapfad an, bei dem die Start- und Zielklasse denen der Kante in der angegebenen Richtung entsprechen.
     *
     * @param edgeClass
     * @param direction
     */
    ElementaryMetaPath(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState) {
        this((Class<? extends ModelElement>) null, edgeClass, (Class<? extends ModelElement>) null, getDirection(direction), connectionState, Type.ELEMENT_EDGE_ELEMENT);
        otherDirection = new ElementaryMetaPath(endClass, edgeClass, startClass, this.direction == Direction.BACKWARD ? Direction.FORWARD : Direction.BACKWARD, connectionState, Type.ELEMENT_EDGE_ELEMENT);
        otherDirection.otherDirection = this;
    }

    /**
     * Legt einen neuen Elementarmetapfad an, bei dem die Start- und Zielklasse des übergebenen Original-Metapfades durch die übergebenen Klasse
     * ersetzt werden.
     *
     * @param startClass
     * @param originalElementaryMetaPath
     * @param endClass
     */
    ElementaryMetaPath(final Class<? extends ModelElement> startClass, final ElementaryMetaPath originalElementaryMetaPath, final Class<? extends ModelElement> endClass) {
        this(startClass, originalElementaryMetaPath.edgeClass, endClass, originalElementaryMetaPath.direction, originalElementaryMetaPath.connectionState, originalElementaryMetaPath.type);
        otherDirection = new ElementaryMetaPath(endClass, edgeClass, startClass, direction == Direction.BACKWARD ? Direction.FORWARD : Direction.BACKWARD, connectionState, originalElementaryMetaPath.type);
        otherDirection.otherDirection = this;
    }

    /**
     * @param startClass
     * @param edgeClass
     * @param endClass
     * @param direction
     * @param connectionState
     * @param type
     */
    private ElementaryMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass, final Direction direction, final ConnectionState connectionState, final Type type) {
        super(startClass == null ? isForward(direction) ? Edge.getStartClass(edgeClass) : Edge.getEndClass(edgeClass) : startClass, endClass == null ? isForward(direction) ? Edge.getEndClass(edgeClass) : Edge.getStartClass(edgeClass) : endClass);
        Class<? extends ModelElement> edgeStartClass = Edge.getStartClass(edgeClass);
        Class<? extends ModelElement> edgeEndClass = Edge.getEndClass(edgeClass);
        if (direction == Direction.FORWARD) {
            this.startClass = startClass != null && edgeStartClass.isAssignableFrom(startClass) ? startClass : edgeStartClass;
            this.endClass = endClass != null && edgeEndClass.isAssignableFrom(endClass) ? endClass : edgeEndClass;
        } else {
            this.startClass = startClass != null && edgeEndClass.isAssignableFrom(startClass) ? startClass : edgeEndClass;
            this.endClass = endClass != null && edgeStartClass.isAssignableFrom(endClass) ? endClass : edgeStartClass;
        }
        this.edgeClass = edgeClass;
        this.direction = direction;
        this.connectionState = DoubleMeaningEdge.class.isAssignableFrom(edgeClass) ? connectionState : null; //nur bei DoubleMeaningEdges darf ein gültiger connectionState gesetzt werden, sonst muss er null sein!
        this.type = type;
        directed = this.startClass != this.endClass && getFullForwardMetaAssociationName(edgeClass) != getFullBackwardMetaAssociationName(edgeClass);
        createable = getIsCreateable();
        //TODO: hier müsste noch der invalidReason geprüft werden!
    }

    private static final Direction getDirection(final Direction direction) {
        return direction != null ? direction : Direction.FORWARD;
    }

    private static final boolean isForward(final Direction direction) {
        return getDirection(direction) == Direction.FORWARD;
    }

    @Override
    public ElementaryMetaPath getOtherDirection() {
        return (ElementaryMetaPath) super.getOtherDirection();
    }

    /**
     * @return the startClass
     */
    public Class<? extends ModelElement> getStartClass() {
        return startClass;
    }

    /**
     * @return the endClass
     */
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
     * Liefert <code>true</code>, wenn die übergebene Kantenklasse eine Ober- oder Unterklasse der im Elementarpfad einthaltenen Kantenklasse ist.
     *
     * @param edgeClass Kantenklasse, deren Zuweisungskompatibilität
     * @return
     */
    public boolean hasEdgeClass(final Class<? extends Edge> edgeClass) {
        return edgeClass != null && this.edgeClass != null && ReflectionUtils.isAssignable(this.edgeClass, edgeClass);
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Liefert <code>true</code>, wenn die im Elementarpfad enthaltene Kante die Richtung vorwärts hat, also wenn direction == Direction.FORWARD ist,
     * sonst <code>false</code>.
     *
     * @return
     */
    public boolean hasDirectionForward() {
        return direction == Direction.FORWARD;
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
            } else if (startClass == null || de.imise.tool3lgm.graphtools.path.meta.Direction.class.isAssignableFrom(startClass)) {
                invalidReason = InvalidReason.INVALID_START_CLASS;
            } else if (endClass == null || de.imise.tool3lgm.graphtools.path.meta.Direction.class.isAssignableFrom(endClass)) {
                invalidReason = InvalidReason.INVALID_END_CLASS;
            } else if (direction != null && edgeClass == null) {
                invalidReason = InvalidReason.INVALID_DIRECTION;
            }
            invalidityCheckResult = new InvalidityCheckResult(invalidReason);
        }
        return invalidityCheckResult;
    }

    @Override
    protected String createName() {
        return ElementsNameBuilder.getMetaAssociationName(edgeClass, direction, connectionState);
    }

    @Override
    public final boolean isCreateable() {
        return createable;
    }

    /**
     * Nur ElementarMetaPfade, die eine Kante zwischen 2 Knoten repräsentieren, wobei die Kantenklasse nicht abstract sein darf, sind anlegtbar. Alle
     * anderen nicht. Die Elementklassen können abstract sein. Ob sie anlegbar sind, ist hier (bei einem einzelnen Elementarpfad) egal, da es nur um
     * die Anlegbarkeit der Kante geht.
     *
     * @return
     */
    private final boolean getIsCreateable() {
        if (type != Type.ELEMENT_EDGE_ELEMENT) {
            return false;
        }
        if (!isValid()) {
            return false;
        }
        if (Modifier.isAbstract(edgeClass.getModifiers())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public List<ElementaryMetaPath> getElementaryMetaPaths() {
        if (elementaryMetaPaths == null) {
            elementaryMetaPaths = ImmutableList.of(this);
        }
        return elementaryMetaPaths;
    }

    @Override
    public boolean containsHasPartEdge() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        return edgeClass != null && HasPartEdge.class.isAssignableFrom(getEdgeClass());
    }

    ////////////////////////
    // Factory-Funktionen //
    ////////////////////////

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEdgeToStartElementMetaPath(final Class<? extends Edge> edgeClass) {
        return getEdgeToStartElementMetaPath(edgeClass, null);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @return
     */
    public final ElementaryMetaPath getEdgeToStartElementMetaPath(final Class<? extends Edge> edgeClass, final ConnectionState connectionState) {
        return new ElementaryMetaPath(edgeClass, edgeClass, Edge.getStartClass(edgeClass), Direction.BACKWARD, connectionState, Type.START_WITH_EDGE);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEdgeToEndElementMetaPath(final Class<? extends Edge> edgeClass) {
        return getEdgeToEndElementMetaPath(edgeClass, null);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @return
     */
    public final ElementaryMetaPath getEdgeToEndElementMetaPath(final Class<? extends Edge> edgeClass, final ConnectionState connectionState) {
        return new ElementaryMetaPath(edgeClass, edgeClass, Edge.getStartClass(edgeClass), Direction.FORWARD, connectionState, Type.START_WITH_EDGE);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEdgeToStartAndEndElementMetaPath(final Class<? extends Edge> edgeClass) {
        return getEdgeToStartAndEndElementMetaPath(edgeClass, null);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @return
     */
    public final ElementaryMetaPath getEdgeToStartAndEndElementMetaPath(final Class<? extends Edge> edgeClass, final ConnectionState connectionState) {
        return new ElementaryMetaPath(edgeClass, edgeClass, Edge.getStartClass(edgeClass), null, connectionState, Type.START_WITH_EDGE);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getStartElementToEdgeMetaPath(final Class<? extends Edge> edgeClass) {
        return getStartElementToEdgeMetaPath(edgeClass, null);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @return
     */
    public final ElementaryMetaPath getStartElementToEdgeMetaPath(final Class<? extends Edge> edgeClass, final ConnectionState connectionState) {
        return new ElementaryMetaPath(Edge.getStartClass(edgeClass), edgeClass, edgeClass, Direction.FORWARD, connectionState, Type.END_WITH_EDGE);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getEndElementToEdgeMetaPath(final Class<? extends Edge> edgeClass) {
        return getEndElementToEdgeMetaPath(edgeClass, null);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @return
     */
    public final ElementaryMetaPath getEndElementToEdgeMetaPath(final Class<? extends Edge> edgeClass, final ConnectionState connectionState) {
        return new ElementaryMetaPath(Edge.getStartClass(edgeClass), edgeClass, edgeClass, Direction.BACKWARD, connectionState, Type.END_WITH_EDGE);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getStartAndEndElementToEdgeMetaPath(final Class<? extends Edge> edgeClass) {
        return getStartAndEndElementToEdgeMetaPath(edgeClass, null);
    }

    /**
     * @param edgeClass
     * @param connectionState
     * @return
     */
    public final ElementaryMetaPath getStartAndEndElementToEdgeMetaPath(final Class<? extends Edge> edgeClass, final ConnectionState connectionState) {
        return new ElementaryMetaPath(edgeClass, edgeClass, ReflectionUtils.getCommonSuperClass(Edge.getStartClass(edgeClass), Edge.getEndClass(edgeClass)), null, connectionState, Type.END_WITH_EDGE);
    }

}
