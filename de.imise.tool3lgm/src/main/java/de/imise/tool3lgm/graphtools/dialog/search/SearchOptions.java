package de.imise.tool3lgm.graphtools.dialog.search;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.swing.component.HistoryComboBox;

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

    /** the history of the {@link HistoryComboBox} with the search string for the names */
    public List<String> inputHistoryName;

    /** the history of the {@link HistoryComboBox} with the search string for the descriptions */
    public List<String> inputHistoryDescription;

    /** the history of the {@link HistoryComboBox} with the search string for the userfields */
    public List<String> inputHistoryUserFields;

}
