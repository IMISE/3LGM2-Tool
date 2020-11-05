/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * @author fstephan
 */
public class LGMComponentListener implements ComponentListener {

    private final LGMAction componentHiddenAction;

    private final LGMAction componentMovedAction;

    private final LGMAction componentResizedAction;

    private final LGMAction componentShownAction;

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
    public LGMComponentListener(final LGMAction componentHiddenAction, final LGMAction componentMovedAction, final LGMAction componentResizedAction, final LGMAction componentShownAction) {

        this.componentHiddenAction = componentHiddenAction;
        this.componentMovedAction = componentMovedAction;
        this.componentResizedAction = componentResizedAction;
        this.componentShownAction = componentShownAction;
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
        if (componentHiddenAction != null) {
            componentHiddenAction.execute(e);
        }
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
        if (componentMovedAction != null) {
            componentMovedAction.execute(e);
        }
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        if (componentResizedAction != null) {
            componentResizedAction.execute(e);
        }
    }

    @Override
    public void componentShown(final ComponentEvent e) {
        if (componentShownAction != null) {
            componentShownAction.execute(e);
        }
    }
}
