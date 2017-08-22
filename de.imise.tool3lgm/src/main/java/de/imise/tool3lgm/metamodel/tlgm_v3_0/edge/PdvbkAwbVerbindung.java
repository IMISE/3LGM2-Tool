/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBKonfiguration;

/**
 * @author Thomas
 */
public final class PdvbkAwbVerbindung extends Edge {

    //    public static final Class[] stcl = {Anwendungsbaustein.class, RechAnwendungsbaustein.class, KonAnwendungsbaustein.class};
    public static final Class<? extends ModelElement> stcl = Anwendungsbaustein.class;

    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.ONE
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = DBKonfiguration.class;

    //	public static final Class[] etcl = {DBKonfiguration.class};

    //	private static Object[][] stcl = {{Anwendungsbaustein.class, ModelConstants.ONE, ModelConstants.UNLIMITED},
    //									{RechAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.UNLIMITED},
    //									{KonAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.UNLIMITED}};
    //	private static Object[][] etcl = {{DBKonfiguration.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    /**
     * 
     */
    public PdvbkAwbVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public PdvbkAwbVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public PdvbkAwbVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.INTER_LOGICAL_PHYSICAL_LAYER;
    }

}
