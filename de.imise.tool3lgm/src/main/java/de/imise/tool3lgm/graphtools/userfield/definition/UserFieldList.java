package de.imise.tool3lgm.graphtools.userfield.definition;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SUBTYPE;
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
    public final void insert(final UserField userField, int index) {
        if (index == list.size()) {
            add(userField); //this checks if a default tab must be added
        } else {
            list.remove(userField);
            if (ensureDefaultTab(userField, index) != null) {
                index++;
            }
            list.add(index, userField);
        }
    }

    /**
     * @param userField
     * @return if a new default tab userField was added, this tab userfield will
     *         be returned. If no new Tab was added <code>null</code> will be
     *         returned.
     */
    /**
     * @param userField
     * @param index
     * @return
     */
    private UserField ensureDefaultTab(final UserField userField, final int index) {
        if (!userField.hasStyle(TAB, SUBTYPE)) {
            Class<? extends UserFieldTarget> targetClass = userField.getTargetClass();
            if (targetClass != UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS) {
                if (isEmpty() || index == 0 || get(index - 1).hasStyle(SUBTYPE)) {
                    UserField defaultTab = new UserField(targetClass, TAB);
                    defaultTab.setName(Tool3lgmConstants.getResString("userfields"));
                    insert(defaultTab, index);
                    return defaultTab;
                }
            }
        }
        return null;
    }

    /**
     *
     */
    public void ensureDefaultTabs() {
        for (int i = 0; i < size(); i++) {
            UserField userField = get(i);
            ensureDefaultTab(userField, i);
        }
    }

    /**
     * @param userField
     * @return if a new default tab userField was added, this tab userfield will
     *         be returned. If no new Tab was added <code>null</code> will be
     *         returned.
     */
    public final UserField add(final UserField userField) {
        list.remove(userField);
        UserField createdDefaultTabUserField = ensureDefaultTab(userField, list.size());
        list.add(userField);
        return createdDefaultTabUserField;
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
     * @param subType
     * @return for every tab in the whole list an own sub list
     */
    public List<UserFieldList> getTabSubLists(final SubType subType) {
        UserField lastSubTypeField = null;
        List<UserFieldList> tabSubLists = new ArrayList<>();
        UserFieldList tabSubList = new UserFieldList(targetClass);
        int i = 0;
        UserField firstUserField = list.get(i);
        if (firstUserField.hasStyle(SUBTYPE)) {
            //first userfield is a subtype but we want to get the tab list for no subtype -> return the empty tab list
            if (SubType.isDummy(subType)) {
                return tabSubLists;
            }
            //first subtype definition matches the given subtype
            if (subType.hasUserField(firstUserField)) {
                firstUserField = list.get(++i);
            } else {
                //first subtype definition does not match the given subtype
                for (i++; i < list.size(); i++) {
                    //search for the first UserField after the UserField with the given subtype
                    firstUserField = list.get(i);
                    if (subType.hasUserField(firstUserField)) {
                        if (++i < list.size()) {
                            firstUserField = list.get(i);
                            break;
                        }
                    }
                }
            }
        }
        if (firstUserField.hasStyle(TAB)) {
            tabSubList.add(firstUserField);
            i++;
        }
        for (; i < list.size(); i++) {
            UserField userField = list.get(i);
            if (userField.hasStyle(SUBTYPE)) {
                lastSubTypeField = userField;
                continue;
            }
            if (lastSubTypeField == null || subType != null && subType.hasUserField(lastSubTypeField)) {
                if (userField.hasStyle(TAB)) {
                    tabSubLists.add(tabSubList);
                    tabSubList = new UserFieldList(targetClass);
                }
                tabSubList.add(userField);
            }
            if (i == list.size() - 1) {
                tabSubLists.add(tabSubList);
            }
        }
        return tabSubLists;
    }

    /**
     * Refreshes the parent subtypes property for every UserField so that every
     * UserField knows for which subtype it is defined
     */
    void refreshParentSubTypesForUserFields() {
        SubType subType = SubType.DUMMY_SUBTYPE;
        for (UserField userField : list) {
            if (userField.hasStyle(SUBTYPE)) {
                subType = new SubType(userField);
            }
            userField.setParentSubType(subType);
        }
    }

    /**
     * Marks the parent subtypes of all userFields in this list as invalid
     */
    public void invalidateParentSubTypesForUserFields() {
        for (UserField userField : list) {
            userField.setParentSubType(null);
        }
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

    @Override
    public String toString() {
        return list.toString();
    }

}