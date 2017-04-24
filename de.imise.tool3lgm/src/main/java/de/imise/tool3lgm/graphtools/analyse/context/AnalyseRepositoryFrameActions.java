package de.imise.tool3lgm.graphtools.analyse.context;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.dialog.ExtendedFileChooser;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author AXS created on 21.08.2007
 */
public class AnalyseRepositoryFrameActions {

    // ////////////////////////////////////
    // Actions für Buttons und das Menü //
    // ////////////////////////////////////

    /**
     * Aktion für das Schließen des Dialoges
     */
    static final Action ACTION_CLOSE_DIALOG = new AbstractAction(Tool3lgmConstants.getResString("close")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            if (AnalyseRepositoryFrame.analysisChanged) {
                int answer = MultipleOptionPane.showConfirmDialog(AnalyseRepositoryFrame.dialog, Tool3lgmConstants.getResString("ana_close_repository_frame_question_title"), Tool3lgmConstants.getResString("ana_close_repository_frame_question"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    AnalyseRepository.setXMLAnalysen(AnalyseRepositoryFrame.analysen);
                    AnalyseRepository.saveRepository();
                }
            }
            if (AnalyseEditor.editor != null && AnalyseEditor.editor.getOwner() == AnalyseRepositoryFrame.dialog) {
                AnalyseEditor.editor.dispose();
            }
            AnalyseRepositoryFrame.dialog.dispose();
        }
    };

    /**
     * Aktion für den Import von Analysen.<br>
     * Fügt zur Liste der Analysen dieses Dialogs alle Analysen einer vom Benutzer ausgewählten
     * Datei hinzu.
     */
    static final Action ACTION_IMPORT_ANALYSIS = new AbstractAction(Tool3lgmConstants.getResString("ana_import")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            File lastUsedAnalyseFile = AnalyseRepositoryFrame.analyseFile;
            if (lastUsedAnalyseFile == null) {
                lastUsedAnalyseFile = AnalyseRepository.getRepositoryFile();
            }
            ExtendedFileChooser chooser = new ExtendedFileChooser(getClass());
            if (lastUsedAnalyseFile != null) {
                chooser.setCurrentDirectory(lastUsedAnalyseFile);
            }
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showOpenDialog(AnalyseRepositoryFrame.dialog) == ExtendedFileChooser.APPROVE_OPTION) {
                File fileToOpen = chooser.getSelectedFile();
                if (fileToOpen == null) {
                    return;
                }
                List<XMLAnalyse> analysenToAdd = AnalyseRepository.loadAnalyseFile(fileToOpen);
                if (analysenToAdd == null || analysenToAdd.size() == 0) {
                    return;
                }
                int size = AnalyseRepositoryFrame.analysen.size();
                for (XMLAnalyse xMLAnalyse : analysenToAdd) {
                    AnalyseRepositoryFrame.addAnalyse(xMLAnalyse, true);
                }
                // wenn mind. eine neue XMLAnalyse eingefügt wurde -> analysisChanged == true setzen
                AnalyseRepositoryFrame.analysisChanged = size < AnalyseRepositoryFrame.analysen.size();
                AnalyseRepositoryFrame.table.update();
                AnalyseRepositoryFrame.refreshActionStates();
            }
        }
    };

    /**
     * Aktion für das Exportieren von Analysen
     */
    static final Action ACTION_EXPORT_ANALYSIS = new AbstractAction(Tool3lgmConstants.getResString("ana_export")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            // Speichert die aktuellen Analysen in einer vom Benutzer ausgewählten Datei
            File lastUsedAnalyseFile = AnalyseRepositoryFrame.analyseFile;
            if (lastUsedAnalyseFile == null) {
                lastUsedAnalyseFile = AnalyseRepository.getRepositoryFile();
            }
            ExtendedFileChooser chooser = new ExtendedFileChooser(getClass());
            if (lastUsedAnalyseFile != null) {
                chooser.setCurrentDirectory(lastUsedAnalyseFile);
            }
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showSaveDialog(AnalyseRepositoryFrame.dialog) == ExtendedFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                if (fileToSave == null) {
                    return;
                }

                // wenn nicht selektiert ist, dann alles speichern, sonst nur die Selektion
                List<XMLAnalyse> analysenToExport;
                int[] selection = AnalyseRepositoryFrame.table.getSelectedRows();
                if (selection.length == 0) {
                    analysenToExport = AnalyseRepositoryFrame.analysen;
                } else {
                    analysenToExport = new ArrayList<XMLAnalyse>();
                    for (int i = selection.length - 1; i >= 0; i--) {
                        analysenToExport.add(AnalyseRepositoryFrame.analysen.get(selection[i]));
                    }
                }

                AnalyseRepository.saveAnalyseFile(fileToSave, analysenToExport);
                lastUsedAnalyseFile = fileToSave;
            }
            if (lastUsedAnalyseFile != AnalyseRepository.getRepositoryFile()) {
                AnalyseRepositoryFrame.analyseFile = lastUsedAnalyseFile;
            } else {
                AnalyseRepositoryFrame.analyseFile = null;
            }
            AnalyseRepositoryFrame.refreshActionStates();
        }
    };

    /**
     * Aktion für das Zurücksetzen des Repositories auf das Standardrepository
     */
    static final Action ACTION_LOAD_STANDARD_REPOSITORY = new AbstractAction(Tool3lgmConstants.getResString("ana_load_standard_repository")) {

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (AnalyseRepositoryFrame.analysisChanged == true) {
                int answer = JOptionPane.showConfirmDialog(AnalyseRepositoryFrame.dialog, Tool3lgmConstants.getResString("ana_load_standard_repository_question"), Tool3lgmConstants.getResString("ana_load_standard_repository"), JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    AnalyseRepositoryFrame.setAnalysen(AnalyseRepository.getXMLAnalysen());
                    AnalyseRepositoryFrame.analysisChanged = false;
                    AnalyseRepositoryFrame.table.update();
                    AnalyseRepositoryFrame.refreshActionStates();
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return AnalyseRepositoryFrame.analysisChanged;
        }
    };

    /**
     * Action für das Speichern des Repositories
     */
    static final Action ACTION_SAVE_REPOSITORY = new AbstractAction(Tool3lgmConstants.getResString("ana_save_repository")) {

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (!AnalyseRepository.setXMLAnalysen(AnalyseRepositoryFrame.analysen)) {
                return;
            }
            AnalyseRepository.saveRepository();
            AnalyseRepositoryFrame.analysisChanged = false;
            AnalyseRepositoryFrame.refreshActionStates();
        }

        @Override
        public boolean isEnabled() {
            return AnalyseRepositoryFrame.analysisChanged;
        }
    };

    /**
     * Action für das Zurücksetzen des Analyseergebnisses in der Grafik
     */
    static final Action ACTION_RESET_ANALYSIS_RESULT = new AbstractAction(Tool3lgmConstants.getResString("reset_result")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            GraphDocument gd = Static.getSelectedDoc();
            if (gd == null) {
                return;
            }
            gd.clearAnalysisResult();
            AnalyseRepositoryFrame.refreshActionStates();
        }
    };

    /**
     * Action für das starten der ausgewählten Analysen
     */
    static final Action ACTION_START_ANALYSIS = new AbstractAction(Tool3lgmConstants.getResString("ana_start")) {

        @Override
        public void actionPerformed(final ActionEvent e) {
            GraphDocument doc = Static.getSelectedDoc();
            if (doc == null) {
            }
            int[] selection = AnalyseRepositoryFrame.table.getSelectedRows();
            for (int i = 0; i < selection.length; i++) {
                XMLAnalyse query = AnalyseRepositoryFrame.analysen.get(selection[i]);
                query.setAnalysisResult(doc);
            }
            AnalyseRepositoryFrame.refreshActionStates();
        }

        @Override
        public boolean isEnabled() {
            GraphDocument gd = Static.getSelectedDoc();
            if (gd == null) {
                return false;
            }
            return true;
        }

    };

    /**
     * Action für das starten der ausgewählten Analysen
     */
    static final Action ACTION_NEW_ANALYSIS = new AbstractAction(Tool3lgmConstants.getResString("ana_new")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                AnalyseRepositoryFrame.addAnalyse(XMLAnalyse.createAnalyse("", ""), true);
            } catch (SAXException ex) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("AnalyseNichtErstellt") + "\n" + ex.getMessage(), ex);
            }
            AnalyseRepositoryFrame.analysisChanged = true;
            AnalyseRepositoryFrame.table.update();
            AnalyseRepositoryFrame.refreshActionStates();
        }
    };

    /**
     * Action für das starten der ausgewählten Analysen
     */
    static final Action ACTION_DELETE_ANALYSIS = new AbstractAction(Tool3lgmConstants.getResString("ana_delete")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            int[] selection = AnalyseRepositoryFrame.table.getSelectedRows();
            for (int i = selection.length - 1; i >= 0; i--) {
                AnalyseRepositoryFrame.analysen.remove(selection[i]);
            }
            AnalyseRepositoryFrame.analysisChanged = true;
            AnalyseRepositoryFrame.table.update();
            AnalyseRepositoryFrame.refreshActionStates();
        }

        @Override
        public boolean isEnabled() {
            if (AnalyseRepositoryFrame.table == null) {
                return false;
            }
            return AnalyseRepositoryFrame.table.getSelectedRows().length > 0;
        }
    };

    /**
     * Action für das Starten des AnalyseEditors
     */
    static final Action ACTION_ANALYSIS_EDITOR = new AbstractAction(Tool3lgmConstants.getResString("analysis_editor")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            AnalyseEditor.showDialog(AnalyseRepositoryFrame.dialog);
        }
    };

}
