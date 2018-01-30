package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_SupportLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.OrganisationalUnit_SupportLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.SupportLink_Edge;

/**
 * @author AXS (26.12.2017)
 */
public abstract class ApplicationComponent extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(SupportLink_Edge.class, Function_Use_Edge.class);
        dialog.addPathConnectionInfoPanel(ApplicationComponent_SupportLink_Edge.class, OrganisationalUnit_SupportLink_Edge.class);
        dialog.addTabbedPanel(ModelConstants.getDisplayablePluralName(CommunicationInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(InvokingInterface.class, ApplicationComponent_CommunicationInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(ProvidingInterface.class, ApplicationComponent_CommunicationInterface_Edge.class);
        return dialog;
    }

}
