package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableColumnsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheProvidingInterface_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheTransaction extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTabbedPanel(ElementsNameBuilder.getDisplayablePluralName(IheInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class);

        ConnectedElementsTableColumnsDefinition columnsDefinition = new ConnectedElementsTableColumnsDefinition();
        columnsDefinition.addColumnEndElement(300);
        columnsDefinition.addColumnOptional(1, 50);
        columnsDefinition.addColumnPathStepBackwardName(0, "HEADER_CONNECTION_TYPE", 150);
        SimpleMetaPath path1 = createSimpleMetaPath(0, IheInvokingInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class);
        SimpleMetaPath path2 = createSimpleMetaPath(0, IheProvidingInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class);
        dialog.addTablePanel(true, columnsDefinition, path1, path2);

        return dialog;
    }

}