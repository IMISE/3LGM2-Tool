package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPath;
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

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            Bausteinschnittstelle.class,
            Benutzungsschnittstelle.class,
            Datenbanksystem.class,
            Anwendungsprogramm.class,
            DBKonfiguration.class,
            ABKonfiguration.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    public RechAnwendungsbaustein() {
        super();
    }

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

    @Override
    protected MetaPath getNameExtension() {
        return new MetaPath(RechAnwendungsbaustein.class, Softwareprodukt.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class);
    }

}
