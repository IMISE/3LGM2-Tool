package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;

public class Datensatztyp extends Repraesentationsform {

    public Datensatztyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
        dialog.addTab(getResString("Objekttyp"), new NConnectionPanel(Objekttyp.class, dialog, true, true));
        dialog.addTab(getResString("Datenbanksystem"), new NConnectionPanel(Datenbanksystem.class, dialog, false, false));
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
