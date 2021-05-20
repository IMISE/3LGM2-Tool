package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.definition.type.UserField.Style.CHECK_BOX;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.imise.tool3lgm.graphtools.dialog.search.SearchOptions.UserFieldCheckBoxState;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField.Style;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Alphabetical;
import de.imise.util.swing.component.HistoryComboBox;

public class SearchFunctions {

    /**
     * @param value
     * @param caseSensitive
     * @return
     */
    public static Pattern compilePattern(String value, final boolean caseSensitive) {
        if (!caseSensitive) {
            value = toNonNullLowserCaseString(value);
        }
        // replaces special character \
        value = value.replace("\\", "\\\\");
        // replaces special characters *, ?, (, )
        value = value.replaceAll("\\*", ".*").replaceAll("\\?", ".").replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
        // replaces special characters {, }, [, ]
        value = value.replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]");
        // replaces special character +
        value = value.replaceAll("\\+", "\\\\+");

        Pattern pattern = null;
        if (!value.equals("")) {
            try {
                pattern = Pattern.compile(value);
            } catch (PatternSyntaxException error) {
                Log.show(Log.FATAL, getResString("SEARCH_DIALOG_regexp_hint") + "\n" + error, error);
            }
        }
        return pattern;
    }

    /**
     * @param str
     * @return
     */
    public static final String toNonNullLowserCaseString(final String str) {
        if (str == null) {
            return "";
        }
        return str.toLowerCase();
    }

    /**
     * @param pattern
     * @param string
     * @param caseSensitive
     * @return
     */
    private static final boolean matches(final Pattern pattern, String string, final boolean caseSensitive) {
        if (pattern != null) {
            if (!caseSensitive) {
                string = toNonNullLowserCaseString(string);
            }
            Matcher match = pattern.matcher(string);
            return match.find();
        }
        return true;
    }

    /**
     * @param pattern
     * @param ec
     * @param caseSensitive
     * @return
     */
    public static final boolean matchesName(final Pattern pattern, final ElementContainer ec, final boolean caseSensitive) {
        String matchingString = ec.getName();
        return matches(pattern, matchingString, caseSensitive);
    }

    /**
     * @param pattern
     * @param ec
     * @param caseSensitive
     * @return
     */
    public static final boolean matchesDescription(final Pattern pattern, final ElementContainer ec, final boolean caseSensitive) {
        String matchingString = ec.getDescription();
        return matches(pattern, matchingString, caseSensitive);
    }

    /**
     * @param doc
     * @param namePatternSource
     */
    public static List<ElementContainer> getResult(final GraphDocument doc, final HistoryComboBox namePatternSource) {
        Pattern namePattern = getInputSearchPattern(namePatternSource, false);
        return getResult(doc, ModelElement.class, namePattern, false, null, false, null, false, null, null);
    }

    /**
     * @param comboBox
     * @param caseSensitive
     * @return
     */
    public static Pattern getInputSearchPattern(final HistoryComboBox comboBox, final boolean caseSensitive) {
        String value = comboBox.getText();
        Pattern pattern = compilePattern(value, caseSensitive);
        return pattern;
    }

    /**
     * @param doc
     * @param searchOptions
     * @return
     */
    public static List<ElementContainer> getResult(final GraphDocument doc, final SearchOptions searchOptions) {
        String inputStringName = searchOptions.inputStringName;
        boolean caseSensitiveName = searchOptions.caseSensitiveName;
        Pattern patternName = compilePattern(inputStringName, caseSensitiveName);
        String inputStringDescription = searchOptions.inputStringDescription;
        boolean caseSensitiveDescription = searchOptions.caseSensitiveDescription;
        Pattern patternDescription = compilePattern(inputStringDescription, caseSensitiveDescription);
        String inputStringUserFields = searchOptions.inputStringUserFields;
        boolean caseSensitiveUserField = searchOptions.caseSensitiveUserFields;
        Pattern patternUserFields = compilePattern(inputStringUserFields, caseSensitiveUserField);
        return getResult(doc, searchOptions.searchedElementType, patternName, caseSensitiveName, patternDescription, caseSensitiveDescription, patternUserFields, caseSensitiveUserField, searchOptions.userFieldStyle, searchOptions.userFieldCheckBoxState);
    }

    /**
     * @param doc
     * @param searchedElementType
     * @param patternName
     * @param caseSensitiveName
     * @param patternDescription
     * @param caseSensitiveDescription
     * @param patternUserFields
     * @param caseSensitiveUserFields
     * @param userFieldStyle
     * @param checkBoxMode
     * @return
     */
    public static List<ElementContainer> getResult(final GraphDocument doc, final Class<? extends ModelElement> searchedElementType, final Pattern patternName, final boolean caseSensitiveName, final Pattern patternDescription,
            final boolean caseSensitiveDescription, final Pattern patternUserFields, final boolean caseSensitiveUserFields, final UserField.Style userFieldStyle, final UserFieldCheckBoxState checkBoxMode) {
        List<ElementContainer> searchSet = getInitialTargetElements(doc, searchedElementType);
        for (int i = searchSet.size() - 1; i >= 0; i--) {
            ElementContainer ec = searchSet.get(i);

            if (!matchesAnd(ec, patternName, caseSensitiveName, patternDescription, caseSensitiveDescription, patternUserFields, caseSensitiveUserFields, userFieldStyle, checkBoxMode)) {
                searchSet.remove(i);
                continue;
            }

        }

        return searchSet;
    }

    /**
     * @param patternUserFields
     * @param ec
     * @param caseSensitive
     * @param searchUserFieldStyle if <code>null</code> then all userfield types
     *            are searched for matches otherwise only the specified type
     *            will be searched
     * @param checkBoxMode
     * @return
     */
    private static boolean matchesUserField(Pattern patternUserFields, final ElementContainer ec, final boolean caseSensitive, final UserField.Style searchUserFieldStyle, final UserFieldCheckBoxState checkBoxMode) {
        boolean searchAllUserFields = searchUserFieldStyle == null;
        if (patternUserFields == null && searchAllUserFields) {
            return true;
        }
        if (patternUserFields != null || searchUserFieldStyle.equals(CHECK_BOX)) {
            ModelElement me = ec.getElement();
            if (me.getUserFieldInputValueKeys().isEmpty()) {
                return false;
            }

            // patternUserFields muss gesetzt werden, sonst wird nie etwas removed, wenn
            //z.b. auf checkboxen eingeschränkt wird
            if (patternUserFields == null && !searchAllUserFields) {
                try {
                    patternUserFields = Pattern.compile(" ");
                } catch (PatternSyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            for (UserField userField : me.getUserFieldInputValueKeys()) {

                String userFieldInputValue = me.getUserFieldInputValue(userField);
                // im all-modus und im checkboxmodus muss auf TRUE/FALSE abgefragt werden
                // + zusätzlich muss label stimmen
                Style currentUserFieldStyle = userField.getStyle();
                boolean currentUserFieldIsCheckBox = CHECK_BOX.equals(currentUserFieldStyle);
                boolean isSearchCheckBoxUserFields = CHECK_BOX.equals(searchUserFieldStyle);
                if ((searchAllUserFields || isSearchCheckBoxUserFields) && currentUserFieldIsCheckBox) {
                    // -> Checkbox suchen + zusätzlich muss label stimmen
                    String userFieldName = userField.getName();
                    if (!caseSensitive) {
                        userFieldName = toNonNullLowserCaseString(userFieldName);
                    }
                    Matcher matchNameOfCheckBox = patternUserFields.matcher(userFieldName);
                    if (matchNameOfCheckBox.find()) {
                        if (checkBoxMode == UserFieldCheckBoxState.CHECKBOX_STATE_CHECKED && userFieldInputValue.equals("true")) {
                            return true;
                        } else if (checkBoxMode == UserFieldCheckBoxState.CHECKBOX_STATE_NOT_CHECKED && userFieldInputValue.equals("false")) {
                            return true;
                        } else if (checkBoxMode == UserFieldCheckBoxState.CHECKBOX_STATE_ALL) {
                            return true;
                        }
                    }
                }
                // im allmodus und wenn der Attributtyp übereinstimmt muss im Inhalt gesucht werden
                if (searchAllUserFields || !isSearchCheckBoxUserFields && currentUserFieldStyle.equals(searchUserFieldStyle)) {
                    // -> keine Checkbox suchen, sondern Inhalte Punkte in Kommas umwandeln
                    if (Pattern.matches("[0-9]+\\.[0-9]+", userFieldInputValue)) {
                        userFieldInputValue = userFieldInputValue.replaceAll("\\.", ",");
                    }
                    userFieldInputValue = caseSensitive ? userFieldInputValue : toNonNullLowserCaseString(userFieldInputValue);
                    Matcher matchUserFieldValue = patternUserFields.matcher(userFieldInputValue);
                    if (matchUserFieldValue.find()) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    /**
     * @param ec
     * @param patternName
     * @param caseSensitiveName
     * @param patternDescription
     * @param caseSensitiveDescription
     * @param patternUserFields
     * @param caseSensitive
     * @param searchUserFieldStyle
     * @param checkBoxMode
     * @return
     */
    public static boolean matchesAnd(final ElementContainer ec, final Pattern patternName, final boolean caseSensitiveName, final Pattern patternDescription, final boolean caseSensitiveDescription, final Pattern patternUserFields,
            final boolean caseSensitive, final UserField.Style searchUserFieldStyle, final UserFieldCheckBoxState checkBoxMode) {
        if (!matchesName(patternName, ec, caseSensitiveName)) {
            return false;
        }
        if (!matchesDescription(patternDescription, ec, caseSensitiveDescription)) {
            return false;
        }
        if (!matchesUserField(patternUserFields, ec, caseSensitive, searchUserFieldStyle, checkBoxMode)) {
            return false;
        }
        return true;
    }
    /**
     * @param doc
     * @param searchedElementType
     * @return all ElementContainer of the given type in the given
     *         {@link GraphDocument}
     */
    private static List<ElementContainer> getInitialTargetElements(final GraphDocument doc, final Class<? extends ModelElement> searchedElementType) {
        List<ElementContainer> initialTargetElements = doc.getElementContainers(searchedElementType, true);
        GDCollection gdcoll = doc.getCollection();
        GraphDocument mainDoc = gdcoll.getMainDoc();
        if (doc != mainDoc) {
            for (ElementContainer ec : mainDoc.getElementContainers(searchedElementType, true)) {
                if (ec.isUnique()) {
                    Alphabetical.insert(initialTargetElements, ec);
                }
            }
        }
        return initialTargetElements;
    }

}
