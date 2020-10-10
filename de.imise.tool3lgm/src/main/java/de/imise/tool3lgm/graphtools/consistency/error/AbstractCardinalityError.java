package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;

/**
 * @author AXS
 * @created 13.09.2008
 */
public abstract class AbstractCardinalityError extends AbstractElementaryPathError {

    /**
     * Wert der Kardinalität, die über oder unterschritten wurde
     */
    protected int cardValue;

    /**
     * @param me
     * @param elementaryMetaPath
     * @param cardValue
     */
    public AbstractCardinalityError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath, final int cardValue) {
        super(me, elementaryMetaPath);
        this.cardValue = cardValue;
    }

    /**
     * @return
     */
    private Class<? extends Edge> getEdgeClass() {
        return ((ElementaryMetaPath) errorField).getEdgeClass();
    }

    @Override
    public String getErrorFieldString() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
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