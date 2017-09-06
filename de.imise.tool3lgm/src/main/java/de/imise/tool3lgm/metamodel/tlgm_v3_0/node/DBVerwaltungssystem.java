package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DbsDbvsVerbindung;

public final class DBVerwaltungssystem extends Node {

    /**
     *
     */
    public DBVerwaltungssystem() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(DbsDbvsVerbindung.class);
        return dialog;
    }

}
