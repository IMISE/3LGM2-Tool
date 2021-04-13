package de.imise.tool3lgm.graphtools.userfield.definition.definitiontree;

/**
 * @author AXS (29.03.2021)
 */
public class DefinitionGroupNode extends DefinitionStructureNode implements Cloneable {

    /**
     *
     */
    private boolean showGroupAsTitledBorderOnTab = true;

    /**
     * @param name
     */
    public DefinitionGroupNode(final String name) {
        this(name, null);
    }

    /**
     * @param name
     * @param description
     */
    public DefinitionGroupNode(final String name, final String description) {
        this(name, description, null);
    }

    /**
     * @param name
     * @param description
     * @param id
     */
    public DefinitionGroupNode(final String name, final String description, final String id) {
        super(name, description, id == null || id.length() < 4 ? "UFG" : id);
    }

    /**
     * @return the showGroupAsTitledBorderOnTab
     */
    public final boolean isShowGroupAsTitledBorderOnTab() {
        return showGroupAsTitledBorderOnTab;
    }

    /**
     * @param showGroupAsTitledBorderOnTab the showGroupAsTitledBorderOnTab to
     *            set
     */
    public final void setShowGroupAsTitledBorderOnTab(final boolean showGroupAsTitledBorderOnTab) {
        this.showGroupAsTitledBorderOnTab = showGroupAsTitledBorderOnTab;
    }

    @Override
    public DefinitionTabNode getParent() {
        return (DefinitionTabNode) super.getParent();
    }

    @Override
    public DefinitionUserFieldNode getChildAt(final int index) {
        return (DefinitionUserFieldNode) super.getChildAt(index);
    }

}
