package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_RepresentationForm_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ObjectType_RepresentationForm_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_RepresentationForm_Edge;

/**
 * @author AXS (12.01.2021)
 */
public class RepresentationForm extends Node {

    @Override
    protected ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(ApplicationComponent_RepresentationForm_Edge.class);
        dialog.addEdgePanel(Service_RepresentationForm_Edge.class);
        dialog.addEdgePanel(ObjectType_RepresentationForm_Edge.class);
        return dialog;
    }

}
