/*
 * Created on 08.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.panel;

import javax.swing.JTree;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.log.Log;

/**
 * @author fstephan Diese Klasse ist eine abstrakte Oberklasse, für alle
 *         <code>ElementDialogPanel</code>s, die DragNDrop-Funktionalität bereitstellen wollen. Es
 *         werden Methoden zur Behandlung aller für DragNDrop relevanten <code>MouseEvent</code>s
 *         und Methoden zur Sammlung aller gewünschten DragNDrop-Aktionen bereitgestellt. Achtung!
 *         <code>init()</code> muss von allen erbenden Klassen aufgerufen werden.
 */
public abstract class LGMDragNDropPanel extends ElementDialogPanel {

    /**
     * Konstruktor Ruft den Super-Konstruktor auf. Setzt Klassen-Attribute auf default-Werte
     *
     * @param dialog
     */
    public LGMDragNDropPanel(final ElementPropertyDialog dialog) {
        super(dialog);
    }

    /**
     * @param dialog Dialog, der dieses Panel enthält
     * @param name
     */
    public LGMDragNDropPanel(final ElementPropertyDialog dialog, final String name) {
        super(dialog, name);
    }

    /**
     * Methode muss von allen erbenden Klassen aufgerufen werden! Hier wird die
     * DragNDrop-Funktionalität dieses Panels aktiviert.
     *
     * @see de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel#init()
     */
    @Override
    protected void init() {
        super.init();
        LGMAction action = LGMActionLibrary.getDragNDropInitAction(collectDragNDropActionChains());
        initDragNDropAction(action);
    }

    /**
     * Methode erstellt anhand der übergeben <code>LGMAction</code> einen MouseListener, der an alle
     * Trees, die durch <code>getAllDragNDropTrees()</code> zurückgegeben werden, angeheftet wird.
     * Damit werden nun alle Trees kontrolliert und DragNDrop kann stattfinden.
     *
     * @param action
     */
    private void initDragNDropAction(final LGMAction action) {

        LGMMouseListener ml = new LGMMouseListener(action, action, action, action, action);
        LGMDragNDropTree[] trees = getAllDragNDropTrees();

        for (int i = 0; i < trees.length; i++) {
            trees[i].addMouseListener(ml);
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
    public abstract LGMDragNDropTree[] getAllDragNDropTrees();

    /**
     * Gibt die Kante zurück, die man zwischen Elementen der übergebenen Art in diesem Panel neu
     * erzeugen kann.
     *
     * @param me1
     * @param me2
     * @return
     */
    public Class<? extends Kante> getEdgeType(final ModelElement me1, final ModelElement me2) {
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

}