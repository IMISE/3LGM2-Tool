package de.imise.util.swing.component.list;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * Abstraktes {@link ListModel} welches die Editierbarkeit der Zellen verwaltet.
 *
 * @author fstephan
 */
public abstract class EditableListModel extends AbstractListModel {

    /**
     * Gibt wieder, ob die Zelle am spezifizierten Index editierbar ist.
     *
     * @param index
     * @return
     *         <ul>
     *         <code>true</code> - Zelle editierbar<br>
     *         <code>false</code> - Zelle nicht editierbar
     */
    public boolean isCellEditable(final int index) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireContentsChanged(java.lang.Object,
     * int, int)
     */
    @Override
    public void fireContentsChanged(final Object source, final int index0, final int index1) {
        super.fireContentsChanged(source, index0, index1);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireIntervalAdded(java.lang.Object,
     * int, int)
     */
    @Override
    public void fireIntervalAdded(final Object source, final int index0, final int index1) {
        super.fireIntervalAdded(source, index0, index1);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireIntervalRemoved(java.lang.Object,
     * int, int)
     */
    @Override
    public void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        super.fireIntervalRemoved(source, index0, index1);
    }

    /**
     * Setzt den Wert der Zelle am spezifizierten Index
     *
     * @param value Neuer Wert der Zelle
     * @param index Index der Zelle
     */
    public abstract void setElementAt(Object value, int index);

}
