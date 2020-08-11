package de.imise.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import de.imise.util.StringUtils;

/**
 * Collection of static functions for Swing components
 *
 * @author AXS (20 Mar 2019)
 */
public class SwingUtils {

    /**
     * Sets all the given components to the same size. The size is given by the largest width and height of the given components.
     *
     * @param components
     */
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
     * Sets the length of the text of the given labels to the same length by adding whitespaces to the shorter one until it has as near as possible
     * the same size as the greater one. The length is computed by the {@link FontMetrics}. This function possibly makes only sense in combination
     * with labes with the alignment {@link SwingConstants#WEST}.
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
        text1 += StringUtils.fillToMinLenght("", count);
        label1.setText(text1);
    }

    /**
     * Sets the length of the text of the given labels to the same length by adding whitespaces to the shorter labels until they have as near as
     * possible the same size as the label with the largest text. The length is computed by the {@link FontMetrics}. This function possibly makes only
     * sense in combination with labes with the alignment {@link SwingConstants#WEST}.
     *
     * @param labels
     */
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

    /**
     * Convinience function to add a component to a container with specific {@link GridBagConstraints}.
     *
     * @param container the container which gets the component added
     * @param component the component to add
     * @param gbc the {@link GridBagConstraints} used to add the component to the container
     * @param x the gridx value that will be set to the constarints before the add
     * @param y the gridy value that will be set to the constarints before the add
     * @param w the gridwidth value that will be set to the constarints before the add
     * @param h the gridheight value that will be set to the constarints before the add
     */
    public static void add(final Container container, final Component component, final GridBagConstraints gbc, final int x, final int y, final int w, final int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        container.add(component, gbc);
    }

    /**
     * @param x
     * @param y
     * @param w
     * @return
     */
    public static boolean canDisplayFrameAtCoordinates(final int x, final int y, final int w) {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        JFrame frame = new JFrame();
        frame.pack();
        Insets frameInsets = frame.getInsets();
        int h = frameInsets.top; //title bar height
        for (GraphicsDevice screen : screens) {
            GraphicsConfiguration screenConfiguration = screen.getDefaultConfiguration();
            Rectangle screenBounds = screenConfiguration.getBounds();
            if (screenBounds.contains(x, y) || screenBounds.contains(x, y + h) || screenBounds.contains(x + w, y) || screenBounds.contains(x + w, y + h)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the height of the titlebar of a {@link JFrame}
     */
    public static int getFrameTitleBarHeight() {
        Insets frameInsets = getFrameInsets();
        int h = frameInsets.top; //title bar height
        return h;
    }

    /**
     * @return the Insets of a {@link JFrame}
     */
    public static final Insets getFrameInsets() {
        JFrame frame = new JFrame();
        frame.pack();
        Insets frameInsets = frame.getInsets();
        return frameInsets;
    }

    /**
     * @return the maxmumim frame bounds
     */
    public static Rectangle getMaximumFrameBounds() {
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maximumFrameBounds = localGraphicsEnvironment.getMaximumWindowBounds();
        Insets frameInsets = getFrameInsets();
        maximumFrameBounds.x -= frameInsets.left;
        maximumFrameBounds.width += frameInsets.right + frameInsets.left;
        maximumFrameBounds.height += frameInsets.bottom;
        return maximumFrameBounds;
    }

}
