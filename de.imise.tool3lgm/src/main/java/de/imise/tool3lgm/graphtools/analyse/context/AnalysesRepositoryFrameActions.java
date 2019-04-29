package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.dialog.ExtendedFileChooser;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author AXS created on 21.08.2007
 */
public class AnalysesRepositoryFrameActions {

    // ////////////////////////////////////
    // Actions für Buttons und das Menü //
    // ////////////////////////////////////

    /**
     * Aktion für das Schließen des Dialoges
     */
    static final Action ACTION_CLOSE_DIALOG = new AbstractAction(getResString("close")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            if (AnalysesRepositoryFrame.analysisChanged) {
                int answer = MultipleOptionPane.showConfirmDialog(AnalysesRepositoryFrame.dialog, getResString("ana_close_repository_frame_question_title"), getResString("ana_close_repository_frame_question"), JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    AnalysesRepository.setXMLAnalysen(AnalysesRepositoryFrame.analysen);
                    AnalysesRepository.saveRepository();
                }
            }
            if (AnalysisEditor.editor != null && AnalysisEditor.editor.getOwner() == AnalysesRepositoryFrame.dialog) {
                AnalysisEditor.editor.dispose();
            }
            AnalysesRepositoryFrame.dialog.dispose();
        }
    };

    /**
     * Aktion für den Import von Analysen.<br>
     * Fügt zur Liste der Analysen dieses Dialogs alle Analysen einer vom Benutzer ausgewählten
     * Datei hinzu.
     */
    static final Action ACTION_IMPORT_ANALYSIS = new AbstractAction(getResString("ana_import")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            File lastUsedAnalyseFile = AnalysesRepositoryFrame.analyseFile;
            if (lastUsedAnalyseFile == null) {
                lastUsedAnalyseFile = AnalysesRepository.getRepositoryFile();
            }
            ExtendedFileChooser chooser = new ExtendedFileChooser(getClass());
            if (lastUsedAnalyseFile != null) {
                chooser.setCurrentDirectory(lastUsedAnalyseFile);
            }
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showOpenDialog(AnalysesRepositoryFrame.dialog) == ExtendedFileChooser.APPROVE_OPTION) {
                File fileToOpen = chooser.getSelectedFile();
                if (fileToOpen == null) {
                    return;
                }
                List<XMLAnalysis> analysenToAdd = AnalysesRepository.loadAnalyseFile(fileToOpen);
                if (analysenToAdd == null || analysenToAdd.size() == 0) {
                    return;
                }
                int size = AnalysesRepositoryFrame.analysen.size();
                for (XMLAnalysis xMLAnalyse : analysenToAdd) {
                    AnalysesRepositoryFrame.addAnalysis(xMLAnalyse, true);
                }
                // wenn mind. eine neue XMLAnalyse eingefügt wurde -> analysisChanged == true setzen
                AnalysesRepositoryFrame.analysisChanged = size < AnalysesRepositoryFrame.analysen.size();
                AnalysesRepositoryFrame.table.update();
                AnalysesRepositoryFrame.refreshActionStates();
            }
        }
    };

    /**
     * Aktion für das Exportieren von Analysen
     */
    static final Action ACTION_EXPORT_ANALYSIS = new AbstractAction(getResString("ana_export")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            // Speichert die aktuellen Analysen in einer vom Benutzer ausgewählten Datei
            File lastUsedAnalyseFile = AnalysesRepositoryFrame.analyseFile;
            if (lastUsedAnalyseFile == null) {
                lastUsedAnalyseFile = AnalysesRepository.getRepositoryFile();
            }
            ExtendedFileChooser chooser = new ExtendedFileChooser(getClass());
            if (lastUsedAnalyseFile != null) {
                chooser.setCurrentDirectory(lastUsedAnalyseFile);
            }
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showSaveDialog(AnalysesRepositoryFrame.dialog) == ExtendedFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                if (fileToSave == null) {
                    return;
                }

                // wenn nicht selektiert ist, dann alles speichern, sonst nur die Selektion
                List<XMLAnalysis> analysenToExport;
                int[] selection = AnalysesRepositoryFrame.table.getSelectedRows();
                if (selection.length == 0) {
                    analysenToExport = AnalysesRepositoryFrame.analysen;
                } else {
                    analysenToExport = new ArrayList<>();
                    for (int i = selection.length - 1; i >= 0; i--) {
                        analysenToExport.add(AnalysesRepositoryFrame.analysen.get(selection[i]));
                    }
                }

                AnalysesRepository.saveAnalyseFile(fileToSave, analysenToExport);
                lastUsedAnalyseFile = fileToSave;
            }
            if (lastUsedAnalyseFile != AnalysesRepository.getRepositoryFile()) {
                AnalysesRepositoryFrame.analyseFile = lastUsedAnalyseFile;
            } else {
                AnalysesRepositoryFrame.analyseFile = null;
            }
            AnalysesRepositoryFrame.refreshActionStates();
        }
    };

    /**
     * Aktion für das Zurücksetzen des Repositories auf das Standardrepository
     */
    static final Action ACTION_LOAD_STANDARD_REPOSITORY = new AbstractAction(getResString("ana_load_standard_repository")) {

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (AnalysesRepositoryFrame.analysisChanged == true) {
                int answer = JOptionPane.showConfirmDialog(AnalysesRepositoryFrame.dialog, getResString("ana_load_standard_repository_question"), getResString("ana_load_standard_repository"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    AnalysesRepositoryFrame.setAnalyses(AnalysesRepository.getXMLAnalyses());
                    AnalysesRepositoryFrame.analysisChanged = false;
                    AnalysesRepositoryFrame.table.update();
                    AnalysesRepositoryFrame.refreshActionStates();
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return AnalysesRepositoryFrame.analysisChanged;
        }
    };

    /**
     * Action für das Speichern des Repositories
     */
    static final Action ACTION_SAVE_REPOSITORY = new AbstractAction(getResString("ana_save_repository")) {

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (!AnalysesRepository.setXMLAnalysen(AnalysesRepositoryFrame.analysen)) {
                return;
            }
            AnalysesRepository.saveRepository();
            AnalysesRepositoryFrame.analysisChanged = false;
            AnalysesRepositoryFrame.refreshActionStates();
        }

        @Override
        public boolean isEnabled() {
            return AnalysesRepositoryFrame.analysisChanged;
        }
    };

    /**
     * Action für das Zurücksetzen des Analyseergebnisses in der Grafik
     */
    static final Action ACTION_RESET_ANALYSIS_RESULT = new AbstractAction(getResString("reset_result")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            GraphDocument gd = Static.getSelectedDoc();
            if (gd == null) {
                return;
            }
            gd.clearAnalysisResult();
            AnalysesRepositoryFrame.refreshActionStates();
        }
    };

    /**
     * Action für das starten der ausgewählten Analysen
     */
    static final Action ACTION_START_ANALYSIS = new AbstractAction(getResString("ana_start")) {

        @Override
        public void actionPerformed(final ActionEvent e) {
            GraphDocument doc = Static.getSelectedDoc();
            if (doc == null) {
            }
            int[] selection = AnalysesRepositoryFrame.table.getSelectedRows();
            for (int i = 0; i < selection.length; i++) {
                XMLAnalysis query = AnalysesRepositoryFrame.analysen.get(selection[i]);
                query.setAnalysisResult(doc);
            }
            AnalysesRepositoryFrame.refreshActionStates();
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
    static final Action ACTION_NEW_ANALYSIS = new AbstractAction(getResString("ana_new")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                AnalysesRepositoryFrame.addAnalysis(XMLAnalysis.createAnalysis("", ""), true);
            } catch (SAXException ex) {
                Log.show(Log.ERROR, getResString("ANALYSIS_CANT_CREATE") + "\n" + ex.getMessage(), ex);
            }
            AnalysesRepositoryFrame.analysisChanged = true;
            AnalysesRepositoryFrame.table.update();
            AnalysesRepositoryFrame.refreshActionStates();
        }
    };

    /**
     * Action für das starten der ausgewählten Analysen
     */
    static final Action ACTION_DELETE_ANALYSIS = new AbstractAction(getResString("ana_delete")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            int[] selection = AnalysesRepositoryFrame.table.getSelectedRows();
            for (int i = selection.length - 1; i >= 0; i--) {
                AnalysesRepositoryFrame.analysen.remove(selection[i]);
            }
            AnalysesRepositoryFrame.analysisChanged = true;
            AnalysesRepositoryFrame.table.update();
            AnalysesRepositoryFrame.refreshActionStates();
        }

        @Override
        public boolean isEnabled() {
            if (AnalysesRepositoryFrame.table == null) {
                return false;
            }
            return AnalysesRepositoryFrame.table.getSelectedRows().length > 0;
        }
    };

    /**
     * Action für das Starten des {@link AnalysisEditor}
     */
    static final Action ACTION_ANALYSIS_EDITOR = new AbstractAction(getResString("analysis_editor")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            AnalysisEditor.showDialog(AnalysesRepositoryFrame.dialog);
        }
    };

}
