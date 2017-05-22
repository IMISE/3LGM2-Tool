package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AwbAufPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.BSNPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.FreeTextPanel;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.KawbDoksVerbindung;
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
        dialog.addTab(getResString("Bausteinschnittstelle_p"), new BSNPanel(Bausteinschnittstelle.class, dialog));
        dialog.addTab(getResString("Benutzungsschnittstelle_p"), new BSNPanel(Benutzungsschnittstelle.class, dialog));
        dialog.addTab(getResString("Organisationsplan"), new FreeTextPanel(Organisationsplan.class, dialog));
        dialog.addTab(getResString("Aufgabe_p"), new AwbAufPanel(dialog));
        dialog.addPathConnectionPanel(PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class);
        return dialog;
    }

}
