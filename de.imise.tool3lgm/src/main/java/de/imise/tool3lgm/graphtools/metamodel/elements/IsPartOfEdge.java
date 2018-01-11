package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * Die PartOf-Beziehung ist eine Bezihung zwischen zwei Elementen, die von der selben Elementklasse sind. Start der Edge ist immer das Kindelement,
 * das Ende der Edge ist immer das Elternelement. Dir Richtung der Edge ist immer forward.
 */
public abstract class IsPartOfEdge extends HierarchyEdge {

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

    /**
     * Gibt die Teilelementklasse der Teil-Von-Beziehung zurück
     *
     * @param poClass
     * @return
     */
    public static final Class<? extends ModelElement> getPartClass(final Class<? extends IsPartOfEdge> poClass) {
        return getStartClass(poClass);
    }

    /**
     * Gibt die Elementklasse der Teil-Von-Beziehung zurück, die nicht die Teilelementklasse ist
     *
     * @param poClass
     * @return
     */
    public static final Class<? extends ModelElement> getParentClass(final Class<? extends IsPartOfEdge> poClass) {
        return getEndClass(poClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Teilelementklasse der übergebenen Edge zuweisungskompatibel ist.
     *
     * @param poClass
     * @param meClass
     * @return
     */
    public static final boolean isPartClass(final Class<? extends IsPartOfEdge> poClass, final Class<? extends ModelElement> meClass) {
        return getPartClass(poClass).isAssignableFrom(meClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Elementklasse der übergebenen Edge zuweisungskompatibel ist, ide nicht
     * die Teilelementklasse ist.
     *
     * @param poClass
     * @param meClass
     * @return
     */
    public static final boolean isParentClass(final Class<? extends IsPartOfEdge> poClass, final Class<? extends ModelElement> meClass) {
        return getParentClass(poClass).isAssignableFrom(meClass);
    }

    /**
     * Richtung, in der die Edge vom Part auf den Parent zeigt.
     */
    public static final int PART_TO_PARENT_DIRECTION = FORWARD;

    /**
     * Richtung, in der die Edge vom Prent auf den Part zeigt.
     */
    public static final int PARENT_TO_PART_DIRECTION = BACKWARD;

}
