package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.original.edge.EtntDotVerbindung;

/**
 * @author Thomas Rudert
 */
public final class EreignisDokumentenTyp extends EtntEtdtKombination {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripDescriptedPanel(EtntDotVerbindung.class);
        return dialog;
    }

}
