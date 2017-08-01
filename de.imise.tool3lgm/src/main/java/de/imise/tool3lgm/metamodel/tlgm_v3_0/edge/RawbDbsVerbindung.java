/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

/**
 * @author Thomas
 */
public final class RawbDbsVerbindung extends Composition {

    //    public static final Class[] stcl = {RechAnwendungsbaustein.class};
    public static final Class<? extends ModelElement> stcl = RechAnwendungsbaustein.class;
    //	public static final int[] scard = {ModelConstants.ONE, ModelConstants.ONE};

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.ONE
    };
    public static final Class<? extends ModelElement> etcl = Datenbanksystem.class;

    //	public static final Class[] etcl = {Datenbanksystem.class};

    //	private static Object[][] stcl = {{RechAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.ONE}}; 
    //	private static Object[][] etcl = {{Datenbanksystem.class, ModelConstants.ZERO, ModelConstants.ONE}}; 

    /**
	 * 
	 */
    public RawbDbsVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public RawbDbsVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public RawbDbsVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    //	public boolean isMasterSlave() { return true; }

}
