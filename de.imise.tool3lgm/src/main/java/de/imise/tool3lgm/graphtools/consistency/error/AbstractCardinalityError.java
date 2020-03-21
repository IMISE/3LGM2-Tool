package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;

/**
 * @author AXS
 * @created 13.09.2008
 */
public abstract class AbstractCardinalityError extends AbstractError {

    /**
     * Wert der Kardinalität, die über oder unterschritten wurde
     */
    protected int cardValue;

    /**
     * @param me
     * @param elementaryMetaPath
     * @param cardValue
     * @param gdcoll
     */
    public AbstractCardinalityError(final ModelElement me, final AbstractMetaPath metaPath, final GDCollection gdcoll, final int cardValue) {
        super(me, metaPath, gdcoll);
        this.cardValue = cardValue;
    }

    /**
     * @return
     */
    private Class<? extends Edge> getEdgeClass() {
        if (errorField instanceof ElementaryMetaPath) {
            return ((ElementaryMetaPath) errorField).getEdgeClass();
        }
        return null;
    }

    /**
     * @return
     */
    public AbstractMetaPath getMetaPath() {
        return (AbstractMetaPath) errorField;
    }

    @Override
    public String getErrorFieldString() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        MetaModel metaModel = gdcoll.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        return edgeClass != null ? elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass) : "";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cardValue;
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
        AbstractCardinalityError other = (AbstractCardinalityError) obj;
        if (cardValue != other.cardValue) {
            return false;
        }
        return true;
    }

}