package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_ProvidingInterface_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class ProvidingInterface extends CommunicationInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, true, IheProvidingInterface_ProvidingInterface_Edge.class);
        dialog.addDescripSingleConnectionPanel(IheProvidingInterface_ProvidingInterface_Edge.class, IheProvidingInterface_IheTransaction_Edge.class);
        dialog.addEdgePanel(CommunicationLink_Edge.class);
        dialog.addEdgePanel(Service_ProvidingInterface_Edge.class);
        return dialog;
    }

}
