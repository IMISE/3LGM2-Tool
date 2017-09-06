package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbDbsVerbindung;

public final class Datenbanksystem extends LogischerSpeicher {

    public Datenbanksystem() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, RawbDbsVerbindung.class);
        dialog.addDescripSingleConnectionPanel(DbsDbvsVerbindung.class);
        dialog.addPathConnectionPanel(DbsDatVerbindung.class);
        dialog.addPathConnectionPanel(true, ObjLogspVerbindung.class);
        return dialog;
    }

}
