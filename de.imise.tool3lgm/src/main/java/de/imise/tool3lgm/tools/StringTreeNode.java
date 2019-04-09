package de.imise.tool3lgm.tools;

/**
 * @author AXS (8 Apr 2019)
 */
public class StringTreeNode extends LGMTreeNode {

    /**
     * @param s
     */
    public StringTreeNode(final String s) {
        super(s);
    }

    /**
     * @param s
     * @param sort
     */
    public StringTreeNode(final String s, final boolean sort) {
        super(s, sort);
    }

    @Override
    public String getUserObject() {
        return (String) super.getUserObject();
    }

    @Override
    public void setUserObject(final Object userObject) {
        if (userObject instanceof String) {
            super.setUserObject(userObject);
        } else {
            throw new UnsupportedOperationException();
        }
    }

}