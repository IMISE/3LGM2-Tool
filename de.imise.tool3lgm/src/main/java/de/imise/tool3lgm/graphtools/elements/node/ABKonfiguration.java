package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

public final class ABKonfiguration extends Konfiguration {

    @Override
    public int layerFor() {
        return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER;
    }

    @Override
    public ArrayList<ElementContainer> getClientContainer(final GraphDocument doc) {
        ArrayList<ElementContainer> v = new ArrayList<ElementContainer>();
        ArrayList<ElementContainer> aufOrg = getConnectedContainer(AufOrgKombination.class, doc);
        for (int i = 0; i < aufOrg.size(); i++) {
            v.addAll(((NodeContainer) aufOrg.get(i)).getKnoten().getConnectedContainer(Aufgabe.class, doc));
        }
        return v;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTab(getResString("Anwendungsbaustein"), new NConnectionPanel(Anwendungsbaustein.class, dialog, true, true));
        dialog.addTab(getResString("AufOrgKombination"), new NConnectionPanel(AufOrgKombination.class, dialog, false, false));
        return dialog;
    }

    @Override
    public ArrayList<ElementContainer> getServerContainer(final GraphDocument doc) {
        return getConnectedContainer(Anwendungsbaustein.class, doc);
    }

    @Override
    public boolean hasLayout() {
        return false;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }
}