package de.imise.tool3lgm.graphtools.view.tree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * @author AXS (22.09.2019)
 */
public abstract class DynamicTree extends JTree implements GraphDocumentOwner {

    /**
     * @param newModel
     */
    public DynamicTree(final TreeModel newModel) {
        super(newModel);
        DynamicTreeMouseAdapter.addAdapter(this);
    }

    /**
     * @param root
     * @param asksAllowsChildren
     */
    public DynamicTree(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        DynamicTreeMouseAdapter.addAdapter(this);
    }

    /**
     * @param root
     */
    public DynamicTree(final TreeNode root) {
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

    void setTransactionListenerActive(final boolean active) {

    }

    public boolean setActiveLayer(final LGMTreeNode node) {
        return false;
    }

}
