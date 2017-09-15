package de.imise.tool3lgm.graphtools.metamodel;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author N.N.
 * @create Long time ago
 */
public abstract class Node extends ModelElement {

    /**
     *
     */
    public Node() {
        super();
    }

    /**
     * @param classesToShow
     * @return
     */
    public String getLabel(final String[] classesToShow) {
        return getName();
    }

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        if (ModelConstants.isInterLayerStartClass(getClass())) {
            return new InterLayerConnectedNodeContainer(this, doc);
        }
        return new NodeContainer(this, doc);
    }

    @Override
    public boolean putXMLFieldString(final String field, final String value) {
        return super.putXMLFieldString(field, value);
    }

}
