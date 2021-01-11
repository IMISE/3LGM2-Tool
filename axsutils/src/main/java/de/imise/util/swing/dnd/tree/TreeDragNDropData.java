package de.imise.util.swing.dnd.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.tree.TreePath;

/**
 * @author helloworld922
 *         <p>
 * @version 1.0
 *          <p>
 *          copyright 2010 <br>
 *          You are welcome to use/modify this code for any purposes you want so
 *          long as credit is given to me.
 */
public class TreeDragNDropData implements Transferable, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1270874212613332692L;
    /**
     * Data flavor that allows a DnDTreeList to be extracted from a transferable
     * object
     */
    public final static DataFlavor TreeDragNDropData_FLAVOR = new DataFlavor(TreeDragNDropData.class, "Tree drag and drop data");
    /**
     * List of flavors this DnDTreeList can be retrieved as. Currently only
     * supports DnDTreeList_FLAVOR
     */
    protected static DataFlavor[] flavors = {
            TreeDragNDropData.TreeDragNDropData_FLAVOR
    };

    /**
     * Nodes to transfer
     */
    private final TreePath[] draggedTreePaths;

    /**
     * @param sourceTree
     * @param draggedTreePaths
     */
    public TreeDragNDropData(final TreePath[] draggedTreePaths) {
        this.draggedTreePaths = draggedTreePaths;
    }

    /**
     * @return
     */
    public TreePath[] getDreaggedTreePaths() {
        return draggedTreePaths;
    }

    /**
     * @param flavor
     * @return
     * @throws UnsupportedFlavorException
     * @throws IOException
     **/
    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * @return
     **/
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return TreeDragNDropData.flavors;
    }

    /**
     * @param flavor
     * @return
     **/
    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        DataFlavor[] flavs = getTransferDataFlavors();
        for (int i = 0; i < flavs.length; i++) {
            if (flavs[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

}