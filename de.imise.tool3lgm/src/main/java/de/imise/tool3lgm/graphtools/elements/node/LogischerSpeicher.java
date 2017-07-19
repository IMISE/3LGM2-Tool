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

    @Override
    public final boolean hasSortedKanten() {
        return false;
    }

}
