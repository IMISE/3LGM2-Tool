package de.imise.util.collections;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Implementierung einer sortierten Menge.
 * 
 * @author fstephan
 * @param <E>
 */
public class SortedSet<E> extends AbstractSet<E> {

    /**
     * @param comp
     * @param initialCapacity
     * @return
     */
    public static <T> SortedSet<T> createSet(Comparator<T> comp, int initialCapacity) {
        return new SortedSet<T>(comp, initialCapacity);
    }

    /**
     * @param comp
     * @return
     */
    public static <T> SortedSet<T> createSet(Comparator<T> comp) {
        return new SortedSet<T>(comp);
    }

    /**
     * @return
     */
    public static <T extends Comparable<T>> SortedSet<T> createSet() {
        Comparator<T> comp = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
        return createSet(comp);
    }

    /**
     * @param initialCapacity
     * @return
     */
    public static <T extends Comparable<T>> SortedSet<T> createSet(int initialCapacity) {
        Comparator<T> comp = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
        return createSet(comp, initialCapacity);
    }

    private Comparator<E> comp;
    private ArrayList<E> elements;

    public SortedSet(Comparator<E> comp, int initialCapacity) {
        elements = new ArrayList<E>(initialCapacity);
        this.comp = comp;
    }

    public SortedSet(Comparator<E> comp) {
        elements = new ArrayList<E>();
        this.comp = comp;
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) // Element ist bereits enthalten
            return false;
        int pos = Collections.binarySearch(elements, e, comp);
        elements.add(-pos - 1, e);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean b = false;
        for (E e : c)
            b |= add(e);
        return b;
    }

    public E get(int index) {
        return elements.get(index);
    }

    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }
}
