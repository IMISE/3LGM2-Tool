package de.imise.tool3lgm.metamodel.service.edge;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.service.node.IheProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;

/**
 * @author AXS (24.04.2018)
 */
public class IheProvidingInterface_ProvidingInterface_Edge extends IheInterface_CommunicationInterface_Edge {

    public static final Class<? extends ModelElement> STCL = IheProvidingInterface.class;

    public static final Class<? extends ModelElement> ETCL = ProvidingInterface.class;

}