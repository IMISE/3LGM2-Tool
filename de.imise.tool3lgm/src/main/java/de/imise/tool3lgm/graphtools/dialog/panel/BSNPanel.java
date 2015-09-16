package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Das Panel für die Bausteinschnittstellen
 */
public class BSNPanel extends ElementDialogPanel {

    /**
     * COMMENTME
     */
    private final JTree tree;

    /**
     * COMMENTME
     */
    private final DefaultTreeModel lmodel;

    /**
     * COMMENTME
     */
    private final LGMTreeNode lroot;

    /**
     * COMMENTME
     */
    private JPanel workingpanel;

    private final JPanel buttonpanel;

    /**
     * COMMENTME
     */
    private final Class<? extends ModelElement> searchElementClass;

    public Class<? extends ModelElement> getSearchElementClass() {
        return searchElementClass;
    }

    /**
     * COMMENTME
     */
    private LGMAction addAction;

    /**
     * COMMENTME
     */
    private LGMAction removeAction;

    /**
     * @param searchElementClass
     * @param dl
     */
    public BSNPanel(final Class<? extends ModelElement> searchElementClass, final ElementPropertyDialog dl) {
        super(dl);

        this.searchElementClass = searchElementClass;

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label = new JLabel(Tool3lgmConstants.getResString("verb"));
        lroot = new LGMTreeNode(Tool3lgmConstants.getResString("verb"), false);
        lmodel = new DefaultTreeModel(lroot);
        tree = new JTree(lmodel);
        tree.setRootVisible(false);
        tree.setCellRenderer(treeRenderer);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // MouseListener erstellen und an tree anhängen
        LGMAction treeMouseAction = LGMActionLibrary.getMouseAction(tree, this);
        tree.addMouseListener(new LGMMouseListener(null, null, null, treeMouseAction, null));

        // TreeSelectionListener erstellen und an tree anhängen
        LGMAction treeSelectionAction = LGMActionLibrary.getTreeSelectionAction(tree, this);
        tree.addTreeSelectionListener(new LGMTreeSelectionListener(treeSelectionAction));

        /*
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        JButton addButton = new JButton();
        JButton removeButton = new JButton();

        try {
            addAction = LGMActionLibrary.getAddElementAction(null, null, this, false);
            removeAction = LGMActionLibrary.getDisconnectAction(tree, null, this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addButton.setAction(addAction);
        removeButton.setAction(removeAction);
        /*
         * ... end: Buttons & Actions erstellen und registrieren
         */

        JScrollPane sp = new JScrollPane(tree);

        constraints.anchor = GridBagConstraints.CENTER;
        buttonpanel = new JPanel();
        buttonpanel.setLayout(new GridLayout(1, 2));
        buttonpanel.add(removeButton);
        buttonpanel.add(addButton);
        add(this, buttonpanel, constraints, 0, 2, 3, 1);

        // add(this, viewButton, constraints, 2, 3, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, label, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp, constraints, 0, 1, 3, 1);

        init();
    }

    @Override
    protected void init() {
        super.init();
        // remove(workingpanel);
        lroot.removeAllChildren();
        ModelElement modelElement = getModelElement();
        List<ElementContainer> all = modelElement.getConnectedContainer(searchElementClass, mainDoc);
        for (int m = 0; m < all.size(); m++) {
            LGMTreeNode node = new LGMTreeNode(all.get(m), false);
            lroot.add(node);
        }
        if (UserProperties.isSearchParts()) {
            all = ((Knoten) modelElement).getPartConnectedContainer(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(all.get(m), false);
                node.setSelectable(false);
                lroot.add(node);
            }
        }
        if (UserProperties.isSearchParents()) {
            all = ((Knoten) modelElement).getParentConnectedContainer(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(all.get(m), false);
                node.setSelectable(false);
                lroot.add(node);
            }
        }
        lmodel.reload();
        expandTree(tree);
        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {

        if (true) {
            return;
        }

        super.showFullDialog();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 100;
        constraints.weighty = 100;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(this, workingpanel, constraints, 1, 3, 1, 3);

        revalidate();
        repaint();
    }
}
