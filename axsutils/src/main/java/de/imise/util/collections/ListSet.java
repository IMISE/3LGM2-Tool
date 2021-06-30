package de.imise.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;

/**
 * @author AXS (30.06.2021)
 * @param <E>
 */
public class ListSet<E> implements Set<E>, List<E> {

    private final List<E> content;

    public ListSet() {
        content = new ArrayList<>();
    }

    public ListSet(final int capacity) {
        content = new ArrayList<>(capacity);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        for (E e : c) {
            add(index, e);
        }
        return true;
    }

    @Override
    public E get(final int index) {
        return content.get(index);
    }

    @Override
    public E set(int index, final E element) {
        int i = indexOf(element);
        if (i >= 0 && i != index) {
            remove(i);
            if (i < index) {
                index--;
            }
        }
        content.set(index, element);
        return element;
    }

    @Override
    public void add(int index, final E element) {
        int i = indexOf(element);
        if (i >= 0) {
            if (i == index) {
                content.set(index, element);
                return;
            } else {
                remove(i);
                if (i < index) {
                    index--;
                }
            }
        }
        content.add(index, element);
    }

    @Override
    public E remove(final int index) {
        return content.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return content.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return content.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return content.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return content.listIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return content.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return content.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return content.iterator();
    }

    @Override
    public Object[] toArray() {
        return content.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return content.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        add(content.size(), e);
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        return content.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return content.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        for (E e : c) {
            add(e);
        }
        return true;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return content.retainAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return content.removeAll(c);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        return Set.super.spliterator();
    }

}
