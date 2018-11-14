package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.path.MetaPathOld;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbDbsVerbindung;

public final class RechAnwendungsbaustein extends Anwendungsbaustein {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(RawbDbsVerbindung.class);
        dialog.addDescripSingleConnectionPanel(RawbAwpVerbindung.class, AwpSwpVerbindung.class);
        dialog.addEdgePanel(Bausteinschnittstelle.class, AwbKommssVerbindung.class);
        dialog.addEdgePanel(Benutzungsschnittstelle.class, AwbKommssVerbindung.class);
        dialog.addDescriptedSingleConnectionPanel(RawbAwpVerbindung.class);
        dialog.addPathConnectionInfoPanel(AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class);
        return dialog;
    }

    private static final MetaPathOld NAME_EXTENSION_PATH = new MetaPathOld(RechAnwendungsbaustein.class, Softwareprodukt.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class);

    @Override
    protected MetaPathOld getNameExtensionPath() {
        return NAME_EXTENSION_PATH;
    }

}
