package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntKommstVerbindung;

public final class Kommunikationsstandard extends Node {

    public Kommunikationsstandard() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(EtntKommstVerbindung.class);
        dialog.addPathConnectionPanel(BssKommstVerbindung.class);
        return dialog;
    }

}
