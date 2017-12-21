/*
 * Created on 20.01.2004
 */
package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;

/**
 * @author thomas
 */
public class LayerKnoten extends Node {

    public LayerKnoten(final int layer) {
        this.layer = layer;
        setName(ModelConstants.getVisibleLayerName(layer));
    }

}
