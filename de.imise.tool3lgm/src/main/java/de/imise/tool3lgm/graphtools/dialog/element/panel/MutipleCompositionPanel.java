package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge.MASTER_TO_SLAVE_DIRECTION;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.ElementDialogPanelTree;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * Panel für {@link CompositionEdge}s, die ein Element mehrfach zu den über die
 * Compositions untergeordneten Elementen haben kann. Also für alle
 * Compositions, bei denen die maximale Kardinalität zu dem untergeordneten
 * Element > 1 ist.
 */
public class MutipleCompositionPanel extends AbstractPathConnectionTreePanel {

    /**
     *
     */
    private final ElementDialogPanelTree tree;

    /**
     *
     */
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
        super(dialog, titleLabelOption, westLabelOption, dialog.createSequenceMetaPath(searchElementClass, edgeClass));

        boolean editable = !dialog.isInfoDialog() && metaPath.isCreatable(true) && !metaPath.isStartDependent(); // element to connect can be created new in this panel

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        LGMGraphDocument mainDoc = getMainDoc();
        tree = new ElementDialogPanelTree(searchElementClass, mainDoc);
        tree.setRootVisible(false);
        tree.setCellRenderer(treeRenderer);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // MouseListener und SelectionListener erstellen und an tree anhängen
        addListener(tree);

        JButton addButton = new JButton(getCreateNewElementAction());
        JButton removeButton = new JButton(getDisconnectAction());

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
        add(this, tree.getScrollPane(), constraints, 0, 0, 1, 1);

        update();
    }

    @Override
    public void update() {
        LGMTreeNode<?> root = tree.getRoot();
        root.removeAllChildren();
        ModelElement me = getModelElement();
        GraphDocument mainDoc = getMainDoc();

        List<ElementContainer> elementContainers = me.getConnectedContainers(searchElementClass, mainDoc);
        addNodes(root, elementContainers, true);
        if (me instanceof Node) {
            if (OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is()) {
                elementContainers = ((Node) me).getPartConnectedContainers(searchElementClass, mainDoc);
                addNodes(root, elementContainers, false);
            }
            if (OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is()) {
                elementContainers = ((Node) me).getParentConnectedContainers(searchElementClass, mainDoc);
                addNodes(root, elementContainers, false);
            }
        }
        tree.reloadModel();
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
                GraphDocument.createSubordinated(selectedDoc, me, lastEdgeClassInPathAsComposition, searchElementClass, pid);
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
                        ElementContainerTreeNode ecTreeNode = (ElementContainerTreeNode) selpaths[n].getLastPathComponent();
                        ModelElement topLevelElement;
                        topLevelElement = getModelElement();
                        GDCollection gdcoll = topLevelElement.getCollection();
                        ModelElement treeNodeElement = ecTreeNode.getModelElement();
                        Class<? extends Edge> lastEdgeClassInPath = getLastEdgeClassInPath();
                        int pid = getTransactionID();
                        gdcoll.unlink(topLevelElement, treeNodeElement, lastEdgeClassInPath, MASTER_TO_SLAVE_DIRECTION, pid);
                    }
                }
            }
        };
        return returnAction;
    }

    @Override
    public Collection<JComponent> getToolTipTargets() {
        return Lists.newArrayList(tree);
    }

}
