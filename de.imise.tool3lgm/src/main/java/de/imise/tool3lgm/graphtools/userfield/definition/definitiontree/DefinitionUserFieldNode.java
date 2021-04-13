package de.imise.tool3lgm.graphtools.userfield.definition.definitiontree;

import de.imise.tool3lgm.graphtools.IDSource;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;

/**
 * @author AXS (29.03.2021)
 */
public class DefinitionUserFieldNode extends IconifiedTreeNode<UserField> implements IDSource {

    /**
     * @param userField
     */
    public DefinitionUserFieldNode(final UserField userField) {
        super(userField, false);
    }

    @Override
    public String getID() {
        UserField userField = getUserObject();
        return userField.getID();
    }

    @Override
    public DefinitionUserFieldNode clone() {
        DefinitionUserFieldNode clone = null;
        try {
            clone = (DefinitionUserFieldNode) super.clone();
            UserField originalUserObject = getUserObject();
            clone.userObject = originalUserObject.clone();
        } catch (Exception e) {
            //this should never happen since we are cloneable
            throw new InternalError(e);
        }
        return clone;
    }

    @Override
    public String toString() {
        UserField userField = getUserObject();
        String name = userField.getName() + "  ( " + userField.getStyle() + " )";
        return name;

    }

    @Override
    public DefinitionGroupNode getParent() {
        return (DefinitionGroupNode) super.getParent();
    }

}
