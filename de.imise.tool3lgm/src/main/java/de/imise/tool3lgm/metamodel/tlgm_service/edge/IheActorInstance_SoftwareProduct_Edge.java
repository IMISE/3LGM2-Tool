package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.tlgm_service.node.SoftwareProduct;

/**
 * @author AXS (07.01.2018)
 */
public final class IheActorInstance_SoftwareProduct_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = IheActorInstance.class;

    public static final EdgeCardinality scard = ZERO_UNLIMITED;

    public static final EdgeCardinality ecard = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = SoftwareProduct.class;

}
