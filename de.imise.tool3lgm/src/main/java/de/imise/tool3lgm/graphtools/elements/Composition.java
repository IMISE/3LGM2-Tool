/**
 *
 */
package de.imise.tool3lgm.graphtools.elements;

/**
 * Eine Komposition im Sinne der Objektortientierung. Das 2. Element bzw. Endelement gilt immer als Untergeordnetes Element (Slave) des 1. bzw.
 * Startelementes (Master). Diese Tatsache kann aber weitestgehend unberücksichtigt bleiben, wenn man die
 * speziellen Eigenschaften der Komposition ausschließlich über die Klasse <code>Composition.class</code> und nicht über die Klasse
 * <code>Kante.class</code> abfragt (also wer Master und wer Slave sein kann und wieviele Elemente miteinander verbunden sein
 * können.)
 *
 * @author AXS
 */
public abstract class Composition extends Doppelkante {

    /**
     * Die Slave-Kardinalität zum Master ist immer 1..1. ALso ein Slave muss genau einen Master haben.
     */
    public static final int[] scard = {
            ModelConstants.ONE,
            ModelConstants.ONE
    };

    /**
     *
     */
    public Composition() {
    }

    /**
     * @param knot1
     * @param knot2
     */
    public Composition(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public Composition(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

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
     * Liefert alle Elementklassen, die Masterklasse der übergebenen Komposition sein können.
     *
     * @param compositionClass
     * @return
     */
    public static final Class<? extends ModelElement> getMasterType(final Class<? extends Composition> compositionClass) {
        return getStartClass(compositionClass);
    }

    /**
     * Liefert alle Elementklassen, die Slaveklasse der übergebenen Komposition sein können.
     *
     * @param compositionClass
     * @return
     */
    public static final Class<? extends ModelElement> getSlaveType(final Class<? extends Composition> compositionClass) {
        return getEndClass(compositionClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Slave-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public static final boolean isSlaveType(final Class<? extends Composition> compositionClass, final Class<? extends ModelElement> elementClass) {
        return isEndClass(compositionClass, elementClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Elementklasse eine Master-Klasse der übergebenen Kompositionsklasse ist.
     *
     * @param compositionClass
     * @param elementClass
     * @return
     */
    public static final boolean isMasterType(final Class<? extends Composition> compositionClass, final Class<? extends ModelElement> elementClass) {
        return isStartClass(compositionClass, elementClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinMasterToSlaveCardinality(final Class<? extends Kante> edgeClass) {
        return getMinStartToEndCardinality(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxMasterToSlaveCardinality(final Class<? extends Kante> edgeClass) {
        return getMaxStartToEndCardinality(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMinSlaveToMasterCardinality(final Class<? extends Kante> edgeClass) {
        return getMinEndToStartCardinality(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public static final int getMaxSlaveToMasterCardinality(final Class<? extends Kante> edgeClass) {
        return getMaxEndToStartCardinality(edgeClass);
    }

    @Override
    public boolean checkValidity() {
        if (!super.checkValidity()) {
            return false;
        }
        direction = FORWARD;
        return true;
    }

}
