package de.imise.util;

import java.text.DateFormat;
import java.util.Date;

public class HashStringGenerator {

    /**
     * Anzahl bereits generierter Hashes. Static, damit garantiert niemals dieselbe Nummer
     * angehängt wird, auch wenn man ständig einen neuen HashStringGenerator anlegt.
     */
    private static int counter = 1;

    /** Trenner für die einzelnen Sektionen des HashStrings */
    public static final String HASH_STRING_DELIMITER = "_";

    public static String getHash(final String prefix) {
        return prefix + HASH_STRING_DELIMITER + System.currentTimeMillis() + HASH_STRING_DELIMITER + counter++;
    }

    /**
     * Berechnet aus dem HashString das Datum, an dem das Element erstellt wurde. Lässt sich das Datum aus irgendwelchen Gründen nicht berechnen kommt
     * new STANDARD_CREATION_DATE = new Date(0) zurück.
     *
     * @return
     */
    public static Date getCreationDate(final String hashString) {
        long l = 0;
        try {
            l = Long.parseLong(hashString.substring(hashString.indexOf(HASH_STRING_DELIMITER) + 1, hashString.lastIndexOf(HASH_STRING_DELIMITER)));
        } catch (Exception e) {
            // 01.01.1970 als Fallback-Date
        }
        return new Date(l);
    }

    public static String getCreationTimeShort(final String hashString) {
        return getCreationTime(DateFormat.SHORT, hashString);
    }

    public static String getCreationTimeMedium(final String hashString) {
        return getCreationTime(DateFormat.MEDIUM, hashString);
    }

    public static String getCreationTimeLong(final String hashString) {
        return getCreationTime(DateFormat.LONG, hashString);
    }

    public static String getCreationTimeFull(final String hashString) {
        return getCreationTime(DateFormat.FULL, hashString);
    }

    private static String getCreationTime(final int style, final String hashString) {
        Date creationDate = getCreationDate(hashString);
        return DateFormat.getDateTimeInstance(style, style).format(creationDate);
    }

}
