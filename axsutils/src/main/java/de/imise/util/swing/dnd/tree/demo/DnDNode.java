package de.imise.util.swing.dnd.tree.demo;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @author helloworld922
 *         <p>
 * @version 1.0
 *          <p>
 *          copyright 2010 <br>
 *          You are welcome to use/modify this code for any purposes you want so
 *          long as credit is given to me.
 */
public class DnDNode extends DefaultMutableTreeNode implements Transferable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4816704492774592665L;

    /**
     * data flavor used to get back a DnDNode from data transfer
     */
    public static final DataFlavor DnDNode_FLAVOR = new DataFlavor(DnDNode.class, "Drag and drop Node");

    /**
     * list of all flavors that this DnDNode can be transfered as
     */
    protected static DataFlavor[] flavors = {
            DnDNode.DnDNode_FLAVOR
    };

    /**
     *
     */
    public DnDNode() {
        super();
    }

    /**
     * Constructs
     *
     * @param data
     */
    public DnDNode(final Serializable data) {
        super(data);
    }

    /**
     * Determines if we can add a certain node as a child of this node.
     *
     * @param node
     * @return
     */
    public boolean canAdd(final DnDNode node) {
        if (node != null) {
            if (!equals(node.getParent())) {
                if (!equals(node)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the index node should be inserted at to maintain sorted order. Also
     * performs checking to see if that node can be added to this node. By
     * default, DnDNode adds children at the end.
     *
     * @param node
     * @return the index to add at, or -1 if node can not be added
     */
    public int getAddIndex(final DnDNode node) {
        if (!canAdd(node)) {
            return -1;
        }
        return getChildCount();
    }

    /**
     * Checks this node for equality with another node. To be equal, this node
     * and all of it's children must be equal. Note that the parent/ancestors do
     * not need to match at all.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof DnDNode)) {
            return false;
        } else if (!equalsNode((DnDNode) o)) {
            return false;
        } else if (getChildCount() != ((DnDNode) o).getChildCount()) {
            return false;
        }
        // compare all children
        for (int i = 0; i < getChildCount(); i++) {
            TreeNode child = getChildAt(i);
            TreeNode otherChild = ((DnDNode) o).getChildAt(i);
            if (!child.equals(otherChild)) {
                return false;
            }
        }
        // they are equal!
        return true;
    }

    /**
     * Compares if this node is equal to another node. In this method, children
     * and ancestors are not taken into concideration.
     *
     * @param node
     * @return
     */
    public boolean equalsNode(final DnDNode node) {
        if (node != null) {
            if (getAllowsChildren() == node.getAllowsChildren()) {
                if (getUserObject() != null) {
                    if (getUserObject().equals(node.getUserObject())) {
                        return true;
                    }
                } else {
                    if (node.getUserObject() == null) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        return DnDNode.flavors;
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

    /**
     * @param temp
     * @return
     */
    public int indexOfNode(final DnDNode node) {
        if (node == null) {
            throw new NullPointerException();
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i).equals(node)) {
                    return i;
                }
            }
            return -1;
        }
    }
}