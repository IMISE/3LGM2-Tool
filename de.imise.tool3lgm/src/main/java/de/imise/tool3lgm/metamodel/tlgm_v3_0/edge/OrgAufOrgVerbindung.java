package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_UNLIMITED;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationseinheit;

/**
 * @author Thomas (16.01.2004)
 */
public final class OrgAufOrgVerbindung extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = AufOrgKombination.class;

    public static final EdgeCardinality scard = ZERO_UNLIMITED;

    public static final EdgeCardinality ecard = ONE_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = Organisationseinheit.class;

}
