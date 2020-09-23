package de.imise.tool3lgm.metamodel.original.node;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;

public class Objekttyp extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(LABEL_LAST_EDGE_CONNECTION_NAME, ObjLogspVerbindung.class);
        dialog.addEdgePanel(AufObjVerbindung.class);
        dialog.addTabbedPanel(Repraesentationsform.class);
        dialog.addTabbedPanelPathConnectionPanel(Nachrichtentyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Dokumententyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Datensatztyp.class, ObjReprVerbindung.class);
        return dialog;
    }

}
