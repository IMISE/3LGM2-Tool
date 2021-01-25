package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author AXS (8 Apr 2019)
 */
public class ElementContainerTreeNode extends IconifiedTreeNode<ElementContainer> implements GraphDocumentOwner {

    /**
     *
     */
    private final boolean equalsIfSameElementContainer;

    /**
     * @param ec
     * @return
     */
    public static ElementContainerTreeNode createDialogTreeNode(final ElementContainer ec) {
        return new ElementContainerTreeNode(ec, false, false, true);
    }

    /**
     * @param ec
     * @param index
     * @return
     */
    public static ElementContainerTreeNode createIndexedDialogTreeNode(final ElementContainer ec, final int index) {
        ElementContainerTreeNode elementContainerTreeNode = new ElementContainerTreeNode(ec, false, false, null, false, null);
        elementContainerTreeNode.setText(index);
        return elementContainerTreeNode;
    }

    /**
     * @param ec
     * @return
     */
    public static ElementContainerTreeNode createModelBrowserTreeNode(final ElementContainer ec) {
        return new ElementContainerTreeNode(ec, true, true, false);
    }

    /**
     * @param ec
     * @param sortChildren
     * @return
     */
    public static ElementContainerTreeNode createDialogRootTreeNode(final ElementContainer ec, final boolean sortChildren) {
        //equalsIfSameElementContainer doesn't matter
        return new ElementContainerTreeNode(ec, false, true, sortChildren);
    }

    /**
     * @param ec
     * @param equalsIfSameElementContainer
     * @param setTreeNode
     * @param sort
     */
    public ElementContainerTreeNode(final ElementContainer ec, final boolean equalsIfSameElementContainer, final boolean setTreeNode, final boolean sort) {
        this(ec, equalsIfSameElementContainer, setTreeNode, sort, null);
    }

    /**
     * @param ec
     * @param equalsIfSameElementContainer
     * @param setTreeNode
     * @param sort
     * @param icon
     */
    public ElementContainerTreeNode(final ElementContainer ec, final boolean equalsIfSameElementContainer, final boolean setTreeNode, final boolean sort, final ImageIcon icon) {
        this(ec, equalsIfSameElementContainer, setTreeNode, null, sort, icon);
    }

    /**
     * @param ec
     * @param equalsIfSameElementContainer
     * @param setTreeNode
     * @param visibleText
     * @param sort
     * @param icon
     */
    protected ElementContainerTreeNode(final ElementContainer ec, final boolean equalsIfSameElementContainer, final boolean setTreeNode, final String visibleText, final boolean sort, final ImageIcon icon) {
        super(ec, visibleText, sort, icon);
        if (setTreeNode) {
            setTreeNode(ec);
        }
        this.equalsIfSameElementContainer = equalsIfSameElementContainer;
    }

    /**
     * @return
     */
    public ModelElement getModelElement() {
        ElementContainer ec = getUserObject();
        return ec.getElement();
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

    @Override
    public boolean equals(final Object obj) {
        if (!equalsIfSameElementContainer) {
            return this == obj;
        }
        return super.equals(obj);
    }

    /**
     * @param ec
     * @return
     */
    private String getIndexedText(final int index) {
        return "[" + (index + 1) + "] " + userObject;
    }

    /**
     * @param index
     */
    public void setText(final int index) {
        String indexedText = getIndexedText(index);
        setText(indexedText);
    }

}