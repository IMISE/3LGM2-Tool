package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_RepresentationForm_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ObjectType_RepresentationForm_Edge;

/**
 * @author AXS (26.12.2017)
 */
public class ObjectType extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Function_ObjectType_Edge.class);
        dialog.addEdgePanel(ObjectType_RepresentationForm_Edge.class);
        dialog.addPanel(ObjectType_RepresentationForm_Edge.class, ApplicationComponent_RepresentationForm_Edge.class);
        return dialog;
    }

}
