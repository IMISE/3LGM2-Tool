package de.imise.tool3lgm.graphtools.userfield.definition;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SUBTYPE;

import java.security.InvalidParameterException;

import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.NameAndDescriptionSource;

/**
 * A Subtype is basically just a delegate for a UserField with the style
 * SUBTYPE. But so that the thing is really called SubType and not just
 * UserField, that's what this class is for.
 *
 * @author AXS (03.05.2021)
 */
public class SubType implements NameAndDescriptionSource, IDSource {

    /**
     * Dummy subtype to mark UserFields for which the subtype has already been
     * set, but which do not have a subtype at all (as opposed to
     * <code>null</code>, which means that the subtype of the UserField has not
     * yet been checked/set at all).
     */
    public static final SubType DUMMY_SUBTYPE = new SubType();

    /**
     *
     */
    private final UserField subTypeUserField;

    /**
     *
     */
    private final Class<? extends ModelElement> superClass;

    /**
     * This constructor should be used only for creating the DUMMY_SUBTYPE
     */
    private SubType() {
        superClass = ModelElement.class;
        subTypeUserField = new UserField(superClass, SUBTYPE);
    }

    /**
     *
     */
    public SubType(final UserField subTypeUserField) {
        if (!subTypeUserField.hasStyle(SUBTYPE)) {
            throw new InvalidParameterException();
        }
        this.subTypeUserField = subTypeUserField;
        superClass = subTypeUserField.getTargetClass().asSubclass(ModelElement.class);
    }

    /**
     * @return the subTypeUserField
     */
    public final UserField getSubTypeUserField() {
        return subTypeUserField;
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getSuperClass() {
        return superClass;
    }

    @Override
    public String getName() {
        return subTypeUserField.getName();
    }

    @Override
    public String getDescription() {
        return subTypeUserField.getDescription();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getID() {
        return subTypeUserField.getID();
    }

    /**
     * @param userField
     * @return
     */
    public boolean hasUserField(final UserField userField) {
        return subTypeUserField.equals(userField);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (subTypeUserField == null ? 0 : subTypeUserField.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SubType other = (SubType) obj;
        if (subTypeUserField == null) {
            if (other.subTypeUserField != null) {
                return false;
            }
        } else if (!subTypeUserField.equals(other.subTypeUserField)) {
            return false;
        }
        return true;
    }

    /**
     * @param subType
     * @return
     */
    public static final boolean isDummy(final SubType subType) {
        return subType == null || DUMMY_SUBTYPE.equals(subType);
    }

}
