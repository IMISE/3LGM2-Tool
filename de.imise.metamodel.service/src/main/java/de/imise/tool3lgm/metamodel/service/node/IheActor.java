package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheActor extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheIntegrationProfile_IheActor_Edge.class, IheIntegrationProfile_IheDomain_Edge.class);
        dialog.addDescripSingleConnectionPanel(IheIntegrationProfile_IheActor_Edge.class);
        dialog.addEdgePanel(IheActor_IheActorInstance_Edge.class);
        //dieses tabbedPane wird nur im ExpertMode angezeigt! Und damit auch die darin enthaltenen Interface-Panels
        dialog.addTabbedPanel(ElementsNameBuilder.getDisplayablePluralName(IheInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheActor_IheInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheActor_IheInterface_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnEndElement(300);
        tableDefinition.addColumnOptional(0, 50);
        tableDefinition.addColumnPathStepName(1, "HEADER_CONNECTION_TYPE", 150);
        dialog.addTablePanel(tableDefinition, 1, IheActor_IheInterface_Edge.class, IheInterface_IheTransaction_Edge.class);
        return dialog;
    }

}
