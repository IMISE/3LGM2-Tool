package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;

import de.imise.tool3lgm.graphtools.metamodel.Composition;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

/**
 * @author Thomas (16.01.2004)
 */
public final class RawbAwpVerbindung extends Composition {

    public static final Class<? extends ModelElement> stcl = RechAnwendungsbaustein.class;

    public static final EdgeCardinality ecard = ONE_ONE;

    public static final Class<? extends ModelElement> etcl = Anwendungsprogramm.class;

    public RawbAwpVerbindung() {
    }

    public RawbAwpVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    public RawbAwpVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
