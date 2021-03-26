package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.util.NamedObjectContainer;

/**
 * {@link JList}, die UserFields anzeigen kann.
 *
 * @author astruebi
 */
public class UserFieldDeclarationDialogFieldList extends JList<NamedObjectContainer<UserField>> {

    /**
     *
     */
    private final UserFieldDefinitions definitions;

    /**
     *
     */
    private final DefaultListModel<NamedObjectContainer<UserField>> model;

    /**
     * @param definitions
     */
    public UserFieldDeclarationDialogFieldList(final UserFieldDefinitions definitions) {
        model = new DefaultListModel<>();
        setModel(model);
        this.definitions = definitions;
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * Aktualisiert die Liste der {@link UserField}s für die selektierte Klasse
     */
    public void update(final Class<? extends UserFieldTarget> selectedClass) {
        clear();
        for (UserField uf : definitions.getUserFields(selectedClass)) {
            addEntry(uf);
        }
    }

    /**
     *
     */
    private void clear() {
        model.removeAllElements();
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene
     * <code>UserField</code> hinzu.
     *
     * @param userField
     */
    public void addEntry(final UserField userField) {
        addEntry(userField, model.getSize());
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene
     * <code>UserField</code> hinzu.
     *
     * @param userField
     * @param index
     */
    public void addEntry(final UserField userField, final int index) {
        String name = userField.getName() + "  ( " + userField.getStyle() + " )";
        NamedObjectContainer<UserField> noc = new NamedObjectContainer<>(userField, name);
        model.add(index, noc);
    }

    /**
     *
     */
    public void refreshSelected() {
        //aus der Liste entfernen und wieder hinzufügen, damit der Anzeigename korrekt aktualisert wird
        int selectedIndex = getSelectedIndex();
        //Das Element aus der Liste entfernen und an alter Stelle wieder neu hinzufügen,
        //damit der evtl. geänderte korrekt Name angezeigt wird
        NamedObjectContainer<UserField> removed = model.remove(selectedIndex);
        addEntry(removed.getObject(), selectedIndex);
        setSelectedIndex(selectedIndex);
    }

    /**
     * @return
     */
    public UserField getSelected() {
        return get(getSelectedIndex());
    }

    /**
     * @param i
     * @return
     */
    public UserField get(final int i) {
        NamedObjectContainer<UserField> selectedValue = i < 0 ? null : model.get(i);
        return selectedValue == null ? null : selectedValue.getObject();
    }

    /**
     *
     */
    public void moveUp() {
        move(-1);
    }

    /**
     *
     */
    public void moveDown() {
        move(1);
    }

    /**
     * Verschiebt das selektierte {@link UserField} um i Schritte (wenn die
     * Liste das zulässt). In den {@link UserFieldDefinitions} wird das
     * UserField ebenfalls verschoben.
     *
     * @param i
     */
    private void move(final int i) {
        int selectedIndex = getSelectedIndex();
        int newIndex = selectedIndex + i;
        if (0 <= newIndex && newIndex < model.size()) {
            UserField userField = get(selectedIndex);
            model.insertElementAt(model.remove(selectedIndex), newIndex);
            definitions.insert(userField, newIndex);
            setSelectedIndex(newIndex);
        }
    }

}
