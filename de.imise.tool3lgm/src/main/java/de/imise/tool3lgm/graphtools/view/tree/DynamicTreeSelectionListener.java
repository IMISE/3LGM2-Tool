package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ACTIVE_LAYER_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

public class DynamicTreeSelectionListener implements TreeSelectionListener {

    private final DynamicTree tree;

    private boolean active = true;

    public DynamicTreeSelectionListener(final DynamicTree tree) {
        this.tree = tree;
        tree.addTreeSelectionListener(this);
    }

    /**
     * If nodes are selected that are not NodeContainer nodes, this variable
     * indicates that such a node has been removed from the selection.
     */
    int correctingSelectionCount = 0;

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        if (!active || correctingSelectionCount > 0) {
            return;
        }
        GraphDocument doc = tree.getGraphDocument();
        if (doc == null) {
            return;
        }
        // do not receive SelectionChangeEvents triggered by itself -> deactivate and switch on again for closing
        tree.setTransactionListenerActive(false);
        doc.start_transaction(ModelBrowserTree.PID, false);
        doc.deselectAll(true);
        TreePath[] paths = tree.getSelectionPaths();
        LGMTreeNode<?> selectedLayerNode = null;
        if (paths != null) {
            LGMTreeNode<?> node = null;
            for (int i = 0; i < paths.length; i++) {
                node = (LGMTreeNode<?>) paths[i].getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof NodeContainer) {
                    if (node.isSelectable()) {
                        NodeContainer knot = (NodeContainer) userObject;
                        doc.addToSelection(knot, ModelBrowserTree.PID);
                    } else {
                        correctingSelectionCount++;
                        tree.removeSelectionPath(paths[i]);
                        correctingSelectionCount--;
                    }
                }
            }
            // determine the layer from the last selected node
            selectedLayerNode = getLayerNode(node);
        }
        // if this is a layer node, the layer is changed in the tree and true is returned
        boolean layerChanged = tree.setActiveLayer(selectedLayerNode);
        doc.finish_transaction(ModelBrowserTree.PID, false);
        doc.distributeEvent(SELECTION_CHANGED);
        if (layerChanged) {
            doc.distributeEvent(ACTIVE_LAYER_CHANGED);
        }
        tree.setTransactionListenerActive(true);
    }

    /**
     * If the passed node is itself a layer node, then that node is returned. If
     * not, it will search in the node's parents until a layer node is found
     * (which will return) or <code>null</code> is returned if no layer node is
     * found.
     *
     * @param node
     * @return
     */
    private LGMTreeNode<?> getLayerNode(final LGMTreeNode<?> node) {
        TreeNode parent = node;
        while (parent != null) {
            if (tree.isLayerNode(parent)) {
                return (LGMTreeNode<?>) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

}
