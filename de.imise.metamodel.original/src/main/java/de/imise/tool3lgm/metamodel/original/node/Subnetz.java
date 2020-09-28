package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.SubnNetztVerbindung;

public final class Subnetz extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(PdvbSubnVerbindung.class);
        dialog.addPathConnectionPanel(SubnNetztVerbindung.class);
        dialog.addPathConnectionPanel(SubnNetzpVerbindung.class);
        return dialog;
    }

}
