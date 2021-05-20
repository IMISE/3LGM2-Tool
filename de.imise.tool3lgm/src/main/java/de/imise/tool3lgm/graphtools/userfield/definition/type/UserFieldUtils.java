package de.imise.tool3lgm.graphtools.userfield.definition.type;

/**
 * @author AXS (20.05.2021)
 */
public class UserFieldUtils {

    /**
     * @param userField
     * @return <code>true</code> if the userField is a
     *         {@link AccountingUserField}, these are {@link NumberUserField}
     *         and {@link FormulaUserField}
     */
    public static boolean isAccounting(final UserField userField) {
        return userField instanceof AccountingUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link FormulaUserField}
     */
    public static boolean isFormula(final UserField userField) {
        return userField instanceof FormulaUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link NumberUserField}
     */
    public static boolean isNumber(final UserField userField) {
        return userField instanceof NumberUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link SubtypeUserField}
     */
    public static boolean isSubtype(final UserField userField) {
        return userField instanceof SubtypeUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link TabUserField}
     */
    public static boolean isTab(final UserField userField) {
        return userField instanceof TabUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link GroupUserField}
     */
    public static boolean isGroup(final UserField userField) {
        return userField instanceof GroupUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a
     *         {@link SeparatorUserField}
     */
    public static boolean isSeparator(final UserField userField) {
        return userField instanceof SeparatorUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link CheckBoxUserField}
     */
    public static boolean isCheckBox(final UserField userField) {
        return userField instanceof CheckBoxUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link ComboBoxUserField}
     */
    public static boolean isComboBox(final UserField userField) {
        return userField instanceof ComboBoxUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a
     *         {@link SingleLineUserField}
     */
    public static boolean isSingleLine(final UserField userField) {
        return userField instanceof SingleLineUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a
     *         {@link MultiLineUserField}
     */
    public static boolean isMultiLine(final UserField userField) {
        return userField instanceof MultiLineUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a {@link IDUserField}
     */
    public static boolean isID(final UserField userField) {
        return userField instanceof IDUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a
     *         {@link RadioButtonUserField}
     */
    public static boolean isRadioButton(final UserField userField) {
        return userField instanceof RadioButtonUserField;
    }

    /**
     * @param userField
     * @return <code>true</code> if the userField is a
     *         {@link HyperlinkUserField}
     */
    public static boolean isHyperlink(final UserField userField) {
        return userField instanceof HyperlinkUserField;
    }

}
