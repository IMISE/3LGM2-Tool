/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MinCardinalityError extends AbstractCardinalityError {

    /**
     * @param me
     * @param edgeClass
     * @param cardValue
     * @param gdcoll
     */
    public MinCardinalityError(final ModelElement me, final Class<? extends Edge> edgeClass, final GDCollection gdcoll, final int cardValue) {
        super(me, edgeClass, gdcoll, cardValue);
    }

}
