package de.imise.tool3lgm.metamodel.tlgm_service.node;

import java.util.List;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.OrganisationalUnit_Use_Edge;
import de.imise.util.StringUtils;

/**
 * @author AXS (26.12.2017)
 */
public class Use extends Node {

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder(ElementsNameBuilder.getDisplayableName(getClass()));
        List<? extends ModelElement> connectedElements = getConnectedElements(OrganisationalUnit.class, OrganisationalUnit_Use_Edge.class);
        if (!connectedElements.isEmpty()) {
            retVal.append(": ");
            StringUtils.appendCollectionString(retVal, connectedElements);
        }
        return retVal.toString();
    }

}
