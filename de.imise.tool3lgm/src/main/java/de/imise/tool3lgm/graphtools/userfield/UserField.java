package de.imise.tool3lgm.graphtools.userfield;

import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.GLOBAL_FORMAT_IDENTIFIER_CLASS;
import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.xml.XMLCharacterCoder;

/**
 * Ein <code>UserField</code> ist ein Element bzw. Objekt, dass Element- und Kantenklassen zugewiesen werden kann. Mit Hilfe dieses Elementes können
 * zusätzliche Eigenschaften für Element- und Kantenklassen deklariert und definiert werden. Es kann somit eine benutzergesteuerte Erweiterung des
 * Metamodells vorgenommen werden. die Klasse <code>UserField</code> hat als Attribute eine Bezeichung, eine Beschreibung, einen <code>hashCode</code>
 * , einen <code>style</code> und einen Identifikator, der kenntlich macht, zu welcher Elementklasse das <code>UserField</code> gehört.
 * 
 * @author Thomas Rudert
 */
public final class UserField implements Cloneable, Comparator<ModelElement> {

    public static enum Style {
        SEPARATOR {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return 0;
            }
        },
        SINGLE_LINE {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return alphabeticalCompare(uf, me1, me2);
            }
        },
        MULTI_LINE {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return alphabeticalCompare(uf, me1, me2);
            }
        },
        CHECK_BOX {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                String v1 = uf.getValue(me1);
                String v2 = uf.getValue(me2);
                if (v1 == null) {
                    return v2 == null ? 0 : -1;
                }
                if (v2 == null) {
                    return 1;
                }

                boolean b1 = UserField.CHECKBOX_TRUE.equals(v1);
                boolean b2 = UserField.CHECKBOX_TRUE.equals(v2);
                if (b1) {
                    return b2 ? 0 : 1;
                }
                return b2 ? 0 : -1;
            }
        },
        COMBO_BOX {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return alphabeticalCompare(uf, me1, me2);
            }
        },
        RADIO_BUTTON {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                String v1 = uf.getValue(me1);
                String v2 = uf.getValue(me2);

                if (v1 == null || v1.isEmpty()) {
                    if (v2 == null || v2.isEmpty()) {
                        return 0;
                    }
                    return -1;
                }
                if (v2 == null || v2.isEmpty()) {
                    return 1;
                }

                Integer i1 = uf.listValues.indexOf(v1);
                Integer i2 = uf.listValues.indexOf(v2);
                int retval = i1.compareTo(i2);
                return retval;
            }
        },
        HYPERLINK {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return alphabeticalCompare(uf, me1, me2);
            }
        },
        ID {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return alphabeticalCompare(uf, me1, me2);
            }
        },
        CLASSIFICATION_NUMBER {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return numberCompare(uf, me1, me2);
            }
        },
        CLASSIFICATION_NUMBER_FORMULA {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return numberCompare(uf, me1, me2);
            }
        },
        FORMAT {
            @Override
            int compare(final UserField uf, final ModelElement me1, final ModelElement me2) {
                return alphabeticalCompare(uf, me1, me2);
            }
        },
        /*
         * PANEL {
         * @Override int compare(UserField uf, ModelElement me1, ModelElement me2) { return alphabeticalCompare(uf, me1, me2); } }
         */
        ;

        public static final Set<Style> CLASSIFICATION_NUMBER_STYLES = ImmutableSet.of(CLASSIFICATION_NUMBER, CLASSIFICATION_NUMBER_FORMULA);

        /**
         * Vergleicht die beiden UserFields hinsichtlich ihres Wertes bezüglich des Modelelements.
         * <p>
         * Bedingungen für die Vergleichbarkeit sind:
         * <li>Die UserFields haben den selben Style
         * <li>Beide UserFields sind für die Klasse des Modelements definiert
         * 
         * @param uf1
         * @param uf2
         * @param me
         * @return
         */
        abstract int compare(UserField uf, ModelElement me1, ModelElement me2);

        /** Alphabetischer Vergleich der jeweiligen Werte (siehe {@link String#compareTo(String)}) */
        private static int alphabeticalCompare(final UserField uf, final ModelElement me1, final ModelElement me2) {
            String v1 = uf.getValue(me1);
            String v2 = uf.getValue(me2);
            if (v1 == null) {
                return v2 == null ? 0 : -1;
            }
            if (v2 == null) {
                return 1;
            }
            return v1.compareTo(v2);
        }

        /** Vergleich der jeweiligen Werte für Kennzahlen/Kennzahlformeln */
        private static int numberCompare(final UserField uf, final ModelElement me1, final ModelElement me2) {
            String v1 = uf.getValue(me1);
            String v2 = uf.getValue(me2);
            if (v1 == null) {
                return v2 == null ? 0 : -1;
            }
            if (v2 == null) {
                return 1;
            }
            if (isIgnoreableError(v1) && isIgnoreableError(v2)) {
                return v1.compareTo(v2);
            }
            if (isIgnoreableError(v1) && isCriticalError(v2)) {
                return -1;
            }
            if (isCriticalError(v1) && isIgnoreableError(v2)) {
                return 1;
            }
            if (isCriticalError(v1) && isCriticalError(v2)) {
                return v1.compareTo(v2);
            }
            if (isCriticalError(v1) || isIgnoreableError(v1)) {
                return 1;
            }
            if (isCriticalError(v2) || isIgnoreableError(v2)) {
                return -1;
            }

            try {
                return new BigDecimal(v1).compareTo(new BigDecimal(v2));
            } catch (NumberFormatException e) {
                return v1.compareTo(v2);
            }

        }
    }

    //Die Konstanten für die Styles
    //	/**
    //	 * Comment for <code>SEPARATOR_STYLE</code>
    //	 * /
    //	public static final int SEPARATOR_STYLE = 0;
    //
    //	/**
    //	 * Comment for <code>SINGLE_LINE_STYLE</code>
    //	 * /
    //	public static final int SINGLE_LINE_STYLE = 1;
    //
    //	/**
    //	 * Comment for <code>MULTI_LINE_STYLE</code>
    //	 * /
    //	public static final int MULTI_LINE_STYLE = 2;
    //
    //	/**
    //	 * Comment for <code>CHECK_BOX_STYLE</code>
    //	 * /
    //	public static final int CHECK_BOX_STYLE = 3;
    //
    //	/**
    //	 * Comment for <code>COMBO_BOX_STYLE</code>
    //	 * /
    //	public static final int COMBO_BOX_STYLE = 4;
    //
    //	/**
    //	 * Comment for <code>RADIO_BUTTON_STYLE</code>
    //	 * /
    //	public static final int RADIO_BUTTON_STYLE = 5;
    //
    //	/**
    //	 * Comment for <code>HYPERLINK_STYLE</code>
    //	 * /
    //	public static final int HYPERLINK_STYLE = 6;
    //
    //	/**
    //	 * Comment for <code>CLASSIFICATION_NUMBER_STYLE</code>
    //	 * /
    //	public static final int CLASSIFICATION_NUMBER_STYLE = 7;
    //
    //	/**
    //	 * Comment for <code>CLASSIFICATION_WEIGHTING_STYLE</code>
    //	 * /
    //	public static final int _CLASSIFICATION_WEIGHTING_STYLE = 8;
    //
    //	/**
    //	 * Comment for <code>CLASSIFICATION_NUMBER_FORMULA_STYLE</code>
    //	 * /
    //	public static final int CLASSIFICATION_NUMBER_FORMULA_STYLE = 9;
    //
    //	/**
    //	 * Comment for <code>FORMAT_STYLE</code>
    //	 * /
    //	public static final int FORMAT_STYLE = 10;

    /**
     * Wert eines UserFields vom Typ {@link Style#CHECK_BOX}: "Häkchen gesetzt"
     */
    public static final String CHECKBOX_TRUE = "true";

    /**
     * Wert eines UserFields vom Typ {@link Style#CHECK_BOX}: "Häkchen nicht gesetzt"
     */
    public static final String CHECKBOX_FALSE = "false";

    /**
     * Repräsentiert den String from_part_to_whole
     */
    public static final String DIRECTION_FROM_PART_TO_WHOLE = "from_part_to_whole";

    /**
     * Repräsentiert den Sting from_whole_to_part
     */
    public static final String DIRECTION_FROM_WHOLE_TO_PART = "from_whole_to_part";

    /**
     * TODO:Alle Funktionen durch diesen ENUM ersetzen!
     */
    //	public static enum FUNC {SUM, MULT, TWSUM, AVG, INDI, REF, MIN, MAX};

    /**
     * Repräsentiert den Sting SUM
     */
    public static final String ACCOUNTING_FUNCTION_SUM = "SUM";

    /**
     * Repräsentiert den String MULT
     */
    public static final String ACCOUNTING_FUNCTION_MULT = "MULT";

    /**
     * Repräsentiert den Sting TWSUM
     */
    public static final String ACCOUNTING_FUNCTION_TWSUM = "TWSUM";

    /**
     * Repräsentiert den Sting AVG
     */
    public static final String ACCOUNTING_FUNCTION_AVG = "AVG";

    /**
     * Repräsentiert den Sting INDI
     */
    public static final String ACCOUNTING_FUNCTION_INDI = "INDI";

    /**
     * Repräsentiert den Sting REF
     */
    public static final String ACCOUNTING_FUNCTION_REF = "REF";

    /**
     * Repräsentiert den Sting MIN
     */
    public static final String ACCOUNTING_FUNCTION_MIN = "MIN";

    /**
     * Repräsentiert den Sting MAX
     */
    public static final String ACCOUNTING_FUNCTION_MAX = "MAX";

    /**
     * Präfix aller Hash-Strings von <code>UserField</code>s
     */
    public static final String USERFIELD_HASH_STRING_PREFIX = "USERFIELD";

    //////////////////////////////////////////////////////////////////////
    // Werte der Kennzahl- und Kennzahlformeluserfields in Sonderfällen //
    //////////////////////////////////////////////////////////////////////

    /**
     * Ein konstant leerer <code>String</code>. <code>UserFieldTarget</code>s, die keinen Wert für dieses <code>UserField</code> liefern können, geben
     * diesen <code>String</code> zurück.
     */
    public static final String EMPTY_STRING = "EMPTY_VALUE";
    public static final String NO_ELEMENTS_CONNECTED = "NO_ELEMENTS_CONNECTED";
    public static final String POSITIVE_VALUES_ONLY = "POSITIVE_VALUES_ONLY";
    public static final String NUMBER_FORMAT_ERROR = "NUMBER_FORMAT_ERROR";
    public static final String ERROR_DIVIDE_BY_ZERO = "DIVIDE_BY_ZERO";
    public static final String ERROR_CROSS_REFERENCE_IN_FORMULA_DEFINITION = "CROSS_REFERENCE";

    /**
     * Dieses Set beinhaltet Strings, die Formel-UserFields als Werte annehmen, wenn sie sich nicht berechnen lassen oder irgendwelche anderen
     * Probleme auftreten. <code>NUMBER_FORMAT_ERROR</code> und <code>EMPTY_STRING</code> können auch bei Kennzahl-UserFields auftreten. beinhaltet:
     */
    private static final Set<String> ERROR_SET = ImmutableSet.of(POSITIVE_VALUES_ONLY, NUMBER_FORMAT_ERROR, ERROR_DIVIDE_BY_ZERO, ERROR_CROSS_REFERENCE_IN_FORMULA_DEFINITION);

    /**
     * Dieses Set enthält alle Strings, die Werte von UserFields sein können, die bei Berechnungen ignoriert werden sollen. D.h. wenn eine
     * Summen-/Differenz/Divisions-/Multiplikationsfunktion auf ein UserField mit einem <code>String</code> aus diesem Set als value trifft, wird
     * dafür der Wert 0 angenommen.
     */
    private static final Set<String> IGNOREABLE_ERROR_SET = ImmutableSet.of(EMPTY_STRING, NO_ELEMENTS_CONNECTED);

    /**
     * Dieses Set beinhaltet Strings, die interne Verrechnungsfunktionen kennzeichen beinhaltet:
     */
    public static final Set<String> ACCOUNTING_FUNCTIONS_SET = ImmutableSet.of(ACCOUNTING_FUNCTION_SUM, ACCOUNTING_FUNCTION_TWSUM, ACCOUNTING_FUNCTION_MAX, ACCOUNTING_FUNCTION_MIN, ACCOUNTING_FUNCTION_MULT, ACCOUNTING_FUNCTION_AVG,
            ACCOUNTING_FUNCTION_INDI, ACCOUNTING_FUNCTION_REF);

    ///////////////////////
    // Format-UserFields //
    ///////////////////////

    /**
     * Dieser String wird im Format den Nachkommastellen vorangestellt, damit man ihn von anderen Formatinformationen unterscheiden kann.
     */
    private static final String FORMAT_DECIMAL_PLACES_PREFIX = "d";

    /**
     * Dieser String wird im Format der Eiheit vorangestellt, damit man ihn von anderen Formatinformationen unterscheiden kann.
     */
    private static final String FORMAT_UNIT_PREFIX = "u";

    /**
     * String der als Einheit bei angezeigt werden soll
     */
    private String formatUnit = null;

    /**
     * Das eigentliche Format, wenn dieses <code>UserField</code> den Style <code>FORMAT_STYLE</code> besitzt.
     */
    private NumberFormat numberFormat = null;

    /**
     * Kennzahl für Nummerierungsteil der UserField_hash_codes
     */
    private static int id = 0;

    /**
     * Ist der <code>String</code>, durch den das <code>UserField</code> eindeutig identifizierbar wird.
     */
    private String hashCode;

    /**
     * Gibt an, zu welcher Klasse das <code>UserField</code> gehört.
     */
    private Class<? extends UserFieldTarget> targetClass;

    /**
     * Ist der Name des <code>UserField</code>s.
     */
    private String name = "";

    /**
     * Ist die Beschreibung des <code>UserField</code>s.
     */
    private String description = "";

    /**
     * Gibt an, von welcher Form das <code>userField</code> sein soll. / private int style = -1; /** Art dieses {@link UserField}s. Immer ein Wert aus
     * {@link Style}
     */
    private Style style = null;

    /**
     * Gibt an, ob das <code>UserField</code> im Modellbrowser sichtbar sein soll.
     */
    private boolean treeVisibility = true;

    /**
     * Beinhaltet alle Einträge, die in UserFields mit den Styles ComboBox, RadioButton und CheckBox auftauchen sollen.
     */
    private ArrayList<String> listValues;

    /**
     * Interne Repräsentation von Kennzahlformeln
     */
    private String formulaString;

    /**
     * <code>true</code>, wenn die Formel dieses UserFields eine einfache Teilwertsumme mit oder ohne Verteilungsgewicht ist.
     */
    private boolean simplePartValueSumFormula;

    /**
     * Die Definition in der sich dieses UserField befindet. Wird gebraucht, um z.B. das Format-<code>UserField</code> zu finden.
     */
    private UserFieldDefinitions definitions;

    /**
     * UserField mit dem Style <code>FORMAT_STYLE</code>, das vorgibt, wie der Zahlenwert dieses UserFields formatiert werden soll.
     */
    private UserField formatUserField;

    /**
     * Wenn <code>true</code> werden nur positive Werte akzeptiert.
     */
    private boolean positiveOnly;

    /**
     * Erzeugt ein globales UserField, das keiner realen Elementklasse zugeordnet ist
     * 
     * @param style
     * @param definitions
     */
    public UserField(final Style style, final UserFieldDefinitions definitions) {
        this(style == Style.FORMAT ? GLOBAL_FORMAT_IDENTIFIER_CLASS : GLOBAL_USERFIELD_IDENTIFIER_CLASS, definitions);
        this.style = style;
    }

    /**
     * Erzeugt ein {@link UserField} für die übergebene Elementklasse
     * 
     * @param targetClass
     * @param style
     * @param definitions
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass, final Style style, final UserFieldDefinitions definitions) {
        this(targetClass, definitions);
        this.style = style;
    }

    /**
     * @param targetClass
     * @param definitions
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass, final UserFieldDefinitions definitions) {
        this(targetClass, UserField.USERFIELD_HASH_STRING_PREFIX + "_" + String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(id++), definitions);
    }

    /**
     * @param targetClass
     * @param hashCode
     * @param definitions
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass, final String hashCode, final UserFieldDefinitions definitions) {
        this.hashCode = hashCode;
        if (targetClass != null) {
            this.targetClass = targetClass;
        } else {
            this.targetClass = GLOBAL_USERFIELD_IDENTIFIER_CLASS;
        }
        this.definitions = definitions;
    }

    /**
     * Erzeugt ein globales UserField
     * 
     * @param hashCode
     * @param definitions
     */
    public UserField(final String hashCode, final UserFieldDefinitions definitions) {
        this(null, hashCode, definitions);
    }

    /**
     * @return Das <code>UserField</code> in XML-Notation.
     */
    public String toXMLString() {
        StringBuilder sb = new StringBuilder("<userFieldDef hash=\"");
        sb.append(hashCode);
        //bei Modell-Attributen wird die targetClass nicht als UserField ins
        // Tag geschrieben
        if (!isGlobalOrFormat()) {
            sb.append("\" elementClass=\"");
            sb.append(targetClass.getSimpleName());
        }
        sb.append("\">");
        sb.append("<userFieldName>");
        sb.append(XMLCharacterCoder.encodeString(name));
        sb.append("</userFieldName>");
        sb.append("<userFieldDescription>");
        sb.append(XMLCharacterCoder.encodeString(description));
        sb.append("</userFieldDescription>");
        sb.append("<userFieldStyle>");
        sb.append(style.name());
        sb.append("</userFieldStyle>");
        sb.append("<userFieldTreeVis>");
        sb.append(String.valueOf(treeVisibility));
        sb.append("</userFieldTreeVis>");

        if (listValues != null) {
            for (String lv : listValues) {
                sb.append("<userFieldStandardValue>");
                sb.append(XMLCharacterCoder.encodeString(lv));
                sb.append("</userFieldStandardValue>");
            }
        }
        if (style == Style.CLASSIFICATION_NUMBER) {
            if (formatUserField != null) {
                sb.append("<userFieldFormatHash>");
                sb.append(formatUserField.getHashCode());
                sb.append("</userFieldFormatHash>");
            }
        } else if (style == Style.CLASSIFICATION_NUMBER_FORMULA) {
            sb.append("<userFieldFormula>");
            sb.append(formulaString);
            sb.append("</userFieldFormula>");
            if (formatUserField != null) {
                sb.append("<userFieldFormatHash>");
                sb.append(formatUserField.getHashCode());
                sb.append("</userFieldFormatHash>");
            }
        } else if (style == Style.FORMAT) {
            sb.append("<userFieldFormatString>");
            sb.append(XMLCharacterCoder.encodeString(getFormatExportString()));
            sb.append("</userFieldFormatString>");
        }
        sb.append("</userFieldDef>");
        return sb.toString();

    }

    /**
     * Setzt den übergeben Wert als Variable
     * 
     * @param fieldName Der Name der zu belegenden Variable des userFieldes
     * @param value Der Wert, mit der die Variable belegt werden soll.
     */
    public boolean putXMLFieldString(final String fieldName, final String value) {
        if (fieldName.equals("userFieldName")) {
            name = value;
        } else if (fieldName.equals("userFieldDescription")) {
            description = value;
        } else if (fieldName.equals("userFieldStyle")) {
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
            //beim Einlesen können Formate im ersten Schritt nur als globale Eigenschaften erkannt werden.
            //Sie werden erstmal mit der TargetClass == GLOBAL_USERFIELD_IDENTIFIER_CLASS angelegt. 
            //Erst wenn hier der Style "FORMAT" erkannt werden konnte, wird die richtige TargetClass ==
            //GLOBAL_FORMAT_IDENTIFIER_CLASS gesetzt.
            if (style == Style.FORMAT) {
                targetClass = GLOBAL_FORMAT_IDENTIFIER_CLASS;
            }

        } else if (fieldName.equals("userFieldTreeVis")) {
            treeVisibility = Boolean.valueOf(value).booleanValue();
        } else if (fieldName.equals("userFieldStandardValue")) {
            addListValue(value);
        } else if (fieldName.equals("userFieldFormula")) {
            setFormula(value);
            style = Style.CLASSIFICATION_NUMBER_FORMULA;
        } else if (fieldName.equals("userFieldFormatHash")) {
            setFormatUserField(definitions.getUserField(value));
        } else if (fieldName.equals("userFieldFormatString")) {
            setFormatFractionDigitsCountAndUnit(value);
        }

        return true;
    }

    /**
     * Entfernt die Zahlenformatierungs- und Einheitsdefinition
     */
    public void removeAllStandardValues() {
        if (listValues != null) {
            listValues.clear();
        }
    }

    /**
     * Gibt den aktuellen <code>style</code> des <code>UserField</code> s zurück
     * 
     * @return Style des <code>UserField</code> s
     */
    public Style getStyle() {
        return style;
    }

    /**
     * @param style
     * @return <code>true</code>, wenn der übergebene {@link Style} der selbe ist, wie der dieses UserFields
     */
    public boolean hasStyle(final Style style) {
        return this.style == style;
    }

    /**
     * Liefert <code>true</code>, wemm der Style dieses UserFields <code>CLASSIFICATION_NUMBER</code> oder <code>CLASSIFICATION_NUMBER_FORMULA</code>
     * ist.
     * 
     * @return
     */
    public boolean hasClassfificationStyle() {
        return Style.CLASSIFICATION_NUMBER_STYLES.contains(style);
    }

    /**
     * Gibt den Namen des <code>UserField</code> s zurück.
     * 
     * @return Name des <code>UserField</code> s
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des <code>UserField</code> s
     * 
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gibt die Beschreibung des <code>UserField</code> s zurück.
     * 
     * @return Beschreibung des <code>UserField</code> s
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setzt die Beschreibung des <code>UserField</code> s.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return <code>true</code>, wenn das UserField nicht für ein Element definiert ist sondern global fürs Modell oder ein Format ist
     */
    public final boolean isGlobalOrFormat() {
        return targetClass == UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS || targetClass == UserFieldDefinitions.GLOBAL_FORMAT_IDENTIFIER_CLASS;
    }

    /**
     * Prüft, ob das UserField eine Kennzahl, Kennzahlformel oder ein Verteilungsgweicht ist
     * 
     * @return
     */
    public static boolean isClassificationStyle(final Style style) {
        return Style.CLASSIFICATION_NUMBER_STYLES.contains(style);
    }

    /**
     * Prüft, ob ein {@link UserField} mit dem übergebenen {@link Style} Listenwert zur Auswahl stellt
     * 
     * @return
     */
    public static boolean isListValueStyle(final Style style) {
        //blöder Weise ist das Checkbox_UserField nicht darauf ausgelegt, mehr als eine Checkbox gleichzeitug darzustellen,
        //daher hat es im Moment keinen ListValueStyle
        return style == Style.RADIO_BUTTON /* || style==Style.CHECK_BOX */|| style == Style.COMBO_BOX;
    }

    /**
     * Prüft, ob dieses {@link UserField} ein Formel-UserFIeld ist, das einen Indikator definiert.
     * 
     * @param userField
     * @return
     */
    public final boolean isIndicatorFormula() {
        return style == Style.CLASSIFICATION_NUMBER_FORMULA && formulaString != null && formulaString.trim().startsWith(ACCOUNTING_FUNCTION_INDI);
    }

    public final boolean isSimplePartValueSumFormula() {
        return simplePartValueSumFormula;
    }

    /**
     * Prüft, ob dieses {@link UserField} ein UserField ist, das einen Wert in sich tragen kann (also kein Format-, kein Separator- und kein
     * Panel-USerfield ist).
     * 
     * @param userField
     * @return
     */
    public final boolean isValueUserField() {
        return style != Style.FORMAT && style != Style.SEPARATOR;
    }

    /**
     * Prüft, ob das UserField eine Kennzahl oder Kennzahlformel ist
     * 
     * @return
     */
    public boolean isClassificationUserField() {
        return isClassificationStyle(style);
    }

    /**
     * Setzt die Kennzahlformel.
     * 
     * @param formulaString
     * @return boolean true: Wenn es sich bei dem <code>userField</code> um eine sekundäre Kennzahl handelt, d.h. eine Kennzahl, die Kennzahlformeln
     *         repräsentiert - ansonsten false
     */
    public boolean setFormula(final String formulaString) {
        if (style != Style.CLASSIFICATION_NUMBER_FORMULA) {
            return false;
        }
        this.formulaString = formulaString;
        simplePartValueSumFormula = CostingUtil.isSimpleFractionValueSumFormula(this);
        return true;
    }

    /**
     * Gibt den Klassentyp des <code>UserField</code> s zurück.
     * 
     * @return Elementklassentyp
     */
    public Class<? extends UserFieldTarget> getTargetClass() {
        return targetClass;
    }

    /**
     * gibt den <code>hashCode</code> des <code>UserField</code> s zurück.
     * 
     * @return hashCode des <code>UserField</code> s
     */
    public String getHashCode() {
        return hashCode;
    }

    /**
     * @return Returns the definitions.
     */
    public UserFieldDefinitions getDefinitions() {
        return definitions;
    }

    /**
     * @param definitions
     */
    public void setDefinitions(final UserFieldDefinitions definitions) {
        this.definitions = definitions;
    }

    /**
     * Gibt das Format zurück.
     * 
     * @return
     */
    public UserField getFormatUserField() {
        return formatUserField;
    }

    /**
     * Setzt das Format, mit dem Zahlenwerte dieses Userfields formatiert werden können.
     * 
     * @return
     */
    public void setFormatUserField(final UserField formatUserField) {
        this.formatUserField = formatUserField;
    }

    /**
     * Gibt die Formel zurück, die sich hinter dem <code>UserField</code> verbirgt.
     * 
     * @return wenn es sich um ein UserField handelt, dass eine Kennzhalformel repräsentierne soll, wird die Formel in INFIX-Notation zurückgegeben
     *         ansonsten null.
     */
    public String getFormula() {
        return formulaString;
    }

    /**
     * Gibt die Anzahl der definierten Listeneinträge zurück.
     * 
     * @return Anzahl der definierten Listeneinträge
     */
    public int getListValuesCount() {
        return listValues == null ? 0 : listValues.size();
    }

    /**
     * Gibt die definierten Listeneintrag an Position <code>index</code> zurück.
     * 
     * @return Listeneintrag an <code>index</code>
     */
    public String getListValueAt(final int index) {
        return listValues.get(index);
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
            listValues = new ArrayList<String>();
        }
        listValues.add(value);
    }

    /**
     * Prüft ob der übergebene String in den Listenwerten dieses Userfields steht.
     * 
     * @param value
     * @return
     */
    public boolean containsListValue(final String value) {
        return listValues != null && listValues.contains(value);
    }

    /**
     * Liefert den <code>String</code>, der in Modeldatein oder in der Export von <code>UserField</code>s geschrieben wird.
     * 
     * @return
     */
    private String getFormatExportString() {
        StringBuilder sb = new StringBuilder(FORMAT_DECIMAL_PLACES_PREFIX);
        if (numberFormat != null) {
            sb.append(numberFormat.getMinimumFractionDigits());
            sb.append(" ");
        }
        sb.append(FORMAT_UNIT_PREFIX);
        if (formatUnit != null) {
            sb.append(formatUnit);
        }
        return sb.toString();
    }

    /**
     * Setzt die Nachkommastellenanzahl und die Einheit des Formates. Es wird ein String der Form "d### uXXX" erwartet, wobei d für den String
     * {@link #FORMAT_DECIMAL_PLACES_PREFIX} und u für {@link #FORMAT_UNIT_PREFIX} steht. "###" steht für eine Zahl und "XXX" für eine beliebige
     * Zeichenkette. Vor dem 'u' können beliebig viele Whitespaces (auch keins) stehen.
     * 
     * @param formatStringWithPrefixes
     */
    private void setFormatFractionDigitsCountAndUnit(final String formatStringWithPrefixes) {
        int decimalIndex = formatStringWithPrefixes.indexOf(FORMAT_DECIMAL_PLACES_PREFIX);
        int unitIndex = formatStringWithPrefixes.indexOf(FORMAT_UNIT_PREFIX);
        String decimalPlaces = formatStringWithPrefixes.substring(decimalIndex + FORMAT_DECIMAL_PLACES_PREFIX.length(), unitIndex).trim();
        setFormatFractionDigits(Integer.parseInt(decimalPlaces));
        String unit = formatStringWithPrefixes.substring(unitIndex + FORMAT_UNIT_PREFIX.length());
        setFormatUnit(unit);
    }

    /**
     * Liefert das <code>NumberFormat</code> des <code>UserField</code>s. Ist das <code>UserField</code> selbst ein Format, gibt es seine eigenen
     * Nachkommastellen zurück, ist es ein UserField, dem ein Format zugewiesen ist (was für ein Format selbst nie zutreffen kann), dann gibt es die
     * Nachkommstellen des Formates zurück.
     * 
     * @param definitions die <code>UserFieldDefinitions</code>, in denen das Format-<code>UserField</code> dieses <code>UserField</code>s defniert
     *            ist oder <code>null</code>, wenn man diese Information direkt für ein Format-<code>UserField</code> abfragen möchte.
     * @return die Anzahl der Nachkommastellen des Formates. Wenn kein Format eingestellt ist, kommt -1 zurück;
     */
    public NumberFormat getNumberFormat() {
        if (style != Style.FORMAT) {
            if (formatUserField == null) {
                return null;
            }
            return formatUserField.getNumberFormat();
        }
        return numberFormat;
    }

    /**
     * Liefert die Anzahl der Nachkommastellen. Ist das UserField selbst ein Format, gibt es seine eigenen Nachkommastellen zurück, ist es ein
     * UserField, dem ein Format zugewiesen ist (was für ein Format selbst nie zutreffen kann), dann gibt es die Nachkommstellen des Formates zurück.
     * 
     * @return die Anzahl der Nachkommastellen des Formates. Wenn kein Format eingestellt ist, kommt -1 zurück;
     */
    public int getFormatFractionDigits() {
        NumberFormat numberFormat = getNumberFormat();
        if (numberFormat == null) {
            return -1;
        }
        return numberFormat.getMinimumFractionDigits();
    }

    /**
     * Setzt bei Format-<code>UserField</code>s die Anzahl der Nachkommastellen.
     * 
     * @param fractionDigits
     */
    public void setFormatFractionDigits(final int fractionDigits) {
        if (style != Style.FORMAT || fractionDigits < 0) {
            return;
        }
        if (numberFormat == null) {
            numberFormat = NumberFormat.getNumberInstance();
        }
        numberFormat.setMinimumFractionDigits(fractionDigits);
        numberFormat.setMaximumFractionDigits(fractionDigits);
    }

    /**
     * Setzt die Einheit des <code>UserFields</code>, wenn es den Style <code>FORMAT_STYLE</code>.
     * 
     * @param unitString
     */
    public void setFormatUnit(final String unitString) {
        if (style != Style.FORMAT) {
            return;
        }
        if (unitString.trim().equals("")) {
            formatUnit = null;
        } else {
            formatUnit = unitString;
        }
    }

    /**
     * Gibt die Einheit des <code>UserField</code>s zurück. Ist das UserField selbst ein Format, gibt es seine eigene Einheit zurück, ist es ein
     * UserField, dem ein Format zugewiesen ist (was für ein Format selbst nie zutreffen kann), dann gibt es die Nachkommstellen des Formates zurück.
     * 
     * @return die Einheit des Formates. Wenn kein Format eingestellt ist, kommt <code>null</code> zurück;
     */
    public String getFormatUnit() {
        if (style != Style.FORMAT) {
            if (formatUserField == null) {
                return null;
            }
            return formatUserField.getFormatUnit();
        }
        return formatUnit;
    }

    @Override
    public int hashCode() {
        return hashCode.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof UserField && ((UserField) obj).getHashCode().equals(hashCode);
    }

    @Override
    public final UserField clone() {
        UserField userField = null;
        try {
            userField = (UserField) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }

        //clonen aller Eigenschaften in alphabetischer Reihenfolge
        userField.definitions = definitions;
        userField.description = description == null ? null : new String(description);
        userField.hashCode = new String(hashCode);
        userField.numberFormat = numberFormat == null ? null : (NumberFormat) numberFormat.clone();
        userField.formatUserField = formatUserField;
        userField.formatUnit = formatUnit == null ? null : new String(formatUnit);
        userField.formulaString = formulaString == null ? null : new String(formulaString);
        userField.listValues = listValues == null ? null : new ArrayList<String>(listValues);
        userField.name = name == null ? null : new String(name);
        userField.style = style;
        userField.targetClass = targetClass;
        userField.treeVisibility = treeVisibility;
        return userField;
    }

    @Override
    public String toString() {
        String retVal = null;
        if (style == Style.SEPARATOR) {
            retVal = "--- " + name + " ---";
        } else {
            if (Strings.isNullOrEmpty(description)) {
                retVal = name;
            } else {
                retVal = name + " (" + description + ")";
            }
        }
        return retVal;
    }

    /**
     * @return Returns the treeVisibility.
     */
    public boolean isTreeVisibility() {
        return treeVisibility;
    }

    /**
     * Gibt <code>true</code> zurück, falls nur positive Werte erlaubt sind, sonst <code>false</code>
     * 
     * @return {@link #positiveOnly}
     */
    public boolean isPositiveOnly() {
        return positiveOnly;
    }

    /**
     * Setzt das Attribut {@link #positiveOnly} auf <code>b</code>
     * 
     * @param b <code>true</code> --> nur noch postive Werte erlaubt <code>false</code> --> positive und negative Werte erlaubt
     */
    public void setPositiveOnly(final boolean b) {
        positiveOnly = b;
    }

    /**
     * @param treeVisibility The treeVisibility to set.
     */
    public void setTreeVisibility(final boolean treeVisibility) {
        this.treeVisibility = treeVisibility;
    }

    ////////////////////////////////////
    // Werte ausgeben und formatieren //
    ////////////////////////////////////

    /**
     * Liefert den unformatierten Wert einer Kennzahl.
     * 
     * @param target Kennzahlwertträger, für das der Wert zurück gegeben werden soll
     * @return
     */
    public String getValue(final UserFieldTarget target) {
        //wenn es eine Kennzahlformel ist, deren Wert ermittelt werden soll
        if (style == Style.CLASSIFICATION_NUMBER_FORMULA) {
            //wenn die globale Option der Berechnung eingeschaltet ist
            if (UserProperties.isEnableClassificationNumberCalculation()) {
                //alle berechneten Kennzahl Werte löschen
                definitions.reset();
                //falls das reset nicht ausgeführt wurde, da sich nichts geändert
                //hatte -> value hat den bisherigen Wert
                //falls das reset wirklich ausgeführt wurde -> value hat den Wert
                //UserField.EMPTY_STRING
                String value = target.getCalculatedUserFieldValue(this);
                //wenn das reset ausgeführt wurde
                if (value == EMPTY_STRING) {
                    //berechne den Wert neu
                    value = definitions.calculate(this, target);
                    //setze ihn im UserFieldTarget
                    target.setCalculatedUserFieldValue(this, value);
                }
                //gib den berechneten Wert zurück
                return value;
            }
            //wenn nicht berechnet werden sollte -> gib einen leeren String zurück
            return EMPTY_STRING;
        }
        //gib den eingegebenen Wert des UserFields zurück
        return target.getUserFieldInputValue(this);
    }

    /**
     * Formatiert einen übergenenen Wert mit der Formatvorlage.
     * 
     * @param me Modellelement, für das der formatierte Wert zurück gegeben werden soll
     * @return
     */
    public String getFormattedValue(final ModelElement me) {
        return getFormattedValue(me, false);
    }

    /**
     * Formatiert einen übergenenen Wert mit der Formatvorlage.
     * 
     * @param me Modellelement, für das der formatierte Wert zurück gegeben werden soll
     * @param appendUnit wenn <code>true</code> wird auch die Einheit an den Rückgabewert angehängt
     * @return
     */
    public String getFormattedValue(final ModelElement me, final boolean appendUnit) {
        String value = getValue(me);
        return getFormattedValue(value, appendUnit);
    }

    /**
     * Formatiert den über <code>o.toString()</code> erhaltenen String gemäß der Formatvorlage.
     * 
     * @see #getFormattedValue(ModelElement, boolean)
     * @param o
     * @param appendUnit
     * @return
     */
    public String getFormattedValue(final Object o, final boolean appendUnit) {

        if (o == null) {
            return null;
        }

        String value = o.toString();

        if (!hasClassfificationStyle()) {
            return value;
        }

        if (value == null || value.equals("")) {
            return "";
        }

        if (isCriticalError(value)) {
            return value;
        }

        if (isIgnoreableError(value)) {
            return value;
        }

        value = getFormattedValue(value, formatUserField, appendUnit);

        // Falls positiveOnly=true und value mit "-" beginnt, wird errorString zurückgegeben
        if (positiveOnly == true && value.startsWith("-")) {
            return POSITIVE_VALUES_ONLY;
        }

        return value;
    }

    /**
     * @param value
     * @param formatUserField
     * @param appendUnit
     * @return
     */
    public static final String getFormattedValue(final String value, final UserField formatUserField, final boolean appendUnit) {
        // Falls sich der Wert-String nicht in einen BigDecimal umwandeln lässt, wird errorString NUMBER_FORMAT_ERROR zurückgegeben
        try {
            //hier prüfen, ob sich der String überhaupt in eine Zahl umwandeln lässt
            BigDecimal numberValue = new BigDecimal(value);
            //wenn kein Format gesetzt ist 
            if (formatUserField == null) {
                return value;
            }
            String v = formatUserField.numberFormat.format(numberValue);
            if (!appendUnit) {
                return v;
            }
            StringBuilder sb = new StringBuilder(v);
            if (formatUserField.formatUnit != null) {
                sb.append(" ");
                sb.append(formatUserField.formatUnit);
            }
            return sb.toString();
        } catch (NumberFormatException nfe) {
            return NUMBER_FORMAT_ERROR;
        }
    }

    /**
     * Vergleicht die beiden spezifizierten Modelelemente hinsichtlich ihrer Werte für dieses UserField.
     * <p>
     * Bedingungen für die Vergleichbarkeit sind:
     * <li>Beide Modelemente sind Instanzen der selben Klasse</li>
     * <li>Das UserField ist für diese Klasse definiert</li> <br>
     * <br>
     * 
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(final ModelElement o1, final ModelElement o2) {
        Class<? extends ModelElement> class1 = o1.getClass();
        Class<? extends ModelElement> class2 = o2.getClass();
        if (!class1.equals(class2)) {
            throw new ClassCastException("Die Modelelemente sind Instanzen verschiedener Klassen.");
        }
        if (!isUserFieldFor(class1)) {
            throw new ClassCastException("Das UserField " + UserField.this + " ist für die Klasse " + class1.getSimpleName() + " nicht definiert.");
        }

        return getStyle().compare(this, o1, o2);
    }

    /**
     * Führt, wenn das übergebene <code>Object</code> ungleich <code>null</code> ist die <code>toString()</code>-Methode aus und ersetzt in diesem
     * String alle Punkte gegen Kommas, wenn der DecimalSeparator ein Komma ist und umgekehrt.
     * 
     * @param string
     */
    public static String replaceWrongDecimalSeparator(final Object objectToString) {
        char decimalSeparator = new java.text.DecimalFormatSymbols().getDecimalSeparator();
        return replaceWrongDecimalSeparator(objectToString, decimalSeparator);
    }

    /**
     * Ersetzt alle in <code>objectToString.toString()</code> enthaltenen Kommas und Punkte durch <code>decimalSeparator</code>. Mit
     * <code>replaceAll = true</code> wird nur das letzte Treenzeichen ersetzt
     * 
     * @param objectToString
     * @param decimalSeparator
     * @return
     */
    public static String replaceWrongDecimalSeparator(final Object objectToString, final char decimalSeparator) {
        if (objectToString == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(objectToString.toString());
        //Komma oder Punkte im String? wenn ja, wo steht das/der letzte?
        int lastIndexOfSeparator = Math.max(sb.lastIndexOf("."), sb.lastIndexOf(","));
        //gar kein Separator -> einfach raus
        if (lastIndexOfSeparator < 0) {
            return sb.toString();
        }
        //das letzte Vorkommen einfach schon mal auf den Anzeigeseparator setzen
        sb.setCharAt(lastIndexOfSeparator, decimalSeparator);
        //alle Punkte vor diesem letzten Separator löschen
        int index = 0;

        while (true) {
            index = sb.indexOf(".", index);
            if (index >= 0 && index < lastIndexOfSeparator) {
                sb.deleteCharAt(index);
                lastIndexOfSeparator--;
            } else {
                break;
            }
        }
        //alle Kommas vor diesem letzten Separator löschen
        index = 0;
        while (true) {
            index = sb.indexOf(",", index);
            if (index >= 0 && index < lastIndexOfSeparator) {
                sb.deleteCharAt(index);
                lastIndexOfSeparator--;
            } else {
                break;
            }
        }
        return sb.toString();
    }

    ///////////////////////////////////////////////////////
    // prüfen, ob ein UserField ein anderes referenziert // 
    ///////////////////////////////////////////////////////

    /**
     * Prüft, ob <code>this</code> das übergebenene <code>UserField</code> <code>other</code> benutzt. Möglich ist als Format oder innerhalb einer
     * Formel.
     */
    public boolean uses(final UserField possibleUsedField) {
        if (possibleUsedField == null) {
            return false;
        }
        if (formatUserField == possibleUsedField) {
            return true;
        }
        if (style == Style.CLASSIFICATION_NUMBER_FORMULA) {
            Set<String> hashesInFormula = getHashesInFormula();
            if (hashesInFormula != null && getHashesInFormula().contains(possibleUsedField.hashCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt wieder, ob dieses UserField ein UserField für die spezfizierte Klasse ist.
     * 
     * @param elementClass
     * @return
     */
    public boolean isUserFieldFor(final Class<? extends ModelElement> elementClass) {
        for (UserField uf : definitions.getUserFields(elementClass)) {
            if (uf == this) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert eine Liste aller in der Formel vorkommenden Hash-Strings von anderen <code>UserField</code>s.
     * 
     * @return
     */
    public Set<String> getHashesInFormula() {
        if (formulaString == null || formulaString.equals("")) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(formulaString, " ()+-/*|");
        HashSet<String> hashList = new HashSet<String>(st.countTokens());
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            if (token.startsWith(USERFIELD_HASH_STRING_PREFIX)) {
                //hashList.add(st.nextToken());
                hashList.add(token);
            }
        }
        return hashList;
    }

    /**
     * Prüft, ob der übergebene Wert ein Wert aus <code>ERROR_SET</code> ist.
     * 
     * @param value Der zu prüfende Wert
     * @return <code>true</code>, wenn sich der übergebene Wert in <code>ERROR_SET</code> befindet sonst <code>false</code>
     */
    public static boolean isCriticalError(final String value) {
        return ERROR_SET.contains(value);
    }

    /**
     * Prüft, ob der übergebene Wert ein Wert aus <code>IGNOREABLE_ERROR_SET</code> ist.
     * 
     * @param value Der zu prüfende Wert
     * @return <code>true</code>, wenn sich der übergebene Wert in <code>IGNOREABLE_ERROR_SET</code> befindet sonst <code>false</code>
     */
    public static boolean isIgnoreableError(final String value) {
        return IGNOREABLE_ERROR_SET.contains(value);
    }

    /**
     * Prüft, ob der übergebene Wert ein Wert aus <code>ERROR_SET</code> oder <code>IGNOREABLE_ERROR_SET</code> ist.
     * 
     * @param value Der zu prüfende Wert
     * @return <code>true</code>, wenn sich der übergebene Wert in <code>ERROR_SET</code> oder <code>IGNOREABLE_ERROR_SET</code> befindet sonst
     *         <code>false</code>
     */
    public static boolean isError(final String value) {
        return IGNOREABLE_ERROR_SET.contains(value) || ERROR_SET.contains(value);
    }

}
