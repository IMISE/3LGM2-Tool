package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_Location_Edge;

/**
 * @author AXS (22.12.2017)
 */
public final class Location extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(PhysicalDataProcessingComponent_Location_Edge.class);
        return dialog;
    }

}
