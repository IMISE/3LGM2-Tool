package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SubordinationEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActorApplicationSystem;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActorOfIntegrationProfile;

/**
 * @author AXS (31.01.2018)
 */
public class IheActorApplicationSystem_IheActorOfIntegrationProfile_Edge extends SubordinationEdge {

    public static final Class<? extends ModelElement> stcl = IheActorApplicationSystem.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ONE_ONE;

    public static final Class<? extends ModelElement> etcl = IheActorOfIntegrationProfile.class;

}
