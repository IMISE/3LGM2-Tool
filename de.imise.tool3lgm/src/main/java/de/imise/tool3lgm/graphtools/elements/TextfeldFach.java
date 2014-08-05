package de.imise.tool3lgm.graphtools.elements;

public final class TextfeldFach extends Textfeld {

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
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
