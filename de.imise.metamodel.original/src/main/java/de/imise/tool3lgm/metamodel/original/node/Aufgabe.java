package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtAufVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgAufOrgVerbindung;

public final class Aufgabe extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(AufObjVerbindung.class);
        dialog.addPathConnectionLeafPanel(AufAufOrgVerbindung.class, OrgAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(EtAufVerbindung.class);
        dialog.addPathConnectionPanel(AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class);
        return dialog;
    }

}
