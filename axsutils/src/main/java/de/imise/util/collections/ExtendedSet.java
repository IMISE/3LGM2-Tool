package de.imise.util.collections;

import java.util.Collection;
import java.util.HashSet;

/**
 * Erweiterungsklasse zum {@link HashSet}
 * 
 * @author fstephan
 * @param <E>
 */
public class ExtendedSet<E> extends HashSet<E> {

    /**
     * Erzeugt eine neue Instanz dieser Klasse und fügt alle spezifizierten
     * Elemente hinzu.
     * 
     * @param es Initial hinzuzufügende Elemente
     */
    @SafeVarargs
    public ExtendedSet(final E... es) {
        super();
        for (E e : es) {
            add(e);
        }
    }

    /**
     * Erzeugt eine neue Instanz dieser Klasse und fügt alle spezifizierten
     * Elemente hinzu.
     * 
     * @param es Initial hinzuzufügende Elemente
     */
    public ExtendedSet(final Collection<E> elements) {
        super(elements);
    }

    public ExtendedSet(final int initialCapacity) {
        super(initialCapacity);
    }

}
