package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
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
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        dialog.addTab(ModelConstants.getDisplayableName(Anwendungsbaustein.class), new NConnectionPanel(Anwendungsbaustein.class, dialog, false, true));
        dialog.addTab(ModelConstants.getDisplayableName(PhysischerDVBaustein.class), new NConnectionPanel(PhysischerDVBaustein.class, dialog, false, true));
        return dialog;
    }

}
