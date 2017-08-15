package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommBeziehung;

public final class Bausteinschnittstelle extends Schnittstelle {

    /**
     *
     */
    public Bausteinschnittstelle() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, AwbKommssVerbindung.class);
        dialog.addDescripSingleConnectionPanel(BssKommstVerbindung.class);
        dialog.addEdgePanel(KommBeziehung.class);
        dialog.addEdgePanel(BssEtntVerbindung.class);
        return dialog;
    }

}
