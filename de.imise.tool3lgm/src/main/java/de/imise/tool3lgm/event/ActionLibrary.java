package de.imise.tool3lgm.event;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Static.getSelectedDoc;
import static de.imise.tool3lgm.Static.getSelectedGDCollection;
import static de.imise.tool3lgm.Static.getTool;
import static de.imise.tool3lgm.Tool3lgmConstants.getReplacedResString;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.event.action.StaticAction.PPP;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_RENDER_SETTINGS;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_RMI_PORT;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.event.action.GlobalOptionAction;
import de.imise.tool3lgm.event.action.GraphDocumentAction;
import de.imise.tool3lgm.event.action.GraphFrameAction;
import de.imise.tool3lgm.event.action.GraphSelectedRealNodeAction;
import de.imise.tool3lgm.event.action.OpenUrlAction;
import de.imise.tool3lgm.event.action.SelectedElementsAction;
import de.imise.tool3lgm.event.action.StaticAction;
import de.imise.tool3lgm.event.action.SubmodelAction;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.analyse.context.AnalysesRepositoryFrame;
import de.imise.tool3lgm.graphtools.analyse.context.AnalysisEditor;
import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.dialog.GraphViewOptionsDialog;
import de.imise.tool3lgm.graphtools.dialog.GraphicPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.LayoutEditor;
import de.imise.tool3lgm.graphtools.dialog.ModelPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.RMIPropertyPanel;
import de.imise.tool3lgm.graphtools.dialog.SzenarioDialog;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialogsContext;
import de.imise.tool3lgm.graphtools.dialog.search.SearchDialog;
import de.imise.tool3lgm.graphtools.metamodel.AnalysesDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverter;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionImExportHandler;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.userfield.dialog.declaration.UserFieldDeclarationDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;
import de.imise.tool3lgm.gui.Tool3lgmMetaModelContextChooser;
import de.imise.tool3lgm.gui.ToolSplashScreen;
import de.imise.tool3lgm.gui.viewpane.ViewPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.help.Help;
import de.imise.tool3lgm.imexport.DataImporter;
import de.imise.tool3lgm.imexport.csv.DataExportModule;
import de.imise.tool3lgm.imexport.csv.DataImportModule;
import de.imise.tool3lgm.imexport.graphml.GraphmlExporter;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.tool3lgm.xslt.WebExportDialog;
import de.imise.tool3lgm.xslt.XMLExportDialog;
import de.imise.util.Alphabetical;
import de.imise.util.BrowseUtils;
import de.imise.util.image.ComponentAsImageExportHandler;
import de.imise.util.pair.Pair;
import de.imise.util.swing.dialog.DirectoryChooser;
import de.imise.util.swing.event.ExtendedAction;
import de.imise.util.swing.event.ToggleAction;

/**
 * Sammlung global einsetzbarer {@link Action}s.
 *
 * @author fstephan
 */
public class ActionLibrary {

    /** Konstruktor - Verhindert Instanziierung */
    private ActionLibrary() {
    }

    /**
     * Actions für das Erstellen, Laden, Speichern, ... von Dateien.
     *
     * @author fstephan
     */
    public static class FileActions {

        /** Öffnen eines neuen Models */
        public static final Action ACTION_NEW_MODEL = new StaticAction(ActionIdentifier.ACTION_NEW_MODEL) {
            @Override
            protected void actionPerformed() {
                getTool().createNewModel();
            }
        };

        /** Öffnen eines bestehenden Models */
        public static final Action ACTION_OPEN_MODEL = new StaticAction(ActionIdentifier.ACTION_OPEN_MODEL, PPP) {
            @Override
            protected void actionPerformed() {
                getTool().openModelFile(null);
            }
        };

        /** Speichern des Models an bekannter Stelle */
        public static final Action ACTION_SAVE_MODEL = new GraphDocumentAction(ActionIdentifier.ACTION_SAVE_MODEL) {
            @Override
            protected void actionPerformed() {
                Tool3lgm tool3lgm = getTool();
                if (!tool3lgm.fileSave(false)) {
                    JOptionPane.showMessageDialog(getMainFrame(), getResString("save_failed"), "", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        /** Speichern des Models an neuer Stelle */
        public static final Action ACTION_SAVE_MODEL_AS = new GraphDocumentAction(ActionIdentifier.ACTION_SAVE_MODEL_AS, PPP) {
            @Override
            protected void actionPerformed() {
                Tool3lgm tool3lgm = getTool();
                if (!tool3lgm.fileSave(true)) {
                    JOptionPane.showMessageDialog(getMainFrame(), getResString("save_failed"), "", JOptionPane.ERROR_MESSAGE);
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
                GDCollection gdcoll = getSelectedGDCollection();
                ModelPropertyDialog dialog = ModelPropertyDialog.getDialog(gdcoll);
                dialog.setVisible(true);
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

            /**
             * Öffnet einen Dialog zum Import von Daten im tab-separierten
             * Format
             */
            public static final Action ACTION_IMPORT_DATA = new GraphDocumentAction(ActionIdentifier.ACTION_IMPORT_DATA, PPP) {
                @Override
                protected void actionPerformed() {
                    new DataImportModule(getSelectedGDCollection());
                }
            };

            public static final List<Action> IMPORT_PLUGIN_ACTIONS = getImportPluginActions();

            @SuppressWarnings({
                    "rawtypes", "unchecked"
            })
            private static final List<Action> getImportPluginActions() {
                List<Action> importPluginActions = new ArrayList<>();
                List<DataImporter> dataImporters = Static.loadPlugins(DataImporter.class);
                List<ModelConverterDefinition> modelConverterDefinitions = Static.loadPlugins(ModelConverterDefinition.class);
                for (final DataImporter dataImporter : dataImporters) {
                    Class<? extends MetaModelDefinition> importMetaModelDefinitionClass = dataImporter.getImportMetaModelDefinitionClass();
                    //wenn das Metamodel, in das importiert werden soll, ein reguläres Modellierungsmetamodell ist, dann eine direkte Import-Action anbieten
                    if (Tool3lgmMetaModelContext.isRegularMetaModelDefinition(importMetaModelDefinitionClass)) {
                        Action importAction = createImportAction(dataImporter, null, ModelCategory.REGULAR);
                        importPluginActions.add(importAction);
                        importAction = createImportAction(dataImporter, null, ModelCategory.TEMPLATE);
                        importPluginActions.add(importAction);
                    }
                    //jetzt für jeden Converter, der in ein reguläres Modellierungsmetamodell übersetzt, eine Action hinzufügen, die erst importiert und dann konvertiert
                    for (ModelConverterDefinition modelConverterDefinition : modelConverterDefinitions) {
                        if (modelConverterDefinition.canConvert(importMetaModelDefinitionClass)) {
                            Action importAction = createImportAction(dataImporter, modelConverterDefinition, ModelCategory.REGULAR);
                            importPluginActions.add(importAction);
                            importAction = createImportAction(dataImporter, modelConverterDefinition, ModelCategory.TEMPLATE);
                            importPluginActions.add(importAction);
                        }
                    }
                }
                return importPluginActions;
            }

            @SuppressWarnings("rawtypes")
            private static final Action createImportAction(final DataImporter dataImporter, final ModelConverterDefinition modelConverterDefinition, final ModelCategory modelCategory) {
                Class<? extends DataImporter> dataImporterClass = dataImporter.getClass();
                String actionName = dataImporterClass.getSimpleName();
                if (modelConverterDefinition != null) {
                    MetaModelContext targetMetaModelContext = modelConverterDefinition.getTargetMetaModelContext();
                    String metaModelDisplayName = targetMetaModelContext.getMetaModelDisplayName();
                    actionName += " -> " + metaModelDisplayName + " (" + modelCategory.name() + ")";
                }
                Action importAction = new StaticAction(actionName) {
                    @Override
                    protected void actionPerformed() {
                        if (dataImporter.startImport(modelCategory)) {
                            GDCollection gdcoll = dataImporter.getCollection();
                            if (modelConverterDefinition != null) {
                                gdcoll = ModelConverter.convert(modelConverterDefinition, gdcoll);
                            }
                            Static.getTool().openModel(gdcoll);
                        }
                    }
                };
                return importAction;
            }

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
                    ViewPaneFrameComponent selframe = Static.getActiveFrame();
                    if (selframe instanceof GraphViewPaneFrameComponent) {
                        InputGraphArea iga = ((GraphViewPaneFrameComponent) selframe).getInputGraphArea();
                        iga.setPaintState(PaintState.SAVE_IMAGE_AS_FILE);
                        ComponentAsImageExportHandler.createFile(iga);
                        iga.setPaintState(PaintState.REGULAR);
                    } else { //MatrixView
                        ViewPane viewPane = selframe.getViewPane();
                        JScrollPane sp = viewPane.getScrollPane();
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
                    File path = DirectoryChooser.showDialog(getMainFrame(), "graphml");// den String braucht man nicht auslagern
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
                    File path = DirectoryChooser.showDialog(getMainFrame(), "graphml");// den String braucht man nicht auslagern
                    if (path != null) {
                        //                  File path = new File("/Users/astruebi/Projekte/eclipse/IMISE/graphml-export");
                        //                  File path = new File("/Users/astruebi/Dropbox/2017_Bachelorarbeit_MBraungardt/Mapping/test_exports");
                        new GraphmlExporter(path, Static.getSelectedGDCollection()).writeYFilesGraphml();
                        //                    new GraphmlExporter(path, Static.getSelectedGDCollection()).writeYEdGraphml();
                    }
                }
            };

            /**
             * Öffnet einen Dialog zur Anwendung von XSL-Scripts auf das Modell
             */
            public static final Action ACTION_EXPORT_XSLT = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_XSLT, PPP) {
                @Override
                protected void actionPerformed() {
                    // der Dialog zeigt sich im Konstuktor selbst an
                    new XMLExportDialog(getMainFrame(), getSelectedGDCollection());
                }
            };

            /** Öffnet einen Dialog zum Export eines Teilmodells */
            public static final Action ACTION_EXPORT_SUBMODEL = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_SUBMODEL, PPP) {
                @Override
                protected void actionPerformed() {
                    SzenarioDialog.showExportDialog(getMainFrame(), getSelectedGDCollection());
                }
            };

            /**
             * Öffnet einen Dialog zum Export des gesamten Models als HTML-Site
             */
            public static final Action ACTION_EXPORT_HTML = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_HTML, PPP) {
                @Override
                protected void actionPerformed() {
                    WebExportDialog.showWebExportDialog(getMainFrame(), getSelectedGDCollection());
                }
            };

            /**
             * Öffnet einen Dialog zum Export einzelner Elemente in
             * tab-separiertem Format
             */
            public static final Action ACTION_EXPORT_DATA = new GraphDocumentAction(ActionIdentifier.ACTION_EXPORT_DATA, PPP) {
                @Override
                protected void actionPerformed() {
                    DataExportModule.exportData(getSelectedDoc());
                }
            };
        }

        /*
         * Die Actions zum öffnen der zuletzt verwendeten Dateien befinden sich
         * in der Klasse {@link DynamicActions}
         */

        /** Beenden des Programms */
        public static final Action ACTION_EXIT = new StaticAction(ActionIdentifier.ACTION_EXIT) {
            @Override
            protected void actionPerformed() {
                Tool3lgm tool3lgm = getTool();
                tool3lgm.close();
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
        public static final Action ACTION_ANALYSIS_OPEN_REPOSITORY = new GraphDocumentAction(ActionIdentifier.ACTION_ANALYSIS_OPEN_REPOSITORY, PPP) {
            @Override
            protected void actionPerformed() {
                AnalysesRepositoryFrame.showDialog();
            }
        };

        /** Öffnet den XMLAnalyse-Editor */
        public static final Action ACTION_ANALYSIS_OPEN_EDITOR = new GraphDocumentAction(ActionIdentifier.ACTION_ANALYSIS_OPEN_EDITOR, PPP) {
            @Override
            protected void actionPerformed() {
                AnalysisEditor.showDialog(getMainFrame(), Static.getSelectedMetaModel());
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
         * Zeigt einen ColorChooser zum auswählen der Farbe, mit der
         * Analyseergnisse in der Grafik hervorgehovben werden
         */
        public static final Action ACTION_ANALYSIS_CHOOSE_GRAPH_ANALYSIS_RESULT_COLOR = new StaticAction(ActionIdentifier.ACTION_ANALYSIS_CHOOSE_GRAPH_ANALYSIS_RESULT_COLOR, PPP) {
            @Override
            public void actionPerformed() {
                NodeRenderer.analysisColor = JColorChooser.showDialog(getMainFrame(), getText(), NodeRenderer.analysisColor);
            }
        };

    }

    public static class ContextActions {

        public static final Action ACTION_SHOW_ELEMENTS_PROPERTY_DIALOG = new SelectedElementsAction(ActionIdentifier.ACTION_SHOW_ELEMENTS_PROPERTY_DIALOG) {
            @Override
            public void actionPerformed() {
                Static.showPropertyDialogOfLastSelected();
            }
        };

        public static final GraphSelectedRealNodeAction MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_OFF = createMODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY(false);

        public static final GraphSelectedRealNodeAction MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON = createMODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY(true);

        public static final GraphSelectedRealNodeAction createMODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY(final boolean show) {
            return new GraphSelectedRealNodeAction(show ? GDCommands.MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON : GDCommands.MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_OFF) {
                @Override
                public boolean isEnabled() {
                    if (!super.isEnabled()) {
                        return false;
                    }
                    MetaModel selectedMetaModel = Static.getSelectedMetaModel();
                    for (ElementContainer ec : Static.iterableSelectedRealElementContainer()) {
                        if (ec instanceof InterLayerConnectedNodeContainer) {
                            ModelElement me = ec.getElement();
                            ModelElement selected = ec.getElement();
                            GraphViewDefinition graphViewDefinition = selectedMetaModel.getGraphViewDefinition();
                            MetaPath interLayerMetaPath = graphViewDefinition.getInterLayerMetaPath(selected);
                            if (interLayerMetaPath != null) {
                                Collection<ModelElement> interLayerConnectedElements = interLayerMetaPath.getConnectedElements(me);
                                if (!interLayerConnectedElements.isEmpty()) {
                                    boolean hasVisibleConfigs = ((InterLayerConnectedNodeContainer) ec).isShowInterLayerConnections();
                                    if (show != hasVisibleConfigs) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }
            };
        }

        public static final GraphFrameAction MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON = createMODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY(true);

        public static final GraphFrameAction MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_OFF = createMODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY(false);

        private static final GraphFrameAction createMODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY(final boolean visible) {
            return new GraphFrameAction(visible ? GDCommands.MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON : GDCommands.MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_OFF) {
                @Override
                public boolean isEnabled() {
                    if (!super.isEnabled()) {
                        return false;
                    }
                    LGMGraphDocument selectedDoc = Static.getSelectedDoc();
                    LayerContainer lc = selectedDoc.getActiveLayer();
                    MetaModel selectedMetaModel = Static.getSelectedMetaModel();
                    for (ElementContainer ec : lc.getGraphNodeContainers()) {
                        ModelElement me = ec.getElement();
                        if (selectedMetaModel.hasInterLayerStartClass(me)) {
                            boolean hasVisibleConfigs = ((InterLayerConnectedNodeContainer) ec).isShowInterLayerConnections();
                            if (visible != hasVisibleConfigs) {
                                return true;
                            }
                        }
                    }
                    return visible != lc.isShowInterLayerConnections();
                }
            };
        }

        public static final Action MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_ON = new GraphSelectedRealNodeAction(GDCommands.MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_ON) {
            @Override
            public boolean isEnabled() {
                return !Static.isSelection() && MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON.isEnabled() || MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON.isEnabled();
            };
        };

        public static final Action MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_OFF = new GraphSelectedRealNodeAction(GDCommands.MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_OFF) {
            @Override
            public boolean isEnabled() {
                return !Static.isSelection() && MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_OFF.isEnabled() || MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_OFF.isEnabled();
            };
        };

    }

    /**
     * Sammlung von Methoden, die häufig verwendete {@link Action}s generieren.
     * <br>
     * Das Generieren ist notwendig, weil sich diese Actions wärend der
     * Programmausführung ändern können und damit nicht statisch definierbar
     * sind.
     *
     * @author fstephan
     */
    public static class DynamicActions {

        /**
         * Gibt ein Array zurück, dessen Elemente Actions zum Öffnen der zuletzt
         * verwendeten Dateien sind
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
            ExtendedAction[] actions = new ExtendedAction[files.size()];
            for (int i = 0; i < actions.length; i++) {
                final File file = files.get(i);
                actions[i] = new ExtendedAction(file.getName()) {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Static.getTool().openModelFile(file);
                    }
                };
                actions[i].setShortDescription(file.getPath());
            }
            // Die Actions werden hier nicht alphabetisch sortiert, da die durch die
            // UserProperties gegebene Reihenfolge entscheidend ist
            return actions;
        }

        /**
         * Gibt ein Array zurück, dessen Elemente Actions zum Öffnen der
         * Teilmodell-Frames sind
         */
        public static final Action[] getSelectFrameActions() {

            List<ViewPaneFrameComponent> frames = Static.getAllFrames();
            ViewPaneFrameComponent selectedFrame = Static.getActiveFrame();

            Action[] actions = new Action[frames.size()];
            int index = 0, next;
            for (final ViewPaneFrameComponent frame : frames) {

                if (frame != selectedFrame) {
                    next = ++index;
                } else {
                    next = 0;
                }

                actions[next] = new AbstractAction(frame.getFullName()) {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (!frame.isSelected()) {
                            frame.setSelected();
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
                SearchDialog sd = new SearchDialog(getMainFrame());
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

        /**
         * Öffnet einen Dialog zum Anlegen benutzerdefinierter
         * Eigenschaftsfelder
         */
        public static final Action ACTION_OPEN_USERFIELD_DEFINITION_DIALOG = new GraphDocumentAction(ActionIdentifier.ACTION_OPEN_USERFIELD_DEFINITION_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                if (ElementPropertyDialogsContext.hasOpenDialogs()) {
                    JOptionPane.showMessageDialog(getMainFrame(), getResString("message_close_all_dialogs"));
                    return;
                }
                UserFieldDeclarationDialog.showDialog(getMainFrame(), getSelectedGDCollection());
            }
        };

        /** Öffnet einen Editor zur Eingabe von Kennzahlen */
        public static final Action ACTION_OPEN_USERFIELD_VALUE_EDITOR_DIALOG = new GraphDocumentAction(ActionIdentifier.ACTION_OPEN_USERFIELD_VALUE_EDITOR_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                if (ElementPropertyDialogsContext.hasOpenDialogs()) {
                    JOptionPane.showMessageDialog(getMainFrame(), getResString("message_close_all_dialogs"));
                    return;
                }
                if (getSelectedDoc() != null) {
                    UserFieldEditorDialog.getDialog(getMainFrame(), getSelectedGDCollection()).setVisible(true);
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
        public static final ExtendedAction ACTION_OPEN_HELP_DIALOG = new StaticAction(ActionIdentifier.ACTION_OPEN_HELP_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                Help.showHelp();
            }
        };

        /** Aktiviert die Direkthilfe */
        public static final ExtendedAction ACTION_ACTIVATE_DIRECT_HELP = new StaticAction(ActionIdentifier.ACTION_ACTIVATE_DIRECT_HELP) {
            @Override
            public void actionPerformedWithEvent(final ActionEvent e) {
                Help.getHelp().getDisplayHelpAfterTracking().actionPerformed(e);
            }
        };

        /**
         * Zeigt eine lokale Webseite mit Themen zur weiteren Modellnutzung an
         */
        public static final ExtendedAction ACTION_SHOW_INFORMATION_SYSTEM_EVALUATION_TUTORIAL = new StaticAction(ActionIdentifier.ACTION_SHOW_INFORMATION_SYSTEM_EVALUATION_TUTORIAL, PPP) {
            @Override
            public void actionPerformed() {
                BrowseUtils.browseApplicationPathRelativeFile(getResString("auswhilfe_datei"));
            }
        };

        /** öffnet die Online-Hilfe-Seite */
        public static final ExtendedAction ACTION_OPEN_URL_ONLINE_HELP = new OpenUrlAction(ActionIdentifier.ACTION_OPEN_URL_ONLINE_HELP);

        /** öffnet die 3lgm-Homepage */
        public static final ExtendedAction ACTION_OPEN_URL_3LGM_WEBSITE = new OpenUrlAction(ActionIdentifier.ACTION_OPEN_URL_3LGM_WEBSITE);

        /** öffnet die Website mit dem IssueTracker */
        public static final ExtendedAction ACTION_OPEN_URL_ISSUE_TRACKER = new OpenUrlAction(ActionIdentifier.ACTION_OPEN_URL_ISSUE_TRACKER);

        /** öffnet das Beispielmodell */
        public static final ExtendedAction ACTION_OPEN_EXAMPLE_MODEL_FILE = new StaticAction(ActionIdentifier.ACTION_OPEN_EXAMPLE_MODEL_FILE) {
            @Override
            public void actionPerformed() {
                Static.getTool().openModelFile(Tool3lgmConstants.EXAMPLE_MODEL_FILE);
            }
        };

        /**
         * Öffnet den Ordner mit dem Beispielmodellen bzw die sog.
         * Modellbibliothek mit Modellen von Standards
         */
        public static final ExtendedAction ACTION_OPEN_FILE_CHOSSER_IN_MODEL_LIBRARY = new StaticAction(ActionIdentifier.ACTION_OPEN_FILE_CHOSSER_IN_MODEL_LIBRARY, PPP) {
            @Override
            public void actionPerformed() {
                File modelLibraryDir = Tool3lgmConstants.TEMPLATE_DIR;
                getTool().openModelFile(modelLibraryDir, ActionIdentifier.ACTION_OPEN_FILE_CHOSSER_IN_MODEL_LIBRARY);
            }
        };

        /** Zeigt die Programm-Info an */
        public static final ExtendedAction ACTION_OPEN_ABOUT_DIALOG = new StaticAction(ActionIdentifier.ACTION_OPEN_ABOUT_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                new ToolSplashScreen().showAboutDialog();
            }
        };

        //        /** öffnet ein Fenster zum Import von Lizenzdateien */
        //        public static final ExtendedAction ACTION_IMPORT_LICENSE_FILE = new StaticAction(ActionIdentifier.ACTION_IMPORT_LICENSE_FILE) {
        //            @Override
        //            public void actionPerformed() {
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

        /**
         * Gibt alle Actions zum Erzeugen von {@link ModelElement}en der
         * spezifizierten Klassen wieder
         *
         * @param metaModel
         * @param layerIndex
         */
        public static Iterable<StaticAction> getCreateElementActions(final MetaModel metaModel, final int layerIndex) {
            final Iterable<Class<? extends ModelElement>> creatableLayerNodes = metaModel.getCreatableLayerNodes(layerIndex);
            ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            List<StaticAction> actions = new ArrayList<>();
            for (Class<? extends ModelElement> creatableClass : creatableLayerNodes) {
                String actionName = elementsNameBuilder.getDisplayableName(creatableClass);
                actions.add(new GraphDocumentAction(GDCommands.MODEL_ACTION_CREATE_NODE, creatableClass.getName(), actionName, null));
            }
            return ImmutableList.sortedCopyOf(Alphabetical.getLocalizedComparator(), actions);
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

            public static final Action[] getOptionsSimpleRedundancyAnalysis(final MetaModel metaModel) {
                //die Definitionen für die SimpleRedundancyAnalysis aud der AnalyseDefinition holen
                AnalysesDefinition analysisDefinition = metaModel.getAnalysesDefinition();
                SimpleRedundancyAnalysisDefinitions simpleRedundancyAnalysisDefinition = analysisDefinition.getSimpleRedundancyAnalysisDefinitions();
                //wenn es gültige Definitionen für die SimpleRedundancyAnalysis gibt, dann werden in dieses Array die zugehörigen Actions geschrieben
                Action[] returnActions = new StaticAction[simpleRedundancyAnalysisDefinition.size()];
                ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                for (int i = 0; i < returnActions.length; i++) {
                    //Definition einer der aktuellen SimpleRedundancyAnalysis holen
                    final SingleSimpleRedundancyAnalysisDefinition singleSimpleRedundancyDefinition = simpleRedundancyAnalysisDefinition.get(i);
                    GlobalOptionAction action = new GlobalOptionAction(ActionIdentifier.OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS, LGMChangeType.ELEMENT_GRAPHICS_CHANGED) {
                        @Override
                        public void changeOption() {
                            boolean oldState = isSelected();
                            for (GDCollection gdcoll : Static.iterableCollections()) {
                                gdcoll.getMainDoc().setSimpleRedundancyAnalysisState(singleSimpleRedundancyDefinition, !oldState);
                                for (Szenario szenario : gdcoll.getSzenarios()) {
                                    szenario.setSimpleRedundancyAnalysisState(singleSimpleRedundancyDefinition, !oldState);
                                }
                            }
                        }
                        @Override
                        public boolean isSelected() {
                            return isEnabled() && getSelectedDoc().isSimpleRedundancyAnalysis(singleSimpleRedundancyDefinition);
                        }
                        @Override
                        public boolean isEnabled() {
                            return super.isEnabled() && Static.getSelectedDoc() != null;
                        }
                    };
                    String resKey = ActionIdentifier.OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS.name();
                    MetaPath metaPath = singleSimpleRedundancyDefinition.getMetaPath();
                    String startClassPluralName = elementsNameBuilder.getDisplayablePluralName(metaPath.getStartClasses());
                    String endClassPluralName = elementsNameBuilder.getDisplayablePluralName(metaPath.getEndClasses());
                    String fullActionDisplayName = getReplacedResString(resKey, startClassPluralName, endClassPluralName);
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
            public static final Action ACTION_PROPERTY_INT_RENDER_SETTINGS = new StaticAction(PROPERTY_INT_RENDER_SETTINGS, PPP) {
                @Override
                public void actionPerformed() {
                    GraphicPropertyDialog dialog = new GraphicPropertyDialog(getMainFrame());
                    dialog.setVisible(true);
                }
            };

            /**
             * Erzeugt das Array, aller Actions, für die das Ein- und Ausblenden
             * in der Grafik in der GraphViewDefinition angegeben wurde.
             *
             * @param hide
             * @return
             */
            public static final GraphFrameAction[] getModelActionsHideUnhideUnassociated(final MetaModel metaModel) {
                List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> hidableIfNotConnected = metaModel.getGraphViewDefinition().getHidableIfNotConnected();
                if (hidableIfNotConnected == null || hidableIfNotConnected.isEmpty()) {
                    return null;
                }
                GraphFrameAction[] actions = new GraphFrameAction[hidableIfNotConnected.size() * 2];
                ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                for (int i = 0; i < actions.length; i++) {
                    Pair<Class<? extends ModelElement>, Class<? extends Edge>> hidable = hidableIfNotConnected.get(i / 2);
                    Class<? extends ModelElement> elementClass = hidable.getFirstItem();
                    final boolean hide = i % 2 == 0;
                    GDCommands command;
                    String arguments;
                    if (hide) {
                        command = GDCommands.MODEL_ACTION_HIDE_UNASSOCIATED;
                        arguments = elementClass.getSimpleName() + " " + hidable.getSecondItem().getSimpleName();
                    } else {
                        command = GDCommands.MODEL_ACTION_UNHIDE_ALL;
                        arguments = elementClass.getSimpleName();
                    }
                    GraphFrameAction hideAction = new GraphFrameAction(command, arguments, null);
                    hideAction.setReplacedText(elementsNameBuilder.getDisplayablePluralName(elementClass));
                    actions[i] = hideAction;
                }
                return actions;
            }

        }

        /** Zeigt die Programm-Info an */
        public static final ExtendedAction ACTION_OPEN_CHOOSE_DEFAULT_METAMODEL_DIALOG = new StaticAction(ActionIdentifier.ACTION_OPEN_CHOOSE_DEFAULT_METAMODEL_DIALOG, PPP) {
            @Override
            public void actionPerformed() {
                new Tool3lgmMetaModelContextChooser().chooseModelType();
            }
        };

        /** Öffnet ein Fenster zum Auswählen des RMI-Ports */
        public static final Action ACTION_PROPERTY_INT_RMI_PORT = new StaticAction(PROPERTY_INT_RMI_PORT, PPP) {

            @Override
            public void actionPerformed() {
                // Für die Konfiguration des RMI, wir das RMIPropertyPanel einem
                // JOptionPane übergeben.
                // Nach dem OK, werden die Values des Panels abgefragt.
                RMIPropertyPanel rmip = new RMIPropertyPanel();

                int oldRegPort = PROPERTY_INT_RMI_PORT.get();
                if (JOptionPane.showOptionDialog(null, rmip, getResString(PROPERTY_INT_RMI_PORT.name()), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                    PROPERTY_INT_RMI_PORT.set(rmip.getRmiRegistryPortTextFieldValue());
                }
                if (oldRegPort != PROPERTY_INT_RMI_PORT.get()) {
                    JOptionPane.showMessageDialog(getMainFrame(), getResString("RMI_SETTINGS_INFO"));
                }
            }
        };

    }

    /**
     * Actions für das Wechseln der Ebenen-Ansicht, Anzeigen der Toolbars, etc.
     *
     * @author fstephan
     */
    public static class ViewActions {

        /** Wechselt zur Ein-Ebenen-Ansicht */
        private static final ExtendedAction ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE = new GraphFrameAction(ActionIdentifier.ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE) {
            @Override
            protected void actionPerformed() {
                GraphViewPaneFrameComponent frame = (GraphViewPaneFrameComponent) Static.getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                area.setMultiView(false);
                frame.getGraphDocument().deselectAll(false);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && ((GraphViewPaneFrameComponent) Static.getActiveFrame()).getInputGraphArea().isMultiView();
            }
        };

        /** Wechselt zur Drei-Ebenen-Ansicht */
        private static final ExtendedAction ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE = new GraphFrameAction(ActionIdentifier.ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE) {
            @Override
            protected void actionPerformed() {
                GraphViewPaneFrameComponent frame = (GraphViewPaneFrameComponent) Static.getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                area.setMultiView(true);
                frame.getGraphDocument().deselectAll(false);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !((GraphViewPaneFrameComponent) Static.getActiveFrame()).getInputGraphArea().isMultiView();
            }
        };

        /**
         * Schaltet immer zischen den beiden Aktionen Einzellayer-Ansicht und
         * Mehrlayer-Ansicht um
         */
        public static final Action ACTION_GRAPH_SWITCH_ONE_LAYER_AND_THREE_LAYER_PERSPECTIVE = new ToggleAction(ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE, ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE);

        /**
         * Zeigt die Fachliche Ebene an, falls die Ein-Ebenen-Ansicht aktiviert
         * ist
         */
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

        /**
         * Zeigt die Logische Werzeugebene an, falls die Ein-Ebenen-Ansicht
         * aktiviert ist
         */
        public static final Action ACTION_ACTIVATE_LOGICAL_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_LOGICAL_LAYER) {
            @Override
            protected void actionPerformed() {
                getSelectedGDCollection().setActiveLayer(ModelConstants.LOGICAL_LAYER);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedGDCollection().getActiveLayer() != ModelConstants.LOGICAL_LAYER;
            }

        };

        /**
         * Zeigt die physische Werkzeugebene an, falls die Ein-Ebenen-Ansicht
         * aktiviert ist
         */
        public static final Action ACTION_ACTIVATE_PHYSICAL_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_PHYSICAL_LAYER) {
            @Override
            protected void actionPerformed() {
                getSelectedGDCollection().setActiveLayer(ModelConstants.PHYSICAL_LAYER);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedGDCollection().getActiveLayer() != ModelConstants.PHYSICAL_LAYER;
            }

        };

        /**
         * Öffnet einen Dialog für die Einstellung von Größe, Abstand, etc. der
         * Ebenen
         */
        public static final Action ACTION_OPEN_GRAPH_VIEW_SETTINGS_DIALOG = new GraphFrameAction(ActionIdentifier.ACTION_OPEN_GRAPH_VIEW_SETTINGS_DIALOG, PPP) {
            @Override
            protected void actionPerformed() {
                new GraphViewOptionsDialog().showDialog((GraphViewPaneFrameComponent) Static.getActiveFrame());
            }
        };

        /** Öffnet die Matrix-Ansicht */
        public static final Action ACTION_OPEN_MATRIX_VIEW = new GraphDocumentAction(ActionIdentifier.ACTION_OPEN_MATRIX_VIEW) {
            @Override
            public void actionPerformed() {
                getTool().openMatrixView();
            }
        };

    }

}
