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
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Mit diesem Panel zeigen physische Datenverarbeitungsbausteine die Anwenungsbausteine an, die sie
 * unterstützen. Es ist ein reines Anzeigepanel ohne Änderungsfunktionalität.
 * 
 * @author N.N.
 */
public class PDVBKonfPanel2 extends ElementDialogPanel {

    private final LGMTree tree;
    private final DefaultTreeModel model;
    private final LGMTreeNode root;

    /**
     * @param pd
     */
    public PDVBKonfPanel2(final ElementPropertyDialog pd) {
        super(pd);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label = new JLabel(Tool3lgmConstants.getResString("Anwendungsbaustein_p"));
        root = new LGMTreeNode(getModelElement().getName(), false);
        model = new DefaultTreeModel(root);
        tree = new LGMTree(model, mainDoc);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(treeRenderer);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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

        // MouseListener erstellen und an tree anhängen
        LGMAction treeMouseAction = LGMActionLibrary.getMouseAction(tree, this);
        tree.addMouseListener(new LGMMouseListener(null, null, null, treeMouseAction, null));

        // TreeSelectionListener erstellen und an tree anhängen
        LGMAction treeSelectionAction = LGMActionLibrary.getTreeSelectionAction(tree, this);
        tree.addTreeSelectionListener(new LGMTreeSelectionListener(treeSelectionAction));

        init();
    }

    @Override
    protected void init() {
        super.init();
        builtTree();
        // expandTree(tree);
        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {
        super.showFullDialog();
    }

    /**
	 * 
	 */
    private void builtTree() {
        root.removeAllChildren();
        tree.reset();
        ModelElement modelElement = getModelElement();
        ArrayList<ElementContainer> configs = modelElement.getConnectedContainer(DBKonfiguration.class, mainDoc);
        int ownKonfigsSize = configs.size();
        if (UserProperties.isSearchParts()) {
            configs.addAll(modelElement.getPartConnectedContainer(DBKonfiguration.class, mainDoc));
        }
        if (UserProperties.isSearchParents()) {
            configs.addAll(modelElement.getParentConnectedContainer(DBKonfiguration.class, mainDoc));
        }
        for (int i = 0; i < configs.size(); i++) {
            ElementContainer ec = configs.get(i);
            ArrayList<ElementContainer> clients = ((DBKonfiguration) ec.getElement()).getClientContainer(mainDoc);
            if (clients.size() > 0) {
                LGMTreeNode pdvbkonf = new LGMTreeNode(ec, false);
                if (i >= ownKonfigsSize) {
                    pdvbkonf.setSelectable(false);
                }
                root.add(pdvbkonf);
                LGMTreeNode pdvb = null;
                for (ElementContainer cC : clients) {
                    pdvb = tree.addObject(cC, pdvbkonf, null, true, false, false);
                    if (i >= ownKonfigsSize && pdvb != null) {
                        pdvb.setSelectable(false);
                    }
                }
            }
        }
        model.reload();
    }

}
