package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInterface_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheDomain extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(IheIntegrationProfile_IheDomain_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.setTableResKeyOrName("TABLE_NAME_OVERVIEW");
        tableDefinition.addColumnPathStepEnd(0, 300);
        tableDefinition.addColumnPathStepEnd(1, 300);
        tableDefinition.addColumnPathStepName(3, "HEADER_CONNECTION_TYPE", 150);
        tableDefinition.addColumnEndElement(300);
        tableDefinition.addColumnOptional(2, 50);
        dialog.addTablePanel(tableDefinition, 3, IheIntegrationProfile_IheDomain_Edge.class, IheIntegrationProfile_IheActor_Edge.class, IheActor_IheInterface_Edge.class, IheInterface_IheTransaction_Edge.class);

        return dialog;
    }

}
