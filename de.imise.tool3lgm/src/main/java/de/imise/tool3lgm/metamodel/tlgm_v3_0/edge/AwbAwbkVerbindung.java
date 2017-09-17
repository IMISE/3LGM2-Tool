package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_UNIMITED;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;

/**
 * @author Thomas (16.01.2004)
 */
public final class AwbAwbkVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = ABKonfiguration.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ONE_UNIMITED;

    public static final Class<? extends ModelElement> etcl = Anwendungsbaustein.class;

}