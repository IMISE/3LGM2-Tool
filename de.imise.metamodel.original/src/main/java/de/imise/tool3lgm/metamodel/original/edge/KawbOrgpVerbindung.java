package de.imise.tool3lgm.metamodel.original.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.original.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Organisationsplan;

/**
 * @author Thomas (16.01.2004)
 */
public final class KawbOrgpVerbindung extends CompositionEdge {

    public static final Class<? extends ModelElement> STCL = KonAnwendungsbaustein.class;

    public static final EdgeCardinality ECARD = ONE_ONE;

    public static final Class<? extends ModelElement> ETCL = Organisationsplan.class;

}
