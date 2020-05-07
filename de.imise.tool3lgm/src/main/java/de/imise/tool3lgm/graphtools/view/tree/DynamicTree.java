package de.imise.tool3lgm.graphtools.view.tree;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.gui.menu.ContextGenerator;

/**
 * @author AXS (22.09.2019)
 */
public abstract class DynamicTree extends JTree implements GraphDocumentOwner {

    private DynamicTreeSelectionListener selectionListener;

    /**
     * @param newModel
     */
    public DynamicTree(final TreeModel newModel) {
        super(newModel);
        init();
    }

    /**
     * @param root
     * @param asksAllowsChildren
     */
    public DynamicTree(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        init();
    }

    /**
     * @param root
     */
    public DynamicTree(final TreeNode root) {
        super(root);
        init();
    }

    private void init() {
        DynamicTreeMouseAdapter.addAdapter(this);
        selectionListener = new DynamicTreeSelectionListener(this);
    }

    /**
     * @return the ContextGenerator for this tree
     */
    public abstract ContextGenerator getContextGenerator();

    /**
     * @param active
     * @return
     */
    protected void setSelectionListenerActive(final boolean active) {
        selectionListener.setActive(active);
    }

    /**
     * @param o
     * @return
     */
    public boolean isLayerNode(final Object o) {
        return false;
    }

    /**
     * @param active
     */
    void setTransactionListenerActive(final boolean active) {

    }

    /**
     * @param node
     * @return
     */
    public boolean setActiveLayer(final LGMTreeNode node) {
        return false;
    }

    /**
     * @return
     */
    public TreePath getRootPath() {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        TreeNode root = (TreeNode) model.getRoot();
        TreeNode[] pathToRoot = model.getPathToRoot(root);
        TreePath rootPath = new TreePath(pathToRoot);
        return rootPath;
    }

    /**
     * @param expandedPaths
     */
    public final void setExpandedPaths(final Enumeration<TreePath> expandedPaths) {
        if (expandedPaths != null) {
            while (expandedPaths.hasMoreElements()) {
                TreePath path = expandedPaths.nextElement();
                expandPath(path);
            }
        }
    }

}
