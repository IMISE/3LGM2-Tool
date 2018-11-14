package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.MetaPathOld;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationSystem_SoftwareProduct_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class ApplicationSystem extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(ApplicationSystem_SoftwareProduct_Edge.class);
        dialog.addEdgePanel(ApplicationSystem_IheActorInstance_Edge.class);
        return dialog;
    }

    private static final MetaPathOld NAME_EXTENSION_PATH = new MetaPathOld(ApplicationComponent.class, SoftwareProduct.class, ApplicationSystem_SoftwareProduct_Edge.class);

    @Override
    protected MetaPathOld getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
