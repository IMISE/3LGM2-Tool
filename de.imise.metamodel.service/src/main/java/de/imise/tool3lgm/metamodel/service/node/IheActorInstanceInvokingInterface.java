package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheActorInstanceInvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class IheActorInstanceInvokingInterface extends IheActorInstanceInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class);
        dialog.addDescripPanel(IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class);
        return dialog;
    }

}
