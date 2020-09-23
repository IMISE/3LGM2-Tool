package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;

public final class Dokumentensammlung extends LogischerSpeicher {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(DoksDokVerbindung.class);
        dialog.addPathConnectionPanel(ObjLogspVerbindung.class);
        return dialog;
    }

}
