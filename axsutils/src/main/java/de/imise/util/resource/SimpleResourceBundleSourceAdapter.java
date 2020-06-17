package de.imise.util.resource;

import java.util.Locale;
import java.util.ResourceBundle;

import de.imise.util.Sys;

/**
 * Erzeugt einen neuen Handler, der die Resourcen für eine übergebene Klasse zurückgeben kann.
 *
 * @author AXS created on 21.08.2007
 */
public class SimpleResourceBundleSourceAdapter implements SimpleResourceBundleSource {

    /**
     * ResourceBundles mit den speziellen Ressourcen für eine bestimmte Klasse
     */
    private final ResourceBundle resourceBundle;

    /**
     * Erzeugt einen neuen Handler, der die Resourcen für die übergebene Klasse zurückgeben kann.
     *
     * @param ressourceNameClassSource
     *            Klassenname, der den Namen der zu ladenden Ressorcendatei vorgibt. Außerdem wird von dieser Klasse der ClassLoader genutzt,
     *            um das ResourceBundle zu laden.
     */
    public SimpleResourceBundleSourceAdapter() {
        this(null, null);
    }

    /**
     * Erzeugt einen neuen Handler, der die Resourcen für die übergebene Klasse zurückgeben kann.
     *
     * @param ressourceNameClassSource
     *            Klassenname, der den Namen der zu ladenden Ressorcendatei vorgibt. Außerdem wird von dieser Klasse der ClassLoader genutzt,
     *            um das ResourceBundle zu laden.
     */
    public SimpleResourceBundleSourceAdapter(final Class<?> ressourceNameClassSource) {
        this(ressourceNameClassSource, null);
    }

    /**
     * Erzeugt einen neuen Handler, der die Resourcen für die übergebene Klasse zurückgeben kann.
     *
     * @param ressourcePackageNameSource
     *            Klassen, deren Package das Package des zu ladenden ResouceBundles vorgibt. Außerdem wird von dieser Klasse der ClassLoader genutzt,
     *            um das ResourceBundle zu laden.
     * @param resourceBundleSimpleName
     *            SimpleName des ResouceBundles. Dieser wird dan den Package-Namen des obigen Packages angehängt. Ist dieser SimpleName
     *            <code>null</code>, dann wird der GesamtName aus <code>ressourcePackageNameSource</code> gebildet - also nicht nur das Package
     *            genommen, sondern auch der SimpleName der Klasse als Package-Name.
     */
    public SimpleResourceBundleSourceAdapter(final Class<?> ressourcePackageNameSource, final String resourceBundleSimpleName) {
        this(ressourcePackageNameSource, resourceBundleSimpleName, Locale.getDefault());
    }

    /**
     * Erzeugt einen neuen Handler, der die Resourcen für die übergebene Klasse zurückgeben kann
     *
     * @param ressourcePackageNameSource
     *            Klassen, deren Package das Package des zu ladenden ResouceBundles vorgibt. Außerdem wird von dieser Klasse der ClassLoader genutzt,
     *            um das ResourceBundle zu laden.
     * @param resourceBundleSimpleName
     *            SimpleName des ResouceBundles. Dieser wird dan den Package-Namen des obigen Packages angehängt. Ist dieser SimpleName
     *            <code>null</code>, dann wird der GesamtName aus <code>ressourcePackageNameSource</code> gebildet - also nicht nur das Package
     *            genommen, sondern auch der SimpleName der Klasse als Package-Name.
     * @param locale
     *            Locale des ResourceBundles
     */
    public SimpleResourceBundleSourceAdapter(final Class<?> ressourcePackageNameSource, final String resourceBundleSimpleName, final Locale locale) {
        resourceBundle = loadResourceBundle(ressourcePackageNameSource, resourceBundleSimpleName, locale);
    }

    /**
     * Initialisiertdas ResourceBundle, wenn es sich laden lässt. Wenn nicht, dann bleibt dieses <code>null</code>.
     *
     * @param ressourcePackageNameSource
     * @param resourceBundleSimpleName
     * @param locale
     * @return
     */
    private ResourceBundle loadResourceBundle(final Class<?> ressourcePackageNameSource, final String resourceBundleSimpleName, final Locale locale) {
        String resourceFileName = getResourceFileName(ressourcePackageNameSource, resourceBundleSimpleName);
        try {
            return ResourceBundle.getBundle(resourceFileName, locale, ressourcePackageNameSource.getClassLoader());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Liefert den Resourcen-String zum übergebenen Schlüssel
     *
     * @param key
     * @return
     */
    @Override
    public String getResString(final String key) {
        return resourceBundle.getString(key);
    }

    /**
     * Liefert <code>true</code>, wenn ein gültiges ResourceBundle geladen werden konnte.
     *
     * @return
     */
    public boolean hasValidResourceBundle() {
        return resourceBundle != null;
    }

    /**
     *
     */
    public void printBundle() {
        for (String key : resourceBundle.keySet()) {
            System.out.println(key + " -> " + resourceBundle.getString(key));
        }
        Sys.out1(resourceBundle);
    }

}
