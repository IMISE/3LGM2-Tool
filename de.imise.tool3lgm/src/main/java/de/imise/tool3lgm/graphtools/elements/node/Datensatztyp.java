package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.edge.DbsDatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjReprVerbindung;

public class Datensatztyp extends Repraesentationsform {

    public Datensatztyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(ObjReprVerbindung.class);
        dialog.addPathConnectionInfoPanel(DbsDatVerbindung.class);
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

    @Override
    public boolean avoidDuplicates() {
        return false;
    }

}
