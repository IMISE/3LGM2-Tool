package de.imise.util.swing.component.text;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * Erweitert {@link JTextField} um ein Kontextmenü mit Cut, Copy, Paste, Select
 * All und Delete All.
 *
 * @author AXS
 * @create 20.07.2012
 */
public class ExtendedTextField extends JTextField {

    /**
     * 
     */
    public ExtendedTextField() {
        super();
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param doc
     * @param text
     * @param columns
     */
    public ExtendedTextField(final Document doc, final String text, final int columns) {
        super(doc, text, columns);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param columns
     */
    public ExtendedTextField(final int columns) {
        super(columns);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param text
     * @param columns
     */
    public ExtendedTextField(final String text, final int columns) {
        super(text, columns);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    /**
     * @param text
     */
    public ExtendedTextField(final String text) {
        super(text);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

}
