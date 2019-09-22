package de.imise.tool3lgm.graphtools.view.tree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;

/**
 * @author AXS (22.09.2019)
 */
public abstract class DynamicTreeMouseAdapterTarget extends JTree implements GraphDocumentOwner {

    /**
     * @param newModel
     */
    public DynamicTreeMouseAdapterTarget(final TreeModel newModel) {
        super(newModel);
        DynamicTreeMouseAdapter.addAdapter(this);
    }

    /**
     * @param root
     * @param asksAllowsChildren
     */
    public DynamicTreeMouseAdapterTarget(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        DynamicTreeMouseAdapter.addAdapter(this);
    }

    /**
     * @param root
     */
    public DynamicTreeMouseAdapterTarget(final TreeNode root) {
        super(root);
        DynamicTreeMouseAdapter.addAdapter(this);
    }

    /**
     * @param o
     * @return
     */
    public boolean isLayerNode(final Object o) {
        return false;
    }

}
