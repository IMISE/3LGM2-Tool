package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntEtVerbindung;

public final class Ereignistyp extends Knoten {

    public Ereignistyp() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(EtAufVerbindung.class);
        dialog.addPathConnectionInfoPanel(EtntEtVerbindung.class);
        return dialog;
    }

}
