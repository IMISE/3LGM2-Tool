package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.EtntDotVerbindung;

/**
 * @author Thomas Rudert
 */
public final class EreignisDokumentenTyp extends EtntEtdtKombination {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripDescriptedSingleConnectionPanel(EtntDotVerbindung.class);
        return dialog;
    }

}
