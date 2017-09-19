package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgAufOrgVerbindung;

/**
 * @author N.N.
 * @create Long time ago
 */
public class AufOrgKombination extends Node {

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder(getResString("in_oes"));
        retVal.append(": ");
        boolean first = true;
        for (ModelElement oe : getConnectedElements(Organisationseinheit.class, OrgAufOrgVerbindung.class)) {
            if (!first) {
                retVal.append(", ");
            } else {
                first = false;
            }
            retVal.append(oe);
        }
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
