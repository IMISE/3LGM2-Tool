package de.imise.util;

import java.util.Locale;

/**
 * @author AXS created on 22.08.2007
 */
public class Locales {

    /**
     * Liefert eine System-<code>Locale</code>, die der Sprache des übergebenen
     * String entspricht.<br>
     * Wird ein 5-Zeichen locale-String übergeben, werden die ersten beiden
     * Zeichen als Sprache angenommen. Bei einem 2-Zeichen-String wird diese
     * direkt als Sprache angenommen. Bei Strings anderer Länge kommt
     * <code>null</code> zurück, genauso wenn keine passende Locale gefunden
     * wurde.
     * 
     * @param localeString
     * @return
     */
    public static final Locale getSystemLanguageLocale(String localeString) {
        //die Sprache extrahieren
        String s = null;
        if (localeString.length() == 5)
            s = localeString.substring(0, 2).toLowerCase();
        else if (localeString.length() == 2)
            s = localeString;
        else
            return null;
        //alle Locales des Systems holen
        Locale[] availLocales = Locale.getAvailableLocales();
        //die passende Locale für die Sprache heruassuchen
        for (int i = 0; i < availLocales.length; i++)
            if (availLocales[i].toString().equals(s))
                return availLocales[i];
        return null;
    }

}
