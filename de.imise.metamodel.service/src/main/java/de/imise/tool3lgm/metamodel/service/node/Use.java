package de.imise.tool3lgm.metamodel.service.node;

import java.util.List;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.OrganisationalUnit_Use_Edge;
import de.imise.util.StringUtils;

/**
 * @author AXS (26.12.2017)
 */
public class Use extends Node {

    @Override
    protected ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripPanel(Function_Use_Edge.class);
        dialog.addEdgePanel(ApplicationComponent_Use_Edge.class);
        dialog.addEdgePanel(OrganisationalUnit_Use_Edge.class);
        return dialog;
    }

    @Override
    public String toString() {
        ElementsNameBuilder elementsNameBuilder = getMetaModel().getElementsNameBuilder();
        StringBuilder retVal = new StringBuilder(elementsNameBuilder.getDisplayableName(getClass()));
        List<? extends ModelElement> connectedElements = getConnectedElements(OrganisationalUnit.class, OrganisationalUnit_Use_Edge.class);
        if (!connectedElements.isEmpty()) {
            retVal.append(": ");
            StringUtils.appendCollectionString(retVal, connectedElements);
        }
        return retVal.toString();
    }

}
