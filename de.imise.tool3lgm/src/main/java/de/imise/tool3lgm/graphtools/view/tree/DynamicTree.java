package de.imise.tool3lgm.graphtools.view.tree;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.tooltip.LGMToolTipProvider;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.util.ToolTipProvider;
import de.imise.util.swing.ToolTipShowTimeHandler;

/**
 * @author AXS (22.09.2019)
 */
public abstract class DynamicTree extends JTree implements GraphDocumentOwner {

    private DynamicTreeSelectionListener selectionListener;

    /**
     * Object that provides the tooltips for the treenodes. If <code>null</code>
     * no tooltips are shown.
     */
    private ToolTipProvider elementToolTipProvider;

    /**
     * The TreePath that was clicked left or right (it must not be selected and
     * can be <code>null</code>).
     */
    private TreePath clickedTreePath = null;

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

    /**
     *
     */
    private void init() {
        DynamicTreeMouseAdapter.addAdapter(this);
        selectionListener = new DynamicTreeSelectionListener(this);
        elementToolTipProvider = new LGMToolTipProvider(this);
        //Tooltip dismiss time increase (from 4s to 15s)
        ToolTipShowTimeHandler.setDismissTime(this, 15000);
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
    public boolean setActiveLayer(final LGMTreeNode<?> node) {
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

    @Override
    public final String getToolTipText(final MouseEvent event) {
        return elementToolTipProvider.getToolTip(event);
    }

    /**
     * @param path
     */
    public void scrollToPath(final TreePath[] paths) {
        int lastSelectedPathIndex = paths.length - 1;
        if (lastSelectedPathIndex >= 0) {
            TreePath lastSelectedPath = paths[lastSelectedPathIndex];
            Rectangle bounds = getPathBounds(lastSelectedPath);
            if (bounds != null) {
                bounds.x = 0;
                scrollRectToVisible(bounds);
            }
        }
    }

    /**
     * Expands the given node full (recursive)
     *
     * @param node
     */
    public void expandNodeFull(final DefaultMutableTreeNode node) {
        TreeNode[] path = node.getPath();
        TreePath treePath = new TreePath(path);
        expandPath(treePath);
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            expandNodeFull(child);
        }
    }

    /**
     *
     */
    public final void expandAll() {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        expandNodeFull(root);
    }

    /**
     * @param e
     * @return the GraphDocument which is associated with the given mouse
     *         position
     */
    public abstract GraphDocument getGraphDocument(final MouseEvent e);

    /**
     * @return The TreePath that was clicked left or right (it must not be
     *         selected and can be <code>null</code>).
     */
    public TreePath getClickedTreePath() {
        return clickedTreePath;
    }

    /**
     * @param clickedTreePath the clickedTreePath to set. This is the TreePath
     *            that was clicked left or right (it must not be selected and
     *            can be <code>null</code>).
     */
    public final void setClickedTreePath(TreePath clickedTreePath) {
        this.clickedTreePath = clickedTreePath;
    }

    /**
     * @param x
     * @param y
     * @return the clicked TreePath
     */
    public final TreePath setClickedTreePathForLocation(int x, int y) {
        TreePath pathForLocation = getPathForLocation(x, y);
        if (clickedTreePath != null) {
            addSelectionPath(pathForLocation);
        }
        setClickedTreePath(pathForLocation);
        return clickedTreePath;
    }

}
