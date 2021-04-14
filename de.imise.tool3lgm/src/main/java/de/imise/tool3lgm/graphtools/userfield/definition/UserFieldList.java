package de.imise.tool3lgm.graphtools.userfield.definition;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * @author Thomas Rudert
 */
public class UserFieldList implements Cloneable, Iterable<UserField> {

    private final Class<? extends UserFieldTarget> targetClass;

    private List<UserField> list = new ArrayList<>();

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
    public final void insert(final UserField element, final int index) {
        if (isEmpty()) {
            add(element);
        } else {
            list.remove(element);
            list.add(index, element);
        }
    }

    /**
     * @param userFields
     * @param userField
     */
    private void ensureDefaultTab(final UserField userField) {
        if (userField.hasStyle(TAB)) {
            return;
        }
        Class<? extends UserFieldTarget> targetClass = userField.getTargetClass();
        if (targetClass != UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS) {
            if (isEmpty()) {
                UserField defaultTab = new UserField(targetClass, TAB);
                defaultTab.setName(Tool3lgmConstants.getResString("userfields"));
                add(defaultTab);
            }
        }
    }

    /**
     * @param element
     */
    public final void add(final UserField element) {
        list.remove(element);
        ensureDefaultTab(element);
        list.add(element);
    }

    /**
     * Ersetzt das element am gegebenen Index durch das übergebene.
     *
     * @param index
     * @param element
     */
    void set(final int index, final UserField element) {
        list.set(index, element);
    }

    /**
     * @param element
     */
    public final void remove(final UserField element) {
        list.remove(element);
    }

    /**
     * @return
     */
    public final int size() {
        return list.size();
    }

    /**
     * @param i
     * @return
     */
    public final UserField get(final int i) {
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
    public final UserField get(final Object id) {
        for (UserField element : list) {
            if (element.getID().equals(id)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public final Iterator<UserField> iterator() {
        return list.iterator();
    }

    /**
     * @return Immutable copy of the data
     */
    public final List<UserField> getData() {
        return ImmutableList.copyOf(list);
    }

    public boolean isValidIndex(final int index) {
        return index >= 0 && index < list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

}