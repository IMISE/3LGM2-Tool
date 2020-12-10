package de.imise.util.swing.component.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;

/**
 * JList, which always displays all items alphabetically sorted.
 *
 * @author AXS created on 15.08.2007
 */
public class AlphabeticalJList<T> extends JList<NamedObjectContainer<T>> {

    /**
     * ListModel, which does the alphabetical sorting
     */
    private final AlphabeticalListModel<NamedObjectContainer<T>> lm = new AlphabeticalListModel<>(null);

    /**
     *
     */
    public AlphabeticalJList() {
        setModel(lm);
    }

    /**
     * @param objects
     */
    public AlphabeticalJList(final Collection<? extends T> objects) {
        this();
        setObjects(objects);
    }

    /**
     * @param objects
     */
    public void setItems(final Collection<NamedObjectContainer<T>> objects) {
        removeAllElements();
        for (NamedObjectContainer<T> o : objects) {
            lm.addElement(o);
        }
        revalidate();
        repaint();
    }

    /**
     * @param objects
     */
    public void setObjects(final Collection<? extends T> objects) {
        removeAllElements();
        for (T o : objects) {
            String displayName = o == null ? null : o.toString();
            NamedObjectContainer<T> namedObjectContainer = new NamedObjectContainer<>(o, displayName);
            lm.addElement(namedObjectContainer);
        }
        revalidate();
        repaint();
    }

    /**
     * @param anObject
     */
    public void addItem(final NamedObjectContainer<T> anObject) {
        lm.addElement(anObject);
        revalidate();
        revalidate();
        repaint();
    }

    /**
     * Fügt zur Liste ein <code>NamedObjectContainer</code> hinzu mit
     * <code>anObject</code> als Objekt und dem Anzeige-String
     * <code>displayName</code>.
     *
     * @param anObject
     */
    public void addObject(final T anObject) {
        addObject(anObject, anObject == null ? null : anObject.toString());
    }

    /**
     * Fügt zur Liste ein <code>NamedObjectContainer</code> hinzu mit
     * <code>anObject</code> als Objekt und dem Anzeige-String
     * <code>displayName</code>.
     *
     * @param anObject
     * @param displayName
     */
    public void addObject(final T anObject, final String displayName) {
        NamedObjectContainer<T> namedObjectContainer = new NamedObjectContainer<>(anObject, displayName);
        addItem(namedObjectContainer);
    }

    /**
     * Liefert das selektierte <code>Object</code>.<br>
     * Wenn ein <code>NamedObjectContainer</code> selektiert ist, wird von
     * diesem die Methode
     * <code>getObject()<code> aufgerufen und das Ergebnis zurück gegeben, sonst wird einfach
     * das <code>Object</code> am selektierten Index zurückgegeben.
     *
     * @return selektierte Objekt
     */
    public T getSelectedObject() {
        NamedObjectContainer<T> selectedObject = getSelectedValue();
        return selectedObject == null ? null : selectedObject.getObject();
    }

    /**
     * Liefert die selektierten <code>Object</code>s.<br>
     * Wenn <code>NamedObjectContainer</code> selektiert sind, wird von diesem
     * die Methode
     * <code>getObject()<code> aufgerufen und das Ergebnis zurück gegeben, sonst wird einfach
     * das <code>Object</code> am selektierten Index zurückgegeben.
     *
     * @return selektierte Objekt
     */
    public List<T> getSelectedObjects() {
        List<NamedObjectContainer<T>> selectedItems = getSelectedValuesList();
        int size = selectedItems.size();
        List<T> selectedObjects = new ArrayList<>(size);
        for (NamedObjectContainer<T> selectedItem : selectedItems) {
            T selectedObject = selectedItem.getObject();
            selectedObjects.add(selectedObject);
        }
        return selectedObjects;
    }

    /**
     *
     */
    public void removeAllElements() {
        lm.removeAllElements();

    }

    /**
     * Listmodel, das alle Einträge immer alphabetisch einsortiert. Wird beim
     * Hinzufügen ein Index mit angegeben, an dem das neue Element eingefügt
     * werden soll, wird dieser ignoriert.
     *
     * @author AXS
     */
    private class AlphabeticalListModel<E> extends DefaultListModel<E> {

        /**
         * @param items
         */
        public AlphabeticalListModel(final Collection<? extends E> items) {
            if (items != null) {
                addAll(items);
            }
        }

        @Override
        public void addAll(final Collection<? extends E> items) {
            for (Iterator<? extends E> it = items.iterator(); it.hasNext();) {
                addElement(it.next());
            }
        }

        @Override
        public void add(int index, final E element) {
            Object[] array = toArray();
            index = Alphabetical.getInsertPosition(array, element);
            super.add(index, element);
        }

        @Override
        public void addElement(final E obj) {
            add(0, obj);
        }

        @Override
        public void insertElementAt(final E obj, final int index) {
            add(index, obj);
        }

        @Override
        public E set(final int index, final E element) {
            E o = remove(index);
            add(0, element);
            return o;
        }

        @Override
        public void setElementAt(final E obj, final int index) {
            set(index, obj);
        }
    }

}
