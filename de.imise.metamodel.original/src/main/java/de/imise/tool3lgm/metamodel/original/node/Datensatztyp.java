package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;

public class Datensatztyp extends Repraesentationsform {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(ObjReprVerbindung.class);
        dialog.addPathConnectionInfoPanel(DbsDatVerbindung.class);
        return dialog;
    }

}
