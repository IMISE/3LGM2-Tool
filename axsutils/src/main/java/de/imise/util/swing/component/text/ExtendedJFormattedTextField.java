package de.imise.util.swing.component.text;

import java.text.Format;

import javax.swing.JFormattedTextField;

/**
 * Erweitert {@link JFormattedTextField} um ein Kontextmenü mit Cut, Copy, Paste, Select All und Delete All.
 * 
 * @author AXS
 * @create 21.08.2015
 */
public class ExtendedJFormattedTextField extends JFormattedTextField {

    public ExtendedJFormattedTextField() {
    }

    public ExtendedJFormattedTextField(final Object value) {
        super(value);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    public ExtendedJFormattedTextField(final Format format) {
        super(format);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    public ExtendedJFormattedTextField(final AbstractFormatter formatter) {
        super(formatter);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    public ExtendedJFormattedTextField(final AbstractFormatterFactory factory) {
        super(factory);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

    public ExtendedJFormattedTextField(final AbstractFormatterFactory factory, final Object currentValue) {
        super(factory, currentValue);
        TextComponentStandardPopup.addPopupMenuTo(this);
    }

}
