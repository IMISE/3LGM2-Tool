package de.imise.util;

import java.awt.Color;

import com.google.common.base.Strings;

/**
 * @author N.N., AXS (created on 16.08.2007), AXS (updated 25.07.2017)
 */
public class HTMLConverter {

    /**
     * Keine Instanzen zulassen.
     */
    private HTMLConverter() {
    }

    private static class CharEncoder {
        String prefix, suffix;
        int radix;

        public CharEncoder(final String prefix, final String suffix, final int radix) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.radix = radix;
        }

        void encode(final char ch, final StringBuilder sb) {
            sb.append(prefix).append(Integer.toString(ch, radix)).append(suffix);
        }
    }

    static final CharEncoder hexUrlEncoder = new CharEncoder("%", "", 16);
    static final CharEncoder hexHtmlEncoder = new CharEncoder("&#x", ";", 16);
    static final CharEncoder decimalHtmlEncoder = new CharEncoder("&#", ";", 10);

    /**
     * Liefert <code>true</code>, wenn ein übergebenes Zeichen in HTML kodiert werden soll.
     *
     * @param ch
     * @return
     */
    private static boolean encode(final char ch) {
        //Character.isLetter sagt je nach Locale z.B. im deutschen auch bei Ä true -> nur die englischen Buchstaben durchlassen
        // check if ch is a letter
        if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
            return false;
        }
        // check if ch is a digit
        if (ch >= '0' && ch <= '9') {
            return false;
        }
        // check if ch is a whitespace (Chracter.isWhiteSpace() sagt auch bei '\n' true, was hier nich richtig wäre
        if (ch == ' ' || ch == '\t') {
            return false;
        }
        return true;
    }

    /**
     * Wandelt Sonderzeichen im übergebenen String HTML-konform um und hängt den Ergebnis-String
     * an den übergebenen StringBuilder an.
     *
     * @param sb
     * @param s
     * @return
     */
    public static final StringBuilder appendDecimalEncodedHTMLString(final StringBuilder sb, final String s) {
        if (!Strings.isNullOrEmpty(s)) {
            int l = s.length();
            for (int c = 0; c < l; c++) {
                char ch = s.charAt(c);
                switch (ch) {
                case '<':
                    sb.append("&lt;");
                    continue;
                case '>':
                    sb.append("&gt;");
                    continue;
                case '&':
                    sb.append("&amp;");
                    continue;
                case '\"':
                    sb.append("&quot;");
                    continue;
                case '\'':
                    sb.append("&apos;");
                    continue;
                case '\\':
                    int nextC = c + 1;
                    if (nextC < l) {
                        char nextCh = s.charAt(nextC);
                        if (nextCh == '-') {
                            c = nextC;
                            sb.append("-<BR>");
                            continue;
                        }
                    }
                    //wenn das einfach ein Backslash ohne Minus dahinter war -> hänge den Escaped-Backslash an
                    sb.append('\\');
                    continue;
                case '\n':
                    sb.append("<BR>");
                    continue;
                default:
                    if (!encode(ch)) {
                        sb.append(ch);
                    } else {
                        decimalHtmlEncoder.encode(ch, sb);
                    }
                    continue;
                }
            }
        }
        return sb;
    }

    /**
     * Wandelt Sonderzeichen im übergebenen String HTML-konform um und gibt den String zurück.
     *
     * @param s
     * @return
     */
    public static final String getDecimalEncodedHTMLString(final String s) {
        StringBuilder sb = new StringBuilder();
        appendDecimalEncodedHTMLString(sb, s);
        return sb.toString();
    }

    /**
     * Hängt den HTML-Farbcode der uebergebenen Farbe in der Form "#rrggbb" an den übergebenen StringBuilder an.
     *
     * @param col
     * @return
     */
    public static final void appendHTMLColor(final StringBuilder sb, final Color col) {
        int rgb = col.getRGB();
        //16 = Red
        //8 = Green
        //0 = Blue
        for (int offset = 16; offset >= 0; offset -= 8) {
            //siehe Color getRed(), getGreen(), getBlue()
            int val = rgb >> offset & 0xFF;
            for (int r = 0; r < 2; r++) {
                int i = r == 0 ? val / 16 : val % 16;
                if (i < 10) {
                    sb.append(i);
                } else {
                    sb.append((char) (55 + i));
                }
            }
        }
    }

}
