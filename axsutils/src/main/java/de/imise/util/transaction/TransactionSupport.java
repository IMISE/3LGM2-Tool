package de.imise.util.transaction;

/**
 * Interface für Klassen, welche Transaktionen unterstützen.
 * 
 * @author fstephan
 */
public interface TransactionSupport {
	
	/**
	 * Rückgängigmachen der letzten Änderung
	 * @return <ul>
	 * 			<li><code>true</code>: UNDO wurde erfolgreich ausgeführt </li>
	 * 			<li><code>false</code>: UNDO konnte nicht ausgeführt werden </li>
	 */
	boolean undo();
	
	/**
	 * Wiederholen der letzten Änderung
	 * @return <ul>
	 * 			<li><code>true</code>: REDO wurde erfolgreich ausgeführt </li>
	 * 			<li><code>false</code>: REDO konnte nicht ausgeführt werden </li>
	 */
	boolean redo();
	
	/**
	 * Gibt wieder, ob UNDO verfügbar ist.
	 * @return
	 */
	boolean isUndoAvailable();
	
	/**
	 * Gibt wieder, ob REDO verfügbar ist.
	 * @return
	 */
	boolean isRedoAvailable();
	
	/**
	 * Gibt den TransactionManager der implementierenden Klasse wieder.
	 * <p>
	 * Grundsätzlich ist diese Methode optional und darf auch
	 * <code>null</code> zurückgeben.
	 * 
	 * @return
	 */
	TransactionManager getTransactionManager();

}
