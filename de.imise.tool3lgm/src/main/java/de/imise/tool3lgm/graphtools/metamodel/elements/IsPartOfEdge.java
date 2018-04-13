package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * Die PartOf-Beziehung ist eine Bezihung zwischen zwei Elementen, die von der selben Elementklasse sind. Start der Edge ist immer das Kindelement,
 * das Ende der Edge ist immer das Elternelement. Dir Richtung der Edge ist immer forward.
 */
public abstract class IsPartOfEdge extends SubordinationEdge {

    /**
     * Gibt das Element zurück, welches durch diese Edge Teil des anderen Elementes ist.
     *
     * @return Partelement der Edge
     */
    public final ModelElement getPart() {
        return getSubElement();
    }

    /**
     * Gibt das Element zurück, welches durch diese Edge das Oberelement des anderen Elementes ist.
     *
     * @return Parentelement der Edge
     */
    public final ModelElement getParent() {
        return getSuperElement();
    }

}
