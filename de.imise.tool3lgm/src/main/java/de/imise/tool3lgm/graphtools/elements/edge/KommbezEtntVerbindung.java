/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;

/**
 * @author Thomas
 */
public final class KommbezEtntVerbindung extends Doppelkante {

    //    public static final Class[] stcl = {KommBeziehung.class};
    public static final Class<? extends ModelElement> stcl = KommBeziehung.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = EtntEtdtKombination.class;

    //	public static final Class[] etcl = {EreignisNachrichtenTyp.class, EreignisDokumentenTyp.class};

    //	private static Object[][] stcl = {{KommBeziehung.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{EreignisNachrichtenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED},
    //									{EreignisDokumentenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 

    /**
	 * 
	 */
    public KommbezEtntVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public KommbezEtntVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public KommbezEtntVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
