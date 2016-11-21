/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;

/**
 * @author fstephan
 */
public class StructurePanel extends LGMDragNDropPanel {

    /**
	 * 
	 */
    private final LGMDragNDropTree lotree, lutree;

    /**
	 * 
	 */
    private final LGMDragNDropTree rtree;

    /**
	 * 
	 */
    private final DefaultTreeModel lomodel, lumodel, rmodel;

    /**
	 * 
	 */
    private final LGMTreeNode loroot, luroot, rroot;

    /**
	 * 
	 */
    private final JPanel control1, control2;

    /**
	 * 
	 */
    private final JLabel label2;

    /**
	 * 
	 */
    private final JScrollPane sp2;

    /**
     * COMMENTME
     */
    private final ArrayList<ElementContainer> childrenToExcludeFromRtree = new ArrayList<ElementContainer>(100);

    private LGMAction addUeberAction;
    private LGMAction removeUeberAction;
    private LGMAction addUnterAction;
    private LGMAction removeUnterAction;

    /**
     * @param pd
     */
    public StructurePanel(final ElementPropertyDialog pd) {
        super(pd);

        // lotree
        JLabel oben = new JLabel(Tool3lgmConstants.getResString("ueberg"));
        loroot = new LGMTreeNode(Tool3lgmConstants.getResString("ueberg"), false);
        lomodel = new DefaultTreeModel(loroot);
        lotree = new LGMDragNDropTree(lomodel, mainDoc);
        lotree.setName("lotree");
        lotree.setRootVisible(false);
        lotree.setShowsRootHandles(true);
        lotree.setCellRenderer(treeRenderer);
        lotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane spueber = new JScrollPane(lotree);

        // lutree
        JLabel unten = new JLabel(Tool3lgmConstants.getResString("unterg"));
        luroot = new LGMTreeNode(Tool3lgmConstants.getResString("unterg"), false);
        lumodel = new DefaultTreeModel(luroot);
        lutree = new LGMDragNDropTree(lumodel, mainDoc);
        lutree.setName("lutree");
        lutree.setRootVisible(false);
        lutree.setCellRenderer(treeRenderer);
        JScrollPane spunter = new JScrollPane(lutree);

        // PanelLayout
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.ipadx = -30;
        constraints.ipady = -10;
        add(this, viewButton, constraints, 0, 6, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, oben, constraints, 0, 0, 1, 1);
        add(this, unten, constraints, 0, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, spueber, constraints, 0, 1, 1, 1);
        add(this, spunter, constraints, 0, 3, 1, 1);

        // rtree
        label2 = new JLabel(Tool3lgmConstants.getResString("frei"));
        rroot = new LGMTreeNode(Tool3lgmConstants.getResString("frei"), false);
        rmodel = new DefaultTreeModel(rroot);
        rtree = new LGMDragNDropTree(rmodel, mainDoc);
        rtree.setName("rtree");
        rtree.setRootVisible(false);
        rtree.setShowsRootHandles(true);
        rtree.setCellRenderer(treeRenderer);
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        sp2 = new JScrollPane(rtree);

        /*
         * Start: MouseListener erstellen und an Trees anhängen ...
         */
        LGMAction lotreeMouseAction = LGMActionLibrary.getMouseAction(lotree, this);
        LGMAction lutreeMouseAction = LGMActionLibrary.getMouseAction(lutree, this);
        LGMAction rtreeMouseAction = LGMActionLibrary.getMouseAction(rtree, this);

        lotree.addMouseListener(new LGMMouseListener(null, null, null, lotreeMouseAction, null));
        lutree.addMouseListener(new LGMMouseListener(null, null, null, lutreeMouseAction, null));
        rtree.addMouseListener(new LGMMouseListener(null, null, null, rtreeMouseAction, null));
        /*
         * ... End: MouseListener erstellen und an Trees anhängen
         */

        /*
         * Start: TreeSelectionListener erstellen und an Trees anhängen ...
         */
        LGMAction lotreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(lotree, this);
        LGMAction lutreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(lutree, this);
        LGMAction rtreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rtree, this);

        lotree.addTreeSelectionListener(new LGMTreeSelectionListener(lotreeSelectionAction));
        lutree.addTreeSelectionListener(new LGMTreeSelectionListener(lutreeSelectionAction));
        rtree.addTreeSelectionListener(new LGMTreeSelectionListener(rtreeSelectionAction));
        /*
         * ... End: TreeSelectionListener erstellen und an Trees anhängen
         */

        /*
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        JButton addUeberButton = new JButton();
        JButton removeUeberButton = new JButton();
        JButton addUnterButton = new JButton();
        JButton removeUnterButton = new JButton();

        try {
            addUeberAction = LGMActionLibrary.getAddElementAction(rtree, lotree, this, false);
            removeUeberAction = LGMActionLibrary.getDisconnectAction(lotree, rtree, this, false);
            addUnterAction = LGMActionLibrary.getAddElementAction(rtree, lutree, this, true);
            removeUnterAction = LGMActionLibrary.getDisconnectAction(lutree, rtree, this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addUeberButton.setAction(addUeberAction);
        removeUeberButton.setAction(removeUeberAction);
        addUnterButton.setAction(addUnterAction);
        removeUnterButton.setAction(removeUnterAction);
        /*
         * ... end: Buttons & Actions erstellen und registrieren
         */

        // ButtonPanels erstellen
        control1 = new JPanel();
        control2 = new JPanel();
        control1.setLayout(new GridLayout(2, 1));
        control2.setLayout(new GridLayout(2, 1));

        // Buttons dem Panel hinzufügen
        control1.add(addUeberButton);
        control1.add(removeUeberButton);
        control2.add(addUnterButton);
        control2.add(removeUnterButton);

        init();
    }

    @Override
    protected void init() {

        super.init();

        remove(control1);
        remove(control2);
        remove(label2);
        remove(sp2);

        childrenToExcludeFromRtree.clear();
        loroot.removeAllChildren();
        lotree.reset();
        ModelElement modelElement = getModelElement();
        for (ElementContainer ec : modelElement.getDirectParentContainer(mainDoc)) {
            childrenToExcludeFromRtree.add(ec);
            lotree.addObject(ec, loroot, null, true, false, false);
        }
        lomodel.reload();
        // expandTree(lotree);

        luroot.removeAllChildren();
        lutree.reset();
        for (ElementContainer ec : modelElement.getDirectPartContainer(mainDoc)) {
            childrenToExcludeFromRtree.add(ec);
            lutree.addObject(ec, luroot, null, true, false, false);
        }
        lumodel.reload();
        // expandTree(lutree);

        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {

        super.showFullDialog();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        constraints.weightx = 0;
        constraints.weighty = 0;
        add(this, control1, constraints, 1, 1, 1, 1);
        add(this, control2, constraints, 1, 3, 1, 1);
        constraints.anchor = GridBagConstraints.WEST;
        add(this, label2, constraints, 2, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp2, constraints, 2, 1, 1, 3);

        rroot.removeAllChildren();
        rtree.reset();
        ModelElement modelElement = getModelElement();
        ArrayList<ElementContainer> all = mainDoc.getElementContainer(modelElement.getClass());
        all.remove(modelElement.getContainer(doc));
        for (ElementContainer ec : all) {
            rtree.addObject(ec, rroot, childrenToExcludeFromRtree, false, false, true);
        }
        rmodel.reload();
        // expandTree(rtree);

        revalidate();
        repaint();

    }

    /**
     * Hier werden alle möglichen DragNDrop-Aktionen zwischen den Trees des Panels zumsammengefasst
     * und als Attribut in der Oberklasse abgespeichert.
     */
    @Override
    protected DragNDropInitializer.DragNDropActionChain[] collectDragNDropActionChains() {
        /*
         * alle Aktionen zwischen rtree <-> lotree und rtree <-> lutree
         */
        DragNDropInitializer.DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, lotree, addUeberAction);
        DragNDropInitializer.DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(rtree, lutree, addUnterAction);
        DragNDropInitializer.DragNDropActionChain tac3 = DragNDropInitializer.createNewDragNDropActionChain(lotree, rtree, removeUeberAction);
        DragNDropInitializer.DragNDropActionChain tac4 = DragNDropInitializer.createNewDragNDropActionChain(lutree, rtree, removeUnterAction);

        /*
         * alle Aktionen zwischen lotree <-> lutree
         */
        DragNDropInitializer.DragNDropActionChain tac5 = DragNDropInitializer.createNewDragNDropActionChain(new LGMDragNDropTree[] {
                lotree, rtree, lutree
        }, new LGMAction[] {
                removeUeberAction, addUnterAction
        });
        DragNDropInitializer.DragNDropActionChain tac6 = DragNDropInitializer.createNewDragNDropActionChain(new LGMDragNDropTree[] {
                lutree, rtree, lotree
        }, new LGMAction[] {
                removeUnterAction, addUeberAction
        });

        DragNDropInitializer.DragNDropActionChain[] allDndActionChains = new DragNDropInitializer.DragNDropActionChain[] {
                tac1, tac2, tac3, tac4, tac5, tac6
        };

        return allDndActionChains;

    }

    @Override
    public LGMDragNDropTree[] getAllDragNDropTrees() {
        return new LGMDragNDropTree[] {
                rtree, lotree, lutree
        };
    }

}
