package de.imise.tool3lgm.graphtools.userfield.definition.type;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public abstract class ListUserField extends ValueUserField {

    /**
     * Beinhaltet alle Einträge, die in UserFields mit den Styles ComboBox und
     * RadioButton auftauchen sollen.
     */
    protected ArrayList<String> listValues;

    /**
     * @param targetClass
     */
    public ListUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * @param targetClass
     * @param id
     */
    public ListUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

    /**
     * Entfernt die Zahlenformatierungs- und Einheitsdefinition
     */
    public void clearValues() {
        if (listValues != null) {
            listValues.clear();
        }
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

}
