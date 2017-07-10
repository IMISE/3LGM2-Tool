package de.imise.tool3lgm.graphtools.userfield;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.XMLSource;

/**
 * @author Thomas Rudert
 */
public class UserFieldTargetSpecificList<T extends HashSource & XMLSource> implements Cloneable, Iterable<T>, XMLSource {

    private Class<? extends UserFieldTarget> targetClass;

    private ArrayList<T> list = Lists.newArrayList();

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
        if (!list.contains(element)) {
            list.add(element);
        } else {
            // Das hier ermöglich das Importieren von userFields.
            // Es werden somit schon bestehende userField und deren Eiegenschaften überschrieben
            list.remove(element);
            list.add(element);
        }
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
    public final Object clone() {
        UserFieldTargetSpecificList<T> collection = null;
        try {
            collection = (UserFieldTargetSpecificList<T>) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        collection.targetClass = targetClass;
        collection.list = Lists.newArrayList(list);
        return collection;
    }

    /**
     * @return
     */
    @Override
    public final String toXMLString() {
        String retVal = new String();
        for (T element : list) {
            retVal = retVal.concat(element.toXMLString());
        }
        return retVal;
    }

    /**
     * @param hashString
     * @return
     */
    public final T get(final Object hashString) {
        for (T element : list) {
            if (element.getHashCode().equals(hashString)) {
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
