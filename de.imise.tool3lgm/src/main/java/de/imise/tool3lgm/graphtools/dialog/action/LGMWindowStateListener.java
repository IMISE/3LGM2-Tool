/*
 * Created on 17.01.2008
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 * @author fstephan
 */
public class LGMWindowStateListener implements WindowStateListener {

    /**
     * COMMENTME
     */
    private final LGMAction windowStateChangedAction;

    /**
     * @param windowStateChangedAction
     */
    public LGMWindowStateListener(final LGMAction windowStateChangedAction) {
        this.windowStateChangedAction = windowStateChangedAction;
    }

    @Override
    public void windowStateChanged(final WindowEvent e) {
        if (windowStateChangedAction != null) {
            windowStateChangedAction.execute(e);
        }
    }

}
