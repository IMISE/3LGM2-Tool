/*
 * Created on 23.04.2004
 */
package de.imise.util.pair;

/**
 * @author AXS
 */
public class Pair<T, S> {

    /**
     *
     */
    protected T firstObject;

    /**
     *
     */
    protected S secondObject;

    /**
     * @param o
     */
    public void setFirstItem(final T o) {
        firstObject = o;
    }

    /**
     * @param o
     */
    public void setSecondItem(final S o) {
        secondObject = o;
    }

    /**
     * @param o1
     * @param o2
     * @param i
     */
    public Pair(final T o1, final S o2) {
        firstObject = o1;
        secondObject = o2;
    }

    /**
     * @return
     */
    public T getFirstItem() {
        return firstObject;
    }

    /**
     * @return
     */
    public S getSecondItem() {
        return secondObject;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (firstObject == null ? 0 : firstObject.hashCode());
        result = prime * result + (secondObject == null ? 0 : secondObject.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (firstObject == null) {
            if (other.firstObject != null) {
                return false;
            }
        } else if (!firstObject.equals(other.firstObject)) {
            return false;
        }
        if (secondObject == null) {
            if (other.secondObject != null) {
                return false;
            }
        } else if (!secondObject.equals(other.secondObject)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "firstObject:" + firstObject.toString() + ";secondObject:" + secondObject.toString();
    }

}
