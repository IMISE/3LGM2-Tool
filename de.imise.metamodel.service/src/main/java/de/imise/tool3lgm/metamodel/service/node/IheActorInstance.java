package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_IheActorInstanceInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheActorInstanceInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PartableApplicationComponent_CommunicationInterface_Edge;

public class IheActorInstance extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(ApplicationSystem_IheActorInstance_Edge.class);
        dialog.addDescripPanel(IheActor_IheActorInstance_Edge.class);
        dialog.addEdgePanel(IheActorInstance_SoftwareProduct_Edge.class);

        dialog.addMultiPanel(IheActorInstanceInterface.class);
        dialog.addMultiPanelEdgePanel(IheActorInstanceInvokingInterface.class, IheActorInstance_IheActorInstanceInterface_Edge.class);
        dialog.addMultiPanelEdgePanel(IheActorInstanceProvidingInterface.class, IheActorInstance_IheActorInstanceInterface_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnEndElement(300);
        tableDefinition.addColumnPathStepName(2, "HEADER_CONNECTION_TYPE", 150);
        dialog.addTablePanel(tableDefinition, 2, PartableApplicationComponent_CommunicationInterface_Edge.class, IheInterface_IheActorInstanceInterface_Edge.class, IheInterface_IheTransaction_Edge.class);

        return dialog;
    }

}
