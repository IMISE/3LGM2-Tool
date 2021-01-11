package de.imise.util.swing.dnd.tree.demo;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.imise.util.swing.dnd.tree.TreeDragNDropData;
import de.imise.util.swing.dnd.tree.TreeTransferHandler;

/**
 * @author helloworld922
 *         <p>
 * @version 1.0
 *          <p>
 *          copyright 2010 <br>
 *          You are welcome to use/modify this code for any purposes you want so
 *          long as credit is given to me.
 */
public class JTreeTransferHandler extends TreeTransferHandler {
    /**
     * Creates a JTreeTransferHandler to handle a certain tree. Note that this
     * constructor does NOT set this transfer handler to be that tree's transfer
     * handler, you must still add it manually.
     *
     * @param tree
     */
    public JTreeTransferHandler(final JTree tree) {
        super(tree);
    }

    /**
     * @param supp
     * @return
     */
    @Override
    public boolean canImport(final TransferSupport supp) {
        // Setup so we can always see what it is we are dropping onto.
        supp.setShowDropLocation(true);
        if (supp.isDataFlavorSupported(TreeDragNDropData.TreeDragNDropData_FLAVOR)) {
            // at the moment, only allow us to import list of DnDNodes from other trees
            if (isDragOnSourceTree(supp)) {
                return false;
            }
            // Fetch the drop path
            TreePath dropPath = getDropLocationTreePath(supp);
            // Determine whether we accept the location
            if (dropPath != null && dropPath.getLastPathComponent() instanceof DnDNode) {
                // only allow us to drop onto a DnDNode
                try {
                    // using the node-defined checker, see if that node will
                    // accept
                    // every selected node as a child.
                    DnDNode parent = (DnDNode) dropPath.getLastPathComponent();
                    TreePath[] list = ((TreeDragNDropData) supp.getTransferable().getTransferData(TreeDragNDropData.TreeDragNDropData_FLAVOR)).getDreaggedTreePaths();
                    for (int i = 0; i < list.length; i++) {
                        if (parent.getAddIndex((DnDNode) list[i].getLastPathComponent()) < 0) {
                            return false;
                        }
                    }

                    return true;
                } catch (UnsupportedFlavorException exception) {
                    // Don't allow dropping of other data types. As of right
                    // now,
                    // only DnDNode_FLAVOR and DnDTreeList_FLAVOR are supported.
                    exception.printStackTrace();
                } catch (IOException exception) {
                    // to make the compiler happy.
                    exception.printStackTrace();
                }
            }
        }
        // something prevented this import from going forward
        return false;
    }

    /**
     * @param supp
     * @return
     */
    @Override
    public boolean importData(final TransferSupport supp) {
        if (this.canImport(supp)) {
            // Fetch the data to transfer
            TreeDragNDropData dragNDropData = getDragNDropData(supp);
            TreePath[] importPaths = dragNDropData.getDreaggedTreePaths();
            // Fetch the drop location
            TreePath dropPath = getDropLocationTreePath(supp);
            // Insert the data at this location
            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            for (int i = 0; i < importPaths.length; i++) {
                DnDNode importNode = (DnDNode) dropPath.getLastPathComponent();
                DnDNode dropTargetNode = (DnDNode) importPaths[i].getLastPathComponent();
                treeModel.insertNodeInto(dropTargetNode, importNode, importNode.getAddIndex(dropTargetNode));
            }
            // success!
            return true;
        }
        // import isn't allowed at this time.
        return false;
    }

    /**
     * @param c
     * @param transferable
     * @param action
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable transferable, final int action) {
        if (action == TransferHandler.MOVE) {
            // get back the list of items that were transfered
            TreeDragNDropData dragNDropData = getDragNDropData(transferable);
            TreePath[] importTreePaths = dragNDropData.getDreaggedTreePaths();
            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            for (int i = 0; i < importTreePaths.length; i++) {
                // remove them
                DnDNode importTreeNode = (DnDNode) importTreePaths[i].getLastPathComponent();
                treeModel.removeNodeFromParent(importTreeNode);
            }
        }
    }

}