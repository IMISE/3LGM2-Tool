package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;

/**
 * @author AXS (24.04.2018)
 */
public abstract class IheInterface extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, IheActor_IheInterface_Edge.class);
        dialog.addDescripPanel(LABEL_END_ELEMENT_TYPE, IheInterface_IheTransaction_Edge.class);
        dialog.addDescripPanel(IheActor_IheInterface_Edge.class, IheIntegrationProfile_IheActor_Edge.class, IheIntegrationProfile_IheDomain_Edge.class);
        dialog.addDescripPanel(IheActor_IheInterface_Edge.class, IheIntegrationProfile_IheActor_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnPathStepBackwardName(0, "HEADER_CONNECTION_TYPE", 150);
        tableDefinition.addColumnEndElement(300);
        dialog.addTablePanel(tableDefinition, 0, IheCommunicationLink_Edge.class);

        return dialog;
    }

}
