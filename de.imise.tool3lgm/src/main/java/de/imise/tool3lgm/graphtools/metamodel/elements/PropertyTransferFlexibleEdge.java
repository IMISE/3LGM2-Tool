package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * @author AXS (29 Dec 2018)
 */
public abstract class PropertyTransferEdgeFlexible extends PropertyTransferEdge {

    /**
     * @param propertyTransferType
     */
    public PropertyTransferEdgeFlexible(final PropertyTransferType propertyTransferType) {
        super(propertyTransferType, false);
    }

}
