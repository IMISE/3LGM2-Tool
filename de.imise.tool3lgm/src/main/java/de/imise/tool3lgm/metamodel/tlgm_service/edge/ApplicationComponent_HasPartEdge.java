package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModularisationEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationComponent;

/**
 * @author AXS (26.12.2017)
 */
public final class ApplicationComponent_HasPartEdge extends ModularisationEdge {

    public static final Class<? extends ModelElement> stcl = ApplicationComponent.class;

    public static final EdgeCardinality scard = ZERO_ONE;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = ApplicationComponent.class;

}