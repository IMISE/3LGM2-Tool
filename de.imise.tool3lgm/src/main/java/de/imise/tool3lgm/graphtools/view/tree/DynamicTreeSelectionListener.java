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
     * Wenn Node selektiert wurden, die keine NodeContainer-Node sind, wird über diese Variable fetsgelegt,
     * dass gerade so ein Node aus der Selektion entfernt wurde.
     */
    int correctingSelectionCount = 0;

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        if (!active || correctingSelectionCount > 0) {
            return;
        }
        GraphDocument doc = tree.getGraphDocument();
        //keine von sich selbst ausgelösten SelectionChangeEvents empfangen -> deaktivieren und zum Schnluss wieder anschalten
        tree.setTransactionListenerActive(false);
        doc.start_transaction(DynamicTree.PID, false);
        doc.deselectAll(true);
        TreePath[] paths = tree.getSelectionPaths();
        LGMTreeNode selectedLayerNode = null;
        if (paths != null) {
            LGMTreeNode node = null;
            for (int i = 0; i < paths.length; i++) {
                node = (LGMTreeNode) paths[i].getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject != null && userObject instanceof NodeContainer) {
                    if (node.isSelectable()) {
                        NodeContainer knot = (NodeContainer) userObject;
                        doc.addToSelection(knot, DynamicTree.PID);
                    } else {
                        correctingSelectionCount++;
                        tree.removeSelectionPath(paths[i]);
                        correctingSelectionCount--;
                    }
                }
            }
            //vom letzten selektieren Node den Layer bestimmen
            selectedLayerNode = getLayerNode(node);
        }
        //wenn das hier ein Layerknoten ist, wird im Tree der Layer gewechselt und es komtm true zurück
        boolean layerChanged = tree.setActiveLayer(selectedLayerNode);
        doc.finish_transaction(DynamicTree.PID, false);
        doc.distributeEvent(SELECTION_CHANGED);
        if (layerChanged) {
            doc.distributeEvent(ACTIVE_LAYER_CHANGED);
        }
        tree.setTransactionListenerActive(true);
    }

    /**
     * Wenn der übergebene Node selbst ein Layer-Node ist, dann kommt dieser Node zurück. Wenn nicht,
     * wird solange in den Parents des Knotens gesucht bis ein Layer-Node gefunden wurde (der dann zurück
     * kommt) oder <code>null</code> zurück gegeben, wenn kein Layer-Node gefunden wurde.
     *
     * @param node
     * @return
     */
    private LGMTreeNode getLayerNode(final LGMTreeNode node) {
        TreeNode parent = node;
        while (parent != null) {
            if (tree.isLayerNode(parent)) {
                return (LGMTreeNode) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

}
