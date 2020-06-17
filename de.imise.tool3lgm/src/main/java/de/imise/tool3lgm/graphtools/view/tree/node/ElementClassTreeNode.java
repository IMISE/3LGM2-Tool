package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (8 Apr 2019)
 */
public class ElementClassTreeNode extends IconifiedTreeNode {

    /**
     * @param elementClass
     * @param label
     */
    public ElementClassTreeNode(final Class<? extends ModelElement> elementClass, final String label) {
        super(elementClass, label, true);
    }

    /**
     * @param elementClass
     * @param label
     * @param sort
     */
    public ElementClassTreeNode(final Class<? extends ModelElement> elementClass, final String label, final boolean sort) {
        super(elementClass, label, sort);
    }

    /**
     * @param elementClass
     * @param label
     * @param icon
     */
    public ElementClassTreeNode(final Class<? extends ModelElement> elementClass, final String label, final ImageIcon icon) {
        super(elementClass, label, true);
    }

    /**
     * @param elementClass
     * @param label
     * @param sort
     * @param icon
     */
    public ElementClassTreeNode(final Class<? extends ModelElement> elementClass, final String label, final boolean sort, final ImageIcon icon) {
        super(elementClass, label, sort);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement> getUserObject() {
        return (Class<? extends ModelElement>) super.getUserObject();
    }

    @Override
    public void setUserObject(final Object userObject) {
        throw new UnsupportedOperationException();
    }

}