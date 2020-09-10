package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (20.03.2016)
 */
public class IDEmptyError extends AbstractIDError {

    /**
     * @param me
     * @param errorField
     */
    public IDEmptyError(final ModelElement me, final Object errorField) {
        super(me, errorField);
    }

}
