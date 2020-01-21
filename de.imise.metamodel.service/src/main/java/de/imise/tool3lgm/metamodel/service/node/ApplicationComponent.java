package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
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
        dialog.addMultiPanel(CommunicationInterface.class);
        dialog.addMultiPanelEdgePanel(InvokingInterface.class, ApplicationComponent_CommunicationInterface_Edge.class);
        dialog.addMultiPanelEdgePanel(ProvidingInterface.class, ApplicationComponent_CommunicationInterface_Edge.class);
        return dialog;
    }

}
