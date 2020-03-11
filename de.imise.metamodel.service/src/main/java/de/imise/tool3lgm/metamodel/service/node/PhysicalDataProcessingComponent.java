package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR;

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
        dialog.addDescripPanel(PhysicalDataProcessingComponent_Location_Edge.class);
        dialog.addDescripPanel(PhysicalDataProcessingComponent_DeviceClass_Edge.class);
        dialog.addEdgePanel(LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR, LABEL_LAST_EDGE_CONNECTION_NAME, DataTransmissionLink_Edge.class);
        dialog.addEdgePanel(LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR, LABEL_LAST_EDGE_CONNECTION_NAME, PhysicalDataProcessingComponentVirtualises_Edge.class);
        dialog.addMultiPanel(ApplicationComponent.class);
        dialog.addMultiPanelEdgePanel(LABEL_LAST_EDGE_CONNECTION_NAME, ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge.class);
        dialog.addMultiPanelEdgePanel(LABEL_LAST_EDGE_CONNECTION_NAME, ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge.class);
        //Das auskommentierte ist dasselbe wie das MultiPanel, nur in 2 Einzelpanels. Das MultiPanel ist wahrscheinlich besser (analog zur
        //Gegenrichtung - also dem Dialog der Anwendungssysteme)
        //        dialog.addEdgePanel(LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR, ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge.class);
        //        dialog.addEdgePanel(LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR, ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge.class);
        return dialog;
    }

}
