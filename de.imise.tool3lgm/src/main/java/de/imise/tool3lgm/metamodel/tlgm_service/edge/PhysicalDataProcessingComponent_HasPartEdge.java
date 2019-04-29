package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.PartitioningEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.PhysicalDataProcessingComponent;

/**
 * @author AXS (22.12.2017)
 */
public final class PhysicalDataProcessingComponent_HasPartEdge extends PartitioningEdge {

    public static final Class<? extends ModelElement> STCL = PhysicalDataProcessingComponent.class;

    public static final EdgeCardinality SCARD = ZERO_ONE;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = PhysicalDataProcessingComponent.class;

}