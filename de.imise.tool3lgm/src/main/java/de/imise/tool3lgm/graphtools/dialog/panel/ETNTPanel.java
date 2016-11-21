package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

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
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

public class ETNTPanel extends LGMDragNDropPanel {

    private final LGMDragNDropTree otree, rotree, utree, rutree;
    private final DefaultTreeModel lmodel1, rmodel1, lmodel2, rmodel2;
    private final LGMTreeNode lroot1, rroot1, lroot2, rroot2;
    private final JScrollPane sp1, sp2;
    private final JPanel buttonpanel;
    private final JLabel label;
    private final Class<? extends ModelElement> searchElementClass;

    private LGMAction addAction;
    private LGMAction removeAction;

    public ETNTPanel(final Class<? extends ModelElement> searchElementClass, final ElementPropertyDialog dl) {
        super(dl);
        this.searchElementClass = searchElementClass;

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label1 = new JLabel(Tool3lgmConstants.getResString("etntges"));
        lroot1 = new LGMTreeNode(Tool3lgmConstants.getResString("verb"), false);
        lmodel1 = new DefaultTreeModel(lroot1);
        otree = new LGMDragNDropTree(lmodel1);
        otree.setRootVisible(false);
        otree.setCellRenderer(treeRenderer);

        otree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        JScrollPane sp_1 = new JScrollPane(otree);

        JLabel label2 = new JLabel(Tool3lgmConstants.getResString("etntempf"));
        lroot2 = new LGMTreeNode(Tool3lgmConstants.getResString("verb"), false);
        lmodel2 = new DefaultTreeModel(lroot2);
        utree = new LGMDragNDropTree(lmodel2);
        utree.setRootVisible(false);
        utree.setCellRenderer(treeRenderer);
        utree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane sp_2 = new JScrollPane(utree);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.ipadx = -30;
        constraints.ipady = -10;
        add(this, viewButton, constraints, 0, 4, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, label1, constraints, 0, 0, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, label2, constraints, 0, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 50;
        add(this, sp_1, constraints, 0, 1, 1, 1);
        add(this, sp_2, constraints, 0, 3, 1, 1);

        label = new JLabel("");
        rroot1 = new LGMTreeNode(Tool3lgmConstants.getResString("frei"), false);
        rmodel1 = new DefaultTreeModel(rroot1);
        rotree = new LGMDragNDropTree(rmodel1);
        rotree.setRootVisible(false);
        rotree.setCellRenderer(treeRenderer);
        rotree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        sp1 = new JScrollPane(rotree);

        rroot2 = new LGMTreeNode(Tool3lgmConstants.getResString("frei"), false);
        rmodel2 = new DefaultTreeModel(rroot2);
        rutree = new LGMDragNDropTree(rmodel2);
        rutree.setRootVisible(false);
        rutree.setCellRenderer(treeRenderer);
        rutree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        sp2 = new JScrollPane(rutree);

        /*
         * Start: MouseListener erstellen und an Trees anhängen ...
         */
        LGMAction otreeMouseAction = LGMActionLibrary.getMouseAction(otree, this);
        LGMAction utreeMouseAction = LGMActionLibrary.getMouseAction(utree, this);
        LGMAction rotreeMouseAction = LGMActionLibrary.getMouseAction(rotree, this);
        LGMAction rutreeMouseAction = LGMActionLibrary.getMouseAction(rutree, this);

        otree.addMouseListener(new LGMMouseListener(null, null, null, otreeMouseAction, null));
        utree.addMouseListener(new LGMMouseListener(null, null, null, utreeMouseAction, null));
        rotree.addMouseListener(new LGMMouseListener(null, null, null, rotreeMouseAction, null));
        rutree.addMouseListener(new LGMMouseListener(null, null, null, rutreeMouseAction, null));
        /*
         * ... End: MouseListener erstellen und an Trees anhängen
         */

        /*
         * Start: TreeSelectionListener erstellen und an Trees anhängen ...
         */
        LGMAction otreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(otree, this);
        LGMAction utreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(utree, this);
        LGMAction rotreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rotree, this);
        LGMAction rutreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rutree, this);

        otree.addTreeSelectionListener(new LGMTreeSelectionListener(otreeSelectionAction));
        utree.addTreeSelectionListener(new LGMTreeSelectionListener(utreeSelectionAction));
        rotree.addTreeSelectionListener(new LGMTreeSelectionListener(rotreeSelectionAction));
        rutree.addTreeSelectionListener(new LGMTreeSelectionListener(rutreeSelectionAction));
        /*
         * ... End: TreeSelectionListener erstellen und an Trees anhängen
         */

        /*
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        JButton addButton = new JButton();
        JButton removeButton = new JButton();

        try {
            final LGMAction addAction1 = LGMActionLibrary.getAddElementAction(rotree, otree, this, true);
            final LGMAction addAction2 = LGMActionLibrary.getAddElementAction(rutree, utree, this, false);
            addAction = new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {
                @Override
                public void execute(final EventObject eo) {
                    addAction1.execute(eo);
                    addAction2.execute(eo);
                }
            };

            final LGMAction removeAction1 = LGMActionLibrary.getDisconnectAction(otree, rotree, this, true);
            final LGMAction removeAction2 = LGMActionLibrary.getDisconnectAction(utree, rutree, this, false);

            removeAction = new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
                @Override
                public void execute(final EventObject eo) {
                    removeAction1.execute(eo);
                    removeAction2.execute(eo);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        addButton.setAction(addAction);
        removeButton.setAction(removeAction);
        /*
         * ... end: Buttons & Actions erstellen und registrieren
         */

        buttonpanel = new JPanel();
        buttonpanel.setSize(30, 250);
        buttonpanel.setLayout(new GridLayout(2, 1));
        buttonpanel.add(addButton);
        buttonpanel.add(removeButton);

        // but = new JButton(Tool3lgmConstants.getResourceString("new"));
        // but.setActionCommand("newKnot");
        // but.addActionListener(this);
        // buttonpanel.add(but);

        init();
    }

    @Override
    protected void init() {

        super.init();

        remove(label);
        remove(buttonpanel);
        remove(sp1);
        remove(sp2);

        ModelElement modelElement = getModelElement();
        List<ElementContainer> knoten1 = modelElement.getConnectedContainer(searchElementClass, mainDoc, null, Doppelkante.BACKWARD);
        List<ElementContainer> knoten2 = modelElement.getConnectedContainer(searchElementClass, mainDoc, null, Doppelkante.FORWARD);

        lroot1.removeAllChildren();
        for (int m = 0; m < knoten1.size(); m++) {
            LGMTreeNode node = new LGMTreeNode(knoten1.get(m), false);
            lroot1.add(node);
        }
        lroot2.removeAllChildren();
        for (int m = 0; m < knoten2.size(); m++) {
            LGMTreeNode node = new LGMTreeNode(knoten2.get(m), false);
            lroot2.add(node);
        }

        if (UserProperties.isSearchParts()) {
            knoten1 = modelElement.getPartConnectedContainer(searchElementClass, mainDoc, null, Doppelkante.BACKWARD);
            knoten2 = modelElement.getPartConnectedContainer(searchElementClass, mainDoc, null, Doppelkante.FORWARD);

            for (int m = 0; m < knoten1.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(knoten1.get(m), false);
                node.setSelectable(false);
                lroot1.add(node);
            }
            for (int m = 0; m < knoten2.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(knoten2.get(m), false);
                node.setSelectable(false);
                lroot2.add(node);
            }
        }
        if (UserProperties.isSearchParents()) {
            knoten1 = modelElement.getParentConnectedContainer(searchElementClass, mainDoc, null, Doppelkante.BACKWARD);
            knoten2 = modelElement.getParentConnectedContainer(searchElementClass, mainDoc, null, Doppelkante.FORWARD);

            for (int m = 0; m < knoten1.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(knoten1.get(m), false);
                node.setSelectable(false);
                lroot1.add(node);
            }
            for (int m = 0; m < knoten2.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(knoten2.get(m), false);
                node.setSelectable(false);
                lroot2.add(node);
            }
        }

        lmodel1.reload();
        expandTree(otree);
        lmodel2.reload();
        expandTree(utree);

        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {

        super.showFullDialog();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel, constraints, 1, 1, 1, 3);
        constraints.anchor = GridBagConstraints.WEST;
        add(this, label, constraints, 2, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 50;
        add(this, sp1, constraints, 2, 1, 1, 1);
        add(this, sp2, constraints, 2, 3, 1, 1);

        List<ElementContainer> nt;

        /*
         * is searchknot abstract class ETNT_ETDT_KOMBINATION, then searchknot = { ETNT_KOMBINATION,
         * ETDT_KOMBINATION }
         */
        if (searchElementClass == EtntEtdtKombination.class) {
            nt = mainDoc.getElementContainer(EreignisNachrichtenTyp.class);
            nt.addAll(mainDoc.getElementContainer(EreignisDokumentenTyp.class));

        } else {
            nt = mainDoc.getElementContainer(searchElementClass);
        }
        List<ElementContainer> knoten1 = new ArrayList<ElementContainer>();
        List<ElementContainer> knoten2 = new ArrayList<ElementContainer>();
        ModelElement modelElement = getModelElement();
        for (ElementContainer ec : nt) {
            if (!modelElement.isConnectedFrom(ec.getElement())) {
                knoten1.add(ec);
            }
        }
        for (ElementContainer ec : nt) {
            if (!modelElement.isConnectedTo(ec.getElement())) {
                knoten2.add(ec);
            }
        }

        rroot1.removeAllChildren();
        for (int m = 0; m < knoten1.size(); m++) {
            LGMTreeNode node = new LGMTreeNode(knoten1.get(m), false);
            rroot1.add(node);
        }
        rmodel1.reload();
        expandTree(rotree);

        rroot2.removeAllChildren();
        for (int m = 0; m < knoten2.size(); m++) {
            LGMTreeNode node = new LGMTreeNode(knoten2.get(m), false);
            rroot2.add(node);
        }
        rmodel2.reload();
        expandTree(rutree);

        revalidate();
        repaint();
    }

    @Override
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(rotree, otree, addAction);
        DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(otree, rotree, removeAction);
        DragNDropActionChain tac3 = DragNDropInitializer.createNewDragNDropActionChain(rutree, utree, addAction);
        DragNDropActionChain tac4 = DragNDropInitializer.createNewDragNDropActionChain(utree, rutree, removeAction);

        return new DragNDropActionChain[] {
                tac1, tac2, tac3, tac4
        };
    }

    @Override
    public LGMDragNDropTree[] getAllDragNDropTrees() {
        return new LGMDragNDropTree[] {
                otree, rotree, utree, rutree
        };
    }

}
