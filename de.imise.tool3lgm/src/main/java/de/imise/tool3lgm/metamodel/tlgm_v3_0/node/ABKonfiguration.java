package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Konfiguration;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;

public final class ABKonfiguration extends Konfiguration {

    @Override
    public int layerFor() {
        return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER;
    }

    @Override
    public List<ElementContainer> getClientContainer(final GraphDocument doc) {
        List<ElementContainer> v = new ArrayList<>();
        List<ElementContainer> aufOrg = getConnectedContainer(AufOrgKombination.class, doc);
        for (int i = 0; i < aufOrg.size(); i++) {
            v.addAll(((NodeContainer) aufOrg.get(i)).getKnoten().getConnectedContainer(Aufgabe.class, doc));
        }
        return v;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(AwbAwbkVerbindung.class);
        dialog.addPathConnectionInfoPanel(AwbkAufOrgVerbindung.class);
        return dialog;
    }

    @Override
    public List<ElementContainer> getServerContainer(final GraphDocument doc) {
        return getConnectedContainer(Anwendungsbaustein.class, doc);
    }

}