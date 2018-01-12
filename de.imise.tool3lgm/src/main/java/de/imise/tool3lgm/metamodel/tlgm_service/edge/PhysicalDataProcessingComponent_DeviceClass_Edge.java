package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.node.DeviceClass;
import de.imise.tool3lgm.metamodel.tlgm_service.node.PhysicalDataProcessingComponent;

/**
 * @author AXS (26.12.2017)
 */
public final class PhysicalDataProcessingComponent_DeviceClass_Edge extends Edge {

    public static final Class<? extends ModelElement> stcl = PhysicalDataProcessingComponent.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_ONE;

    public static final Class<? extends ModelElement> etcl = DeviceClass.class;

}
