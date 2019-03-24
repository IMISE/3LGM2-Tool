package de.imise.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;

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

    public static void add(final Container con, final Component c, final GridBagConstraints gbc, final int x, final int y, final int w, final int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        con.add(c, gbc);
    }

}
