package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInterface_IheTransaction_Edge;

/**
 * @author AXS (24.04.2018)
 */
public abstract class IheInterface extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, IheActor_IheInterface_Edge.class);
        dialog.addDescripSingleConnectionPanel(false, IheInterface_IheTransaction_Edge.class);
        dialog.addDescripSingleConnectionPanel(IheActor_IheInterface_Edge.class, IheIntegrationProfile_IheActor_Edge.class, IheIntegrationProfile_IheDomain_Edge.class);
        dialog.addDescripSingleConnectionPanel(IheActor_IheInterface_Edge.class, IheIntegrationProfile_IheActor_Edge.class);
        return dialog;
    }

}
