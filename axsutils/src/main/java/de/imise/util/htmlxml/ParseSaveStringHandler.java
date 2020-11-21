package de.imise.util.htmlxml;

/**
 * Kam aus GraphDocument.
 *
 * @author AXS (12.03.2020)
 */
public class ParseSaveStringHandler {

    /**
     * Zeichen, das in Kommandos zusammengehörigen Text umschließt, damit er als
     * zusammengehörig erkannt werden kann
     */
    public static final char TEXT_SURROUNDER = '\'';

    /**
     * Liefert den übergeben String eingerahmt in einfache Anführungszeichen
     * ('') sowie kodierten Backslashes. Dies ist der Elementname, der in alle
     * Undo-Redo-Kommandos benutzt werden sollte.
     *
     * @param o
     * @param trim
     * @return
     */
    public static String getParseSaveString(final Object o, final boolean trim) {
        if (o == null) {
            StringBuilder sb = new StringBuilder(2);
            sb.append(TEXT_SURROUNDER);
            sb.append(TEXT_SURROUNDER);
            return sb.toString();
        }
        String s = o.toString();
        if (trim) {
            s = s.trim();
        }
        return TEXT_SURROUNDER + s.replace("\r", "").replace('\n', '\u001e').replace(TEXT_SURROUNDER, '´').replaceAll("\\\\", "\\\\\\\\") + TEXT_SURROUNDER;
    }

    /**
     * Liefert den übergeben String eingerahmt in einfache Anführungszeichen
     * ('') sowie kodierten Backslashes. Dies ist der Elementname, der in alle
     * Undo-Redo-Kommandos benutzt werden sollte.
     *
     * @param o
     * @return
     */
    public static String getParseSaveString(final Object o) {
        return getParseSaveString(o, false);
    }

    /**
     * Liefert einen übergebenen String, in dem die Transformationen der
     * Zeilenumbrüche durch die Funktion
     * <code>getParseSaveString(String s)</code> wieder rückgängig gemacht
     * werden.
     *
     * @param o
     * @return
     */
    public static String getDecodedParseSaveString(final Object o) {
        if (o == null) {
            return "";
        }
        String s = o.toString();
        if (s.isEmpty()) {
            return "";
        }
        if (s.charAt(0) == TEXT_SURROUNDER && s.charAt(s.length() - 1) == TEXT_SURROUNDER) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace('\u001e', '\n');
    }

}
