package de.imise.tool3lgm.graphtools.userfield.definition.type;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldNumberFormat;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
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
public abstract class UserField extends NameAndDescriptionTargetAdapter implements IDSource, Cloneable, Comparator<UserFieldTarget> {

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
     * ID des <code>UserField</code>s. Diese ist final, da UserFields nur
     * geclont werden, um eine Sicherheitskopie vor einer Änderung anzulegen,
     * die eventuell zurück genommen wird, so dass die alte ID erhalten bleiben
     * muss.
     */
    private String id;

    /**
     * Gibt an, zu welcher Klasse das <code>UserField</code> gehört.
     */
    private Class<? extends UserFieldTarget> targetClass;

    /**
     * Gibt an, ob das <code>UserField</code> im Modellbrowser sichtbar sein
     * soll.
     */
    protected boolean treeVisibility = true;

    /**
     * Gibt an, ob das <code>UserField</code> im Modellbrowser sichtbar sein
     * soll.
     */
    protected boolean showDescriptionInDialog = false;

    /**
     * Erzeugt ein {@link UserField} für die übergebene Elementklasse
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass) {
        this(targetClass, null);
    }

    /**
     * @param targetClass
     * @param id
     */
    public UserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        this.id = Strings.isNullOrEmpty(id) ? IDStringGenerator.createIDString(USERFIELD_ID_PREFIX) : id;

        if (targetClass != null) {
            this.targetClass = targetClass;
        } else {
            this.targetClass = GLOBAL_USERFIELD_IDENTIFIER_CLASS;
        }
    }

    /**
     * Alphabetischer Vergleich der jeweiligen Werte (siehe
     * {@link String#compareTo(String)})
     *
     * @param userField
     * @param me1
     * @param me2
     * @return
     */
    protected static int alphabeticalCompare(final UserField userField, final UserFieldTarget me1, final UserFieldTarget me2) {
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

    /**
     * @param userFieldClass
     * @return <code>true</code>, wenn der übergebene {@link Style} der selbe
     *         ist, wie der dieses UserFields
     */
    public boolean hasStyle(final Class<? extends UserField> userFieldClass) {
        Class<? extends UserField> clazz = getClass();
        return userFieldClass.isAssignableFrom(clazz);
    }

    /**
     * @param userFieldClass1
     * @param userFieldClass2
     * @return <code>true</code> if the style of this is the same like one of
     *         the given styles
     */
    public boolean hasStyle(final Class<? extends UserField> userFieldClass1, final Class<? extends UserField> userFieldClass2) {
        Class<? extends UserField> clazz = getClass();
        return userFieldClass1.isAssignableFrom(clazz) || userFieldClass2.isAssignableFrom(clazz);
    }

    /**
     * @return <code>true</code>, wenn das UserField nicht für ein Element
     *         definiert ist sondern global fürs Modell oder ein Format ist
     */
    public final boolean isGlobal() {
        return targetClass == UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;
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
    public final String getID() {
        return id;
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
        return clone(false);
    }

    /**
     * @param changeID if <code>true</code> the clone gets a new generated id
     * @return
     */
    public UserField clone(final boolean changeID) {
        UserField clone = null;
        try {
            clone = (UserField) super.clone();
        } catch (Exception e) {
            //this should never happen since we are cloneable
            throw new InternalError(e);
        }
        if (changeID) {
            clone.id = createID(USERFIELD_ID_PREFIX);
        }
        return clone;
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
     * Cache for the internal style names for the UserFieldClasses
     */
    private static final Map<Class<? extends UserField>, String> CLASS_TO_INTERNAL_STYLE_NAME = new HashMap<>();

    /**
     * This method takes the simple class name, removes the string "UserField"
     * from the name, then inserts an underscore '_' at all transitions from
     * lowercase to uppercase, and then converts the string to uppercase. For
     * example, "MultiLineUserField" becomes "MULTI_LINE", IDUserField becomes
     * "ID", and so on.
     *
     * @return the internal userfield style name
     */
    protected String getInternalUserFieldStyleName() {
        Class<? extends UserField> userFieldClass = getClass();
        String internalStyleName = CLASS_TO_INTERNAL_STYLE_NAME.get(userFieldClass);
        if (internalStyleName == null) {
            String className = userFieldClass.getSimpleName();
            StringBuilder sb = new StringBuilder(className);
            String baseName = UserField.class.getSimpleName();
            int baseNameIndex = sb.indexOf(baseName);
            if (baseNameIndex >= 0) {
                sb.delete(baseNameIndex, baseNameIndex + baseName.length());
            }
            for (int i = 1; i < sb.length(); i++) {
                char c = className.charAt(i);
                if (Character.isUpperCase(c)) {
                    c = className.charAt(i - 1);
                    if (Character.isLowerCase(c)) {
                        sb.insert(i++, '_');
                    }
                } else {
                    if (i == sb.length() - 1) {
                        c = Character.toUpperCase(c);
                        sb.setCharAt(i, c);
                    }
                    c = className.charAt(i - 1);
                    c = Character.toUpperCase(c);
                    sb.setCharAt(i - 1, c);
                }
            }
            internalStyleName = sb.toString();
            CLASS_TO_INTERNAL_STYLE_NAME.put(userFieldClass, internalStyleName);
        }
        return internalStyleName;
    }

    /**
     * @return Displayable name of the style of this userfield
     */
    public String getDisplayableStyleName() {
        String styleResKey = getInternalUserFieldStyleName();
        return getResString(styleResKey);
    }

    /**
     * @return Returns the treeVisibility.
     */
    public boolean isTreeVisibility() {
        return treeVisibility;
    }

    /**
     * @return
     */
    public boolean isShowDescriptionInDialog() {
        return showDescriptionInDialog;
    }

    /**
     * @param treeVisibility The treeVisibility to set.
     */
    public void setTreeVisibility(final boolean treeVisibility) {
        this.treeVisibility = treeVisibility;
    }

    /**
     * @param showDescriptionInDialog
     */
    public void setShowDescriptionInDialog(final boolean showDescriptionInDialog) {
        this.showDescriptionInDialog = showDescriptionInDialog;
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
     * @param o1
     * @param o2
     * @return
     */
    protected int compareValues(final UserFieldTarget o1, final UserFieldTarget o2) {
        return 0;
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
    public final int compare(final UserFieldTarget o1, final UserFieldTarget o2) {
        Class<? extends UserFieldTarget> class1 = o1.getClass();
        Class<? extends UserFieldTarget> class2 = o2.getClass();
        if (!class1.equals(class2)) {
            throw new ClassCastException("Die Modelelemente sind Instanzen verschiedener Klassen.");
        }
        return compareValues(o1, o2);
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
