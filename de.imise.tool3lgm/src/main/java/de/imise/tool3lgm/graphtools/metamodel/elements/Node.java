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
        ElementContainer ec;
        if (getMetaModel().hasInterLayerStartClass(this)) {
            ec = new InterLayerConnectedNodeContainer(this, doc);
        } else {
            ec = new NodeContainer(this, doc);
        }
        updateHTMLNameAndAdditionalShape(ec);
        return ec;
    }

}
