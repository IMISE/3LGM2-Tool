package de.imise.util.transaction;

import java.beans.PropertyChangeListener;

/**
 * Interface für die Basisfunktionalität aller TransactionManager.
 * 
 * @author fstephan
 */
public interface TransactionManager {

    /**
     * Macht die letzte Transaktion rückgängig.
     * 
     * @return
     *         <li><code>true</code>, wenn das UNDO erfolgreich ausgeführt wurde
     *         <li><code>false</code>, sonst
     * @throws TransactionManagerException Wird geworfen, wenn die aktuelle
     *             Transaktion noch nicht durchgeführt wurde.
     */
    boolean undo() throws TransactionManagerException;

    /**
     * Macht das letzte UNDO rückgängig.
     * 
     * @return
     *         <li><code>true</code>, wenn das REDO erfolgreich ausgeführt wurde
     *         <li><code>false</code>, sonst
     * @throws TransactionManagerException Wird geworfen, wenn die aktuelle
     *             Transaktion noch nicht durchgeführt wurde.
     */
    boolean redo() throws TransactionManagerException;

    /**
     * Gibt wieder, ob UNDO verfügbar ist.
     */
    boolean isUndoAvailable();

    /**
     * Gibt wieder, ob REDO verfügbar ist.
     */
    boolean isRedoAvailable();

    /**
     * Fügt einen {@link PropertyChangeListener} an, welcher benachrichtigt
     * wird, sobald UNDO bzw. REDO verfügbar sind. <br>
     * Die PropertyNames hierfür sind <tt>"undoAvailable"</tt> bzw.
     * <tt>"redoAvailable"</tt>.
     * 
     * @param l Der anzufügende Listener
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Entfernt den spezifizierten {@link PropertyChangeListener}.
     * 
     * @param l Der zu entfernende Listener
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Fügt einen {@link ExtendedTransactionListener} an, welcher benachrichtigt
     * wird, sobald ein UNDO bzw. REDO ausgeführt wird, eine Transaktion
     * geöffnet bzw. beendet wurde oder die aktuelle Transaktion abgebrochen
     * wurde.
     * 
     * @param l Der anzufügende Listener
     */
    void addTransactionListener(ExtendedTransactionListener l);

    /**
     * Entfernt den spezifizierten {@link ExtendedTransactionListener}.
     * 
     * @param l Der zu entfernende Listener
     */
    void removeTransactionListener(ExtendedTransactionListener l);

}
