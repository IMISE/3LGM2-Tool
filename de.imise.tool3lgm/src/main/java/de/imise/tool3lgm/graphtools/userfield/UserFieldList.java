package de.imise.tool3lgm.graphtools.userfield;

/**
 * @author Thomas Rudert
 */
public class UserFieldList extends UserFieldTargetSpecificList<UserField> {

    /**
     * @param targetClass
     */
    public UserFieldList(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    @Override
    protected int getInsertIndex(final UserField userField) {
        return userField.hasStyle(UserField.Style.FORMAT) ? 0 : super.getInsertIndex(userField);
    }

}
