package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.KawbOrgpVerbindung;

public final class Organisationsplan extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(KawbOrgpVerbindung.class);
        return dialog;
    }

}
