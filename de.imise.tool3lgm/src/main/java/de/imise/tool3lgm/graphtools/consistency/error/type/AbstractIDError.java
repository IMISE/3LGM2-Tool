package de.imise.tool3lgm.graphtools.consistency.error.type;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;

/**
 * Error for ID userFields where the ID is not unique.
 *
 * @author AXS (17.08.15)
 */
public class AbstractIDError extends AbstractConsistencyError {

    /**
     *
     */
    protected final UserField userField;

    /**
     * @param me
     * @param userField
     */
    public AbstractIDError(final ModelElement me, final UserField userField) {
        super(me);
        this.userField = userField;
    }

    @Override
    public String getErrorFieldString() {
        StringBuilder sb = new StringBuilder(userField.getName());
        sb.append(" = ");
        sb.append(me.getUserFieldInputValue(userField));
        return sb.toString();

    }

    /**
     * @return
     */
    public UserField getUserField() {
        return userField;
    }

}
