package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;

public abstract class Repraesentationsform extends Knoten {

    /**
	 * 
	 */
    public Repraesentationsform() {
        super();
    }

    @Override
    public final int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

}
