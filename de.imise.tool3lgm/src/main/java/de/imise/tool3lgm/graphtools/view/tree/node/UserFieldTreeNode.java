package de.imise.tool3lgm.graphtools.view.tree.node;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.BrowseUtils;
import de.imise.util.pair.Pair;

/**
 * @author AXS (8 Apr 2019)
 */
public class UserFieldTreeNode extends LGMTreeNode {

    /**
     * @param elementClass
     */
    public UserFieldTreeNode(final UserField userField, final ModelElement me) {
        super(new Pair<>(userField, me), getLabel(userField, me), false); // da kommt nichts drunter -> sort = false
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pair<UserField, ModelElement> getUserObject() {
        return (Pair<UserField, ModelElement>) super.getUserObject();
    }

    @Override
    public void setUserObject(final Object userObject) {
        throw new UnsupportedOperationException();
    }

    private static final String getLabel(final UserField uf, final ModelElement me) {
        String label = null;
        if (uf.hasStyle(UserField.Style.HYPERLINK)) {
            String value = me.getUserFieldInputValue(uf);
            if (UserField.isError(value)) {
                value = ""; //Bei nicht vorhandenen Links nichts statt EMPTY_VALUE anzeigen
            }
            label = uf.getName() + ": " + value;
        } else if (uf.hasStyle(UserField.Style.SEPARATOR)) {
            label = "--- " + uf.getName() + " ---------";
        } else if (uf.isClassificationUserField()) {
            label = uf.getName() + ": " + uf.getFormattedValue(me, true);
        } else {
            String value = me.getUserFieldInputValue(uf);
            label = uf.getName() + ": " + value;
        }
        return label;
    }

    public final UserField getUserField() {
        Pair<UserField, ModelElement> userObject = getUserObject();
        return userObject.getFirstItem();
    }

    public final ModelElement getModelElement() {
        Pair<UserField, ModelElement> userObject = getUserObject();
        return userObject.getSecondItem();
    }

    /**
     * Wenn das UserField ein Hyperlink ist, dann wird dieser geöffnet.
     *
     * @return <code>true</code> wenn das UserField ein Hyperlink, sonst <code>false</code>
     */
    public boolean openHyperlink() {
        UserField userField = getUserField();
        if (userField.hasStyle(UserField.Style.HYPERLINK)) {
            ModelElement me = getModelElement();
            String link = me.getUserFieldInputValue(userField);
            if (!UserField.isError(link)) {
                BrowseUtils.browse(link);
            }
            return true;
        }
        return false;
    }

}