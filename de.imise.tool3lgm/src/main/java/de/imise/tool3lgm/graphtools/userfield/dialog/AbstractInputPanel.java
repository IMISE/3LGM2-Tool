/*
 * Created on 10.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog;

import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * @author AXS
 */
public abstract class AbstractInputPanel extends JPanel {

    /**
	 * 
	 */
    public AbstractInputPanel() {
        super();
    }

    /**
     * @param layout
     */
    public AbstractInputPanel(final LayoutManager layout) {
        super(layout);
    }

    /**
     * Wird aufgerufen, wenn die Änderungen des Panels rückgängig gemacht werden sollen.
     */
    public abstract void cancel();

    /**
     * Wird aufgerufen, wenn die Änderungen des Panels übernommen werden sollen.
     */
    public abstract void commit();

    /**
     * Wenn alle Eingaben in dem Panel korrekt sind, kommt <code>null</code> zurück. Ansonsten kommt, eine Fehlermeldung zurück, die dem Benutzer
     * angezeigt werden kann.
     */
    public String getError() {
        return null;
    }

}
