package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorOfIntegrationProfile_ApplicationSystem_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class ApplicationSystem extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(ApplicationSystem_SoftwareProduct_Edge.class);
        dialog.addEdgePanel(IheActorOfIntegrationProfile_ApplicationSystem_Edge.class);
        return dialog;
    }

    private static final MetaPath NAME_EXTENSION_PATH = new MetaPath(ApplicationComponent.class, SoftwareProduct.class, ApplicationSystem_SoftwareProduct_Edge.class);

    @Override
    protected MetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
