/*
 * Created on 23.11.2007
 *
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


/**
 * @author fstephan
 */
public class LGMTreeSelectionListener implements TreeSelectionListener{
	
	/**
	 * COMMENTME
	 */
	private LGMAction action;
	
	/**
	 * @param action
	 */
	public LGMTreeSelectionListener(LGMAction action) {
		this.action = action;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		action.execute(e);
	}

		
		

}
