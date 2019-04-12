package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.PartitioningEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.OrganisationalUnit;

/**
 * @author AXS (31.12.2017)
 */
public final class OrganisationalUnit_HasPartEdge extends PartitioningEdge {

    public static final Class<? extends ModelElement> stcl = OrganisationalUnit.class;

    public static final EdgeCardinality scard = ZERO_ONE;

    public static final EdgeCardinality ecard = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = OrganisationalUnit.class;

}