package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.service.node.ObjectType;
import de.imise.tool3lgm.metamodel.service.node.Service;

/**
 * @author AXS (31.12.2017)
 */
public final class Service_ObjectType_Edge extends DoubleMeaningEdge {

    public static final Class<? extends ModelElement> STCL = Service.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = ObjectType.class;

}
