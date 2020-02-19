package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

/**
 * Oberklasse für alle Kantenklassen, die ein Element von einem anderen ableiten, also eine Instanz bilden. Das Instanzelement ist immer das
 * Endelement der Kante und das Oberelement immer das Startelement.
 * Der Mechanismus funktioniert folgendermaßen: Anders als bei allen anderen Kanten legt man diese Kante nicht zwischen 2 existierenden Elementen
 * an, sondern man legt diese Kante für ein Element der Startklasse der Kante an. Dadurch wird das Endelement erst neu erzeugt ("instanziiert").
 *
 * @author AXS (25.09.2018)
 */
public abstract class InstanciationEdge extends SubordinationEdge {

    /** Richtung, in der die Kante vom Oberelement auf das Unterelement zeigt */
    public static final Direction MASTER_TO_INSTANCE_DIRECTION = FORWARD;

    /** Richtung, in der die Kante vom Unterelement auf das Oberelement zeigt */
    public static final Direction INSTANCE_TO_MASTER_DIRECTION = BACKWARD;

    /**
     * Liefert die Master-Elementklasse, dieser Instanziierungskante
     *
     * @param instancieationEdgeClass
     * @return
     */
    public static Class<? extends ModelElement> getInstanciationMaster(final Class<? extends InstanciationEdge> instancieationEdgeClass) {
        return getStartClass(instancieationEdgeClass);
    }

    /**
     * Liefert die Istnanz-Elementklasse, dieser Instanziierungskante
     *
     * @param instancieationEdgeClass
     * @return
     */
    public static Class<? extends ModelElement> getInstanciationInstance(final Class<? extends InstanciationEdge> instancieationEdgeClass) {
        return getEndClass(instancieationEdgeClass);
    }

    /**
     * @return
     */
    public ModelElement getMasterElement() {
        return getSuperElement();
    }

    /**
     * @return
     */
    public ModelElement getInstanceElement() {
        return getSubElement();
    }

}