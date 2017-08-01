package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author AXS Created on 15.04.2008
 */

public final class PdvbVirtualPdvbVerbindung extends PdvbPdvbVerbindung {

    /**
	 * 
	 */
    public PdvbVirtualPdvbVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public PdvbVirtualPdvbVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public PdvbVirtualPdvbVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
