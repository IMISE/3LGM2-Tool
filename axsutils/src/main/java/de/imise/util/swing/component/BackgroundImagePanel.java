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
 *
 */
public class BackgroundImagePanel extends JPanel {

	/**
	 * Background image
	 */
	private Image image;

	/**
	 * @param image
	 */
	public BackgroundImagePanel(Image image) {
		this.image = image;
	    Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

}
