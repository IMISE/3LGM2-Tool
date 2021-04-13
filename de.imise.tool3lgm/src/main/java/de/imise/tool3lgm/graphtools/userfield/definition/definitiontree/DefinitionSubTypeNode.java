package de.imise.tool3lgm.graphtools.userfield.definition.definitiontree;

import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;

/**
 * @author AXS (29.03.2021)
 */
public class DefinitionSubTypeNode extends DefinitionStructureNode {

    /**
     * @param name
     */
    public DefinitionSubTypeNode(final String name) {
        this(name, null);
    }

    /**
     * @param name
     * @param description
     */
    public DefinitionSubTypeNode(final String name, final String description) {
        this(name, description, null);
    }

    /**
     * @param name
     * @param description
     * @param id
     */
    public DefinitionSubTypeNode(final String name, final String description, final String id) {
        super(name, description, id == null || id.length() < 4 ? "TYP" : id);
    }

    @Override
    public IconifiedTreeNode<?> getParent() {
        return (IconifiedTreeNode<?>) super.getParent();
    }

    @Override
    public DefinitionTabNode getChildAt(final int index) {
        return (DefinitionTabNode) super.getChildAt(index);
    }

}
