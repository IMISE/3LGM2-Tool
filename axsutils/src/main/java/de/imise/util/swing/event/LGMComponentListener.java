/*
 * Created on 23.11.2007
 */
package de.imise.util.swing.event;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * @author fstephan
 */
public class LGMComponentListener implements ComponentListener {

    private FlexibleEventAction componentHiddenAction;

    private FlexibleEventAction componentMovedAction;

    private FlexibleEventAction componentResizedAction;

    private FlexibleEventAction componentShownAction;

    /**
     * *************************************************************************
     */

    /**
     * Konstruktor
     * 
     * @param componentHiddenAction
     * @param componentMovedAction
     * @param componentResizedAction
     * @param componentShownAction
     */
    public LGMComponentListener(FlexibleEventAction componentHiddenAction, FlexibleEventAction componentMovedAction, FlexibleEventAction componentResizedAction, FlexibleEventAction componentShownAction) {

        this.componentHiddenAction = componentHiddenAction;
        this.componentMovedAction = componentMovedAction;
        this.componentResizedAction = componentResizedAction;
        this.componentShownAction = componentShownAction;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.
     * ComponentEvent)
     */
    @Override
    public void componentHidden(final ComponentEvent e) {
        if (this.componentHiddenAction != null)
            this.componentHiddenAction.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.
     * ComponentEvent)
     */
    @Override
    public void componentMoved(final ComponentEvent e) {
        if (this.componentMovedAction != null)
            this.componentMovedAction.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.
     * ComponentEvent)
     */
    @Override
    public void componentResized(final ComponentEvent e) {
        if (this.componentResizedAction != null)
            this.componentResizedAction.execute(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.
     * ComponentEvent)
     */
    @Override
    public void componentShown(final ComponentEvent e) {
        if (this.componentShownAction != null)
            this.componentShownAction.execute(e);
    }
}
