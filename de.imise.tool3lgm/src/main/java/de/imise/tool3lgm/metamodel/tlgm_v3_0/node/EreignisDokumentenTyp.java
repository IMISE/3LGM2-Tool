package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntDotVerbindung;

/**
 * @author Thomas Rudert
 */
public final class EreignisDokumentenTyp extends EtntEtdtKombination {

    public EreignisDokumentenTyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripDescriptedSingleConnectionPanel(EtntDotVerbindung.class);
        return dialog;
    }

}
