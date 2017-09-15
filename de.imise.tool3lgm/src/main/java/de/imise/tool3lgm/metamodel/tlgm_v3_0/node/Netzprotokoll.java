package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetzpVerbindung;

public final class Netzprotokoll extends Node {

    public Netzprotokoll() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(SubnNetzpVerbindung.class);
        return dialog;
    }

}
