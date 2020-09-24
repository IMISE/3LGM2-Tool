package de.imise.tool3lgm.graphtools.dialog.search;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;

/**
 * Class for the data structure that represents search options
 * in a model.
 *
 * @author Ich (23.09.2020)
 */
public class SearchOptions {

    /**
     *
     */
    protected enum UserFieldCheckBoxState {
        /** Konstante für Checkboxen Suchen (aktivierte und deaktivierte) */
        CHECKBOXMODE_ALL,
        /** Konstante für aktivierte Checkboxen */
        CHECKBOXMODE_ACTIVATED,
        /** Konstante für deaktivierte Checkboxen Suchen */
        CHECKBOXMODE_NOT_ACTIVATED;
    }

    /** */
    public Class<? extends ModelElement> searchedElementType;

    /** */
    private String inputStringName;

    /** */
    private boolean caseSensitiveName;

    /** */
    private String inputStringDescription;

    /** */
    private boolean caseSensitiveDescription;

    /** */
    private String inputStringUserFields;

    /** */
    private boolean caseSensitiveUserFields;

    /** */
    public UserField.Style userFieldStyle;

    /** */
    public UserFieldCheckBoxState userFieldCheckBoxState;

    /**
     * @return the inputStringName
     */
    public final String getInputStringName() {
        return inputStringName;
    }

    /**
     * @param inputStringName the inputStringName to set
     * @param caseSensitive
     */
    public final void setInputStringName(final String inputStringName, final boolean caseSensitive) {
        this.inputStringName = inputStringName;
        caseSensitiveName = caseSensitive;
    }

    /**
     * @return the inputStringDescription
     */
    public final String getInputStringDescription() {
        return inputStringDescription;
    }

    /**
     * @param inputStringDescription the inputStringDescription to set
     * @param caseSensitive
     */
    public final void setInputStringDescription(final String inputStringDescription, final boolean caseSensitive) {
        this.inputStringDescription = inputStringDescription;
        caseSensitiveDescription = caseSensitive;
    }

    /**
     * @return the inputStringUserFields
     */
    public final String getInputStringUserFields() {
        return inputStringUserFields;
    }

    /**
     * @param inputStringUserFields the inputStringUserFields to set
     * @param caseSensitive
     */
    public final void setInputStringUserFields(final String inputStringUserFields, final boolean caseSensitive) {
        this.inputStringUserFields = inputStringUserFields;
        caseSensitiveUserFields = caseSensitive;
    }

    /**
     * @return the caseSensitiveName
     */
    public final boolean isCaseSensitiveName() {
        return caseSensitiveName;
    }

    /**
     * @return the caseSensitiveDescription
     */
    public final boolean isCaseSensitiveDescription() {
        return caseSensitiveDescription;
    }

    /**
     * @return the caseSensitiveUserField
     */
    public final boolean isCaseSensitiveUserFields() {
        return caseSensitiveUserFields;
    }

}
