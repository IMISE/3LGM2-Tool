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
    //    private Pattern patternName;

    /** */
    private boolean caseSensitiveName;

    /** */
    private String inputStringDescription;

    /** */
    //    private Pattern patternDescription;

    /** */
    private boolean caseSensitiveDescription;

    /** */
    private String inputStringUserFields;

    /** */
    //    private Pattern patternUserFields;

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
        //        patternName = SearchFunctions.compilePattern(inputStringName, caseSensitive);
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
        //        patternDescription = SearchFunctions.compilePattern(inputStringDescription, caseSensitive);
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
        //        patternUserFields = SearchFunctions.compilePattern(inputStringUserFields, caseSensitive);
    }

    //    /**
    //     * @return the patternName
    //     */
    //    public final Pattern getPatternName() {
    //        return patternName;
    //    }
    //
    //    /**
    //     * @return the patternDescription
    //     */
    //    public final Pattern getPatternDescription() {
    //        return patternDescription;
    //    }
    //
    //    /**
    //     * @return the patternUserFields
    //     */
    //    public final Pattern getPatternUserFields() {
    //        return patternUserFields;
    //    }
    //
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
