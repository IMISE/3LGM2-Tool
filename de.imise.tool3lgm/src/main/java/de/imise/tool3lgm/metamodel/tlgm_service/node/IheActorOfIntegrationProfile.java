package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorApplicationSystem_IheActorOfIntegrationProfile_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorOfIntegrationProfile_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActorOfIntegrationProfile_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheTransactionLink_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheActorOfIntegrationProfile extends IheConcept {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheIntegrationProfile_IheActorOfIntegrationProfile_Edge.class);
        dialog.addEdgePanel(IheTransactionLink_Edge.class);
        dialog.addEdgePanel(IheActorApplicationSystem_IheActorOfIntegrationProfile_Edge.class);
        dialog.addTabbedPanel(ModelConstants.getDisplayablePluralName(IheInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheActorOfIntegrationProfile_IheInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheActorOfIntegrationProfile_IheInterface_Edge.class);
        return dialog;
    }

}
