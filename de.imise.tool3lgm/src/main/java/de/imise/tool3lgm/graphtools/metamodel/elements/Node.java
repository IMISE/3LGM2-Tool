package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.userfield.definition.SubType.DUMMY_SUBTYPE;

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
    private SubType subType = DUMMY_SUBTYPE;

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
    @Override
    public final SubType getSubType() {
        return subType;
    }

    /**
     * Sets the subtype of this. If the subtypes superclass is not assignable
     * frim this class nothing happens (it will be ignored).
     *
     * @param subType the subType to set
     */
    @Override
    public final void setSubType(final SubType subType) {
        invalidateNameWithSzens();
        if (subType == null) {
            this.subType = DUMMY_SUBTYPE;
            return;
        }
        Class<? extends ModelElement> superClass = subType.getSuperClass();
        Class<? extends Node> thisClass = getClass();
        if (superClass.isAssignableFrom(thisClass)) {
            this.subType = subType;
        }
    }

    @Override
    protected String getNameExtension() {
        return SubType.isDummy(subType) ? super.getNameExtension() : INNER_NAME_PARTS_SPACE + "(" + subType + ")";
    }

}
