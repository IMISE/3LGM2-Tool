package de.imise.util.outparam;

/**
 * @author AXS (18.03.2020)
 */
public class OutParamDouble {

    public double value;

    public OutParamDouble() {
    }

    public OutParamDouble(final double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
