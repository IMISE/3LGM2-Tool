package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

/**
 * @author AXS (8 Apr 2019)
 */
public class StringTreeNode extends IconifiedTreeNode<String> {

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
        super(s, icon);
    }

    /**
     * @param s
     * @param icon
     * @param sort
     */
    public StringTreeNode(final String s, final ImageIcon icon, final boolean sort) {
        super(s, sort, icon);
    }

    @Override
    public String getUserObject() {
        return super.getUserObject();
    }

    @Override
    public void setUserObject(final Object userObject) {
        String s = String.valueOf(userObject);
        setUserObject(s);
    }

}