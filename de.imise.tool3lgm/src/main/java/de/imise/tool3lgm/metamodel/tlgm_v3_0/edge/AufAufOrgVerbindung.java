/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;

/**
 * @author Thomas
 */
public final class AufAufOrgVerbindung extends Kante {

    // public static final Class[] stcl = {Aufgabe.class};
    public static final Class<? extends ModelElement> stcl = Aufgabe.class;

    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.ONE
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final Class<? extends ModelElement> etcl = AufOrgKombination.class;

    // public static final Class[] etcl = {AufOrgKombination.class};

    // private static Object[][] stcl = {{Aufgabe.class, ModelConstants.ONE, ModelConstants.ONE}};
    // private static Object[][] etcl = {{AufOrgKombination.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    /**
     * 
     */
    public AufAufOrgVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public AufAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public AufAufOrgVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

}
