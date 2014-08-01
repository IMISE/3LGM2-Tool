/*
 * Created on 23.11.2007
 *
 */
package de.imise.util.swing.event;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


/**
 * Ein {@link WindowListener}, der beim Eintreffen von {@link WindowEvent} die jeweils
 * festgelegte Aktion ausführt.
 * 
 * @author fstephan
 * 
 */
public class LGMWindowListener implements WindowListener {

	/**
	 * Aktion, die ausgeführt wird, wenn ein Fenster aktiviert wird.
	 */
	private FlexibleEventAction windowActivatedAction;
	
	/**
	 * Aktion, die ausgeführt wird, wenn ein Fenster geschlossen wurde.
	 */
	private FlexibleEventAction windowClosedAction;
	
	/**
	 * Aktion, die ausgeführt wird, wenn ein Fenster geschlossen wird.
	 */
	private FlexibleEventAction windowClosingAction;
	
	/**
	 * Aktion, die ausgeführt wird, wenn ein Fenster deaktiviert wird.
	 */
	private FlexibleEventAction windowDeactivatedAction;
	
	/**
	 * Aktion, die ausgeführt wird, wenn von einem Fenster das Icon entfernt wurde.
	 */
	private FlexibleEventAction windowDeiconifiedAction;
	
	/**
	 * Aktion, die ausgeführt wird, wenn bei einem Fenster ein Icon hinzugefügt wurde.
	 */
	private FlexibleEventAction windowIconifiedAction;
	
	/**
	 * Aktion, die ausgeführt wird, wenn ein Fenster geöffnet wurde.
	 */
	private FlexibleEventAction windowOpenedAction;

	/**
	 * Ein {@link WindowListener}, der beim Eintreffen von {@link WindowEvent} die jeweilige
	 * übergebene Aktion ausführt, wenn diese nicht null ist
	 * 
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
	public LGMWindowListener(FlexibleEventAction windowActivatedAction, FlexibleEventAction windowClosedAction, FlexibleEventAction windowClosingAction, FlexibleEventAction windowDeactivatedAction, FlexibleEventAction windowDeiconifiedAction, FlexibleEventAction windowIconifiedAction, FlexibleEventAction windowOpenedAction) {
		super();
		setWindowActivatedAction(windowActivatedAction);
		setWindowClosedAction(windowClosedAction);
		setWindowClosingAction(windowClosingAction);
		setWindowDeactivatedAction(windowDeactivatedAction);
		setWindowDeiconifiedAction(windowDeiconifiedAction);
		setWindowIconifiedAction(windowIconifiedAction);
		setWindowOpenedAction(windowOpenedAction);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		if (windowActivatedAction != null)
			windowActivatedAction.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		if (windowClosedAction != null)
			windowClosedAction.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if (windowClosingAction != null)
			windowClosingAction.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {
		if (windowDeactivatedAction != null)
			windowDeactivatedAction.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {
		if (windowDeiconifiedAction != null)
			windowDeiconifiedAction.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e) {
		if (windowIconifiedAction != null)
			windowIconifiedAction.execute(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e) {
		if (windowOpenedAction != null)
			windowOpenedAction.execute(e);
	}

	/**
	 * @return the windowActivatedAction
	 */
	public FlexibleEventAction getWindowActivatedAction() {
		return windowActivatedAction;
	}

	/**
	 * @param windowActivatedAction the windowActivatedAction to set
	 */
	public void setWindowActivatedAction(FlexibleEventAction windowActivatedAction) {
		this.windowActivatedAction = windowActivatedAction;
	}

	/**
	 * @return the windowClosedAction
	 */
	public FlexibleEventAction getWindowClosedAction() {
		return windowClosedAction;
	}

	/**
	 * @param windowClosedAction the windowClosedAction to set
	 */
	public void setWindowClosedAction(FlexibleEventAction windowClosedAction) {
		this.windowClosedAction = windowClosedAction;
	}

	/**
	 * @return the windowClosingAction
	 */
	public FlexibleEventAction getWindowClosingAction() {
		return windowClosingAction;
	}

	/**
	 * @param windowClosingAction the windowClosingAction to set
	 */
	public void setWindowClosingAction(FlexibleEventAction windowClosingAction) {
		this.windowClosingAction = windowClosingAction;
	}

	/**
	 * @return the windowDeactivatedAction
	 */
	public FlexibleEventAction getWindowDeactivatedAction() {
		return windowDeactivatedAction;
	}

	/**
	 * @param windowDeactivatedAction the windowDeactivatedAction to set
	 */
	public void setWindowDeactivatedAction(FlexibleEventAction windowDeactivatedAction) {
		this.windowDeactivatedAction = windowDeactivatedAction;
	}

	/**
	 * @return the windowDeiconifiedAction
	 */
	public FlexibleEventAction getWindowDeiconifiedAction() {
		return windowDeiconifiedAction;
	}

	/**
	 * @param windowDeiconifiedAction the windowDeiconifiedAction to set
	 */
	public void setWindowDeiconifiedAction(FlexibleEventAction windowDeiconifiedAction) {
		this.windowDeiconifiedAction = windowDeiconifiedAction;
	}

	/**
	 * @return the windowIconifiedAction
	 */
	public FlexibleEventAction getWindowIconifiedAction() {
		return windowIconifiedAction;
	}

	/**
	 * @param windowIconifiedAction the windowIconifiedAction to set
	 */
	public void setWindowIconifiedAction(FlexibleEventAction windowIconifiedAction) {
		this.windowIconifiedAction = windowIconifiedAction;
	}

	/**
	 * @return the windowOpenedAction
	 */
	public FlexibleEventAction getWindowOpenedAction() {
		return windowOpenedAction;
	}

	/**
	 * @param windowOpenedAction the windowOpenedAction to set
	 */
	public void setWindowOpenedAction(FlexibleEventAction windowOpenedAction) {
		this.windowOpenedAction = windowOpenedAction;
	}

	
}
