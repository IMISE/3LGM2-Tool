package de.imise.tool3lgm.metamodel.original.node;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbDbsVerbindung;

public final class Datenbanksystem extends LogischerSpeicher {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, RawbDbsVerbindung.class);
        dialog.addDescripPanel(DbsDbvsVerbindung.class);
        dialog.addPathConnectionPanel(DbsDatVerbindung.class);
        dialog.addPathConnectionPanel(ObjLogspVerbindung.class);
        return dialog;
    }

}
