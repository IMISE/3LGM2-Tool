package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorApplicationSystem_IheActor_Edge;

public class IheActorApplicationSystem extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheActorApplicationSystem_IheActor_Edge.class);
        return dialog;
    }

    private static final MetaPath NAME_EXTENSION_PATH = new MetaPath(IheActorApplicationSystem.class, IheActor.class, IheActorApplicationSystem_IheActor_Edge.class);

    @Override
    protected MetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
