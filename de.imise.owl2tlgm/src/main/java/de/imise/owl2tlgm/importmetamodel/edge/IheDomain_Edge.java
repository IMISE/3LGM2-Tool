package de.imise.owl2tlgm.importmetamodel.edge;

import de.imise.owl2tlgm.importmetamodel.node.Domain;
import de.imise.owl2tlgm.importmetamodel.node.IntegrationProfile;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;

/**
 * @author AXS (9 Jun 2019)
 */
public class IheDomain_Edge extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = Domain.class;

    public static final Class<? extends ModelElement> ETCL = IntegrationProfile.class;

}
