package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;

public class Objekttyp extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, ObjLogspVerbindung.class);
        dialog.addEdgePanel(AufObjVerbindung.class);
        dialog.addTabbedPanel(Repraesentationsform.class);
        dialog.addTabbedPanelPathConnectionPanel(Nachrichtentyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Dokumententyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Datensatztyp.class, ObjReprVerbindung.class);
        return dialog;
    }

}
