package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActor_IheInterface_Edge;

/**
 * @author AXS (24.04.2018)
 */
public abstract class IheInterface extends IheConcept {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, IheActor_IheInterface_Edge.class);
        return dialog;
    }

}
