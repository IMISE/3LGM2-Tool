package de.imise.tool3lgm.graphtools.dialog.panel;

import java.util.List;

import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

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
     * @param dialog
     * @param simpleMetaPath
     */
    private InstanciationPathPanel(final AbstractElementPropertyDialog dialog, final AbstractMetaPath metaPath) {
        super(dialog, metaPath);
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
        if (isCreatableMetaPathWithBackwardInstanciationEnd(simpleMetaPath)) {
            return new InstanciationPathPanel(dialog, simpleMetaPath);
        }
        return null;
    }

    /**
     * @return
     */
    private static final boolean isCreatableMetaPathWithBackwardInstanciationEnd(final AbstractMetaPath metaPath) {
        if (!metaPath.isCreatable(false)) {
            return false;
        }
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.size() < 2) {
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
        if (!lastElementaryMetaPath.hasDirection(InstanciationEdge.INSTANCE_TO_MASTER_DIRECTION)) {
            return false;
        }
        return true;
    }

}
