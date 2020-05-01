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

    /**
     * Draws the rectangle in the given Color if it is not <code>null</code>.
     * If it is <code>null</code> nothing happens. The rectangles width and height
     * are interpreted as the coordinates of the second point and not really the
     * width and height. So the width of
     *
     * @param g
     * @param rect with coordinates for the second point too instead of height and width
     * @param color
     */
    public static void drawPointRect(final Graphics g, final Rectangle rect, final Color color) {
        if (rect != null) {
            g.setColor(color);
            int x;
            int y;
            int w;
            int h;
            if (rect.x < rect.width) {
                x = rect.x;
                w = rect.width - x;
            } else {
                x = rect.width;
                w = rect.x - x;
            }
            if (rect.y < rect.height) {
                y = rect.y;
                h = rect.height - y;
            } else {
                y = rect.height;
                h = rect.y - y;
            }
            g.drawRect(x, y, w, h);
        }
    }

}
