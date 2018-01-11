package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Benutzungsschnittstelle;

/**
 * @author AXS (26.12.2017)
 */
public final class OrganisationSystem extends Anwendungsbaustein {

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

}
