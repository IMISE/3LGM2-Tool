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

}
