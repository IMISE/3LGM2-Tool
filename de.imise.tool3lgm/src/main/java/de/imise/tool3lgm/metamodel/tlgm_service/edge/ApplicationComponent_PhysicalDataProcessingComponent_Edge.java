package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.tlgm_service.node.PhysicalDataProcessingComponent;

/**
 * @author AXS (29.01.2018)
 */
public abstract class ApplicationComponent_PhysicalDataProcessingComponent_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = ApplicationComponent.class;

    public static final EdgeCardinality scard = ZERO_UNLIMITED;

    public static final EdgeCardinality ecard = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> etcl = PhysicalDataProcessingComponent.class;

}
