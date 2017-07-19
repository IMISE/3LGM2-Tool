package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;

public abstract class Anwendungsbaustein extends Knoten {

    /**
     *
     */
    public Anwendungsbaustein() {
        super();
    }

    @Override
    public final int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public final boolean hasSortedKanten() {
        return false;
    }

}
