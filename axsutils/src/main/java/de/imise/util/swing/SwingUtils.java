package de.imise.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.imise.util.StringUtils;

/**
 * @author AXS (20 Mar 2019)
 */
public class SwingUtils {

    public static final void setSamePreferredSize(final JComponent... components) {
        if (components == null || components.length == 0) {
            return;
        }
        Dimension dim = components[0].getPreferredSize();
        for (int i = 1; i < components.length; i++) {
            Dimension otherDim = components[i].getPreferredSize();
            dim.width = Math.max(dim.width, otherDim.width);
            dim.height = Math.max(dim.height, otherDim.height);
        }
        for (int i = 0; i < components.length; i++) {
            components[i].setPreferredSize(dim);
        }
    }

    /**
     * Macht die Texte in beiden Labels gleich lang. Dabei werden die Fontmetrics abgefragt und an den kürzeren Text der beiden Labels so viele
     * Leerzeichen angehängt, dass er nahezu gleich lang ist, wie der längere. In dieser Form passt das wahrscheinlich nur bei Labels mit der
     * Ausrichtung WEST.
     *
     * @param label1
     * @param label2
     */
    public static void fillToSameLength(final JLabel label1, final JLabel label2) {
        FontMetrics fontMetrics1 = label1.getFontMetrics(label1.getFont());
        FontMetrics fontMetrics2 = label2.getFontMetrics(label2.getFont());
        String text1 = label1.getText();
        String text2 = label2.getText();
        int width1 = SwingUtilities.computeStringWidth(fontMetrics1, text1);
        int width2 = SwingUtilities.computeStringWidth(fontMetrics2, text2);
        if (width1 == width2) {
            return;
        }
        if (width1 > width2) {
            fillToSameLength(label2, label1);
            return;
        }
        int spaceWidth1 = SwingUtilities.computeStringWidth(fontMetrics1, " ");
        int diff = width2 - width1;
        int count = diff / spaceWidth1;
        text1 += StringUtils.fillToLenght("", count);
        label1.setText(text1);
    }

    public static void fillToSameLength(final JLabel... labels) {
        if (labels.length == 0) {
            return;
        }
        JLabel largest = null;
        int largestWidth = -1;
        for (JLabel label : labels) {
            FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
            String text = label.getText();
            int width = SwingUtilities.computeStringWidth(fontMetrics, text);
            if (width > largestWidth) {
                largestWidth = width;
                largest = label;
            }
        }
        for (JLabel label : labels) {
            if (label != largest) {
                fillToSameLength(largest, label);
            }
        }
    }

    public static void add(final Container con, final Component c, final GridBagConstraints gbc, final int x, final int y, final int w, final int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        con.add(c, gbc);
    }

    /**
     * Button, dessen PreferredSize, MinSize und MaxSize genau die MaxSize des vorhandenen Icons oder eines übergebenen Wertes ist. Dadurch nehmen die
     * Buttons in einer wahlweise beiden Dimensionen nur soviel Platz, wie sie tatsächlich brauchen.
     *
     * @author AXS (26 Mar 2019)
     */
    public static class MinSizedIconButton extends JButton {

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

}
