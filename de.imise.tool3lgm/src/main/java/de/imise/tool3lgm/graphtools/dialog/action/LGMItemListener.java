/*
 * Created on 17.12.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author fstephan
 */
public class LGMItemListener implements ItemListener {

    /**
     * COMMENTME
     */
    private final LGMAction action;

    /**
     * @param action
     */
    public LGMItemListener(final LGMAction action) {
        this.action = action;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        action.execute(e);
    }

}
