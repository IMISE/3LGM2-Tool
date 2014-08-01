package de.imise.util.clipboard;

/**
 * Interface für alle Klassen, die Inhalte mit der System-Zwischenablage austauschen wollen.<br>
 * Die Methoden {@link #cut()}, {@link #copy()} und {@link #paste()} werden automatisch vom 
 * {@link ContentExchangeListener} aufgerufen. Die implementierende Klasse muss sich also nur noch darum 
 * kümmern, dass der {@link ContentExchangeListener} gesetzt wird.
 * 
 * @author fstephan
 */
public interface ContentExchanger {
	
	/** Schneidet aktuell ausgewählten Inhalt aus und übergibt ihn der System-Zwischenablage */
	void cut();
	
	/** Übergibt den aktuell ausgewählten Inhalt der System-Zwischenablage */
	void copy();
	
	/** Fügt den Inhalt der System-Zwischenablage an gewählter Stelle ein */
	void paste();
	
	/** Fügt den {@link ContentExchangeListener} <code>l</code> an */
	void addContentExchangeListener(ContentExchangeListener l);
	
	/** Entfernt den {@link ContentExchangeListener} <code>l</code> */
	void removeContentExchangeListener(ContentExchangeListener l);
}
