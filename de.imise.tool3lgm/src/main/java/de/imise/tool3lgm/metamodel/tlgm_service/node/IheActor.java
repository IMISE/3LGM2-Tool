package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableColumnsDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheActor extends IheConcept {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(false, IheIntegrationProfile_IheActor_Edge.class);
        dialog.addEdgePanel(IheActor_IheActorInstance_Edge.class);
        //dieses tabbedPane wird nur im ExpertMode angezeigt! Und damit auch die darin enthaltenen Interface-Panels
        dialog.addTabbedPanel(ElementsNameBuilder.getDisplayablePluralName(IheInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheActor_IheInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheActor_IheInterface_Edge.class);

        ConnectedElementsTableColumnsDefinition columnsDefinition = new ConnectedElementsTableColumnsDefinition();
        columnsDefinition.addColumnEndElement(300);
        columnsDefinition.addColumnOptional(0, 50);
        columnsDefinition.addColumnPathStepName(1, "HEADER_ACTOR_TRANSACTION_CONNECTION_NAME", 150);
        SimpleMetaPath path1 = createSimpleMetaPath(IheActor_IheInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class);
        SimpleMetaPath path2 = createSimpleMetaPath(IheActor_IheInterface_Edge.class, IheProvidingInterface_IheTransaction_Edge.class);
        dialog.addTablePanel(true, columnsDefinition, path1, path2);
        return dialog;
    }

}
