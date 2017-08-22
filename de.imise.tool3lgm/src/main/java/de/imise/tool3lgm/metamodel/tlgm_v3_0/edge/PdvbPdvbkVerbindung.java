/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;

/**
 * @author Thomas
 */
public final class PdvbPdvbkVerbindung extends Kante {

    //    public static final Class[] stcl = {PhysischerDVBaustein.class};
    public static final Class<? extends ModelElement> stcl = PhysischerDVBaustein.class;

    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = DBKonfiguration.class;

    //	public static final Class[] etcl = {DBKonfiguration.class};

    //	private static Object[][] stcl = {{PhysischerDVBaustein.class, ModelConstants.ONE, ModelConstants.UNLIMITED}};
    //	private static Object[][] etcl = {{DBKonfiguration.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    /**
     * 
     */
    public PdvbPdvbkVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public PdvbPdvbkVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public PdvbPdvbkVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.INTER_LOGICAL_PHYSICAL_LAYER;
    }

}
