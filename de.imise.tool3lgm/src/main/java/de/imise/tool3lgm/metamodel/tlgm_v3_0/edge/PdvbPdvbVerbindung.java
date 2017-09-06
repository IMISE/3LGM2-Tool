/*
 * Created on 16.01.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;

/**
 * @author Thomas
 */
public class PdvbPdvbVerbindung extends PartOfBeziehung {

    public static final Class<? extends ModelElement> stcl = PhysischerDVBaustein.class;
    public static final int[] scard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO,
            ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = PhysischerDVBaustein.class;

    /**
     * 
     */
    public PdvbPdvbVerbindung() {
        super();
    }

    /**
     * @param k1
     * @param k2
     */
    public PdvbPdvbVerbindung(final ModelElement k1, final ModelElement k2) {
        super(k1, k2);
    }

    /**
     * @param k1
     * @param k2
     * @param registerInKnots
     */
    public PdvbPdvbVerbindung(final ModelElement k1, final ModelElement k2, final boolean registerInKnots) {
        super(k1, k2, registerInKnots);
    }

}
