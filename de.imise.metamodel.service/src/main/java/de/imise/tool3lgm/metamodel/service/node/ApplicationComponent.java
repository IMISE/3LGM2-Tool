package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_FIRST_EDGE_CONNECTION_NAME;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_FIRST_EDGE_ELEMENT_NAME;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_RepresentationForm_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ObjectType_RepresentationForm_Edge;
import de.imise.tool3lgm.metamodel.service.edge.OrganisationalUnit_Use_Edge;

/**
 * @author AXS (26.12.2017)
 */
public abstract class ApplicationComponent extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(ApplicationComponent_Use_Edge.class, Function_Use_Edge.class);
        dialog.addPathConnectionPanel(ApplicationComponent_Use_Edge.class, OrganisationalUnit_Use_Edge.class);
        dialog.addPathConnectionPanel(LABEL_FIRST_EDGE_ELEMENT_NAME, LABEL_FIRST_EDGE_CONNECTION_NAME, ApplicationComponent_RepresentationForm_Edge.class, ObjectType_RepresentationForm_Edge.class);
        dialog.addMultiPanel(PhysicalDataProcessingComponent.class);
        dialog.addMultiPanelPathPanel(LABEL_LAST_EDGE_CONNECTION_NAME, ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge.class);
        dialog.addMultiPanelPathPanel(LABEL_LAST_EDGE_CONNECTION_NAME, ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge.class);
        return dialog;
    }

}
