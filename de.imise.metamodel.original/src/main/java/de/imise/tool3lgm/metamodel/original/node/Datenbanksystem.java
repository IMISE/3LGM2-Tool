package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbDbsVerbindung;

public final class Datenbanksystem extends LogischerSpeicher {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, RawbDbsVerbindung.class);
        dialog.addDescripPanel(DbsDbvsVerbindung.class);
        dialog.addPathConnectionPanel(DbsDatVerbindung.class);
        dialog.addPathConnectionPanel(true, ObjLogspVerbindung.class);
        return dialog;
    }

}
