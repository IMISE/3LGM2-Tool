package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Mit diesem Panel zeigen Organisationseinheiten ihre Aufgaben an. Dieses Panel ist momentan
 * überflüssig. Es wurde durch das <code>AufOrgPanel</code> ersetzt, welches auch gleich die
 * Gegenrichtung darstellen kann und so auch das <code>AufOrgPanel</code> ersetzt.
 */
public class OrgAufPanel extends ElementDialogPanel {

    private final LGMTree tree;
    private final DefaultTreeModel lmodel;
    private final JScrollPane spl;
    private final LGMTreeNode lroot;

    public OrgAufPanel(final ElementPropertyDialog dl) {
        super(dl);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        lroot = new LGMTreeNode(Tool3lgmConstants.getResString("Aufgabe_p"), false);
        lmodel = new DefaultTreeModel(lroot);
        tree = new LGMTree(lmodel, mainDoc);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(treeRenderer);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // MouseListener erstellen und an tree anhängen
        LGMAction treeMouseAction = LGMActionLibrary.getMouseAction(tree, this);
        tree.addMouseListener(new LGMMouseListener(null, null, null, treeMouseAction, null));

        // TreeSelectionListener erstellen und an tree anhängen
        LGMAction treeSelectionAction = LGMActionLibrary.getTreeSelectionAction(tree, this);
        tree.addTreeSelectionListener(new LGMTreeSelectionListener(treeSelectionAction));

        spl = new JScrollPane(tree);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.ipadx = -30;
        constraints.ipady = -10;
        add(this, viewButton, constraints, 0, 2, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(Tool3lgmConstants.getResString("Organisationseinheit_p"));
        add(this, label, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, spl, constraints, 0, 1, 1, 1);

        init();
    }

    @Override
    protected void init() {
        super.init();
        lroot.removeAllChildren();
        tree.reset();
        ModelElement modelElement = getModelElement();
        for (ElementContainer ec : modelElement.getConnectedContainer(AufOrgKombination.class, mainDoc)) {
            ArrayList<ElementContainer> o = ec.getElement().getConnectedContainer(Aufgabe.class, mainDoc);
            if (o.size() > 0) {
                tree.addObject(o.get(0), lroot, null, true, false);
            }
        }
        if (UserProperties.isSearchParts()) {
            for (ElementContainer ec : modelElement.getPartConnectedContainer(AufOrgKombination.class, mainDoc)) {
                ArrayList<ElementContainer> o = ec.getElement().getConnectedContainer(Aufgabe.class, mainDoc);
                if (o.size() > 0) {
                    LGMTreeNode node = tree.addObject(o.get(0), lroot, null, true, false);
                    if (node != null) {
                        node.setSelectable(false);
                    }
                }
            }
        }
        if (UserProperties.isSearchParents()) {
            for (ElementContainer ec : modelElement.getParentConnectedContainer(AufOrgKombination.class, mainDoc)) {
                ArrayList<ElementContainer> o = ec.getElement().getConnectedContainer(Aufgabe.class, mainDoc);
                if (o.size() > 0) {
                    LGMTreeNode node = tree.addObject(o.get(0), lroot, null, true, false);
                    if (node != null) {
                        node.setSelectable(false);
                    }
                }
            }
        }
        lmodel.reload();
        // expandTree(ltree);

        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {
        super.showFullDialog();
    }

    // public void actionPerformed(ActionEvent e) {
    // super.actionPerformed(e);
    // }
    //
    // public void mousePressed(MouseEvent e) {
    // JTree tree = (JTree) e.getSource();
    // if (tree.getLeadSelectionPath() != null) {
    // LGMTreeNode node = (LGMTreeNode) tree.getLeadSelectionPath().getLastPathComponent();
    // ElementContainer knot = (ElementContainer) node.getUserObject();
    // doc.select(knot, dialog.getTransactionID());
    // if (isPopupTrigger(e)) {
    // Tool3lgm.getContextGenerator().getTreeKnotContextMenu().show(e.getComponent(), e.getX() + 3,
    // e.getY() + 3);
    // }
    // }
    // }
}
