package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheActorInstance_Edge;

public class IheActorInstance extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheActor_IheActorInstance_Edge.class);
        return dialog;
    }

    private static final MetaPath NAME_EXTENSION_PATH = new MetaPath(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class);

    @Override
    protected MetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
