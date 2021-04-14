package de.imise.tool3lgm.graphtools.userfield.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.imise.tool3lgm.graphtools.userfield.UserFieldListItem;

/**
 * @author Thomas Rudert
 */
public class UserFieldList implements Cloneable, Iterable<UserField> {

    private final Class<? extends UserFieldTarget> targetClass;

    private List<UserFieldListItem> list = new ArrayList<>();

    /**
     * @param targetClass
     */
    public UserFieldList(final Class<? extends UserFieldTarget> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * @param element
     * @param index
     */
    public final void insert(final UserFieldListItem element, final int index) {
        list.remove(element);
        list.add(index, element);
    }

    protected int getInsertIndex(final UserFieldListItem element) {
        return -1;
    }

    /**
     * @param element
     */
    public final void add(final UserFieldListItem element) {
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
    public void set(final int index, final UserFieldListItem element) {
        list.set(index, element);
    }

    /**
     * @param element
     */
    public final void remove(final UserFieldListItem element) {
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
    public final UserFieldListItem get(final int i) {
        return isValidIndex(i) ? list.get(i) : null;
    }

    /**
     * @return
     */
    public final Class<? extends UserFieldTarget> getTargetClass() {
        return targetClass;
    }

    @Override
    public UserFieldList clone() {
        UserFieldList clone = null;
        try {
            clone = (UserFieldList) super.clone();
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
    public final UserFieldListItem get(final Object id) {
        for (UserFieldListItem element : list) {
            if (element.getID().equals(id)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public final Iterator<UserField> iterator() {
        return new Iterator<UserField>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                for (; i < list.size(); i++) {
                    UserFieldListItem userFieldListItem = list.get(i);
                    if (userFieldListItem instanceof UserField) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public UserField next() {
                return (UserField) list.get(i);
            }

        };
    }

    /**
     * Kopie
     *
     * @return
     */
    public final List<UserField> getData() {
        List<UserField> userFieldItems = new ArrayList<>();
        for (UserFieldListItem item : list) {
            if (item instanceof UserField) {
                userFieldItems.add((UserField) item);
            }
        }
        return userFieldItems;
    }

    public int size() {
        return list.size();
    }

    public boolean isValidIndex(final int index) {
        return index >= 0 && index < list.size();
    }

}