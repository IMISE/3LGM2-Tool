package de.imise.util.outparam;

/**
 * @author AXS (18.03.2020)
 */
public class OutParamFloat {

    public float value;

    public OutParamFloat() {
    }

    public OutParamFloat(final float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
