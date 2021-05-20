package de.imise.tool3lgm.graphtools.userfield.definition.type;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public class RadioButtonUserField extends ListUserField {

    /**
     * @param targetClass
     */
    public RadioButtonUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public RadioButtonUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

    @Override
    public int compareValues(final UserFieldTarget me1, final UserFieldTarget me2) {
        String v1 = me1.getValue(this);
        String v2 = me2.getValue(this);

        if (v1 == null || v1.isEmpty()) {
            if (v2 == null || v2.isEmpty()) {
                return 0;
            }
            return -1;
        }
        if (v2 == null || v2.isEmpty()) {
            return 1;
        }
        Integer i1 = listValues.indexOf(v1);
        Integer i2 = listValues.indexOf(v2);
        int retval = i1.compareTo(i2);
        return retval;
    }

}
