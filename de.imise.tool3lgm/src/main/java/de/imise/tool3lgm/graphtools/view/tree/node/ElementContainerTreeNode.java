package de.imise.tool3lgm.graphtools.view.tree.node;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author AXS (8 Apr 2019)
 */
public class ElementContainerTreeNode extends LGMTreeNode implements GraphDocumentOwner {

    /**
     * @param ec
     * @param setTreeNode
     * @param sort
     */
    public ElementContainerTreeNode(final ElementContainer ec, final boolean setTreeNode, final boolean sort) {
        super(ec, sort);
        if (setTreeNode && ec instanceof NodeContainer) {
            ((NodeContainer) ec).setTreeNode(this);
        }
    }

    @Override
    public ElementContainer getUserObject() {
        return (ElementContainer) super.getUserObject();
    }

    @Override
    public void setUserObject(final Object userObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphDocument getGraphDocument() {
        ElementContainer ec = getUserObject();
        return ec == null ? null : ec.getGraphDocument();
    }

}