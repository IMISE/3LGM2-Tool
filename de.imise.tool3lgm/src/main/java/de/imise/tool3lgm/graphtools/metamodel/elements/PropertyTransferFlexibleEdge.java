package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * @author AXS (29 Dec 2018)
 */
public abstract class PropertyTransferFlexibleEdge extends PropertyTransferEdge {

    /**
     * @param propertyTransferType
     */
    public PropertyTransferFlexibleEdge(final PropertyTransferType propertyTransferType) {
        super(propertyTransferType, false);
    }

}
