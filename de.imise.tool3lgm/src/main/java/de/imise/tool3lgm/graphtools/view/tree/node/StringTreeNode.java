package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

/**
 * @author AXS (8 Apr 2019)
 */
public class StringTreeNode extends LGMTreeNode {

    /**
     *
     */
    private ImageIcon icon;

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

    /**
     * @param s
     * @param icon
     */
    public StringTreeNode(final String s, final ImageIcon icon) {
        super(s);
        this.icon = icon;
    }

    /**
     * @param s
     * @param icon
     * @param sort
     */
    public StringTreeNode(final String s, final ImageIcon icon, final boolean sort) {
        super(s, sort);
        this.icon = icon;
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

    /**
     * @return
     */
    public ImageIcon getIcon() {
        return icon;
    }

}