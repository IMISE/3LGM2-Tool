/*
 * Created on 16.01.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBKonfiguration;

/**
 * @author Thomas
 */
public final class PdvbkAwbVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = Anwendungsbaustein.class;

    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.ONE
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = DBKonfiguration.class;

    /**
     *
     */
    public PdvbkAwbVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public PdvbkAwbVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public PdvbkAwbVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
