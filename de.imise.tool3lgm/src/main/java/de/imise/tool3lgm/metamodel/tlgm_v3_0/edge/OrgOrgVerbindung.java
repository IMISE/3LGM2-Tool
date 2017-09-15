/*
 * Created on 06.04.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationseinheit;

/**
 * @author AXS
 */
public final class OrgOrgVerbindung extends PartOfBeziehung {

    public static final Class<? extends ModelElement> stcl = Organisationseinheit.class;
    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Organisationseinheit.class;

    /**
     * 
     */
    public OrgOrgVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public OrgOrgVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public OrgOrgVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
