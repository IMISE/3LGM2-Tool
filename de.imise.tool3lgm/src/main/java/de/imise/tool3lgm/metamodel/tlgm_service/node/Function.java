package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.OrganisationalUnit_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.SupportLink_Edge;

public final class Function extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Function_ObjectType_Edge.class);
        //        dialog.addPathConnectionLeafPanel(AufAufOrgVerbindung.class, OrgAufOrgVerbindung.class);
        dialog.addPathConnectionLeafPanel(Function_Use_Edge.class, OrganisationalUnit_Use_Edge.class);
        dialog.addEdgePanel(Function_SoftwareProduct_Edge.class);
        dialog.addPathConnectionPanel(Function_Use_Edge.class, SupportLink_Edge.class);
        return dialog;
    }

}
