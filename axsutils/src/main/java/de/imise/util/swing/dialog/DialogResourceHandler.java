package de.imise.util.swing.dialog;

import java.util.Locale;
import java.util.ResourceBundle;

import de.imise.util.resource.SimpleResourceBundleHandler;

/**
 * Stellt für Dilaoge die Resourcen nach folgende Regeln bereit:
 * Zuererst wird nach einer Resourcendatei gesucht, die dem Klassennamen entspricht,
 * dann nach der allgemeinen Ressourcendatei mit dem <code>baseName</code>
 * CommonDialogResources. In dieser Reihenfolge wird auch nach jedem der Resourcenwerte
 * gesucht.
 *
 * @author AXS
 *         created on 21.08.2007
 */
public class DialogResourceHandler extends SimpleResourceBundleHandler {

    /**
     * BaseName des ResourceBundles mit den für alle Dialoge gleichen Ressourcen
     */
    private static final String BASE_NAME = DialogResourceHandler.class.getPackage().getName().replace('.', '/') + "/" + "CommonDialogResources";
    /**
     * ResourceBundles mit den für alle Dialoge gleichen Ressourcen
     */
    private static final ResourceBundle COMMON_RESOURCE_BUNDLE = ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH);

    /**
     * Erzeugt einen neuen Handler, der die Resourcen für die übergebene Klasse zurückgeben kann.
     *
     * @param ressourceNameClassSource
     *            Klassenname, der den Namen der zu ladenden Ressorcendatei vorgibt.
     */
    public DialogResourceHandler(final Class<?> ressourceNameClassSource) {
        super(ressourceNameClassSource);
    }

    /**
     * Liefert den Resourcen-String zum übergebenen Schlüssel. Zuerst wird im speziellen RessoruceBundle mit dem
     * Klassennamen gesucht und dann in den allgemeinen Ressourcen.
     *
     * @param key
     * @return
     */
    @Override
    public String getResString(final String key) {
        try {
            return super.getResString(key);
        } catch (Exception e) {
            return getCommonString(key);
        }
    }

    /**
     * Liefert den Resourcen-String zum übergebenen Schlüssel aus dem gemeinsamen ResourceBundle zurück
     *
     * @param key
     * @return
     */
    public static String getCommonString(final String key) {
        return COMMON_RESOURCE_BUNDLE.getString(key);
    }

}
