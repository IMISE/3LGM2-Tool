package de.imise.tool3lgm.graphtools.userfield;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Thomas Rudert
 */
public class UserFieldTargetSpecificList<T extends IDSource> implements Cloneable, Iterable<T> {

    private final Class<? extends UserFieldTarget> targetClass;

    protected List<T> list = new ArrayList<>();

    /**
     * @param targetClass
     */
    public UserFieldTargetSpecificList(final Class<? extends UserFieldTarget> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * @param element
     * @param index
     */
    public final void insert(final T element, final int index) {
        list.remove(element);
        list.add(index, element);
    }

    protected int getInsertIndex(final T element) {
        return -1;
    }

    /**
     * @param element
     */
    public final void add(final T element) {
        int insertIndex = getInsertIndex(element);
        if (insertIndex >= 0) {
            insert(element, insertIndex);
        }
        list.remove(element);
        list.add(element);
    }

    /**
     * Ersetzt das element am gegebenen Index durch das übergebene.
     *
     * @param index
     * @param element
     */
    public void set(final int index, final T element) {
        list.set(index, element);
    }

    /**
     * @param element
     */
    public final void remove(final T element) {
        list.remove(element);
    }

    /**
     * @return
     */
    public final int getSize() {
        return list.size();
    }

    /**
     * @param i
     * @return
     */
    public final T get(final int i) {
        return isValidIndex(i) ? list.get(i) : null;
    }

    /**
     * @return
     */
    public final Class<? extends UserFieldTarget> getTargetClass() {
        return targetClass;
    }

    @Override
    public UserFieldTargetSpecificList<T> clone() {
        UserFieldTargetSpecificList<T> clone = null;
        try {
            clone = (UserFieldTargetSpecificList<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            //this should never happen since we are cloneable
            throw new InternalError(e);
        }
        //die Liste selbst clonen
        clone.list = new ArrayList<>(list);
        return clone;
    }

    /**
     * @param id
     * @return
     */
    public final T get(final Object id) {
        for (T element : list) {
            if (element.getID().equals(id)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public final Iterator<T> iterator() {
        return list.iterator();
    }

    /**
     * Kopie
     *
     * @return
     */
    public final List<T> getData() {
        return ImmutableList.copyOf(list);
    }

    public int size() {
        return list.size();
    }

    public boolean isValidIndex(final int index) {
        return index >= 0 && index < list.size();
    }

}