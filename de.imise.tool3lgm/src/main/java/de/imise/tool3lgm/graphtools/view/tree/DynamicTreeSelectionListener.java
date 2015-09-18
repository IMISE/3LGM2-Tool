package de.imise.tool3lgm.graphtools.view.tree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;

public class DynamicTreeSelectionListener implements TreeSelectionListener {

    private final DynamicTree tree;

    public DynamicTreeSelectionListener(final DynamicTree tree) {
        this.tree = tree;
        tree.addTreeSelectionListener(this);
    }

    /**
     * Wenn Knoten selektiert wurden, die keine NodeContainer-Knoten sind, wird über diese Variable fetsgelegt,
     * dass gerade so ein Knoten aus der Selektion entfernt wurde.
     */
    int correctingSelectionCount = 0;

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        if (correctingSelectionCount > 0) {
            return;
        }
        GraphDocument doc = tree.getGraphDocument();
        //keine von sich selbst ausgelösten SelectionChangeEvents empfangen -> removen und zum Schluss wieder adden
        doc.removeGraphDocumentListener(tree);
        doc.start_transaction(DynamicTree.PID, false);
        doc.deselectAll(true);
        TreePath[] paths = tree.getSelectionPaths();
        LGMTreeNode selectedLayerNode = null;
        if (paths != null) {
            LGMTreeNode node = null;
            for (int i = 0; i < paths.length; i++) {
                node = (LGMTreeNode) paths[i].getLastPathComponent();
                Object uo = node.getUserObject();
                if (uo != null && uo instanceof NodeContainer) {
                    if (node.isSelectable()) {
                        NodeContainer knot = (NodeContainer) uo;
                        doc.addToSelection(knot, DynamicTree.PID);
                    } else {
                        correctingSelectionCount++;
                        tree.removeSelectionPath(paths[i]);
                        correctingSelectionCount--;
                    }
                }
            }
            //vom letzten selektieren Knoten den Layer bestimmen
            selectedLayerNode = getLayerNode(node);
        }
        //wenn das hier ein Layerknoten ist, wird im Tree der Layer gewechselt und es komtm true zurück
        boolean layerChanged = tree.setActiveLayer(selectedLayerNode);
        doc.finish_transaction(DynamicTree.PID, false);
        doc.distributeEvent(GraphDocument.SELECTION_CHANGED);
        if (layerChanged) {
            doc.distributeEvent(GraphDocument.ACTIVE_LAYER_CHANGED);
        }
        doc.addGraphDocumentListener(tree);
    }

    /**
     * Wenn der übergebene Knoten selbst ein Layer-Knoten ist, dann kommt dieser Knoten zurück. Wenn nicht,
     * wird solange in den Parents des Knotens gesucht bis ein Layer-Knoten gefunden wurde (der dann zurück
     * kommt) oder <code>null</code> zurück gegeben, wenn kein Layer-Knoten gefunden wurde.
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

    public void setInactive() {
        tree.removeTreeSelectionListener(this);
    }

    public void setActive() {
        tree.removeTreeSelectionListener(this);
        tree.addTreeSelectionListener(this);
    }

}
