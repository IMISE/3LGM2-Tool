package de.imise.util.transaction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

/**
 * Abstrakter Transaction-Manager, welcher die grundlegende
 * Listener-Funktionalität bereitstellt.
 * <p>
 * <u>Unterstützte Properties:</u>
 * <ul>
 * <li>"isUndoAvailable"</li>
 * <li>"isRedoAvailable"</li>
 * </ul>
 * 
 * @author fstephan
 */
public abstract class AbstractTransactionManager implements TransactionManager {

    /** Die Liste aller Listener für die TransactionManager */
    protected EventListenerList listenerList = new EventListenerList();

    /*
     * (non-Javadoc)
     * @see
     * tool3lgm.util.transaction.TransactionManager#addPropertyChangeListener(
     * java.beans.PropertyChangeListener)
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        listenerList.add(PropertyChangeListener.class, l);
    }

    /*
     * (non-Javadoc)
     * @see
     * tool3lgm.util.transaction.TransactionManager#removePropertyChangeListener
     * (java.beans.PropertyChangeListener)
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        listenerList.remove(PropertyChangeListener.class, l);
    }

    /*
     * (non-Javadoc)
     * @see tool3lgm.util.transaction.TransactionManager#addTransactionListener(
     * tool3lgm.util.transaction.ExtendedTransactionListener)
     */
    @Override
    public void addTransactionListener(ExtendedTransactionListener l) {
        listenerList.add(ExtendedTransactionListener.class, l);
    }

    /*
     * (non-Javadoc)
     * @see
     * tool3lgm.util.transaction.TransactionManager#removeTransactionListener(
     * tool3lgm.util.transaction.ExtendedTransactionListener)
     */
    @Override
    public void removeTransactionListener(ExtendedTransactionListener l) {
        listenerList.remove(ExtendedTransactionListener.class, l);
    }

    /**
     * Benachrichtigt alle {@link ExtendedTransactionListener}s, dass eine neue
     * Transaktion geöffnet wurde.
     */
    protected void fireTransactionOpened() {
        ExtendedTransactionListener[] ls = listenerList.getListeners(ExtendedTransactionListener.class);
        for (TransactionListener l : ls)
            l.transactionStarted();
    }

    /**
     * Benachrichtigt alle {@link ExtendedTransactionListener}s, dass die
     * aktuelle Transaktion beendet ist.
     */

    protected void fireTransactionDone() {
        ExtendedTransactionListener[] ls = listenerList.getListeners(ExtendedTransactionListener.class);
        for (ExtendedTransactionListener l : ls)
            l.transactionStopped();
    }

    /**
     * Benachrichtigt alle {@link ExtendedTransactionListener}s, dass die
     * aktuelle Transaktion abgebrochen wurde.
     */
    protected void fireTransactionAborted() {
        ExtendedTransactionListener[] ls = listenerList.getListeners(ExtendedTransactionListener.class);
        for (ExtendedTransactionListener l : ls)
            l.transactionAborted();
    }

    /**
     * Benachrichtig alle {@link PropertyChangeListener}, dass jetzt das UNDO
     * verfügbar ist.
     * 
     * @param oldValue alter Wert, der "undoAvailable"-Property
     * @param newValue neuer Wert, der "undoAvailable"-Property
     */
    protected void fireUndoAvailable(boolean oldValue, boolean newValue) {
        firePropertyChanged("undoAvailable", oldValue, newValue);
    }

    /**
     * Benachrichtig alle {@link PropertyChangeListener}, dass jetzt das REDO
     * verfügbar ist.
     * 
     * @param oldValue alter Wert, der "redoAvailable"-Property
     * @param newValue neuer Wert, der "redoAvailable"-Property
     */
    protected void fireRedoAvailable(boolean oldValue, boolean newValue) {
        firePropertyChanged("redoAvailable", oldValue, newValue);
    }

    /**
     * Benachrichtigt alle {@link ExtendedTransactionListener}s, dass ein UNDO
     * durchgeführt wurde.
     */
    protected void fireUndoPerformed() {
        ExtendedTransactionListener[] ls = listenerList.getListeners(ExtendedTransactionListener.class);
        for (ExtendedTransactionListener l : ls)
            l.undoPerformed();
    }

    /**
     * Benachrichtigt alle {@link ExtendedTransactionListener}s, dass ein REDO
     * durchgeführt wurde.
     */
    protected void fireRedoPerformed() {
        ExtendedTransactionListener[] ls = listenerList.getListeners(ExtendedTransactionListener.class);
        for (ExtendedTransactionListener l : ls)
            l.redoPerformed();
    }

    /**
     * Benachrichtig alle {@link PropertyChangeListener}, dass sich die
     * spezifizierte Property geändert hat.
     * 
     * @param propertyName Name der veränderten Property
     * @param oldValue alter Wert
     * @param newValue neuer Wert
     */
    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        PropertyChangeListener[] ls = listenerList.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener l : ls)
            l.propertyChange(e);
    }
}
