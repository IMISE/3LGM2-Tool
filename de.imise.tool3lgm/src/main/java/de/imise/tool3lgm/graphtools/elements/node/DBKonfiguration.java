package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Konfiguration;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public final class DBKonfiguration extends Konfiguration {

    @Override
    public int layerFor() {
        return ModelConstants.INTER_LOGICAL_PHYSICAL_LAYER;
    }

    @Override
    public ArrayList<ElementContainer> getClientContainer(final GraphDocument doc) {
        return getConnectedContainer(Anwendungsbaustein.class, doc);
    }

    @Override
    public ArrayList<ElementContainer> getServerContainer(final GraphDocument doc) {
        return getConnectedContainer(PhysischerDVBaustein.class, doc);
    }

    @Override
    public boolean hasLayout() {
        return false;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(PdvbkAwbVerbindung.class);
        dialog.addPathConnectionPanel(PdvbPdvbkVerbindung.class);
        return dialog;
    }

}
