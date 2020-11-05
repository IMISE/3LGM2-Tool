package de.imise.util.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class that provides a list through which elements can be added or removed.
 * Actually the class is very general and only the name indicates the intended
 * application area.
 *
 * @author AXS (16 Aug 2019)
 */
public class ListenerSupport<T> implements Iterable<T> {

    private final List<T> elements = new ArrayList<>();

    /**
     * @param element
     */
    public void add(final T element) {
        remove(element);
        elements.add(element);
    }

    /**
     * @param element
     */
    public void remove(final T element) {
        elements.remove(element);
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    /**
     * @return
     */
    public int size() {
        return elements.size();
    }

    /**
     * @param i
     * @return
     */
    public T get(final int i) {
        return elements.get(i);
    }

    /**
     * @param element
     * @return
     */
    public boolean contains(final Object element) {
        return elements.contains(element);
    }

}
