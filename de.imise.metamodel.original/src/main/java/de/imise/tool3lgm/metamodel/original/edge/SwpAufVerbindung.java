package de.imise.tool3lgm.metamodel.original.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Softwareprodukt;

/**
 * @author Thomas (16.01.2004)
 */
public final class SwpAufVerbindung extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = Softwareprodukt.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Aufgabe.class;

}
