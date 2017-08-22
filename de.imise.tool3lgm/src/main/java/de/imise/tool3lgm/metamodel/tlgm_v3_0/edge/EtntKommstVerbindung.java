/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EtntEtdtKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Kommunikationsstandard;

/**
 * @author Thomas
 */
public final class EtntKommstVerbindung extends Edge {

    //    public static final Class[] stcl = {EreignisNachrichtenTyp.class, EreignisDokumentenTyp.class};
    public static final Class<? extends ModelElement> stcl = EtntEtdtKombination.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.ONE
    };

    public static final Class<? extends ModelElement> etcl = Kommunikationsstandard.class;

    //	public static final Class[] etcl = {Kommunikationsstandard.class};

    //	private static Object[][] stcl = {{EreignisNachrichtenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED},
    //								   {EreignisDokumentenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
    //	private static Object[][] etcl = {{Kommunikationsstandard.class, ModelConstants.ZERO, ModelConstants.ONE}};

    /**
     * 
     */
    public EtntKommstVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public EtntKommstVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public EtntKommstVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
