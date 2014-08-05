package de.imise.tool3lgm.graphtools.elements;

public final class TextfeldLog extends Textfeld {

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public boolean hasLayout() {
        return true;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

}
