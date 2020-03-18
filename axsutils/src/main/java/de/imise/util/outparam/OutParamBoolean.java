package de.imise.util.outparam;

/**
 * @author AXS (18.03.2020)
 */
public class OutParamBoolean {

    public boolean value;

    public OutParamBoolean() {
    }

    public OutParamBoolean(final boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
