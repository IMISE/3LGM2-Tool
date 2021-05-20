/**
 * 
 */
package de.imise.tool3lgm.graphtools.userfield.definition.type;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 *
 */
public abstract class StructureUserField extends UserField {

    /**
     * @param targetClass
     */
    public StructureUserField(Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public StructureUserField(Class<? extends UserFieldTarget> targetClass, String id) {
        super(targetClass, id);
    }

}
