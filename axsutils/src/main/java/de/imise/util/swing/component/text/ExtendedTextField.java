package de.imise.util.swing.component.text;

import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.text.Document;

import de.imise.util.swing.component.MinWidthComponent;

/**
 * Erweitert {@link JTextField} um ein Kontextmenü mit Cut, Copy, Paste, Select
 * All und Delete All.
 *
 * @author AXS
 * @create 20.07.2012
 */
public class ExtendedTextField extends JTextField implements MinWidthComponent {

    /**
     *
     */
    public ExtendedTextField() {
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

    @Override
    public Dimension getMaximumSize() {
        Dimension size = super.getMaximumSize();
        size.width = getMinWidth();
        return size;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = getMinWidth();
        return size;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension size = super.getPreferredScrollableViewportSize();
        size.width = getMinWidth();
        return size;
    }

}
