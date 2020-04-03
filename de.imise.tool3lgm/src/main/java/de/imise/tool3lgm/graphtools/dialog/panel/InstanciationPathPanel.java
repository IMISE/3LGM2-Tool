package de.imise.tool3lgm.graphtools.dialog.panel;

import java.util.Collection;
import java.util.List;

import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * Panel to represent instaciation paths. That means the last {@link ElementaryMetaPath}
 * in the panels {@link SimpleMetaPath} has an {@link InstanciationEdge} as edge class
 * and the direction {@link InstanciationEdge#INSTANCE_TO_MASTER_DIRECTION} =
 * {@link Direction#BACKWARD}. In this case the panel shows on the left side the instances
 * (= start elements of the last path step) and on the right side the instanciation master
 * elements (=end elements of the last path step and the whole path).<BR>
 * If an element from the right is connected or dragged to the left, internally an instance
 * of the instanciation master is created and connected over the path to the model element
 * of the dialog.
 *
 * @author AXS (02.04.2020)
 */
public class InstanciationPathPanel extends PathConnectionPanel {

    /**
     *
     */
    private final int elementaryMetaPathCount;

    /**
     * @param dialog
     * @param simpleMetaPath
     */
    private InstanciationPathPanel(final AbstractElementPropertyDialog dialog, final AbstractMetaPath metaPath, final int elementaryMetaPathCount) {
        super(dialog, metaPath);
        this.elementaryMetaPathCount = elementaryMetaPathCount;
    }

    /**
     * Returns a valid panel (and not null) only if the simpleMetaPath fits the conditions.
     * These are that the metaPath must be creatable and the last {@link ElementaryMetaPath}
     * must be an {@link InstanciationEdge} from instance element to instanciation master
     * element.
     *
     * @param dialog
     * @param simpleMetaPath
     * @return
     */
    public static final InstanciationPathPanel getInstanciationPathPanel(final AbstractElementPropertyDialog dialog, final AbstractMetaPath simpleMetaPath) {
        int elementaryMetaPathCount = isCreatableMetaPathWithBackwardInstanciationEnd(simpleMetaPath);
        if (elementaryMetaPathCount > 0) {
            return new InstanciationPathPanel(dialog, simpleMetaPath, elementaryMetaPathCount);
        }
        return null;
    }

    /**
     * @return the elementaryMetaPathCount of the given metaPath if this metaPath fits the conditions
     *         for this panel or -1 if the conditions are not fulfilled.
     */
    private static final int isCreatableMetaPathWithBackwardInstanciationEnd(final AbstractMetaPath metaPath) {
        if (!metaPath.isCreatable(false)) {
            return -1;
        }
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        int elementaryMetaPathCount = elementaryMetaPaths.size();
        if (elementaryMetaPathCount < 2) {
            // 2 is the minimum for this type of metapath. The first elementary metapath(s) is/are
            // at least one edge from the dialog modelelement to the instance and the second is
            // the backward instance edge to the instanciation master element
            return -1;
        }
        ElementaryMetaPath lastElementaryMetaPath = metaPath.getLastElementaryMetaPath();
        if (lastElementaryMetaPath == null) {
            return -1;
        }
        Class<? extends Edge> edgeClass = lastElementaryMetaPath.getEdgeClass();
        if (!MetaModel.isInstanciation(edgeClass)) {
            return -1;
        }
        //the direction of the last elementary metapath must be backward = from the instance
        //element to the instanciation master element
        if (!lastElementaryMetaPath.hasDirection(InstanciationEdge.INSTANCE_TO_MASTER_DIRECTION)) {
            return -1;
        }
        return elementaryMetaPathCount;
    }

    /**
     * Baut im linken Baum nur die Elemente der letzten Edge des Pfades auf
     */
    @Override
    protected Collection<LGMTreeNode> buildLeftTree() {
        Collection<LGMTreeNode> leafNodes = super.buildLeftTree();
        if (!leafNodes.isEmpty()) {
            //if there are more thant 2 elementary metapaths in the whole metapath
            //all other nodes between the root and the seocnd last have to be removed
            //from the tree
            if (elementaryMetaPathCount > 2) {
                lroot.removeAllChildren();
            }
            for (LGMTreeNode instanciationMasterNode : leafNodes) {
                LGMTreeNode instanceNode = (LGMTreeNode) instanciationMasterNode.getParent();
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

}
