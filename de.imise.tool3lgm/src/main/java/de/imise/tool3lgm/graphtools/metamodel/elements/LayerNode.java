/*
 * Created on 20.01.2004
 */
package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;

/**
 * @author thomas
 */
public class LayerNode extends Node {

    public LayerNode(final MetaModel metaModel, final int layer) {
        this.layer = layer;
        setMetaModel(metaModel);
        setName(ModelConstants.getVisibleLayerName(layer));
    }

}
