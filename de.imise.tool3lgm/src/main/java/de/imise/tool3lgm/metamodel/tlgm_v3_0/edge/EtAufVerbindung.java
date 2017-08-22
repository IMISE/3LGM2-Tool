/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Ereignistyp;

/**
 * @author Thomas
 */
public final class EtAufVerbindung extends Edge {

    //    public static final Class[] stcl = {Ereignistyp.class};
    public static final Class<? extends ModelElement> stcl = Ereignistyp.class;

    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = Aufgabe.class;

    //	public static final Class[] etcl = {Aufgabe.class};

    //	private static Object[][] stcl = {{Ereignistyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
    //	private static Object[][] etcl = {{Aufgabe.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    /**
     * 
     */
    public EtAufVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public EtAufVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public EtAufVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER;
    }

}
