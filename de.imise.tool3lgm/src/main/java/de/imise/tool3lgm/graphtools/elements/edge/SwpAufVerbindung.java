/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;

/**
 * @author Thomas
 */
public final class SwpAufVerbindung extends Doppelkante {

    //    public static final Class[] stcl = {Softwareprodukt.class};
    public static final Class<? extends ModelElement> stcl = Softwareprodukt.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Aufgabe.class;

    //	public static final Class[] etcl = {Aufgabe.class};

    //	private static Object[][] stcl = {{Softwareprodukt.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{Aufgabe.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 

    /**
	 * 
	 */
    public SwpAufVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public SwpAufVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public SwpAufVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER;
    }

}
