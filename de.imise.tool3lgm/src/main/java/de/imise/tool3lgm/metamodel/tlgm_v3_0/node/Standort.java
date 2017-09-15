package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbStoVerbindung;

public final class Standort extends Node {

    /**
     *
     */
    public Standort() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(PdvbStoVerbindung.class);
        return dialog;
    }

}
