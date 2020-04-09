package de.imise.util.outparam;

/**
 * @author AXS (18.03.2020)
 */
public class OutParamObject<T> {

    public T value;

    public OutParamObject() {
    }

    public OutParamObject(final T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
