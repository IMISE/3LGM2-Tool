package de.imise.util.swing.component.text;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;


/**
 * Erweitert {@link JTextPane} um ein Kontextmenü mit Cut, Copy, Paste, Select All und Delete All.
 * 
 * @author AXS
 * @create 20.07.2012
 */
public class ExtendedTextPane extends JTextPane {

	/**
	 * 
	 */
	public ExtendedTextPane() {
		super();
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

	/**
	 * @param doc
	 */
	public ExtendedTextPane(StyledDocument doc) {
		super(doc);
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

}
