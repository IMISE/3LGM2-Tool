/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;

/**
 * @author Thomas To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class AwbAwbkVerbindung extends Edge {

    // public static final Class[] stcl = {ABKonfiguration.class};
    public static final Class<? extends ModelElement> stcl = ABKonfiguration.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ONE,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = Anwendungsbaustein.class;

    // public static final Class[] etcl = {Anwendungsbaustein.class, RechAnwendungsbaustein.class, KonAnwendungsbaustein.class};

    // private static Object[][] stcl = {{ABKonfiguration.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
    // private static Object[][] etcl = {{Anwendungsbaustein.class, ModelConstants.ONE, ModelConstants.UNLIMITED},
    // {RechAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.UNLIMITED},
    // {KonAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.UNLIMITED}};

    /**
     *
     */
    public AwbAwbkVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public AwbAwbkVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public AwbAwbkVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
