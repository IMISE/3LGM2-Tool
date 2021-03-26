package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.util.swing.component.HistoryComboBox;

/**
 * Class for the data structure that represents search options in a model.
 *
 * @author Ich (23.09.2020)
 */
public class SearchOptions {

    /**
     * The order is importent. Its the same order they are displayed in the
     * {@link BasicSearchOptionsPanel}
     */
    protected enum UserFieldCheckBoxState {
        /** Konstante für Checkboxen Suchen (aktivierte und deaktivierte) */
        CHECKBOX_STATE_ALL,
        /** Konstante für aktivierte Checkboxen */
        CHECKBOX_STATE_CHECKED,
        /** Konstante für deaktivierte Checkboxen Suchen */
        CHECKBOX_STATE_NOT_CHECKED;

        @Override
        public String toString() {
            return getResString(this);
        }
    }

    /** */
    public Class<? extends ModelElement> searchedElementType;

    /** */
    public String inputStringName;

    /** */
    public boolean caseSensitiveName;

    /** */
    public String inputStringDescription;

    /** */
    public boolean caseSensitiveDescription;

    /** */
    public String inputStringUserFields;

    /** */
    public boolean caseSensitiveUserFields;

    /** */
    public UserField.Style userFieldStyle;

    /** */
    public UserFieldCheckBoxState userFieldCheckBoxState;

    /**
     * the history of the {@link HistoryComboBox} with the search string for the
     * names
     */
    public List<String> inputHistoryName;

    /**
     * the history of the {@link HistoryComboBox} with the search string for the
     * descriptions
     */
    public List<String> inputHistoryDescription;

    /**
     * the history of the {@link HistoryComboBox} with the search string for the
     * userfields
     */
    public List<String> inputHistoryUserFields;

}
