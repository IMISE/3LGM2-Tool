/*
 * Created on 06.04.2004 To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;

/**
 * @author AXS
 */
public final class OrgOrgVerbindung extends PartOfBeziehung {

    //    public static final Class[] stcl = {Organisationseinheit.class};
    public static final Class<? extends ModelElement> stcl = Organisationseinheit.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Organisationseinheit.class;

    //	public static final Class[] etcl = {Organisationseinheit.class};

    //	private static Object[][] stcl = {{Organisationseinheit.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{Organisationseinheit.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 

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

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

}
