package de.imise.tool3lgm.graphtools.view.tree.node;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.imise.util.Alphabetical;

/**
 * @author N.N. (< 2005), AXS (08.04.2019)
 */
public class LGMTreeNode extends DefaultMutableTreeNode {

    private String visibleText = null;

    private final boolean sort;

    private boolean selectable = true;

    private Color foregroundColor = Color.black;

    // iconState = SHOW_NORMAL_ICON setzt in TreeRenderer das für den Node
    // spezifische Icon, sonst wird ein Error- oder Warning-Icon gesetzt
    public static final int SHOW_NORMAL_ICON = 0;
    public static final int SHOW_ERROR_ICON = 1;
    public static final int SHOW_WARNING_ICON = 2;
    private int iconState = SHOW_NORMAL_ICON;

    /**
     * @param o
     * @param setTreeNode
     */
    protected LGMTreeNode(final Object o) {
        this(o, true);
    }

    /**
     * @param o
     * @param sort
     */
    public LGMTreeNode(final Object o, final boolean sort) {
        this(o, null, sort);
    }

    /**
     * @param o
     * @param visibleText
     * @param sort
     */
    public LGMTreeNode(final Object o, final String visibleText, final boolean sort) {
        super(o);
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
        } else if (userObject != null) {
            return userObject.toString();
        }
        return "";
    }

    @Override
    public void insert(final MutableTreeNode newChild, int childIndex) {
        if (children != null && sort) {
            childIndex = Alphabetical.getInsertPosition((Vector<?>) children, newChild);
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
     * Gibt zurück, ob der Node selektierbar ist
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

        Set<Color> colors = new HashSet<>();
        if (retVal != Color.black) {
            colors.add(retVal);
        }

        //ACHTUNG: nicht auf eine Iterable-For-Schleife umstellen, weil es sonst in 1 von 10
        //Fällen beim Start aus den AWT-Klassen heraus eine java.util.ConcurrentModificationException
        //gibt!
        for (int i = 0; i < children.size(); i++) {
            Object tn = children.get(i);
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
