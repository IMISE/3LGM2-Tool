package de.imise.util.transaction;

/**
 * Sammlung von {@link Exception}s, die durch einen TransactionManager geworfen werden.
 * @author fstephan
 *
 */
public class TransactionManagerException extends RuntimeException {
	
	TransactionManagerException(String text) {
		super(text);
	}
	
	TransactionManagerException(String message, Throwable cause) {
	    super(message, cause);
    }

	public static final TransactionManagerException noOpenTransaction() {
		return new TransactionManagerException("Es wurde noch keine Transaktion geöffnet");
	}
	
	public static final TransactionManagerException alreadyOpenTransaction() {
		return new TransactionManagerException("Es existiert bereits eine offene Transaktion");
	}
	
	public static final TransactionManagerException transactionNotProcessable(Throwable cause) {
		return new TransactionManagerException("Die Transaktion konnte nicht ausgeführt werden", cause);
	}
	
	public static final TransactionManagerException transactionAlreadyExecuted() {
		return new TransactionManagerException("Die Transaktion wurde bereits ausgeführt");
	}
	
	public static final TransactionManagerException undoAlreadyPerformed() {
		return new TransactionManagerException("Die Transaktion wurde bereits rückgängig gemacht");
	}
	
	public static final TransactionManagerException redoAlreadyPerformed() {
		return new TransactionManagerException("Die Transaktion wurde bereits wiederhergestellt gemacht");
	}
	
	public static final TransactionManagerException currentTransactionNotExecuted() {
		return new TransactionManagerException("Aktuelle Transaktion wurde noch nicht ausgeführt");
	}
	
	
	
	
}
