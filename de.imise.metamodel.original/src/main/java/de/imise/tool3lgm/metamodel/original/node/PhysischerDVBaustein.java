package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.DatenuebertragungsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbBtypVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbkAwbVerbindung;

public class PhysischerDVBaustein extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(PdvbStoVerbindung.class);
        dialog.addDescripPanel(PdvbBtypVerbindung.class);
        dialog.addPathConnectionPanel(PdvbSubnVerbindung.class);
        dialog.addPathConnectionLeafPanel(PdvbPdvbkVerbindung.class, PdvbkAwbVerbindung.class);
        dialog.addEdgePanel(DatenuebertragungsVerbindung.class);
        return dialog;
    }

}
