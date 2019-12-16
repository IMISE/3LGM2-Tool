/*
 * Created on 08.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTree;

import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.log.Log;

/**
 * @author fstephan Diese Klasse ist eine abstrakte Oberklasse, für alle
 *         <code>ElementDialogPanel</code>s, die DragNDrop-Funktionalität bereitstellen wollen. Es
 *         werden Methoden zur Behandlung aller für DragNDrop relevanten <code>MouseEvent</code>s
 *         und Methoden zur Sammlung aller gewünschten DragNDrop-Aktionen bereitgestellt. Achtung!
 *         <code>init()</code> muss von allen erbenden Klassen aufgerufen werden.
 */
public abstract class LGMDragNDropPanel extends AbstractPathConnectionTreePanel {

    /**
     * Konstruktor Ruft den Super-Konstruktor auf. Setzt Klassen-Attribute auf default-Werte
     *
     * @param dialog
     */
    public LGMDragNDropPanel(final AbstractElementPropertyDialog dialog, final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog, labelLastEdgeName, simpleMetaPath);
    }

    /**
     * Methode erstellt anhand der übergeben <code>LGMAction</code> einen MouseListener, der an alle
     * Trees, die durch <code>getAllDragNDropTrees()</code> zurückgegeben werden, angeheftet wird.
     * Damit werden nun alle Trees kontrolliert und DragNDrop kann stattfinden.
     *
     * @param action
     */
    protected final void initTreeListenerAndDragNDrop() {
        LGMAction action = getDragNDropInitAction(collectDragNDropActionChains());
        LGMMouseListener ml = new LGMMouseListener(action, action, action, action, action);
        JTree[] trees = getAllDragNDropTrees();
        for (int i = 0; i < trees.length; i++) {
            //die Bäume können null sein, da sie nur bei Bedarf initialisiert werden (insbesondere der rechte Baum im PathConnectionPanel)
            if (trees[i] != null) {
                trees[i].addMouseListener(ml);
                addListener(trees[i]);
            }
        }
    }

    /**
     * Diese Methode soll so überschrieben werden, dass alle möglichen DragNDrop-Aktionen die im
     * jeweils abgeleiteten Panel zur Verfügung stehen sollen, als <code>DragNDropActionChain</code>
     * s in einem Array zusammengefasst und returniert werden.
     */
    protected abstract DragNDropActionChain[] collectDragNDropActionChains();

    /**
     * Methode soll so überschrieben werden, dass alle Trees, die an DragNDrop-Aktionen beteiligt
     * sind, in einem Array zusammengefasst und returniert werden.
     */
    public abstract JTree[] getAllDragNDropTrees();

    /**
     * Gibt die Edge zurück, die man zwischen Elementen der übergebenen Art in diesem Panel neu
     * erzeugen kann.
     *
     * @param me1
     * @param me2
     * @return
     */
    public final Class<? extends Edge> getEdgeType(final ModelElement me1, final ModelElement me2) {
        return null;
    }

    /**
     * Gibt das <code>ModelElement</code> des <code>ElementPropertyDialog</code> s wieder, in dem
     * sich der <code>tree</code> befindet.
     *
     * @param tree
     */
    protected final ModelElement getTopLevelModelElement(final JTree tree) {
        ModelElement me = null;
        try {
            ElementPropertyDialog d = (ElementPropertyDialog) tree.getTopLevelAncestor();
            me = d.getModelElement();
        } catch (Exception ex) {
            Log.log(Log.ERROR, getClass().getSimpleName() + ": could'nt find TopLevelAncestor for tree", ex);
        }
        return me;
    }

    /**
     * Methode liefert eine neue LGMAction zurück. Diese LGMAction verwaltet das Initialisieren von
     * DragNDrop in einem Panel. Alle Panels, die DragNDrop-Funktionalität bieten wollen, müssen
     * diese Action über einen MouseListener an ihre Trees anfügen. Dabei sollte diese Action sowohl
     * bei mousePressed als auch bei mouseEntered aufgerufen werden.
     *
     * @param dndActionChains Sammlung aller <code>DragNDropActionChain</code>s, die bei einem DragNDrop-Ereignis
     *            ausgeführt werden können
     */
    private static final LGMAction getDragNDropInitAction(final DragNDropInitializer.DragNDropActionChain[] dndActionChains) {

        return new LGMAction() {

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
                    int id = me.getID();
                    if (id == MouseEvent.MOUSE_ENTERED) {
                        mouseEntered(me);
                    } else if (id == MouseEvent.MOUSE_PRESSED) {
                        mousePressed(me);
                    } else if (id == MouseEvent.MOUSE_DRAGGED) {
                        mousePressed(me);
                    }
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

}