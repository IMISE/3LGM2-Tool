package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;

public class Textfield extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, Textfield_ModelElement_Edge.class);
        return dialog;
    }

}