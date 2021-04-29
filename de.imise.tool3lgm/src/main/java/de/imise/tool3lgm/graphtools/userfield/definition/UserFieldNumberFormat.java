package de.imise.tool3lgm.graphtools.userfield.definition;

import java.text.NumberFormat;

import de.imise.tool3lgm.graphtools.IDSource;

public class UserFieldNumberFormat implements IDSource, Cloneable {

    private final String id;

    /**
     * Das eigentliche Format, wenn dieses <code>UserField</code> den Style
     * <code>FORMAT_STYLE</code> besitzt.
     */
    private NumberFormat numberFormat;

    /**
     * Dieser String wird im Format den Nachkommastellen vorangestellt, damit
     * man ihn von anderen Formatinformationen unterscheiden kann.
     */
    private static final String FORMAT_DECIMAL_PLACES_PREFIX = "d";

    /**
     * Dieser String wird im Format der Eiheit vorangestellt, damit man ihn von
     * anderen Formatinformationen unterscheiden kann.
     */
    private static final String FORMAT_UNIT_PREFIX = "u";

    /**
     * String der als Einheit bei angezeigt werden soll
     */
    private String formatUnit = null;

    /**
     *
     */
    public UserFieldNumberFormat() {
        id = createID("FRM");
    }

    /**
     * @param id
     */
    public UserFieldNumberFormat(final String id) {
        this.id = id;
    }

    @Override
    protected UserFieldNumberFormat clone() {
        UserFieldNumberFormat clone = null;
        try {
            clone = (UserFieldNumberFormat) super.clone();
        } catch (Exception e) {
            //this should never happen since we are cloneable
            throw new InternalError(e);
        }
        clone.numberFormat = numberFormat == null ? null : (NumberFormat) numberFormat.clone();
        return clone;
    }

    /**
     * Setzt den übergeben Wert als Variable
     *
     * @param fieldName Der Name der zu belegenden Variable des userFieldes
     * @param value Der Wert, mit der die Variable belegt werden soll.
     */
    public boolean putXMLFieldString(final String fieldName, final String value) {
        if (fieldName.equals("userFieldFormatString")) {
            setFractionDigitsCountAndUnit(value);
            return true;
        }
        return false;
    }

    @Override
    public String getID() {
        return id;
    }

    /**
     * Liefert das <code>NumberFormat</code> des <code>UserField</code>s. Ist
     * das <code>UserField</code> selbst ein Format, gibt es seine eigenen
     * Nachkommastellen zurück, ist es ein UserField, dem ein Format zugewiesen
     * ist (was für ein Format selbst nie zutreffen kann), dann gibt es die
     * Nachkommstellen des Formates zurück.
     *
     * @param definitions die <code>UserFieldDefinitions</code>, in denen das
     *            Format-<code>UserField</code> dieses <code>UserField</code>s
     *            defniert ist oder <code>null</code>, wenn man diese
     *            Information direkt für ein Format-<code>UserField</code>
     *            abfragen möchte.
     * @return die Anzahl der Nachkommastellen des Formates. Wenn kein Format
     *         eingestellt ist, kommt -1 zurück;
     */
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * Setzt die Einheit des <code>UserFields</code>, wenn es den Style
     * <code>FORMAT_STYLE</code>.
     *
     * @param unitString
     */
    public void setUnit(final String unitString) {
        if (unitString.trim().equals("")) {
            formatUnit = null;
        } else {
            formatUnit = unitString;
        }
    }

    /**
     * Liefert den <code>String</code>, der in Modeldatein oder in der Export
     * von <code>UserField</code>s geschrieben wird.
     *
     * @return
     */
    public String getExportString() {
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
     * Setzt die Nachkommastellenanzahl und die Einheit des Formates. Es wird
     * ein String der Form "d### uXXX" erwartet, wobei d für den String
     * {@link #FORMAT_DECIMAL_PLACES_PREFIX} und u für
     * {@link #FORMAT_UNIT_PREFIX} steht. "###" steht für eine Zahl und "XXX"
     * für eine beliebige Zeichenkette. Vor dem 'u' können beliebig viele
     * Whitespaces (auch keins) stehen.
     *
     * @param formatStringWithPrefixes
     */
    private void setFractionDigitsCountAndUnit(final String formatStringWithPrefixes) {
        int decimalIndex = formatStringWithPrefixes.indexOf(FORMAT_DECIMAL_PLACES_PREFIX);
        int unitIndex = formatStringWithPrefixes.indexOf(FORMAT_UNIT_PREFIX);
        String decimalPlaces = formatStringWithPrefixes.substring(decimalIndex + FORMAT_DECIMAL_PLACES_PREFIX.length(), unitIndex).trim();
        setFractionDigits(Integer.parseInt(decimalPlaces));
        String unit = formatStringWithPrefixes.substring(unitIndex + FORMAT_UNIT_PREFIX.length());
        setUnit(unit);
    }

    /**
     * Liefert die Anzahl der Nachkommastellen. Ist das UserField selbst ein
     * Format, gibt es seine eigenen Nachkommastellen zurück, ist es ein
     * UserField, dem ein Format zugewiesen ist (was für ein Format selbst nie
     * zutreffen kann), dann gibt es die Nachkommstellen des Formates zurück.
     *
     * @return die Anzahl der Nachkommastellen des Formates. Wenn kein Format
     *         eingestellt ist, kommt -1 zurück;
     */
    public int getFractionDigits() {
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
    public void setFractionDigits(final int fractionDigits) {
        if (fractionDigits < 0) {
            return;
        }
        if (numberFormat == null) {
            numberFormat = NumberFormat.getNumberInstance();
        }
        numberFormat.setMinimumFractionDigits(fractionDigits);
        numberFormat.setMaximumFractionDigits(fractionDigits);
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
    public String getUnit() {
        return formatUnit;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof UserFieldNumberFormat && ((UserFieldNumberFormat) obj).getID().equals(id);
    }

    @Override
    public String toString() {
        int minimumFractionDigits = getFractionDigits();
        StringBuilder sb = new StringBuilder("#0");
        if (minimumFractionDigits > 0) {
            sb.append(".0");
        }
        for (int i = 1; i < minimumFractionDigits; i++) {
            sb.append("0");
        }
        if (minimumFractionDigits > 0) {
            sb.append("#");
        }
        String formatUnit = getUnit();
        if (formatUnit != null) {
            sb.append(" ");
            sb.append(formatUnit);
        }
        return sb.toString();
    }

    /**
     * @param object
     * @return
     * @see java.text.NumberFormat#format(Object)
     */
    public final String format(final Object object) {
        return numberFormat.format(object);
    }

}
