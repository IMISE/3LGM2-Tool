package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.PartableApplicationComponent_CommunicationInterface_Edge;

/**
 * @author AXS (03.03.2020)
 */
public abstract class PartableApplicationComponent extends ApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addMultiPanel(CommunicationInterface.class);
        dialog.addMultiPanelPathPanel(InvokingInterface.class, PartableApplicationComponent_CommunicationInterface_Edge.class);
        dialog.addMultiPanelPathPanel(ProvidingInterface.class, PartableApplicationComponent_CommunicationInterface_Edge.class);
        return dialog;
    }

}
