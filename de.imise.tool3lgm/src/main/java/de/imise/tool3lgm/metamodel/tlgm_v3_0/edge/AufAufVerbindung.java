/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;

/**
 * @author Thomas
 */
public final class AufAufVerbindung extends PartOfBeziehung {

    // public static final Class[] stcl = {Aufgabe.class};
    public static final Class<? extends ModelElement> stcl = Aufgabe.class;
    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Aufgabe.class;

    // public static final Class[] etcl = {Aufgabe.class};

    // private static Object[][] stcl = {{Aufgabe.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};
    // private static Object[][] etcl = {{Aufgabe.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}};

    /**
     *
     */
    public AufAufVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public AufAufVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public AufAufVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
