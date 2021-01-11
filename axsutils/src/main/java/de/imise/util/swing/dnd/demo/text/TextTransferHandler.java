package de.imise.util.swing.dnd.demo.text;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

/**
 * An implementation of TransferHandler that adds support for the import and
 * export of text using drag and drop and cut/copy/paste.
 */
class TextTransferHandler extends TransferHandler {
    //Start and end position in the source text.
    //We need this information when performing a MOVE
    //in order to remove the dragged text from the source.
    Position p0 = null, p1 = null;

    /**
     * Perform the actual import. This method supports both drag and drop and
     * cut/copy/paste.
     */
    @Override
    public boolean importData(final TransferHandler.TransferSupport support) {
        //If we can't handle the import, bail now.
        if (!canImport(support)) {
            return false;
        }

        //Fetch the data -- bail if this fails
        String data;
        try {
            data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (java.io.IOException e) {
            return false;
        }

        JTextField tc = (JTextField) support.getComponent();
        tc.replaceSelection(data);
        return true;
    }

    /**
     * Bundle up the data for export.
     */
    @Override
    protected Transferable createTransferable(final JComponent c) {
        JTextField source = (JTextField) c;
        int start = source.getSelectionStart();
        int end = source.getSelectionEnd();
        Document doc = source.getDocument();
        if (start == end) {
            return null;
        }
        try {
            p0 = doc.createPosition(start);
            p1 = doc.createPosition(end);
        } catch (BadLocationException e) {
            System.out.println("Can't create position - unable to remove text from source.");
        }
        String data = source.getSelectedText();
        return new StringSelection(data);
    }

    /**
     * These text fields handle both copy and move actions.
     */
    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    /**
     * When the export is complete, remove the old text if the action was a
     * move.
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (action != MOVE) {
            return;
        }

        if (p0 != null && p1 != null && p0.getOffset() != p1.getOffset()) {
            try {
                JTextComponent tc = (JTextComponent) c;
                tc.getDocument().remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
            } catch (BadLocationException e) {
                System.out.println("Can't remove text from source.");
            }
        }
    }

    /**
     * We only support importing strings.
     */
    @Override
    public boolean canImport(final TransferHandler.TransferSupport support) {
        // we only import Strings
        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        return true;
    }
}