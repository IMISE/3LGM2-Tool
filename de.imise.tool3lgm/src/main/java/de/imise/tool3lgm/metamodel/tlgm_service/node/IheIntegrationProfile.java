package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActorOfIntegrationProfile_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheIntegrationProfile extends IheConcept {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(IheIntegrationProfile_IheTransaction_Edge.class);
        dialog.addEdgePanel(IheIntegrationProfile_IheActorOfIntegrationProfile_Edge.class);
        return dialog;
    }

}
