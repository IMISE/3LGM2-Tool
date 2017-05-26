package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.edge.SubnNetzpVerbindung;

public final class Netzprotokoll extends Knoten {

    public Netzprotokoll() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.PHYSICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(SubnNetzpVerbindung.class);
        return dialog;
    }

    @Override
    public boolean hasLayout() {
        return false;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

    @Override
    public boolean avoidDuplicates() {
        return true;
    }

}
