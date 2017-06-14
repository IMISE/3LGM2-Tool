/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.analyse.process.ProzessStructurePanel;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractSingleConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DoubleMeaningEdgePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PrzAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Schnittstelle;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;

/**
 * @author fstephan Diese Klasse stellt statische Methoden zur Erzeugung von <code>LGMAction</code>
 *         s bereit. Panels erzeugen und verwenden diese Actions um Funktionen wie etwa das
 *         Verschieben von Elementen zwischen ihren Trees bereitstellen zu können.
 *         <code>LGMActions</code> bereit.
 */
public class LGMActionLibrary {

    /*
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! zu
     * getAddElementAction(...) und getRemoveElementAction(...): Bei Panels mit mehr als zwei Trees,
     * kann es dazu kommen, dass Elemente falsch verschoben werden. Um dieses Problem zu beheben,
     * sollte einfach der Wert von switchTree geändert werden.
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "addButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     * @param edp
     * @param switchTree
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getAddElementAction(final JTree srcTree, final JTree targetTree, final ElementDialogPanel edp, final boolean switchTree) throws ActionNotDefinedForClassException {

        final boolean switchIt = switchTree;
        final JTree tree1 = srcTree;
        final JTree tree2 = targetTree;
        final GraphDocument doc = edp.getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final ElementPropertyDialog dialog = edp.getDialog();
        final ModelElement modelElement = edp.getModelElement();

        if (edp instanceof StructurePanel || edp instanceof NConnectionPanel || edp instanceof DoubleMeaningEdgePanel) {
            return new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {
                @Override
                public void execute(final EventObject e) {
                    TreePath[] selpaths = tree1.getSelectionPaths();
                    if (selpaths != null) {
                        for (int n = 0; n < selpaths.length; n++) {
                            // if(lomodel.getChildCount(loroot)>0) return;
                            LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                            ModelElement me = ((ElementContainer) node.getUserObject()).getElement();
                            ModelElement topLevelMe = getTopLevelModelElement(tree2);
                            if (switchIt) {
                                gdcoll.link(me, topLevelMe, dialog.getTransactionID());
                            } else {
                                gdcoll.link(topLevelMe, me, dialog.getTransactionID());
                            }
                        }
                    }
                }

            };
        }

        else if (edp instanceof ProzessStructurePanel) {

            return new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {

                @Override
                public void execute(final EventObject eo) {
                    getDragNDropLocateElementAsTargetAction(tree2).execute(eo);
                    TreeModel lmodel = tree2.getModel();
                    LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();
                    ProzessStructurePanel panel = (ProzessStructurePanel) edp;

                    synchronized (tree2.getTreeLock()) {
                        TreePath selPath = tree1.getSelectionPath();
                        // wenn rechts etwas selektiert war
                        if (selPath != null) {
                            // den selektierten Knoten ermitteln
                            LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
                            // wenn es sich bei dem rechts selektierten Knoten
                            // um eine Aufgabe handelt
                            if (node.getUserObject() instanceof NodeContainer) {
                                // seinen Container holen (diese muessen links
                                // im Baum geaddet werden)
                                NodeContainer knot = (NodeContainer) node.getUserObject();
                                if (knot.getElement() instanceof Aufgabe) {
                                    // wenn links was selektiert ist, dann muss
                                    // die naechste Aufgabe ueber bzw. vor der
                                    // selektierten eingefuegt werden
                                    selPath = tree2.getSelectionPath();
                                    boolean nothingSelected = true;
                                    // wenn links etwas selektiert war
                                    if (selPath != null) {
                                        nothingSelected = false;
                                        node = (LGMTreeNode) selPath.getLastPathComponent();
                                        // prüfen, ob eine Aufgabe selektiert ist
                                        if (node.getUserObject() instanceof NodeContainer && ((NodeContainer) node.getUserObject()).getElement() instanceof Aufgabe) {
                                            int selRow = tree2.getRowForPath(selPath);
                                            int index = lmodel.getIndexOfChild(lroot, node);
                                            gdcoll.link(PrzAufVerbindung.class, modelElement, knot.getElement(), index, GDCommands.INVALID_EDGE_INDEX, dialog.getTransactionID());
                                            // ((NodeContainer)modelElement.getContainer(doc)).addSpecialInfoTarget(index,knot);
                                            selRow++;
                                            while (tree2.getPathForRow(selRow).getPathCount() != 2) {
                                                selRow++;
                                            }
                                            panel.setSelectionRow(tree2, selRow);

                                        }
                                    }
                                    // wenn links nichts selektiert war ->
                                    // einfach hinten anhaengen
                                    if (nothingSelected) {
                                        // es wird eine neue ProzessKante
                                        // angelegt, aber der rechte Baum
                                        // braucht nicht aktualisiert werden
                                        gdcoll.link(PrzAufVerbindung.class, modelElement, knot.getElement(), modelElement.getEdgesCount(), GDCommands.INVALID_EDGE_INDEX, dialog.getTransactionID());
                                        tree2.scrollRowToVisible(tree2.getRowCount() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            };
        } else {
            return null;
        }
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     * @param edp
     * @param switchTree
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getDisconnectAction(final JTree srcTree, final JTree targetTree, final ElementDialogPanel edp, final boolean switchTree) throws ActionNotDefinedForClassException {

        final boolean switchIt = switchTree;
        final JTree tree1 = srcTree;
        final JTree tree2 = targetTree;
        final GDCollection gdcoll = edp.getGraphDocument().getCollection();
        final ElementPropertyDialog dialog = edp.getDialog();
        final ElementDialogPanel pane = edp;
        final ModelElement modelElement = edp.getModelElement();

        if (edp instanceof StructurePanel || edp instanceof NConnectionPanel || edp instanceof DoubleMeaningEdgePanel) {

            LGMAction returnAction = new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {

                @Override
                public void execute(final EventObject e) {
                    TreePath[] selpaths = tree1.getSelectionPaths();
                    if (selpaths != null) {
                        for (int n = 0; n < selpaths.length; n++) {
                            // if(lomodel.getChildCount(loroot)>0) return;
                            LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                            ElementContainer knot = (ElementContainer) node.getUserObject();

                            ModelElement topLevelModelElement;
                            if (tree2 == null) {
                                topLevelModelElement = getTopLevelModelElement(tree1);
                            } else {
                                topLevelModelElement = getTopLevelModelElement(tree2);
                            }

                            if (switchIt == true) {
                                gdcoll.unlink(knot.getElement(), topLevelModelElement, dialog.getTransactionID());
                            } else {
                                gdcoll.unlink(topLevelModelElement, knot.getElement(), dialog.getTransactionID());
                            }
                        }
                    }
                }
            };
            return returnAction;

        } else if (edp instanceof ProzessStructurePanel) {
            return new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
                @Override
                public void execute(final EventObject e) {
                    getDragNDropLocateElementAsTargetAction(tree2).execute(e);
                    TreeModel lmodel = tree1.getModel();
                    LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();
                    ProzessStructurePanel panel = (ProzessStructurePanel) pane;

                    synchronized (tree2.getTreeLock()) {
                        TreePath selPath = tree1.getSelectionPath();
                        // wenn links etwas selektiert war
                        if (selPath != null) {
                            LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
                            // prüfen, ob eine Aufgabe selektiert ist
                            Object knot = node.getUserObject(); // auf keinen
                            // Fall hier
                            // gleich auf
                            // ElementContainer
                            // casten, weil
                            // es auch ein
                            // String sein
                            // kann !!!
                            if (!(knot instanceof String)) {
                                ModelElement otherMe = ((ElementContainer) knot).getElement();
                                if (otherMe instanceof Aufgabe) {
                                    // es wird eine neue ProzessKante angelegt, aber
                                    // der rechte Baum braucht nicht aktualisiert
                                    // werden
                                    int index = lmodel.getIndexOfChild(lroot, node);
                                    gdcoll.unlink(modelElement, otherMe, PrzAufVerbindung.class, index, dialog.getTransactionID());
                                }
                            }
                            // ####################################################################################################
                            // Dies hier evtl. weglassen, da es auf nem lahmen
                            // Rechner und nem großen Modell rel. lange dauern
                            // kann
                            // Die der auf der linken Seite entfernten Aufgabe
                            // auf der rechten Seite entsprechende wird
                            // selektiert.
                            // Diese Funktion geht davon aus, dass der rechte
                            // Baum komplett expandiert ist, was er in dem Fall,
                            // dass alle Kinder von rroot Blätter sind und rroot
                            // selbst nicht angezeigt wird immer automatisch
                            // ist.
                            for (int i = 0; i < tree2.getRowCount(); i++) {
                                if (((LGMTreeNode) tree2.getPathForRow(i).getLastPathComponent()).getUserObject() == knot) {
                                    panel.setSelectionRow(tree2, i);
                                    break;
                                }
                            }
                        }
                        return;
                    }
                }
            };
        } else {
            throw new ActionNotDefinedForClassException(pane.getClass().getName());
        }
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die auf Mouse-Aktionen in AbstractSingleConnectionPanel reagiert.
     *
     * @param panel
     * @param edp
     * @return
     */
    public static final LGMAction getMouseAction(final AbstractSingleConnectionPanel panel) {
        return getMouseActionInternal(panel, panel);
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die auf Mouse-Aktionen in Trees reagiert.
     *
     * @param tree
     * @param panel
     * @return
     */
    public static final LGMAction getMouseAction(final JTree tree, final ElementDialogPanel panel) {
        return getMouseActionInternal(tree, panel);
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die auf Mouse-Aktionen in Trees reagiert.
     *
     * @param component
     * @param panel
     * @return
     */
    private static final LGMAction getMouseActionInternal(final JComponent component, final ElementDialogPanel panel) {
        return new LGMAction() {
            @Override
            public void execute(final EventObject eo) {
                MouseEvent e = (MouseEvent) eo;
                boolean popup = Tool3lgmConstants.isPopupTrigger(e);
                boolean doubleClick = !popup && e.getClickCount() > 1;
                if (popup || doubleClick) {

                    //find selection
                    int xin = e.getX();
                    int yin = e.getY();
                    Object selection = null;
                    if (component instanceof JTree) {
                        JTree tree = (JTree) component;
                        TreePath path = tree.getPathForLocation(xin, yin);
                        if (path == null) {
                            return;
                        }
                        LGMTreeNode node = (LGMTreeNode) path.getLastPathComponent();
                        if (node == null) {
                            return;
                        }
                        selection = node.getUserObject();
                    } else if (component instanceof JComboBox) {
                        JComboBox<?> combobox = (JComboBox<?>) component;
                        selection = combobox.getSelectedItem();
                    } else if (panel instanceof AbstractSingleConnectionPanel) {
                        AbstractSingleConnectionPanel singleSelectionPanel = (AbstractSingleConnectionPanel) panel;
                        selection = singleSelectionPanel.getSelection();
                    }

                    //set selection
                    GraphDocument doc = panel.getGraphDocument();
                    ElementContainer selected = null;
                    if (selection instanceof ElementContainer) {
                        selected = (ElementContainer) selection;
                    } else if (selection instanceof ModelElement) {
                        //da die Selektion sowieso in allen Teilmodellen ausgeführt wird, ist es hier ok, das ModelElement durch
                        //den Container aus dem Hauptdokument zu ersetzen
                        ModelElement me = (ModelElement) selection;
                        GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();
                        selected = me.getContainer(mainDoc);
                    }
                    if (selected != null) {
                        doc.select(selected, panel.getTransactionID());
                        if (popup) {
                            Tool3lgm.getContextGenerator().getTreeKnotContextMenu().show(e.getComponent(), e.getX() + 3, e.getY() + 3);
                        } else if (doubleClick) {
                            doc.showPropertyDialog(selected.getElement());
                        }

                    }
                }
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die auf das Selektieren von Elementen in
     * Trees reagiert.
     *
     * @param tree
     * @param panel
     * @return
     */
    public static final LGMAction getTreeSelectionAction(final JTree tree, final ElementDialogPanel panel) {
        return new LGMAction() {
            @Override
            public void execute(final EventObject e) {
                if (panel.getCorrectingSelectionCount() > 0) {
                    return;
                }
                panel.getGraphDocument().deselectAll(true);
                if (e == null) {
                    return;
                }
                panel.setLastSelEvent(e);

                JTree tree = (JTree) e.getSource();

                TreePath[] paths = tree.getSelectionPaths();
                if (paths != null) {
                    panel.removeHighLights();
                    for (int i = 0; i < paths.length; i++) {
                        LGMTreeNode node = (LGMTreeNode) paths[i].getLastPathComponent();
                        if (!(node.getUserObject() instanceof String)) {
                            if (node.isSelectable()) {
                                // das hier muss sein, falls im Baum ein Element
                                // keinen Container im aktuellen Doc besitzt
                                ElementContainer ec = (ElementContainer) node.getUserObject();
                                ElementContainer knotCont = ec.getElement().getContainer(panel.getGraphDocument());
                                if (knotCont != null) {
                                    // highlight ist eine Container-Eigenschaft
                                    panel.addHighlight(knotCont);
                                    knotCont.setHighLight(true);
                                }
                                // selected ist eine Container-Eigenschaft
                                panel.getGraphDocument().addToSelection(ec, panel.getDialog().getTransactionID());
                            } else {
                                panel.setCorrectingSelectionCount(panel.getCorrectingSelectionCount() + 1);
                                tree.removeSelectionPath(paths[i]);
                                panel.setCorrectingSelectionCount(panel.getCorrectingSelectionCount() - 1);
                            }
                        }
                    }
                }
                panel.getGraphDocument().distributeEvent(GraphDocument.SELECTION_CHANGED, panel.getDialog().getTransactionID());
            }
        };
    }

    /**
     * Methode liefert eine neue LGMAction zurück. Diese LGMAction verwaltet das Initialisieren von
     * DragNDrop in einem Panel. Alle Panels, die DragNDrop-Funktionalität bieten wollen, müssen
     * diese Action über einen MouseListener an ihre Trees anfügen. Dabei sollte diese Action sowohl
     * bei mousePressed als auch bei mouseEntered aufgerufen werden.
     *
     * @param dndActionChains
     */
    public static final LGMAction getDragNDropInitAction(final DragNDropInitializer.DragNDropActionChain[] dndActionChains) {

        final DragNDropInitializer.DragNDropActionChain[] chains = dndActionChains;

        return new LGMAction() {

            /**
             * Sammlung aller <code>DragNDropActionChain</code>s, die bei einem DragNDrop-Ereignis
             * ausgeführt werden können
             */
            private final DragNDropActionChain[] dndActionChains = chains;

            /**
             * Variable dient der Trennung von DragNDrop-Ausführung und DragNDrop-Initialisierungen.
             * Es kann entweder eine DragNDrop-Aktion in einem Panel ausgeführt werden, oder eine
             * DragNDrop-Aktion für ein Panel initialisiert werden. = <code>true</code>, wenn gerade
             * eine DragNDrop-Aktion ausgeführt wird =<code>false</code>, sonst
             */
            private boolean blockDragNDropInitializing;

            /**
             * Attribut speichert den zuletzt angeklickten Tree, um mehrfaches Initialisieren von
             * DragNDrop zu vermeiden.
             */
            private JTree lastEnteredTree;

            /**
             * Falls <code>e</code> ein <code>MouseEvent</code> ist, wird in Abhängigkeit davon, ob
             * die Maus über einen Tree bewegt bzw. ein Element des Trees angeklickt wurde, die
             * Methode <code>mouseEntered(MouseEvent me)</code> bzw.
             * <code>mousePressed(MouseEvent me)</code> aufgerufen.
             *
             * @param e
             */
            @Override
            public void execute(final EventObject e) {
                if (e instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e;
                    if (me.getID() == MouseEvent.MOUSE_ENTERED) {
                        mouseEntered(me);
                    } else if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                        mousePressed(me);
                    } else if (me.getID() == MouseEvent.MOUSE_DRAGGED) {
                        mousePressed(me);
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }

            /**
             * Hier wird, je nach dem welcher der Trees angeklickt wurde, die Selektion der Elemente
             * in den anderen Trees entfernt. Dadurch werden Uneindeutigkeiten beim DragNDrop
             * vermieden. Solang die Mousetaste gedrückt bleibt, ist das Initialisieren einer neuen
             * DragNDrop-Aktion deaktiviert, um das Auführen der aktuellen DragNDrop-Aktion nicht zu
             * behindern.
             *
             * @param me
             */
            private void mousePressed(final MouseEvent me) {

                blockDragNDropInitializing = true;

                if (!(me.getSource() instanceof JTree)) {
                    return;
                }

                JTree focusedTree = (JTree) me.getSource();

                int n = dndActionChains.length;

                if (n > 2) {
                    for (int i = 0; i < n; i++) {
                        JTree tree = dndActionChains[i].getSrcTree();
                        if (tree != focusedTree) {
                            tree.removeSelectionPaths(tree.getSelectionPaths());
                        }
                    }
                }

                blockDragNDropInitializing = false;
                mouseEntered(me);
            }

            /**
             * Methode ruft <code>activateDragNDrop(LGMDragNDropTree focusedTree)</code> auf, falls
             * sich die Mouse über einen der Trees des Panels befindet.
             *
             * @param me
             */
            private void mouseEntered(final MouseEvent me) {

                if (blockDragNDropInitializing == false && me.getSource() instanceof JTree) {
                    activateDragNDrop((JTree) me.getSource());
                }
            }

            /**
             * Methode aktiviert die DragNDrop-Funktion vom <code>focusedTree</code> auf alle Trees,
             * die in den <code>dndActionChains</code> als targetTree dieses Trees vorkommen.
             *
             * @param focusedTree
             */
            private void activateDragNDrop(final JTree focusedTree) {
                if (lastEnteredTree == focusedTree) {
                    return;
                }
                lastEnteredTree = focusedTree;
                for (int i = 0; i < dndActionChains.length; i++) {
                    DragNDropActionChain dndAC = dndActionChains[i];
                    if (dndAC.getSrcTree() == focusedTree) {
                        DragNDropInitializer.initDragNDrop(dndAC);
                    }
                }
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Erzeugen eines neuen Elements
     * realisiert.
     *
     * @param panel
     * @param elementClass
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getNewElementAction(final ElementDialogPanel panel, final Class<? extends ModelElement> elementClass) throws ActionNotDefinedForClassException {

        final GraphDocument doc = panel.getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final ElementPropertyDialog dialog = panel.getDialog();
        final ModelElement modelElement = panel.getModelElement();

        if (panel instanceof LGMDragNDropPanel) {
            final LGMDragNDropPanel dndPanel = (LGMDragNDropPanel) panel;

            if (panel instanceof NConnectionPanel) {
                return new LGMAction(Tool3lgmConstants.getResString("new")) {
                    @Override
                    public void execute(final EventObject eo) {
                        // doc.start_transaction(dialog.getTransactionID());
                        Knoten k = null;
                        if (modelElement instanceof Anwendungsbaustein && Schnittstelle.class.isAssignableFrom(elementClass)) {
                            doc.select(modelElement.getContainer(doc), dialog.getTransactionID());
                            GraphDocument.createAddicted(doc.getCollection().getSelectedDoc(), modelElement, AwbKommssVerbindung.class, elementClass, dialog.getTransactionID());
                            if (doc.getLastCreated() != null) {
                                if (elementClass.isAssignableFrom(doc.getLastCreated().getElement().getClass())) {
                                    k = (Knoten) doc.getLastCreated().getElement();
                                } else {
                                    System.out.println("Was ist mit der Selektion los????");
                                }
                            }
                        } else {
                            doc.createKnotenWithContainer(elementClass, dialog.getTransactionID());
                            if (doc.getLastCreated() != null) {
                                k = (Knoten) doc.getLastCreated().getElement();
                                gdcoll.link(dndPanel.getEdgeType(modelElement, k), modelElement, k, dialog.getTransactionID());
                            }
                        }
                        // doc.finish_transaction(dialog.getTransactionID());
                        // doc.distributeEvent(GraphDocument.DATA_CHANGED, null,
                        // null, dialog.getTransactionID());
                        return;
                    }
                };
            }
        } else {
            throw new ActionNotDefinedForClassException(panel.getClass().getName());
        }
        return null;

    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben eines Elements in
     * einem Tree realisiert.
     *
     * @param tree
     * @param edp
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getMoveDownAction(final JTree tree, final ElementDialogPanel edp) throws ActionNotDefinedForClassException {

        final ElementDialogPanel pane = edp;
        final JTree ltree = tree;
        final DefaultTreeModel lmodel = (DefaultTreeModel) tree.getModel();
        final LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();
        final GraphDocument doc = edp.getGraphDocument();
        final ModelElement modelElement = edp.getModelElement();
        final ElementPropertyDialog dialog = edp.getDialog();

        if (pane instanceof ProzessStructurePanel) {
            return new LGMAction("", Tool3lgmConstants.getIcon("runter2.gif")) {
                @Override
                public void execute(final EventObject eo) {
                    ProzessStructurePanel panel = (ProzessStructurePanel) pane;
                    // Aufaben haben Pfadlänge 2 (das nicht sichtbare root hat
                    // die 1)
                    TreePath selPath = ltree.getSelectionPath();
                    if (selPath != null && selPath.getPathCount() == 2) {
                        int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
                        if (pos1 < lroot.getChildCount() - 1) {
                            int pos2 = ltree.getRowForPath(selPath) + 1;
                            TreePath path = ltree.getPathForRow(pos2);
                            while (path.getPathCount() > 2) {
                                pos2++;
                                path = ltree.getPathForRow(pos2);
                            }
                            Enumeration<TreePath> en = ltree.getExpandedDescendants(path);
                            LGMTreeNode node = (LGMTreeNode) lroot.getChildAt(pos1 + 1);
                            lmodel.removeNodeFromParent(node);
                            lmodel.insertNodeInto(node, lroot, pos1);

                            if (en != null) {
                                panel.expandFullPath(true);
                                ltree.expandRow(pos1 + 1);
                                while (en.hasMoreElements()) {
                                    ltree.expandPath(en.nextElement()); // seine Unterknoten auch
                                                                        // expandieren
                                }
                                panel.willExpand(false);
                                panel.expandFullPath(false);
                            }
                            ltree.scrollPathToVisible(selPath);
                            node.setText("[" + (pos1 + 1) + "] " + node.getUserObject());
                            node = (LGMTreeNode) lroot.getChildAt(pos1 + 1);
                            node.setText("[" + (pos1 + 2) + "] " + node.getUserObject());
                            doc.swapEdgePositions(modelElement, pos1, pos1 + 1, dialog.getTransactionID());
                        }
                    }
                    ltree.repaint();
                    return;
                }
            };
        }
        throw new ActionNotDefinedForClassException(edp.getClass().getName());
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben eines Elements in
     * einem Tree realisiert.
     *
     * @param tree
     * @param edp
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getMoveUpAction(final JTree tree, final ElementDialogPanel edp) throws ActionNotDefinedForClassException {

        final ElementDialogPanel pane = edp;
        final JTree ltree = tree;
        final DefaultTreeModel lmodel = (DefaultTreeModel) tree.getModel();
        final LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();
        final GraphDocument doc = edp.getGraphDocument();
        final ModelElement modelElement = edp.getModelElement();
        final ElementPropertyDialog dialog = edp.getDialog();

        if (pane instanceof ProzessStructurePanel) {
            return new LGMAction("", Tool3lgmConstants.getIcon("hoch2.gif")) {
                @Override
                public void execute(final EventObject eo) {
                    ProzessStructurePanel panel = (ProzessStructurePanel) pane;
                    // Aufaben haben Pfadlänge 2 (das nicht sichtbare root hat
                    // die 1)
                    TreePath selPath = ltree.getSelectionPath();
                    // wenn links eine Aufgabe selektiert ist
                    if (selPath != null && selPath.getPathCount() == 2) {
                        // Position der selektierten Aufgabe im Baum bzw. Prozess holen
                        int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
                        // wenn nicht die erste sondern eine Aufgabe dahinter selektiert ist
                        if (pos1 > 0) {
                            // jetzt die Position der über der selektierten Aufgabe liegenden
                            // Aufgabe holen
                            // -> von dieser alle evtl. expandierten Unterknoten merken
                            // -> sie removen und unter der selektierten wieder einfügen
                            // -> alles was von ihr expandiert war, wieder expandieren
                            int pos2 = ltree.getRowForPath(selPath) - 1;
                            TreePath path = ltree.getPathForRow(pos2);
                            while (path.getPathCount() > 2) {
                                pos2--;
                                path = ltree.getPathForRow(pos2);
                            }

                            // wenn die selektierte Aufgabe expandierte Unterknoten hat (können max.
                            // 2 sein, nämlich
                            // "Interpretiert" und "Bearbeitet"), dann sind diese TreePathes jetzt
                            // in enum
                            Enumeration<TreePath> en = ltree.getExpandedDescendants(path);

                            // jetzt den Baum anpassen (DER WIRD IN DIESEM FALL IN buildLeftTree()
                            // NICHT VERÄNDERT)
                            // und weil hier noch die Expasionen anpasst werden (über enum), soll
                            // das auch hier bleiben!
                            LGMTreeNode node = (LGMTreeNode) lroot.getChildAt(pos1 - 1); // den
                            // oberen Knoten holen
                            lmodel.removeNodeFromParent(node); // ihn entfernen
                            lmodel.insertNodeInto(node, lroot, pos1); // ihn einen tiefer als vorher
                                                                      // einfügen, wenn die
                                                                      // selektierte Aufgabe
                                                                      // expandiert war
                            if (en != null) {
                                panel.expandFullPath(true); // muss sein wegen treeWillExpand,
                                                            // damits auch wirklich expandiert wird
                                ltree.expandRow(pos1 + 1); // den Knoten wieder expandieren
                                while (en.hasMoreElements()) {
                                    ltree.expandPath(en.nextElement()); // seine Unterknoten auch
                                                                        // expandieren
                                }
                                // das muss immer nch einem Expandieren zurückgesetzt werden (siehe
                                // treeWillExpand)
                                panel.willExpand(false);
                                panel.expandFullPath(false);
                            }
                            ltree.scrollPathToVisible(selPath);
                            // jetzt erst die Nummerierungen anpassen (auf keinen Fall vor dem
                            // Expandieren, weil sonst die Pfade nicht mehr stimmen)
                            node.setText("[" + (pos1 + 1) + "] " + node.getUserObject());
                            node = (LGMTreeNode) lroot.getChildAt(pos1 - 1);
                            node.setText("[" + pos1 + "] " + node.getUserObject());

                            // das switchen in den connections vom Prozess ausführen
                            doc.swapEdgePositions(modelElement, pos1, pos1 - 1, dialog.getTransactionID());
                        }
                    }
                    ltree.repaint();
                    return;
                }
            };
        }
        throw new ActionNotDefinedForClassException(edp.getClass().getName());
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die beim Eintreten eines
     * DragNDrop-Ereignisses, die konkrete Drop-Location im targetTree ermittelt. Dabei wird das
     * Element, was sich dem Mouse-Zeiger am nächsten befindet, als SelectionPath des targetTrees
     * gesetzt. Damit ist es möglich Elemente an eine bestimmte Position innerhalb eines Trees zu
     * verschieben. Methode wird hier automatisch in
     * <code>getAddElementAction(JTree srcTree, JTree targetTree, ElementDialogPanel edp, boolean switchTree)</code>
     * und in
     * <code>getRemoveElementAction(JTree srcTree, JTree targetTree, ElementDialogPanel edp, boolean switchTree)</code>
     * aufgerufen --> Panel muss sich also darum nicht kümmern
     *
     * @param tree
     */
    public static final LGMAction getDragNDropLocateElementAsTargetAction(final JTree tree) {
        return new LGMAction() {
            @Override
            public void execute(final EventObject eo) {
                if (!(eo instanceof DropTargetDropEvent)) {
                    return;
                }
                DropTargetDropEvent dtde = (DropTargetDropEvent) eo;
                Point p = dtde.getLocation();
                TreePath path = tree.getPathForLocation(p.x, p.y);
                tree.setSelectionPath(path);
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die beim Schließen eines Panels alle
     * WindowListener des Panels entfernt.
     *
     * @param edp
     */
    public static final LGMAction getWindowClosedAction(final ElementDialogPanel edp) {

        final ElementDialogPanel panel = edp;
        final ElementPropertyDialog dialog = panel.getDialog();

        return new LGMAction() {
            @Override
            public void execute(final EventObject eo) {
                panel.removeHighLightsAndSpecialInfos();
                WindowListener[] listeners = dialog.getWindowListeners();
                for (int i = 0; i < listeners.length; i++) {
                    dialog.removeWindowListener(listeners[i]);
                }
                Static.getMainFrame().repaint();
            }
        };
    }

    /**
     * @param edp
     */
    public static final LGMAction getComponentShownAction(final ElementDialogPanel edp) {

        final ElementDialogPanel panel = edp;

        return new LGMAction() {
            @Override
            public void execute(final EventObject eo) {
                panel.update();
            }
        };
    }

    /**
     * Gibt das <code>ModelElement</code> des <code>ElementPropertyDialog</code> s wieder, in dem
     * sich der <code>tree</code> befindet.
     *
     * @param tree TODO: diese Funktion hat jetzt das {@link LGMDragNDropPanel}, so dass das hier
     *            irgendwann mal weg kann
     */
    private static ModelElement getTopLevelModelElement(final JTree tree) {

        ModelElement me = null;

        try {
            ElementPropertyDialog d = (ElementPropertyDialog) tree.getTopLevelAncestor();
            me = d.getModelElement();
        } catch (Exception ex) {
            Log.log(Log.ERROR, "LGMActionLibary: could'nt find TopLevelAncestor for tree", ex);
        }
        return me;
    }

}
