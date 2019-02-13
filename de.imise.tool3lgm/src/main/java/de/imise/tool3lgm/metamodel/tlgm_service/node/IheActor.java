package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActor_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheActor extends IheConcept {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(false, true, false, IheIntegrationProfile_IheActor_Edge.class);
        dialog.addEdgePanel(true, IheActor_IheActorInstance_Edge.class);
        dialog.addTabbedPanel(ElementsNameBuilder.getDisplayablePluralName(IheInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheActor_IheInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheActor_IheInterface_Edge.class);
        return dialog;
    }

}
