package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * @author AXS (29 Dec 2018)
 */
public abstract class HasPartPropertyTransferBothDirectionsEdge extends HasPartEdge {

    /**
     *
     */
    public HasPartPropertyTransferBothDirectionsEdge() {
        super(PropertyTransferType.PROPERTY_TRANSFER_END_TO_START);
    }

}
