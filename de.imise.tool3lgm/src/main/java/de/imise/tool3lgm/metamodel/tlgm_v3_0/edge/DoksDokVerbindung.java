/*
 * Created on 16.01.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Dokumententyp;

/**
 * @author Thomas
 */
public final class DoksDokVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = Dokumentensammlung.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = Dokumententyp.class;

    /**
     *
     */
    public DoksDokVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public DoksDokVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public DoksDokVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
