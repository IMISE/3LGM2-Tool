/*
 * Created on 23.11.2007
 */
package de.imise.util.swing.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author fstephan
 */
public class LGMMouseListener extends MouseAdapter {

    private FlexibleEventAction mouseClicked;
    private FlexibleEventAction mouseEntered;
    private FlexibleEventAction mouseExited;
    private FlexibleEventAction mousePressed;
    private FlexibleEventAction mouseReleased;
    private FlexibleEventAction mouseDragged;

    public LGMMouseListener(FlexibleEventAction mouseClicked, FlexibleEventAction mouseEntered, FlexibleEventAction mouseExited, FlexibleEventAction mousePressed, FlexibleEventAction mouseReleased, FlexibleEventAction mouseDragged) {
        this.mouseClicked = mouseClicked;
        this.mouseEntered = mouseEntered;
        this.mouseExited = mouseExited;
        this.mousePressed = mousePressed;
        this.mouseReleased = mouseReleased;
        this.mouseDragged = mouseDragged;
    }

    public LGMMouseListener(FlexibleEventAction mouseClicked, FlexibleEventAction mouseEntered, FlexibleEventAction mouseExited, FlexibleEventAction mousePressed, FlexibleEventAction mouseReleased) {
        this.mouseClicked = mouseClicked;
        this.mouseEntered = mouseEntered;
        this.mouseExited = mouseExited;
        this.mousePressed = mousePressed;
        this.mouseReleased = mouseReleased;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (mouseClicked != null)
            mouseClicked.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (mouseEntered != null)
            mouseEntered.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (mouseExited != null)
            mouseExited.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (mousePressed != null)
            mousePressed.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouseReleased != null)
            mouseReleased.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseDragged != null)
            mouseDragged.execute(e);
    }

}
