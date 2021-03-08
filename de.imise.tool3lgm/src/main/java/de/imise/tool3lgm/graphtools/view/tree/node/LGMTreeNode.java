package de.imise.tool3lgm.graphtools.view.tree.node;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.util.swing.component.tree.TypedTreeNode;

/**
 * @author N.N. (< 2005), AXS (08.04.2019)
 */
public class LGMTreeNode<T> extends TypedTreeNode<T> {

    /**
     * If this error is set, this node is rendered in a different way. The way
     * depends on whether the element of this node is the faulty element or an
     * element to solve the error.
     */
    private AbstractConsistencyError consistencyError;

    /** selectable yes/no */
    private boolean selectable = true;

    /** The color of the font */
    private Color foregroundColor = Color.black;

    /**
     * @param o
     * @param setTreeNode
     */
    protected LGMTreeNode(final T o) {
        super(o);
    }

    /**
     * @param o
     * @param sort
     */
    public LGMTreeNode(final T o, final boolean sort) {
        super(o, sort);
    }

    /**
     * @param o
     * @param visibleText
     * @param sort
     */
    public LGMTreeNode(final T o, final String visibleText, final boolean sort) {
        super(o, visibleText, sort);
    }

    /**
     * Setzt die Selektierbar-Eigenschaft des Knotens und der Kinder.
     *
     * @param s
     */
    public void setSelectable(final boolean s) {
        selectable = s;
        for (int i = 0; i < getChildCount(); i++) {
            ((LGMTreeNode<?>) getChildAt(i)).setSelectable(s);
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
     * @return Returns the color.
     */
    public Color getForegroundColor() {
        return foregroundColor;
    }

    /**
     * @param color The color to set.
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
            LGMTreeNode<?> child = (LGMTreeNode<?>) tn;
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

    /**
     * @return the consistencyError
     */
    public final AbstractConsistencyError getConsistencyError() {
        return consistencyError;
    }

    /**
     * @param consistencyError the consistencyError to set
     */
    public final void setConsistencyError(final AbstractConsistencyError consistencyError) {
        this.consistencyError = consistencyError;
    }

    @Override
    public LGMTreeNode<?> getChildAt(final int index) {
        return (LGMTreeNode<?>) super.getChildAt(index);
    }
}
