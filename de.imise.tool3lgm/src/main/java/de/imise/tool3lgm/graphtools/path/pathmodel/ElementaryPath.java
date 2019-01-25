package de.imise.tool3lgm.graphtools.path.pathmodel;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;

/**
 * @author AXS
 * @create 08.02.2011
 */
public final class ElementaryPath extends AbstractPath {

    /** Die Kante zwischen Start- und Endelement */
    private final Edge edge;

    /**
     * @param startElement
     * @param endElement
     * @param edge
     * @param metaPath
     */
    public ElementaryPath(final ModelElement startElement, final ModelElement endElement, final Edge edge, final ElementaryMetaPath metaPath) {
        super(startElement, endElement, metaPath);
        this.edge = edge;
    }

    /**
     * Für elementare MertaPfade, die nur eine einzelne Elementart darstellen
     *
     * @param element
     * @param metaPath
     */
    public ElementaryPath(final ModelElement element, final ElementaryMetaPath metaPath) {
        this(element, element, null, metaPath);
    }

    /**
     * @return the edge
     */
    public final Edge getEdge() {
        return edge;
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
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
        } else if (!edge.equals(other.edge)) {
            return false;
        }
        return true;
    }

}
