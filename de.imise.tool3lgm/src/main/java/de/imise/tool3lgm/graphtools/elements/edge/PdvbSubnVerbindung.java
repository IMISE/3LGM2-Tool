/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.elements.node.Subnetz;

/**
 * @author Thomas
 */
public final class PdvbSubnVerbindung extends Doppelkante {

    //    public static final Class[] stcl = {PhysischerDVBaustein.class};
    public static final Class<? extends ModelElement> stcl = PhysischerDVBaustein.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Subnetz.class;

    //	public static final Class[] etcl = {Subnetz.class};

    //	private static Object[][] stcl = {{PhysischerDVBaustein.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{Subnetz.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 

    /**
	 * 
	 */
    public PdvbSubnVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public PdvbSubnVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public PdvbSubnVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.PHYSICAL_LAYER;
    }

}
