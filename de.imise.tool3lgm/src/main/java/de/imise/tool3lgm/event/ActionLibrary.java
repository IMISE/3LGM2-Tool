package de.imise.tool3lgm.event;

import static de.imise.tool3lgm.Static.getSelectedDoc;
import static de.imise.tool3lgm.Static.getSelectedGDCollection;
import static de.imise.tool3lgm.Static.getTool;
import static de.imise.tool3lgm.Tool3lgmConstants.getFileNameExtensionFilters;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.isConnectingForward;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayablePluralName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getGraphViewDefinition;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getMetaAssociationName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isDoubleMeaningEdge;

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
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.LicenseHandler;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.event.LayoutAction.ElementAlignmentAction;
import de.imise.tool3lgm.event.LayoutAction.ElementLayoutAction;
import de.imise.tool3lgm.event.LayoutAction.LayerLayoutAction;
import de.imise.tool3lgm.event.action.ChangeLocaleAction;
import de.imise.tool3lgm.event.action.GraphDocumentAction;
import de.imise.tool3lgm.event.action.GraphFrameAction;
import de.imise.tool3lgm.event.action.SelectionAction;
import de.imise.tool3lgm.event.action.StaticActionNew;
import de.imise.tool3lgm.event.action.SubmodelAction;
import de.imise.tool3lgm.graphtools.analyse.context.AnalyseEditor;
import de.imise.tool3lgm.graphtools.analyse.context.AnalyseRepositoryFrame;
import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysis;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.dialog.GraphViewOptionsDialog;
import de.imise.tool3lgm.graphtools.dialog.GraphicPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.LayoutEditor;
import de.imise.tool3lgm.graphtools.dialog.RMIPropertyPanel;
import de.imise.tool3lgm.graphtools.dialog.SearchDialog;
import de.imise.tool3lgm.graphtools.dialog.SzenarioDialog;
import de.imise.tool3lgm.graphtools.matrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionImExportHandler;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.dialog.declaration.UserFieldDeclarationDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.GraphAreaToolbarManager;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.gui.ToolBar;
import de.imise.tool3lgm.gui.ToolSplashScreen;
import de.imise.tool3lgm.imexport.DataExportModule;
import de.imise.tool3lgm.imexport.DataImportModule;
import de.imise.tool3lgm.imexport.graphml.GraphmlExporter;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.process.DataAvailabilityFinder;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.xslt.WebExportDialog;
import de.imise.tool3lgm.xslt.XMLExportDialog;
import de.imise.util.Pair;
import de.imise.util.image.ComponentAsImageExportHandler;
import de.imise.util.swing.dialog.DirectoryChooser;
import de.imise.util.swing.dialog.ExtendedFileChooser;
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
                    ExtendedFileChooser oeffnenDialog = new ExtendedFileChooser(null);
                    oeffnenDialog.setMultiSelectionEnabled(false);
                    oeffnenDialog.setFileFilters(false, getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED));
                    if (oeffnenDialog.showOpenDialog(getTool()) == ExtendedFileChooser.APPROVE_OPTION) {
                        GDCollection selectedGDColl = getSelectedGDCollection();
                        GDCollectionImExportHandler imExportHandler = selectedGDColl.getImExportHandler();
                        imExportHandler.importSzenarios(oeffnenDialog.getSelectedFile(), true);
                    }
                }
            };

            /** Öffnet einen Dialog zum Import von Modellen */
            public static final Action ACTION_IMPORT_MODEL = new GraphDocumentAction(ActionIdentifier.ACTION_IMPORT_MODEL, PPP) {
                @Override
                protected void actionPerformed() {
                    ExtendedFileChooser oeffnenDialog = new ExtendedFileChooser(null);
                    oeffnenDialog.setMultiSelectionEnabled(false);
                    oeffnenDialog.setFileFilters(false, getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED));
                    if (oeffnenDialog.showOpenDialog(getTool()) == ExtendedFileChooser.APPROVE_OPTION) {
                        GDCollection selectedGDColl = getSelectedGDCollection();
                        GDCollectionImExportHandler imExportHandler = selectedGDColl.getImExportHandler();
                        imExportHandler.importModel(oeffnenDialog.getSelectedFile());
                    }
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
                        new GraphmlExporter(path, Static.getSelectedGDCollection()).writeYFilesGraphml();
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
        public static final Action ACTION_ANALYSIS_REDUNDANCY = new GraphDocumentAction(ActionIdentifier.ACTION_ANALYSIS_REDUNDANCY, true) {
            @Override
            protected void actionPerformed() {
                RedundancyAnalysis.getReport(getSelectedGDCollection());
            }
        };

        /** TODO:AXS,FST: Wirft schon seit ToolMenu eine Exception */
        public static final Action ACTION_ANALYSIS_DATA_AVAILABILITY = new GraphDocumentAction(ActionIdentifier.ACTION_ANALYSIS_DATA_AVAILABILITY, true) {
            @Override
            protected void actionPerformed() {
                // Dieser Aufruf startet auch die Ausgabe des DataAvailabilityFinder
                new DataAvailabilityFinder(getSelectedDoc());
            }
        };

        /** Aktiviert die Konsistenz-Prüfung */
        public static final ExtendedAction OPTION_CHECK_CONSISTENCY = new StaticActionNew(ActionIdentifier.OPTION_CHECK_CONSISTENCY, UserProperties.isCheckConsistency()) {
            @Override
            public final void actionPerformed() {
                boolean isSelected = isSelected();
                if (!isSelected) {
                    ConsistencyChecker checker = getTool().getConsistencyChecker();
                    if (checker != null) {
                        checker.resetConsistencyDefinition();
                    }
                }
                getTool().setCheckConsistencyState(isSelected);
            }
        };
    }

    public static class ContextActions {

        public static final Action OPEN_PROPERTY_DIALOG = new CommandAction(GDCommands.ELEMENT_PROPERTIES);

        /*
         * public static final Action TAKE_OVER_IN_SUBMODEL = new
         * CommandAction(GDCommands.SELECT_LINKED_SZENARIO); public static final Action
         * LINK_WITH_SUBMODEL = new
         * CommandAction(ActionIdentifier.link_with_submodel,GDCommands.LINK_SELECTED_TO_SZENARIO,
         * "'null'"); public static final Action SET_ELEMENT_VISIBLE = new
         * CommandAction(ActionIdentifier.set_element_visible,GDCommands.UNLINK); public static
         * final Action SET_ELEMENT_INVISIBLE = new
         * CommandAction(ActionIdentifier.set_element_invisible,GDCommands.UNLINK); public static
         * final Action EXPAND_ELEMENT = new
         * CommandAction(ActionIdentifier.expand_element,GDCommands.UNLINK); public static final
         * Action COLLAPSE_ELEMENT = new
         * CommandAction(ActionIdentifier.collapse_element,GDCommands.UNLINK); public static final
         * Action ELEMENT_ANALYSIS = new
         * CommandAction(ActionIdentifier.element_analysis,GDCommands.UNLINK); public static final
         * Action JOIN_ELEMENTS = new CommandAction(ActionIdentifier.join,GDCommands.UNLINK); public
         * static final Action CREATE_TEXTFIELD = new
         * CommandAction(ActionIdentifier.create_textfield,GDCommands.LINK);
         */
        /**
         * Action, die das Anzeigen von Interebenenbeziehungen für die ausgewählten Elemente/ die
         * ausgewählte Ebene (de-)aktiviert.
         */
        public static final Action CONFIGURATIONS_VISIBILITY = new CommandAction(GDCommands.SHOW_ALL_CONFIGS) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (isSelected()) {
                    exec(GDCommands.HIDE_ALL_CONFIGS);
                } else {
                    exec(GDCommands.SHOW_ALL_CONFIGS);
                }

            }

            @Override
            public boolean isEnabled() {
                GraphDocument doc = getSelectedDoc();
                for (ElementContainer ec : doc.getSelectedRealElementContainerIterable()) {
                    if (ec instanceof InterLayerConnectedNodeContainer) {
                        return true;
                    }
                }
                LayerContainer activeLayer = doc.getActiveLayer();
                if (activeLayer.isSelected() && activeLayer.getLayerNumber() != ModelConstants.LAYERS[0]) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean isSelected() {
                GraphDocument doc = getSelectedDoc();
                for (ElementContainer ec : doc.getSelectedRealElementContainerIterable()) {
                    if (ec instanceof InterLayerConnectedNodeContainer && ((InterLayerConnectedNodeContainer) ec).isShowInterLayerConnections() && ec.isSelected()) {
                        return true;
                    }
                }
                LayerContainer activeLayer = doc.getActiveLayer();
                if (activeLayer.isSelected()) {
                    return activeLayer.isShowInterLayerConnections();
                }
                return false;
            }
        };

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
         * Erzeugt ein Array von Actions zum Vervinden bzw. Trennen der momentan selektierten
         * Elemente
         *
         * @param command trennen/verbinden ({@link GDCommands#LINK}/{@link GDCommands#UNLINK})
         * @param icon Trennen- bzw. Verbinden-Icon
         * @return
         */
        private static Action[] getConnectionActions(final GDCommands command, final ImageIcon icon) {

            GraphDocument doc = Static.getSelectedDoc();
            boolean knickpunkte = doc.isSelectedOnlyBendpoints();
            List<Action> actions = new ArrayList<>();

            if (!knickpunkte) {
                ModelElement me1 = doc.getLastSelected().getElement();
                Class<? extends ModelElement> me1Class = me1.getClass();

                List<ModelElement> selectedElements = doc.getSelectedElements();

                for (Class<? extends ModelElement> me2Class : doc.getSelectedRealElementClasses()) {
                    for (Class<? extends Edge> edgeClass : ModelConstants.getEdgeTypes(me1Class, me2Class)) {
                        if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
                            if (isConnectingForward(edgeClass, me1Class, me2Class)) {
                                String label = ModelConstants.getBackwardMetaAssociationName(edgeClass);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (me1 == me2) {
                                        continue;
                                    }
                                    if (!me1.isPartOf(me2) && !me1.isParentOf(me2)) {
                                        connectable = true;
                                    }
                                    if (me1.isDirectPartOf(me2)) {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + FORWARD, connectable));
                            }
                            if (isConnectingForward(edgeClass, me2Class, me1Class)) {
                                String label = ModelConstants.getForwardMetaAssociationName(edgeClass);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (me1 == me2) {
                                        continue;
                                    }
                                    if (!me2.isPartOf(me1) && !me2.isParentOf(me1)) {
                                        connectable = true;
                                    }
                                    if (me2.isDirectPartOf(me1)) {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + BACKWARD, connectable));
                            }
                        } else if (isDoubleMeaningEdge(edgeClass)) {
                            if (isConnectingForward(edgeClass, me1Class, me2Class)) {
                                String label = ModelConstants.getMetaAssociationName(edgeClass, false, FORWARD);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (me1 == me2) {
                                        continue;
                                    }
                                    if (!me1.isConnectedTo(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + FORWARD, connectable));

                                label = getMetaAssociationName(edgeClass, false, BACKWARD);
                                connectable = false;
                                disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (me1 == me2) {
                                        continue;
                                    }
                                    if (!me1.isConnectedFrom(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }
                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + BACKWARD, connectable));

                            }
                            // Doppeldeutige Kanten mit identischer Start- und Endklasse brauchen
                            // nur 1x angeboten werden
                            if (isConnectingForward(edgeClass, me2Class, me1Class) && getStartClass(edgeClass) != getEndClass(edgeClass)) {
                                String label = getMetaAssociationName(edgeClass, true, FORWARD);
                                boolean connectable = false;
                                boolean disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (me1 == me2) {
                                        continue;
                                    }
                                    if (!me1.isConnectedTo(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + BACKWARD, connectable));

                                label = getMetaAssociationName(edgeClass, true, BACKWARD);
                                connectable = false;
                                disconnectable = false;
                                for (ModelElement me2 : selectedElements) {
                                    if (me1 == me2) {
                                        continue;
                                    }
                                    if (!me1.isConnectedFrom(me2, edgeClass)) {
                                        connectable = true;
                                    } else {
                                        disconnectable = true;
                                    }
                                    if (connectable && disconnectable) {
                                        break;
                                    }
                                }

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + FORWARD, connectable));

                            }
                        } else /* if (isConnecting(edgeClass, me1Class, me2Class)) */ {
                            String label = ModelConstants.getForwardMetaAssociationName(edgeClass);
                            boolean connectable = false;
                            boolean disconnectable = false;
                            for (ModelElement me2 : selectedElements) {
                                if (me1 == me2) {
                                    continue;
                                }
                                if (!me1.isConnectedWith(me2, edgeClass)) {
                                    connectable = true;
                                } else {
                                    disconnectable = true;
                                }
                                if (connectable && disconnectable) {
                                    break;
                                }
                            }

                            actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + BACKWARD, connectable));
                        }
                    }
                }
            }
            return actions.toArray(new Action[actions.size()]);
        }

        /**
         * Gibt ein Array zurück, dessen Elemente Actions zum Öffnen der zuletzt verwendeten Dateien
         * sind
         */
        public static final Action[] getLastUsedFilesOpenActions() {
            List<File> files = UserProperties.getLastUsedFiles();
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

        /** Erzeugt ein Array von Actions zum Vervinden der momentan selektierten Elemente */
        public static final Action[] getLinkActions() {
            return getConnectionActions(GDCommands.LINK, Tool3lgmConstants.getIcon("verbindung_anlegen.gif"));
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

        /** Erzeugt ein Array von Actions zum Trennen der momentan selektierten Elemente */
        public static final Action[] getUnlinkActions() {
            return getConnectionActions(GDCommands.UNLINK, Tool3lgmConstants.getIcon("verbindung_trennen.gif"));
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

        /** Kopiert die aktuelle Selektion in die Zwischenablage */
        public static final Action MODEL_ACTION_COPY = new SelectionAction(GDCommands.MODEL_ACTION_COPY);

        /** Schneidet die die aktuelle Selektion aus und kopiert sie in die Zwischenablage */
        public static final Action MODEL_ACTION_CUT = new SelectionAction(GDCommands.MODEL_ACTION_CUT);

        /** Fügt den Inhalt der Zwischenablage ein */
        public static final Action MODEL_ACTION_PASTE = new SelectionAction(GDCommands.MODEL_ACTION_PASTE);

        //        ACTION_REMOVE_CHILDS,
        //
        //        /**
        //         * Löscht die Kindelemente der aktuell selektierten Elemente und hängt alle Eigenschaften
        //         * der Kinder an das Oberelement
        //         */
        //        public static final Action MODEL_ACTION_REMOVE_CHILDS = new SelectionAction(ActionIdentifier.MODEL_ACTION_REMOVE_CHILDS) {
        //
        //            @Override
        //            public void actionPerformed(final ActionEvent e) {
        //                if (!isEnabled()) {
        //                    return;
        //                }
        //                GraphDocument doc = getSelectedDoc();
        //                doc.start_transaction(TransactionManager.STANDARD_PID);
        //                List<ModelElement> selectedElements = doc.getSelectedElements();
        //                for (int i = 0; i < selectedElements.size(); i++) {
        //                    removeChilds(selectedElements.get(i), doc);
        //                }
        //                doc.finish_transaction(TransactionManager.STANDARD_PID);
        //                doc.distributeEvent(GraphDocument.DATA_CHANGED);
        //            }
        //
        //            @Override
        //            public boolean isEnabled() {
        //                if (!super.isEnabled()) {
        //                    return false;
        //                }
        //                GraphDocument doc = getSelectedDoc();
        //                return doc.isSingleSelection() && doc.getSelectedElements().get(0).hasPart();
        //            }
        //
        //            /**
        //             * Gibt eine Liste aller Elemente zurück, die dupliziert werden müssten, um die
        //             * Konsistenz zu erhalten, wenn ein anderes Element die gleichen Verbindungen bekommen
        //             * sollte wie das übergebene.
        //             *
        //             * @param elementsList Liste aller Elemente, die dupliziert werden sollen
        //             * @param sourceIndex Index des Elementes, dessen Verbindungen darauf geprüft werden
        //             *            sollen, ob die verbundenen Elemente dupliziert werden müssten, wenn es
        //             *            selbst dupliziert werden würde / private void
        //             *            fillElements2Duplicate(ArrayList<ModelElement> elementsList, int
        //             *            sourceIndex) { ModelElement me = elementsList.get(sourceIndex); for (Edge
        //             *            edge : me.getEdges()) { boolean meIsEdgeStart = edge.isStart(me);
        //             *            ModelElement connected = meIsEdgeStart ? edge.getEnd() : edge.getStart();
        //             *            //Mit wievielen Elementen von der Art des parts darf das umzuhängende
        //             *            Element maximal verbunden sein? int maxConnectedToOtherCardinality =
        //             *            meIsEdgeStart ? edge.getMaxBackwardCardinality() :
        //             *            edge.getMaxForwardCardinality(); int actualConnectedToOtherCardinality
        //             *            = meIsEdgeStart ? connected.countConnectionsToThis(edge.getClass()) :
        //             *            connected.countConnectionsFromThis(edge.getClass()); //das verbundene
        //             *            Element darf nicht mit einem weiteren Element verbunden if
        //             *            (actualConnectedToOtherCardinality >= actualConnectedToOtherCardinality) {
        //             *            } } } /** Dupliziert das übergebene Element und alle seine Verbindungen
        //             *            außer die übergebene Edge. Wenn verbundene Elemente bereits mit der
        //             *            maximalen Anzahl der
        //             * @param me
        //             * @param exceptionalEdge
        //             * @param alreadyDuplicated Menge aller Elemente, die nicht dupliziert werden sollen, da
        //             *            sie bereits dupliziert wurde. Damit kann man verhindern, dass Elemente im
        //             *            Kreis dupliziert werden / public void duplicate(ModelElement me, Edge
        //             *            exceptionalEdge, HashSet<ModelElement> alreadyDuplicated) { } /**
        //             * @param gdcoll
        //             * @param oldEdge
        //             * @param start
        //             * @param end
        //             * @return
        //             */
        //            private Edge link(final GDCollection gdcoll, final Edge oldEdge, final ModelElement start, final ModelElement end) {
        //                return gdcoll.link(oldEdge.getClass().getSimpleName(), GDCommands.INVALID_HASH_STRING, start, end, GDCommands.INVALID_EDGE_INDEX, GDCommands.INVALID_EDGE_INDEX, false, TransactionManager.STANDARD_PID);
        //            }
        //
        //            /*
        //             * private void removeChilds(ModelElement me, GraphDocument doc) { GDCollection gdcoll =
        //             * doc.getCollection(); ArrayList<ModelElement> parts = me.getPartElements(false); while
        //             * (parts.size() > 0) { ModelElement part = parts.get(0); ArrayList<ModelElement>
        //             * partsParents = part.getDirectParentElements(); for (int i = 0; i <
        //             * partsParents.size(); i++) { //in partsParents nur die Parents lassen, an die alle
        //             * Verbindungen //des Parts umgehängt oder dupliziert werden muss (allen die auch
        //             * //gleichzeitig Part von me sind braucht man die Informationen //ihrer Kinder nicht
        //             * unterzuhängen, da sie ja auch weggelassen werden) if
        //             * (parts.contains(partsParents.get(i))) partsParents.remove(i--); } //sicher ist sicher
        //             * -> Kopie anlegen, falls durch irgendwelche Seiteneffekte sich die Kantenliste nochmal
        //             * ändert ArrayList<Edge> partEdges = new ArrayList<Edge>(part.getEdges()); for (int i
        //             * = 0; i < partEdges.size(); i++) { edge = (partEdges.get(i);
        //             * //Alle Kanten zwischen dem zu löschenden Teil und dem Oberelement, das die
        //             * Eigenschaften des Teilelementes //Bekommen soll, werden nicht umgehängt (eigentlich
        //             * kann das nur die Teil-Von-Edge selbst sein, aber in //neuen Metamodellen wäre auch
        //             * etwas anderes denkbar) if (edge instanceof PartOfBeziehung) continue; //Mit wievielen
        //             * Elementen von der Art des parts darf das umzuhängende Element maximal verbunden sein?
        //             * int maxConnectedToPartCardinality = edge.isStartClass(part.getClass()) ?
        //             * edge.getMaxBackwardCardinality() : edge.getMaxForwardCardinality(); for (int j =
        //             * partsParents.size() - 1; j > 0; j--) { ModelElement parent = partsParents.get(j);
        //             * //falls mehr Parents vorhanden sind, als mit dem umzuhängenden Element selbst
        //             * verbunden //sein dürfen, muss das umzuhängende Element dupliziert werden if
        //             * (maxConnectedToPartCardinality >= j) { ArrayList<ModelElement> elements2Duplicate =
        //             * new ArrayList<ModelElement>(); //einfach umhängen } else { } } for (ModelElement
        //             * parent : partsParents) { ModelElement start = edge.getStart() == part ? parent :
        //             * edge.getStart(); ModelElement end = edge.getEnd() == part ? parent : edge.getEnd();
        //             * int minElemCardinality = edge.isStartClass(start.getClass()) ?
        //             * edge.getMinForwardCardinality() : edge.getMinBackwardCardinality(); if
        //             * (minElemCardinality > 0) continue; int dir = edge.getDirection(); String edgeName =
        //             * edge.getName(); String edgeDescrip = edge.getDescription(); Edge newEdge = null; if
        //             * (dir == FORWARD) { newEdge = link(gdcoll, edge, start, end); } else if
        //             * (dir == BACKWARD) { newEdge = link(gdcoll, edge, end, start); } else if
        //             * (dir == DOUBLE) { newEdge = link(gdcoll, edge, start, end); link(gdcoll,
        //             * edge, end, start); } doc.setName(newEdge, edgeName, TransactionManager.STANDARD_PID);
        //             * doc.setDescription(newEdge.getHashString(), edgeDescrip,
        //             * TransactionManager.STANDARD_PID); } } gdcoll.deleteElement(part, doc,
        //             * TransactionManager.STANDARD_PID); parts = me.getDirectPartElements(); } }
        //             */
        //            private void removeChilds(final ModelElement me, final GraphDocument doc) {
        //                GDCollection gdcoll = doc.getCollection();
        //                List<ModelElement> parts = me.getDirectPartElements();
        //                while (parts.size() > 0) {
        //                    ModelElement part = parts.get(0);
        //                    removeChilds(part, doc);
        //                    // sicher ist sicher -> Kopie anlegen, falls durch irgendwelche Seiteneffekte
        //                    // sich die Kantenliste nochmal ändert
        //                    for (Edge edge : part.getEdges()) {
        //                        // Alle Kanten zwischen dem zu löschenden Teil und dem Oberelement, das die
        //                        // Eigenschaften des Teilelementes
        //                        // Bekommen soll, werden nicht umgehängt (eigentlich kann das nur die
        //                        // Teil-Von-Edge selbst sein, aber in
        //                        // neuen Metamodellen wäre auch etwas anderes denkbar)
        //                        if (edge instanceof PartOfBeziehung) {
        //                            continue;
        //                        }
        //                        List<ModelElement> parentElements = part.getDirectParentElements();
        //                        for (ModelElement parent : parentElements) {
        //                            ModelElement start = edge.getStart() == part ? parent : edge.getStart();
        //                            ModelElement end = edge.getEnd() == part ? parent : edge.getEnd();
        //                            int minElemCardinality = edge.isStartClass(start.getClass()) ? edge.getMinForwardCardinality() : edge.getMinBackwardCardinality();
        //                            if (minElemCardinality > 0) {
        //                                continue;
        //                            }
        //                            int dir = edge.getDirection();
        //                            String edgeName = edge.getName();
        //                            String edgeDescrip = edge.getDescription();
        //                            Edge newEdge = null;
        //                            if (dir == FORWARD) {
        //                                newEdge = link(gdcoll, edge, start, end);
        //                                gdcoll.unlink(edge.getStart(), edge.getEnd(), edge.getClass(), TransactionManager.STANDARD_PID);
        //                            } else if (dir == BACKWARD) {
        //                                newEdge = link(gdcoll, edge, end, start);
        //                                gdcoll.unlink(edge.getEnd(), edge.getStart(), edge.getClass(), TransactionManager.STANDARD_PID);
        //                            } else if (dir == DOUBLE) {
        //                                newEdge = link(gdcoll, edge, start, end);
        //                                link(gdcoll, edge, end, start);
        //                                gdcoll.unlink(edge.getStart(), edge.getEnd(), edge.getClass(), TransactionManager.STANDARD_PID);
        //                                gdcoll.unlink(edge.getEnd(), edge.getStart(), edge.getClass(), TransactionManager.STANDARD_PID);
        //                            }
        //                            doc.setName(newEdge, edgeName, TransactionManager.STANDARD_PID);
        //                            doc.setDescription(newEdge, edgeDescrip, TransactionManager.STANDARD_PID);
        //                        }
        //                    }
        //                    gdcoll.deleteElement(part, doc, TransactionManager.STANDARD_PID);
        //                    parts = me.getDirectPartElements();
        //                }
        //            }
        //        };

        /** Löscht das aktuell ausgewählte Element aus dem Teilmodell */
        public static final Action MODEL_ACTION_DELETE_FROM_SUBMODEL = new SubmodelAction(GDCommands.MODEL_ACTION_DELETE_FROM_SUBMODEL) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedDoc().isSelection();
            }
        };

        /** Löscht das aktuell ausgewählte Element aus dem Gesamtmodell */
        public static final Action MODEL_ACTION_REMOVE_FROM_MODEL = new SelectionAction(GDCommands.MODEL_ACTION_DELETE_FROM_MODEL);

        /** Öffnet ein Options-Fenster zum Löschen des aktuell ausgewählten Elements */
        public static final Action MODEL_ACTION_DELETE = new SelectionAction(GDCommands.MODEL_ACTION_DELETE);

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
        public static final Action USERFIELD_DEFINITION_DIALOG = new StaticAction(ActionIdentifier.userfields, PPP, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                if (!ModelConstants.getDialogs().isEmpty()) {
                    JOptionPane.showMessageDialog(getTool(), getResString("message_close_all_dialogs"));
                    return;
                }
                if (getSelectedDoc() != null) {
                    UserFieldDeclarationDialog.showDialog(getTool(), getSelectedCollection());
                }
            }
        };

        /** Öffnet einen Editor zur Eingabe von Kennzahlen */
        public static final Action ATTRIBUTE_EDITOR = new StaticAction(ActionIdentifier.attribute_editor, PPP, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                if (!ModelConstants.getDialogs().isEmpty()) {
                    JOptionPane.showMessageDialog(getTool(), getResString("message_close_all_dialogs"));
                    return;
                }
                if (getSelectedDoc() != null) {
                    UserFieldEditorDialog.getDialog(getTool(), getSelectedCollection()).setVisible(true);
                }
                return;
            }
        };

        /**
         * (De-)Aktiviert das automatische Zuweisen von ETNT-Verbindungen zu
         * Kommunikationsbeziehnungen
         */
        public static final Action AUTOMATIC_ETMT_ASSIGNMENT = new StaticAction(ActionIdentifier.automatic_etmt_assignment, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedDoc().relinkETNT();
            }
        };
    }

    /**
     * Actions für die Benutzer-Hilfe
     *
     * @author fstephan
     */
    public static class HelpActions {

        // TODO:AXS,FST: testen warum hier vieles nicht geht

        /** Funktioniert nicht */
        public static final Action HELP = new StaticAction(ActionIdentifier.help) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                throw new UnsupportedOperationException("Hilfe wird nicht unterstützt");
            }
        };

        /** Funktioniert nicht */
        public static final Action DIRECT_HELP = new StaticAction(ActionIdentifier.direct_help) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                throw new UnsupportedOperationException("Direkthilfe wird nicht unterstützt");
            }
        };

        /** Funktioniert nicht */
        public static final Action EVALUATION = new StaticAction(ActionIdentifier.evaluation) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                BrowseUtils.browseRelativeFileFromResource("auswhilfe_datei");
            }
        };

        /** Öffnet die Online-Hilfe-Seite */
        public static final Action ONLINE_HELP = new StaticAction(ActionIdentifier.online_help) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                BrowseUtils.browseUrlFromResource("3lgm2tool_support_website");
            }
        };

        /** Öffnet die 3lgm-Homepage */
        public static final Action LGM_ONLINE = new StaticAction(ActionIdentifier.lgm_online) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                BrowseUtils.browseUrlFromResource("3lgm2_website");
            }
        };

        /** Öffnet das Beispielmodell */
        public static final Action EXAMPLE = new StaticAction(ActionIdentifier.example) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                Static.getTool().openFile(false, Tool3lgmConstants.EXAMPLE_MODEL_FILE);
            }
        };

        /** Funktioniert nicht */
        public static final Action MODEL_LIBRARY = new StaticAction(ActionIdentifier.model_library) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                BrowseUtils.browseRelativeFileFromResource("modlib_verz");
            }
        };

        /** Zeigt die Programm-Info an */
        public static final Action INFO = new StaticAction(ActionIdentifier.about) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ToolSplashScreen.getInfoDialog();
            }
        };

        /** Öffnet ein Fenster zum Import von Lizenzdateien */
        public static final Action IMPORT_LICENSE = new StaticAction(ActionIdentifier.import_license) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                LicenseHandler.importLicenseFile();
            }
        };
    }

    /**
     * Actions für das Einfügen von Elementen in die 3 Ebenen.
     *
     * @author fstephan
     */
    public static class InsertActions {

        /** Array aller Insert-Actions für die Fachliche Ebene */
        public static final Action[] DOMAIN_LAYER_ACTIONS = InsertAction.getDomainLayerActions();

        /** Array aller Insert-Actions für die Logische Werkzeugebene */
        public static final Action[] LOGICAL_TOOLLAYER_ACTIONS = InsertAction.getLogicalToolLayerActions();

        /** Array aller Insert-Actions für die Physische Werkzeugebene */
        public static final Action[] PHYSICAL_TOOLLAYER_ACTIONS = InsertAction.getPhysicalToolLayerActions();

        /** Gibt wieder, ob der aktuelle Kontext ein Einfügen von Elementen erlaubt, oder nicht */
        public static boolean isInsertAvailable() {
            return InsertAction.isInsertAvailable();
        }
    }

    /**
     * Actions für die grafische Darstellung von Ebenen und Elementen
     *
     * @author fstephan
     */
    public static class LayoutActions {

        /**
         * Actions für die relative Ausrichtung der Elemente zueinander<br>
         * Dabei werden alle markierten Elemente am zuletzt ausgewählten Element entsprechend
         * ausgerichtet.
         *
         * @author fstephan
         */
        public static class ElementAlignment {

            /**
             * Horizonzale Ausrichtung
             *
             * @author fstephan
             */
            public static class Horizontal {

                /** Linksbündige Ausrichtung am zuletzt ausgewählten Element */
                public static final StaticAction LEFT = new ElementAlignmentAction(ActionIdentifier.element_left, GDCommands.HALIGN_LEFT);

                /** Ausrichtung in der Mitte des zuletzt ausgewählten Elements (horizontal) */
                public static final StaticAction CENTER = new ElementAlignmentAction(ActionIdentifier.element_center_horizontal, GDCommands.HALIGN_CENTER);

                /** Rechtsbündige Ausrichtung am zuletzt ausgewählten Element */
                public static final StaticAction RIGHT = new ElementAlignmentAction(ActionIdentifier.element_right, GDCommands.HALIGN_RIGHT);
            }

            /**
             * Größenanpassung
             *
             * @author astruebi
             * @create 13.02.2013
             */
            public static class Size {

                /** Ausrichtung am oberen Rand des zuletzt ausgewählten Elements */
                public static final StaticAction WIDTH = new ElementAlignmentAction(ActionIdentifier.element_width, GDCommands.ALIGN_WIDTH);

                /** Ausrichtung in der Mitte des zuletzt ausgewählten Elements (vertikal) */
                public static final StaticAction HEIGTH = new ElementAlignmentAction(ActionIdentifier.element_height, GDCommands.ALIGN_HEIGHT);

                /** Ausrichtung am unteren Rand des zuletzt ausgewählten Elements */
                public static final StaticAction WIDTH_AND_HEIGTH = new ElementAlignmentAction(ActionIdentifier.element_width_and_height, GDCommands.ALIGN_WIDTH_AND_HEIGTH);

            }

            /**
             * Vertikale Ausrichtung
             *
             * @author fstephan
             */
            public static class Vertical {

                /** Ausrichtung am oberen Rand des zuletzt ausgewählten Elements */
                public static final StaticAction TOP = new ElementAlignmentAction(ActionIdentifier.element_top, GDCommands.VALIGN_TOP);

                /** Ausrichtung in der Mitte des zuletzt ausgewählten Elements (vertikal) */
                public static final StaticAction CENTER = new ElementAlignmentAction(ActionIdentifier.element_center_vertical, GDCommands.VALIGN_CENTER);

                /** Ausrichtung am unteren Rand des zuletzt ausgewählten Elements */
                public static final StaticAction BOTTOM = new ElementAlignmentAction(ActionIdentifier.element_bottom, GDCommands.VALIGN_BOTTOM);
            }

        }

        /**
         * Actions für die grafische Darstellung von Elementen. <br>
         * Diese Actions beziehen sich dabei auf das(die) vom Nutzer ausgewählte(n) Element(e). Im
         * Folgenden werden unter "Elemente" nur {@link NodeContainer} verstanden, die keine {@link BendpointContainer} sind. Das schließt demnach
         * auch {@link EdgeContainer} aus. <br>
         * Im Falle des Einführens eines Kanten-Layouts sollte hier paralle zu dieser Klasse eine
         * separate Klasse mit den jeweiligen Actions erstellt werden
         *
         * @author fstephan
         */
        public static class ElementLayout {

            /*
             * Im Folgenden werden unter "Elemente" nur {@link NodeContainer} verstanden, die keine
             * {@link BendpointContainer} sind. Das schließt demnach auch {@link EdgeContainer} aus.
             * <br> Im Falle des Einführens eines Kanten-Layouts sollte hier parallel zu dieser
             * Klasse eine separate Klasse mit den Actions für die Kanten erstellt werden.
             */

            /**
             * Actions für die Auswahl von Icons für Elemente
             *
             * @author fstephan
             */
            public static class Icon {

                /** Entfernt das Icon des Elements */
                public static final Action NO_ICON = new ElementLayoutAction(ActionIdentifier.no_icon, GDCommands.UNSET_ICON);

                /** Öffnet ein Fenster zur Auswahl eines Icons */
                public static final Action CHOOSE_ICON = new ElementLayoutAction(ActionIdentifier.choose_icon, PPP, GDCommands.CHOOSE_ICON);
            }

            /**
             * Actions für die Auswahl der Ausrichtung der Elementbeschriftungen
             *
             * @author fstephan
             */
            public static class TextAlignment {

                /**
                 * Horizonzale Ausrichtung
                 *
                 * @author fstephan
                 */
                public static class Horizontal {

                    /** Richtet die Beschrfitung linksbündig aus */
                    public static final Action LEFT = new ElementLayoutAction(ActionIdentifier.text_left, GDCommands.LABEL_HALIGN_LEFT);

                    /** Richtet die Beschrfitung horizontal mittig aus */
                    public static final Action CENTER = new ElementLayoutAction(ActionIdentifier.text_center_horizontal, GDCommands.LABEL_HALIGN_CENTER);

                    /** Richtet die Beschrfitung rechtsbündig aus */
                    public static final Action RIGHT = new ElementLayoutAction(ActionIdentifier.text_right, GDCommands.LABEL_HALIGN_RIGHT);
                }

                /**
                 * Vertikale Ausrichtung
                 *
                 * @author fstephan
                 */
                public static class Vertical {

                    /** Richtet die Beschrfitung horizontal mittig aus */
                    public static final Action TOP = new ElementLayoutAction(ActionIdentifier.text_top, GDCommands.LABEL_VALIGN_TOP);

                    /** Richtet die Beschrfitung vertikal mittig aus */
                    public static final Action CENTER = new ElementLayoutAction(ActionIdentifier.text_center_vertical, GDCommands.LABEL_VALIGN_CENTER);

                    /** Richtet die Beschrfitung nach unten aus */
                    public static final Action BOTTOM = new ElementLayoutAction(ActionIdentifier.text_bottom, GDCommands.LABEL_VALIGN_BOTTOM);
                }
            }

            /**
             * Actions für die Transparenz der Elemente
             *
             * @author fstephan
             */
            public static class Transparency {

                /** Macht das ausgewählte Element nicht-transparent */
                public static final Action NO = new ElementLayoutAction(ActionIdentifier.element_no_transparency, GDCommands.CHANGE_ALPHA, GraphElementLayout.NICHT_TRANSPARENT);

                /** Macht das ausgewählte Element halb-transparent */
                public static final Action SEMI = new ElementLayoutAction(ActionIdentifier.element_semi_transparency, GDCommands.CHANGE_ALPHA, GraphElementLayout.HALB_TRANSPARENT);

                /** Macht das ausgewählte Element voll-transparent */
                public static final Action FULL = new ElementLayoutAction(ActionIdentifier.element_full_transparency, GDCommands.CHANGE_ALPHA, GraphElementLayout.VOLL_TRANSPARENT);
            }

            /** Öffnet ein Fenster zur Änderung der Schriftart des ausgewählten Elements */
            public static final Action CHOOSE_FONT = new ElementLayoutAction(ActionIdentifier.font, PPP, GDCommands.CHANGE_FONT);

            /** Öffnet ein Fenster zur Änderung der Farbe des ausgewählten Elements */
            public static final Action CHANGE_COLOR = new ElementLayoutAction(ActionIdentifier.element_change_color, PPP, GDCommands.CHANGE_COLOR);

            /** Setzt die Farbe des Elements zurück */
            public static final Action RESET_COLOR = new ElementLayoutAction(ActionIdentifier.element_reset_color, GDCommands.NORMALIZE_COLOR);

            /** Setzt die Farbe des Elements zurück */
            public static final Action RESET_TRANSPARENCY = new ElementLayoutAction(ActionIdentifier.element_reset_transparency, GDCommands.NORMALIZE_TRANSPARENCY);

            /** Setzt die Schriftart des Elements zurück */
            public static final Action RESET_FONT = new ElementLayoutAction(ActionIdentifier.reset_font, GDCommands.NORMALIZE_FONT);

            /** Setzt alle Layout-Eigenschaften des Elements zurück */
            public static final Action RESET_ALL = new ElementLayoutAction(ActionIdentifier.reset_all, GDCommands.NORMALIZE);
        }

        /**
         * Actions für die grafische Darstellung der Ebenen
         *
         * @author fstephan
         */
        public static class LayerLayout {

            /**
             * Actions für die Transparenz der Ebenen
             *
             * @author fstephan
             */
            public static class Transparency {

                /** Macht die momentan ausgewählte Ebene nicht-transparent */
                public static final Action NO = new LayerLayoutAction(ActionIdentifier.layer_no_transparency, GDCommands.CHANGE_LAYER_ALPHA, GraphElementLayout.NICHT_TRANSPARENT);

                /** Macht die momentan ausgewählte Ebene halb-transparent */
                public static final Action SEMI = new LayerLayoutAction(ActionIdentifier.layer_semi_transparency, GDCommands.CHANGE_LAYER_ALPHA, GraphElementLayout.HALB_TRANSPARENT);

                /** Macht die momentan ausgewählte Ebene voll-transparent */
                public static final Action FULL = new LayerLayoutAction(ActionIdentifier.layer_full_transparency, GDCommands.CHANGE_LAYER_ALPHA, GraphElementLayout.VOLL_TRANSPARENT);
            }

            /** Setzt Farbe und Transparenz der ausgewählten Ebene zurück */
            public static final Action RESET = new LayerLayoutAction(ActionIdentifier.layer_reset_color, GDCommands.NORMALIZE_LAYER);

            /** Öffnet ein Fenster zur Auswahl der Ebenen-Farbe */
            public static final Action CHANGE_COLOR = new LayerLayoutAction(ActionIdentifier.layer_change_color, PPP, GDCommands.CHANGE_LAYER_COLOR);
        }

        /**
         * Actions für die "Höhe" von Elementen innerhalb einer Ebene <br>
         * Diese Actions beziehen sich dabei auf das(die) vom Nutzer ausgewählte(n) Element(e).
         *
         * @author fstephan
         */
        public static class Level {

            /** Element steht über allen anderen */
            public static final StaticAction HIGHEST = new ElementLayoutAction(ActionIdentifier.highest, GDCommands.Z_MOVE_UP);

            /** Element wird um eine (interne) Ebene nach oben bewegt */
            public static final StaticAction INCREASE = new ElementLayoutAction(ActionIdentifier.increase, GDCommands.Z_STEP_UP);

            /** Element wird um eine (interne) Ebene nach unten bewegt */
            public static final StaticAction DECREASE = new ElementLayoutAction(ActionIdentifier.decrease, GDCommands.Z_STEP_DOWN);

            /** Element steht unter allen anderen */
            public static final StaticAction LOWEST = new ElementLayoutAction(ActionIdentifier.lowest, GDCommands.Z_MOVE_DOWN);
        }

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

            /** (De-)Aktiviert das Erzeugen eines Teilmodells für die XMLAnalyse */
            public static final Action NEW_SUBMODEL = new StaticAction(ActionIdentifier.create_submodel, (Boolean) UserProperties.isNewSubmodelForAnalysis()) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setNewSubmodelForAnalysis(isSelected());
                }
            };

            public static final Action[] SIMPLE_REDUNDANCIES = create_SIMPLE_REDUNDANCIES_Actions();

            public static final Action[] create_SIMPLE_REDUNDANCIES_Actions() {
                //die Definitionen für die SimpleRedundancyAnalysis aud der AnalyseDefinition holen
                AnalysisDefinition analysisDefinition = ModelConstants.getAnalysisDefinition();
                SimpleRedundancyAnalysisDefinitions simpleRedundancyAnalysisDefinition = analysisDefinition.getSimpleRedundancyAnalysisDefinitions();
                //wenn es gültige Definitionen für die SimpleRedundancyAnalysis gibt, dann werden in dieses Array die zugehörigen Actions geschrieben
                Action[] returnActions = new StaticAction[simpleRedundancyAnalysisDefinition.size()];
                for (int i = 0; i < returnActions.length; i++) {
                    //Definition einer der aktuellen SimpleRedundancyAnalysis holen
                    SingleSimpleRedundancyAnalysisDefinition singleSimpleRedundancyDefinition = simpleRedundancyAnalysisDefinition.get(i);
                    StaticAction action = new StaticAction(ActionIdentifier.SIMPLE_REDUNDNANCY_ANALYSIS) {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            for (GDCollection gdcoll : getTool().getCollections()) {
                                gdcoll.getMainGraphDocument().switchSimpleRedundancyAnalysisState(singleSimpleRedundancyDefinition);
                                for (Szenario szenario : gdcoll.getSzenarios()) {
                                    szenario.switchSimpleRedundancyAnalysisState(singleSimpleRedundancyDefinition);
                                }
                            }
                            distributeElementGraphicsChanged();
                        }
                    };
                    String resKey = ActionIdentifier.SIMPLE_REDUNDNANCY_ANALYSIS.name();
                    MetaPath metaPath = singleSimpleRedundancyDefinition.getMetaPath();
                    String startClassPluralName = getDisplayablePluralName(metaPath.getStartClass());
                    String endClassPluralName = getDisplayablePluralName(metaPath.getEndClass());
                    String fullActionDisplayName = getResString(resKey, startClassPluralName, endClassPluralName);
                    action.setText(fullActionDisplayName);
                    returnActions[i] = action;
                }
                return returnActions;
            }

            /** (De-)Aktiviert die Kennzahlberechnung */
            public static final Action ACTIVATE_CALCULATION = new StaticAction(ActionIdentifier.activate_calculation, (Boolean) UserProperties.isEnableClassificationNumberCalculation()) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setEnableClassificationNumberCalculation(isSelected());
                    distributeDataChanged();
                }
            };
        }

        /**
         * Allgemeine Optionen
         *
         * @author fstephan
         */
        public static class Gerneral {

            /** (De-)Aktiviert das Anzeigen einer Warnung vor dem Löschen eines Elements */
            public static final Action SHOW_REMOVE_WARNING = new StaticAction(ActionIdentifier.removeWarning, (Boolean) UserProperties.isShowRemoveWarning()) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setShowRemoveWarning(isSelected());
                    distributeDataChanged();
                }
            };
        }

        /**
         * Graphik-Optionen
         *
         * @author fstephan
         */
        public static class Graphics {

            /** (De-)Aktiviert das Zeichnen von Kanten nur für selektierte Elemente */
            public static final Action PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS = new StaticAction(ActionIdentifier.paintEdgesOnlyForSelectedElements, (Boolean) UserProperties.isPaintEdgesOnlyForSelectedElements()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setPaintEdgesOnlyForSelectedElements(isSelected());
                    repaintTool();
                }
            };

            /**
             * Zeigt einen ColorChooser zum auswählen der Farbe, mit der Analyseergnisse in der
             * Grafik hervorgehovben werden
             */
            public static final Action ANALYSIS_COLOR = new StaticAction(ActionIdentifier.analysis_color, PPP) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    NodeRenderer.analysisColor = JColorChooser.showDialog(getTool(), getText(), NodeRenderer.analysisColor);
                }
            };

            /** Öffnet ein Fenster für allgemeine Grafik-Einstellungen */
            public static final Action RENDERING_OPTIONS = new StaticAction(ActionIdentifier.rendering_options, PPP) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    GraphicPropertyDialog dialog = new GraphicPropertyDialog(getTool());
                    dialog.setVisible(true);
                }
            };

            /** (De-)Aktiviert die Verwendung eines Rasters */
            public static final Action USE_RASTER = new StaticAction(ActionIdentifier.useRaster, (Boolean) UserProperties.isUseRaster()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setUseRaster(isSelected());
                    distributeDataChanged();
                }
            };

            /** (De-)Aktiviert das Zeichnen eines Rasters */
            public static final Action SHOW_RASTER = new StaticAction(ActionIdentifier.showRaster, (Boolean) UserProperties.isShowRaster()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setShowRaster(isSelected());
                    distributeDataChanged();
                }
            };

            /** (De-)Aktiviert das Kennzeichnen von Modelelementen mit verknüpften Teilmodellen */
            public static final Action SIGNIFY_LINKED_ELEMENTS = new StaticAction(ActionIdentifier.signify_linked_elements, (Boolean) UserProperties.isShowLinks()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setShowLinks(isSelected());
                    repaintTool();
                }
            };

            /** Array, aller Actions, für die das Ein- und Ausblenden in der Grafik in der GraphViewDefinition angegeben wurde. */
            public static final StaticAction HIDE_UNHIDE_UNASSOCIATED[] = create_HIDE_UNHIDE_UNASSOCIATED_Actions();

            /**
             * Erzeugt das Array, aller Actions, für die das Ein- und Ausblenden in der Grafik in der GraphViewDefinition angegeben wurde.
             *
             * @param hide
             * @return
             */
            private static final StaticAction[] create_HIDE_UNHIDE_UNASSOCIATED_Actions() {
                List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> hidableIfNotConnected = getGraphViewDefinition().getHidableIfNotConnected();
                if (hidableIfNotConnected == null || hidableIfNotConnected.isEmpty()) {
                    return null;
                }
                StaticAction[] actions = new StaticAction[hidableIfNotConnected.size() * 2];
                for (int i = 0; i < actions.length; i++) {
                    Pair<Class<? extends ModelElement>, Class<? extends Edge>> hidable = hidableIfNotConnected.get(i / 2);
                    Class<? extends ModelElement> elementClass = hidable.getFirstItem();
                    final boolean hide = i % 2 == 0;
                    StaticAction hideAction = new StaticAction(hide ? ActionIdentifier.HIDE_UNASSOCIATED : ActionIdentifier.UNHIDE_ALL) {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            if (!isEnabled()) {
                                return;
                            }
                            if (hide) {
                                exec(GDCommands.HIDE_UNASSOCIATED, elementClass.getSimpleName() + " " + hidable.getSecondItem().getSimpleName());
                            } else {
                                exec(GDCommands.UNHIDE_ALL, elementClass.getSimpleName());
                            }
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

            /** (De-)Aktiviert die automatische Farbzuweisung zu Konfigurationslinien */
            public static final Action AUTOMATIC_COLORING = new StaticAction(ActionIdentifier.automatic_coloring, (Boolean) UserProperties.isAssignConfigurationColors()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setAssignConfigurationColors(isSelected());
                    repaintTool();
                }
            };

            /** (De-)Aktiviert das Anzeigen von Tooltips im 3lgm */
            public static final Action TOOLTIPS = new StaticAction(ActionIdentifier.show_tooltips, UserProperties.isShowToolTips()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    throw new UnsupportedOperationException("Tooltips werden nicht unterstützt");
                }
            };
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

        /**
         * Browser-Optionen
         *
         * @author fstephan
         */
        public static class ModelBrowser {

            /** (De-)Aktiviert das Teilmodell-spezifische Layout des ModelBrowsers */
            public static final Action SUBMODEL_SPECIFIC = new StaticAction(ActionIdentifier.submodel_specific, (Boolean) UserProperties.isEnableSubmodelBrowser()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setEnableSubmodelBrowser(isSelected());
                    distributeDataChanged();
                }
            };

            /** (De-)Aktiviert das parallele Anzeigen aller ModelBrowser */
            public static final Action SHOW_MULTIPLE_BROSERS = new StaticAction(ActionIdentifier.show_multiple_browsers, (Boolean) UserProperties.isShowModelsInSeparateBrowser()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setShowModelsInSeparateBrowser(isSelected());
                    distributeDataChanged();
                }
            };

            /** (De-)Aktiviert das Anzeigen der benutzdefinierten Eigenschaften im ModelBrowser */
            public static final Action USERDEFINED_PROPERTIES = new StaticAction(ActionIdentifier.show_userdefinded_properties, (Boolean) UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setShowUserDefinedPropertiesInModelBrowser(isSelected());
                    distributeDataChanged();
                }
            };
        }

        /**
         * Teil-Von-Beziehnung-Optionen
         *
         * @author fstephan
         */
        public static class PartOf {

            /** (De-)Aktiviert das Berücksichtigen übergeordneter Elemente bei der Suche */
            public static final Action CONSIDER_PARENTS = new StaticAction(ActionIdentifier.consider_parents, (Boolean) UserProperties.isSearchParents()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setSearchParents(isSelected());
                    distributeDataChanged();
                }
            };

            /** (De-)Aktiviert das hierarchische Anzeigen der Part-Of-Beziehnung im ModelBrowser */
            public static final Action HIERARCHICAL = new StaticAction(ActionIdentifier.show_hierarchical, (Boolean) UserProperties.isShowPartOfHierarchy()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setShowPartOfHierarchy(isSelected());
                    distributeDataChanged();
                }
            };

            /** (De-)Aktiviert das Anzeigen der Vergröberung */
            public static final Action SIGNIFY_COARSEMENT = new StaticAction(ActionIdentifier.signify_coarsement, (Boolean) UserProperties.isShowExpansionSign()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setShowExpansionSign(isSelected());
                    distributeDataChanged();
                }
            };

            /** (De-)Aktiviert das automatische Verschieben untergeordneter Elemente */
            public static final Action AUTO_MOVE_CHILDREN = new StaticAction(ActionIdentifier.auto_move_children, (Boolean) UserProperties.isMoveSubelements()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    UserProperties.setMoveSubelements(isSelected());
                    distributeDataChanged();
                }
            };
        }

        /** Öffnet ein Fenster zum Auswählen des RMI-Ports */
        public static final Action OPEN_RMI_SETTINGS = new StaticAction(ActionIdentifier.rmi_settings, PPP) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                // Für die Konfiguration des RMI, wir das RMIPropertyPanel einem
                // JOptionPane übergeben.
                // Nach dem OK, werden die Values des Panels abgefragt.
                RMIPropertyPanel rmip = new RMIPropertyPanel();

                String oldRegPort = UserProperties.getRMIRegistryPort();
                if (JOptionPane.showOptionDialog(null, rmip, getResString("rmi_settings"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                    UserProperties.setRMIRegistryPort(rmip.getRmiRegistryPortTextFieldValue());
                }
                if (!oldRegPort.equals(UserProperties.getRMIRegistryPort())) {
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
        public static final Action ACTION_ACTIVATE_DOMAIN_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_DOMAIN_LAYER, true) {
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
        public static final Action ACTION_ACTIVATE_LOGICAL_TOOL_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_LOGICAL_TOOL_LAYER, true) {
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
        public static final Action ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER = new GraphDocumentAction(ActionIdentifier.ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER, true) {
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
