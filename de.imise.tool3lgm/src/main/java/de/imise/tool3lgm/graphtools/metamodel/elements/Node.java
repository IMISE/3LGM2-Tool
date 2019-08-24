package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author N.N.
 * @create Long time ago
 */
public abstract class Node extends ModelElement {

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        if (getMetaModel().hasInterLayerStartClass(this)) {
            return new InterLayerConnectedNodeContainer(this, doc);
        }
        return new NodeContainer(this, doc);
    }

}
