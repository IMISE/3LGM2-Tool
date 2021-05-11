package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheActorInstanceProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;

/**
 * @author AXS (26.12.2017)
 */
public class IheActorInstanceProvidingInterface extends IheActorInstanceInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class);
        dialog.addDescripPanel(IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class, IheProvidingInterface_IheTransaction_Edge.class);
        return dialog;
    }

}
