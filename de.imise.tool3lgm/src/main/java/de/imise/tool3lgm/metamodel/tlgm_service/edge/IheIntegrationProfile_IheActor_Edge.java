package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActor;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheIntegrationProfile;

/**
 * @author AXS (31.01.2018)
 */
public class IheIntegrationProfile_IheActor_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = IheIntegrationProfile.class;

    public static final EdgeCardinality scard = ZERO_ONE;

    public static final EdgeCardinality ecard = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = IheActor.class;

}
