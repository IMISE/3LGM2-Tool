package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgAufOrgVerbindung;

public final class Organisationseinheit extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionLeafPanel(OrgAufOrgVerbindung.class, AufAufOrgVerbindung.class);
        return dialog;
    }

}
