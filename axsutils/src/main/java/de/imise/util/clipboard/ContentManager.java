package de.imise.util.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

/**
 * Interface zum Austausch von Daten zwischen dieser Software und anderen Desktop-Anwendungen 
 * über die System-Zwischenablage.
 * <p>
 * Klassen, die dieses Interface implementieren, bieten die Möglichkeit, auf die 
 * System-Zwischenablage sowie auf ihre Inhalte zu zugreifen.<br>
 * Außerdem ist es möglich, den Inhalt der System-Zwischenablage zu setzen.
 * 
 * @see ClipboardOwner
 * @author fstephan
 *
 */
public interface ContentManager extends ClipboardOwner {
	
	/** 
	 * Gibt die System-Zwischenablage wieder 
	 */
	Clipboard getClipboard();
	
	/** 
	 * Gibt den gesamten Inhalt der System-Zwischenablage wieder 
	 */
	Transferable getClipboardContents();
	
	/** 
	 * Gibt einen ausgewählten Teil der Inhalte der System-Zwischenablage wieder.<br>
	 * Das zurückgegebene Objekt ist zur Weiterverarbeitung durch einen {@link ContentParser}
	 * geeignet.
	 */
	Object getClipboardContent();
	
	/**
	 * Setzt den Inhalt der System-Zwischenablage auf <code>content</code>
	 * @param contents
	 */
	void setClipboardContent(Object content);
	

}
