package de.imise.util.swing.component;

import java.awt.Component;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;

import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;

/**
 * Combo box, which displays one or more separate lists of items
 * always sorted alphabetically.<br>
 * .
 * The sorting is done by the class {@link Alphabetical}.<br>
 * <br>
 * All entries of the selection list except Separators can be
 * indented to the right by any number of spaces. <br>
 * Separators can be added to the list either invisibly or as
 * non-selectable entries with their own label.
 *
 * @author AXS
 *         created on 15.08.2007
 */
public class AlphabeticalComboBox<E> extends JComboBox<NamedObjectContainer<E>> {

    /**
     * Reference to the vector of the combo box entries.
     * (The parent vector has the visibility 'package').
     */
    private final Vector<NamedObjectContainer<E>> items = new Vector<>();

    /**
     * Entry for an empty line to make "no selection".
     */
    public final NamedObjectContainer<E> EMPTY_VALUE_ENTRY = new NamedObjectContainer<>(null, " ");

    /**
     * Index from which new elements are sorted alphabetically.
     * All entries before that remain unchanged in their order.
     */
    int newListStartIndex = 0;

    /**
     * Last selected index.<br>
     * Since separators should not be selectable, the index that
     * was selected before a separator was selected is noted in
     * this variable and the selection is reset to this separator.
     */
    private int lastCorrectSelectedIndex = -1;

    /**
     * Legt eine neue ComboBox an, die ihre Einträge alphabetisch sortiert.
     */
    public AlphabeticalComboBox() {
        this(0);
    }

    /**
     * Creates a new ComboBox that sorts its entries alphabetically.
     *
     * @param addEmptyItem
     *            if true a blank item is added at the top
     */
    public AlphabeticalComboBox(final boolean addEmptyItem) {
        this(0);
        if (addEmptyItem) {
            addItem(EMPTY_VALUE_ENTRY);
        }
    }

    /**
     * Creates a new ComboBox that sorts its entries alphabetically.<br>
     * The selection list is filled with the elements of the passed list.
     *
     * @param objects
     *            Initial elements in the drop-down list
     */
    public AlphabeticalComboBox(final Collection<E> objects) {
        this(objects, 0);
    }

    /**
     * Creates a new ComboBox that sorts its entries alphabetically.<br>
     * All entries of the drop-down list that are added are preceded by
     * <code>shift</code> blanks in the display of the drop-down list.
     *
     * @param shift
     *            Number of spaces to be displayed before each list entry
     */
    public AlphabeticalComboBox(final int shift) {
        DefaultComboBoxModel<NamedObjectContainer<E>> model = new DefaultComboBoxModel<>(items);
        setModel(model);
        setRenderer(new MyRenderer(shift));
        //30 lines should actually always be displayable
        setMaximumRowCount(30);
    }

    /**
     * Creates a new ComboBox that sorts its entries alphabetically.<br>
     * The selection list is filled with the elements of the passed list.
     * All entries of the drop-down list that are added are preceded by
     * <code>shift</code> spaces in the display of the drop-down list.
     *
     * @param objects
     *            Initial elements in the drop-down list
     * @param shift
     *            Number of spaces to be displayed before each list entry
     */
    public AlphabeticalComboBox(final Collection<E> objects, final int shift) {
        this(shift);
        for (E o : objects) {
            addItemInternal(o);
        }
        Alphabetical.sort(items);
    }

    /**
     * Creates a new ComboBox that sorts its entries alphabetically.<br>
     * The selection list is filled with the elements of the passed list.
     *
     * @param objects
     *            Initial elements in the drop-down list
     */
    @SafeVarargs
    public AlphabeticalComboBox(final E... objects) {
        this(0, objects);
    }

    /**
     * Creates a new ComboBox that sorts its entries alphabetically.<br>
     * The selection list is filled with the elements of the passed list.
     * All entries of the drop-down list that are added are preceded by
     * <code>shift</code> spaces in the display of the drop-down list.
     *
     * @param shift
     *            Number of spaces to be displayed before each list entry
     * @param objects
     *            Initial elements in the drop-down list
     */
    @SafeVarargs
    public AlphabeticalComboBox(final int shift, final E... objects) {
        this(shift);
        for (E o : objects) {
            addItemInternal(o);
        }
        Alphabetical.sort(items);
    }

    /**
     * Encloses the passed object with a NamedObjectContainer.
     * The string of this container is the String.valueOf(Object).
     *
     * @param object
     */
    private void addItemInternal(final E object) {
        NamedObjectContainer<E> namedObjectContainer = new NamedObjectContainer<>(object, String.valueOf(object));
        items.add(namedObjectContainer);
    }

    /**
     * Re-sorts all lists
     */
    public void resort() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof AlphabeticalComboBox.SeparatorItem) {
                continue;
            }
            int j = i + 1;
            for (; j < items.size(); j++) {
                if (!(items.get(i) instanceof AlphabeticalComboBox.SeparatorItem)) {
                    continue;
                }
                j--;
                break;
            }
            if (j == items.size()) {
                j--;
            }
            if (j < items.size()) {
                List<NamedObjectContainer<E>> subList = items.subList(i, j + 1);
                Alphabetical.sort(subList);
                for (int k = i; k <= j; k++) {
                    items.set(k, subList.get(k - i));
                }
            }
            i = j + 1;
        }
        revalidate();
        repaint();
    }

    @Override
    public void insertItemAt(final NamedObjectContainer<E> anObject, final int index) {
        if (newListStartIndex > 0) {
            List<NamedObjectContainer<E>> sortedSubList = items.subList(newListStartIndex, items.size());
            int insertPos = Alphabetical.getInsertPosition(sortedSubList, anObject);
            insertPos += newListStartIndex;
            super.insertItemAt(anObject, insertPos);
        } else {
            super.insertItemAt(anObject, Alphabetical.getInsertPosition(items, anObject));
        }
    }

    @Override
    public void addItem(final NamedObjectContainer<E> anObject) {
        insertItemAt(anObject, 0);
    }

    /**
     * Inserts all entries into this ComboBox.
     *
     * @param entries
     * @see #addItem(NamedObjectContainer)
     */
    public void addAllItems(final Iterable<NamedObjectContainer<E>> entries) {
        for (NamedObjectContainer<E> o : entries) {
            addItem(o);
        }
    }

    /**
     * Creates a NamedObjectContainer for all passed objects and adds it
     * to the item list.
     *
     * @param entries
     *            the objects to be added
     * @see #addObject(Object)
     */
    public void addAllObjects(final Iterable<E> objects) {
        for (E object : objects) {
            addObject(object);
        }
    }

    /**
     * Sets the given entries in this box (reomve all and then add all)
     *
     * @param entries
     *            Objects to be set as drop-down list
     */
    public void setAllObjects(final Iterable<E> objects) {
        removeAllItems();
        addAllObjects(objects);
    }

    /**
     * Selects the first occurrence of the passed object in the list.
     * From the {@link NamedObjectContainer} in the list, the method
     * <code>getObjectAt(int)<code> is called and the passed object is
     * compared with it. If they are equal, the line is selected.
     *
     * @param o
     *            Object that is equal to the object to be selected
     */
    public int setSelectedObject(final E o) {
        for (int i = 0; i < getItemCount(); i++) {
            Object itemObject = getObjectAt(i);
            if (Objects.equals(itemObject, o)) {
                setSelectedIndex(i);
                return i;
            }
        }
        if (o == null) {
            setSelectedIndex(-1);
        }
        return -1;
    }

    /**
     * Selects the first occurrence of the passed string in the list.
     * From the {@link NamedObjectContainer} in the list, the method
     * <code>toString()<code> is called and the passed string is
     * compared with it. If they are equal, the line is selected.
     *
     * @param s
     *            String that is equal to the string to be selected
     */
    public int setSelectedString(final String s) {
        for (int i = 0; i < getItemCount(); i++) {
            String itemString = getStringAt(i);
            if (Objects.equals(itemString, s)) {
                setSelectedIndex(i);
                return i;
            }
        }
        if (s == null) {
            setSelectedIndex(-1);
        }
        return -1;
    }

    /**
     * @return The currently displayed text. This can be that
     *         of a selected item or just the current text in
     *         the editor, but not yet added to the list.
     */
    public String getText() {
        Object item = editor.getItem();
        return item.toString();
    }

    /**
     * @return The object of the selected item or
     *         <code>null</code> if no item is selected.
     */
    public E getSelectedObject() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex >= 0) {
            return getObjectAt(selectedIndex);
        }
        return null;
    }

    /**
     * @return The string of the selected item or the current
     *         text in the editor if no item is selected.
     */
    public String getSelectedString() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex >= 0) {
            return getStringAt(selectedIndex);
        }
        return getText();
    }

    /**
     * @param index
     * @return the object of the {@link NamedObjectContainer} with
     *         the index in the list of this ComboBox.
     */
    public E getObjectAt(final int index) {
        NamedObjectContainer<E> itemAtIndex = getItemAt(index);
        if (itemAtIndex == null) {
            return null;
        }
        return itemAtIndex.getObject();
    }

    /**
     * @param index
     * @return the String of the {@link NamedObjectContainer} with
     *         the index in the list of this ComboBox.
     */
    public String getStringAt(final int index) {
        NamedObjectContainer<E> itemAtIndex = getItemAt(index);
        if (itemAtIndex == null) {
            return null;
        }
        return itemAtIndex.getString();
    }

    /**
     * Inserts a <code>SeparatorItem</code> at the end of the
     * existing list and starts a new alphabetically sorted list.
     * The parameter <code>showSeparatorLine</code> determines
     * whether a line should be displayed between the last and
     * the new list. If false is passed, no line is displayed.
     *
     * @param showSeparatorLine
     *            Defines whether a line should be drawn over a
     *            new list or not.
     */
    public void addSeparator(final boolean showSeparatorLine) {
        if (showSeparatorLine) {
            items.add(new SeparatorItem());
        }
        newListStartIndex = items.size();
    }

    /**
     * Inserts a <code>SeparatorItem</code> at the end of the
     * existing list and starts a new alphabetically sorted list.
     *
     * @name <code>String</code> to be displayed as separator. If
     *       it is <code>null</code>, only a line is displayed.
     */
    public void addSeparator(final String name) {
        items.add(new SeparatorItem(name));
        newListStartIndex = items.size();
    }

    /**
     * Adds an entry to the drop-down list.
     *
     * @param anObject
     *            Object that is added to the drop-down list in a
     *            {@link NamedObjectContainer}
     */
    public NamedObjectContainer<E> addObject(final E anObject) {
        return addObject(anObject, 0);
    }

    /**
     * Adds <code>NamedObjectContainer</code> to the list with
     * <code>anObject</code> as object and the display string
     * <code>String.valueOf(anObject)</code>, preceded by
     * <code>shift</code> spaces.
     *
     * @param anObject
     *            Object that is added to the drop-down list in a
     *            {@link NamedObjectContainer}
     * @param shift
     *            Number of spaces to be displayed before the
     *            list entry
     */
    public NamedObjectContainer<E> addObject(final E anObject, final int shift) {
        return addObject(anObject, String.valueOf(anObject), shift);
    }

    /**
     * Add to the list <code>NamedObjectContainer</code> with
     * <code>anObject</code> as object and the display string
     * <code>displayName</code>
     * .
     *
     * @param anObject
     *            the object to be added
     * @param displayName
     *            the display name of the object in the list
     */
    public NamedObjectContainer<E> addObject(final E anObject, final String displayName) {
        return addObject(anObject, displayName, 0);
    }

    /**
     * Add to the list <code>NamedObjectContainer</code> with
     * <code>anObject</code> as object and the display string
     * <code>displayName</code>. The display name is indented
     * by <code>shift</code> positions.
     *
     * @param anObject
     *            the object to be added
     * @param displayName
     *            the display name of the object in the list
     * @param shift
     *            Number of spaces to be displayed before the
     *            list entry
     */
    public NamedObjectContainer<E> addObject(final E anObject, String displayName, final int shift) {
        StringBuilder sb = new StringBuilder(displayName.length() + shift);
        for (int i = 0; i < shift; i++) {
            sb.append(" ");
        }
        sb.append(displayName);
        displayName = sb.toString();
        NamedObjectContainer<E> noc = new NamedObjectContainer<>(anObject, displayName);
        insertItemAt(noc, 0);
        return noc;
    }

    @Override
    protected void selectedItemChanged() {
        //perform the selection first
        super.selectedItemChanged();
        //get the selected object
        Object selected = getSelectedItem();
        //if nothing is selected -> out
        if (selected == null) {
            return;
        }
        //if a separator was selected
        if (selected.getClass() == SeparatorItem.class) {
            //reset selection to the previously selected entry
            setSelectedIndex(lastCorrectSelectedIndex);
        } else {
            //store the index of the now selected element
            lastCorrectSelectedIndex = getSelectedIndex();
        }
    }

    /**
     * A separator that can be added to the list of a
     * {@link AlphabeticalComboBox} to separate multiple lists
     * from each other.<br>
     * If no own {@link String} is passed to the separator, it
     * will be rendered as <code>JSeparator</code>. If a
     * <code>String name</code> is passed, it will be displayed.
     *
     * @author AXS
     * @created 19.10.2007
     */
    private final class SeparatorItem extends NamedObjectContainer<E> {

        /**
         * Creates a new separator that is rendered as a line in the
         * {@link AlphabeticalComboBox}.
         */
        public SeparatorItem() {
            this(null);
        }

        /**
         * @param name String to be displayed
         */
        public SeparatorItem(final String name) {
            super(null, name);
        }

    }

    /**
     * Makes separator items non-selectable and non-enabled.The rest of
     * the list is rendered by default.
     *
     * @author AXS
     * @created 19.10.2007
     */
    private class MyRenderer extends DefaultListCellRenderer {

        /**
         * String consisting of blanks, which are placed in front of all
         * entries except separators, if a number <code>shift > 0</code>
         * was passed in the constructor.<br>
         * The number of spaces is defined by <code>shift</code>.
         */
        private String shiftString = null;

        /**
         * Creates a new renderer that shifts all entries that are not
         * separators by <code>shift</code> spaces to the right.
         *
         * @param shift
         *            Number of spaces by which entries should be shifted
         */
        public MyRenderer(int shift) {
            super();
            //build shiftString from as few spaces as shift > 0
            if (shift > 0) {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(' ');
                } while (shift-- > 0);
                shiftString = sb.toString();
            }
        }

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, boolean isSelected, boolean cellHasFocus) {
            JLabel c = null;
            //separators
            if (value instanceof AlphabeticalComboBox.SeparatorItem) {
                //disabled and not selectable
                cellHasFocus = false;
                isSelected = false;
                //if a separator should be displayed without special text
                if (((AlphabeticalComboBox.SeparatorItem) value).getSecondItem() == null) {
                    return new JSeparator();
                }
                //if the separator should be displayed with text
                c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setEnabled(false);
                //normal entries
            } else {
                c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                //shift entries
                if (shiftString != null) {
                    c.setText(shiftString + c.getText());
                }

                //tooltips
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                    if (index > -1) {
                        list.setToolTipText(null);
                        Object selectedValue = list.getSelectedValue();
                        if (selectedValue != null) {
                            String itemToString = list.getSelectedValue().toString();
                            int stringWidth = list.getFontMetrics(list.getFont()).stringWidth(itemToString);
                            if (stringWidth > list.getVisibleRect().width) {
                                list.setToolTipText(itemToString);
                            }
                        }
                    }
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setFont(list.getFont());
                setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
        newListStartIndex = 0;
    }

    /**
     * @param anObject
     * @return the index of the item in the list whose object is equal
     *         to the passed one or -1 if no such item was found
     */
    public int getIndexOfObject(final E anObject) {
        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            E objectAt = getObjectAt(i);
            if (Objects.equals(objectAt, anObject)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param item
     * @return the index of the item in the list, which is equal to the
     *         passed one or -1 if no such item was found
     */
    public int getIndexOfItem(final NamedObjectContainer<E> item) {
        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            NamedObjectContainer<E> itemAt = getItemAt(i);
            if (Objects.equals(itemAt, item)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param anObject
     * @return <code>true</code> if the passed object is the object of
     *         a contained item
     */
    public boolean contains(final E anObject) {
        int index = getIndexOfObject(anObject);
        return index >= 0;
    }

    /**
     * @param anObject
     */
    public void removeObject(final E anObject) {
        int indexOfObject = getIndexOfObject(anObject);
        if (indexOfObject >= 0) {
            removeItemAt(indexOfObject);
            newListStartIndex--;
        }
    }

    /**
     * If the passed object is a NamedObjectContainer, an equal one (if
     * existing) will be removed from the item list. If it is not a
     * NamedObjectContainer itself, all elements (which are always
     * NamedObjectContainers) are checked for equality of the object
     * contained in them. If one is found, the object is removed.
     */
    @Override
    public void removeItem(final Object anObject) {
        if (anObject instanceof NamedObjectContainer) {
            int itemCount = getItemCount();
            for (int i = 0; i < itemCount; i++) {
                NamedObjectContainer<E> itemAt = getItemAt(i);
                if (Objects.equals(itemAt, anObject)) {
                    newListStartIndex--;
                    super.removeItemAt(i);
                    return;
                }
            }
        }
        try {
            @SuppressWarnings("unchecked")
            E object = (E) anObject;
            removeObject(object);
        } catch (Exception e) {
        }
    }

    @Override
    public void removeItemAt(final int anIndex) {
        super.removeItemAt(anIndex);
        if (anIndex < newListStartIndex) {
            newListStartIndex--;
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + items;
    }

}