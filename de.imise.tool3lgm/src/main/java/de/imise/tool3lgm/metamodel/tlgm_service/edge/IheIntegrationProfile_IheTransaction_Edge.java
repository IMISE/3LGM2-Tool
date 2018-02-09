package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheTransaction;;

/**
 * @author AXS (31.01.2018)
 */
public class IheIntegrationProfile_IheTransaction_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = IheIntegrationProfile.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = IheTransaction.class;

}
