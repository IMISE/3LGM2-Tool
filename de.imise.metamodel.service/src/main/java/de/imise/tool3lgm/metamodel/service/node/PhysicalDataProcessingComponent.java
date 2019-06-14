package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge;
import de.imise.tool3lgm.metamodel.service.edge.DataTransmissionLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponentVirtualises_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_DeviceClass_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_Location_Edge;

/**
 * @author AXS (22.12.2017)
 */
public class PhysicalDataProcessingComponent extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(PhysicalDataProcessingComponent_Location_Edge.class);
        dialog.addDescripSingleConnectionPanel(PhysicalDataProcessingComponent_DeviceClass_Edge.class);
        dialog.addEdgePanel(DataTransmissionLink_Edge.class);
        dialog.addEdgePanel(PhysicalDataProcessingComponentVirtualises_Edge.class);
        dialog.addEdgePanel(ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge.class);
        dialog.addEdgePanel(ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge.class);
        return dialog;
    }

}
