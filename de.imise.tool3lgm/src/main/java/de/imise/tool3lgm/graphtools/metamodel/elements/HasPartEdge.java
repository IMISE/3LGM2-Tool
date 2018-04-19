package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * Die HastPart-Beziehung ist eine Bezihung zwischen zwei Elementklasse. Start der Edge ist immer das Elternelement,
 * das Ende der Edge ist immer das Kindelement. Dir Richtung der Edge ist immer FORWARD.
 */
public abstract class HasPartEdge extends SubordinationEdge {

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
     * Gibt die untergeordnete Teil-Klasse der Beziehung zurück
     *
     * @param hasPartEdgeClass
     * @return
     */
    public static Class<? extends ModelElement> getPartClass(final Class<? extends HasPartEdge> hasPartEdgeClass) {
        return getSubClass(hasPartEdgeClass);
    }

    /**
     * Gibt die übergeordnete Ober-Klasse der Beziehung zurück
     *
     * @param hasPartEdgeClass
     * @return
     */
    public static Class<? extends ModelElement> getParentClass(final Class<? extends HasPartEdge> hasPartEdgeClass) {
        return getSuperClass(hasPartEdgeClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Unterlementklasse der übergebenen Edge zuweisungskompatibel ist.
     *
     * @param hasPartEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isPartClass(final Class<? extends HasPartEdge> hasPartEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSubClass(hasPartEdgeClass).isAssignableFrom(meClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Elementklasse der übergebenen Edge zuweisungskompatibel ist, die
     * nicht die Teilelementklasse ist.
     *
     * @param hasPartEdgeClass
     * @param meClass
     * @return
     */
    public static final boolean isParentClass(final Class<? extends HasPartEdge> hasPartEdgeClass, final Class<? extends ModelElement> meClass) {
        return getSuperClass(hasPartEdgeClass).isAssignableFrom(meClass);
    }

}
