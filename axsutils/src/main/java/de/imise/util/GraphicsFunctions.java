package de.imise.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author AXS (27.02.2020)
 */
public class GraphicsFunctions {

    /**
     * Draws the rectangle in the given Color if it is not <code>null</code>.
     * If it is <code>null</code> nothing happens.
     *
     * @param g
     * @param rect
     * @param color
     */
    public static void drawRect(final Graphics g, final Rectangle rect, final Color color) {
        if (rect != null) {
            g.setColor(color);
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
    }

}
