/*
 * Created on 17.01.2008
 */
package de.imise.util.swing.event;

import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 * Ein {@link WindowStateListener}, der beim Eintreffen eines
 * {@link WindowEvent} eine festgelegte Aktion ausführt.
 * 
 * @author fstephan
 */
public class LGMWindowStateListener implements WindowStateListener {

    /**
     * Aktion, die beim Eintreffen eines {@link WindowEvent} ausgeführt wird.
     */
    private FlexibleEventAction windowStateChangedAction;

    /**
     * Ein {@link WindowStateListener}, der beim Eintreffen eines
     * {@link WindowEvent} die übergebene Aktion ausführt.
     * 
     * @param windowStateChangedAction
     */
    public LGMWindowStateListener(FlexibleEventAction windowStateChangedAction) {
        this.windowStateChangedAction = windowStateChangedAction;
    }

    /*
     * (non-Javadoc)
     * @see
     * java.awt.event.WindowStateListener#windowStateChanged(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowStateChanged(WindowEvent e) {
        if (this.windowStateChangedAction != null)
            this.windowStateChangedAction.execute(e);
    }

    /**
     * @return the windowStateChangedAction
     */
    public FlexibleEventAction getWindowStateChangedAction() {
        return windowStateChangedAction;
    }

    /**
     * @param windowStateChangedAction the windowStateChangedAction to set
     */
    public void setWindowStateChangedAction(FlexibleEventAction windowStateChangedAction) {
        this.windowStateChangedAction = windowStateChangedAction;
    }

}
