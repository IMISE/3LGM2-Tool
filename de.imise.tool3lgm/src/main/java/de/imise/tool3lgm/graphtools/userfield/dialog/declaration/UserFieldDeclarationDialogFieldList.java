package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SINGLE_LINE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SUBTYPE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;
import static de.imise.util.htmlxml.HTMLConverter.encode;
import static de.imise.util.htmlxml.HTMLConverter.encodeBold;
import static de.imise.util.htmlxml.HTMLConverter.getTextAsHTMLLabelText;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.apache.jena.ext.com.google.common.primitives.Ints;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.tree.TreeRenderer;
import de.imise.util.NamedObjectContainer;
import de.imise.util.StringUtils;

/**
 * {@link JList}, die UserFields anzeigen kann.
 *
 * @author AXS
 */
public class UserFieldDeclarationDialogFieldList extends JList<NamedObjectContainer<UserField>> {

    /**
     * Number of whitespaces for one indentation step
     */
    private static final int INDENTATION_WITH = 6;

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
        setCellRenderer(new MyListCellRenderer());
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
        int subTypeIndent = 0;
        for (UserField userField : definitions.getUserFields(selectedClass)) {
            if (userField.hasStyle(SUBTYPE)) {
                addEntry(userField, 0);
                subTypeIndent = 1;
            } else if (userField.hasStyle(TAB)) {
                addEntry(userField, subTypeIndent);
                indent = subTypeIndent + 1;
            } else if (userField.hasStyle(GROUP)) {
                addEntry(userField, subTypeIndent + 1);
                indent = subTypeIndent + 2;
            } else {
                addEntry(userField, indent);
            }
        }
        restoreSelection(selectedValuesList);
    }

    /**
     * Restores an old selection. If the model was relaoded the old selected
     * elementes ({@link NamedObjectContainer}) can be identified by the same
     *
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
        String name = userField.getName();
        name = StringUtils.trimAndRemoveNewLines(name);
        name = indentation + "<HTML>" + encodeBold(userField.getStyle() + ": ") + encode(name) + "</HTML>";
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
     * @return the index where a new item will be inserted depending on the
     *         selection.
     */
    public int getNextInsertIndex() {
        int[] selectedIndices = getSelectedIndices();
        if (selectedIndices.length == 0) { //nothing selected -> insert after last index
            return getElementCount();
        }
        return selectedIndices[selectedIndices.length - 1] + 1;
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
    private void move(final int step) {
        List<NamedObjectContainer<UserField>> selectedValuesList = getSelectedValuesList();
        int[] selectedIndices = getSelectedIndices();
        List<Integer> indices = Ints.asList(selectedIndices);
        if (step > 0) {
            Collections.reverse(indices);
        }
        for (int i = 0; i < selectedIndices.length; i++) {
            int newIndex = selectedIndices[i] + step;
            if (0 <= newIndex && newIndex < model.size()) {
                UserField userField = get(selectedIndices[i]);
                NamedObjectContainer<UserField> element2Move = model.remove(selectedIndices[i]);
                model.insertElementAt(element2Move, newIndex);
                definitions.insert(userField, newIndex);
            }
        }
        restoreSelection(selectedValuesList);
    }

    /**
     * @return the number of elements in this list
     */
    public int getElementCount() {
        return model.size();
    }

    /**
     * @param index
     * @return
     */
    public UserField getUserField(final int index) {
        NamedObjectContainer<UserField> listItem = model.get(index);
        return listItem.getObject();
    }

    /**
     * @param index
     * @param style
     * @return <code>true</code> if the style at the index in the list is one of
     *         the paramter styles
     */
    public boolean hasStyle(final int index, final Style... styles) {
        UserField userField = getUserField(index);
        for (Style style : styles) {
            if (userField.hasStyle(style)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        Point point = e.getPoint();
        int indexUnderMouse = locationToIndex(point);
        String description = null;
        if (indexUnderMouse >= 0) {
            NamedObjectContainer<UserField> item = model.get(indexUnderMouse);
            UserField userField = item.getObject();
            description = userField.getDescription();
            description = description.trim(); // is never null
            description = description.isEmpty() ? null : getTextAsHTMLLabelText(description);
        }
        return description;
    }

    /**
     * @author AXS (21.04.2021)
     */
    private class MyListCellRenderer extends DefaultListCellRenderer {

        private final JPanel panel;
        private final JLabel icon_text_label;
        private final JLabel indentation_label;

        private final Color textSelectionColor = UIManager.getColor("List.selectionForeground");
        private final Color backgroundSelectionColor = UIManager.getColor("List.selectionBackground");
        private final Color textNonSelectionColor = UIManager.getColor("List.foreground");
        private final Color backgroundNonSelectionColor = UIManager.getColor("List.background");

        private final ImageIcon USERFIELD_ICON = TreeRenderer.getStyleIcon(SINGLE_LINE);
        private final ImageIcon USERFIELD_GROUP_ICON = TreeRenderer.getStyleIcon(GROUP);
        private final ImageIcon USERFIELD_TAB_ICON = TreeRenderer.getStyleIcon(TAB);

        MyListCellRenderer() {
            panel = new JPanel(new BorderLayout());
            indentation_label = new JLabel();
            indentation_label.setOpaque(true);
            icon_text_label = new JLabel();
            icon_text_label.setOpaque(true);

            panel.add(indentation_label, BorderLayout.WEST);
            panel.add(icon_text_label, BorderLayout.CENTER);

        }

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            if (value instanceof NamedObjectContainer<?>) {
                NamedObjectContainer<?> noc = (NamedObjectContainer<?>) value;
                Object object = noc.getFirstItem();
                if (object instanceof UserField) {
                    UserField userField = (UserField) object;
                    if (userField.hasStyle(SUBTYPE)) {
                        icon_text_label.setIcon(USERFIELD_TAB_ICON); //TODO: own icon for subtypes
                    } else if (userField.hasStyle(TAB)) {
                        icon_text_label.setIcon(USERFIELD_TAB_ICON);
                    } else if (userField.hasStyle(GROUP)) {
                        icon_text_label.setIcon(USERFIELD_GROUP_ICON);
                    } else {
                        icon_text_label.setIcon(USERFIELD_ICON);
                    }
                    String string = value.toString();
                    int i = 0;
                    String indent = "";
                    String text = string;
                    for (; i < string.length(); i += INDENTATION_WITH) {
                        if (string.charAt(i) != ' ') {
                            if (i == 0) {
                                break;
                            } else {
                                indent = string.substring(0, i);
                                text = string.substring(i);
                                break;
                            }
                        }
                    }
                    indentation_label.setText(indent);
                    icon_text_label.setText(text);
                    if (isSelected) {
                        indentation_label.setBackground(backgroundSelectionColor);
                        icon_text_label.setBackground(backgroundSelectionColor);
                        icon_text_label.setForeground(textSelectionColor);
                    } else {
                        indentation_label.setBackground(backgroundNonSelectionColor);
                        icon_text_label.setBackground(backgroundNonSelectionColor);
                        icon_text_label.setForeground(textNonSelectionColor);
                    }
                    return panel;
                }
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

}
