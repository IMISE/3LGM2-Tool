package de.imise.util;

import java.util.ResourceBundle;

/**
 * Erzeugt einen neuen Handler, der die Resourcen für eine übergebene Klasse zurückgeben kann.
 * 
 * @author AXS created on 21.08.2007
 */
public class SimpleResourceHandler {

	/**
	 * ResourceBundles mit den speziellen Ressourcen für eine bestimmte Klasse
	 */
	protected ResourceBundle resourceBundle;

	/**
	 * Erzeugt einen neuen Handler, der die Resourcen für die übergebene Klasse zurückgeben kann.
	 * 
	 * @param ressourceNameClassSource
	 * 		Klassenname, der den Namen der zu ladenden Ressorcendatei vorgibt.
	 */
	public SimpleResourceHandler(Class<?> ressourceNameClassSource) {
		super();
		if (ressourceNameClassSource == null)
			return;
		String resourceFileName = ressourceNameClassSource.getName().replace('.', '/');
		resourceBundle = ResourceBundle.getBundle(resourceFileName);
	}

	/**
	 * Liefert den Resourcen-String zum übergebenen Schlüssel
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return resourceBundle.getString(key);
	}

}
