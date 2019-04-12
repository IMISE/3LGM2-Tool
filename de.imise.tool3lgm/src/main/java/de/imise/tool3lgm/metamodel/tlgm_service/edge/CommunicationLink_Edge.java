package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.BooleanAttributeEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ProvidingInterface;

/**
 * @author AXS (11.01.2017)
 */
public final class CommunicationLink_Edge extends BooleanAttributeEdge {

    public static final Class<? extends ModelElement> stcl = InvokingInterface.class;

    public static final EdgeCardinality scard = ZERO_UNLIMITED;

    public static final EdgeCardinality ecard = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = ProvidingInterface.class;

    public CommunicationLink_Edge() {
        super("CommunicationLinkEdge_executionDepending_Attribute");
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Service_CommunicationLink_Edge.class);
        dialog.addEdgePanel(ApplicationComponent_CommunicationLink_Edge.class);
        return dialog;
    }

}
