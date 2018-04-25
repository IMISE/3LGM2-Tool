package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.InvokingInterface;

/**
 * @author AXS (24.04.2018)
 */
public class IheInvokingInterface_InvokingInterface_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = IheInvokingInterface.class;

    public static final EdgeCardinality scard = ZERO_ONE;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = InvokingInterface.class;

}