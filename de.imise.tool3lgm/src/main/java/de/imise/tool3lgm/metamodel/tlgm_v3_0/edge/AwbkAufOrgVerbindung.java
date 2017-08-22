/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.AufOrgKombination;

/**
 * @author Thomas To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class AwbkAufOrgVerbindung extends Edge {

    //	public static final Class[] stcl = {AufOrgKombination.class};
    public static final Class<? extends ModelElement> stcl = AufOrgKombination.class;

    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.ONE
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = ABKonfiguration.class;

    //	public static final Class[] etcl = {ABKonfiguration.class};

    //	private static Object[][] stcl = {{AufOrgKombination.class, ModelConstants.ONE, ModelConstants.ONE}};
    //	private static Object[][] etcl = {{ABKonfiguration.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    /**
     * 
     */
    public AwbkAufOrgVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public AwbkAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public AwbkAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER;
    }

}
