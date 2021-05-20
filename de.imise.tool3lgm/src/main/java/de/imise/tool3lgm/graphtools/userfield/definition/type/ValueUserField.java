package de.imise.tool3lgm.graphtools.userfield.definition.type;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public abstract class ValueUserField extends UserField {

    /**
     * @param targetClass
     */
    public ValueUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public ValueUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

    @Override
    public int compareValues(final UserFieldTarget me1, final UserFieldTarget me2) {
        return alphabeticalCompare(this, me1, me2);
    }

}
