package de.imise.util.resource;

import java.util.MissingResourceException;

/**
 * Ein Interface, das genutzt werden kann, um zu markieren, dass die
 * implementierende Klasse einen ResourceString zurück liefern kann.
 *
 * @author AXS (3 Aug 2019)
 */
public interface SimpleResourceBundleSource extends SimpleResourceFileLoader {

    /**
     * Liefert für einen übergebenen Resourcen-KeyString einen String aus den Resourcen.
     *
     * @param resKey
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
    public default String getResStringWithoutError(final Object resKey) {
        try {
            String realResKey = getResKey(resKey);
            return getResString(realResKey);
        } catch (Exception e) {
            return String.valueOf(resKey);
        }
    }

    /**
     * @param object
     * @return
     */
    public static String getResKey(final Object object) {
        if (object instanceof Enum<?>) {
            return ((Enum<?>) object).name();
        }
        return String.valueOf(object);
    }

}
