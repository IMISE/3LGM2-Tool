package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;

/**
 * @author AXS (17.01.2020)
 */
public class IheCommunicationLink_IheActorInstanceCommunicationLink_Edge extends SimpleEdge implements InferenceEdge {

    public static final Class<? extends ModelElement> STCL = IheCommunicationLink_Edge.class;

    public static final EdgeCardinality SCARD = ZERO_ONE;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = IheActorInstanceCommunicationLink_Edge.class;

}
