package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheIntegrationProfile extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(IheIntegrationProfile_IheDomain_Edge.class);
        dialog.addEdgePanel(IheIntegrationProfile_IheActor_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.setTableResKeyOrName("TABLE_NAME_OVERVIEW");
        tableDefinition.addColumnPathStepEnd(0, 300);
        tableDefinition.addColumnPathStepName(2, "HEADER_CONNECTION_TYPE", 150);
        tableDefinition.addColumnEndElement(300);
        tableDefinition.addColumnOptional(1, 50);
        SimpleMetaPath path1 = createSimpleMetaPath(2, IheIntegrationProfile_IheActor_Edge.class, IheActor_IheInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class);
        SimpleMetaPath path2 = createSimpleMetaPath(2, IheIntegrationProfile_IheActor_Edge.class, IheActor_IheInterface_Edge.class, IheProvidingInterface_IheTransaction_Edge.class);
        dialog.addTablePanel(tableDefinition, path1, path2);

        return dialog;
    }

}
