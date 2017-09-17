package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetztVerbindung;

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
