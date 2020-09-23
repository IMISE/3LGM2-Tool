package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.Service_InvokingInterface_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class InvokingInterface extends CommunicationInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Service_InvokingInterface_Edge.class);
        return dialog;
    }

}
