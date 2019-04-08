package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorInstance_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInvokingInterface_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_ProvidingInterface_Edge;

public class IheActorInstance extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheActor_IheActorInstance_Edge.class);
        dialog.addEdgePanel(IheActorInstance_SoftwareProduct_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnEndElement(300);
        tableDefinition.addColumnPathStepName(2, "HEADER_CONNECTION_TYPE", 150);
        SimpleMetaPath path1 = createSimpleMetaPath(2, ApplicationComponent_CommunicationInterface_Edge.class, IheInvokingInterface_InvokingInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class);
        SimpleMetaPath path2 = createSimpleMetaPath(2, ApplicationComponent_CommunicationInterface_Edge.class, IheProvidingInterface_ProvidingInterface_Edge.class, IheProvidingInterface_IheTransaction_Edge.class);
        dialog.addTablePanel(tableDefinition, path1, path2);


        return dialog;
    }

    private static final SimpleMetaPath NAME_EXTENSION_PATH = SimpleMetaPathCreator.createSimpleMetaPath(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class);

    @Override
    protected SimpleMetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
