package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbAwpVerbindung;

public final class Anwendungsprogramm extends Node {

    /**
     *
     */
    public Anwendungsprogramm() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(AwpSwpVerbindung.class);
        dialog.addPathConnectionInfoPanel(RawbAwpVerbindung.class);
        return dialog;
    }

}
