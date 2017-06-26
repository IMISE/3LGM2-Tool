package de.imise.tool3lgm.graphtools.elements;

/**
 * Spezielle Klasse für Kanten, die zwischen denselben Elementen mehrfach vorkommen können.
 * Alle anderen können immer nur 1x vorkommen.
 *
 * @author astruebi
 * @created 22.06.2017
 */
public abstract class MultipleConnectionEdge extends Doppelkante {

    public MultipleConnectionEdge() {
    }

    public MultipleConnectionEdge(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    public MultipleConnectionEdge(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

}
