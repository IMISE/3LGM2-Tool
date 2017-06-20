package de.imise.tool3lgm.graphtools.undoredo;

/**
 * Listener denen der Transactionmanager Bescheid sagt, wenn er eine Transaction gestartet und beendet hat.
 * 
 * @author AXS Created on 21.04.2008
 */
public interface TransactionListener {

    /**
     * Ruft der <code>TransactionManager</code> für alle seine Listener auf, wenn er eine Transaktion begonnen hat.
     */
    public void transactionStarted();

    /**
     * Ruft der <code>TransactionManager</code> für alle seine Listener auf, wenn er eine Transaktion beendet hat.
     */
    public void transactionStopped();

}
