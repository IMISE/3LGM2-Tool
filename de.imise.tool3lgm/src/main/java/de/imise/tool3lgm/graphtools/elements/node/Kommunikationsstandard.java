package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.edge.BssKommstVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntKommstVerbindung;

public final class Kommunikationsstandard extends Knoten {

    public Kommunikationsstandard() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(EtntKommstVerbindung.class);
        dialog.addPathConnectionPanel(BssKommstVerbindung.class);
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
