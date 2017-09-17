package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNIMITED;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

/**
 * @author Thomas (16.01.2004)
 */
public final class RawbRawbVerbindung extends PartOfBeziehung {

    public static final Class<? extends ModelElement> stcl = RechAnwendungsbaustein.class;

    public static final EdgeCardinality scard = ZERO_UNIMITED;

    public static final EdgeCardinality ecard = ZERO_UNIMITED;

    public static final Class<? extends ModelElement> etcl = RechAnwendungsbaustein.class;

    public RawbRawbVerbindung() {
    }

    public RawbRawbVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    public RawbRawbVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
