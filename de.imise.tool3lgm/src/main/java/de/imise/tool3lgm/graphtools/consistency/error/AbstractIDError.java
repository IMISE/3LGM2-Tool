package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.userfield.UserField;

/**
 * Error for ID userFields where the ID is not unique.
 *
 * @author AXS (17.08.15)
 */
public class AbstractIDError extends AbstractError {

    /**
     * @param me
     * @param errorField
     * @param gdcoll
     */
    public AbstractIDError(final ModelElement me, final Object errorField, final GDCollection gdcoll) {
        super(me, errorField, gdcoll);
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
