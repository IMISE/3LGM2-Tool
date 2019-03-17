package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_ProvidingInterface_Edge;

/**
 * @author AXS (24.04.2018)
 */
public class IheProvidingInterface extends IheInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        //showOnlyInExpertMode ist auf false, aber diese Knoten sollten sowieso nur im Expert Mode zu sehen sein
        dialog.addDescripSingleConnectionPanel(false, IheProvidingInterface_IheTransaction_Edge.class);
        dialog.addEdgePanel(IheProvidingInterface_ProvidingInterface_Edge.class);
        return dialog;
    }
}
