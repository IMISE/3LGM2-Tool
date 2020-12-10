package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author AXS (8 Apr 2019)
 */
public class ElementContainerTreeNode extends IconifiedTreeNode<ElementContainer> implements GraphDocumentOwner {

    /**
     * @param ec
     * @param setTreeNode
     * @param sort
     */
    public ElementContainerTreeNode(final ElementContainer ec, final boolean setTreeNode, final boolean sort) {
        this(ec, setTreeNode, sort, null);
    }

    /**
     * @param ec
     * @param setTreeNode
     * @param visibleText
     * @param sort
     */
    public ElementContainerTreeNode(final ElementContainer ec, final boolean setTreeNode, final String visibleText, final boolean sort) {
        this(ec, setTreeNode, visibleText, sort, null);
    }

    /**
     * @param ec
     * @param setTreeNode
     * @param sort
     * @param icon
     */
    public ElementContainerTreeNode(final ElementContainer ec, final boolean setTreeNode, final boolean sort, final ImageIcon icon) {
        this(ec, setTreeNode, null, sort, icon);
    }

    /**
     * @param ec
     * @param setTreeNode
     * @param visibleText
     * @param sort
     * @param icon
     */
    public ElementContainerTreeNode(final ElementContainer ec, final boolean setTreeNode, final String visibleText, final boolean sort, final ImageIcon icon) {
        super(ec, visibleText, sort, icon);
        if (setTreeNode) {
            setTreeNode(ec);
        }
    }

    @Override
    public ElementContainer getUserObject() {
        return super.getUserObject();
    }

    @Override
    public GraphDocument getGraphDocument() {
        ElementContainer ec = getUserObject();
        return ec == null ? null : ec.getGraphDocument();
    }

    @Override
    public void setUserObject(final Object userObject) {
        setUserObject((ElementContainer) userObject); //hard cast! if this is an ElementContainer, so don't call this function!
    }

    /**
     * @param userObject
     */
    public void setUserObject(final ElementContainer userObject) {
        super.setUserObject(userObject);
    }

    /**
     * @param ec
     */
    public void setTreeNode(final ElementContainer ec) {
        if (ec instanceof NodeContainer) {
            ((NodeContainer) ec).setTreeNode(this);
        }
    }

}