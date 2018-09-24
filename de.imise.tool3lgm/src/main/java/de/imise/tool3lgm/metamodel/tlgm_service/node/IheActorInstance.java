package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorInstance_IheActor_Edge;

public class IheActorInstance extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheActorInstance_IheActor_Edge.class);
        return dialog;
    }

    private static final MetaPath NAME_EXTENSION_PATH = new MetaPath(IheActorInstance.class, IheActor.class, IheActorInstance_IheActor_Edge.class);

    @Override
    protected MetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
