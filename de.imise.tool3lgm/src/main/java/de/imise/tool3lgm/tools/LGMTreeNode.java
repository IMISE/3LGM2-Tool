package de.imise.tool3lgm.tools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.Alphabetical;

/**
 * @author N.N.
 */
public class LGMTreeNode extends DefaultMutableTreeNode {

    private final ArrayList<Object> userObjects = new ArrayList<Object>(1);
    private String visibleText = null;
    private final boolean sort;
    private boolean selectable = true;
    private boolean actionByDoubleClick = false;
    private Color foregroundColor = Color.black;

    // iconState = SHOW_NORMAL_ICON setzt in TreeRenderer das für den Knoten
    // spezifische Icon,
    // sonst wird ein Error- oder Warning-Icon gesetzt
    public static final int SHOW_NORMAL_ICON = 0;
    public static final int SHOW_ERROR_ICON = 1;
    public static final int SHOW_WARNING_ICON = 2;
    private int iconState = SHOW_NORMAL_ICON;

    /**
     * @param o
     * @param setTreeNode
     * @param sort
     */
    public LGMTreeNode(final Object o, final boolean setTreeNode, final boolean sort) {
        super();
        userObjects.add(o);
        if (setTreeNode && o instanceof NodeContainer) {
            ((NodeContainer) o).setTreeNode(this);
        }
        this.sort = sort;
    }

    /**
     * @param o
     * @param setTreeNode
     */
    public LGMTreeNode(final Object o, final boolean setTreeNode) {
        this(o, setTreeNode, true);
    }

    /**
     * @param o
     * @param visibleText
     * @param sort
     */
    public LGMTreeNode(final Object o, final String visibleText, final boolean sort) {
        super();
        userObjects.add(o);
        this.visibleText = visibleText;
        this.sort = sort;
    }

    /**
     * @param text
     */
    public void setText(final String text) {
        visibleText = text;
    }

    /**
     * @return
     */
    public String getText() {
        return visibleText;
    }

    @Override
    public String toString() {
        if (visibleText != null) {
            return visibleText;
        } else if (userObjects.get(0) != null) {
            return userObjects.get(0).toString();
        } else {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void insert(final MutableTreeNode newChild, int childIndex) {
        if (children != null && sort) {
            childIndex = Alphabetical.getInsertPosition(children, newChild);
        }
        super.insert(newChild, childIndex);
    }

    /**
     * Setzt die Selektierbar-Eigenschaft des Knotens und der Kinder.
     * 
     * @param s
     */
    public void setSelectable(final boolean s) {
        selectable = s;
        for (int i = 0; i < getChildCount(); i++) {
            ((LGMTreeNode) getChildAt(i)).setSelectable(s);
        }
    }

    /**
     * Gibt zurück, ob der Knoten selektierbar ist
     * 
     * @return
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * @return
     */
    public int getIconState() {
        return iconState;
    }

    /**
     * @param i
     */
    public void setIconState(final int i) {
        iconState = i;
    }

    /**
     * Leert die arrayList <code>userObjects</code> und fügt das übergebene
     * Object ein.
     * 
     * @see javax.swing.tree.DefaultMutableTreeNode#setUserObject(java.lang.Object)
     */
    @Override
    public void setUserObject(final Object o) {
        userObjects.clear();
        userObjects.add(o);
    }

    /**
     * @param o
     * @param index
     */
    public void setUserObject(final Object o, final int index) {
        if (index < userObjects.size()) {
            userObjects.set(index, o);
        } else {
            while (index > userObjects.size()) {
                userObjects.add(null);
            }
            userObjects.add(o);
        }
    }

    /*
     * gibt das erste Element aus der ArrayList <code>userObjects</code> zurück.
     * @see javax.swing.tree.DefaultMutableTreeNode#getUserObject()
     */
    @Override
    public Object getUserObject() {
        return userObjects.size() == 0 ? null : userObjects.get(0);
    }

    /**
     * Gibt das <code>userObject</code> an der Stelle <code>index</code> aus der
     * ArrayList <code>userObjects</code> zurück.
     * 
     * @param index
     * @return
     */
    public Object getUserObject(final int index) {
        return userObjects.size() <= index ? null : userObjects.get(index);
    }

    /**
     * @return
     */
    public boolean isActionByDoubleClick() {
        return actionByDoubleClick;
    }

    /**
     * @param b
     */
    public void setActionByDoubleClick(final boolean b) {
        actionByDoubleClick = b;
    }

    /**
     * @return Returns the color.
     */
    public Color getForegroundColor() {
        return foregroundColor;
    }

    /**
     * @param color
     *            The color to set.
     */
    public void setForegroundColor(final Color color) {
        foregroundColor = color;
    }

    /**
     * @return Returns the signalColor.
     */
    public Color getSignalColor() {
        Color retVal = getForegroundColor();
        if (children == null || children.size() == 0) {
            return retVal;
        }

        HashSet<Color> colors = new HashSet<Color>();
        if (retVal != Color.black) {
            colors.add(retVal);
        }

        for (Object tn : children) {
            LGMTreeNode child = (LGMTreeNode) tn;
            Color c = child.isLeaf() ? child.getForegroundColor() : child.getSignalColor();
            if (!c.equals(Color.black) && !colors.contains(c)) {
                colors.add(c);
            }
        }
        int r = 0, g = 0, b = 0;
        for (Color c : colors) {
            r += c.getRed();
            g += c.getGreen();
            b += c.getBlue();
        }
        int numCol = colors.size();
        if (numCol > 0) {
            retVal = new Color(r / numCol, g / numCol, b / numCol);
        }

        return retVal;
    }
}
