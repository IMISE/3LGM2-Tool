package de.imise.tool3lgm.tools;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (8 Apr 2019)
 */
public class ElementClassTreeNode extends LGMTreeNode {

    /**
     * @param elementClass
     */
    public ElementClassTreeNode(final Class<? extends ModelElement> elementClass) {
        super(elementClass, ElementsNameBuilder.getDisplayableName(elementClass), true);
    }

    /**
     * @param elementClass
     * @param sort
     */
    public ElementClassTreeNode(final Class<? extends ModelElement> elementClass, final boolean sort) {
        super(elementClass, ElementsNameBuilder.getDisplayableName(elementClass), sort);
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