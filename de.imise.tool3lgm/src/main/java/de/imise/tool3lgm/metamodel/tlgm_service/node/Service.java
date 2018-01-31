package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheTransaction_Service_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ServiceUses_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_ServiceClass_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class Service extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(Service_ServiceClass_Edge.class);
        dialog.addEdgePanel(ServiceUses_Edge.class);
        dialog.addEdgePanel(Service_ObjectType_Edge.class);
        dialog.addTabbedPanel(ModelConstants.getDisplayablePluralName(CommunicationInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(Service_InvokingInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(Service_ProvidingInterface_Edge.class);
        dialog.addEdgePanel(Service_ObjectType_Edge.class);
        dialog.addEdgePanel(IheTransaction_Service_Edge.class);
        return dialog;
    }

}
