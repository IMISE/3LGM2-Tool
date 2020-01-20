package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.service.node.IheTransaction;

/**
 * @author AXS (14.11.2019)
 */
public final class IheTransaction_IheCommunicationLink_Edge extends SimpleEdge implements InferenceEdge {

    public static final Class<? extends ModelElement> STCL = IheTransaction.class;

    public static final EdgeCardinality SCARD = ZERO_ONE;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = IheCommunicationLink_Edge.class;

}