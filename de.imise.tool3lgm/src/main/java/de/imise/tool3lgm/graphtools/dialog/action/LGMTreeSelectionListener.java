/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * @author fstephan
 */
public class LGMTreeSelectionListener implements TreeSelectionListener {

    /**
     * COMMENTME
     */
    private final LGMAction action;

    /**
     * @param action
     */
    public LGMTreeSelectionListener(final LGMAction action) {
        this.action = action;
    }

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        action.execute(e);
    }

}
