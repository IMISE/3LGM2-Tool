package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActor;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheInterface;

/**
 * @author AXS (24.04.2018)
 */
public class IheActor_IheInterface_Edge extends CompositionEdge implements OptionalEdge {

    public static final Class<? extends ModelElement> stcl = IheActor.class;

    public static final EdgeCardinality ecard = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = IheInterface.class;

}
