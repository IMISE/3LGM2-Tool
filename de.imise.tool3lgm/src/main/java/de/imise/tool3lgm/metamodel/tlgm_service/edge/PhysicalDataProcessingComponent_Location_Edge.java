package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Location;
import de.imise.tool3lgm.metamodel.tlgm_service.node.PhysicalDataProcessingComponent;

/**
 * @author AXS (22.12.2017)
 */
public final class PhysicalDataProcessingComponent_Location_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = PhysicalDataProcessingComponent.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_ONE;

    public static final Class<? extends ModelElement> etcl = Location.class;

}
