package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;

public class Group extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(LABEL_LAST_EDGE_CONNECTION_NAME, LABEL_LAST_EDGE_CONNECTION_NAME, Group_HasPartEdge.class);
        return dialog;
    }

}
