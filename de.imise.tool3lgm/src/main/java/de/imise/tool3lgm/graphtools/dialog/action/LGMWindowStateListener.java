/*
 * Created on 17.01.2008
 *
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
	private LGMAction windowStateChangedAction;
	
	/**
	 * @param windowStateChangedAction
	 */
	public LGMWindowStateListener(LGMAction windowStateChangedAction) {
		this.windowStateChangedAction = windowStateChangedAction;
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowStateListener#windowStateChanged(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowStateChanged(WindowEvent e) {
		if (this.windowStateChangedAction != null)
			this.windowStateChangedAction.execute(e);
	}
	

}
