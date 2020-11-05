/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author fstephan
 */
public class LGMWindowListener implements WindowListener {

    private final LGMAction windowActivatedAction;
    private final LGMAction windowClosedAction;
    private final LGMAction windowClosingAction;
    private final LGMAction windowDeactivatedAction;
    private final LGMAction windowDeiconifiedAction;
    private final LGMAction windowIconifiedAction;
    private final LGMAction windowOpenedAction;

    /**
     * Konstruktor
     *
     * @param windowActivatedAction
     * @param windowClosedAction
     * @param windowClosingAction
     * @param windowDeactivatedAction
     * @param windowDeiconifiedAction
     * @param windowIconifiedAction
     * @param windowOpenedAction
     */
    public LGMWindowListener(final LGMAction windowActivatedAction, final LGMAction windowClosedAction, final LGMAction windowClosingAction, final LGMAction windowDeactivatedAction, final LGMAction windowDeiconifiedAction,
            final LGMAction windowIconifiedAction, final LGMAction windowOpenedAction) {

        this.windowActivatedAction = windowActivatedAction;
        this.windowClosedAction = windowClosedAction;
        this.windowClosingAction = windowClosingAction;
        this.windowDeactivatedAction = windowDeactivatedAction;
        this.windowDeiconifiedAction = windowDeiconifiedAction;
        this.windowIconifiedAction = windowIconifiedAction;
        this.windowOpenedAction = windowOpenedAction;
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        if (windowActivatedAction != null) {
            windowActivatedAction.execute(e);
        }
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        if (windowClosedAction != null) {
            windowClosedAction.execute(e);
        }
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        if (windowClosingAction != null) {
            windowClosingAction.execute(e);
        }
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
        if (windowDeactivatedAction != null) {
            windowDeactivatedAction.execute(e);
        }
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
        if (windowDeiconifiedAction != null) {
            windowDeiconifiedAction.execute(e);
        }
    }

    @Override
    public void windowIconified(final WindowEvent e) {
        if (windowIconifiedAction != null) {
            windowIconifiedAction.execute(e);
        }
    }

    @Override
    public void windowOpened(final WindowEvent e) {
        if (windowOpenedAction != null) {
            windowOpenedAction.execute(e);
        }
    }

}
