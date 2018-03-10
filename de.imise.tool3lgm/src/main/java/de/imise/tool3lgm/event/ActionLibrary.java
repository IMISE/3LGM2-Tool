package de.imise.tool3lgm.event;

import static de.imise.tool3lgm.Static.getSelectedDoc;
import static de.imise.tool3lgm.Static.getSelectedGDCollection;
import static de.imise.tool3lgm.Static.getTool;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayablePluralName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getGraphViewDefinition;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.action.ChangeLocaleAction;
import de.imise.tool3lgm.event.action.GlobalOptionAction;
import de.imise.tool3lgm.event.action.GraphDocumentAction;
import de.imise.tool3lgm.event.action.GraphFrameAction;
import de.imise.tool3lgm.event.action.GraphSelectedRealNodeAction;
import de.imise.tool3lgm.event.action.SelectedElementsAction;
import de.imise.tool3lgm.event.action.StaticActionNew;
import de.imise.tool3lgm.event.action.SubmodelAction;
import de.imise.tool3lgm.graphtools.analyse.context.AnalyseEditor;
import de.imise.tool3lgm.graphtools.analyse.context.AnalyseRepositoryFrame;
import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.dialog.GraphViewOptionsDialog;
import de.imise.tool3lgm.graphtools.dialog.GraphicPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.LayoutEditor;
import de.imise.tool3lgm.graphtools.dialog.RMIPropertyPanel;
import de.imise.tool3lgm.graphtools.dialog.SearchDialog;
import de.imise.tool3lgm.graphtools.dialog.SzenarioDialog;
import de.imise.tool3lgm.graphtools.matrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeType;
import de.imise.tool3lgm.graphtools.model.GDCollectionImExportHandler;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.dialog.declaration.UserFieldDeclarationDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.GraphAreaToolbarManager;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.gui.ToolBar;
import de.imise.tool3lgm.gui.ToolSplashScreen;
import de.imise.tool3lgm.help.Help;
import de.imise.tool3lgm.imexport.DataExportModule;
import de.imise.tool3lgm.imexport.DataImportModule;
import de.imise.tool3lgm.imexport.graphml.GraphmlExporter;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.IntProperty;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.tool3lgm.xslt.WebExportDialog;
import de.imise.tool3lgm.xslt.XMLExportDialog;
import de.imise.util.Alphabetical;
import de.imise.util.Pair;
import de.imise.util.image.ComponentAsImageExportHandler;
import de.imise.util.swing.dialog.DirectoryChooser;
import de.imise.util.swing.event.ExtendedAction;
import de.imise.util.swing.event.ToggleAction;

/**
 * Sammlung global einsetzbarer {@link Action}s.
 *
 * @author fstephan
 */
public class ActionLibrary {

    /**
     * Actions für das Erstellen, Laden, Speichern, ... von Dateien.
     *
     * @author fstephan
     */
    public static class FileActions {

        /** Öffnen eines neuen Models */
        public static final Action ACTION_NEW_MODEL = new StaticActionNew(ActionIdentifier.ACTION_NEW_MODEL) {
            @Override
            protected void actionPerformed() {
                getTool().openFile(false);
            }
        };

        /** Öffnen eines bestehenden Models */
        public static final Action ACTION_OPEN_MODEL = new StaticActionNew(ActionIdentifier.ACTION_OPEN_MODEL, PPP) {
            @Override
            protected void actionPerformed() {
                getTool().openFile(true);
            }
        };

        /** Speichern des Models an bekannter Stelle */
        public static final Action ACTION_SAVE_MODEL = new GraphDocumentAction(ActionIdentifier.ACTION_SAVE_MODEL) {
            @Override
            protected void actionPerformed() {
                Tool3lgm tool3lgm = getTool();
                if (!tool3lgm.fileSave(false)) {
                    JOptionPane.showMessageDialog(tool3lgm, getResString("save_failed"), "", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        /** Speichern des Models an neuer Stelle */
        public static final Action ACTION_SAVE_MODEL_AS = new GraphDocumentAction(ActionIdentifier.ACTION_SAVE_MODEL_AS, PPP) {
            @Override
            protected void actionPerformed() {
                Tool3lgm tool3lgm = getTool();
                if (!tool3lgm.fileSave(true)) {
                    JOptionPane.showMessageDialog(tool3lgm, getResString("save_failed"), "", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        /** Schließen des Models */
        public static final Action ACTION_CLOSE_MODEL = new GraphDocumentAction(ActionIdentifier.ACTION_CLOSE_MODEL) {
            @Override
            protected void actionPerformed() {
                getTool().fileClose();
            }
        };

        /** Zeigt die Beschreibung des Tools an */
        public static final Action ACTION_SHOW_MODEL_DESCRIPTION_FRAME = new GraphDocumentAction(ActionIdentifier.ACTION_SHOW_MODEL_DESCRIPTION_FRAME, PPP) {
            @Override
            protected void actionPerformed() {
                getSelectedGDCollection().showDescriptionFrame(true);
            }
        };

        /**
         * Actions für den Daten-Import
         *
         * @author fstephan
         */
        public static class ImportActions {

            /** Öffnet einen Dialog zum Import von Teilmodellen */
            public static final Action ACTION_IMPORT_SUBMODEL = new GraphDocumentAction(ActionIdentifier.ACTION_IMPORT_SUBMODEL, PPP) {
                @Override
                protected void actionPerformed() {
                    GDCollectionImExportHandler.importSzenarios();
                }
            };

            /** Öffnet einen Dialog zum Import von Modellen */
            public static final Action ACTION_IMPORT_MODEL = new GraphDocumentAction(ActionIdentifier.ACTION_IMPORT_MODEL, PPP) {
                @Override
                protected void actionPerformed() {
                    GDCollectionImExportHandler.importModel();
                }
            };

            /** Öffnet einen Dialog zum Import von Daten im tab-separierten Format */
            public static final Action ACTION_IMPORT_DATA = new GraphDocumentAction(ActionIdentifier.ACTION_IMPORT_DATA, PPP) {
                @Override
                protected void actionPerformed() {
                    new DataImportModule(getSelectedGDCollection());
                }
            };
        }

        /**
         * Actions für den Daten-Export
         *
         * @author fstephan
         */
        public static class ExportActions {

            /** öffnet einen Dialog zum Export des Models als Grafik-Datei */
            public static final Action ACTION_EXPORT_GRAPHIC = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_GRAPHIC, PPP) {

                @Override
                protected void actionPerformed() {
                    AbstractInternalFrame selframe = Static.getActiveFrame();
                    if (selframe instanceof InternalGraphFrame) {
                        InputGraphArea iga = ((InternalGraphFrame) selframe).getInputGraphArea();
                        iga.setPaintState(PaintState.SAVE_IMAGE_AS_FILE);
                        ComponentAsImageExportHandler.createFile(iga);
                        iga.setPaintState(PaintState.REGULAR);
                    } else if (selframe instanceof MatrixViewInternalFrame) {
                        JScrollPane sp = selframe.getScrollPane();
                        Dimension size = sp.getSize();
                        sp.setSize(sp.getMaximumSize());
                        sp.revalidate();
                        ComponentAsImageExportHandler.createFile(sp);
                        sp.setSize(size);
                        sp.revalidate();
                    }
                }

                @Override
                public boolean isEnabled() {
                    //sowohl Grafiken als auch Matrizen kann man (grafisch) exportieren
                    return super.isEnabled() && (Static.isActiveFrameGraphFrame() || Static.isActiveFrameMatrixFrame());
                }
            };

            /** öffnet einen Dialog zum Export des Models als graphml-Datei */
            public static final Action ACTION_EXPORT_GRAPHML_YED = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_GRAPHML_YED, PPP) {
                @Override
                protected void actionPerformed() {
                    File path = DirectoryChooser.showDialog(Static.getTool(), "graphml");// den String braucht man nicht auslagern
                    if (path != null) {
                        //                    File path = new File("/Users/astruebi/Projekte/eclipse/IMISE/graphml-export");
                        //                    File path = new File("/Users/astruebi/Dropbox/2017_Bachelorarbeit_MBraungardt/Mapping/test_exports");
                        //                    new GraphmlExporter(path, Static.getSelectedGDCollection()).writeYFilesGraphml();
                        new GraphmlExporter(path, Static.getSelectedGDCollection()).writeYEdGraphml();
                    }
                }
            };

            /** öffnet einen Dialog zum Export des Models als graphml-Datei */
            public static final Action ACTION_EXPORT_GRAPHML_YFILES = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_GRAPHML_YFILES, PPP) {
                @Override
                protected void actionPerformed() {
                    File path = DirectoryChooser.showDialog(Static.getTool(), "graphml");// den String braucht man nicht auslagern
                    if (path != null) {
                        //                  File path = new File("/Users/astruebi/Projekte/eclipse/IMISE/graphml-export");
                        //                  File path = new File("/Users/astruebi/Dropbox/2017_Bachelorarbeit_MBraungardt/Mapping/test_exports");
                        new GraphmlExporter(path, Static.getSelectedGDCollection()).writeYFilesGraphml();
                        //                    new GraphmlExporter(path, Static.getSelectedGDCollection()).writeYEdGraphml();
                    }
                }
            };

            /** Öffnet einen Dialog zur Anwendung von XSL-Scripts auf das Modell */
            public static final Action ACTION_EXPORT_XSLT = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_XSLT, PPP) {
                @Override
                protected void actionPerformed() {
                    // der Dialog zeigt sich im Konstuktor selbst an
                    new XMLExportDialog(getTool(), getSelectedGDCollection());
                }
            };

            /** Öffnet einen Dialog zum Export eines Teilmodells */
            public static final Action ACTION_EXPORT_SUBMODEL = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_SUBMODEL, PPP) {
                @Override
                protected void actionPerformed() {
                    SzenarioDialog.showExportDialog(getTool(), getSelectedGDCollection());
                }
            };

            /** Öffnet einen Dialog zum Export des gesamten Models als HTML-Site */
            public static final Action ACTION_EXPORT_HTML = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_HTML, PPP) {
                @Override
                protected void actionPerformed() {
                    WebExportDialog.showWebExportDialog(getTool(), getSelectedGDCollection());
                }
            };

            /** Öffnet einen Dialog zum Export einzelner Elemente in tab-separiertem Format */
            public static final Action ACTION_EXPORT_DATA = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_DATA, PPP) {
                @Override
                protected void actionPerformed() {
                    DataExportModule.exportData(getSelectedDoc());
                }
            };
        }

        /*
         * Die Actions zum öffnen der zuletzt verwendeten Dateien befinden sich in der Klasse {@link DynamicActions}
         */

        /** Beenden des Programms */
        public static final Action ACTION_EXIT = new StaticActionNew(ActionIdentifier.ACTION_EXIT) {
            @Override
            protected void actionPerformed() {
                Tool3lgm tool3lgm = getTool();
                tool3lgm.windowClosing(new WindowEvent(tool3lgm, WindowEvent.WINDOW_CLOSING));
            }
        };
    }

    /**
     * Actions für Analysen
     *
     * @author fstephan
     */
    public static class AnalysisActions {

        /** Zeigt das XMLAnalyse-Repository an */
        public static final Action ACTION_ANALYSIS_OPEN_REPOSITORY = new StaticActionNew(ActionIdentifier.ACTION_ANALYSIS_OPEN_REPOSITORY, PPP) {
            @Override
            protected void actionPerformed() {
                AnalyseRepositoryFrame.showDialog();
            }
        };

        /** Öffnet den XMLAnalyse-Editor */
        public static final Action ACTION_ANALYSIS_OPEN_EDITOR = new StaticActionNew(ActionIdentifier.ACTION_ANALYSIS_OPEN_EDITOR, PPP) {
            @Override
            protected void actionPerformed() {
                AnalyseEditor.showDialog(getTool());
            }
        };

        /** Setzt alle XMLAnalyse-Ergebnisse zurück */
        public static final Action ACTION_ANALYSIS_RESET_RESULT = new GraphDocumentAction(ActionIdentifier.ACTION_ANALYSIS_RESET_RESULT) {
            @Override
            protected void actionPerformed() {
                getSelectedDoc().clearAnalysisResult();
            }
        };

        /** Aktiviert die Redundanz-XMLAnalyse */
        public static final Action ACTION_ANALYSIS_REDUNDANCY = new GraphDocumentAction(ActionIdentifier.ACTION_ANALYSIS_REDUNDANCY, PPP) {
            @Override
            protected void actionPerformed() {
                RedundancyAnalysis.getReport(getSelectedGDCollection());
            }
        };

        /**
         * Zeigt einen ColorChooser zum auswählen der Farbe, mit der Analyseergnisse in der
         * Grafik hervorgehovben werden
         */
        public static final Action ACTION_ANALYSIS_CHOOSE_GRAPH_ANALYSIS_RESULT_COLOR = new StaticActionNew(ActionIdentifier.ACTION_ANALYSIS_CHOOSE_GRAPH_ANALYSIS_RESULT_COLOR, PPP) {
            @Override
            public void actionPerformed() {
                NodeRenderer.analysisColor = JColorChooser.showDialog(getTool(), getText(), NodeRenderer.analysisColor);
            }
        };

    }

    public static class ContextActions {

        public static final Action ACTION_SHOW_ELEMENT_PROPERTY_DIALOG = new SelectedElementsAction(ActionIdentifier.ACTION_SHOW_ELEMENT_PROPERTY_DIALOG) {
            @Override
            public void actionPerformed() {
                Static.getSelectedDoc().showPropertyDialog();
            }
        };

        public static final Action MODEL_ACTION_HIDE_ELEMENT_CONFIGS = createMODEL_ACTION_SHOW_HIDE_ELEMENT_CONFIGS(false);

        public static final Action MODEL_ACTION_SHOW_ELEMENT_CONFIGS = createMODEL_ACTION_SHOW_HIDE_ELEMENT_CONFIGS(true);

        public static final Action createMODEL_ACTION_SHOW_HIDE_ELEMENT_CONFIGS(final boolean show) {
            return new GraphSelectedRealNodeAction(show ? GDCommands.MODEL_ACTION_SHOW_ELEMENT_CONFIGS : GDCommands.MODEL_ACTION_HIDE_ELEMENT_CONFIGS) {
                @Override
                public boolean isEnabled() {
                    if (!super.isEnabled()) {
                        return false;
                    }
                    for (ElementContainer ec : Static.iterableSelectedRealElementContainer()) {
                        if (ModelConstants.isInterLayerStartClass(ec.getElement().getClass())) {
                            boolean hasVisibleConfigs = ((InterLayerConnectedNodeContainer) ec).isShowInterLayerConnections();
                            if (show != hasVisibleConfigs) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };
        }

        public static final Action MODEL_ACTION_HIDE_ALL_LAYER_CONFIGS = createMODEL_ACTION_SHOW_HIDE_LAYER_CONFIGS(false);

        public static final Action MODEL_ACTION_SHOW_ALL_LAYER_CONFIGS = createMODEL_ACTION_SHOW_HIDE_LAYER_CONFIGS(true);

        public static final Action createMODEL_ACTION_SHOW_HIDE_LAYER_CONFIGS(final boolean show) {
            return new GraphFrameAction(show ? GDCommands.MODEL_ACTION_SHOW_ALL_LAYER_CONFIGS : GDCommands.MODEL_ACTION_HIDE_ALL_LAYER_CONFIGS) {
                @Override
                public boolean isEnabled() {
                    if (!super.isEnabled()) {
                        return false;
                    }
                    LayerContainer lc = Static.getSelectedDoc().getActiveLayer();
                    for (ElementContainer ec : lc.getKnoten()) {
                        if (ModelConstants.isInterLayerStartClass(ec.getElement().getClass())) {
                            boolean hasVisibleConfigs = ((InterLayerConnectedNodeContainer) ec).isShowInterLayerConnections();
                            if (show != hasVisibleConfigs) {
                                return true;
                            }
                        }
                    }
                    return show != lc.isShowInterLayerConnections();
                }
            };
        }

        /*
         * public static final Action VERIFICATION = new
         * CommandAction(ActionIdentifier.verification,GDCommands.LINK); public static final Action
         * INTERACTIVE = new CommandAction(ActionIdentifier.interactive,GDCommands.LINK); public
         * static final Action COMMANDLINE = new
         * CommandAction(ActionIdentifier.commandline,GDCommands.LINK); public static final Action
         * SHOW_QUEUE = new CommandAction(ActionIdentifier.show_queue,GDCommands.LINK); public
         * static final Action TEST = new CommandAction(ActionIdentifier.test,GDCommands.LINK);
         */

    }

    /**
     * Sammlung von Methoden, die häufig verwendete {@link Action}s generieren. <br>
     * Das Generieren ist notwendig, weil sich diese Actions wärend der Programmausführung ändern
     * können und damit nicht statisch definierbar sind.
     *
     * @author fstephan
     */
    public static class DynamicActions {

        /**
         * Gibt ein Array zurück, dessen Elemente Actions zum Öffnen der zuletzt verwendeten Dateien
         * sind
         */
        public static final Action[] getLastUsedFilesOpenActions() {
            List<String> fileNames = UserProperties.getListValues(StringProperty.LAST_USED_MODEL_FILES);
            List<File> files = new ArrayList<>();
            for (String fileName : fileNames) {
                File file = new File(fileName);
                try {
                    if (file.canRead()) {
                        files.add(file);
                    }
                } catch (Exception e) {
                    //nichts machen
                }
            }
            Action[] actions = new Action[files.size()];
            for (int i = 0; i < actions.length; i++) {
                final File file = files.get(i);
                actions[i] = new AbstractAction(file.getName()) {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Static.getTool().openFile(true, file);
                    }
                };
            }
            // Die Actions werden hier nicht alphabetisch sortiert, da die durch die
            // UserProperties gegebene Reihenfolge entscheidend ist
            return actions;
        }

        /** Gibt ein Array zurück, dessen Elemente Actions zum Öffnen der Teilmodell-Frames sind */
        public static final Action[] getSelectInternalFrameActions() {

            AbstractInternalFrame[] internalFrames = Static.getAllFrames();
            AbstractInternalFrame selectedFrame = Static.getActiveFrame();

            Action[] actions = new Action[internalFrames.length];
            int index = 0, next;
            for (final AbstractInternalFrame internalFrame : internalFrames) {

                if (internalFrame != selectedFrame) {
                    next = ++index;
                } else {
                    next = 0;
                }

                actions[next] = new AbstractAction(internalFrame.getTitle()) {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        try {
                            if (!internalFrame.isSelected()) {
                                internalFrame.setSelected(true);
                            }
                        } catch (PropertyVetoException exp) {
                            Log.show(Log.FATAL, getResString("FehlerAllgemein"), exp);
                        }
                    }
                };
            }
            return actions;
        }

    }

    /**
     * Actions, wie z.B. UNDO, REDO, Löschen, ...
     *
     * @author fstephan
     */
    public static class EditActions {

        /** Macht letzte Änderung rückgängig */
        public static final Action ACTION_UNDO = new GraphDocumentAction(ActionIdentifier.ACTION_UNDO) {
            @Override
            protected void actionPerformed() {
                getSelectedDoc().undo();
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedGDCollection().getTman().isUndoAvailable();
            }
        };

        /** Macht letztes UNDO rückgängig */
        public static final Action ACTION_REDO = new GraphDocumentAction(ActionIdentifier.ACTION_REDO) {
            @Override
            protected void actionPerformed() {
                getSelectedDoc().redo();
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedGDCollection().getTman().isRedoAvailable();
            }
        };

        /** Öffnet ein Suchen-Fenster */
        public static final Action ACTION_SEARCH = new GraphDocumentAction(ActionIdentifier.ACTION_SEARCH, PPP) {
            @Override
            protected void actionPerformed() {
                SearchDialog sd = new SearchDialog(Static.getTool());
                sd.showDialog();
            }
        };

        /** Wählt alle Elemente im Teilmodell aus */
        public static final Action SELECT_ALL = new GraphDocumentAction(ActionIdentifier.ACTION_SELECT_ALL) {
            @Override
            protected void actionPerformed() {
                Static.showProgressDialog();
                Static.setProgressDialogTitle(getResString("PROGRESS_SELECT_ALL"));
                getSelectedDoc().selectAll();
                Static.closeProgressDialog();
            }
        };
    }

    /**
     * Actions Extras, wie z.B. Benutzdefinierte Felder
     *
     * @author fstephan
     */
    public static class ExtrasActions {

        /** Öffnet einen Dialog zum Anlegen benutzerdefinierter Eigenschaftsfelder */
        public static final Action ACTION_OPEN_USERFIELD_DEFINITION_DIALOG = new GraphDocumentAction(ActionIdentifier.ACTION_OPEN_USERFIELD_DEFINITION_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                if (!ModelConstants.getDialogs().isEmpty()) {
                    JOptionPane.showMessageDialog(getTool(), getResString("message_close_all_dialogs"));
                    return;
                }
                UserFieldDeclarationDialog.showDialog(getTool(), getSelectedGDCollection());
            }
        };

        /** Öffnet einen Editor zur Eingabe von Kennzahlen */
        public static final Action ACTION_OPEN_USERFIELD_VALUE_EDITOR_DIALOG = new GraphDocumentAction(ActionIdentifier.ACTION_OPEN_USERFIELD_VALUE_EDITOR_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                if (!ModelConstants.getDialogs().isEmpty()) {
                    JOptionPane.showMessageDialog(getTool(), getResString("message_close_all_dialogs"));
                    return;
                }
                if (getSelectedDoc() != null) {
                    UserFieldEditorDialog.getDialog(getTool(), getSelectedGDCollection()).setVisible(true);
                }
                return;
            }
        };

    }

    /**
     * Actions für die Benutzer-Hilfe
     *
     * @author fstephan
     */
    public static class HelpActions {

        /** Funktioniert nicht */
        public static final ExtendedAction ACTION_OPEN_HELP_DIALOG = new StaticActionNew(ActionIdentifier.ACTION_OPEN_HELP_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                Help.showHelp();
            }
        };

        /** Aktiviert die Direkthilfe */
        public static final ExtendedAction ACTION_ACTIVATE_DIRECT_HELP = new StaticActionNew(ActionIdentifier.ACTION_ACTIVATE_DIRECT_HELP) {
            @Override
            public void actionPerformedWithEvent(final ActionEvent e) {
                Help.getHelp().getDisplayHelpAfterTracking().actionPerformed(e);
            }
        };

        /** Zeigt eine lokale Webseite mit Themen zur weiteren Modellnutzung an */
        public static final ExtendedAction ACTION_SHOW_INFORMATION_SYSTEM_EVALUATION_TUTORIAL = new StaticActionNew(ActionIdentifier.ACTION_SHOW_INFORMATION_SYSTEM_EVALUATION_TUTORIAL, PPP) {
            @Override
            public void actionPerformed() {
                BrowseUtils.browseRelativeFileFromResource("auswhilfe_datei");
            }
        };

        /** öffnet die Online-Hilfe-Seite */
        public static final ExtendedAction ACTION_SHOW_ONLINE_HELP = new StaticActionNew(ActionIdentifier.ACTION_SHOW_ONLINE_HELP, PPP) {
            @Override
            public void actionPerformed() {
                BrowseUtils.browseUrlFromResource("3lgm2tool_support_website");
            }
        };

        /** öffnet die 3lgm-Homepage */
        public static final ExtendedAction ACTION_SHOW_3LGM_WEB_SITE = new StaticActionNew(ActionIdentifier.ACTION_SHOW_3LGM_WEB_SITE, PPP) {
            @Override
            public void actionPerformed() {
                BrowseUtils.browseUrlFromResource("3lgm2_website");
            }
        };

        /** öffnet das Beispielmodell */
        public static final ExtendedAction ACTION_OPEN_EXAMPLE_MODEL_FILE = new StaticActionNew(ActionIdentifier.ACTION_OPEN_EXAMPLE_MODEL_FILE) {
            @Override
            public void actionPerformed() {
                Static.getTool().openFile(false, Tool3lgmConstants.EXAMPLE_MODEL_FILE);
            }
        };

        /** Öffnet den Ordner mit dem Beispielmodellen bzw die sog. Modellbibliothek mit Modellen von Standards */
        public static final ExtendedAction ACTION_OPEN_FILE_CHOSSER_IN_MODEL_LIBRARY = new StaticActionNew(ActionIdentifier.ACTION_OPEN_FILE_CHOSSER_IN_MODEL_LIBRARY, PPP) {
            @Override
            public void actionPerformed() {
                BrowseUtils.browseRelativeFileFromResource("modlib_verz");
            }
        };

        /** Zeigt die Programm-Info an */
        public static final ExtendedAction ACTION_OPEN_ABOUT_DIALOG = new StaticActionNew(ActionIdentifier.ACTION_OPEN_ABOUT_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                ToolSplashScreen.getInfoDialog();
            }
        };

        //        /** öffnet ein Fenster zum Import von Lizenzdateien */
        //        public static final ExtendedAction ACTION_IMPORT_LICENSE_FILE = new StaticAction(ActionIdentifier.ACTION_IMPORT_LICENSE_FILE) {
        //
        //            @Override
        //            public void actionPerformed(final ActionEvent e) {
        //                Static.getTool().importLicenseFile();
        //            }
        //        };
    }

    /**
     * Actions für das Einfügen von Elementen in die 3 Ebenen.
     *
     * @author fstephan
     */
    public static class CreateElementActions {

        /** Array aller Insert-Actions für die Fachliche Ebene */
        public static final Action[] DOMAIN_LAYER_CREATEABLE_NODES_ACTIONS = getActions(ModelConstants.CREATABLE_DOMAIN_LAYER_NODES);

        /** Array aller Insert-Actions für die Logische Werkzeugebene */
        public static final Action[] LOGICAL_TOOL_LAYER_CREATEABLE_NODES_ACTIONS = getActions(ModelConstants.CREATABLE_LOGICAL_LAYER_NODES);

        /** Array aller Insert-Actions für die Physische Werkzeugebene */
        public static final Action[] PHYSICAL_TOOL_LAYER_CREATEABLE_NODES_ACTIONS = getActions(ModelConstants.CREATABLE_PHYSICAL_LAYER_NODES);

        /** Gibt alle Actions zum Erzeugen von {@link ModelElement}en der spezifizierten Klassen wieder */
        private static Action[] getActions(final Class<? extends ModelElement>[] treeCreatableLayerNodes) {
            GraphDocumentAction[] actions = new GraphDocumentAction[treeCreatableLayerNodes.length];
            for (int c = 0; c < treeCreatableLayerNodes.length; c++) {
                String actionName = ModelConstants.getDisplayableName(treeCreatableLayerNodes[c]);
                actions[c] = new GraphDocumentAction(GDCommands.MODEL_ACTION_CREATE_NODE, treeCreatableLayerNodes[c].getName(), actionName);
            }
            Alphabetical.sort(actions);
            return actions;
        }

    }

    /**
     * Actions für die grafische Darstellung von Ebenen und Elementen
     *
     * @author fstephan
     */
    public static class LayoutActions {

        public static final Action ACTION_OPEN_GLOBAL_LAYOUT_EDITOR = new SubmodelAction(ActionIdentifier.ACTION_OPEN_GLOBAL_LAYOUT_EDITOR, PPP) {
            @Override
            public void actionPerformed() {
                // der Diolog zeigt sich im Konstruktor selbst an
                new LayoutEditor(new javax.swing.JFrame(), getSelectedDoc());
            }
        };

    }

    /**
     * Actions XMLAnalyse-Optionen, Graphik-Optionen, usw.
     *
     * @author fstephan
     */
    public static class OptionsActions {

        /**
         * XMLAnalyse-Optionen
         *
         * @author fstephan
         */
        public static class Analysis {

            public static final Action[] OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS = create_OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS();

            private static final Action[] create_OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS() {
                //die Definitionen für die SimpleRedundancyAnalysis aud der AnalyseDefinition holen
                AnalysisDefinition analysisDefinition = ModelConstants.getAnalysisDefinition();
                SimpleRedundancyAnalysisDefinitions simpleRedundancyAnalysisDefinition = analysisDefinition.getSimpleRedundancyAnalysisDefinitions();
                //wenn es gültige Definitionen für die SimpleRedundancyAnalysis gibt, dann werden in dieses Array die zugehörigen Actions geschrieben
                Action[] returnActions = new StaticActionNew[simpleRedundancyAnalysisDefinition.size()];
                for (int i = 0; i < returnActions.length; i++) {
                    //Definition einer der aktuellen SimpleRedundancyAnalysis holen
                    SingleSimpleRedundancyAnalysisDefinition singleSimpleRedundancyDefinition = simpleRedundancyAnalysisDefinition.get(i);
                    GlobalOptionAction action = new GlobalOptionAction(ActionIdentifier.OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS, false, GDCollectionChangeType.ELEMENT_GRAPHICS_CHANGED) {
                        @Override
                        public void changeOption() {
                            for (GDCollection gdcoll : getTool().getCollections()) {
                                gdcoll.getMainGraphDocument().switchSimpleRedundancyAnalysisState(singleSimpleRedundancyDefinition);
                                for (Szenario szenario : gdcoll.getSzenarios()) {
                                    szenario.switchSimpleRedundancyAnalysisState(singleSimpleRedundancyDefinition);
                                }
                            }
                        }
                    };
                    String resKey = ActionIdentifier.OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS.name();
                    MetaPath metaPath = singleSimpleRedundancyDefinition.getMetaPath();
                    String startClassPluralName = getDisplayablePluralName(metaPath.getStartClass());
                    String endClassPluralName = getDisplayablePluralName(metaPath.getEndClass());
                    String fullActionDisplayName = getResString(resKey, startClassPluralName, endClassPluralName);
                    action.setText(fullActionDisplayName);
                    returnActions[i] = action;
                }
                return returnActions;
            }

        }

        /**
         * Graphik-Optionen
         *
         * @author fstephan
         */
        public static class Graphics {

            /** Öffnet ein Fenster für allgemeine Grafik-Einstellungen */
            public static final Action ACTION_PROPERTY_INT_RENDER_SETTINGS = new StaticActionNew(IntProperty.PROPERTY_INT_RENDER_SETTINGS, PPP) {
                @Override
                public void actionPerformed() {
                    GraphicPropertyDialog dialog = new GraphicPropertyDialog(getTool());
                    dialog.setVisible(true);
                }
            };

            /** Array, aller Actions, für die das Ein- und Ausblenden in der Grafik in der GraphViewDefinition angegeben wurde. */
            public static final GraphFrameAction HIDE_UNHIDE_UNASSOCIATED[] = create_HIDE_UNHIDE_UNASSOCIATED_Actions();

            /**
             * Erzeugt das Array, aller Actions, für die das Ein- und Ausblenden in der Grafik in der GraphViewDefinition angegeben wurde.
             *
             * @param hide
             * @return
             */
            private static final GraphFrameAction[] create_HIDE_UNHIDE_UNASSOCIATED_Actions() {
                List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> hidableIfNotConnected = getGraphViewDefinition().getHidableIfNotConnected();
                if (hidableIfNotConnected == null || hidableIfNotConnected.isEmpty()) {
                    return null;
                }
                GraphFrameAction[] actions = new GraphFrameAction[hidableIfNotConnected.size() * 2];
                for (int i = 0; i < actions.length; i++) {
                    Pair<Class<? extends ModelElement>, Class<? extends Edge>> hidable = hidableIfNotConnected.get(i / 2);
                    Class<? extends ModelElement> elementClass = hidable.getFirstItem();
                    final boolean hide = i % 2 == 0;
                    GraphFrameAction hideAction = new GraphFrameAction(hide ? ActionIdentifier.HIDE_UNASSOCIATED : ActionIdentifier.UNHIDE_ALL) {
                        @Override
                        public void actionPerformed() {
                            if (!isEnabled()) {
                                return;
                            }
                            String commandString;
                            if (hide) {
                                commandString = GDCommands.HIDE_UNASSOCIATED.name() + " " + elementClass.getSimpleName() + " " + hidable.getSecondItem().getSimpleName();
                            } else {
                                commandString = GDCommands.UNHIDE_ALL.name() + " " + elementClass.getSimpleName();
                            }
                            Static.getSelectedDoc().exec(commandString, TransactionManager.STANDARD_PID);
                        }
                    };
                    //das hier auf keine Fall mit static import ersetzen, weil er dann statt der GDCommands die Action nimmt, die genauso heißen und null sind
                    String resKey = hide ? GDCommands.HIDE_UNASSOCIATED.name() : GDCommands.UNHIDE_ALL.name();
                    String elementClassPluralName = getDisplayablePluralName(elementClass);
                    String fullActionDisplayName = getResString(resKey, elementClassPluralName);
                    hideAction.setText(fullActionDisplayName);
                    actions[i] = hideAction;
                }
                return actions;
            }

        }

        /**
         * Sprach-Optionen
         *
         * @author fstephan
         */
        public static class Locale {

            /** Array aller Actions, die das Umschalten auf eine andere Sprache ermöglichen */
            public static final Action[] CHANGE_LOCALE_ACTIONS = ChangeLocaleAction.getAllActions();
        }

        /** Öffnet ein Fenster zum Auswählen des RMI-Ports */
        public static final Action OPEN_RMI_SETTINGS = new StaticAction(ActionIdentifier.rmi_settings, PPP) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                // Für die Konfiguration des RMI, wir das RMIPropertyPanel einem
                // JOptionPane übergeben.
                // Nach dem OK, werden die Values des Panels abgefragt.
                RMIPropertyPanel rmip = new RMIPropertyPanel();

                int oldRegPort = UserProperties.getRMIRegistryPort();
                if (JOptionPane.showOptionDialog(null, rmip, getResString("rmi_settings"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                    UserProperties.setRMIRegistryPort(rmip.getRmiRegistryPortTextFieldValue());
                }
                if (oldRegPort != UserProperties.getRMIRegistryPort()) {
                    JOptionPane.showMessageDialog(getTool(), getResString("RMI_SETTINGS_INFO"));
                }
            }
        };
    }

    /**
     * Actions für das Erstellen, Löschen, ... von Teilmodellen
     *
     * @author fstephan
     */
    public static class SubmodelActions {

        /** Erstellt ein neues Teilmodell */
        public static final Action NEW = new StaticAction(ActionIdentifier.new_submodel, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                Szenario szenario = getSelectedCollection().createSzenario(true);
                if (szenario != null) {
                    getTool().createSzenarioFrame(szenario);
                }
            }
        };

        /** Entfernt das aktuell ausgewählte Teilmodell */
        public static final Action REMOVE = new StaticAction(ActionIdentifier.remove_submodel, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                int answer = JOptionPane.showConfirmDialog(getTool(), getResString("deleteSzenario"), getResString("warnung"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    getSelectedCollection().deleteSzenario(getSelectedDoc().getHashString(), TransactionManager.STANDARD_PID);
                }
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedDoc() instanceof Szenario;
            }
        };

        /** Öffnet ein Fenster zum Umbenennen des aktuell ausgewählten Teilmodells */
        public static final Action RENAME = new StaticAction(ActionIdentifier.rename_submodel, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                getTool().renameSzenario();
            }

            @Override
            public boolean isEnabled() {
                return getSelectedDoc() instanceof Szenario && super.isEnabled();
            }
        };
    }

    /**
     * Actions für das Wechseln der Ebenen-Ansicht, Anzeigen der Toolbars, etc.
     *
     * @author fstephan
     */
    public static class ViewActions {

        /**
         * Actions für das (De-)aktivieren der Zeichnen- und Standard-Toolbar
         *
         * @author fstephan
         */
        public static class ToolbarActions {

            /** (De-)aktiviert die Zeichnen-Toolbar */
            public static final Action OPTION_SHOW_PAINTING_TOOLBAR = new GraphFrameAction(ActionIdentifier.OPTION_SHOW_PAINTING_TOOLBAR, true) {
                @Override
                protected void actionPerformed() {
                    Tool3lgm tool = getTool();
                    GraphAreaToolbarManager graphAreaToolBarManager = tool.getGraphAreaToolBarManager();
                    graphAreaToolBarManager.setToolBarVisible(isSelected());
                }
            };

            /** (De-)aktiviert die Standard-Toolbar */
            public static final Action OPTION_SHOW_STANDARD_TOOLBAR = new StaticActionNew(ActionIdentifier.OPTION_SHOW_STANDARD_TOOLBAR, true) {
                @Override
                protected void actionPerformed() {
                    //TODO: das hier müsste eigentlich ein Funktionaufruf in Tool3lgm sein. Die Action muss nicht das Tool revalidaten!
                    Tool3lgm tool = getTool();
                    Container contentPane = tool.getContentPane();
                    ToolBar toolBar = tool.getToolBar();
                    if (isSelected()) {
                        contentPane.add(toolBar, BorderLayout.NORTH);
                    } else {
                        contentPane.remove(toolBar);
                    }
                    tool.getWorkArea().revalidate();
                }
            };
        }

        /** (De-)Aktiviert das Anzeigen des ModelBrowsers */
        public static final Action OPTION_MODEL_BROWSER_SHOW = new StaticActionNew(ActionIdentifier.OPTION_MODEL_BROWSER_SHOW, true) {
            @Override
            protected void actionPerformed() {
                getTool().showModelBrowser(isSelected());
            }
        };

        /** Wechselt zur Ein-Ebenen-Ansicht */
        private static final ExtendedAction ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE = new GraphFrameAction(ActionIdentifier.ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE) {
            @Override
            protected void actionPerformed() {
                InternalGraphFrame frame = (InternalGraphFrame) Static.getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                area.setMultiView(false);
                frame.getGraphDocument().deselectAll(false);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && ((InternalGraphFrame) Static.getActiveFrame()).getInputGraphArea().isMultiView();
            }
        };

        /** Wechselt zur Drei-Ebenen-Ansicht */
        private static final ExtendedAction ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE = new GraphFrameAction(ActionIdentifier.ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE) {
            @Override
            protected void actionPerformed() {
                InternalGraphFrame frame = (InternalGraphFrame) Static.getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                area.setMultiView(true);
                frame.getGraphDocument().deselectAll(false);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !((InternalGraphFrame) Static.getActiveFrame()).getInputGraphArea().isMultiView();
            }
        };

        /** Schaltet immer zischen den beiden Aktionen Einzellayer-Ansicht und Mehrlayer-Ansicht um */
        public static final Action ACTION_GRAPH_SWITCH_ONE_LAYER_AND_THREE_LAYER_PERSPECTIVE = new ToggleAction(ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE, ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE);

        /** Zeigt die Fachliche Ebene an, falls die Ein-Ebenen-Ansicht aktiviert ist */
        public static final Action ACTION_ACTIVATE_DOMAIN_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_DOMAIN_LAYER) {
            @Override
            protected void actionPerformed() {
                getSelectedGDCollection().setActiveLayer(ModelConstants.DOMAIN_LAYER);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedGDCollection().getActiveLayer() != ModelConstants.DOMAIN_LAYER;
            }

        };

        /** Zeigt die Logische Werzeugebene an, falls die Ein-Ebenen-Ansicht aktiviert ist */
        public static final Action ACTION_ACTIVATE_LOGICAL_TOOL_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_LOGICAL_TOOL_LAYER) {
            @Override
            protected void actionPerformed() {
                getSelectedGDCollection().setActiveLayer(ModelConstants.LOGICAL_LAYER);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedGDCollection().getActiveLayer() != ModelConstants.LOGICAL_LAYER;
            }

        };

        /** Zeigt die physische Werkzeugebene an, falls die Ein-Ebenen-Ansicht aktiviert ist */
        public static final Action ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER) {
            @Override
            protected void actionPerformed() {
                getSelectedGDCollection().setActiveLayer(ModelConstants.PHYSICAL_LAYER);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedGDCollection().getActiveLayer() != ModelConstants.PHYSICAL_LAYER;
            }

        };

        /** Öffnet einen Dialog für die Einstellung von Größe, Abstand, etc. der Ebenen */
        public static final Action ACTION_OPEN_GRAPH_VIEW_SETTINGS_DIALOG = new GraphFrameAction(ActionIdentifier.ACTION_OPEN_GRAPH_VIEW_SETTINGS_DIALOG, PPP) {
            @Override
            protected void actionPerformed() {
                new GraphViewOptionsDialog().showDialog((InternalGraphFrame) Static.getActiveFrame());
            }
        };

        /** Öffnet die Matrix-Ansicht */
        public static final Action ACTION_OPEN_MATRIX_VIEW = new GraphDocumentAction(ActionIdentifier.ACTION_OPEN_MATRIX_VIEW) {
            @Override
            public void actionPerformed() {
                getTool().createTableInternalFrame(getSelectedDoc());
            }
        };

    }

    /**
     * Actions für Fenster-Einstellungen
     *
     * @author fstephan
     */
    public static class WindowActions {

        /** Aktiviert die parallele Darstellung der (Teil-)Modelle */
        public static final Action MODELS_PARALLEL_ARRANGEMENT = new StaticAction(ActionIdentifier.models_parallel_arrangement) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                getTool().fensterNebeneinander();
            }

            @Override
            public boolean isEnabled() {
                return hasInternalFrames();
            }
        };

        /** Aktiviert das überlappende Darstellen der (Teil-)Modelle */
        public static final Action MODELS_OVERLAPPING_ARRANGEMENT = new StaticAction(ActionIdentifier.overlapping_arrangement) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                getTool().fensterUeberlappen();
            }

            @Override
            public boolean isEnabled() {
                return hasInternalFrames();
            }
        };
    }

    /** "..."-Suffix Actions */
    private static final String PPP = getResString("3points");

    /*
     * ***************************************** Ende: Actions
     * ********************************************************
     */

    /**
     * Wandelt Unterklassen dieser Library in ActionArrays um, sodass alle Actions der
     * spezifizierten Klasse im Array enthalten sind.<br>
     * Unterklassen dieser Klassen werden dabei ignoriert.
     * <p>
     * Diese Methode stellt eine potentielle Vermischung von View und Controller da und sollte deshalb nur zu Testzwecken verwendet werden.
     */
    @Deprecated
    public static Action[] toActionArray(final Class<?> clazz) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = clazz.getFields();
        Collection<Action> tmp = new ArrayList<>(fields.length);
        Object o;
        for (int i = 0; i < fields.length; i++) {
            o = new Object();
            fields[i].get(o);
            if (o instanceof Action) {
                tmp.add((Action) o);
            }
        }
        Action[] actions = new Action[tmp.size()];
        tmp.toArray(actions);
        return actions;
    }

    /** Konstruktor - Verhindert Instanziierung */
    private ActionLibrary() {
    }
}
