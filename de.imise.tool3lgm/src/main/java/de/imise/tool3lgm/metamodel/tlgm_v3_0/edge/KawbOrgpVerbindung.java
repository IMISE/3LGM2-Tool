/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationsplan;

/**
 * @author Thomas
 */
public final class KawbOrgpVerbindung extends Composition {

    //    public static final Class[] stcl = {KonAnwendungsbaustein.class};
    public static final Class<? extends ModelElement> stcl = KonAnwendungsbaustein.class;
    public static final int[] scard = {
            ModelConstants.ONE, ModelConstants.ONE
    };

    public static final int[] ecard = {
            ModelConstants.ONE, ModelConstants.ONE
    };
    public static final Class<? extends ModelElement> etcl = Organisationsplan.class;

    //	public static final Class[] etcl = {Organisationsplan.class};

    //	private static Object[][] stcl = {{KonAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.ONE}}; 
    //	private static Object[][] etcl = {{Organisationsplan.class, ModelConstants.ZERO, ModelConstants.ONE}}; 

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

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
