package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * Spezialisierungskanten vererben ihre Eigenschaften vom Generellen ans Spezielle, also vom Startelement an das Endelement.
 *
 * @author AXS (05 Dec 2018)
 */
public abstract class SpecialisationEdge extends HasPartPropertyTransferStartToEndEdge {

    public final ModelElement getSpecializedElement() {
        return getSubElement();
    }

    public final ModelElement getGeneralElement() {
        return getSuperElement();
    }

}
