/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Nachrichtentyp;

/**
 * @author Thomas
 */
public final class EtntNatVerbindung extends Doppelkante {

    //    public static final Class[] stcl = {EreignisNachrichtenTyp.class};
    public static final Class<? extends ModelElement> stcl = EreignisNachrichtenTyp.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.ONE
    };
    public static final Class<? extends ModelElement> etcl = Nachrichtentyp.class;

    //	public static final Class[] etcl = {Nachrichtentyp.class};

    //	private static Object[][] stcl = {{EreignisNachrichtenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{Nachrichtentyp.class, ModelConstants.ZERO, ModelConstants.ONE}}; 

    /**
	 * 
	 */
    public EtntNatVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public EtntNatVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public EtntNatVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
