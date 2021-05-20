package de.imise.tool3lgm.graphtools.userfield.definition.type;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public class HyperlinkUserField extends ValueUserField {

    /**
     * @param targetClass
     */
    public HyperlinkUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public HyperlinkUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

}
