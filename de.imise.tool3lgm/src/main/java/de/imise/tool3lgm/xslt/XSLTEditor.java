package de.imise.tool3lgm.xslt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.swing.component.text.ExtendedTextArea;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * @author Thomas Rudert
 *         Klasse stellt einen TextEditor fuer unformatierten TextDokumente zur
 *         Verfuegung
 */
public class XSLTEditor extends JDialog implements ActionListener, WindowListener {

    private ExtendedTextArea textArea;
    private File file = null;
    private boolean readOnly = false;
    private JButton buttonSave;

    public XSLTEditor(final JDialog owner) {
        super(owner);
        setComponents(new ExtendedTextArea());
    }

    public XSLTEditor(final JDialog owner, final File _file) {
        super(owner);
        file = _file;
        setComponents(new ExtendedTextArea(loadFromFile(file)));
    }

    public XSLTEditor(final JDialog owner, final InputStream stream) {
        super(owner);
        readOnly = true;
        StringBuilder text = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new InputStreamReader(stream));
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
        } catch (IOException exp) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        setComponents(new ExtendedTextArea(text.toString()));
    }

    public XSLTEditor(final JDialog owner, final String text) {
        super(owner);
        setComponents(new ExtendedTextArea(text));
    }

    /**
     * richtet alle Komponenten des Dialogs ein
     * 
     * @param _textArea die TextArea die dargestellt werden soll
     */
    private void setComponents(final ExtendedTextArea _textArea) {
        textArea = _textArea;
        this.setSize(700, 500);
        addWindowListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonSave = new JButton(Tool3lgmConstants.getResString("save"));
        buttonSave.setActionCommand("action_save");
        buttonSave.addActionListener(this);
        buttonPanel.add(buttonSave);
        JButton button = new JButton(Tool3lgmConstants.getResString("save_as"));
        button.setActionCommand("action_saveAs");
        button.addActionListener(this);
        buttonPanel.add(button);
        button = new JButton(Tool3lgmConstants.getResString("exit"));
        button.setActionCommand("action_exit");
        button.addActionListener(this);
        buttonPanel.add(button);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        if (readOnly) {
            buttonSave.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        int result;

        if (e.getActionCommand().equals("action_save")) {
            saveToFile();
            if (getParent() instanceof XMLExportDialog) {
                ((XMLExportDialog) getParent()).updateTable();
            }
        } else if (e.getActionCommand().equals("action_saveAs")) {
            saveAsFile();
            if (getParent() instanceof XMLExportDialog) {
                ((XMLExportDialog) getParent()).updateTable();
            }
        } else if (e.getActionCommand().equals("action_exit")) {

            if ((result = JOptionPane.showConfirmDialog(this, Tool3lgmConstants.getResString("quest_saveBeforeExit"), "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) == JOptionPane.YES_OPTION) {
                if (readOnly) {
                    if (!saveAsFile()) {
                        return;
                    }
                } else if (!saveToFile()) {
                    return;
                }
            }
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (getParent() instanceof XMLExportDialog) {
                ((XMLExportDialog) getParent()).updateTable();
            }
            dispose();
        }
    }

    /**
     * laed den darzustellenden Text aus einer Datei
     * 
     * @param f File
     * @return String mit dem Inhalt der Datei
     */
    private String loadFromFile(final File f) {
        StringBuilder text = new StringBuilder();
        String line;
        try {
            RandomAccessFile input = new RandomAccessFile(f, "r");
            while ((line = input.readLine()) != null) {
                text.append(line + "\n");
            }
            input.close();
            if (text.substring(text.length() - 2) == "\n") {
                text.deleteCharAt(text.length() - 1);
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            JOptionPane.showMessageDialog(this, Tool3lgmConstants.getResString("err_file") + "\n(" + file + ")", e.toString(), JOptionPane.ERROR_MESSAGE);
        }
        return text.toString();
    }

    /**
     * speichert den Text in die Datei file, wenn file != null, ansonsten wird
     * die Methode saveAsFile aufgerufen
     * 
     * @return boolean with true, if save was successful
     */
    private boolean saveToFile() {
        if (file == null) {
            return saveAsFile();
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            RandomAccessFile output = new RandomAccessFile(file, "rw");
            output.writeBytes(textArea.getText());
            output.close();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            JOptionPane.showMessageDialog(this, e.getMessage(), Tool3lgmConstants.getResString("err_file"), JOptionPane.ERROR_MESSAGE);
            return false;

        }
        return true;
    }

    /**
     * speichert den Text in einer Datei, wobei der Dateiname in einem Dialog
     * ausgewaehlt werden kann
     * 
     * @return boolean with true, if save was successful
     */
    private boolean saveAsFile() {
        File lastUserDir = UserProperties.getWorkingDirectory();
        ExtendedFileChooser saveDialog = new ExtendedFileChooser(XSLTEditor.class);
        saveDialog.addChoosableFileFilter(XSLTFileHandler.XSLT_FILE_FILTER.getFileNameExtensionFilter());
        saveDialog.setFileFilter(XSLTFileHandler.XSLT_FILE_FILTER.getFileNameExtensionFilter());
        if (file != null && !readOnly) {
            saveDialog.setSelectedFile(file);
        } else {
            saveDialog.setCurrentDirectory(lastUserDir);
        }
        if (saveDialog.showSaveDialog(this) != ExtendedFileChooser.APPROVE_OPTION) {
            return false;
        }
        file = saveDialog.getSelectedFile();
        UserProperties.setWorkingDirectory(file.getParentFile());
        readOnly = false;
        buttonSave.setEnabled(true);
        UserProperties.addXslSearchDir(file.getParentFile());
        return saveToFile();
    }

    @Override
    public void windowActivated(final WindowEvent e) {
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        int result;
        if ((result = JOptionPane.showConfirmDialog(this, Tool3lgmConstants.getResString("quest_saveBeforeExit"), "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) == JOptionPane.YES_OPTION) {
            if (readOnly) {
                if (!saveAsFile()) {
                    return;
                }
            } else if (!saveToFile()) {
                return;
            }
        }
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
        dispose();
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }

}
