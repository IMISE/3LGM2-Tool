package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (8 Apr 2019)
 */
public class ElementClassTreeNode extends IconifiedTreeNode<Class<? extends ModelElement>> {

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
        super(elementClass, label, true, icon);
    }

    /**
     * @param elementClass
     * @param label
     * @param sort
     * @param icon
     */
    public ElementClassTreeNode(final Class<? extends ModelElement> elementClass, final String label, final boolean sort, final ImageIcon icon) {
        super(elementClass, label, sort, icon);
    }

    @Override
    public Class<? extends ModelElement> getUserObject() {
        return super.getUserObject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setUserObject(final Object userObject) {
        setUserObject((Class<? extends ModelElement>) userObject); //hard cast! if this is not such class, so don't call this function!
    }

    /**
     * @param userObject
     */
    public void setUserObject(final Class<? extends ModelElement> userObject) {
        super.setUserObject(userObject);
    }

}