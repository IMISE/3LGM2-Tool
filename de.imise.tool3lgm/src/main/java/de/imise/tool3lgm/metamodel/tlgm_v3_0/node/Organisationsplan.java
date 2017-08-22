package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KawbOrgpVerbindung;

public final class Organisationsplan extends Node {

    public Organisationsplan() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(KawbOrgpVerbindung.class);
        return dialog;
    }

}
