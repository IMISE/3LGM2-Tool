package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_SoftwareProduct_Edge;

/**
 * @author AXS (26.12.2017)
 */
public final class SoftwareProduct extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Function_SoftwareProduct_Edge.class);
        dialog.addMultiPanel(ApplicationSystem_SoftwareProduct_Edge.class);
        dialog.addMultiPanelEdgePanel(ApplicationSystem_SoftwareProduct_Edge.class);
        dialog.addMultiPanelEdgePanel(IheActorInstance_SoftwareProduct_Edge.class);
        return dialog;
    }

}
