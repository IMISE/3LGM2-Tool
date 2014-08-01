/*
 * Created on 23.11.2007
 *
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author fstephan
 * 
 */
public class LGMMouseListener extends MouseAdapter {

	private LGMAction mouseClicked;
	private LGMAction mouseEntered;
	private LGMAction mouseExited;
	private LGMAction mousePressed;
	private LGMAction mouseReleased;
	private LGMAction mouseDragged;

	/**
	 * @param mouseClicked
	 * @param mouseEntered
	 * @param mouseExited
	 * @param mousePressed
	 * @param mouseReleased
	 * @param mouseDragged
	 */
	public LGMMouseListener(LGMAction mouseClicked, LGMAction mouseEntered, LGMAction mouseExited, LGMAction mousePressed, LGMAction mouseReleased, LGMAction mouseDragged) {
		this.mouseClicked = mouseClicked;
		this.mouseEntered = mouseEntered;
		this.mouseExited = mouseExited;
		this.mousePressed = mousePressed;
		this.mouseReleased = mouseReleased;
		this.mouseDragged = mouseDragged;
	}

	/**
	 * @param mouseClicked
	 * @param mouseEntered
	 * @param mouseExited
	 * @param mousePressed
	 * @param mouseReleased
	 */
	public LGMMouseListener(LGMAction mouseClicked, LGMAction mouseEntered, LGMAction mouseExited, LGMAction mousePressed, LGMAction mouseReleased) {
		this.mouseClicked = mouseClicked;
		this.mouseEntered = mouseEntered;
		this.mouseExited = mouseExited;
		this.mousePressed = mousePressed;
		this.mouseReleased = mouseReleased;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (mouseClicked != null)
			mouseClicked.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		if (mouseEntered != null)
			mouseEntered.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		if (mouseExited != null)
			mouseExited.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (mousePressed != null)
			mousePressed.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (mouseReleased != null)
			mouseReleased.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseDragged != null)
			mouseDragged.execute(e);
	}

}
