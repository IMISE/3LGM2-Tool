package de.imise.tool3lgm.graphtools.elements;

public abstract class Textfeld extends Knoten {

    /**
     *
     */
    public Textfeld() {
        super();
    }

    @Override
    public final boolean hasSortedKanten() {
        return false;
    }

}
