/*
 * Created on 16.01.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationseinheit;

/**
 * @author Thomas
 */
public final class OrgAufOrgVerbindung extends Edge {

    public static final Class<? extends ModelElement> stcl = AufOrgKombination.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ONE,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = Organisationseinheit.class;

    /**
     *
     */
    public OrgAufOrgVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public OrgAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public OrgAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
