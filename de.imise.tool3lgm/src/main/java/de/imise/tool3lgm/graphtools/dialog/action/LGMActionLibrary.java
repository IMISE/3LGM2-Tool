/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractSingleConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
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
                if (e.getClickCount() > 0) {
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
                        tree.setSelectionPath(path);
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
                    executeMouseAction(selection, panel, e);
                }
            }
        };
    }

    private static final void executeMouseAction(final Object selection, final ElementDialogPanel panel, final MouseEvent e) {
        boolean popup = Tool3lgmConstants.isPopupTrigger(e);
        boolean doubleClick = !popup && e.getClickCount() > 1;
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
