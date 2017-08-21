/**
 *
 */
package de.imise.util;

/**
 * Ein Range Objekt besitzt eine Position und eine Länge
 *
 * @author AXS
 */
public class IntRange {

    /**
     * Anfangspostion des Ranges
     */
    private int min = 0;

    /**
     * Endpostion des Ranges
     */
    private int max = 0;

    /**
     * Länge des Ranges
     */
    private int length = 0;

    /**
     * @param min
     * @param length
     */
    private IntRange(final int min, final int max) {
        this.min = min;
        this.max = max;
        length = max - min + 1;
    }

    public static IntRange minToLength(final int min, final int length) {
        int max = min + length - 1;
        return minToMax(min, max);
    }

    public static IntRange minToMax(final int min, final int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum greater than Maximum: " + min + ">" + max);
        }
        return new IntRange(min, max);
    }

    /**
     * @return the minimum
     */
    public int min() {
        return min;
    }

    /**
     * @return the maximum
     */
    public int max() {
        return max;
    }

    /**
     * @return the maximum + 1 (= min + length)
     */
    public int maxPlusOne() {
        return max + 1;
    }

    /**
     * @return the length
     */
    public int length() {
        return length;
    }

}
