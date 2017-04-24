package de.imise.tool3lgm.imexport;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import javax.swing.JOptionPane;

import de.imise.tool3lgm.Static;

public class ImportErrorConfigurationUserInterface {

    private final ImportErrorConfiguration errorConfiguration;

    private final int maxDisplayedErrors = 10;

    public ImportErrorConfigurationUserInterface(final ImportErrorConfiguration errorConfiguration) {
        this.errorConfiguration = errorConfiguration;
    }

    public void showErrors() {
        if (errorConfiguration.hasErrors()) {
            String fullErrorString = getFullErrorString(maxDisplayedErrors);
            showErrorDialog(fullErrorString);
        }
    }

    public void copyToClipboard() {
        String fullErrorString = getFullErrorString(-1);
        StringSelection clipboardString = new StringSelection(fullErrorString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(clipboardString, null);
    }

    public String getFullErrorString(final int maxDisplayedErrors) {
        StringBuilder sb = new StringBuilder();
        List<ImportError> errors = errorConfiguration.getErrors();
        for (int i = 0; i < errors.size(); i++) {
            ImportError err = errors.get(i);
            sb.append(err.getErrorString());
            sb.append("\n");
            if (i == maxDisplayedErrors - 1) {
                int remainErorrs = errors.size() - maxDisplayedErrors;
                if (remainErorrs > 3) {
                    sb.append(remainErorrs + " more errors...");
                    break;
                }
            }
        }
        return sb.toString();
    }

    private void showErrorDialog(final String message) {
        JOptionPane.showMessageDialog(Static.getMainFrame(), message, "Import Error", JOptionPane.ERROR_MESSAGE);
    }

}
