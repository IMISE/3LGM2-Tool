package de.imise.tool3lgm.graphtools.elements;

public final class TextfeldPhy extends Textfeld {

    @Override
    public int layerFor() {
        return ModelConstants.PHYSICAL_LAYER;
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
