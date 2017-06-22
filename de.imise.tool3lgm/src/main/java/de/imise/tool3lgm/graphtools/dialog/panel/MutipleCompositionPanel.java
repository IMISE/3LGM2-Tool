package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Panel für {@link Composition}s, die ein Element mehrfach zu den über die Compositions untergeordneten Elementen haben kann.
 * Also für alle Compositions, bei denen die maximale Kardinalität zu dem untergeordneten Element > 1 ist.
 */
public class MutipleCompositionPanel extends AbstractPathConnectionTreePanel {

    /**
     * COMMENTME
     */
    private final JTree tree;

    /**
     * COMMENTME
     */
    private final DefaultTreeModel model;

    /**
     * COMMENTME
     */
    private final LGMTreeNode root;

    private final JPanel buttonpanel;

    /**
     * @param dialog
     * @param searchElementClass
     * @param edgeClass
     */
    @SuppressWarnings("unchecked")
    public MutipleCompositionPanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Composition> edgeClass) {
        super(dialog, searchElementClass, edgeClass);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label = new JLabel(Tool3lgmConstants.getResString("verb"));
        root = new LGMTreeNode(Tool3lgmConstants.getResString("verb"), false);
        model = new DefaultTreeModel(root);
        tree = new JTree(model);
        tree.setRootVisible(false);
        tree.setCellRenderer(treeRenderer);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // MouseListener und SelectionListener erstellen und an tree anhängen
        addListener(tree);

        JButton addButton = new JButton(getCreateNewElementAction());
        JButton removeButton = new JButton(getDisconnectAction());

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

        update();
    }

    @Override
    public void update() {
        root.removeAllChildren();
        ModelElement modelElement = getModelElement();
        List<ElementContainer> all = modelElement.getConnectedContainer(searchElementClass, mainDoc);
        for (int m = 0; m < all.size(); m++) {
            LGMTreeNode node = new LGMTreeNode(all.get(m), false);
            root.add(node);
        }
        if (UserProperties.isSearchParts()) {
            all = ((Knoten) modelElement).getPartConnectedContainer(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(all.get(m), false);
                node.setSelectable(false);
                root.add(node);
            }
        }
        if (UserProperties.isSearchParents()) {
            all = ((Knoten) modelElement).getParentConnectedContainer(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(all.get(m), false);
                node.setSelectable(false);
                root.add(node);
            }
        }
        model.reload();
        expandTree(tree);
        revalidate();
        repaint();
    }

    /**
     *
     */
    public final LGMAction getCreateNewElementAction() {
        return new LGMAction(Tool3lgmConstants.getResString("addButtonText")) {
            @Override
            public void execute(final EventObject eo) {
                int pid = getTransactionID();
                GraphDocument selectedDoc = doc.getCollection().getSelectedDoc();
                ModelElement me = getModelElement();
                ElementContainer ec = me.getContainer(mainDoc);
                doc.select(ec, pid);
                GraphDocument.createAddicted(selectedDoc, me, edgeClasses[0].asSubclass(Composition.class), searchElementClass, pid);
                doc.select(ec, pid);
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     */
    public final LGMAction getDisconnectAction() {
        LGMAction returnAction = new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = tree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        // if(lomodel.getChildCount(loroot)>0) return;
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer knot = (ElementContainer) node.getUserObject();

                        ModelElement topLevelModelElement;
                        topLevelModelElement = getModelElement();
                        GDCollection gdcoll = getGraphDocument().getCollection();
                        gdcoll.unlink(topLevelModelElement, knot.getElement(), getTransactionID());
                    }
                }
            }
        };
        returnAction.putValue("Name", Tool3lgmConstants.getResString("delete"));
        returnAction.putValue("SmallIcon", null);

        return returnAction;
    }

}