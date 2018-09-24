package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheTransactionLink_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheActor extends IheConcept {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheIntegrationProfile_IheActor_Edge.class);
        dialog.addEdgePanel(IheTransactionLink_Edge.class);
        dialog.addEdgePanel(IheActor_IheActorInstance_Edge.class);
        dialog.addTabbedPanel(ModelConstants.getDisplayablePluralName(IheInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheActor_IheInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheActor_IheInterface_Edge.class);
        return dialog;
    }

}
