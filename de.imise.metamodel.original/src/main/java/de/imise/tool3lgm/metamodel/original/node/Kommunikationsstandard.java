package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntKommstVerbindung;

public final class Kommunikationsstandard extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(EtntKommstVerbindung.class);
        dialog.addPathConnectionPanel(BssKommstVerbindung.class);
        return dialog;
    }

}
