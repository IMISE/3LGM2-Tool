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
