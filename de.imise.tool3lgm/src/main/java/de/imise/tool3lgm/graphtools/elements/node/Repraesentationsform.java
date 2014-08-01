package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;

public abstract class Repraesentationsform extends Knoten {

	/**
	 * 
	 */
	public Repraesentationsform() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public final int layerFor() {
		return ModelConstants.LOGICAL_LAYER; 
	}

}
