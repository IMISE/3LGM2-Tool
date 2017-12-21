package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;

public final class DBKonfiguration extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(PdvbkAwbVerbindung.class);
        dialog.addPathConnectionPanel(PdvbPdvbkVerbindung.class);
        return dialog;
    }

}
