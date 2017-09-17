package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Nachrichtentyp;

/**
 * @author Thomas (16.01.2004)
 */
public final class EtntNatVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = EreignisNachrichtenTyp.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_ONE;

    public static final Class<? extends ModelElement> etcl = Nachrichtentyp.class;

}
