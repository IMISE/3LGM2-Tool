package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntNatVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjReprVerbindung;

public final class Nachrichtentyp extends Repraesentationsform {

    public Nachrichtentyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(ObjReprVerbindung.class);
        dialog.addPathConnectionPanel(EtntNatVerbindung.class);
        return dialog;
    }

    @Override
    public boolean avoidDuplicates() {
        return true;
    }

}
