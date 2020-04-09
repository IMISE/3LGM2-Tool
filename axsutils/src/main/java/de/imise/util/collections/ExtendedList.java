package de.imise.util.collections;

import java.util.ArrayList;

/**
 * Erweiterungsklasse zur {@link ArrayList}
 *
 * @author fstephan
 * @param <E>
 */
public class ExtendedList<E> extends ArrayList<E> {

    @SafeVarargs
    public ExtendedList(final E... es) {
        this();
        for (E e : es) {
            add(e);
        }
    }

    public ExtendedList() {
        super();
    }

    public void setLastElement(final E e) {
        set(size() - 1, e);
    }

    public boolean removeLastElement() {
        int n = size();
        if (n > 0) {
            remove(size() - 1);
            return true;
        }
        return false;
    }

    public E getLastElement() {
        if (isEmpty()) {
            return null;
        }
        return get(size() - 1);
    }

    @SafeVarargs
    public final void addAll(final E... es) {
        for (E e : es) {
            add(e);
        }
    }
}
