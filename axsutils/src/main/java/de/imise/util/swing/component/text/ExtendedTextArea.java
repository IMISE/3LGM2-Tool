package de.imise.util.swing.component.text;

import javax.swing.JTextArea;
import javax.swing.text.Document;


/**
 * Erweitert {@link JTextArea} um ein Kontextmenü mit Cut, Copy, Paste, Select All und Delete All.
 * 
 * @author AXS
 * @create 20.07.2012
 */
public class ExtendedTextArea extends JTextArea {

	/**
	 * 
	 */
	public ExtendedTextArea() {
		super();
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

	/**
	 * @param doc
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public ExtendedTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

	/**
	 * @param doc
	 */
	public ExtendedTextArea(Document doc) {
		super(doc);
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

	/**
	 * @param rows
	 * @param columns
	 */
	public ExtendedTextArea(int rows, int columns) {
		super(rows, columns);
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

	/**
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public ExtendedTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

	/**
	 * @param text
	 */
	public ExtendedTextArea(String text) {
		super(text);
		TextComponentStandardPopup.addPopupMenuTo(this);
	}

}
