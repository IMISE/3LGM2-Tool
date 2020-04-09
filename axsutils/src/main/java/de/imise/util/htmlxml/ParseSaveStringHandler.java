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
     * Liefert den übergeben String eingerahmt in einfache Anführungszeichen ('') sowie
     * kodierten Backslashes. Dies ist der Elementname, der in alle Undo-Redo-Kommandos
     * benutzt werden sollte.
     *
     * @param s
     * @return
     */
    public static String getParseSaveString(final String s, final boolean trim) {
        if (s == null) {
            StringBuilder sb = new StringBuilder(2);
            sb.append(TEXT_SURROUNDER);
            sb.append(TEXT_SURROUNDER);
            return sb.toString();
        }
        String ss = trim ? s.trim() : s;
        return TEXT_SURROUNDER + ss.replace("\r", "").replace('\n', '\u001e').replace(TEXT_SURROUNDER, '´').replaceAll("\\\\", "\\\\\\\\") + TEXT_SURROUNDER;
    }

    /**
     * Liefert den übergeben String eingerahmt in einfache Anführungszeichen ('') sowie
     * kodierten Backslashes. Dies ist der Elementname, der in alle Undo-Redo-Kommandos
     * benutzt werden sollte.
     *
     * @param s
     * @return
     */
    public static String getParseSaveString(final String s) {
        return getParseSaveString(s, false);
    }

    /**
     * Liefert einen übergebenen String, in dem die Transformationen der Zeilenumbrüche durch
     * die Funktion <code>getParseSaveString(String s)</code> wieder rückgängig gemacht werden.
     *
     * @param s
     * @return
     */
    public static String getDecodedParseSaveString(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (s.charAt(0) == TEXT_SURROUNDER && s.charAt(s.length() - 1) == TEXT_SURROUNDER) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace('\u001e', '\n');
    }

}
