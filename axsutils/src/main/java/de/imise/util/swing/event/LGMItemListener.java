/*
 * Created on 17.12.2007
 *
 */
package de.imise.util.swing.event;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * @author fstephan
 *
 */
public class LGMItemListener implements ItemListener {

	/**
	 * COMMENTME
	 */
	private FlexibleEventAction action;
	
	/**
	 * @param action
	 */
	public LGMItemListener(FlexibleEventAction action) {
		this.action = action;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		action.execute(e);
	}

}
