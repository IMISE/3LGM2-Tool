package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.BooleanAttributeEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ProvidingInterface;

/**
 * @author AXS (11.01.2017)
 */
public final class CommunicationLinkEdge extends BooleanAttributeEdge {

    public static final Class<? extends ModelElement> stcl = InvokingInterface.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = ProvidingInterface.class;

    public CommunicationLinkEdge() {
        super("CommunicationLinkEdge_executionDepending_Attribute");
    }

}
