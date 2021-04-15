package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.util.NamedObjectContainer;
import de.imise.util.StringUtils;

/**
 * {@link JList}, die UserFields anzeigen kann.
 *
 * @author AXS
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
        List<NamedObjectContainer<UserField>> selectedValuesList = getSelectedValuesList();
        clear();
        int indent = 0;
        for (UserField userField : definitions.getUserFields(selectedClass)) {
            if (userField.hasStyle(TAB)) {
                addEntry(userField, 0);
                indent = 1;
            } else if (userField.hasStyle(GROUP)) {
                addEntry(userField, 1);
                indent = 2;
            } else {
                addEntry(userField, indent);
            }
        }
        restoreSelection(selectedValuesList);
    }

    /**
     * @param selectedValuesList
     */
    private void restoreSelection(final List<NamedObjectContainer<UserField>> oldSelectedValuesList) {
        int[] selectedIndices = new int[oldSelectedValuesList.size()];
        int count = 0;
        for (NamedObjectContainer<UserField> oldSelectedItem : oldSelectedValuesList) {
            int index = indexOfEqualsUserField(oldSelectedItem);
            if (index >= 0) {
                selectedIndices[count++] = index;
            }
        }
        if (count < selectedIndices.length) {
            int[] newSelectedIndices = new int[count];
            System.arraycopy(selectedIndices, 0, newSelectedIndices, 0, count);
            setSelectedIndices(newSelectedIndices);
        } else {
            setSelectedIndices(selectedIndices);
        }
    }

    /**
     * @param userFieldObjectContainer
     * @return
     */
    private int indexOfEqualsUserField(final NamedObjectContainer<UserField> userFieldObjectContainer) {
        int itemCount = model.getSize();
        UserField userField = userFieldObjectContainer.getObject();
        for (int i = 0; i < itemCount; i++) {
            NamedObjectContainer<UserField> listUserFieldObjectContainer = model.get(i);
            UserField listUserField = listUserFieldObjectContainer.getObject();
            if (listUserField.equals(userField)) {
                return i;
            }
        }
        return -1;
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
     * @param indent
     */
    private void addEntry(final UserField userField, final int indent) {
        addEntry(userField, model.getSize(), indent);
    }

    /**
     * Fügt zur Liste der <code>UserField</code>s das übergebene
     * <code>UserField</code> hinzu.
     *
     * @param userField
     * @param index
     * @param indent
     */
    private void addEntry(final UserField userField, final int index, final int indent) {
        int whiteSpaceCount = indent * 6;
        String indentation = StringUtils.fillToMinLenght("", whiteSpaceCount);
        String name = indentation + userField.getStyle() + ": " + userField.getName();
        NamedObjectContainer<UserField> noc = new NamedObjectContainer<>(userField, name);
        model.add(index, noc);
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

    /**
     * @return the number of elements in this list
     */
    public int getElementCount() {
        return model.size();
    }

}
