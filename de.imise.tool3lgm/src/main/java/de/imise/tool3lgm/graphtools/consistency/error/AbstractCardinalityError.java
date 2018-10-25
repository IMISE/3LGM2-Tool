package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;

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
     * @param edgeClass
     * @param cardValue
     * @param gdcoll
     */
    public AbstractCardinalityError(final ModelElement me, final Class<? extends Edge> edgeClass, final GDCollection gdcoll, final int cardValue) {
        super(me, edgeClass, gdcoll);
        this.cardValue = cardValue;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        AbstractCardinalityError ce = (AbstractCardinalityError) obj;
        return cardValue == ce.cardValue;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Edge> getEdgeClass() {
        return (Class<? extends Edge>) errorField;
    }

    @Override
    public String getErrorFieldString() {
        Class<? extends Edge> edgeClass = getEdgeClass();
        return edgeClass != null ? ElementsNameBuilder.getFullForwardMetaAssociationName(edgeClass) : "";
    }

}