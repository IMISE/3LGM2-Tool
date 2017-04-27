package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;

public final class Nachrichtentyp extends Repraesentationsform {

    public Nachrichtentyp() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTab(getResString("Objekttyp"), new NConnectionPanel(Objekttyp.class, dialog, true, true));
        dialog.addTab(getResString("EreignisNachrichtenTyp"), new NConnectionPanel(EreignisNachrichtenTyp.class, dialog, true, true));
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
        return true;
    }

}
