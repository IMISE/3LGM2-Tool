package de.imise.util.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Eine Klasse, die eine Liste zur Verfügung stellt, über die Elemente hinzugefügt oder entfernt werden können. Das
 *
 * @author AXS (16 Aug 2019)
 */
public class ListenerSupport<T> implements Iterable<T> {

    private final Collection<T> elements = new ArrayList<>();

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

}
