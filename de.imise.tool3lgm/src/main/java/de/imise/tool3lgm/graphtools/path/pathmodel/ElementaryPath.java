package de.imise.tool3lgm.graphtools.path.pathmodel;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath.Type;

/**
 * @author AXS
 * @create 08.02.2011
 */
public final class ElementaryPath extends AbstractPath {

    /** Die Kante zwischen Start- und Endelement */
    private Edge edge;

    /**
     * @param metaPath
     * @param startElement
     * @param endElement
     * @param edge
     */
    public ElementaryPath(final ElementaryMetaPath metaPath, final ModelElement startElement, final ModelElement endElement, final Edge edge) {
        super(metaPath, startElement, endElement);
        this.edge = edge;
    }

    /**
     * Für elementare MertaPfade, die nur eine einzelne Elementart darstellen
     *
     * @param metaPath
     * @param element
     */
    public ElementaryPath(final ElementaryMetaPath metaPath, final ModelElement element) {
        this(metaPath, element, element, null);
    }

    /**
     * @param startsWithEdgeMetaPath
     * @param edge
     * @throws ClassCastException if the {@link ModelElement} is not an {@link Edge}
     * @return
     */
    public static ElementaryPath createStartsWithEdgePath(final ElementaryMetaPath startsWithEdgeMetaPath, final Edge edge) {
        Direction direction = startsWithEdgeMetaPath.getDirection();
        ModelElement endElement = direction == Direction.BACKWARD ? edge.getStart() : edge.getEnd();
        return new ElementaryPath(startsWithEdgeMetaPath, edge, endElement, edge);
    }

    /**
     * @param startWithEdgeMetaPath
     * @param edge
     * @throws ClassCastException if the {@link ModelElement} is not an {@link Edge}
     * @return
     */
    public static ElementaryPath createEndsWithEdgePath(final ElementaryMetaPath endsWithEdgeMetaPath, final Edge edge) {
        Direction direction = endsWithEdgeMetaPath.getDirection();
        ModelElement startElement = direction == Direction.BACKWARD ? edge.getEnd() : edge.getStart();
        return new ElementaryPath(endsWithEdgeMetaPath, startElement, edge, edge);
    }

    /**
     * @return the edge
     */
    public final Edge getEdge() {
        return edge;
    }

    @Override
    public boolean isValid() {
        //das ganze hier könnte man bei Bedarf analog zum public InvalidityCheckResult getInvalidityCheckResult() bei den MetaPfaden machen
        if (metaPath == null || !(metaPath instanceof ElementaryMetaPath)) {
            return false;
        }
        ElementaryMetaPath elementaryMetaPath = (ElementaryMetaPath) metaPath;
        Type type = elementaryMetaPath.getType();
        if (!metaPath.isValid()) {
            return false;
        }
        if (startElement == null) {
            return false;
        }
        if (endElement == null) {
            return false;
        }
        Class<? extends ModelElement> startClass = elementaryMetaPath.getStartClass();
        Class<? extends ModelElement> startElementClass = startElement.getClass();
        if (!startClass.isAssignableFrom(startElementClass)) {
            return false;
        }
        Class<? extends ModelElement> endClass = elementaryMetaPath.getEndClass();
        Class<? extends ModelElement> endElementClass = endElement.getClass();
        if (!endClass.isAssignableFrom(endElementClass)) {
            return false;
        }
        Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
        Class<? extends Edge> edgeElementClass = edge.getClass();
        if (edge != null && !edgeClass.isAssignableFrom(edgeElementClass)) {
            return false;
        }
        if (type == Type.SINGLE_ELEMENT) {
            if (startElement != endElement) {
                return false;
            }
            if (edge != null) {
                return false;
            }
        } else {
            if (edge == null) {
                return false;
            }
            Direction direction = elementaryMetaPath.getDirection();
            if (type == Type.START_WITH_EDGE) {
                if (startElement != edge) {
                    return false;
                }
                if (direction == Direction.FORWARD) {
                    ModelElement edgeEnd = edge.getEnd();
                    if (edgeEnd != endElement) {
                        return false;
                    }
                } else if (direction == Direction.BACKWARD) {
                    ModelElement edgeStart = edge.getStart();
                    if (edgeStart != endElement) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (type == Type.END_WITH_EDGE) {
                if (endElement != edge) {
                    return false;
                }
                if (direction == Direction.FORWARD) {
                    ModelElement edgeStart = edge.getStart();
                    if (edgeStart != startElement) {
                        return false;
                    }
                } else if (direction == Direction.BACKWARD) {
                    ModelElement edgeEnd = edge.getEnd();
                    if (edgeEnd != startElement) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (type == Type.ELEMENT_EDGE_ELEMENT) {
                ModelElement edgeStart = edge.getStart();
                ModelElement edgeEnd = edge.getEnd();
                if (direction == Direction.FORWARD) {
                    if (startElement != edgeStart) {
                        return false;
                    }
                    if (endElement != edgeEnd) {
                        return false;
                    }
                } else if (direction == Direction.BACKWARD) {
                    if (startElement != edgeEnd) {
                        return false;
                    }
                    if (endElement != edgeStart) {
                        return false;
                    }
                } else {
                    if (startElement != edgeStart && endElement != edgeEnd || startElement != edgeEnd && endElement != edgeStart) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (edge == null ? 0 : edge.hashCode());
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
        ElementaryPath other = (ElementaryPath) obj;
        if (edge == null) {
            if (other.edge != null) {
                return false;
            }
            //da Edge keine eigene euals() aber eine equalsTo() hat sollte man eigentlich wahrscheinlich diese nehmen!?
        } else if (!edge.equals(other.edge)) {
            return false;
        }
        return true;
    }

    @Override
    public final ElementaryMetaPath getMetaPath() {
        return (ElementaryMetaPath) super.getMetaPath();
    }

    @Override
    protected void replace(final ModelElement original, final ModelElement replacement) {
        if (edge == original) {
            edge = (Edge) replacement; // must be castable!
        }
    }

    @Override
    public String toString() {
        //        return startElement + " (" + startElement.getHashString() + ") " + metaPath.getName() + " (" + edge.getHashString() + ") " + " " + endElement + " (" + endElement.getHashString() + ") ";
        String metaPathName = metaPath == null ? null : metaPath.getName();
        return startElement + " " + metaPathName + " " + endElement;
    }

}
