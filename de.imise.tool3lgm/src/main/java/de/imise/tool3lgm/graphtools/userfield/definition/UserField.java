package de.imise.tool3lgm.graphtools.userfield.definition;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.util.IDStringGenerator;
import de.imise.util.NameAndDescriptionTargetAdapter;

/**
 * Ein <code>UserField</code> ist ein Element bzw. Objekt, dass Element- und
 * Kantenklassen zugewiesen werden kann. Mit Hilfe dieses Elementes können
 * zusätzliche Eigenschaften für Element- und Kantenklassen deklariert und
 * definiert werden. Es kann somit eine benutzergesteuerte Erweiterung des
 * Metamodells vorgenommen werden. die Klasse <code>UserField</code> hat als
 * Attribute eine Bezeichung, eine Beschreibung, eine ID, einen
 * <code>style</code> und einen Identifikator, der kenntlich macht, zu welcher
 * Elementklasse das <code>UserField</code> gehört.
 *
 * @author Thomas Rudert
 */
public final class UserField extends NameAndDescriptionTargetAdapter implements Cloneable, Comparator<UserFieldTarget>, IDSource {

    public static enum Style {
        SINGLE_LINE {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                return alphabeticalCompare(userField, me1, me2);
            }
        },
        MULTI_LINE {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                return alphabeticalCompare(userField, me1, me2);
            }
        },
        CHECK_BOX {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                String v1 = me1.getValue(userField);
                String v2 = me2.getValue(userField);
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
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                return alphabeticalCompare(userField, me1, me2);
            }
        },
        RADIO_BUTTON {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                String v1 = me1.getValue(userField);
                String v2 = me2.getValue(userField);

                if (v1 == null || v1.isEmpty()) {
                    if (v2 == null || v2.isEmpty()) {
                        return 0;
                    }
                    return -1;
                }
                if (v2 == null || v2.isEmpty()) {
                    return 1;
                }
                Integer i1 = userField.listValues.indexOf(v1);
                Integer i2 = userField.listValues.indexOf(v2);
                int retval = i1.compareTo(i2);
                return retval;
            }
        },
        HYPERLINK {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                return alphabeticalCompare(userField, me1, me2);
            }
        },
        ID {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                return alphabeticalCompare(userField, me1, me2);
            }
        },
        NUMBER {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                return numberCompare(userField, me1, me2);
            }
        },
        FORMULA {
            @Override
            int compare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
                return numberCompare(userField, me1, me2);
            }
        };

        public static final Set<Style> NUMBER_STYLES = ImmutableSet.of(NUMBER, FORMULA);

        /**
         * Vergleicht die beiden UserFields hinsichtlich ihres Wertes bezüglich
         * des Modelelements.
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
        abstract int compare(UserField uf, UserFieldTarget me1, UserFieldTarget me2);

        /**
         * Alphabetischer Vergleich der jeweiligen Werte (siehe
         * {@link String#compareTo(String)})
         *
         * @param userField
         * @param me1
         * @param me2
         * @return
         */
        private static int alphabeticalCompare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
            String v1 = me1.getValue(userField);
            String v2 = me2.getValue(userField);
            if (v1 == null) {
                return v2 == null ? 0 : -1;
            }
            if (v2 == null) {
                return 1;
            }
            return v1.compareTo(v2);
        }

        /** Vergleich der jeweiligen Werte für Kennzahlen/Kennzahlformeln */
        /**
         * @param userField
         * @param me1
         * @param me2
         * @return
         */
        private static int numberCompare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
            String v1 = me1.getValue(userField);
            String v2 = me2.getValue(userField);
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

        @Override
        public String toString() {
            return getResString(name());
        }
    }

    /**
     * Wert eines UserFields vom Typ {@link Style#CHECK_BOX}: "Häkchen gesetzt"
     */
    public static final String CHECKBOX_TRUE = "true";

    /**
     * Wert eines UserFields vom Typ {@link Style#CHECK_BOX}: "Häkchen nicht
     * gesetzt"
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
     * Präfix aller IDs von <code>UserField</code>s
     */
    public static final String USERFIELD_ID_PREFIX = "USERFIELD";

    //////////////////////////////////////////////////////////////////////
    // Werte der Kennzahl- und Kennzahlformeluserfields in Sonderfällen //
    //////////////////////////////////////////////////////////////////////

    /**
     * Ein konstant leerer <code>String</code>. <code>UserFieldTarget</code>s,
     * die keinen Wert für dieses <code>UserField</code> liefern können, geben
     * diesen <code>String</code> zurück.
     */
    public static final String EMPTY_STRING = "EMPTY_VALUE";
    public static final String NO_ELEMENTS_CONNECTED = "NO_ELEMENTS_CONNECTED";
    public static final String POSITIVE_VALUES_ONLY = "POSITIVE_VALUES_ONLY";
    public static final String NUMBER_FORMAT_ERROR = "NUMBER_FORMAT_ERROR";
    public static final String ERROR_DIVIDE_BY_ZERO = "DIVIDE_BY_ZERO";
    public static final String ERROR_CROSS_REFERENCE_IN_FORMULA_DEFINITION = "CROSS_REFERENCE";
    public static final String CALCULATION_DISABLED = "CALCULATION_DISABLED";

    /**
     * Dieses Set beinhaltet Strings, die Formel-UserFields als Werte annehmen,
     * wenn sie sich nicht berechnen lassen oder irgendwelche anderen Probleme
     * auftreten. <code>NUMBER_FORMAT_ERROR</code> und <code>EMPTY_STRING</code>
     * können auch bei Kennzahl-UserFields auftreten. beinhaltet:
     */
    private static final Set<String> ERROR_SET = ImmutableSet.of(CALCULATION_DISABLED, POSITIVE_VALUES_ONLY, NUMBER_FORMAT_ERROR, ERROR_DIVIDE_BY_ZERO, ERROR_CROSS_REFERENCE_IN_FORMULA_DEFINITION);

    /**
     * Dieses Set enthält alle Strings, die Werte von UserFields sein können,
     * die bei Berechnungen ignoriert werden sollen. D.h. wenn eine
     * Summen-/Differenz/Divisions-/Multiplikationsfunktion auf ein UserField
     * mit einem <code>String</code> aus diesem Set als value trifft, wird dafür
     * der Wert 0 angenommen.
     */
    private static final Set<String> IGNOREABLE_ERROR_SET = ImmutableSet.of(EMPTY_STRING, NO_ELEMENTS_CONNECTED);

    /**
     * Dieses Set beinhaltet Strings, die interne Verrechnungsfunktionen
     * kennzeichen beinhaltet:
     */
    private static final Set<String> ACCOUNTING_FUNCTIONS_SET = ImmutableSet.of(ACCOUNTING_FUNCTION_SUM, ACCOUNTING_FUNCTION_TWSUM, ACCOUNTING_FUNCTION_MAX, ACCOUNTING_FUNCTION_MIN, ACCOUNTING_FUNCTION_MULT, ACCOUNTING_FUNCTION_AVG,
            ACCOUNTING_FUNCTION_INDI, ACCOUNTING_FUNCTION_REF);

    /**
     * ID des <code>UserField</code>s. Diese ist final, da UserFields nur
     * geclont werden, um eine Sicherheitskopie vor einer Änderung anzulegen,
     * die eventuell zurück genommen wird, so dass die alte ID erhalten bleiben
     * muss.
     */
    private final String id;

    /**
     * Gibt an, zu welcher Klasse das <code>UserField</code> gehört.
     */
    private Class<? extends UserFieldTarget> targetClass;

    /**
     * Gibt an, von welcher Form das <code>userField</code> sein soll. / private
     * int style = -1; /** Art dieses {@link UserField}s. Immer ein Wert aus
     * {@link Style}
     */
    private Style style = null;

    /**
     * Gibt an, ob das <code>UserField</code> im Modellbrowser sichtbar sein
     * soll.
     */
    private boolean treeVisibility = true;

    /**
     * Beinhaltet alle Einträge, die in UserFields mit den Styles ComboBox,
     * RadioButton und CheckBox auftauchen sollen.
     */
    private ArrayList<String> listValues;

    /**
     * Interne Repräsentation von Kennzahlformeln
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
     * Wenn <code>true</code> werden nur positive Werte akzeptiert.
     */
    private boolean positiveOnly;

    /**
     * Erzeugt ein globales UserField, das keiner realen Elementklasse
     * zugeordnet ist
     *
     * @param style
     * @param definitions
     */
    public UserField(final Style style) {
        this(GLOBAL_USERFIELD_IDENTIFIER_CLASS, style);
    }

    /**
     * Erzeugt ein {@link UserField} für die übergebene Elementklasse
     *
     * @param targetClass
     * @param style
     * @param definitions
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass, final Style style) {
        this(targetClass);
        this.style = style;
    }

    /**
     * @param targetClass
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass) {
        this(targetClass, IDStringGenerator.createIDString(USERFIELD_ID_PREFIX));
    }

    /**
     * @param targetClass
     * @param id
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        this.id = id;
        if (targetClass != null) {
            this.targetClass = targetClass;
        } else {
            this.targetClass = GLOBAL_USERFIELD_IDENTIFIER_CLASS;
        }
    }

    /**
     * Erzeugt ein globales UserField
     *
     * @param id
     */
    public UserField(final String id) {
        this(null, id);
    }

    /**
     * Setzt den übergeben Wert als Variable
     *
     * @param fieldName Der Name der zu belegenden Variable des userFieldes
     * @param value Der Wert, mit der die Variable belegt werden soll.
     * @param definitions
     * @return
     */
    public boolean putXMLFieldString(final String fieldName, final String value, final UserFieldDefinitions definitions) {
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
        } else if (fieldName.equals("userFieldTreeVis")) {
            treeVisibility = Boolean.valueOf(value).booleanValue();
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
     * @return <code>true</code>, wenn der übergebene {@link Style} der selbe
     *         ist, wie der dieses UserFields
     */
    public boolean hasStyle(final Style style) {
        return this.style == style;
    }

    /**
     * Liefert <code>true</code>, wemm der Style dieses UserFields
     * <code>NUMBER</code> oder <code>FORMULA</code> ist.
     *
     * @return
     */
    public boolean hasClassfificationStyle() {
        return hasNumberStyle(style);
    }

    /**
     * Gibt die Einheit des <code>UserField</code>s zurück. Ist das UserField
     * selbst ein Format, gibt es seine eigene Einheit zurück, ist es ein
     * UserField, dem ein Format zugewiesen ist (was für ein Format selbst nie
     * zutreffen kann), dann gibt es die Nachkommstellen des Formates zurück.
     *
     * @return die Einheit des Formates. Wenn kein Format eingestellt ist, kommt
     *         <code>null</code> zurück;
     */
    public String getFormatUnit() {
        return numberFormat == null ? null : numberFormat.getUnit();
    }

    /**
     * @return <code>true</code>, wenn das UserField nicht für ein Element
     *         definiert ist sondern global fürs Modell oder ein Format ist
     */
    public final boolean isGlobal() {
        return targetClass == UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;
    }

    /**
     * Prüft, ob das UserField eine Kennzahl, Kennzahlformel oder ein
     * Verteilungsgweicht ist
     *
     * @return
     */
    public static boolean hasNumberStyle(final Style style) {
        return Style.NUMBER_STYLES.contains(style);
    }

    /**
     * Prüft, ob ein {@link UserField} mit dem übergebenen {@link Style}
     * Listenwert zur Auswahl stellt
     *
     * @return
     */
    public static boolean isListValueStyle(final Style style) {
        //blöder Weise ist das Checkbox_UserField nicht darauf ausgelegt, mehr als eine Checkbox gleichzeitug darzustellen,
        //daher hat es im Moment keinen ListValueStyle
        return style == Style.RADIO_BUTTON /* || style==Style.CHECK_BOX */ || style == Style.COMBO_BOX;
    }

    /**
     * Prüft, ob dieses {@link UserField} ein Formel-UserFIeld ist, das einen
     * Indikator definiert.
     *
     * @param userField
     * @return
     */
    public final boolean isIndicatorFormula() {
        return style == Style.FORMULA && formulaString != null && formulaString.trim().startsWith(ACCOUNTING_FUNCTION_INDI);
    }

    public final boolean isSimplePartValueSumFormula() {
        return simplePartValueSumFormula;
    }

    /**
     * Prüft, ob das UserField eine Kennzahl oder Kennzahlformel ist
     *
     * @return
     */
    public boolean isNumberUserField() {
        return hasNumberStyle(style);
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

    @Override
    public String getID() {
        return id;
    }

    /**
     * Gibt das Format zurück.
     *
     * @return
     */
    public UserFieldNumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * @return the encapsulated java internal number format of the
     *         {@link UserFieldNumberFormat} if exists, otherwise
     *         <code>null</code>
     */
    public NumberFormat getJavaNumberFormat() {
        return numberFormat == null ? null : numberFormat.getNumberFormat();
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
     * Removes the format of this UserField
     */
    public void removeNumberFormat() {
        setNumberFormat(null);
    }

    /**
     * Gibt die Formel zurück, die sich hinter dem <code>UserField</code>
     * verbirgt.
     *
     * @return wenn es sich um ein UserField handelt, dass eine Kennzhalformel
     *         repräsentierne soll, wird die Formel in INFIX-Notation
     *         zurückgegeben ansonsten null.
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
            listValues = new ArrayList<>();
        }
        listValues.add(value);
    }

    /**
     * Prüft ob der übergebene String in den Listenwerten dieses Userfields
     * steht.
     *
     * @param value
     * @return
     */
    public boolean containsListValue(final String value) {
        return listValues != null && listValues.contains(value);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof UserField && ((UserField) obj).getID().equals(id);
    }

    @Override
    public final UserField clone() {
        UserField userField = null;
        try {
            userField = (UserField) super.clone();
        } catch (Exception e) {
            //this should never happen since we are cloneable
            throw new InternalError(e);
        }
        //clonen aller Eigenschaften, die nicht auf dieselbe Object-Referenz zeigen sollen, die durch super.clone() hergestellt wurde
        numberFormat = numberFormat == null ? null : (UserFieldNumberFormat) numberFormat.clone();
        userField.listValues = listValues == null ? null : new ArrayList<>(listValues);
        return userField;
    }

    @Override
    public String toString() {
        String retVal = null;
        if (Strings.isNullOrEmpty(description)) {
            retVal = name;
        } else {
            retVal = name + " (" + description + ")";
        }
        return retVal;
    }

    /**
     * @return Displayable name of the style of this userfield
     */
    public String getDisplayableStyleName() {
        return style.toString();
    }

    /**
     * @return Returns the treeVisibility.
     */
    public boolean isTreeVisibility() {
        return treeVisibility;
    }

    /**
     * Gibt <code>true</code> zurück, falls nur positive Werte erlaubt sind,
     * sonst <code>false</code>
     *
     * @return {@link #positiveOnly}
     */
    public boolean isPositiveOnly() {
        return positiveOnly;
    }

    /**
     * Setzt das Attribut {@link #positiveOnly} auf <code>b</code>
     *
     * @param b <code>true</code> --> nur noch postive Werte erlaubt
     *            <code>false</code> --> positive und negative Werte erlaubt
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
     * Formatiert einen übergenenen Wert mit der Formatvorlage.
     *
     * @param me Modellelement, für das der formatierte Wert zurück gegeben
     *            werden soll
     * @return
     */
    public String getFormattedValue(final UserFieldTarget me) {
        return getFormattedValue(me, false);
    }

    /**
     * Formatiert einen übergenenen Wert mit der Formatvorlage.
     *
     * @param me Modellelement, für das der formatierte Wert zurück gegeben
     *            werden soll
     * @param appendUnit wenn <code>true</code> wird auch die Einheit an den
     *            Rückgabewert angehängt
     * @return
     */
    public String getFormattedValue(final UserFieldTarget me, final boolean appendUnit) {
        String value = me.getValue(this);
        return getFormattedValue(value, appendUnit);
    }

    /**
     * Formatiert den über <code>o.toString()</code> erhaltenen String gemäß der
     * Formatvorlage.
     *
     * @see #getFormattedValue(UserFieldTarget, boolean)
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

        value = getFormattedValue(value, numberFormat, appendUnit);

        // Falls positiveOnly=true und value mit "-" beginnt, wird errorString zurückgegeben
        if (positiveOnly == true && value.startsWith("-")) {
            return POSITIVE_VALUES_ONLY;
        }

        return value;
    }

    /**
     * @param value
     * @param numberFormat
     * @param appendUnit
     * @return
     */
    public static final String getFormattedValue(final String value, final UserFieldNumberFormat numberFormat, final boolean appendUnit) {
        // Falls sich der Wert-String nicht in einen BigDecimal umwandeln lässt, wird errorString NUMBER_FORMAT_ERROR zurückgegeben
        try {
            //hier prüfen, ob sich der String überhaupt in eine Zahl umwandeln lässt
            BigDecimal numberValue = new BigDecimal(value);
            //wenn kein Format gesetzt ist
            if (numberFormat == null) {
                return value;
            }
            String v = numberFormat.format(numberValue);
            if (!appendUnit) {
                return v;
            }
            StringBuilder sb = new StringBuilder(v);
            String unit = numberFormat.getUnit();
            if (unit != null) {
                sb.append(" ");
                sb.append(unit);
            }
            return sb.toString();
        } catch (NumberFormatException nfe) {
            return NUMBER_FORMAT_ERROR;
        }
    }

    /**
     * Vergleicht die beiden spezifizierten Modelelemente hinsichtlich ihrer
     * Werte für dieses UserField.
     * <p>
     * Bedingungen für die Vergleichbarkeit sind:
     * <li>Beide Modelemente sind Instanzen der selben Klasse</li> <br>
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(final UserFieldTarget o1, final UserFieldTarget o2) {
        Class<? extends UserFieldTarget> class1 = o1.getClass();
        Class<? extends UserFieldTarget> class2 = o2.getClass();
        if (!class1.equals(class2)) {
            throw new ClassCastException("Die Modelelemente sind Instanzen verschiedener Klassen.");
        }
        return getStyle().compare(this, o1, o2);
    }

    /**
     * Führt, wenn das übergebene <code>Object</code> ungleich <code>null</code>
     * ist die <code>toString()</code>-Methode aus und ersetzt in diesem String
     * alle Punkte gegen Kommas, wenn der DecimalSeparator ein Komma ist und
     * umgekehrt.
     *
     * @param string
     */
    public static String replaceWrongDecimalSeparator(final Object objectToString) {
        char decimalSeparator = new java.text.DecimalFormatSymbols().getDecimalSeparator();
        return replaceWrongDecimalSeparator(objectToString, decimalSeparator);
    }

    /**
     * Ersetzt alle in <code>objectToString.toString()</code> enthaltenen Kommas
     * und Punkte durch <code>decimalSeparator</code>. Mit
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

    public boolean hasNumberFormat(final UserFieldNumberFormat numberFormat) {
        return this.numberFormat == numberFormat;
    }

    /**
     * Prüft, ob <code>this</code> das übergebenene <code>UserField</code>
     * <code>other</code> benutzt. Möglich ist als Format oder innerhalb einer
     * Formel.
     */
    public boolean uses(final UserField possibleUsedField) {
        if (style == Style.FORMULA) {
            Set<String> idsInFormula = getIDsInFormula();
            if (idsInFormula != null && getIDsInFormula().contains(possibleUsedField.id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert eine Liste aller in der Formel vorkommenden IDs von anderen
     * <code>UserField</code>s.
     *
     * @return
     */
    public Set<String> getIDsInFormula() {
        if (formulaString == null || formulaString.equals("")) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(formulaString, " ()+-/*|");
        Set<String> ids = new HashSet<>(st.countTokens());
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            if (token.startsWith(USERFIELD_ID_PREFIX)) {
                ids.add(token);
            }
        }
        return ids;
    }

    /**
     * Prüft, ob der übergebene Wert ein Wert aus <code>ERROR_SET</code> ist.
     *
     * @param value Der zu prüfende Wert
     * @return <code>true</code>, wenn sich der übergebene Wert in
     *         <code>ERROR_SET</code> befindet sonst <code>false</code>
     */
    public static boolean isCriticalError(final String value) {
        return ERROR_SET.contains(value);
    }

    /**
     * Prüft, ob der übergebene Wert ein Wert aus
     * <code>IGNOREABLE_ERROR_SET</code> ist.
     *
     * @param value Der zu prüfende Wert
     * @return <code>true</code>, wenn sich der übergebene Wert in
     *         <code>IGNOREABLE_ERROR_SET</code> befindet sonst
     *         <code>false</code>
     */
    public static boolean isIgnoreableError(final String value) {
        return IGNOREABLE_ERROR_SET.contains(value);
    }

    /**
     * Prüft, ob der übergebene Wert ein Wert aus <code>ERROR_SET</code> oder
     * <code>IGNOREABLE_ERROR_SET</code> ist.
     *
     * @param value Der zu prüfende Wert
     * @return <code>true</code>, wenn sich der übergebene Wert in
     *         <code>ERROR_SET</code> oder <code>IGNOREABLE_ERROR_SET</code>
     *         befindet sonst <code>false</code>
     */
    public static boolean isError(final String value) {
        return IGNOREABLE_ERROR_SET.contains(value) || ERROR_SET.contains(value);
    }

    public static final boolean isAccountingFunction(final String s) {
        return ACCOUNTING_FUNCTIONS_SET.contains(s);
    }

    public static String getDisplayableFunctionName(final String functionName) {
        //falls mal die Funktionen tatsächlich auf enum umgestellt werden, muss das hier anders laufen
        return getResString(functionName);
    }

}
