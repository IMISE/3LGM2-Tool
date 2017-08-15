package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntNatVerbindung;

/**
 * @author Thomas Rudert
 */
public final class EreignisNachrichtenTyp extends EtntEtdtKombination {

    public EreignisNachrichtenTyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripDescriptedSingleConnectionPanel(EtntNatVerbindung.class);
        return dialog;
    }

}
