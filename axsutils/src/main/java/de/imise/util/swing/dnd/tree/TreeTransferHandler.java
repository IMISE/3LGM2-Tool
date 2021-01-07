package de.imise.util.swing.dnd.tree;

import java.awt.Component;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class TreeTransferHandler extends TransferHandler {

    /**
     * Stores the component where the last drag started
     */
    public static Component currentDragSource = null;

    /**
     *
     */
    private static final TreeDragNDropData EMPTY_DRAG_N_DROP_DATA = new TreeDragNDropData(new TreePath[0]);

    /**
     * Using tree models allows us to add/remove nodes from a tree and pass the
     * appropriate messages.
     */
    protected JTree tree;
    /**
     *
     */
    private static final long serialVersionUID = -6851440217837011463L;

    /**
     *
     */
    public TreeTransferHandler(final JTree tree) {
        this.tree = tree;
    }

    /**
     * @param transferable
     * @return
     */
    protected static TreeDragNDropData getDragNDropData(final Transferable transferable) {
        // Fetch the data to transfer
        try {
            TreeDragNDropData transferData = (TreeDragNDropData) transferable.getTransferData(TreeDragNDropData.TreeDragNDropData_FLAVOR);
            return transferData;
        } catch (Exception e) {
            // ignore
        }
        return EMPTY_DRAG_N_DROP_DATA;
    }

    /**
     * @param supp
     * @return
     */
    protected static TreeDragNDropData getDragNDropData(final TransferSupport supp) {
        Transferable transferable = supp.getTransferable();
        return getDragNDropData(transferable);
    }

    /**
     * @param supp
     * @return
     */
    protected boolean isDragOnSourceTree(final TransferSupport supp) {
        Component currrentDragTarget = supp.getComponent();
        return currentDragSource == currrentDragTarget;
        //        try {
        //            Component component = supp.getComponent();
        //            if (component instanceof JTree) {
        //                JTree tree = (JTree) component;
        //                TreeModel model = tree.getModel();
        //                Object modelRoot = model.getRoot();
        //                TreeDragNDropData dragNDropData = getDragNDropData(supp);
        //                TreePath[] transferedTreePaths = dragNDropData.getDreaggedTreePaths();
        //                TreePath firstTransferedTreePath = transferedTreePaths[0];
        //                Object treePathRoot = firstTransferedTreePath.getPathComponent(0);
        //                return modelRoot.equals(treePathRoot);
        //            }
        //        } catch (Exception e) {
        //            //ignore
        //        }
        //        return false;
    }

    @Override
    public int getSourceActions(final JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    /**
     * @param dragSouce
     * @return null if no nodes were selected, or this transfer handler was not
     *         added to a DnDJTree. I don't think it's possible because of the
     *         constructor layout, but one more layer of safety doesn't matter.
     */
    @Override
    protected Transferable createTransferable(final JComponent dragSouce) {
        currentDragSource = dragSouce;
        if (dragSouce instanceof JTree) {
            JTree tree = (JTree) dragSouce;
            TreePath[] selectionPaths = tree.getSelectionPaths();
            return new TreeDragNDropData(selectionPaths);
        } else {
            return null;
        }
    }

    /**
     * @param supp
     * @return
     */
    protected TreePath getDropLocationTreePath(final TransferSupport supp) {
        javax.swing.JTree.DropLocation dropLocation = (javax.swing.JTree.DropLocation) supp.getDropLocation();
        TreePath dropPath = dropLocation.getPath();
        if (dropPath == null) {
            JTree tree = (JTree) supp.getComponent();
            TreeModel treeModel = tree.getModel();
            Object root = treeModel.getRoot();
            dropPath = new TreePath(root);
        }
        return dropPath;
    }

}