package de.imise.tool3lgm.graphtools.metamodel.elements;

public abstract class SpecialisationEdge extends SubordinationEdge {

    public final ModelElement getSpecializedElement() {
        return getSubElement();
    }

    public final ModelElement getGeneralElement() {
        return getSuperElement();
    }

}
