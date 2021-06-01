package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.SubType;
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
    private SubType subType = null;

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        ElementContainer ec;
        MetaModel metaModel = getMetaModel();
        if (metaModel.hasInterLayerStartClass(this)) {
            ec = new InterLayerConnectedNodeContainer(this, doc);
        } else {
            ec = new NodeContainer(this, doc);
        }
        updateGraphName(ec);
        return ec;
    }

    /**
     * @param doc
     * @return
     */
    @Override
    public NodeContainer getContainer(final GraphDocument doc) {
        return (NodeContainer) super.getContainer(doc);
    }

    /**
     * @return the subType
     */
    public final SubType getSubType() {
        return subType;
    }

    /**
     * @param subType the subType to set
     */
    public final void setSubType(final SubType subType) {
        this.subType = subType;
    }

}
