package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbBtypVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;

public class PhysischerDVBaustein extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(PdvbStoVerbindung.class);
        dialog.addDescripSingleConnectionPanel(PdvbBtypVerbindung.class);
        dialog.addPathConnectionPanel(PdvbSubnVerbindung.class);
        dialog.addPathConnectionInfoPanel(PdvbPdvbkVerbindung.class, PdvbkAwbVerbindung.class);
        return dialog;
    }

}
