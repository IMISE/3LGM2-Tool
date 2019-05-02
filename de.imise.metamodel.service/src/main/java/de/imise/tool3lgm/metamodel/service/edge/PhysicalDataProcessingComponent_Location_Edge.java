package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.service.node.Location;
import de.imise.tool3lgm.metamodel.service.node.PhysicalDataProcessingComponent;

/**
 * @author AXS (22.12.2017)
 */
public final class PhysicalDataProcessingComponent_Location_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = PhysicalDataProcessingComponent.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_ONE;

    public static final Class<? extends ModelElement> ETCL = Location.class;

}
