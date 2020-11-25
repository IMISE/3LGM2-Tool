package de.imise.tool3lgm.metamodel.original.node;

import java.util.List;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgAufOrgVerbindung;
import de.imise.util.StringUtils;

/**
 * @author N.N.
 * @create Long time ago
 */
public class AufOrgKombination extends Node {

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder(getMetaModel().getResString("in_oes"));
        retVal.append(": ");
        List<? extends ModelElement> connectedElements = getConnectedElements(Organisationseinheit.class, OrgAufOrgVerbindung.class);
        StringUtils.appendCollectionString(retVal, connectedElements);
        return retVal.toString();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripDescriptedPanel(AufAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(OrgAufOrgVerbindung.class);
        return dialog;
    }

}
