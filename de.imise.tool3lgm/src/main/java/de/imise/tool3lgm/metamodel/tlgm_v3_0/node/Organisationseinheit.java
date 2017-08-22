package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgAufOrgVerbindung;

public final class Organisationseinheit extends Node {

    /**
     *
     */
    public Organisationseinheit() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionLeafPanel(OrgAufOrgVerbindung.class, AufAufOrgVerbindung.class);
        return dialog;
    }

}
