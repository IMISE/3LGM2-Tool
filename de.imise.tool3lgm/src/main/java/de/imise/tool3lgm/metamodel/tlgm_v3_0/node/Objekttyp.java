package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjReprVerbindung;

public class Objekttyp extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, ObjLogspVerbindung.class);
        dialog.addEdgePanel(AufObjVerbindung.class);
        dialog.addTabbedPanel("Repraesentationsform_p");
        dialog.addTabbedPanelPathConnectionPanel(Nachrichtentyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Dokumententyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Datensatztyp.class, ObjReprVerbindung.class);
        return dialog;
    }

}
