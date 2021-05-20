package de.imise.tool3lgm.xml;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldNumberFormat;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.definition.type.CheckBoxUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.ComboBoxUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.FormulaUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.GroupUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.HyperlinkUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.IDUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.ListUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.MultiLineUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.NumberUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.RadioButtonUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.SeparatorUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.SingleLineUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.SubtypeUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.TabUserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;

/**
 * Es gab bis zum Entstehen dieser Klasse nur eine finale Klasse UserField, die
 * als Enum ihren Style mitbekommen hat. Nun ist jeder Style eine eigene
 * Unterklasse von UserField. Beim Einlesen wird der Style aber erst
 *
 * @author AXS (11.05.2021)
 */
public class DummyUserField {

    private static enum Style {
        SINGLE_LINE,
        MULTI_LINE,
        CHECK_BOX,
        COMBO_BOX,
        RADIO_BUTTON,
        HYPERLINK,
        ID,
        NUMBER,
        FORMULA,
        TAB,
        GROUP,
        SEPARATOR,
        SUBTYPE;
    }

    /**
     *
     */
    private final Class<? extends UserFieldTarget> targetClass;

    private final String id;

    /**
     *
     */
    private Style style;

    /**
     *
     */
    private String name = "";

    /**
     *
     */
    private String description = "";

    /**
     *
     */
    private boolean treeVisibility;

    /**
     *
     */
    private boolean showDescriptionInDialog;

    /**
     *
     */
    private String formulaString;

    /**
     * <code>true</code>, wenn die Formel dieses UserFields eine einfache
     * Teilwertsumme mit oder ohne Verteilungsgewicht ist.
     */
    private boolean simplePartValueSumFormula = false;

    /**
     * UserField mit dem Style <code>FORMAT_STYLE</code>, das vorgibt, wie der
     * Zahlenwert dieses UserFields formatiert werden soll.
     */
    private UserFieldNumberFormat numberFormat;

    /**
     * Beinhaltet alle Einträge, die in UserFields mit den Styles ComboBox und
     * RadioButton auftauchen sollen.
     */
    private ArrayList<String> listValues;

    /**
     * @param targetClass
     * @param id
     */
    public DummyUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        this.targetClass = targetClass;
        this.id = id;
    }

    /**
     * @param targetClass
     * @return
     */
    public static DummyUserField createIDUSerField(final Class<? extends UserFieldTarget> targetClass) {
        DummyUserField dummyUserField = new DummyUserField(targetClass, null);
        dummyUserField.style = Style.ID;
        return dummyUserField;
    }

    /**
     * @param fieldName
     * @param value
     * @param definitions
     * @return
     */
    public boolean putXMLFieldString(final String fieldName, String value, final UserFieldDefinitions definitions) {
        if (fieldName.equals("userFieldName")) {
            name = value;
        } else if (fieldName.equals("userFieldDescription")) {
            description = value;
        } else if (fieldName.equals("userFieldStyle")) {
            if (value.equals("userFieldStyle")) {
                //Style.NUMER was Style.CLASSIFICATION_NUMBER and
                //Style.FORMULA was Style.CLASSIFICATION_NUMBER_FORMULA
                if (value.equals("CLASSIFICATION_NUMBER")) {
                    value = Style.NUMBER.name();
                } else if (value.equals("CLASSIFICATION_NUMBER_FORMULA")) {
                    value = Style.FORMULA.name();
                } else if (value.equals("FORMAT")) {
                    //formats are no longer UserFields
                    value = null;
                }
            }

            try {
                //neue Modelle geben des Style als String an.
                style = Style.valueOf(value);
            } catch (Exception e) {
                //Alte Modelle geben den Style als Index des Styles im enum Style an
                int index = Integer.parseInt(value);
                //das hier muss sein, weil ein Style (mit Nummer 8) rausgeflogen ist und damit die Reihenfolge
                //sonst nicht mehr stimmt
                if (index > 7) {
                    index--;
                }
                style = Style.values()[index];
            }
        } else if (fieldName.equals("userFieldTreeVis")) {
            treeVisibility = Boolean.valueOf(value).booleanValue();
        } else if (fieldName.equals("userFieldShowDescriptionInDialog")) {
            showDescriptionInDialog = Boolean.valueOf(value).booleanValue();
        } else if (fieldName.equals("userFieldStandardValue")) {
            addListValue(value);
        } else if (fieldName.equals("userFieldFormula")) {
            setFormula(value);
            style = Style.FORMULA;
        } else if (fieldName.equals("userFieldFormatHash")) {
            setNumberFormat(definitions.getNumberFormat(value));
        } else {
            return false;
        }
        return true;
    }

    /**
     * Fügt einen Listeneintrag hinzu
     *
     * @param value
     */
    public void addListValue(final String value) {
        if (value == null) {
            return;
        }
        if (listValues == null) {
            listValues = new ArrayList<>();
        }
        listValues.add(value);
    }

    /**
     * Setzt die Kennzahlformel.
     *
     * @param formulaString
     * @return boolean true: Wenn es sich bei dem <code>userField</code> um eine
     *         sekundäre Kennzahl handelt, d.h. eine Kennzahl, die
     *         Kennzahlformeln repräsentiert - ansonsten false
     */
    public boolean setFormula(final String formulaString) {
        if (style != Style.FORMULA) {
            return false;
        }
        this.formulaString = formulaString;
        simplePartValueSumFormula = CostingUtil.isSimpleFractionValueSumFormula(formulaString);
        return true;
    }

    /**
     * Setzt das Format, mit dem Zahlenwerte dieses Userfields formatiert werden
     * können.
     *
     * @param numberFormat
     */
    public void setNumberFormat(final UserFieldNumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    /**
     * @return
     */
    public UserField convert() {
        UserField userField = null;
        switch (style) {
        case SINGLE_LINE:
            userField = new SingleLineUserField(targetClass, id);
            break;
        case MULTI_LINE:
            userField = new MultiLineUserField(targetClass, id);
            break;
        case CHECK_BOX:
            userField = new CheckBoxUserField(targetClass, id);
            break;
        case COMBO_BOX:
            userField = new ComboBoxUserField(targetClass, id);
            break;
        case RADIO_BUTTON:
            userField = new RadioButtonUserField(targetClass, id);
            break;
        case NUMBER:
            userField = new NumberUserField(targetClass, id);
            break;
        case FORMULA:
            FormulaUserField formulaUserField = new FormulaUserField(targetClass, id);
            formulaUserField.setFormula(formulaString);
            userField = formulaUserField;
            break;
        case HYPERLINK:
            userField = new HyperlinkUserField(targetClass, id);
            break;
        case ID:
            userField = new IDUserField(targetClass, id);
            break;
        case TAB:
            userField = new TabUserField(targetClass, id);
            break;
        case GROUP:
            userField = new GroupUserField(targetClass, id);
            break;
        case SEPARATOR:
            userField = new SeparatorUserField(targetClass, id);
            break;
        case SUBTYPE:
            userField = new SubtypeUserField(targetClass, id);
            break;
        default:
            break;
        }
        userField.setName(name);
        userField.setDescription(description);
        userField.setTreeVisibility(treeVisibility);
        userField.setShowDescriptionInDialog(showDescriptionInDialog);
        if (listValues != null && userField instanceof ListUserField) {
            ListUserField listUserField = (ListUserField) userField;
            for (String listValue : listValues) {
                userField.addListValue(listValue);
            }
        }
        return userField;
    }

}
