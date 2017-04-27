package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AwbAufPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.BSNPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.FreeTextPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PDVBKonfPanel;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbDbsVerbindung;

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
        dialog.addTab(getResString("Bausteinschnittstelle_p"), new BSNPanel(Bausteinschnittstelle.class, dialog));
        dialog.addTab(getResString("Benutzungsschnittstelle_p"), new BSNPanel(Benutzungsschnittstelle.class, dialog));
        dialog.addTab(getResString("Anwendungsprogramm"), new FreeTextPanel(Anwendungsprogramm.class, dialog));
        dialog.addTab(getResString("Aufgabe_p"), new AwbAufPanel(dialog));
        dialog.addTab(getResString("PhysischerDVBaustein_p"), new PDVBKonfPanel(dialog));
        return dialog;
    }

}
