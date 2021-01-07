/*
 * Created on 23.11.2007
 */
package de.imise.util.swing.event;

import javax.swing.Action;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Ein {@link TreeSelectionListener}, der beim eintreffen eines
 * {@link TreeSelectionEvent} eine übergebare {@link Action} ausführt.
 * 
 * @author fstephan
 */
public class LGMTreeSelectionListener implements TreeSelectionListener {

    /**
     * Aktion, die ausgeführt wird, wenn diesen Listener ein
     * {@link TreeSelectionEvent} erreicht.
     */
    private FlexibleEventAction action;

    /**
     * Legt einen {@link TreeSelectionListener} an, der beim eintreffen eines
     * {@link TreeSelectionEvent} die übergebene {@link Action} ausführt.
     * 
     * @param action
     */
    public LGMTreeSelectionListener(FlexibleEventAction action) {
        super();
        setAction(action);
    }

    /*
     * (non-Javadoc)
     * @see
     * javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.
     * TreeSelectionEvent)
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        action.execute(e);
    }

    /**
     * @return the action
     */
    public FlexibleEventAction getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(FlexibleEventAction action) {
        this.action = action;
    }

}
