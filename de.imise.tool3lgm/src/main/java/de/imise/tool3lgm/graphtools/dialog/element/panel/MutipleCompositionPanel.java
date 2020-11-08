package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge.MASTER_TO_SLAVE_DIRECTION;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;

/**
 * Panel für {@link CompositionEdge}s, die ein Element mehrfach zu den über die
 * Compositions untergeordneten Elementen haben kann. Also für alle
 * Compositions, bei denen die maximale Kardinalität zu dem untergeordneten
 * Element > 1 ist.
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

    private JPanel buttonpanel;

    /**
     * @param dialog
     * @param titleLabelOption
     * @param westLabelOption
     * @param searchElementClass
     * @param edgeClass
     */
    public MutipleCompositionPanel(final ElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final Class<? extends ModelElement> searchElementClass,
            final Class<? extends CompositionEdge> edgeClass) {
        super(dialog, titleLabelOption, westLabelOption, dialog.createSimpleMetaPath(searchElementClass, edgeClass));

        boolean editable = !dialog.isInfoDialog() && metaPath.isCreatable(true) && !metaPath.isFirstPathElementDependent(); // element to connect can be created new in this panel

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        root = new StringTreeNode(getResString("verb"));
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

        if (editable) {
            constraints.anchor = GridBagConstraints.CENTER;
            buttonpanel = new JPanel();
            buttonpanel.setLayout(new GridLayout(1, 2));
            buttonpanel.add(removeButton);
            buttonpanel.add(addButton);
            add(this, buttonpanel, constraints, 0, 2, 3, 1);
        }

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp, constraints, 0, 0, 1, 1);

        update();
    }

    @Override
    public void update() {
        root.removeAllChildren();
        ModelElement me = getModelElement();
        GraphDocument mainDoc = getMainDoc();
        List<ElementContainer> all = me.getConnectedContainers(searchElementClass, mainDoc);
        for (int m = 0; m < all.size(); m++) {
            LGMTreeNode node = new ElementContainerTreeNode(all.get(m), false, true);
            root.add(node);
        }
        if (OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is()) {
            all = ((Node) me).getPartConnectedContainers(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new ElementContainerTreeNode(all.get(m), false, true);
                node.setSelectable(false);
                root.add(node);
            }
        }
        if (OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is()) {
            all = ((Node) me).getParentConnectedContainers(searchElementClass, mainDoc);
            for (int m = 0; m < all.size(); m++) {
                LGMTreeNode node = new ElementContainerTreeNode(all.get(m), false, true);
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
        return new LGMAction(getResString("addButtonText")) {
            @Override
            public void execute(final EventObject eo) {
                int pid = getTransactionID();
                ModelElement me = getModelElement();
                GraphDocument selectedDoc = getSelectedDoc();
                GraphDocument mainDoc = selectedDoc.getMainDoc();
                ElementContainer ec = me.getContainer(mainDoc);
                mainDoc.select(ec, pid);
                Class<? extends Edge> lastEdgeClassInPath = getLastEdgeClassInPath();
                Class<? extends CompositionEdge> lastEdgeClassInPathAsComposition = lastEdgeClassInPath.asSubclass(CompositionEdge.class);
                GraphDocument.createAddicted(selectedDoc, me, lastEdgeClassInPathAsComposition, searchElementClass, pid);
                mainDoc.select(ec, pid);
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * von Elementen aus dem <code>srcTree</code> in den <code>targetTree</code>
     * realisiert. Diese <code>LGMAction</code> sollte an die "removeButtons"
     * der Panels angefügt werden.
     */
    public final LGMAction getDisconnectAction() {
        LGMAction returnAction = new LGMAction(getResString("delete")) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = tree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        // if(lomodel.getChildCount(loroot)>0) return;
                        LGMTreeNode treeNode = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer ec = (ElementContainer) treeNode.getUserObject();

                        ModelElement topLevelElement;
                        topLevelElement = getModelElement();
                        GDCollection gdcoll = topLevelElement.getCollection();
                        ModelElement treeNodeElement = ec.getElement();
                        Class<? extends Edge> lastEdgeClassInPath = getLastEdgeClassInPath();
                        int pid = getTransactionID();
                        gdcoll.unlink(topLevelElement, treeNodeElement, lastEdgeClassInPath, MASTER_TO_SLAVE_DIRECTION, pid);
                    }
                }
            }
        };
        return returnAction;
    }

}