package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetztVerbindung;

public final class Netztyp extends Node {

    public Netztyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(SubnNetztVerbindung.class);
        return dialog;
    }

}
