/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

/**
 * @author Thomas
 */
public final class RawbRawbVerbindung extends PartOfBeziehung {

    //    public static final Class[] stcl = {RechAnwendungsbaustein.class};
    public static final Class<? extends ModelElement> stcl = RechAnwendungsbaustein.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = RechAnwendungsbaustein.class;

    //	public static final Class[] etcl = {RechAnwendungsbaustein.class};

    /*
     * private static Object[][] stcl = {{RechAnwendungsbaustein.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; private static Object[][] etcl = {{RechAnwendungsbaustein.class, ModelConstants.ZERO, ModelConstants.UNLIMITED},
     * {Anwendungsbaustein.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
     */
    /**
	 * 
	 */
    public RawbRawbVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public RawbRawbVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public RawbRawbVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
