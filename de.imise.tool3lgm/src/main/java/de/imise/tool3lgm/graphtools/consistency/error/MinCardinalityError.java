/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MinCardinalityError extends CardinalityError {

    /**
     * @param me
     * @param edgeClass
     * @param cardValue
     * @param gdcoll
     */
    public MinCardinalityError(final ModelElement me, final Class<? extends Kante> edgeClass, final int cardValue, final GDCollection gdcoll) {
        super(me, edgeClass, cardValue, gdcoll);
    }

}
