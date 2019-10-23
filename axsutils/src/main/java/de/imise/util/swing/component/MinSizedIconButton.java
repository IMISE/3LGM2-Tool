package de.imise.util.swing.component;

import java.awt.Dimension;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Button, dessen PreferredSize, MinSize und MaxSize genau die MaxSize des vorhandenen Icons oder eines übergebenen Wertes ist. Dadurch nehmen die
 * Buttons in einer wahlweise beiden Dimensionen nur soviel Platz, wie sie tatsächlich brauchen.
 *
 * @author AXS (26 Mar 2019)
 */
public class MinSizedIconButton extends JButton {

    private Dimension realDimension = null;

    private Dimension minDimension = null;

    private MinSizedIconButton(final Action a, final int minWidth, final int minHeight) {
        super(a);
        minDimension = new Dimension(minWidth, minHeight);
    }

    /**
     * Legt einen Button mit der übergeben Action an. Die Breite des Buttons wird durch das in der Action enthaltene Icon bestimmt. Die Höhe ist
     * immer das Maximum aus der Höhe des Icons und der übergebenen minHeight - also mindestens die minHeight.
     *
     * @param a
     *            Action des Buttons. Diese sollte ein Icon enthalten, damit der Button sich korrekt resizen kann. Hat er das nicht, wird die
     *            super.getPreferredSize() als Größe gesetzt.
     * @param minHeight
     *            minimale Höhe des Buttons (wenn das Icon nicht höher ist)
     * @return
     */
    public static JButton createLimitedWidthButton(final Action a, final int minHeight) {
        return new MinSizedIconButton(a, -1, minHeight);
    }

    /**
     * Legt einen Button mit der übergeben Action an. Die Höhe des Buttons wird durch das in der Action enthaltene Icon bestimmt. Die Breite ist
     * immer das Maximum aus der Breite des Icons und der übergebenen minWidth - also mindestens die minWidth.
     *
     * @param a
     *            Action des Buttons. Diese sollte ein Icon enthalten, damit der Button sich korrekt resizen kann. Hat er das nicht, wird die
     *            super.getPreferredSize() als Größe gesetzt.
     * @param minWitdh
     *            minimale Breite des Buttons (wenn das Icon nicht breiter ist)
     * @return
     */
    public static JButton createLimitedHeightButton(final Action a, final int minWitdh) {
        return new MinSizedIconButton(a, -1, minWitdh);
    }

    /**
     * Legt einen Button mit der übergeben Action an. Die Breite und Höhe des Buttons werden durch das in der Action enthaltene Icon bestimmt. Die
     * Breite/Höhe ist immer das Maximum aus der Breite/Höhe des Icons und der übergebenen minWidth/minHeight - also mindestens diese übergebenen
     * Werte.
     *
     * @param a
     *            Action des Buttons. Diese sollte ein Icon enthalten, damit der Button sich korrekt resizen kann. Hat er das nicht, wird die
     *            super.getPreferredSize() als Größe gesetzt.
     * @param minWitdh
     *            minimale Breite des Buttons (wenn das Icon nicht breiter ist)
     * @param minHeight
     *            minimale Höhe des Buttons (wenn das Icon nicht höher ist)
     * @return
     */
    public static JButton createLimitedWidthButton(final Action a, final int minWidth, final int minHeight) {
        return new MinSizedIconButton(a, minWidth, minHeight);
    }

    /**
     * Legt einen Button mit der übergeben Action an. Die Breite und Höhe des Buttons werden durch das in der Action enthaltene Icon bestimmt
     *
     * @param a
     *            Action des Buttons. Diese sollte ein Icon enthalten, damit der Button sich korrekt resizen kann. Hat er das nicht, wird die
     *            super.getPreferredSize() als Größe gesetzt.
     * @return
     */
    public static JButton createLimitedWidthAndHeigthButton(final Action a) {
        return new MinSizedIconButton(a, -1, -1);
    }

    @Override
    public void setAction(final Action a) {
        realDimension = null;
        super.setAction(a);
    }

    @Override
    public Dimension getPreferredSize() {
        if (realDimension != null) {
            return realDimension;
        }
        Action action = getAction();
        if (action != null) {
            Icon icon = (Icon) action.getValue(AbstractAction.SMALL_ICON);
            if (icon != null) {
                realDimension = new Dimension(Math.max(icon.getIconWidth(), minDimension.width), Math.max(icon.getIconHeight(), minDimension.height));
            }
        }
        if (realDimension == null) { // wurde oben nicht gesetzt
            realDimension = super.getPreferredSize();
            realDimension = new Dimension(Math.max(realDimension.width, minDimension.width), Math.max(realDimension.height, minDimension.height));
        }
        return realDimension;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

}