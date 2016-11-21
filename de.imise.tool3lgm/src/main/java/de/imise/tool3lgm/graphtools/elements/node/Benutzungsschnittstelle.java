package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;

public final class Benutzungsschnittstelle extends Schnittstelle {

    public Benutzungsschnittstelle() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
        return super.createPropertyDialog(gdcoll);
    }

    @Override
    public boolean hasLayout() {
        return true;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

    //	public static final Class[] MASTER_TYPES = {
    //		Anwendungsbaustein.class,
    //		RechAnwendungsbaustein.class,
    //		KonAnwendungsbaustein.class,
    //	};
    //	
    //	public final Class[] getMasterTypes()  {
    //		return MASTER_TYPES;
    //	}

}
