package de.imise.util.swing.component.text;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * Erweitert {@link JTextArea} um ein Kontextmenü mit Cut, Copy, Paste, Select
 * All und Delete All.
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
    public ExtendedTextArea(final Document doc, final String text, final int rows, final int columns) {
        super(doc, text, rows, columns);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param doc
     */
    public ExtendedTextArea(final Document doc) {
        super(doc);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param rows
     * @param columns
     */
    public ExtendedTextArea(final int rows, final int columns) {
        super(rows, columns);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param text
     * @param rows
     * @param columns
     */
    public ExtendedTextArea(final String text, final int rows, final int columns) {
        super(text, rows, columns);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param text
     */
    public ExtendedTextArea(final String text) {
        super(text);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

}
