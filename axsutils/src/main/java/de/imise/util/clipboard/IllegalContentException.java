package de.imise.util.clipboard;

/**
 * Exception, die geworfen wird, wenn ein {@link ContentParser} interne bzw. externe Inhalte 
 * nicht umwandeln kann.
 * 
 * @author fstephan
 */
public class IllegalContentException extends Exception {
	
	/**
	 * Konstruktor
	 *
	 * @param content
	 * 			Inhalt, der nicht umgewandelt werden kann
	 */
	public IllegalContentException(Object content) {
		super(content + " besitzt kein gültiges Format");
	}
	
	/**
	 * Konstruktor
	 * 
	 * @param content
	 * 			Inhalt, der nicht umgewandelt werden kann
	 * @param t
	 * 			Auslösende Exception
	 */
	public IllegalContentException(Object content, Throwable t) {
		super(content + " besitzt kein gültiges Format\n" + t.getMessage());
		
	}

}
