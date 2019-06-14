package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbAwpVerbindung;

public final class Anwendungsprogramm extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(AwpSwpVerbindung.class);
        dialog.addPathConnectionInfoPanel(RawbAwpVerbindung.class);
        return dialog;
    }

}
