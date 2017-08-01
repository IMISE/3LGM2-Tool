package de.imise.tool3lgm.event;

import java.awt.BorderLayout;
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

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.event.LayoutAction.ElementAlignmentAction;
import de.imise.tool3lgm.event.LayoutAction.ElementLayoutAction;
import de.imise.tool3lgm.event.LayoutAction.LayerLayoutAction;
import de.imise.tool3lgm.graphtools.analyse.context.AnalyseEditor;
import de.imise.tool3lgm.graphtools.analyse.context.AnalyseRepositoryFrame;
import de.imise.tool3lgm.graphtools.analyse.process.DataAvailabilityFinder;
import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyChecker;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.dialog.EinstellungDialog;
import de.imise.tool3lgm.graphtools.dialog.GraphicPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.LayoutEditor;
import de.imise.tool3lgm.graphtools.dialog.RMIPropertyPanel;
import de.imise.tool3lgm.graphtools.dialog.SearchDialog;
import de.imise.tool3lgm.graphtools.dialog.SzenarioDialog;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.matrixview.TableInternalFrame;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionImExportHandler;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
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
import de.imise.tool3lgm.gui.ToolInternalFrame;
import de.imise.tool3lgm.gui.ToolSplashScreen;
import de.imise.tool3lgm.imexport.DataExportModule;
import de.imise.tool3lgm.imexport.DataImportModule;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.xslt.WebExportDialog;
import de.imise.tool3lgm.xslt.XMLExportDialog;
import de.imise.util.image.ComponentAsImageExportHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * Sammlung global einsetzbarer {@link Action}s.
 *
 * @author fstephan
 */
public class ActionLibrary {

    /**
     * Actions für Analysen
     *
     * @author fstephan
     */
    public static class AnalysisActions {

        /** Zeigt das XMLAnalyse-Repository an */
        public static final Action OPEN_REPOSITORY = new StaticAction(ActionIdentifier.repository, PPP) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AnalyseRepositoryFrame.showDialog();
            }
        };

        /** Öffnet den XMLAnalyse-Editor */
        public static final Action OPEN_EDITOR = new StaticAction(ActionIdentifier.analysis_editor, PPP) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AnalyseEditor.showDialog(getTool());
            }
        };

        /** Setzt alle XMLAnalyse-Ergebnisse zurück */
        public static final Action RESET_RESULT = new StaticAction(ActionIdentifier.reset_result, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedDoc().clearAnalysisResult();
            }
        };

        /** Aktiviert die Redundanz-XMLAnalyse */
        public static final Action ACTIVATE_REDUNDANCY_ANALYSIS = new StaticAction(ActionIdentifier.redundancy_analysis, PPP, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                RedundancyChecker.getReport(getSelectedCollection());
            }
        };

        /** TODO:AXS,FST: Wirft schon seit ToolMenu eine Exception */
        public static final Action ACTIVATE_DATA_AVAILABILITY = new StaticAction(ActionIdentifier.data_availability, PPP, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                // Dieser Aufruf startet auch die Ausgabe des DataAvailabilityFinder
                new DataAvailabilityFinder(getSelectedDoc());
            }
        };

        /** Aktiviert die Konsistenz-Prüfung */
        public static final Action ACTIVATE_CONSISTENCY_CHECK = new StaticAction(ActionIdentifier.consistency_check, false, UserProperties.isCheckConsistency()) {

            @Override
            public final void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                boolean isSelected = isSelected();
                if (!isSelected) {
                    ConsistencyChecker checker = getTool().getConsistencyChecker();
                    if (checker != null) {
                        checker.setConsistencyDefinition(null);
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
                    for (Class<? extends Kante> edgeClass : ModelConstants.getEdgeTypes(me1Class, me2Class)) {
                        if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
                            if (Kante.isConnectingForward(edgeClass, me1Class, me2Class)) {
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

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + Doppelkante.FORWARD, connectable));
                            }
                            if (Kante.isConnectingForward(edgeClass, me2Class, me1Class)) {
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
                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + Doppelkante.BACKWARD, connectable));
                            }
                        } else if (ModelConstants.isDoubleMeaningEdge(edgeClass)) {
                            if (Kante.isConnectingForward(edgeClass, me1Class, me2Class)) {
                                String label = ModelConstants.getMetaAssociationName(edgeClass, false, Doppelkante.FORWARD);
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

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + Doppelkante.FORWARD, connectable));

                                label = ModelConstants.getMetaAssociationName(edgeClass, false, Doppelkante.BACKWARD);
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
                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + Doppelkante.BACKWARD, connectable));

                            }
                            // Doppeldeutige Kanten mit identischer Start- und Endklasse brauchen
                            // nur 1x angeboten werden
                            if (Kante.isConnectingForward(edgeClass, me2Class, me1Class) && Kante.getStartClass(edgeClass) != Kante.getEndClass(edgeClass)) {
                                String label = ModelConstants.getMetaAssociationName(edgeClass, true, Doppelkante.FORWARD);
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

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + Doppelkante.BACKWARD, connectable));

                                label = ModelConstants.getMetaAssociationName(edgeClass, true, Doppelkante.BACKWARD);
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

                                actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + Doppelkante.FORWARD, connectable));

                            }
                        } else /* if (Kante.isConnecting(edgeClass, me1Class, me2Class)) */ {
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

                            actions.add(new CommandAction(label, icon, command, edgeClass.getSimpleName() + " " + Doppelkante.BACKWARD, connectable));
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
                            Log.show(Log.FATAL, Tool3lgmConstants.getErrString("FehlerAllgemein"), exp);
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
        public static final Action UNDO = new StaticAction(ActionIdentifier.undo, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedDoc().undo();
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedCollection().getTman().isUndoAvailable();
            }
        };

        /** Macht letztes UNDO rückgängig */
        public static final Action REDO = new StaticAction(ActionIdentifier.redo, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedDoc().redo();
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedCollection().getTman().isRedoAvailable();
            }
        };

        // public static int COUNT = 0;

        /** Öffnet ein Suche-Fenster */
        public static final Action SEARCH = new StaticAction(ActionIdentifier.search, PPP, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                // if (COUNT++ % 2 == 0) {
                new SearchDialog(getTool()).showDialog();
                // } else {
                // new SearchDialogOld(getTool()).showDialog();
                // }
            }
        };

        /** Kopiert die aktuelle Selektion in die Zwischenablage */
        public static final StaticAction COPY = new StaticAction(ActionIdentifier.copy, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                exec(GDCommands.COPY);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedDoc().isSelection();
            }
        };

        /** Schneidet die die aktuelle Selektion aus und kopiert sie in die Zwischenablage */
        public static final StaticAction CUT = new StaticAction(ActionIdentifier.cut, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                exec(GDCommands.CUT);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedDoc().isSelection();
            }
        };

        /** Fügt den Inhalt der Zwischenablage ein */
        public static final StaticAction PASTE = new StaticAction(ActionIdentifier.paste, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                exec(GDCommands.PASTE);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && LGMGraphDocument.isClipboardAvailable();
            }
        };

        /** Leert die Zwischenablage */
        public static final StaticAction CLEAR_CLIPBOARD = new StaticAction(ActionIdentifier.clear_clipboard, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                exec(GDCommands.CLEAR_CLIPBOARD);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && LGMGraphDocument.isClipboardAvailable();
            }
        };

        /**
         * Löscht die Kindelemente der aktuell selektierten Elemente und hängt alle Eigenschaften
         * der Kinder an das Oberelement
         */
        public static final Action MODEL_ACTION_REMOVE_CHILDS = new StaticAction(ActionIdentifier.MODEL_ACTION_REMOVE_CHILDS, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                GraphDocument doc = getSelectedDoc();
                doc.start_transaction(TransactionManager.STANDARD_PID);
                List<ModelElement> selectedElements = doc.getSelectedElements();
                for (int i = 0; i < selectedElements.size(); i++) {
                    removeChilds(selectedElements.get(i), doc);
                }
                doc.finish_transaction(TransactionManager.STANDARD_PID);
                doc.distributeEvent(GraphDocument.DATA_CHANGED);
            }

            @Override
            public boolean isEnabled() {
                GraphDocument doc = getSelectedDoc();
                return super.isEnabled() && doc.isSingleSelection() && doc.getSelectedElements().get(0).hasPart();
            }

            /**
             * Gibt eine Liste aller Elemente zurück, die dupliziert werden müssten, um die
             * Konsistenz zu erhalten, wenn ein anderes Element die gleichen Verbindungen bekommen
             * sollte wie das übergebene.
             *
             * @param elementsList Liste aller Elemente, die dupliziert werden sollen
             * @param sourceIndex Index des Elementes, dessen Verbindungen darauf geprüft werden
             *            sollen, ob die verbundenen Elemente dupliziert werden müssten, wenn es
             *            selbst dupliziert werden würde / private void
             *            fillElements2Duplicate(ArrayList<ModelElement> elementsList, int
             *            sourceIndex) { ModelElement me = elementsList.get(sourceIndex); for (Kante
             *            edge : me.getEdges()) { boolean meIsEdgeStart = edge.isStart(me);
             *            ModelElement connected = meIsEdgeStart ? edge.getEnd() : edge.getStart();
             *            //Mit wievielen Elementen von der Art des parts darf das umzuhängende
             *            Element maximal verbunden sein? int maxConnectedToOtherCardinality =
             *            meIsEdgeStart ? edge.getMaxEndToStartCardinality() :
             *            edge.getMaxStartToEndCardinality(); int actualConnectedToOtherCardinality
             *            = meIsEdgeStart ? connected.countConnectionsToThis(edge.getClass()) :
             *            connected.countConnectionsFromThis(edge.getClass()); //das verbundene
             *            Element darf nicht mit einem weiteren Element verbunden if
             *            (actualConnectedToOtherCardinality >= actualConnectedToOtherCardinality) {
             *            } } } /** Dupliziert das übergebene Element und alle seine Verbindungen
             *            außer die übergebene Kante. Wenn verbundene Elemente bereits mit der
             *            maximalen Anzahl der
             * @param me
             * @param exceptionalEdge
             * @param alreadyDuplicated Menge aller Elemente, die nicht dupliziert werden sollen, da
             *            sie bereits dupliziert wurde. Damit kann man verhindern, dass Elemente im
             *            Kreis dupliziert werden / public void duplicate(ModelElement me, Kante
             *            exceptionalEdge, HashSet<ModelElement> alreadyDuplicated) { } /**
             * @param gdcoll
             * @param oldEdge
             * @param start
             * @param end
             * @return
             */
            private Kante link(final GDCollection gdcoll, final Kante oldEdge, final ModelElement start, final ModelElement end) {
                return gdcoll.link(oldEdge.getClass().getSimpleName(), GDCommands.INVALID_HASH_STRING, start, end, GDCommands.INVALID_EDGE_INDEX, GDCommands.INVALID_EDGE_INDEX, false, TransactionManager.STANDARD_PID);
            }

            /*
             * private void removeChilds(ModelElement me, GraphDocument doc) { GDCollection gdcoll =
             * doc.getCollection(); ArrayList<ModelElement> parts = me.getPartElements(false); while
             * (parts.size() > 0) { ModelElement part = parts.get(0); ArrayList<ModelElement>
             * partsParents = part.getDirectParentElements(); for (int i = 0; i <
             * partsParents.size(); i++) { //in partsParents nur die Parents lassen, an die alle
             * Verbindungen //des Parts umgehängt oder dupliziert werden muss (allen die auch
             * //gleichzeitig Part von me sind braucht man die Informationen //ihrer Kinder nicht
             * unterzuhängen, da sie ja auch weggelassen werden) if
             * (parts.contains(partsParents.get(i))) partsParents.remove(i--); } //sicher ist sicher
             * -> Kopie anlegen, falls durch irgendwelche Seiteneffekte sich die Kantenliste nochmal
             * ändert ArrayList<Kante> partEdges = new ArrayList<Kante>(part.getEdges()); for (int i
             * = 0; i < partEdges.size(); i++) { Doppelkante edge = (Doppelkante)partEdges.get(i);
             * //Alle Kanten zwischen dem zu löschenden Teil und dem Oberelement, das die
             * Eigenschaften des Teilelementes //Bekommen soll, werden nicht umgehängt (eigentlich
             * kann das nur die Teil-Von-Kante selbst sein, aber in //neuen Metamodellen wäre auch
             * etwas anderes denkbar) if (edge instanceof PartOfBeziehung) continue; //Mit wievielen
             * Elementen von der Art des parts darf das umzuhängende Element maximal verbunden sein?
             * int maxConnectedToPartCardinality = edge.isStartClass(part.getClass()) ?
             * edge.getMaxEndToStartCardinality() : edge.getMaxStartToEndCardinality(); for (int j =
             * partsParents.size() - 1; j > 0; j--) { ModelElement parent = partsParents.get(j);
             * //falls mehr Parents vorhanden sind, als mit dem umzuhängenden Element selbst
             * verbunden //sein dürfen, muss das umzuhängende Element dupliziert werden if
             * (maxConnectedToPartCardinality >= j) { ArrayList<ModelElement> elements2Duplicate =
             * new ArrayList<ModelElement>(); //einfach umhängen } else { } } for (ModelElement
             * parent : partsParents) { ModelElement start = edge.getStart() == part ? parent :
             * edge.getStart(); ModelElement end = edge.getEnd() == part ? parent : edge.getEnd();
             * int minElemCardinality = edge.isStartClass(start.getClass()) ?
             * edge.getMinStartToEndCardinality() : edge.getMinEndToStartCardinality(); if
             * (minElemCardinality > 0) continue; int dir = edge.getDirection(); String edgeName =
             * edge.getName(); String edgeDescrip = edge.getDescription(); Kante newEdge = null; if
             * (dir == Doppelkante.FORWARD) { newEdge = link(gdcoll, edge, start, end); } else if
             * (dir == Doppelkante.BACKWARD) { newEdge = link(gdcoll, edge, end, start); } else if
             * (dir == Doppelkante.DOUBLE) { newEdge = link(gdcoll, edge, start, end); link(gdcoll,
             * edge, end, start); } doc.setName(newEdge, edgeName, TransactionManager.STANDARD_PID);
             * doc.setDescription(newEdge.getHashString(), edgeDescrip,
             * TransactionManager.STANDARD_PID); } } gdcoll.deleteElement(part, doc,
             * TransactionManager.STANDARD_PID); parts = me.getDirectPartElements(); } }
             */
            private void removeChilds(final ModelElement me, final GraphDocument doc) {
                GDCollection gdcoll = doc.getCollection();
                List<ModelElement> parts = me.getDirectPartElements();
                while (parts.size() > 0) {
                    ModelElement part = parts.get(0);
                    removeChilds(part, doc);
                    // sicher ist sicher -> Kopie anlegen, falls durch irgendwelche Seiteneffekte
                    // sich die Kantenliste nochmal ändert
                    for (Kante edge : part.getEdges()) {
                        // Alle Kanten zwischen dem zu löschenden Teil und dem Oberelement, das die
                        // Eigenschaften des Teilelementes
                        // Bekommen soll, werden nicht umgehängt (eigentlich kann das nur die
                        // Teil-Von-Kante selbst sein, aber in
                        // neuen Metamodellen wäre auch etwas anderes denkbar)
                        if (edge instanceof PartOfBeziehung) {
                            continue;
                        }
                        List<ModelElement> parentElements = part.getDirectParentElements();
                        for (ModelElement parent : parentElements) {
                            ModelElement start = edge.getStart() == part ? parent : edge.getStart();
                            ModelElement end = edge.getEnd() == part ? parent : edge.getEnd();
                            int minElemCardinality = edge.isStartClass(start.getClass()) ? edge.getMinStartToEndCardinality() : edge.getMinEndToStartCardinality();
                            if (minElemCardinality > 0) {
                                continue;
                            }
                            int dir = ((Doppelkante) edge).getDirection();
                            String edgeName = edge.getName();
                            String edgeDescrip = edge.getDescription();
                            Kante newEdge = null;
                            if (dir == Doppelkante.FORWARD) {
                                newEdge = link(gdcoll, edge, start, end);
                                gdcoll.unlink(edge.getStart(), edge.getEnd(), edge.getClass(), TransactionManager.STANDARD_PID);
                            } else if (dir == Doppelkante.BACKWARD) {
                                newEdge = link(gdcoll, edge, end, start);
                                gdcoll.unlink(edge.getEnd(), edge.getStart(), edge.getClass(), TransactionManager.STANDARD_PID);
                            } else if (dir == Doppelkante.DOUBLE) {
                                newEdge = link(gdcoll, edge, start, end);
                                link(gdcoll, edge, end, start);
                                gdcoll.unlink(edge.getStart(), edge.getEnd(), edge.getClass(), TransactionManager.STANDARD_PID);
                                gdcoll.unlink(edge.getEnd(), edge.getStart(), edge.getClass(), TransactionManager.STANDARD_PID);
                            }
                            doc.setName(newEdge, edgeName, TransactionManager.STANDARD_PID);
                            doc.setDescription(newEdge, edgeDescrip, TransactionManager.STANDARD_PID);
                        }
                    }
                    gdcoll.deleteElement(part, doc, TransactionManager.STANDARD_PID);
                    parts = me.getDirectPartElements();
                }
            }
        };

        /** Löscht das aktuell ausgewählte Element aus dem Teilmodell */
        public static final Action REMOVE_FROM_SUBMODEL = new StaticAction(ActionIdentifier.remove_from_submodel, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                exec(GDCommands.REMOVE_ELEMENT_FROM_SZENARIO);
            }

            @Override
            public boolean isEnabled() {
                GraphDocument doc = getSelectedDoc();
                return super.isEnabled() && doc.isSelection() && doc instanceof Szenario;
            }
        };

        /** Löscht das aktuell ausgewählte Element aus dem Gesamtmodell */
        public static final Action REMOVE_FROM_MODEL = new StaticAction(ActionIdentifier.remove_from_model, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                exec(GDCommands.DELETE);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedDoc().isSelection();
            }
        };

        /** Öffnet ein Options-Fenster zum Löschen des aktuell ausgewählten Elements */
        public static final Action REMOVE = new StaticAction(ActionIdentifier.remove, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                exec(GDCommands.REMOVE_ELEMENT);
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && getSelectedDoc().isSelection();
            }
        };

        /** Wählt alle Elemente im Teilmodell aus */
        public static final Action SELECT_ALL = new StaticAction(ActionIdentifier.select_all, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                Static.showProgressDialog();
                Static.setProgressDialogTitle(Tool3lgmConstants.getResString("select_all"));
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
                    JOptionPane.showMessageDialog(getTool(), Tool3lgmConstants.getResString("message_close_all_dialogs"));
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
                    JOptionPane.showMessageDialog(getTool(), Tool3lgmConstants.getResString("message_close_all_dialogs"));
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
     * Actions für das Erstellen, Laden, Speichern, ... von Dateien.
     *
     * @author fstephan
     */
    public static class FileActions {

        /**
         * Actions für den Daten-Export
         *
         * @author fstephan
         */
        public static class ExportActions {

            /** Öffnet einen Dialog zum Export des Models als Grafik-Datei */
            public static final Action EXPORT_GRAPHIC = new StaticAction(ActionIdentifier.export_graphic, PPP, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    AbstractInternalFrame selframe = getTool().getActiveFrame();
                    if (selframe instanceof ToolInternalFrame) {
                        InputGraphArea iga = ((ToolInternalFrame) selframe).getInputGraphArea();
                        iga.setPaintState(PaintState.SAVE_IMAGE_AS_FILE);
                        ComponentAsImageExportHandler.createFile(iga);
                        iga.setPaintState(PaintState.REGULAR);
                    } else if (selframe instanceof TableInternalFrame) {
                        JScrollPane sp = selframe.getScrollPane();
                        Dimension size = sp.getSize();
                        sp.setSize(sp.getMaximumSize());
                        sp.revalidate();
                        ComponentAsImageExportHandler.createFile(sp);
                        sp.setSize(size);
                        sp.revalidate();
                    }
                }
            };

            /** Öffnet einen Dialog zur Anwendung von XSL-Scripts auf das Modell */
            public static final Action EXPORT_XSLT = new StaticAction(ActionIdentifier.export_xslt, PPP, true) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    // der Dialog zeigt sich im Konstuktor selbst an
                    new XMLExportDialog(getTool(), getSelectedDoc().getCollection());
                }
            };

            /** Öffnet einen Dialog zum Export eines Teilmodells */
            public static final Action EXPORT_SUBMODEL = new StaticAction(ActionIdentifier.export_submodel, PPP, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    SzenarioDialog.showExportDialog(getTool(), getSelectedCollection());
                }
            };

            /** Öffnet einen Dialog zum Export des gesamten Models als HTML-Site */
            public static final Action EXPORT_WEB = new StaticAction(ActionIdentifier.export_web, PPP, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    WebExportDialog.showWebExportDialog(getTool(), getSelectedCollection());
                }
            };

            /** Öffnet einen Dialog zum Export einzelner Elemente in tab-separiertem Format */
            public static final Action EXPORT_DATA = new StaticAction(ActionIdentifier.export_data, PPP, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    DataExportModule.exportData(getSelectedDoc());
                }
            };
        }

        /**
         * Actions für den Daten-Import
         *
         * @author fstephan
         */
        public static class ImportActions {

            /** Öffnet einen Dialog zum Import von Teilmodellen */
            public static final Action IMPORT_SUBMODEL = new StaticAction(ActionIdentifier.import_submodel, PPP, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    ExtendedFileChooser oeffnenDialog = new ExtendedFileChooser(null);
                    oeffnenDialog.setMultiSelectionEnabled(false);
                    oeffnenDialog.setFileFilters(false, Tool3lgmConstants.getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED));
                    if (oeffnenDialog.showOpenDialog(getTool()) == ExtendedFileChooser.APPROVE_OPTION) {
                        GDCollection selectedGDColl = getSelectedCollection();
                        GDCollectionImExportHandler imExportHandler = selectedGDColl.getImExportHandler();
                        imExportHandler.importSzenarios(oeffnenDialog.getSelectedFile(), true);
                    }
                }
            };

            /** Öffnet einen Dialog zum Import von Modellen */
            public static final Action IMPORT_MODEL = new StaticAction(ActionIdentifier.import_model, PPP, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    ExtendedFileChooser oeffnenDialog = new ExtendedFileChooser(null);
                    oeffnenDialog.setMultiSelectionEnabled(false);
                    oeffnenDialog.setFileFilters(false, Tool3lgmConstants.getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED));
                    if (oeffnenDialog.showOpenDialog(getTool()) == ExtendedFileChooser.APPROVE_OPTION) {
                        GDCollection selectedGDColl = getSelectedCollection();
                        GDCollectionImExportHandler imExportHandler = selectedGDColl.getImExportHandler();
                        imExportHandler.importModel(oeffnenDialog.getSelectedFile());
                    }
                }
            };

            /** Öffnet einen Dialog zum Import von Daten im tab-separierten Format */
            public static final Action IMPORT_DATA = new StaticAction(ActionIdentifier.import_data, PPP, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    new DataImportModule(getSelectedCollection());
                }
            };
        }

        /** Öffnen eines neuen Models */
        public static final Action ACTION_NEW_MODEL = new StaticAction(ActionIdentifier.ACTION_NEW_MODEL) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getTool().openFile(false);
            }
        };

        /** Öffnen eines bestehenden Models */
        public static final Action OPEN = new StaticAction(ActionIdentifier.ACTION_OPEN_MODEL, PPP) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getTool().openFile(true);
            }
        };

        /** Speichern des Models an bekannter Stelle */
        public static final Action SAVE = new StaticAction(ActionIdentifier.ACTION_SAVE_MODEL, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                Tool3lgm tool3lgm = getTool();
                if (!tool3lgm.fileSave(false)) {
                    JOptionPane.showMessageDialog(tool3lgm, Tool3lgmConstants.getErrString("save_failed"), "", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        /** Speichern des Models an neuer Stelle */
        public static final Action SAVEAS = new StaticAction(ActionIdentifier.ACTION_SAVE_MODEL_AS, PPP, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                Tool3lgm tool3lgm = getTool();
                if (!tool3lgm.fileSave(true)) {
                    JOptionPane.showMessageDialog(tool3lgm, Tool3lgmConstants.getErrString("save_failed"), "", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        /** Schließen des Models */
        public static final Action CLOSE = new StaticAction(ActionIdentifier.ACTION_CLOSE_MODEL, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getTool().fileClose();
            }
        };

        /** Zeigt die Beschreibung des Tools an */
        public static final Action DESCRIPTION = new StaticAction(ActionIdentifier.ACTION_SHOW_MODEL_DESCRIPTION_FRAME, PPP, true) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedCollection().showDescriptionFrame(true);
            }
        };

        /*
         * Die Actions zum Öffnen der zuletzt verwendeten Dateien befinden sich in der Klasse {@link
         * DynamicActions}
         */

        /** Drucken des Models (hat keine Wirkung, weil Drucken noch nicht implementiert ist) */
        public static final Action PRINT = new StaticAction(ActionIdentifier.print) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                throw new UnsupportedOperationException("Drucken wird nicht unterstützt");
            }
        };

        /** Beenden des Programms */
        public static final Action EXIT = new StaticAction(ActionIdentifier.exit) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Tool3lgm tool3lgm = getTool();
                tool3lgm.windowClosing(new WindowEvent(tool3lgm, WindowEvent.WINDOW_CLOSING));
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
                Static.getTool().importLicenseFile();
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

        public static final Action GLOBAL_LAYOUT = new StaticAction(ActionIdentifier.global_layout, PPP, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
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

            /**
             * (De-)Aktiviert das Anzeigen von Konfigurations-Redundanz für {@link Aufgabe}n. TODO:
             * AXS,FTS: Wirft Exception seit ToolMenu
             */
            public static final Action CONFIGURATIONAL_REDUNDANCY = new StaticAction(ActionIdentifier.configurational_redundancy, (Boolean) UserProperties.isShowABKonfigRedundance()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    boolean isSelected = isSelected();
                    UserProperties.setShowABKonfigRedundance(isSelected);
                    if (isSelected) {
                        for (GDCollection col : getTool().getCollections()) {
                            col.computeRedundance(Aufgabe.class, true);
                        }
                    } else {
                        for (GDCollection col : getTool().getCollections()) {
                            col.clearTextRightDown(Aufgabe.class);
                        }
                    }
                    distributeElementGraphicsChanged();
                }
            };

            /** (De-)Aktiviert das Anzeigen von Daten-Redundanz für {@link Objekttyp}en */
            public static final Action DATA_REDUNDANCY = new StaticAction(ActionIdentifier.data_redundancy, (Boolean) UserProperties.isShowDataRedundance()) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    boolean isSelected = isSelected();
                    UserProperties.setShowDataRedundance(isSelected);
                    if (isSelected) {
                        for (GDCollection col : getTool().getCollections()) {
                            col.computeRedundance(Objekttyp.class, false);
                        }
                    } else {
                        for (GDCollection col : getTool().getCollections()) {
                            col.clearTextRightDown(Objekttyp.class);
                        }
                    }
                    distributeElementGraphicsChanged();
                }
            };

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

            /**
             * (Deaktiviert das Anzeigen von Bausteinschnittstellen ohne Kommunikationspartner im
             * Teilmodell
             */
            public static final StaticAction HIDE_UNASSOCIATED_INTERFACES = new StaticAction(ActionIdentifier.HIDE_UNASSOCIATED_INTERFACES) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    exec(GDCommands.HIDE_UNASSOCIATED_INTERFACES);
                }

            };

            /** Zeigt alle ausgeblendeten Bausteinschnittstellen wieder an */
            public static final StaticAction UNHIDE_ALL_INTERFACES = new StaticAction(ActionIdentifier.UNHIDE_ALL_INTERFACES) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    exec(GDCommands.UNHIDE_ALL_INTERFACES);
                }

            };

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
                if (JOptionPane.showOptionDialog(null, rmip, Tool3lgmConstants.getResString("rmi_settings"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                    UserProperties.setRMIRegistryPort(rmip.getRmiRegistryPortTextFieldValue());
                }
                if (!oldRegPort.equals(UserProperties.getRMIRegistryPort())) {
                    JOptionPane.showMessageDialog(getTool(), Tool3lgmConstants.getResString("settings_info"));
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
                int answer = JOptionPane.showConfirmDialog(getTool(), Tool3lgmConstants.getResString("deleteSzenario"), Tool3lgmConstants.getResString("warnung"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
            public static final Action SWITCH_SHOW_PAINTING_BAR = new StaticAction(ActionIdentifier.painting, true, true) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    if (isSelected()) {
                        getTool().getWorkArea().add(getTool().getWerkzeugleiste(), BorderLayout.SOUTH);
                        getTool().getWorkArea().revalidate();
                    } else {
                        getTool().getWorkArea().remove(getTool().getWerkzeugleiste());
                        getTool().getWorkArea().revalidate();
                    }
                }
            };

            /** (De-)aktiviert die Standard-Toolbar */
            public static final Action SWITCH_SHOW_STANDARD_BAR = new StaticAction(ActionIdentifier.standard, true, true) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!isEnabled()) {
                        return;
                    }
                    Tool3lgm tool = getTool();
                    if (isSelected()) {
                        tool.getContentPane().add(tool.getToolBar(), BorderLayout.NORTH);
                        tool.getWorkArea().revalidate();
                    } else {
                        tool.getContentPane().remove(tool.getToolBar());
                        tool.getWorkArea().revalidate();
                    }
                }
            };
        }

        /** (De-)Aktiviert das Anzeigen des ModelBrowsers */
        public static final Action SWITCH_SHOW_BROWSER = new StaticAction(ActionIdentifier.browser, true, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getTool().showModelBrowser(isSelected());
            }
        };

        /**
         * Wechselt das Layout des ModelBrowsers * / public static final Action
         * SHOW_SUBMODELS_IN_BROWSER_SIDE_BY_SIDE = new
         * StaticAction(ActionIdentifier.showSubModelsInBrowserSideBySide, false,
         * UserProperties.isShowSubModelsInBrowserSideBySide()) {
         *
         * @Override public void actionPerformed(ActionEvent e) {
         *           UserProperties.setShowSubModelsInBrowserSideBySide(isSelected()); ModelBrowser
         *           br = getTool().getModelBrowserPanel().getActiveBrowser(); if (br != null) {
         *           LGMTabbedPane p = (LGMTabbedPane) br.getSelectedComponent(); if (p != null)
         *           p.setTabsInOneLineLayout(isSelected()); } } }; //
         *           ////////////////////////////////////// // Es ist immer nur eine der Actions //
         *           // ONE_LAYER_PERSPECTIVE oder // // THREE_LAYER_PERSPECTIVE aktiviert. // //
         *           Dies wird durch // // InputGraphArea.isMultiViewEnabled() // // sichergestellt.
         *           // ///////////////////////////////////////// /** Wechselt zur
         *           Ein-Ebenen-Ansicht
         */
        public static final Action ONE_LAYER_PERSPECTIVE = new StaticAction(ActionIdentifier.one_layer_perspective, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                ToolInternalFrame frame = (ToolInternalFrame) getTool().getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                area.setMultiViewEnabled(false);
                frame.getGraphDocument().deselectAll(false);
                distributeViewChanged();
            }

            @Override
            public boolean isEnabled() {
                if (!super.isEnabled()) {
                    return false;
                }
                AbstractInternalFrame abstractFrame = getTool().getActiveFrame();
                if (abstractFrame == null || !(abstractFrame instanceof ToolInternalFrame)) {
                    return false;
                }
                ToolInternalFrame frame = (ToolInternalFrame) getTool().getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                return area.isMultiViewEnabled();
            }
        };

        /** Wechselt zur Drei-Ebenen-Ansicht */
        public static final Action THREE_LAYER_PERSPECTIVE = new StaticAction(ActionIdentifier.three_layer_perspective, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                ToolInternalFrame frame = (ToolInternalFrame) getTool().getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                area.setMultiViewEnabled(true);
                frame.getGraphDocument().deselectAll(false);
                distributeViewChanged();
            }

            @Override
            public boolean isEnabled() {
                if (!super.isEnabled()) {
                    return false;
                }
                AbstractInternalFrame abstractFrame = getTool().getActiveFrame();
                if (abstractFrame == null || !(abstractFrame instanceof ToolInternalFrame)) {
                    return false;
                }
                ToolInternalFrame frame = (ToolInternalFrame) getTool().getActiveFrame();
                InputGraphArea area = frame.getInputGraphArea();
                return !area.isMultiViewEnabled();
            }
        };

        /** Zeigt die Fachliche Ebene an, falls die Ein-Ebenen-Ansicht aktiviert ist */
        public static final Action SHOW_DOMAIN_LAYER = new StaticAction(ActionIdentifier.domain_layer, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedCollection().setActiveLayer(ModelConstants.DOMAIN_LAYER);
            }
        };

        /** Zeigt die Logische Werzeugebene an, falls die Ein-Ebenen-Ansicht aktiviert ist */
        public static final Action SHOW_LOGICAL_TOOL_LAYER = new StaticAction(ActionIdentifier.logical_tool_layer, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedCollection().setActiveLayer(ModelConstants.LOGICAL_LAYER);
            }
        };

        /** Zeigt die physische Werkzeugebene an, falls die Ein-Ebenen-Ansicht aktiviert ist */
        public static final Action SHOW_PHYSICAL_TOOL_LAYER = new StaticAction(ActionIdentifier.physical_tool_layer, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
                getSelectedCollection().setActiveLayer(ModelConstants.PHYSICAL_LAYER);
            }
        };

        /** Öffnet einen Dialog für die Einstellung von Größe, Abstand, etc. der Ebenen */
        public static final Action OPEN_LAYER_SETTINGS = new StaticAction(ActionIdentifier.settings, PPP, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                AbstractInternalFrame selframe = getTool().getActiveFrame();
                if (selframe != null && selframe instanceof ToolInternalFrame) {
                    EinstellungDialog dialog = new EinstellungDialog();
                    dialog.showDialog((ToolInternalFrame) selframe);
                }
            }

            @Override
            public boolean isEnabled() {
                AbstractInternalFrame f = getActiveFrame();
                return super.isEnabled() && getSelectedDoc() instanceof Szenario && f != null && f instanceof ToolInternalFrame;
            }
        };

        /** Öffnet die Matrix-Ansicht */
        public static final Action OPEN_MATRIX = new StaticAction(ActionIdentifier.matrix, true) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!isEnabled()) {
                    return;
                }
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
    private static final String PPP = Tool3lgmConstants.getResString("3points");

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
