package de.imise.tool3lgm.graphtools.metamodel;

public class EdgeCardinality {

    /**
     * Unendlich als maximaler Integer
     */
    public static final Integer UNLIMITED = Integer.MAX_VALUE;

    /**
     * Null als Integer
     */
    public static final int ZERO = 0;

    /**
     * Eins als Integer
     */
    public static final int ONE = 1;

    public static final EdgeCardinality ZERO_ONE = new EdgeCardinality(ZERO, ONE);

    public static final EdgeCardinality ZERO_UNIMITED = new EdgeCardinality(ZERO, UNLIMITED);

    public static final EdgeCardinality ONE_ONE = new EdgeCardinality(ONE, ONE);

    public static final EdgeCardinality ONE_UNIMITED = new EdgeCardinality(ONE, UNLIMITED);

    private final int min;

    private final int max;

    private EdgeCardinality(final int min, final int max) {
        if (min > max) {
            throw new Error();
        }
        this.min = min;
        this.max = max;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

}
