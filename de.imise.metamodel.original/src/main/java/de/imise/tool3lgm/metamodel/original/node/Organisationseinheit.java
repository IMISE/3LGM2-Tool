package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgAufOrgVerbindung;

public final class Organisationseinheit extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionLeafPanel(OrgAufOrgVerbindung.class, AufAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class);
        return dialog;
    }

}
