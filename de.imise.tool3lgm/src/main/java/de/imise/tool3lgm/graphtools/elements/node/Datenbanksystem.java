package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.DbsDatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbDbsVerbindung;

public final class Datenbanksystem extends LogischerSpeicher {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] COPY_DEPENDENCY = {
            DBVerwaltungssystem.class,
            Datensatztyp.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     *
     */
    public Datenbanksystem() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        //dialog._addDescripSingleConnectionInfoPanel(true, RawbDbsVerbindung.class);
        dialog.addDescripSingleConnectionPanel(true, RawbDbsVerbindung.class);
        dialog.addDescripSingleConnectionPanel(DbsDbvsVerbindung.class);
        dialog.addPathConnectionPanel(DbsDatVerbindung.class);
        dialog.addPathConnectionPanel(true, ObjLogspVerbindung.class);
        return dialog;
    }

}
