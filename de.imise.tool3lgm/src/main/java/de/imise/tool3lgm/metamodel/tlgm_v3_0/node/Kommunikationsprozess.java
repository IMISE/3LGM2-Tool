/*
 * Created on 09.02.2004
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.metamodel.elements.Node;

/**
 * @author AXS
 */
public class Kommunikationsprozess extends Node {

    protected Prozess prozess;

    /**
     * @return
     */
    public Prozess getProzess() {
        return prozess;
    }

    /**
     * @param prozess
     */
    public void setProzess(final Prozess prozess) {
        this.prozess = prozess;
    }

}
