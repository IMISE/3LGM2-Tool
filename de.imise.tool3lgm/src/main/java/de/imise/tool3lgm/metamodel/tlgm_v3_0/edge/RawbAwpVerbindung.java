/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

/**
 * @author Thomas
 */
public final class RawbAwpVerbindung extends Composition {

    //    public static final Class[] stcl = {RechAnwendungsbaustein.class};
    public static final Class<? extends ModelElement> stcl = RechAnwendungsbaustein.class;
    //	public static final int[] scard = {ModelConstants.ONE, ModelConstants.ONE};

    public static final int[] ecard = {
            ModelConstants.ONE, ModelConstants.ONE
    };
    public static final Class<? extends ModelElement> etcl = Anwendungsprogramm.class;

    //	public static final Class[] etcl = {Anwendungsprogramm.class};

    //	private static Object[][] stcl = {{RechAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.ONE}}; 
    //	private static Object[][] etcl = {{Anwendungsprogramm.class, ModelConstants.ZERO, ModelConstants.ONE}}; 

    /**
	 * 
	 */
    public RawbAwpVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public RawbAwpVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public RawbAwpVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    //	public boolean isMasterSlave() { return true; }

}
