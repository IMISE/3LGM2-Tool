package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.EtntNatVerbindung;

/**
 * @author Thomas Rudert
 */
public final class EreignisNachrichtenTyp extends EtntEtdtKombination {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripDescriptedPanel(EtntNatVerbindung.class);
        return dialog;
    }

}
