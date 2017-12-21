package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntEtVerbindung;

public final class Ereignistyp extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(EtAufVerbindung.class);
        dialog.addPathConnectionInfoPanel(EtntEtVerbindung.class);
        return dialog;
    }

}
