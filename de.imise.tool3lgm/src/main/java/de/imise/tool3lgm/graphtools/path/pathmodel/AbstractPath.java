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
     * @param startElement
     * @param endElement
     * @param metaPath
     */
    public AbstractPath(final ModelElement startElement, final ModelElement endElement, final AbstractMetaPath metaPath) {
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
    public final AbstractMetaPath getMetaPath() {
        return metaPath;
    }

    /**
     * @return
     */
    public abstract boolean isValid();

}
