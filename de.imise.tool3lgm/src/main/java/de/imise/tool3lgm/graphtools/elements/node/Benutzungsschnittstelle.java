package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;

public final class Benutzungsschnittstelle extends Schnittstelle {

    public Benutzungsschnittstelle() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        return super.createPropertyDialog();
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

}
