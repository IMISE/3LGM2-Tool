package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_ONE;

import de.imise.tool3lgm.graphtools.metamodel.Composition;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

/**
 * @author Thomas (16.01.2004)
 */
public final class RawbDbsVerbindung extends Composition {

    public static final Class<? extends ModelElement> stcl = RechAnwendungsbaustein.class;

    public static final EdgeCardinality ecard = ZERO_ONE;

    public static final Class<? extends ModelElement> etcl = Datenbanksystem.class;

    public RawbDbsVerbindung() {
    }

    public RawbDbsVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    public RawbDbsVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
