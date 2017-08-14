package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntDotVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjReprVerbindung;

public class Dokumententyp extends Repraesentationsform {

    public Dokumententyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(ObjReprVerbindung.class);
        dialog.addPathConnectionPanel(DoksDokVerbindung.class);
        dialog.addPathConnectionPanel(EtntDotVerbindung.class);
        return dialog;
    }

    @Override
    public boolean avoidDuplicates() {
        return true;
    }

}
