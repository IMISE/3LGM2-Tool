/**
 *
 */
package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;

/**
 * Eine Komposition im Sinne der Objektortientierung. Das 2. Element bzw.
 * Endelement gilt immer als Untergeordnetes Element (Slave) des 1. bzw.
 * Startelementes (Master). Diese Tatsache kann aber weitestgehend
 * unberücksichtigt bleiben, wenn man die speziellen Eigenschaften der
 * Komposition ausschließlich über die Klasse <code>Composition.class</code> und
 * nicht über die Klasse <code>Edge.class</code> abfragt (also wer Master und
 * wer Slave sein kann und wieviele Elemente miteinander verbunden sein können.)
 *
 * @author AXS
 */
public abstract class CompositionEdge extends SubordinationEdge {

    /** Richtung, in der die Kante vom Oberelement auf das Unterelement zeigt */
    public static final Direction MASTER_TO_SLAVE_DIRECTION = SUPER_TO_SUB_DIRECTION;

    /** Richtung, in der die Kante vom Unterelement auf das Oberelement zeigt */
    public static final Direction SLAVE_TO_MASTER_DIRECTION = SUB_TO_SUPER_DIRECTION;

    /**
     * Die Slave-Kardinalität zum Master ist immer 1..1. ALso ein Slave muss
     * genau einen Master haben.
     */
    public static final EdgeCardinality SCARD = ONE_ONE;

    /**
     * Liefert das ModelElement, von dem das andere abhängt.
     *
     * @return Masterelement der Komposition
     */
    public final ModelElement getMaster() {
        return getStart();
    }

    /**
     * Liefert das ModelElement, das von dem anderen abhängt.
     *
     * @return Slaveelement der Komposition
     */
    public final ModelElement getSlave() {
        return getEnd();
    }

    /**
     * Liefert alle Elementklassen, die Masterklasse der übergebenen Komposition
     * sein können.
     *
     * @param compositionClass
     * @return
     */
    public static final Class<? extends ModelElement> getMasterType(final Class<? extends CompositionEdge> compositionClass) {
        return getStartClass(compositionClass);
    }

    /**
     * Liefert alle Elementklassen, die Slaveklasse der übergebenen Komposition
     * sein können.
     *
     * @param compositionClass
     * @return
     */
    public static final Class<? extends ModelElement> getSlaveType(final Class<? extends CompositionEdge> compositionClass) {
        return getEndClass(compositionClass);
    }

}
