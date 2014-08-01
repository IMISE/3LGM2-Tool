/*
 * Created on 23.11.2007
 *
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


/**
 * @author fstephan
 *
 */
public class LGMWindowListener implements WindowListener{
	
	private LGMAction windowActivatedAction;
	private LGMAction windowClosedAction;
	private LGMAction windowClosingAction;
	private LGMAction windowDeactivatedAction;
	private LGMAction windowDeiconifiedAction;
	private LGMAction windowIconifiedAction;
	private LGMAction windowOpenedAction;
	
	
	
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
	public LGMWindowListener(LGMAction windowActivatedAction,
							 LGMAction windowClosedAction,
							 LGMAction windowClosingAction,
							 LGMAction windowDeactivatedAction,
							 LGMAction windowDeiconifiedAction,
							 LGMAction windowIconifiedAction,
							 LGMAction windowOpenedAction) {
		
		this.windowActivatedAction = windowActivatedAction;
		this.windowClosedAction = windowClosedAction;
		this.windowClosingAction = windowClosingAction;
		this.windowDeactivatedAction = windowDeactivatedAction;
		this.windowDeiconifiedAction = windowDeiconifiedAction;
		this.windowIconifiedAction = windowIconifiedAction;
		this.windowOpenedAction = windowOpenedAction;
	}
	
	
	
	@Override
	public void windowActivated(WindowEvent e) {
		if (this.windowActivatedAction != null)
			this.windowActivatedAction.execute(e);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (this.windowClosedAction != null)
			this.windowClosedAction.execute(e);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (this.windowClosingAction != null)
			this.windowClosingAction.execute(e);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		if (this.windowDeactivatedAction != null)
			this.windowDeactivatedAction.execute(e);
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		if (this.windowDeiconifiedAction != null)
			this.windowDeiconifiedAction.execute(e);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		if (this.windowIconifiedAction != null)
			this.windowIconifiedAction.execute(e);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		if (this.windowOpenedAction != null)
			this.windowOpenedAction.execute(e);
	}

}
