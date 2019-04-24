package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.CommunicationInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheInterface;

/**
 * @author AXS (24.04.2018)
 */
public abstract class IheInterface_CommunicationInterface_Edge extends InstanciationEdge {

    public static final Class<? extends ModelElement> STCL = IheInterface.class;

    public static final EdgeCardinality SCARD = ZERO_ONE;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = CommunicationInterface.class;

}