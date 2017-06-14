package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

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
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Mit diesem Panel zeigen Anwendungsbausteinkonfigurationen ihre Aufgaben an.
 */
// TODO: FST: Funktionsfähigkeit testen
public class AwbAufPanel extends ElementDialogPanel {
    private final LGMTree tree;
    private final DefaultTreeModel model;
    private final LGMTreeNode root;

    public AwbAufPanel(final ElementPropertyDialog pd) {
        super(pd);

        setPreferredSize(new Dimension(550, 350));
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label = new JLabel(Tool3lgmConstants.getResString("Aufgabe_p"));
        root = new LGMTreeNode(getModelElement().getName(), false);
        model = new DefaultTreeModel(root);
        tree = new LGMTree(model, mainDoc);
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

        JScrollPane sp = new JScrollPane(tree);

        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, label, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp, constraints, 0, 1, 1, 4);

        update();
    }

    @Override
    public void update() {
        buildTree();
        tree.expandFull();
        revalidate();
        repaint();
    }

    /**
     *
     */
    private void buildTree() {
        tree.saveExpansion();
        tree.saveSelection();
        root.removeAllChildren();
        tree.reset();
        ModelElement modelElement = getModelElement();
        List<ElementContainer> all = modelElement.getConnectedContainer(ABKonfiguration.class, mainDoc);
        // nur Knoten für Elemente in der all-Liste bis zur Größe der direkt vrbundenen dürfen am
        // Ende
        // selektierbar sein
        int firstNonSelectableIndex = all.size();
        if (UserProperties.isSearchParts()) {
            all.addAll(modelElement.getPartConnectedContainer(ABKonfiguration.class, mainDoc));
        }
        if (UserProperties.isSearchParents()) {
            all.addAll(modelElement.getParentConnectedContainer(ABKonfiguration.class, mainDoc));
        }
        for (ElementContainer ec : all) {
            LGMTreeNode node = new LGMTreeNode(ec, false);
            root.add(node);
            for (ElementContainer konf : ec.getElement().getConnectedContainer(AufOrgKombination.class, mainDoc)) {
                LGMTreeNode abkonf = new LGMTreeNode(konf, false);
                node.add(abkonf);
                for (ElementContainer client : konf.getElement().getConnectedContainer(Aufgabe.class, doc)) {
                    tree.addObject(client, abkonf, null, true, false, false);
                }
            }
            // alle Elemente die von den Parts oder Parents kamen, nichtselektierbar setzen
            if (root.getChildCount() - 1 >= firstNonSelectableIndex) {
                node.setSelectable(false);
            }
        }

        model.reload();
        tree.restoreExpansion();
        tree.restoreSelection();

    }
}
