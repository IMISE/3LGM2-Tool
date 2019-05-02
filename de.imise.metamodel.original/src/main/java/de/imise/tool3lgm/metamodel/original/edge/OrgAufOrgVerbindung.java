package de.imise.tool3lgm.metamodel.original.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_UNLIMITED;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.original.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.original.node.Organisationseinheit;

/**
 * @author Thomas (16.01.2004)
 */
public final class OrgAufOrgVerbindung extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = AufOrgKombination.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ONE_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Organisationseinheit.class;

}
