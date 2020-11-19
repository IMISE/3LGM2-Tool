package de.imise.util;

import java.text.DateFormat;
import java.util.Date;

public class IDStringGenerator {

    /**
     * Anzahl bereits generierter IDs. Static, damit garantiert niemals dieselbe
     * Nummer angehängt wird, auch wenn man ständig einen neuen
     * IDStringGenerator anlegt.
     */
    private static int counter = 1;

    /** Trenner für die einzelnen Sektionen des IDStrings */
    public static final String ID_STRING_DELIMITER = "_";

    /**
     * @param prefix
     * @return
     */
    public static String createIDString(final String prefix) {
        return prefix + ID_STRING_DELIMITER + System.currentTimeMillis() + ID_STRING_DELIMITER + counter++;
    }

    /**
     * Berechnet aus dem idString das Datum, an dem das Element erstellt wurde.
     * Lässt sich das Datum aus irgendwelchen Gründen nicht berechnen kommt new
     * STANDARD_CREATION_DATE = new Date(0) zurück.
     *
     * @return
     */
    public static Date getCreationDate(final String idString) {
        long l = 0;
        try {
            l = Long.parseLong(idString.substring(idString.indexOf(ID_STRING_DELIMITER) + 1, idString.lastIndexOf(ID_STRING_DELIMITER)));
        } catch (Exception e) {
            // 01.01.1970 als Fallback-Date
        }
        return new Date(l);
    }

    /**
     * @param idString
     * @return
     */
    public static String getCreationTimeShort(final String idString) {
        return getCreationTime(DateFormat.SHORT, idString);
    }

    /**
     * @param idString
     * @return
     */
    public static String getCreationTimeMedium(final String idString) {
        return getCreationTime(DateFormat.MEDIUM, idString);
    }

    /**
     * @param idString
     * @return
     */
    public static String getCreationTimeLong(final String idString) {
        return getCreationTime(DateFormat.LONG, idString);
    }

    /**
     * @param idString
     * @return
     */
    public static String getCreationTimeFull(final String idString) {
        return getCreationTime(DateFormat.FULL, idString);
    }

    /**
     * @param style
     * @param idString
     * @return
     */
    private static String getCreationTime(final int style, final String idString) {
        Date creationDate = getCreationDate(idString);
        return DateFormat.getDateTimeInstance(style, style).format(creationDate);
    }

}
