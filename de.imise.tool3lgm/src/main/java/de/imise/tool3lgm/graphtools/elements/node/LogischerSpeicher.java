/**
 * 
 */
package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.elements.Knoten;

/**
 *
 */
public abstract class LogischerSpeicher extends Knoten {

	/**
	 * 
	 */
	public LogischerSpeicher() {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasLayout()
	 */
	@Override
	public final boolean hasLayout() {
		return true;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasSortedKanten()
	 */
	@Override
	public final boolean hasSortedKanten() {
		return false;
	}

}
