package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommBeziehung;

public final class Bausteinschnittstelle extends Schnittstelle {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            Kommunikationsstandard.class,
            EreignisNachrichtenTyp.class,
            EreignisDokumentenTyp.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     *
     */
    public Bausteinschnittstelle() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, AwbKommssVerbindung.class);
        dialog.addDescripSingleConnectionPanel(BssKommstVerbindung.class);
        dialog.addEdgePanel(KommBeziehung.class);
        dialog.addEdgePanel(BssEtntVerbindung.class);
        return dialog;
    }

}
