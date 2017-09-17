package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.MultipleEdge;
import de.imise.tool3lgm.graphtools.metamodel.SortedEdge;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Prozess;

/**
 * @author Thomas (16.01.2004)
 */
public final class PrzAufVerbindung extends Edge implements SortedEdge, MultipleEdge {

    public static final Class<? extends ModelElement> stcl = Prozess.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = Aufgabe.class;

}
