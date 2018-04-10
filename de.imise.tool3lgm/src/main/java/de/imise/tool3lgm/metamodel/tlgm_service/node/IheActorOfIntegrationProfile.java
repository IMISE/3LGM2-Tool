package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorOfIntegrationProfile_IheActorApplicationSystem_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActorOfIntegrationProfile_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheTransactionLink_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheActorOfIntegrationProfile extends IheConcept {

    @Override
    public ElementPropertyDialog getPropertyDialog() {
        ElementPropertyDialog dialog = super.getPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheIntegrationProfile_IheActorOfIntegrationProfile_Edge.class);
        dialog.addEdgePanel(IheTransactionLink_Edge.class);
        dialog.addEdgePanel(IheActorOfIntegrationProfile_IheActorApplicationSystem_Edge.class);
        return dialog;
    }

}
