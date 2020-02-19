package de.imise.tool3lgm.metamodel.original.node;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;

public final class Dokumentensammlung extends LogischerSpeicher {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(DoksDokVerbindung.class);
        dialog.addPathConnectionPanel(LABEL_LAST_EDGE_CONNECTION_NAME, ObjLogspVerbindung.class);
        return dialog;
    }

}
