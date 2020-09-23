package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.EtntNatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;

public final class Nachrichtentyp extends Repraesentationsform {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(ObjReprVerbindung.class);
        dialog.addPathConnectionPanel(EtntNatVerbindung.class);
        return dialog;
    }

}
