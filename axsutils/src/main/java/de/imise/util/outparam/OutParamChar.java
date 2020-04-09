package de.imise.util.outparam;

/**
 * @author AXS (18.03.2020)
 */
public class OutParamChar {

    public char value;

    public OutParamChar() {
    }

    public OutParamChar(final char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
