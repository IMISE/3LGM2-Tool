/*
 * Created on 16.01.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;

/**
 * @author Thomas
 */
public final class PdvbPdvbkVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = PhysischerDVBaustein.class;

    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = DBKonfiguration.class;

    /**
     *
     */
    public PdvbPdvbkVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public PdvbPdvbkVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public PdvbPdvbkVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
