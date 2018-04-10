package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorOfIntegrationProfile_IheActorApplicationSystem_Edge;

public class IheActorApplicationSystem extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(IheActorOfIntegrationProfile_IheActorApplicationSystem_Edge.class);
        return dialog;
    }
}
