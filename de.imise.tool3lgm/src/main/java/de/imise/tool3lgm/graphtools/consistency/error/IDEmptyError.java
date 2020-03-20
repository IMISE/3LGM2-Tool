package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * @author AXS (20.03.2016)
 */
public class IDEmptyError extends AbstractIDError {

    /**
     * @param me
     * @param errorField
     * @param gdcoll
     */
    public IDEmptyError(final ModelElement me, final Object errorField, final GDCollection gdcoll) {
        super(me, errorField, gdcoll);
    }

}
