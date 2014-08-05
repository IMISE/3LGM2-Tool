/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsstandard;

/**
 * @author Thomas To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class BssKommstVerbindung extends Doppelkante {

    //	public static final Class[] stcl = {Bausteinschnittstelle.class};
    public static final Class<? extends ModelElement> stcl = Bausteinschnittstelle.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.ONE
    };
    public static final Class<? extends ModelElement> etcl = Kommunikationsstandard.class;

    //	public static final Class[] etcl = {Kommunikationsstandard.class};

    //	private static Object[][] stcl = {{Bausteinschnittstelle.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{Kommunikationsstandard.class, ModelConstants.ZERO, ModelConstants.ONE}}; 

    /**
	 * 
	 */
    public BssKommstVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public BssKommstVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public BssKommstVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
