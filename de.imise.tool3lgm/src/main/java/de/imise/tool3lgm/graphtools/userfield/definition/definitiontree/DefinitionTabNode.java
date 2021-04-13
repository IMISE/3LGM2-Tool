package de.imise.tool3lgm.graphtools.userfield.definition.definitiontree;

import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;

/**
 * @author AXS (29.03.2021)
 */
public class DefinitionTabNode extends DefinitionStructureNode {

    /**
     * @param name
     */
    public DefinitionTabNode(final String name) {
        this(name, null);
    }

    /**
     * @param name
     * @param description
     */
    public DefinitionTabNode(final String name, final String description) {
        this(name, description, null);
    }

    /**
     * @param name
     * @param description
     * @param id
     */
    public DefinitionTabNode(final String name, final String description, final String id) {
        super(name, description, id == null || id.length() < 4 ? "TAB" : id);
    }

    @Override
    public IconifiedTreeNode<?> getParent() {
        return (IconifiedTreeNode<?>) super.getParent();
    }

    @Override
    public DefinitionGroupNode getChildAt(final int index) {
        return (DefinitionGroupNode) super.getChildAt(index);
    }

}
