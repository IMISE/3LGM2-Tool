/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;

/**
 * @author Thomas
 */
public final class OrgAufOrgVerbindung extends Doppelkante {

    //	public static final Class[] stcl = {AufOrgKombination.class};
    public static final Class<? extends ModelElement> stcl = AufOrgKombination.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ONE, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Organisationseinheit.class;

    //	public static final Class[] etcl = {Organisationseinheit.class};

    //	private static Object[][] stcl = {{AufOrgKombination.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{Organisationseinheit.class, ModelConstants.ONE, ModelConstants.UNLIMITED}}; 

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

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

}
