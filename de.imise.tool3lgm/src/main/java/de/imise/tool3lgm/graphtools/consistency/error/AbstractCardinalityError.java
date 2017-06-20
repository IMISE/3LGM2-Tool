package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

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
    public AbstractCardinalityError(final ModelElement me, final Class<? extends Kante> edgeClass, final GDCollection gdcoll, final int cardValue) {
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
    public Class<? extends Kante> getEdgeClass() {
        return (Class<? extends Kante>) errorField;
    }

    @Override
    public String getErrorFieldString() {
        Class<? extends Kante> edgeClass = getEdgeClass();
        return edgeClass != null ? ModelConstants.getFullForwardMetaAssociationName(edgeClass) : "";
    }

}