package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorInstance_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheActorInstance_Edge;

public class IheActorInstance extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheActor_IheActorInstance_Edge.class);
        dialog.addEdgePanel(IheActorInstance_SoftwareProduct_Edge.class);
        return dialog;
    }

    private static final SimpleMetaPath NAME_EXTENSION_PATH = SimpleMetaPathCreator.createSimpleMetaPath(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class);

    @Override
    protected SimpleMetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
