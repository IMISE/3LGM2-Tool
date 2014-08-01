package de.imise.util.transaction;

/**
 * Implementierung des Konzeptes einer invertierbaren Funktion zur Verwendung
 * bei Transaktionen. 
 * <p>
 * Die Funktion wird über {@link #execute()} ausgeführt und kann durch {@link #invert()}
 * rückgängig gemacht werden.
 * 
 * @see FunctionalTransactionManager
 * 
 * @author fstephan
 */
public interface InvertibleFunction {
	
	/**
	 * Führt diese Funktion aus. 
	 * <p>
	 * Im Falle eines Fehlers muss es über {@link #invert()} möglich sein, die bis zum 
	 * Auftreten des Fehlers durchgeführten Änderungen rückgängig zu machen.
	 * 
	 * @throws RuntimeException
	 */
	void execute() throws RuntimeException;
	
	/**
	 * Diese Methode macht die durch {@link #execute()} durchgeführten Änderungen rückgängig.
	 * 
	 * @throws RuntimeException
	 */
	void invert() throws RuntimeException;
}
