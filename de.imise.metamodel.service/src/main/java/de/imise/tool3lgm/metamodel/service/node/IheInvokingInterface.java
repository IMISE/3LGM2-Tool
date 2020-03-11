package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheActorInstanceInvokingInterface_Edge;

/**
 * @author AXS (24.04.2018)
 */
public class IheInvokingInterface extends IheInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class);
        return dialog;
    }
}
