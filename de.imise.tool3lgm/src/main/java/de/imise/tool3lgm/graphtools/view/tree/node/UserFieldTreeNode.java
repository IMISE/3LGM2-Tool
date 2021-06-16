package de.imise.tool3lgm.graphtools.view.tree.node;

import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.HYPERLINK;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SEPARATOR;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SUBTYPE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;
import static de.imise.util.StringUtils.trimAndRemoveNewLines;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.util.BrowseUtils;
import de.imise.util.pair.Pair;

/**
 * @author AXS (8 Apr 2019)
 */
public class UserFieldTreeNode extends IconifiedTreeNode<Pair<UserField, ModelElement>> {

    /**
     * @param userField
     * @param me
     */
    public UserFieldTreeNode(final UserField userField, final ModelElement me) {
        super(new Pair<>(userField, me), getLabel(userField, me), false); // da kommt nichts drunter -> sort = false
    }

    @Override
    public void setUserObject(final Object userObject) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param userField
     * @param me
     * @return
     */
    private static final String getLabel(final UserField userField, final ModelElement me) {
        String label = null;
        String name = userField.getName();
        name = trimAndRemoveNewLines(name);
        if (userField.hasStyle(HYPERLINK)) {
            String value = me.getUserFieldInputValue(userField);
            if (UserField.isError(value)) {
                value = ""; //Bei nicht vorhandenen Links nichts statt EMPTY_VALUE anzeigen
            }
            label = name + ": " + value;
        } else if (userField.hasStyle(SEPARATOR)) {
            label = "--- " + name + " ---------";
        } else if (userField.isNumberUserField()) {
            label = name + ": " + userField.getFormattedValue(me, true);
        } else if (userField.hasStyle(TAB) || userField.hasStyle(GROUP)) {
            label = name;
        } else {
            String value = me.getUserFieldInputValue(userField);
            String tmpValue = value.trim();
            String htmlTag = "<html>";
            if (tmpValue.startsWith("<")) {
                tmpValue = value.toLowerCase();
                int htmlStart = tmpValue.indexOf(htmlTag);
                if (htmlStart < 0) {
                    htmlTag = null;
                } else {
                    if (htmlStart == 0) {
                        value = value.substring(htmlTag.length());
                    } else {
                        value = value.substring(0, htmlStart) + value.substring(htmlStart + htmlTag.length());
                    }
                    name = htmlTag + name;
                }
            }
            label = name + ": " + value;
        }
        return label;
    }

    /**
     * @return
     */
    public final UserField getUserField() {
        Pair<UserField, ModelElement> userObject = getUserObject();
        return userObject.getFirstItem();
    }

    /**
     * @return
     */
    public final ModelElement getModelElement() {
        Pair<UserField, ModelElement> userObject = getUserObject();
        return userObject.getSecondItem();
    }

    /**
     * Wenn das UserField ein Hyperlink ist, dann wird dieser geöffnet.
     *
     * @return <code>true</code> wenn das UserField ein Hyperlink, sonst
     *         <code>false</code>
     */
    public void openUserFieldEditorOrTarget() {
        UserField userField = getUserField();
        ModelElement me = getModelElement();
        if (userField.hasStyle(UserField.Style.HYPERLINK)) {
            String link = me.getUserFieldInputValue(userField);
            if (!UserField.isError(link)) {
                BrowseUtils.browse(link);
            }
        } else {
            UserFieldDefinitions userFieldDefinitions = me.getUserFieldDefinitions();
            UserField parentTab = userFieldDefinitions.getParentTab(userField);
            UserField valueUserField = userField.hasStyle(SUBTYPE, TAB) ? null : userField;
            ElementPropertyDialog dialog = me.showPropertyDialog();
            dialog.selectTab(parentTab, valueUserField);
        }

    }

    /**
     * @param style
     * @return
     */
    public boolean hasStyle(final Style style) {
        UserField userField = getUserField();
        return userField.hasStyle(style);
    }

}