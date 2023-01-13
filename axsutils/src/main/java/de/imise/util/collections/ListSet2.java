package de.imise.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;

/**
 * Implementation of a ordered set.<br>
 * This implementation stores all data redundant in a {@link List} and in a
 * {@link Set} for performance reasons for the contains() function. If this is
 * not necessary then use {@link ListSet} which stores all values only in a
 * {@link List}.
 *
 * @author AXS (30.06.2021)
 * @param <E>
 */
public class ListSet2<E> implements Set<E>, List<E> {

    private final List<E> contentList;

    private final Set<E> contentSet;

    public ListSet2() {
        contentList = new ArrayList<>();
        contentSet = new HashSet<>();
    }

    public ListSet2(final int capacity) {
        contentList = new ArrayList<>(capacity);
        contentSet = new HashSet<>(capacity);
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
        return contentList.get(index);
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
        contentList.set(index, element);
        contentSet.add(element);
        return element;
    }

    @Override
    public void add(int index, final E element) {
        int i = indexOf(element);
        if (i >= 0) {
            if (i == index) {
                contentList.set(index, element);
                contentSet.add(element);
                return;
            }
            remove(i);
            if (i < index) {
                index--;
            }
        }
        contentList.add(index, element);
        contentSet.add(element);
    }

    // The following implementation would crash the tests
    //    @Override
    //    public E set(final int index, final E element) {
    //        //if the element is already in the list -> nothing happens
    //        if (contentSet.add(element)) {
    //            E old = content.set(index, element);
    //            if (old != element) {
    //                contentSet.remove(old);
    //            }
    //        }
    //        return element;
    //    }
    //
    //    @Override
    //    public void add(final int index, final E element) {
    //        //if the element is already in the list -> nothing happens
    //        if (contentSet.add(element)) {
    //            content.add(index, element);
    //        }
    //    }

    @Override
    public E remove(final int index) {
        E removed = contentList.remove(index);
        contentSet.remove(removed);
        return removed;
    }

    @Override
    public int indexOf(final Object o) {
        return contentList.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return contentList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return contentList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return contentList.listIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return contentList.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return contentList.size();
    }

    @Override
    public boolean isEmpty() {
        return contentList.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return contentSet.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return contentList.iterator();
    }

    @Override
    public Object[] toArray() {
        return contentList.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return contentList.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        add(contentList.size(), e);
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        if (contentSet.remove(o)) {
            return contentList.remove(o);
        }
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return contentSet.containsAll(c);
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
        contentSet.retainAll(c);
        return contentList.retainAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        contentSet.removeAll(c);
        return contentList.removeAll(c);
    }

    @Override
    public void clear() {
        contentList.clear();
        contentSet.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        return Set.super.spliterator();
    }

}
