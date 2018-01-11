package de.imise.tool3lgm.graphtools.metamodel.elements;

public abstract class IsAEdge extends HierarchyEdge {

    public final ModelElement getSpecializedElement() {
        return getSubElement();
    }

    public final ModelElement getGeneralElement() {
        return getSuperElement();
    }

}
