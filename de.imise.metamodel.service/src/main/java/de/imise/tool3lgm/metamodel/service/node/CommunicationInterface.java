package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PartableApplicationComponent_CommunicationInterface_Edge;

/**
 * @author AXS (26.12.2017)
 */
public abstract class CommunicationInterface extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, PartableApplicationComponent_CommunicationInterface_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnPathStepName(0, 150);
        tableDefinition.addColumnPathStepEnd(0, 300);
        tableDefinition.addColumnPathStepName(1, 150);
        tableDefinition.addColumnEndElement(300);
        dialog.addTablePanel(tableDefinition, 0, CommunicationLink_Edge.class, PartableApplicationComponent_CommunicationInterface_Edge.class);

        return dialog;
    }

}
