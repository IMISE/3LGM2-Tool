package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_DeviceClass_Edge;

/**
 * @author AXS (22.12.2017)
 */
public final class DeviceClass extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(PhysicalDataProcessingComponent_DeviceClass_Edge.class);
        return dialog;
    }

}
