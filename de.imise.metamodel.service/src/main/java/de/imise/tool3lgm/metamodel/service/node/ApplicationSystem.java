package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;

/**
 * @author AXS (26.12.2017)
 */
public class ApplicationSystem extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(ApplicationSystem_SoftwareProduct_Edge.class);
        dialog.addEdgePanel(ApplicationSystem_IheActorInstance_Edge.class);
        return dialog;
    }

    private static final SimpleMetaPath NAME_EXTENSION_PATH = SimpleMetaPathCreator.createSimpleMetaPath(ApplicationSystem.class, SoftwareProduct.class, ApplicationSystem_SoftwareProduct_Edge.class);

    @Override
    protected SimpleMetaPath getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
