package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.BssEtntVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KommBeziehung;

public final class Bausteinschnittstelle extends Schnittstelle {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(true, AwbKommssVerbindung.class);
        dialog.addDescripPanel(BssKommstVerbindung.class);
        dialog.addEdgePanel(KommBeziehung.class);
        dialog.addEdgePanel(BssEtntVerbindung.class);
        return dialog;
    }

}
