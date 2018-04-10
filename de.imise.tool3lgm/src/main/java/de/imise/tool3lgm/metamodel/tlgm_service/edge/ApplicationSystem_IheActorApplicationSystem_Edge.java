package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActorApplicationSystem;

public class ApplicationSystem_IheActorApplicationSystem_Edge extends CompositionEdge {

    public static final Class<? extends ModelElement> stcl = ApplicationSystem.class;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = IheActorApplicationSystem.class;

}
