package de.imise.util.clipboard;

/**
 * Interface zur Verarbeitung von Inhalten der System-Zwischenablage.
 * <p>
 * Ist die Grundlage für <b>copy/paste</b> zwischen dieser Software und anderen 
 * Desktop-Anwendungen.
 * <p>
 * Klassen, die dieses Interface implementieren, bieten die Möglichkeit, Inhalte der Zwischenablage 
 * in intern nutzbare Objekte umzuwandeln, sowie aus internen Objekten Inhalte der Zwischenablage zu erzeugen.
 *
 * @author fstephan
 */
public interface ContentParser {
	
	/** 
	 * Wandelt externe Objekte in interne um 
	 * @throws IllegalContentException
	 */
	Object toInternalContent(Object externalContent) throws IllegalContentException;
	
	/** 
	 * Wandelt interne Objekte in externe um
	 * @throws IllegalContentException
	 */
	Object toExternalContent(Object internalContent) throws IllegalContentException;

}
