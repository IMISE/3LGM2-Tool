package de.imise.tool3lgm.graphtools.dialog.action;

import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;

import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowListener;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.log.Log;

/**
 * Diese Klasse stellt statische Methoden zur Erzeugung von <code>LGMAction</code>s bereit. Panels erzeugen und verwenden diese Actions um Funktionen
 * wie etwa das Verschieben von Elementen zwischen ihren Trees bereitstellen zu können. <code>LGMActions</code> bereit.
 *
 * @author fstephan (23.11.2007)
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
                GraphDocument doc = panel.getGraphDocument();
                ElementPropertyDialog dialog = panel.getDialog();
                int pid = dialog.getTransactionID();
                if (paths != null) {
                    panel.removeHighLights();
                    for (int i = 0; i < paths.length; i++) {
                        LGMTreeNode treeNode = (LGMTreeNode) paths[i].getLastPathComponent();
                        if (!(treeNode.getUserObject() instanceof String)) {
                            if (treeNode.isSelectable()) {
                                // das hier muss sein, falls im Baum ein Element
                                // keinen Container im aktuellen Doc besitzt
                                ElementContainer ec = (ElementContainer) treeNode.getUserObject();
                                ModelElement me = ec.getElement();
                                ElementContainer docEc = me.getContainer(doc);
                                if (docEc != null) {
                                    // highlight ist eine Container-Eigenschaft
                                    panel.addHighlight(docEc);
                                    docEc.setHighLight(true);
                                }
                                // selected ist eine Container-Eigenschaft
                                doc.addToSelection(ec, pid);
                            } else {
                                panel.increaseCorrectingSelectionCount();
                                tree.removeSelectionPath(paths[i]);
                                panel.decreaseCorrectingSelectionCount();
                            }
                        }
                    }
                }
                doc.distributeEvent(SELECTION_CHANGED, pid);
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die beim Eintreten eines DragNDrop-Ereignisses, die konkrete Drop-Location im targetTree
     * ermittelt. Dabei wird das Element, was sich dem Mouse-Zeiger am nächsten befindet, als SelectionPath des targetTrees gesetzt. Damit ist es
     * möglich Elemente an eine bestimmte Position innerhalb eines Trees zu verschieben. Methode wird hier automatisch in
     * <code>getAddElementAction(JTree srcTree, JTree targetTree, ElementDialogPanel edp, boolean switchTree)</code> und in
     * <code>getRemoveElementAction(JTree srcTree, JTree targetTree, ElementDialogPanel edp, boolean switchTree)</code> aufgerufen --> Panel muss sich
     * also darum nicht kümmern
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
