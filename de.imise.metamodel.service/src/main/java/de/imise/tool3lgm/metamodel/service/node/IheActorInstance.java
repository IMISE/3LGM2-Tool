package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;

public class IheActorInstance extends ApplicationSystem {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheActor_IheActorInstance_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnEndElement(300);
        tableDefinition.addColumnPathStepName(2, "HEADER_CONNECTION_TYPE", 150);
        dialog.addTablePanel(tableDefinition, 2, ApplicationComponent_CommunicationInterface_Edge.class, IheInterface_CommunicationInterface_Edge.class, IheInterface_IheTransaction_Edge.class);

        return dialog;
    }

    private static final SimpleMetaPath NAME_EXTENSION_PATH = SimpleMetaPathCreator.createSimpleMetaPath(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class);

    @Override
    protected SimpleMetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
