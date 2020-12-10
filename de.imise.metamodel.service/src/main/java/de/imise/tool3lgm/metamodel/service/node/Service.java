package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ServiceUses_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ServiceClass_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class Service extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(Service_ServiceClass_Edge.class);
        dialog.addEdgePanel(ServiceUses_Edge.class);
        dialog.addEdgePanel(Service_ObjectType_Edge.class);
        dialog.addMultiPanel(CommunicationInterface.class);
        dialog.addMultiPanelPathPanel(Service_InvokingInterface_Edge.class);
        dialog.addMultiPanelPathPanel(Service_ProvidingInterface_Edge.class);

        // das auskommentierte ist als Marker bzw. evtl. TODO drin geblieben. Siehe Kommentare unten:

        //        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        //        tableDefinition.addColumnPathStepEnd(0, 300);
        //        tableDefinition.addColumnPathStepName(1, "HEADER_ACCESS_TYPE", 300);
        //        tableDefinition.addColumnEndElement(300);
        //        //auch unnvollständige Pfade anzeigen!!! Also auch verbundene Services in der Tabelle anzeigen, die mit keinem Objekttyp verbunden sind
        //        ElementaryMetaPathHandler emph = getElementaryMetaPathHandler();
        //        ElementaryMetaPath service_CommunicationLink_Emp = emph.getStartElementToEdgeMetaPath(Service.class, CommunicationLink_Edge.class);
        //        ElementaryMetaPath comunicationLink_startElement_InvokingInterface = emph.getEdgeToStartElementMetaPath(CommunicationLink_Edge.class);
        //        ElementaryMetaPath InvokingInterface_ApplicationComponent = emph.getMetaPath(ApplicationComponent_CommunicationInterface_Edge.class, Direction.BACKWARD);
        //        ElementaryMetaPath ApplicationComponent = emph.getMetaPath(ApplicationComponent_CommunicationInterface_Edge.class, Direction.FORWARD);
        //        Der eine Pfad müsste zu den "neuen Startelementen" verlaufen also zu allen InvokingInterfaces, die eine Kommunikationsbeziehung besitzen,
        //        über die dieser Service geschickt werden kann. Dann ein zweiter Pfad, der der in der Tabelle angezeigte Pfad ist und zu den AWB mit den
        //        zugehörigen ProvidingInterfaces verläuft
        //        dialog.addTablePanel(tableDefinition, 0, Service_CommunicationLink_Edge.class, Service_ObjectType_Edge.class);

        return dialog;
    }

}
