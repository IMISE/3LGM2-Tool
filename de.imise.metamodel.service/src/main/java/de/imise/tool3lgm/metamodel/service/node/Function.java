package de.imise.tool3lgm.metamodel.service.node;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.OrganisationalUnit_Use_Edge;

public class Function extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Function_ObjectType_Edge.class);
        dialog.addMultiPanel(Function_Use_Edge.class, FORWARD);
        dialog.addMultiPanelPathPanel(Function_Use_Edge.class, OrganisationalUnit_Use_Edge.class);
        dialog.addMultiPanelPathPanel(Function_Use_Edge.class, ApplicationComponent_Use_Edge.class);
        dialog.addEdgePanel(Function_SoftwareProduct_Edge.class);
        return dialog;
    }

}
