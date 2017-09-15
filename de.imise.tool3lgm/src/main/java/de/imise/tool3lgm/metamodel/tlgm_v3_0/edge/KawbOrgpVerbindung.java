/*
 * Created on 16.01.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.metamodel.Composition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationsplan;

/**
 * @author Thomas
 */
public final class KawbOrgpVerbindung extends Composition {

    public static final Class<? extends ModelElement> stcl = KonAnwendungsbaustein.class;
    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.ONE
    };

    public static final int[] ecard = {
            ModelConstants.ONE,
            ModelConstants.ONE
    };
    public static final Class<? extends ModelElement> etcl = Organisationsplan.class;

    /**
     * 
     */
    public KawbOrgpVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public KawbOrgpVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public KawbOrgpVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
