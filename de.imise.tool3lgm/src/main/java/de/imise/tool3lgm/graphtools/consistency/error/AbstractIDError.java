package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;

/**
 * Error for ID userFields where the ID is not unique.
 *
 * @author AXS (17.08.15)
 */
public class AbstractIDError extends AbstractConsistencyError {

    /**
     * @param me
     * @param errorField
     */
    public AbstractIDError(final ModelElement me, final Object errorField) {
        super(me, errorField);
    }

    @Override
    public String getErrorFieldString() {
        UserField userField = getUserField();
        StringBuilder sb = new StringBuilder(userField.getName());
        sb.append(" = ");
        sb.append(me.getUserFieldInputValue(userField));
        return sb.toString();

    }

    /**
     * @return
     */
    public UserField getUserField() {
        return (UserField) errorField;
    }

}
