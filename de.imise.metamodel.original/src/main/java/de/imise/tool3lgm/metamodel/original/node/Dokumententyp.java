package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntDotVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;

public class Dokumententyp extends Repraesentationsform {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(ObjReprVerbindung.class);
        dialog.addPathConnectionPanel(DoksDokVerbindung.class);
        dialog.addPathConnectionPanel(EtntDotVerbindung.class);
        return dialog;
    }

}
