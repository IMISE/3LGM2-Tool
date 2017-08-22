/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Softwareprodukt;

/**
 * @author Thomas
 */
public final class AwpSwpVerbindung extends Kante {

    //	public static final Class[] stcl = {Anwendungsprogramm.class};
    public static final Class<? extends ModelElement> stcl = Anwendungsprogramm.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.ONE
    };

    public static final Class<? extends ModelElement> etcl = Softwareprodukt.class;

    //	public static final Class[] etcl = {Softwareprodukt.class};

    //	private static Object[][] stcl = {{Anwendungsprogramm.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
    //	private static Object[][] etcl = {{Softwareprodukt.class, ModelConstants.ZERO, ModelConstants.ONE}};

    /**
     * 
     */
    public AwpSwpVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public AwpSwpVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public AwpSwpVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
