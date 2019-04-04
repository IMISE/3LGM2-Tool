package de.imise.tool3lgm.metamodel.tlgm_service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheTransaction;;

/**
 * @author AXS (05.02.2019)
 */
public abstract class IheInterface_IheTransaction_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> stcl = IheInterface.class;

    public static final EdgeCardinality scard = ZERO_UNLIMITED;

    public static final EdgeCardinality ecard = ONE_ONE;

    public static final Class<? extends ModelElement> etcl = IheTransaction.class;

}
