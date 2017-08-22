/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EtntEtdtKombination;

/**
 * @author Thomas
 */
public final class BssEtntVerbindung extends Kante {

    //	public static final Class[] stcl = {Bausteinschnittstelle.class};
    public static final Class<? extends ModelElement> stcl = Bausteinschnittstelle.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = EtntEtdtKombination.class;

    //	public static final Class[] etcl = {EreignisNachrichtenTyp.class, EreignisDokumentenTyp.class};

    //	private static Object[][] stcl = {{Bausteinschnittstelle.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
    //	private static Object[][] etcl = {{EreignisNachrichtenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED},
    //								   {EreignisDokumentenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    /**
     * 
     */
    public BssEtntVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public BssEtntVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public BssEtntVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
