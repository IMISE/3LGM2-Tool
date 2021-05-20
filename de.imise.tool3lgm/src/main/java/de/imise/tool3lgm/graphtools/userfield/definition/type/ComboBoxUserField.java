package de.imise.tool3lgm.graphtools.userfield.definition.type;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public class ComboBoxUserField extends ListUserField {

    /**
     * @param targetClass
     */
    public ComboBoxUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public ComboBoxUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

}
