package de.imise.util.outparam;

/**
 * @author AXS (18.03.2020)
 */
public class OutParamLong {

    public long value;

    public OutParamLong() {
    }

    public OutParamLong(final long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
