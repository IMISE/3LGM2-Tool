package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Service;

/**
 * @author AXS (11.01.2018)
 */
public final class ServiceToProvidingInterfaceEdge extends Edge {

    public static final Class<? extends ModelElement> stcl = Service.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = InvokingInterface.class;

}
