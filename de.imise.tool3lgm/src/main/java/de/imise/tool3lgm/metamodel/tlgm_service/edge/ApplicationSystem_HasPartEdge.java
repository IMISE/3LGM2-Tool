package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.PartitioningEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationSystem;

/**
 * @author AXS (05.02.2019)
 */
public final class ApplicationSystem_HasPartEdge extends PartitioningEdge {

    public static final Class<? extends ModelElement> stcl = ApplicationSystem.class;

    public static final EdgeCardinality scard = ZERO_ONE;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = ApplicationSystem.class;

}