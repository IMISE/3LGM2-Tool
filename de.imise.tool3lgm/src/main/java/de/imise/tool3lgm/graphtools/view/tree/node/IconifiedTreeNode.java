package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

/**
 * @author AXS (15.06.2020)
 */
public class IconifiedTreeNode<T> extends LGMTreeNode<T> {

    /**
     * @author AXS (15.06.2020)
     */
    public enum IconState {
        // iconState = SHOW_NORMAL_ICON setzt in TreeRenderer das für den Node
        // spezifische Icon, sonst wird ein Error- oder Warning-Icon gesetzt
        SHOW_NORMAL_ICON,
        SHOW_ERROR_ICON,
        SHOW_WARNING_ICON,
    }

    private IconState iconState;

    /**
    *
    */
    private ImageIcon icon;

    /**
     * @param o
     */
    public IconifiedTreeNode(final T o) {
        this(o, null);
    }

    /**
     * @param o
     * @param sort
     */
    public IconifiedTreeNode(final T o, final boolean sort) {
        this(o, sort, null);
    }

    /**
     * @param o
     * @param visibleText
     * @param sort
     */
    public IconifiedTreeNode(final T o, final String visibleText, final boolean sort) {
        this(o, visibleText, sort, null);
    }

    /**
     * @param o
     * @param icon
     */
    public IconifiedTreeNode(final T o, final ImageIcon icon) {
        super(o);
        this.icon = icon;
    }

    /**
     * @param o
     * @param sort
     * @param icon
     */
    public IconifiedTreeNode(final T o, final boolean sort, final ImageIcon icon) {
        super(o, sort);
        this.icon = icon;
    }

    /**
     * @param o
     * @param visibleText
     * @param sort
     * @param icon
     */
    public IconifiedTreeNode(final T o, final String visibleText, final boolean sort, final ImageIcon icon) {
        super(o, visibleText, sort);
        this.icon = icon;
    }

    /**
     * @return
     */
    public final IconState getIconState() {
        return iconState;
    }

    /**
     * @param state
     */
    public final void setIconState(final IconState iconState) {
        this.iconState = iconState;
    }

    /**
     * @return the icon
     */
    public final ImageIcon getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public final void setIcon(final ImageIcon icon) {
        this.icon = icon;
    }

}
