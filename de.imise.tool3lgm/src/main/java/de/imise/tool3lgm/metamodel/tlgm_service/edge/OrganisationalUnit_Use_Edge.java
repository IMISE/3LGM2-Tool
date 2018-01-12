package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.OrganisationalUnit;

/**
 * @author AXS (31.12.2017)
 */
public final class OrganisationalUnit_Use_Edge extends Edge {

    public static final Class<? extends ModelElement> stcl = OrganisationalUnit.class;

    public static final EdgeCardinality scard = ONE_ONE;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = OrganisationalUnit.class;

}
