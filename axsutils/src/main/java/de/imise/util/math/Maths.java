package de.imise.util.math;

public final class Maths {

    /**
     * Errechnet das Maximum der übergegebenen Werte.
     *
     * @param values
     * @return
     */
    public static int max(final int... values) {
        int max = Integer.MIN_VALUE;
        for (int value : values) {
            max = Math.max(value, max);
        }
        return max;
    }

    /**
     * Errechnet das Minimum der übergegebenen Werte.
     *
     * @param values
     * @return
     */
    public static int min(final int... values) {
        int min = Integer.MAX_VALUE;
        for (int value : values) {
            min = Math.min(value, min);
        }
        return min;
    }

    /**
     * @param value the value that should be checked to be between min and max
     * @param min the min of value
     * @param max the max of value
     * @return the value if it is in the range of min and max. If it is below min
     *         than min is returnes. If it is greater than max so max is returned.
     */
    public static int getValueInMinMax(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
