package de.imise.tool3lgm.metamodel.original.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SimpleEdge;
import de.imise.tool3lgm.metamodel.original.node.LogischerSpeicher;
import de.imise.tool3lgm.metamodel.original.node.Repraesentationsform;

/**
 * @author AXS (27.01.2021)
 */
public abstract class LogspReprVerbindung extends SimpleEdge {

    public static final Class<? extends ModelElement> STCL = LogischerSpeicher.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = Repraesentationsform.class;

}
