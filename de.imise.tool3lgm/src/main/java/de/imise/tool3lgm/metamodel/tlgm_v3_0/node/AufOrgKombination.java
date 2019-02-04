package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.List;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgAufOrgVerbindung;
import de.imise.util.StringUtils;

/**
 * @author N.N.
 * @create Long time ago
 */
public class AufOrgKombination extends Node {

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder(getResString("in_oes"));
        retVal.append(": ");
        List<? extends ModelElement> connectedElements = getConnectedElements(Organisationseinheit.class, OrgAufOrgVerbindung.class);
        StringUtils.appendCollectionString(retVal, connectedElements);
        return retVal.toString();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(OrgAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(AufAufOrgVerbindung.class);
        return dialog;
    }

}
