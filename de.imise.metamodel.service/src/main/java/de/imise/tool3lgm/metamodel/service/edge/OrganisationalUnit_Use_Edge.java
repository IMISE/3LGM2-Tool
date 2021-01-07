package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_UNLIMITED;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.service.node.OrganisationalUnit;
import de.imise.tool3lgm.metamodel.service.node.Use;

/**
 * @author AXS (31.12.2017)
 */
public final class OrganisationalUnit_Use_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = OrganisationalUnit.class;

    public static final EdgeCardinality SCARD = ONE_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Use.class;

}
