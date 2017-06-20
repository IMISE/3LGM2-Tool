package de.imise.util.transaction;

import java.util.EventListener;

/**
 * Erweiterung zum {@link TransactionListener}.
 * <p>
 * Lauscht auf das Abbrechen einer Transaktion sowie auf das Durchführen von UNDO bzw. REDO.
 * <p>
 * Durch die zusätzliche Erweiterung des {@link EventListener}-Interfaces, kann der
 * {@link ExtendedTransactionListener} auch in Java-Klassen eingesetzt werden.
 * 
 * @author fstephan
 */
public interface ExtendedTransactionListener extends TransactionListener, EventListener{
	
	/** Wird aufegrufen, wenn die aktuelle Transaktion abgebrochen wurde */
	void transactionAborted();
	
	/** Wird aufegrufen, wenn ein UNDO durchgeführt wurde */
	void undoPerformed();
	
	/** Wird aufegrufen, wenn ein RENDO durchgeführt wurde */
	void redoPerformed();
	
	

}
