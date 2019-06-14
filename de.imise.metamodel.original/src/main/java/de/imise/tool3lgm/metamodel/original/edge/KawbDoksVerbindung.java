package de.imise.tool3lgm.metamodel.original.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.original.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.original.node.KonAnwendungsbaustein;

/**
 * @author Thomas (16.01.2004)
 */
public final class KawbDoksVerbindung extends CompositionEdge {

    public static final Class<? extends ModelElement> STCL = KonAnwendungsbaustein.class;

    public static final EdgeCardinality ECARD = ZERO_ONE;

    public static final Class<? extends ModelElement> ETCL = Dokumentensammlung.class;

}
