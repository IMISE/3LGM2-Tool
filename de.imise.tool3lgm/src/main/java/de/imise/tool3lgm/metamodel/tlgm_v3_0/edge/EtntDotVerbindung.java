package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;
import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Dokumententyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EreignisDokumentenTyp;

/**
 * @author Thomas (16.01.2004)
 */
public final class EtntDotVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = EreignisDokumentenTyp.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_ONE;

    public static final Class<? extends ModelElement> etcl = Dokumententyp.class;

}
