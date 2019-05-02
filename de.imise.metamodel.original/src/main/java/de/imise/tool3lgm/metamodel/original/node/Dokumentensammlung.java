package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;

public final class Dokumentensammlung extends LogischerSpeicher {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(DoksDokVerbindung.class);
        dialog.addPathConnectionPanel(true, ObjLogspVerbindung.class);
        return dialog;
    }

}
