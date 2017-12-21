package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.userfield.UserField;

public class AbstractIDError extends AbstractError {

    public AbstractIDError(final ModelElement me, final Object errorField, final GDCollection gdcoll) {
        super(me, errorField, gdcoll);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getErrorFieldString() {
        UserField userField = getUserField();
        StringBuilder sb = new StringBuilder(userField.getName());
        sb.append(" = ");
        sb.append(me.getUserFieldInputValue(userField));
        return sb.toString();

    }

    public UserField getUserField() {
        return (UserField) errorField;
    }

}
