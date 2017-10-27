package de.imise.util;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class StringUtils {

    /**
     * Gibt ein Array zurück, dass durch Aufteilen des übergebenen Strings entsteht.
     *
     * @see StringTokenizer
     * @param str
     *            aufzuteilender String
     * @param delim
     *            Zeichenkette, an der gesplittet wird
     * @param returnDelims
     *            delim wird zurückgegeben oder nicht
     */
    public static String[] tokenize(final String str, final String delim, final boolean returnDelims) {
        StringTokenizer st = new StringTokenizer(str, delim, returnDelims);
        int n = st.countTokens();
        String[] tokens = new String[n];
        for (int i = 0; i < n; i++) {
            tokens[i] = st.nextToken();
        }
        return tokens;
    }

    /**
     * Gibt einen String zurück, der von jedem Wort in dem übergebenen String jeweils den ersten Buchstaben enthält
     *
     * @param str
     *            String, von dessen Wörtern die Anfangsbuchstaben separiert werden sollen
     */
    public static String getFirstChars(final String str) {
        StringTokenizer st = new StringTokenizer(str);
        StringBuilder sb = new StringBuilder(st.countTokens());
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            sb.append(token.charAt(0));
        }
        return sb.toString();
    }

    /**
     * Führt {@link String#trim()} für jeden Eintrag in <code>source</code> aus und gibt das resultierende <code>Array</code> zurück;
     *
     * @param source
     *            Quell-<code>Array</code>
     */
    public static String[] trimAll(final String[] source) {
        int n = source.length;
        String[] result = new String[n];
        for (int j = 0; j < n; j++) {
            result[j] = source[j].trim();
        }
        return result;
    }

    /**
     * Führt {@link String#trim()} für jeden Eintrag in <code>source</code> aus und gibt das resultierende <code>Array</code> zurück;
     *
     * @param source
     *            Quell-<code>Array</code>
     */
    public static String[][] trimAll(final String[][] source) {
        int m = source.length;
        int n = source[0].length;
        String[][] result = new String[m][n];
        result[0] = new String[3];
        for (int i = 0; i < m; i++) {
            result[i] = trimAll(source[i]);
        }
        return result;
    }

    public static Pair<String, String> makeSameLength(final String s1, final String s2) {
        String newS1 = fillToLenght(s1, s2.length());
        String newS2 = fillToLenght(s2, s1.length());
        Pair<String, String> pair = new Pair<>(newS1, newS2);
        return pair;
    }

    public static String fillToLenght(final String s, final int newLenght) {
        int lengthDiff = newLenght - s.length();
        if (lengthDiff > 0) {
            StringBuilder sb = new StringBuilder(s);
            char[] whiteSpaces = new char[lengthDiff];
            Arrays.fill(whiteSpaces, ' ');
            sb.append(whiteSpaces);
            return sb.toString();
        }
        return s;
    }

    /**
     * Liefert einen Bereich, an dem sich in dem übergebenen Text <code>text</code> der zu suchende String <code>searchString</code> befindet.
     *
     * @param text
     *            Zu durchsuchender Text
     * @param text2Find
     *            Zu findender Text
     * @param fromIndex
     *            Startindex in <code>text</code>, ab dem gesucht wird
     * @param ignoreCase
     *            Wenn <code>true</code> wird die Groß- und Kleinschreibung ignoriert.
     * @param searchForward
     *            Wenn <code>true</code> wird ab dem Startindex in Vorwärtsrichtung gesucht, wenn <code>false</code> rückwärts.
     * @param text2FindAsRegExp
     *            Wenn <code>true</code> wird der <code>text2Find</code> als regulärer Ausdruck interpretiert, bei <code>false</code> wird er immer im
     *            Original gesucht.
     * @param wholeWord
     *            Wenn <code>true</code> muss der zu suchende String als ganzes Wort gefunden werden. Das ist der Fall wenn der gefundene Textteil von
     *            Whitespaces, Zeilenumbrüchen oder dem
     *            Stringanfang oder - ende eingerahmt ist.
     * @param wrapSearch
     *            Wenn <code>false</code> endet die Suche (je nach Suchrichtung) am Anfang oder Ende des zu durchsuchenden Strings. Bei
     *            <code>true</code> wird die Suche (je nach Suchrichtung) dann
     *            wieder am Ende bzw. Anfang forgesetzt.
     * @return Position und Länge des gefundenen Suchstrings im zu duerchsuchenden Text oder <code>null</code>, wenn der Suchstring nicht gefunden
     *         wurde. Die zurück gegebene Länge kann sich von der
     *         Länge des <code>text2Find</code> unterscheiden, wenn mit regulären Ausdrücken gesucht wird.
     */
    public static final IntRange find(String text, String text2Find, final int fromIndex, final boolean ignoreCase, final boolean searchForward, final boolean text2FindAsRegExp, final boolean wholeWord, final boolean wrapSearch) {
        // eingeführt, um wholeWord Suche mit zu integrieren;
        boolean foundNoWholeWord = false;
        // eingeführt, um bei regulären Ausdrücken auch wrappen zu können
        boolean wrappedInMode_regExpr = false;
        // Groß- und Kleinschreibung ignorieren? -> Wenn ja, alles in Bleinbuchstaben umwandeln
        if (ignoreCase) {
            text = text.toLowerCase();
            text2Find = text2Find.toLowerCase();
        }
        // falls text2Find tatsächlich ein regulärer Ausdruck ist und auch über reguläre Ausdrücke gesucht wird, ist das hier das kompilierte Pattern aus text2Find
        Pattern regExpPattern = null;
        // Suche nach RegExp -> Pattern compilieren
        if (text2FindAsRegExp) {
            regExpPattern = Pattern.compile(text2Find);
        }
        // Rückgabewert, wenn der Suchtext gefunden wurde
        IntRange range = null;
        int startIndex = -2;
        int endIndex = -1;
        // 2 Durchläufe: je nach Richtung in einem Druchlauf den Teil vom Index 0 bis Index fromindex
        // und im zweiten Durchlauf immer den ganzen String durchsuchen durchsuchen
        outerloop: for (int i = 0;; i++) {
            // im 1. Durchlauf
            if (i == 0) {
                // Vorwärtsuche
                if (searchForward) {
                    // von fromIndex bis zum Ende
                    startIndex = fromIndex;
                    endIndex = text.length();
                    // Rückwärtssuche
                } else {
                    // vom Anfang bis fromIndex
                    startIndex = 0;
                    // Mehrmaliges Rückwärtssuchen
                    // anpassung - 1 , weil man sonst bei lastIndexOf hängen bleibt bei mehrmaligen rückwärts suchen
                    // text.lenght -1 bei 0, sonst kann man anfang nicht rückwärts suchen
                    endIndex = fromIndex > 0 ? fromIndex - 1 : text.length() - 1;
                }
                // im 2. Durchlauf;
                // oder im nten Durchlauf, bei wholeWord
            } else {
                // wenn schon der ganze String durchsucht wurde oder die Suche nicht von
                // vorne neu begonnen werden soll und im ersten Durchlauf nichts gefunden wurde -> raus
                // if (!wrapSearch || (searchForward && fromIndex == 0) || (!searchForward && fromIndex == text.length()))
                // obere Zeile erweitert, weil sonst hier immer immer bei !wrapSearch rausgesprungen wird...+ bei umgekehrter suche, wird endindex deckrementiert
                if (!wrapSearch && !wholeWord || searchForward && fromIndex == 0 || !searchForward && fromIndex == text.length() && !foundNoWholeWord) {
                    break;
                    // außerdem rausspringen, wenn endIndex<=startIndex (entsteht bei wholeWord)
                }
                if (endIndex <= startIndex) {
                    // SK:
                    if (wrapSearch) {
                        startIndex = 0;
                        endIndex = text.length();
                    } else {
                        // bei raussprung letztes range ungültig
                        range = null;
                        break;
                    }
                }
                // wenn die Suche wieder von vorne bzw. hinten beginnen soll
                // immer vom Anfang bis zum Ende suchen!!! Damit der Suchstring in jedem Fall gefunden wird
                // nur im 2. Durchlauf wirklich den ganzen String durchsuchen. Alle Durchlaüfe danach kommen nur noch zu Stande,
                // wenn man nach wholeWord sucht. Dann dürfen die Indizes nicht angepasst werden.
                // wenn im Durchlauf kein wholeWord (false positive im Modus whole word) gefunden wurde, darf man hier nicht zurücksetzen (sonst endlosschleife)
                if (i == 1 && !foundNoWholeWord) {
                    startIndex = 0;
                    endIndex = text.length();
                }
            }
            // Jetzt kann die eigentliche Suche starten
            // normale Stringsuche
            if (!text2FindAsRegExp) {
                int foundIndex = searchForward ? text.indexOf(text2Find, startIndex) : text.lastIndexOf(text2Find, endIndex);
                if (foundIndex >= 0) {
                    range = IntRange.minToLength(foundIndex, text2Find.length());
                    // suche nach regulärem Ausdruck
                }
            } else {
                // reguläre Ausdrücke kann man nur vorwärts durchsuchen
                // zu durchsuchenden Substring holen
                // String textPart = startIndex > 0 || endIndex < text.length() ? text.substring(startIndex, endIndex) : text;
                String textPart = endIndex < text.length() ? text.substring(startIndex, endIndex) : text;
                Matcher m = regExpPattern.matcher(textPart);
                // wenn vorwärts gesucht werden soll, dann fängt er bei Index 0 an und sucht nur 1 mal. Rückwärt fängt er am Ende an
                // und sucht nach vorne durch
                for (int start = searchForward ? startIndex : textPart.length() - 1; start >= 0; start--) {
                    if (!m.find(start)) {
                        // bleibt sonst hängen am letzten Wort bei Wrap
                        // ->
                        // - bei Vorwärtssuche wenn nichts gefunden wird im ersten Lauf, muss startIndex auf 0 gesetzt werden.
                        // - gleichzeitig wird sich gemerkt, dass gewrapped wurde mittels wrappedInMode_regExpr, damit nicht unendlich wieder auf 0 gesetzt wird...
                        if (searchForward && wrapSearch && !wrappedInMode_regExpr) {
                            startIndex = 0;
                            wrappedInMode_regExpr = true;
                            range = null;
                            continue outerloop;
                        }
                    } else {
                        range = IntRange.minToLength(m.start(), m.end() - m.start());
                        break;
                    }
                }
            }
            // nichts gefunden?
            if (range == null) {
                // wenn nichts gefunden in 1ter Schleife, weiter machen
                if (i == 0 && wrapSearch) {
                    continue;
                    // wenn nichts gefunden nach 1ter Schleife, aufhören
                } else {
                    break;
                }
            }
            //TODO:AXS:20121122: das hier hinter ist dead Code! oben ist break oder continue. auch oben das SurpressWarnings heraus nehmen und Testen
            // Hinweise zu wholeword:
            // ->
            // - wenn der Kandidat korrekt ist, soll der Block durchlaufen und unten break bei (range != null)
            // - wenn es ein false positive ist, muss der Bereich eingeschräankt werden (abhäangig von vorwärts rüuckwärts) und es muss wieder hochgesprungen werden
            // - dann darf oben nicht mehr der Bereich auf 0 zurckgesetzt werden Z164
            // - um sich das zu merken => foundNoWholeWord => es wurde kein ganzes wort gefunden (aber false positive)
            // String gefunden, aber suche eigentlich nach ganzem Wort?
            if (wholeWord) {
                // Wenn ganzes Wort gefunden -> soll hier übersprungen werden
                // Wenn ganzes Wort dann muss angepasst und zurückgesprungen werden
                // man beachte die 2 Sonderfälle, ganz am Anfang, ganz am Ende
                // Pos vor dem Wort; 0 beim Anfang
                int beforePos = Math.max(0, range.min() - 1);
                // Pos nach dem Wort; len bei Ende
                int afterPos = Math.min(text.length(), range.min() + range.length());
                // Zeichenersetzung wenn am Anfang
                char charBefore = beforePos == 0 ? ' ' : text.charAt(beforePos);
                // Zeichenersetzung wenn am Ende
                char charAfter = afterPos == text.length() ? ' ' : text.charAt(afterPos);
                // Wenn nicht von Leerzeichen umgeben -> wieder hochspringen
                if (!(charBefore < 33 && charAfter < 33)) {
                    // Wenn kein ganzes Wort -> erhöhe startZeiger bei Vorwärtssuche
                    if (searchForward) {
                        startIndex++;
                        // Wenn kein ganzes Wort -> verringere endZeiger bei Rückwärtssuche
                    } else {
                        endIndex--;
                    }
                    foundNoWholeWord = true;
                    continue;
                }
            }
            // Ergebnis wurde gefunden
            if (range != null) {
                break;
            }
        }
        return range;
    }

    public static final String capitalizeFirstChar(final String s) {
        if (Strings.isNullOrEmpty(s)) {
            return s;
        }
        char firstChar = s.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(firstChar));
        if (s.length() > 1) {
            sb.append(s.substring(1));
        }
        return sb.toString();
    }

    /**
     * Liefert <code>true</code>, wenn der übergebene String <code>null</code> ist, leer ist oder nur aus WhiteSpaces besteht.
     *
     * @param s
     * @return
     */
    public static final boolean isNullOrEmptyOrBlank(final String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Liefert <code>false</code>, wenn der übergebene String <code>null</code> ist, leer ist, nur aus WhiteSpaces besteht oder
     * gleich einem der übergebenen invalidValues ist, sonst <code>true</code>.
     *
     * @param s
     * @param invalidValues
     * @return
     */
    public static final boolean isValid(final String s, final String... invalidValues) {
        if (isNullOrEmptyOrBlank(s)) {
            return false;
        }
        for (String invalid : invalidValues) {
            if (s.equals(invalid)) {
                return false;
            }
        }
        return true;
    }
}
