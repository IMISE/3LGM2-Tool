/**
 *
 */
package de.imise.util.swing.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * @author Ich
 */
public class BackgroundImagePanel extends JPanel {

    /**
     * Background image
     */
    private final Image image;

    /**
     * @param image
     */
    public BackgroundImagePanel(final Image image) {
        this.image = image;
        Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
    }

    @Override
    public void paintComponent(final Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

}
