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
        this(true);
    }

    /**
     * @param editable
     */
    public ExtendedTextPane(final boolean editable) {
        super();
        setEditable(editable);
    }

    /**
     * @param doc
     */
    public ExtendedTextPane(final StyledDocument doc) {
        this(doc, true);
    }

    /**
     * @param doc
     * @param editable
     */
    public ExtendedTextPane(final StyledDocument doc, final boolean editable) {
        super(doc);
        setEditable(editable);
    }

    @Override
    public void setEditable(final boolean b) {
        if (isEnabled() && b) {
            TextComponentStandardPopup.addPopupMenuTo(this);
        } else {
            TextComponentStandardPopup.removePopupMenuFrom(this);
        }
        super.setEditable(b);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (isEditable() && enabled) {
            TextComponentStandardPopup.addPopupMenuTo(this);
        } else {
            TextComponentStandardPopup.removePopupMenuFrom(this);
        }
        super.setEnabled(enabled);
    }

}
