package de.imise.tool3lgm.graphtools.userfield.definition.type;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public class NumberUserField extends AccountingUserField {

    /**
     * @param targetClass
     */
    public NumberUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public NumberUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

    /**
     * Wenn <code>true</code> werden nur positive Werte akzeptiert.
     */
    private boolean positiveOnly;

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

}
