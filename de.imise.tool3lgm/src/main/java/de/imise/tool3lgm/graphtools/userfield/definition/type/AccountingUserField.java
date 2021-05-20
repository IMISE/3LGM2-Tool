package de.imise.tool3lgm.graphtools.userfield.definition.type;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldNumberFormat;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public abstract class AccountingUserField extends ValueUserField {

    /**
     * UserField mit dem Style <code>FORMAT_STYLE</code>, das vorgibt, wie der
     * Zahlenwert dieses UserFields formatiert werden soll.
     */
    private UserFieldNumberFormat numberFormat;

    /**
     * @param targetClass
     */
    public AccountingUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public AccountingUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

    @Override
    public final int compareValues(final UserFieldTarget me1, final UserFieldTarget me2) {
        return numberCompare(me1, me2);
    }

    /** Vergleich der jeweiligen Werte für Kennzahlen/Kennzahlformeln */
    /**
     * @param userField
     * @param me1
     * @param me2
     * @return
     */
    private int numberCompare(final UserFieldTarget me1, final UserFieldTarget me2) {
        String v1 = me1.getValue(this);
        String v2 = me2.getValue(this);
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

    /**
     * @param numberFormat
     * @return
     */
    public boolean hasNumberFormat(final UserFieldNumberFormat numberFormat) {
        return this.numberFormat == numberFormat;
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
    @Override
    public String getFormatUnit() {
        return numberFormat == null ? null : numberFormat.getUnit();
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

}
