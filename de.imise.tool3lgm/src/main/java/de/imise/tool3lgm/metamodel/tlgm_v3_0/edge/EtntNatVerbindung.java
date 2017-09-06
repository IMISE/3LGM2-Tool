/*
 * Created on 16.01.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Nachrichtentyp;

/**
 * @author Thomas
 */
public final class EtntNatVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = EreignisNachrichtenTyp.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.ONE
    };

    public static final Class<? extends ModelElement> etcl = Nachrichtentyp.class;

    /**
     *
     */
    public EtntNatVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public EtntNatVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public EtntNatVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
