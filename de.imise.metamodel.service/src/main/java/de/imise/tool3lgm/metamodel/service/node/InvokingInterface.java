package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_InvokingInterface_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class InvokingInterface extends CommunicationInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(true, IheInvokingInterface_InvokingInterface_Edge.class);
        dialog.addDescripPanel(IheInvokingInterface_InvokingInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class);
        dialog.addEdgePanel(Service_InvokingInterface_Edge.class);
        return dialog;
    }

}
