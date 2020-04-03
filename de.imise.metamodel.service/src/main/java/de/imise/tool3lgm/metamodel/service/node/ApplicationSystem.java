package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;

/**
 * @author AXS (26.12.2017)
 */
public class ApplicationSystem extends PartableApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(ApplicationSystem_SoftwareProduct_Edge.class);
        dialog.addPathConnectionPanel(IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        return dialog;
    }

}
