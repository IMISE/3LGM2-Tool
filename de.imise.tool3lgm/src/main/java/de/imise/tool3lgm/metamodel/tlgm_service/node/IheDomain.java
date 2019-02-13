package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheConcept_IheDomain_Edge;

/**
 * @author AXS (31.01.2018)
 */
public class IheDomain extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(true, IheConcept_IheDomain_Edge.class);
        return dialog;
    }

}
