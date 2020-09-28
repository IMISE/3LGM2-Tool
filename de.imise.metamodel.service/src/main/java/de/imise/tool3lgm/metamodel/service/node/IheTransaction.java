package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheTransaction extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(IheInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class, IheIntegrationProfile_IheActor_Edge.class, IheIntegrationProfile_IheDomain_Edge.class);
        dialog.addDescripPanel(IheInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class, IheIntegrationProfile_IheActor_Edge.class);

        dialog.addTabbedPanel(IheInterface.class);
        dialog.addTabbedPanelPathConnectionPanel(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnEndElement(300);
        tableDefinition.addColumnOptional(1, 50);
        tableDefinition.addColumnPathStepBackwardName(0, "HEADER_CONNECTION_TYPE", 150);

        //die unteren 2 Zeilen sind abslut identisch zu dem, was hier auskommentiert ist. Hier werden beide Pfade explizit definiert und dem TablePanel übergeben. Unten werden
        //dieselben beiden Pfade aus dem abstrakten Pfad abgeleitet. Das ist nur als Vorlage hier enthalten, damit man die verschiedenen Möglichkeiten zur Definition sieht.
        //        SimpleMetaPath path1 = createSimpleMetaPath(0, IheInvokingInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class);
        //        SimpleMetaPath path2 = createSimpleMetaPath(0, IheProvidingInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class);
        //        dialog.addTablePanel(true, tableDefinition, path1, path2);
        dialog.addTablePanel(tableDefinition, 0, IheInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class);

        return dialog;
    }

}