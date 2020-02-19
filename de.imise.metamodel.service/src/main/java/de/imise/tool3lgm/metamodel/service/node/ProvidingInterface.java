package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ProvidingInterface_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class ProvidingInterface extends CommunicationInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, IheProvidingInterface_ProvidingInterface_Edge.class);
        dialog.addDescripPanel(IheProvidingInterface_ProvidingInterface_Edge.class, IheProvidingInterface_IheTransaction_Edge.class);
        dialog.addEdgePanel(Service_ProvidingInterface_Edge.class);
        return dialog;
    }

}
