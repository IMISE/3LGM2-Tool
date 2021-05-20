package de.imise.tool3lgm.graphtools.userfield.definition.type;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public class CheckBoxUserField extends ValueUserField {

    /**
     * Wert eines UserFields vom Typ {@link Style#CHECK_BOX}: "Häkchen gesetzt"
     */
    public static final String CHECKBOX_TRUE = "true";

    /**
     * Wert eines UserFields vom Typ {@link Style#CHECK_BOX}: "Häkchen nicht
     * gesetzt"
     */
    public static final String CHECKBOX_FALSE = "false";

    /**
     * @param targetClass
     */
    public CheckBoxUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public CheckBoxUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

    @Override
    public int compareValues(final UserFieldTarget me1, final UserFieldTarget me2) {
        String v1 = me1.getValue(this);
        String v2 = me2.getValue(this);
        if (v1 == null) {
            return v2 == null ? 0 : -1;
        }
        if (v2 == null) {
            return 1;
        }
        boolean b1 = CHECKBOX_TRUE.equals(v1);
        boolean b2 = CHECKBOX_TRUE.equals(v2);
        if (b1) {
            return b2 ? 0 : 1;
        }
        return b2 ? 0 : -1;
    }

}
