package de.imise.tool3lgm.graphtools.consistency.error.type;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;

/**
 * @author AXS (20.03.2016)
 */
public class IDEmptyError extends AbstractIDError {

    /**
     * @param me
     * @param userField
     */
    public IDEmptyError(final ModelElement me, final UserField userField) {
        super(me, userField);
    }

}
