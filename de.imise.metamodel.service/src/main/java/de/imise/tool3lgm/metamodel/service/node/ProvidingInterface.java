package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.metamodel.service.edge.ObjectType_RepresentationForm_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_RepresentationForm_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class ProvidingInterface extends CommunicationInterface {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Service_ProvidingInterface_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.tablePanelLabelOption = PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE;
        tableDefinition.addColumnPathStepEnd(0, 300);
        tableDefinition.addColumnPathStepName(1, "HEADER_ACCESS_TYPE", 300);
        tableDefinition.addColumnPathStepEnd(1, 300);
        tableDefinition.addColumnPathStepName(2, 300);
        tableDefinition.addColumnEndElement(300);
        //auch unnvollständige Pfade anzeigen!!! Also auch verbundene Services in der Tabelle anzeigen, die mit keinem Objekttyp verbunden sind
        dialog.addTablePanel(tableDefinition, 0, Service_ProvidingInterface_Edge.class, Service_RepresentationForm_Edge.class, ObjectType_RepresentationForm_Edge.class);

        return dialog;
    }

}
