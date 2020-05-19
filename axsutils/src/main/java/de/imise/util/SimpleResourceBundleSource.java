package de.imise.util;

import java.util.MissingResourceException;

import de.imise.util.swing.SimpleResourceSource;

/**
 * Ein Interface das in Verbindung mit dem {@link SimpleResourceBundleHandler} genutzt werden kann, um zu markieren, dass die implementierende Klasse
 * einen
 * ResourceString zurück liefern kann.
 *
 * @author AXS (3 Aug 2019)
 */
public interface SimpleResourceBundleSource extends SimpleResourceSource {

    /**
     * Liefert für einen übergebenen Resourcen-KeyString einen String aus den Resourcen.
     *
     * @param resKex
     * @return
     */
    public String getResString(String resKey);

    /**
     * Liefert für einen übergebenen Resourcen-KeyString einen String aus den Resourcen. Wird der Key nicht in den Resourcen gefunden, kommt ohne
     * {@link MissingResourceException} der Key zurück.
     *
     * @param resKey
     * @return
     */
    public default String getResStringWithoutError(final String resKey) {
        try {
            return getResString(resKey);
        } catch (Exception e) {
            return resKey;
        }
    }

}
