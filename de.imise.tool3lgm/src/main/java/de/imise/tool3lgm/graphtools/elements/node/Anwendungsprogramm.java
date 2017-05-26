package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbAwpVerbindung;

public final class Anwendungsprogramm extends Knoten {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            Softwareprodukt.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     *
     */
    public Anwendungsprogramm() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTab(getResString("Softwareprodukt"), new NConnectionPanel(Softwareprodukt.class, dialog, true, true));
        dialog.addPathConnectionPanel(AwpSwpVerbindung.class);
        dialog.addTab(getResString("Anwendungsbaustein"), new NConnectionPanel(RechAnwendungsbaustein.class, dialog, false, false));
        dialog.addPathConnectionInfoPanel(RawbAwpVerbindung.class);
        return dialog;
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
