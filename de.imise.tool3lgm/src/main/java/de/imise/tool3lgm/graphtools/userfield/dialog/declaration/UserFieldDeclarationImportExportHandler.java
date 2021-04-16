package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.FileFilterType.USERFIELD;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.UserFieldXMLParser;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.util.swing.dialog.ExtendedFileChooser;

public final class UserFieldDeclarationImportExportHandler {

    /**
     * Defnition importieren
     *
     * @return
     */
    public static final boolean importDefinitions(final JDialog dialogOwner, final UserFieldDefinitions definitions) {
        JFileChooser dialog = getFileChooser();
        if (dialog.showOpenDialog(dialogOwner) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        return UserFieldXMLParser.importDefinitions(dialog.getSelectedFile(), definitions);
    }

    /**
     * Defnition exportieren
     *
     * @return
     */
    public static final boolean exportDefinitions(final JDialog dialogOwner, final UserFieldDefinitions definitions) {
        JFileChooser dialog = getFileChooser();
        if (dialog.showSaveDialog(dialogOwner) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        return UserFieldXMLParser.exportDefinitions(dialog.getSelectedFile(), definitions);
    }

    private static final JFileChooser getFileChooser() {
        ExtendedFileChooser dialog = new ExtendedFileChooser(UserFieldDeclarationDialog.class);
        dialog.setFileFilters(true, Tool3lgmConstants.getFileNameExtensionFilter(USERFIELD));
        dialog.setMultiSelectionEnabled(false);
        return dialog;
    }

}
