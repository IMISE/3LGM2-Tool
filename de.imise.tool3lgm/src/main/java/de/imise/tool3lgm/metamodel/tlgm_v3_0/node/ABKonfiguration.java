package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;

public final class ABKonfiguration extends Knoten {

    @Override
    public int layerFor() {
        return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(AwbAwbkVerbindung.class);
        dialog.addPathConnectionInfoPanel(AwbkAufOrgVerbindung.class);
        return dialog;
    }

}