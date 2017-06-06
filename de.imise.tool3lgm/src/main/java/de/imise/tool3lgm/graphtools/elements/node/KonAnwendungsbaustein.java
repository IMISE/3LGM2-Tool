package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KawbOrgpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbkAwbVerbindung;

public final class KonAnwendungsbaustein extends Anwendungsbaustein {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            Bausteinschnittstelle.class,
            Benutzungsschnittstelle.class,
            Dokumentensammlung.class,
            Organisationsplan.class,
            DBKonfiguration.class,
            ABKonfiguration.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     *
     */
    public KonAnwendungsbaustein() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(KawbDoksVerbindung.class);
        dialog.addEdgePanel(Bausteinschnittstelle.class, AwbKommssVerbindung.class);
        dialog.addEdgePanel(Benutzungsschnittstelle.class, AwbKommssVerbindung.class);
        dialog.addDescriptedSingleConnectionPanel(KawbOrgpVerbindung.class);
        dialog.addPathConnectionInfoPanel(AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class);
        return dialog;
    }

}
