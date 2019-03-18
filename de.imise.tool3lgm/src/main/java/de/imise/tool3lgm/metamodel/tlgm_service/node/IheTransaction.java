package de.imise.tool3lgm.metamodel.tlgm_service.node;

import static de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator.createSimpleMetaPath;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableColumnsDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheTransaction extends IheConcept {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTabbedPanel(ElementsNameBuilder.getDisplayablePluralName(IheInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class);

        ConnectedElementsTableColumnsDefinition columnsDefinition = new ConnectedElementsTableColumnsDefinition();
        columnsDefinition.addColumnEndElement(300);
        columnsDefinition.addColumnOptional(1, 50);
        columnsDefinition.addColumnPathStepName(0, "HEADER_ACTOR_TRANSACTION_CONNECTION_NAME", 150);
        SimpleMetaPath path1 = createSimpleMetaPath(IheActor.class, IheTransaction.class, "PATH_IHE_ACTOR_IHE_INVOKING_INTERFACE_IHE_TRANSACTION", IheActor_IheInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class).getOtherDirection();
        SimpleMetaPath path2 = createSimpleMetaPath(IheActor.class, IheTransaction.class, "PATH_IHE_ACTOR_IHE_PROVIDING_INTERFACE_IHE_TRANSACTION", IheActor_IheInterface_Edge.class, IheProvidingInterface_IheTransaction_Edge.class).getOtherDirection();
        dialog.addTablePanel(true, columnsDefinition, path1, path2);

        dialog.addEdgePanel(IheIntegrationProfile_IheTransaction_Edge.class);
        return dialog;
    }

}