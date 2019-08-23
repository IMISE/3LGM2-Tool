package de.imise.util.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Eine Klasse, die eine Liste zur Verfügung stellt, über die Elemente hinzugefügt oder entfernt werden können. Das
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

}
