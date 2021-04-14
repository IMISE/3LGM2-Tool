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

    /**
     *
     */
    private final Class<? extends UserFieldTarget> targetClass;

    /**
     *
     */
    private List<UserField> list = new ArrayList<>();

    /**
     * @param targetClass
     */
    public UserFieldList(final Class<? extends UserFieldTarget> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * @param userField
     * @param index
     */
    public final void insert(final UserField userField, final int index) {
        if (isEmpty()) {
            add(userField);
        } else {
            list.remove(userField);
            list.add(index, userField);
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
     * @param userField
     */
    public final void add(final UserField userField) {
        list.remove(userField);
        ensureDefaultTab(userField);
        list.add(userField);
    }

    /**
     * Ersetzt das element am gegebenen Index durch das übergebene.
     *
     * @param index
     * @param userField
     */
    void set(final int index, final UserField userField) {
        list.set(index, userField);
    }

    /**
     * @return
     */
    private boolean canRemoveTab() {
        //if there is only one tab in the list -> you can remove it
        if (list.size() == 1) {
            return true;
        }
        //if there are more than 1 element in the list -> don't remove the last tab = return false
        int tabCount = 0;
        for (UserField userField : list) {
            if (userField.hasStyle(TAB)) {
                if (tabCount == 1) {
                    return true;
                }
                tabCount++;
            }
        }
        return false;
    }

    /**
     * @param userField
     */
    public final void remove(final UserField userField) {
        if (!userField.hasStyle(TAB) || canRemoveTab()) {
            list.remove(userField);
        }
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