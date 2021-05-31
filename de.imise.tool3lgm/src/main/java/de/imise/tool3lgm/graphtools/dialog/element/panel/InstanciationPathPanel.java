package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.DialogActionCommands;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * Panel to represent instaciation paths. That means the last
 * {@link ElementaryMetaPath} in the panels {@link SimpleMetaPath} has an
 * {@link InstanciationEdge} as edge class and the direction
 * {@link InstanciationEdge#INSTANCE_TO_MASTER_DIRECTION} =
 * {@link Direction#BACKWARD}. In this case the panel shows on the left side the
 * instances (= start elements of the last path step) and on the right side the
 * instanciation master elements (=end elements of the last path step and the
 * whole path).<BR>
 * If an element from the right is connected or dragged to the left, internally
 * an instance of the instanciation master is created and connected over the
 * path to the model element of the dialog.
 *
 * @author AXS (02.04.2020)
 */
public class InstanciationPathPanel extends PathConnectionPanel {

    /**
     *
     */
    private final int elementaryMetaPathCount;

    /**
     *
     */
    private final ElementaryMetaPath lastElementaryMetaPathWithBackwardInstanciationEdge;

    /**
     * @param dialog
     * @param simpleMetaPath
     */
    private InstanciationPathPanel(final AbstractElementPropertyDialog dialog, final MetaPath metaPath) {
        super(dialog, LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_START_ELEMENT_TYPE, metaPath);
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        elementaryMetaPathCount = elementaryMetaPaths.size();
        lastElementaryMetaPathWithBackwardInstanciationEdge = metaPath.getLastElementaryMetaPath();
    }

    /**
     * Returns a valid panel (and not null) only if the simpleMetaPath fits the
     * conditions. These are that the metaPath must be creatable and the last
     * {@link ElementaryMetaPath} must be an {@link InstanciationEdge} from
     * instance element to instanciation master element.
     *
     * @param dialog
     * @param metaPath
     * @return
     */
    public static final InstanciationPathPanel getInstanciationPathPanel(final AbstractElementPropertyDialog dialog, final MetaPath metaPath) {
        if (isCreatableMetaPathWithBackwardInstanciationEnd(metaPath)) {
            return new InstanciationPathPanel(dialog, metaPath);
        }
        return null;
    }

    /**
     * @return the elementaryMetaPathCount of the given metaPath if this
     *         metaPath fits the conditions for this panel or -1 if the
     *         conditions are not fulfilled.
     */
    private static final boolean isCreatableMetaPathWithBackwardInstanciationEnd(final MetaPath metaPath) {
        if (!metaPath.isCreatable(false)) {
            return false;
        }
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        int elementaryMetaPathCount = elementaryMetaPaths.size();
        if (elementaryMetaPathCount < 2) {
            // 2 is the minimum for this type of metapath. The first elementary metapath(s) is/are
            // at least one edge from the dialog modelelement to the instance and the second is
            // the backward instance edge to the instanciation master element
            return false;
        }
        ElementaryMetaPath lastElementaryMetaPath = metaPath.getLastElementaryMetaPath();
        if (lastElementaryMetaPath == null) {
            return false;
        }
        Class<? extends Edge> edgeClass = lastElementaryMetaPath.getEdgeClass();
        if (!MetaModel.isInstanciation(edgeClass)) {
            return false;
        }
        //the direction of the last elementary metapath must be backward = from the instance
        //element to the instanciation master element
        if (!lastElementaryMetaPath.hasDirection(InstanciationEdge.INSTANCE_TO_MASTER_DIRECTION)) {
            return false;
        }
        return true;
    }

    /**
     * Baut im linken Baum nur die Elemente der letzten Edge des Pfades auf
     */
    @Override
    protected Collection<ElementContainerTreeNode> buildLeftTree() {
        Collection<ElementContainerTreeNode> leafNodes = super.buildLeftTree();
        if (!leafNodes.isEmpty()) {
            //if there are more thant 2 elementary metapaths in the whole metapath
            //all other nodes between the root and the seocnd last have to be removed
            //from the tree
            LGMTreeNode<?> lroot = ltree.getRoot();
            if (elementaryMetaPathCount > 2) {
                lroot.removeAllChildren();
            }
            for (ElementContainerTreeNode instanciationMasterNode : leafNodes) {
                ElementContainerTreeNode instanceNode = (ElementContainerTreeNode) instanciationMasterNode.getParent();
                instanceNode.removeAllChildren();
                if (instanceNode.getParent() != lroot) {
                    lroot.add(instanceNode);
                }
            }
        }
        return leafNodes;
    }

    @Override
    protected TreePath getConnectActionTargetTreeSelectionPath() {
        //ausgewählter Path im TargetTree ist immer root -> immer der gesamte Pfad wird neu angelegt
        return new TreePath(ltree.getModel().getRoot());
    }

    @Override
    protected void connectToFirstPath(final ModelElement element2Connect) {
        //dieses Panel ändert das ursprüngliche Verhalten dahingehend, dass es immer den ganzen Pfad neu anlegt und nicht nur den letzten Teil
        createPath(element2Connect);
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * von Elementen aus dem <code>srcTree</code> in den <code>targetTree</code>
     * realisiert. Diese <code>LGMAction</code> sollte an die "removeButtons"
     * der Panels angefügt werden.
     *
     * @param srcTree linker Baum mit dem verknüpften Pfaden
     * @param targetTree rechter Baum mit den Elementen, die ausgewählt werden
     *            können
     */
    @Override
    protected LGMAction getDisconnectAction() {
        final PathConnectionPanel panel = this;
        return new LGMAction(DialogActionCommands.ACTION_DIALOG_DISCONNECT_ELEMENT) {

            @Override
            public void execute(final EventObject eo) {
                int selrows = ltree.getSelectionCount();
                if (selrows < 1) {
                    return;
                }

                TreePath[] path2disconnect = ltree.getSelectionPaths();
                for (int i = 0; i < path2disconnect.length; i++) {
                    //das ist der Index der Edge im Pfad, ab der entfernt werden soll
                    int treePathEdgeIndex = path2disconnect[i].getPathCount() - 2;
                    ModelElement instanceElement = PathConnectionPanel.getPathModelElement(path2disconnect[i]);
                    List<ModelElement> instanciationMasters = lastElementaryMetaPathWithBackwardInstanciationEdge.getConnectedElements(instanceElement);
                    for (ModelElement instanciationMaster : instanciationMasters) {
                        panel.disconnect(instanceElement, instanciationMaster, treePathEdgeIndex + 1);
                    }
                }
            }
        };
    }

}
