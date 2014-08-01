package de.imise.tool3lgm.tools;

import javax.swing.JToolBar;

/**
 * TODO: prüfen, ob diese Klasse weg kann
 * 
 * @author Thomas Rudert
 * Abstrakte Klasse für alle Werkzeugleisten, die nicht floatable sein sollen.
 */
public abstract class UnfloatableToolBar extends JToolBar {

	public UnfloatableToolBar() {
		setFloatable(false);
	}
}
