package de.imise.util.swing.component;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Panel mit einstellbarer Scrollweite (insbesondere beim Mausrad)
 * 
 * @author AXS
 * @create 03.08.2011
 */
public class BlockScrollableJPanel extends JPanel implements Scrollable {

	/**
	 * Anzahl der Pixel, um die bei jedem Scrollvorgang verschoben werden soll.
	 */
	protected int maxUnitIncrement = 1;
	
	/**
	 * 
	 */
	public BlockScrollableJPanel() {
		super();
	}
	
	/**
	 * @param maxUnitIncrement
	 */
	public BlockScrollableJPanel(int maxUnitIncrement) {
		super();
		this.maxUnitIncrement = maxUnitIncrement;
	}

	/**
	 * @param layout
	 * @param maxUnitIncrement
	 */
	public BlockScrollableJPanel(LayoutManager layout, int maxUnitIncrement) {
		super(layout);
		this.maxUnitIncrement = maxUnitIncrement;
	}

	/**
	 * @param isDoubleBuffered
	 * @param maxUnitIncrement
	 */
	public BlockScrollableJPanel(boolean isDoubleBuffered, int maxUnitIncrement) {
		super(isDoubleBuffered);
		this.maxUnitIncrement = maxUnitIncrement;
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 * @param maxUnitIncrement
	 */
	public BlockScrollableJPanel(LayoutManager layout, boolean isDoubleBuffered, int maxUnitIncrement) {
		super(layout, isDoubleBuffered);
		this.maxUnitIncrement = maxUnitIncrement;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
	 */
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		} else {
			currentPosition = visibleRect.y;
		}
//		System.err.println(currentPosition);
		// Return the number of pixels between currentPosition
		// and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition - (currentPosition / maxUnitIncrement) * maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
		}
		return ((currentPosition / maxUnitIncrement) + 1) * maxUnitIncrement - currentPosition;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
	 */
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width - maxUnitIncrement;
		return visibleRect.height - maxUnitIncrement;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

    public void setMaxUnitIncrement(int pixels) {
    	if (pixels < 1)
    		pixels = 1;
   		maxUnitIncrement = pixels;
    }

}
