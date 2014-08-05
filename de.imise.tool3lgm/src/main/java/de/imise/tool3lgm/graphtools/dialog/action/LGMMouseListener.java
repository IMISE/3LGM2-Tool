/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author fstephan
 */
public class LGMMouseListener extends MouseAdapter {

    private final LGMAction mouseClicked;
    private final LGMAction mouseEntered;
    private final LGMAction mouseExited;
    private final LGMAction mousePressed;
    private final LGMAction mouseReleased;
    private LGMAction mouseDragged;

    /**
     * @param mouseClicked
     * @param mouseEntered
     * @param mouseExited
     * @param mousePressed
     * @param mouseReleased
     * @param mouseDragged
     */
    public LGMMouseListener(final LGMAction mouseClicked, final LGMAction mouseEntered, final LGMAction mouseExited, final LGMAction mousePressed, final LGMAction mouseReleased, final LGMAction mouseDragged) {
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
    public LGMMouseListener(final LGMAction mouseClicked, final LGMAction mouseEntered, final LGMAction mouseExited, final LGMAction mousePressed, final LGMAction mouseReleased) {
        this.mouseClicked = mouseClicked;
        this.mouseEntered = mouseEntered;
        this.mouseExited = mouseExited;
        this.mousePressed = mousePressed;
        this.mouseReleased = mouseReleased;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (mouseClicked != null) {
            mouseClicked.execute(e);
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        if (mouseEntered != null) {
            mouseEntered.execute(e);
        }
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        if (mouseExited != null) {
            mouseExited.execute(e);
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (mousePressed != null) {
            mousePressed.execute(e);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (mouseReleased != null) {
            mouseReleased.execute(e);
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (mouseDragged != null) {
            mouseDragged.execute(e);
        }
    }

}
