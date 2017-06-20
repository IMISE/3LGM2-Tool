package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.elements.Doppelkante.BACKWARD;
import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.StringUtils;

/**
 * Mit diesem Panel zeigen
 * <ul>
 * <li>Aufgaben ihre Objekttypen</li>
 * <li>Objekttypen ihre Aufgaben</li>
 * </ul>
 * <br>
 * an. Hinzufügen von Assoziationen zw. den Elementen und Neuanlegen von Aufgaben bzw. Objekttypen
 * geht auch.
 * Das hier könnte man auch aus 2 {@link PathConnectionPanel} zusammen bauen
 */
public class DoubleMeaningEdgePanel extends AbstractPathOfOneEdgePanel {

    private final LGMTree lotree, lutree;
    private final LGMTree rotree, rutree;
    private final DefaultTreeModel lomodel, lumodel, romodel, rumodel;
    private final LGMTreeNode loroot, luroot, roroot, ruroot;
    private final JPanel buttonpanel1, buttonpanel2;
    private final JLabel rolabel, rulabel;
    private final JScrollPane sp3, sp4;

    private final LGMAction loaddAction;
    private final LGMAction loremoveAction;
    private final LGMAction luaddAction;
    private final LGMAction luremoveAction;

    public DoubleMeaningEdgePanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        super(dialog, searchElementClass, edgeClass);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        String lolabeltext = StringUtils.capitalizeFirstChar(getEdgeDisplayName(BACKWARD));
        JLabel lolabel = new JLabel(lolabeltext);

        //hier niemals das this löschen, weil die globale searchElementClass im super-Konsturktor richtig gesetzt wird
        boolean showRootHandles = ModelConstants.canHaveParts(this.searchElementClass);

        loroot = new LGMTreeNode("loroot", false);
        lomodel = new DefaultTreeModel(loroot);
        lotree = new LGMTree(lomodel, mainDoc);
        lotree.setRootVisible(false);
        lotree.setShowsRootHandles(showRootHandles);
        lotree.setCellRenderer(treeRenderer);
        lotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane sp1 = new JScrollPane(lotree);

        String lulabeltext = StringUtils.capitalizeFirstChar(getEdgeDisplayName(FORWARD));
        JLabel lulabel = new JLabel(lulabeltext);

        luroot = new LGMTreeNode("luroot", false);
        lumodel = new DefaultTreeModel(luroot);
        lutree = new LGMTree(lumodel, mainDoc);
        lutree.setRootVisible(false);
        lutree.setShowsRootHandles(showRootHandles);
        lutree.setCellRenderer(treeRenderer);
        lutree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        JScrollPane sp2 = new JScrollPane(lutree);

        constraints.anchor = GridBagConstraints.EAST;
        //das hier braucht man wahrscheinlich nur unter Windows. Auf dem Mac sieht das komisch aus
        //            constraints.ipadx = -30;
        //            constraints.ipady = -10;
        add(this, viewButton, constraints, 0, 6, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, lolabel, constraints, 0, 0, 1, 1);
        add(this, lulabel, constraints, 0, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp1, constraints, 0, 1, 1, 1);
        add(this, sp2, constraints, 0, 3, 1, 1);

        roroot = new LGMTreeNode("roroot", false);
        romodel = new DefaultTreeModel(roroot);
        rotree = new LGMTree(romodel, mainDoc);
        rotree.setRootVisible(false);
        rotree.setShowsRootHandles(showRootHandles);
        rotree.setCellRenderer(treeRenderer);
        rotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        sp3 = new JScrollPane(rotree);

        String unconnected = getResString("frei");
        rolabel = new JLabel(unconnected);
        rulabel = new JLabel(unconnected);
        ruroot = new LGMTreeNode("ruroot", false);
        rumodel = new DefaultTreeModel(ruroot);
        rutree = new LGMTree(rumodel, mainDoc);
        rutree.setRootVisible(false);
        rutree.setShowsRootHandles(showRootHandles);
        rutree.setCellRenderer(treeRenderer);
        rutree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        sp4 = new JScrollPane(rutree);

        /*
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        JButton addUeberButton = new JButton();
        JButton removeUeberButton = new JButton();
        JButton addUnterButton = new JButton();
        JButton removeUnterButton = new JButton();

        loaddAction = getConnectAction(rotree, lotree, false);
        loremoveAction = getDisconnectAction(lotree, rotree, false);
        luaddAction = getConnectAction(rutree, lutree, true);
        luremoveAction = getDisconnectAction(lutree, rutree, true);

        addUeberButton.setAction(loaddAction);
        removeUeberButton.setAction(loremoveAction);
        addUnterButton.setAction(luaddAction);
        removeUnterButton.setAction(luremoveAction);

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

        initTreeListenerAndDragNDrop();

        showFullDialog(true);
    }

    protected String getEdgeDisplayName(final int connectionState) {
        return edgeIsForward ? ModelConstants.getForwardMetaAssociationName(edgeClass, connectionState, false, false) : ModelConstants.getBackwardMetaAssociationName(edgeClass, connectionState, false, false);
    }

    ArrayList<ElementContainer> childrenToExcludeFromRotree = Lists.newArrayList();
    ArrayList<ElementContainer> childrenToExcludeFromRutree = Lists.newArrayList();

    @Override
    public void update() {

        lotree.saveExpansion();
        lotree.saveSelection();
        lutree.saveExpansion();
        lutree.saveSelection();

        loroot.removeAllChildren();
        luroot.removeAllChildren();
        lotree.reset();
        lutree.reset();
        childrenToExcludeFromRotree.clear();
        childrenToExcludeFromRutree.clear();

        ModelElement modelElement = getModelElement();
        for (ElementContainer ec : modelElement.getConnectedContainer(searchElementClass, mainDoc, edgeClass, BACKWARD)) {
            lotree.addObject(ec, loroot, null, true, false, false);
            childrenToExcludeFromRotree.add(ec);
        }
        if (UserProperties.isSearchParts()) {
            for (ElementContainer ec : modelElement.getPartConnectedContainer(searchElementClass, mainDoc, edgeClass, BACKWARD)) {
                LGMTreeNode node = lotree.addObject(ec, loroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRotree.add(ec);
            }
        }
        if (UserProperties.isSearchParents()) {
            for (ElementContainer ec : modelElement.getParentConnectedContainer(searchElementClass, mainDoc, edgeClass, BACKWARD)) {
                LGMTreeNode node = lotree.addObject(ec, loroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRotree.add(ec);
            }
        }

        for (ElementContainer ec : modelElement.getConnectedContainer(searchElementClass, mainDoc, edgeClass, FORWARD)) {
            lutree.addObject(ec, luroot, null, true, false, false);
            childrenToExcludeFromRutree.add(ec);
        }
        if (UserProperties.isSearchParts()) {
            for (ElementContainer ec : modelElement.getPartConnectedContainer(searchElementClass, mainDoc, edgeClass, FORWARD)) {
                LGMTreeNode node = lutree.addObject(ec, luroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRutree.add(ec);
            }
        }
        if (UserProperties.isSearchParents()) {
            for (ElementContainer ec : modelElement.getParentConnectedContainer(searchElementClass, mainDoc, edgeClass, FORWARD)) {
                LGMTreeNode node = lutree.addObject(ec, luroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRutree.add(ec);
            }
        }
        lomodel.reload();
        lotree.restoreExpansion();
        lumodel.reload();
        lutree.restoreExpansion();

        repaint();
        revalidate();

    }

    @Override
    protected void showFullDialog() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel1, constraints, 1, 1, 1, 2);
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel2, constraints, 1, 3, 1, 2);

        constraints.anchor = GridBagConstraints.WEST;
        add(this, rolabel, constraints, 2, 0, 1, 1);
        add(this, rulabel, constraints, 2, 2, 1, 1);
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
    protected void showPartlyDialog() {
        remove(buttonpanel1);
        remove(buttonpanel2);
        remove(rolabel);
        remove(rulabel);
        remove(sp3);
        remove(sp4);
    }

    @Override
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(rotree, lotree, loaddAction);
        DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(lotree, rotree, loremoveAction);
        DragNDropActionChain tac3 = DragNDropInitializer.createNewDragNDropActionChain(rutree, lutree, luaddAction);
        DragNDropActionChain tac4 = DragNDropInitializer.createNewDragNDropActionChain(lutree, rutree, luremoveAction);

        return new DragNDropActionChain[] {
                tac1,
                tac2,
                tac3,
                tac4
        };
    }

    @Override
    public JTree[] getAllDragNDropTrees() {
        return new JTree[] {
                lotree,
                rotree,
                lutree,
                rutree
        };
    }
}
