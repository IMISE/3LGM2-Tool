/*
 * Created on 26.10.2007
 */
package de.imise.util.collections;

import java.util.Collection;
import java.util.TreeSet;

import de.imise.util.Alphabetical;

/**
 * TreeSet, das seine Elemente immer nach dem Set dessen Elemente immer nach dem {@link Alphabetical#getLocalizedComparator()} sortiert.
 * 
 * @author AXS
 */
public class AlphabeticalSet<E> extends TreeSet<E> implements Cloneable {

    public AlphabeticalSet() {
        super(Alphabetical.getLocalizedComparator());
    }

    /**
     * @param arg0
     */
    public AlphabeticalSet(final Collection<? extends E> arg0) {
        this();
        addAll(arg0);
    }

}
