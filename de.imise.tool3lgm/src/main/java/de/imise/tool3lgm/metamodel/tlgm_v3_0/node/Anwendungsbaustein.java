package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;

public abstract class Anwendungsbaustein extends Node {

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

}
