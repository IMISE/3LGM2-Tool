package de.imise.util.outparam;

/**
 * @author AXS (18.03.2020)
 */
public class OutParamInt {

    public int value;

    public OutParamInt() {
    }

    public OutParamInt(final int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
