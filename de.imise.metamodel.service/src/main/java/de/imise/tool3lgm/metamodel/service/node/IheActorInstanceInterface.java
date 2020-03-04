package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstanceCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_IheActorInstanceInterface_Edge;

/**
 * @author AXS (26.12.2017)
 */
public abstract class IheActorInstanceInterface extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, IheActorInstance_IheActorInstanceInterface_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnPathStepEdge(0, 300);
        tableDefinition.addColumnPathStepName(0, 150);
        tableDefinition.addColumnPathStepEnd(0, 300);
        //        tableDefinition.addColumnPathStepName(1, 150);
        //        tableDefinition.addColumnEndElement(300);
        dialog.addTablePanel(tableDefinition, 0,
                IheActorInstanceCommunicationLink_Edge.class/* , IheActorInstance_IheActorInstanceInterface_Edge.class */);

        return dialog;
    }

}
