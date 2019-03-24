package de.imise.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;

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

    public static void add(final Container con, final Component c, final GridBagConstraints gbc, final int x, final int y, final int w, final int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        con.add(c, gbc);
    }

}
