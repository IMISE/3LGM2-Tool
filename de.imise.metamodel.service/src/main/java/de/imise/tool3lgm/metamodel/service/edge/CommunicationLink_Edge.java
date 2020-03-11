package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.BooleanAttributeEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;

/**
 * @author AXS (11.01.2017)
 */
public final class CommunicationLink_Edge extends BooleanAttributeEdge {

    public static final Class<? extends ModelElement> STCL = InvokingInterface.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = ProvidingInterface.class;

    public CommunicationLink_Edge() {
        super("CommunicationLinkEdge_executionDepending_Attribute");
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Service_CommunicationLink_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnPathStepEnd(0, 300);
        tableDefinition.addColumnPathStepName(1, "HEADER_ACCESS_TYPE", 300);
        tableDefinition.addColumnEndElement(300);
        //auch unnvollständige Pfade anzeigen!!! Also auch verbundene Services in der Tabelle anzeigen, die mit keinem Objekttyp verbunden sind
        dialog.addTablePanel(tableDefinition, 0, Service_CommunicationLink_Edge.class, Service_ObjectType_Edge.class);

        return dialog;
    }

}
