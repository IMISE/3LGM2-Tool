package de.imise.tool3lgm.graphtools.path.pathmodel;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;

/**
 * Oberklasse für eine tatsächliche Pfadinstanz
 *
 * @author AXS
 * @create 08.02.2011
 */
public abstract class AbstractPath {

    /**
     * COMMENTME
     */
    protected ModelElement startElement;

    /**
     * COMMENTME
     */
    protected ModelElement endElement;

    /**
     * COMMENTME
     */
    protected AbstractMetaPath metaPath;

    /**
     * @param metaPath
     * @param startElement
     * @param endElement
     */
    public AbstractPath(final AbstractMetaPath metaPath, final ModelElement startElement, final ModelElement endElement) {
        this(startElement, endElement);
        this.metaPath = metaPath;
    }

    /**
     * @param startElement
     * @param endElement
     */
    public AbstractPath(final ModelElement startElement, final ModelElement endElement) {
        super();
        this.startElement = startElement;
        this.endElement = endElement;
    }

    /**
     * @return the startElement
     */
    public final ModelElement getStartElement() {
        return startElement;
    }

    /**
     * @return the endElement
     */
    public final ModelElement getEndElement() {
        return endElement;
    }

    /**
     * @return the metaPath
     */
    public AbstractMetaPath getMetaPath() {
        return metaPath;
    }

    /**
     * @return
     */
    public abstract boolean isValid();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (endElement == null ? 0 : endElement.hashCode());
        result = prime * result + (metaPath == null ? 0 : metaPath.hashCode());
        result = prime * result + (startElement == null ? 0 : startElement.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractPath other = (AbstractPath) obj;
        if (endElement == null) {
            if (other.endElement != null) {
                return false;
            }
        } else if (!endElement.equals(other.endElement)) {
            return false;
        }
        if (metaPath == null) {
            if (other.metaPath != null) {
                return false;
            }
        } else if (!metaPath.equals(other.metaPath)) {
            return false;
        }
        if (startElement == null) {
            if (other.startElement != null) {
                return false;
            }
        } else if (!startElement.equals(other.startElement)) {
            return false;
        }
        return true;
    }

}
