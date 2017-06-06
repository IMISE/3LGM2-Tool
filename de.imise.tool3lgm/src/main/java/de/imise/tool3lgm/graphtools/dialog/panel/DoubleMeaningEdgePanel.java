package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

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

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AufObjVerbindung;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Mit diesem Panel zeigen
 * <ul>
 * <li>Aufgaben ihre Objekttypen</li>
 * <li>Objekttypen ihre Aufgaben</li>
 * </ul>
 * <br>
 * an. Hinzufügen von Assoziationen zw. den Elementen und Neuanlegen von Aufgaben bzw. Objekttypen
 * geht auch.
 */
public class DoubleMeaningEdgePanel extends AbstractPathOfOneEdgePanel {

    private final LGMDragNDropTree otree, utree;
    private final LGMDragNDropTree rotree, rutree;
    private final DefaultTreeModel omodel, umodel, romodel, rumodel;
    private final LGMTreeNode oroot, uroot, roroot, ruroot;
    private final JPanel buttonpanel1, buttonpanel2;
    private final JLabel oben2, unten2;
    private final JScrollPane sp3, sp4;

    private LGMAction addUeberAction;
    private LGMAction removeUeberAction;
    private LGMAction addUnterAction;
    private LGMAction removeUnterAction;

    public DoubleMeaningEdgePanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        super(dialog, searchElementClass, edgeClass);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel oben = new JLabel(getEdgeDisplayName(Doppelkante.BACKWARD));

        oroot = new LGMTreeNode("invisible root", false);
        omodel = new DefaultTreeModel(oroot);
        otree = new LGMDragNDropTree(omodel, mainDoc);
        otree.setRootVisible(false);
        otree.setShowsRootHandles(true);
        otree.setCellRenderer(treeRenderer);
        otree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane sp1 = new JScrollPane(otree);

        JLabel unten = new JLabel(getEdgeDisplayName(Doppelkante.FORWARD));

        uroot = new LGMTreeNode("invisible root", false);
        umodel = new DefaultTreeModel(uroot);
        utree = new LGMDragNDropTree(umodel, mainDoc);
        utree.setRootVisible(false);
        utree.setShowsRootHandles(true);
        utree.setCellRenderer(treeRenderer);
        utree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        JScrollPane sp2 = new JScrollPane(utree);

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
        add(this, sp1, constraints, 0, 1, 1, 1);
        add(this, sp2, constraints, 0, 3, 1, 1);

        roroot = new LGMTreeNode(getResString("frei1"), false);
        romodel = new DefaultTreeModel(roroot);
        rotree = new LGMDragNDropTree(romodel, mainDoc);
        rotree.setRootVisible(false);
        rotree.setShowsRootHandles(true);
        rotree.setCellRenderer(treeRenderer);
        rotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        sp3 = new JScrollPane(rotree);

        oben2 = new JLabel(getResString("frei"));
        unten2 = new JLabel(getResString("frei"));
        ruroot = new LGMTreeNode(getResString("frei2"), false);
        rumodel = new DefaultTreeModel(ruroot);
        rutree = new LGMDragNDropTree(rumodel, mainDoc);
        rutree.setRootVisible(false);
        rutree.setShowsRootHandles(true);
        rutree.setCellRenderer(treeRenderer);
        rutree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        sp4 = new JScrollPane(rutree);

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
        JButton addUeberButton = new JButton();
        JButton removeUeberButton = new JButton();
        JButton addUnterButton = new JButton();
        JButton removeUnterButton = new JButton();

        try {
            addUeberAction = LGMActionLibrary.getAddElementAction(rotree, otree, this, true);
            removeUeberAction = LGMActionLibrary.getDisconnectAction(otree, rotree, this, true);
            addUnterAction = LGMActionLibrary.getAddElementAction(rutree, utree, this, false);
            removeUnterAction = LGMActionLibrary.getDisconnectAction(utree, rutree, this, false);
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

        buttonpanel1 = new JPanel();
        buttonpanel1.setSize(30, 250);
        buttonpanel1.setLayout(new GridLayout(3, 1));

        buttonpanel2 = new JPanel();
        buttonpanel2.setSize(30, 250);
        buttonpanel2.setLayout(new GridLayout(3, 1));

        buttonpanel1.add(addUeberButton);
        buttonpanel1.add(removeUeberButton);
        buttonpanel2.add(addUnterButton);
        buttonpanel2.add(removeUnterButton);

        init();
    }

    protected String getEdgeDisplayName(final int connectionState) {
        return edgeIsForward ? ModelConstants.getForwardMetaAssociationName(edgeClass, connectionState, false, false) : ModelConstants.getBackwardMetaAssociationName(edgeClass, connectionState, false, false);
    }

    ArrayList<ElementContainer> childrenToExcludeFromRotree = new ArrayList<ElementContainer>(500);
    ArrayList<ElementContainer> childrenToExcludeFromRutree = new ArrayList<ElementContainer>(500);

    @Override
    protected void init() {

        super.init();

        remove(buttonpanel1);
        remove(buttonpanel2);
        remove(oben2);
        remove(unten2);
        remove(sp3);
        remove(sp4);

        otree.saveExpansion();
        otree.saveSelection();
        utree.saveExpansion();
        utree.saveSelection();

        oroot.removeAllChildren();
        uroot.removeAllChildren();
        otree.reset();
        utree.reset();
        childrenToExcludeFromRotree.clear();
        childrenToExcludeFromRutree.clear();

        ModelElement modelElement = getModelElement();
        for (ElementContainer ec : modelElement.getConnectedContainer(searchElementClass, mainDoc, AufObjVerbindung.class, Doppelkante.BACKWARD)) {
            otree.addObject(ec, oroot, null, true, false, false);
            childrenToExcludeFromRotree.add(ec);
        }
        if (UserProperties.isSearchParts()) {
            for (ElementContainer ec : modelElement.getPartConnectedContainer(searchElementClass, mainDoc, AufObjVerbindung.class, Doppelkante.BACKWARD)) {
                LGMTreeNode node = otree.addObject(ec, oroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRotree.add(ec);
            }
        }
        if (UserProperties.isSearchParents()) {
            for (ElementContainer ec : modelElement.getParentConnectedContainer(searchElementClass, mainDoc, AufObjVerbindung.class, Doppelkante.BACKWARD)) {
                LGMTreeNode node = otree.addObject(ec, oroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRotree.add(ec);
            }
        }

        for (ElementContainer ec : modelElement.getConnectedContainer(searchElementClass, mainDoc, AufObjVerbindung.class, Doppelkante.FORWARD)) {
            utree.addObject(ec, uroot, null, true, false, false);
            childrenToExcludeFromRutree.add(ec);
        }
        if (UserProperties.isSearchParts()) {
            for (ElementContainer ec : modelElement.getPartConnectedContainer(searchElementClass, mainDoc, AufObjVerbindung.class, Doppelkante.FORWARD)) {
                LGMTreeNode node = utree.addObject(ec, uroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRutree.add(ec);
            }
        }
        if (UserProperties.isSearchParents()) {
            for (ElementContainer ec : modelElement.getParentConnectedContainer(searchElementClass, mainDoc, AufObjVerbindung.class, Doppelkante.FORWARD)) {
                LGMTreeNode node = utree.addObject(ec, uroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRutree.add(ec);
            }
        }
        omodel.reload();
        otree.restoreExpansion();
        umodel.reload();
        utree.restoreExpansion();

        repaint();
        revalidate();

    }

    @Override
    protected void showFullDialog() {
        super.showFullDialog();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel1, constraints, 1, 1, 1, 2);
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel2, constraints, 1, 3, 1, 2);

        constraints.anchor = GridBagConstraints.WEST;
        add(this, oben2, constraints, 2, 0, 1, 1);
        add(this, unten2, constraints, 2, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp3, constraints, 2, 1, 1, 1);
        add(this, sp4, constraints, 2, 3, 1, 1);

        rotree.saveExpansion();
        rutree.saveExpansion();
        rotree.saveSelection();
        rutree.saveSelection();

        roroot.removeAllChildren();
        ruroot.removeAllChildren();
        rotree.reset();
        rutree.reset();

        for (ElementContainer ec : mainDoc.getElementContainer(searchElementClass)) {
            rotree.addObject(ec, roroot, childrenToExcludeFromRotree, false, true);
            rutree.addObject(ec, ruroot, childrenToExcludeFromRutree, false, true);
        }
        romodel.reload();
        // expandTree(rotree);
        rumodel.reload();
        // expandTree(rutree);
        rotree.restoreExpansion();
        rutree.restoreExpansion();

        revalidate();
        repaint();
    }

    @Override
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(rotree, otree, addUeberAction);
        DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(otree, rotree, removeUeberAction);
        DragNDropActionChain tac3 = DragNDropInitializer.createNewDragNDropActionChain(rutree, utree, addUnterAction);
        DragNDropActionChain tac4 = DragNDropInitializer.createNewDragNDropActionChain(utree, rutree, removeUnterAction);

        return new DragNDropActionChain[] {
                tac1,
                tac2,
                tac3,
                tac4
        };
    }

    @Override
    public LGMDragNDropTree[] getAllDragNDropTrees() {
        return new LGMDragNDropTree[] {
                otree,
                rotree,
                utree,
                rutree
        };
    }
}
