package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_SoftwareProduct_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class SoftwareProduct extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Function_SoftwareProduct_Edge.class);
        dialog.addEdgePanel(ApplicationSystem_SoftwareProduct_Edge.class);
        return dialog;
    }

}
