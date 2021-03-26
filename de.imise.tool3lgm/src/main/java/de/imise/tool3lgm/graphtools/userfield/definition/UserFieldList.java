package de.imise.tool3lgm.graphtools.userfield.definition;

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

}
