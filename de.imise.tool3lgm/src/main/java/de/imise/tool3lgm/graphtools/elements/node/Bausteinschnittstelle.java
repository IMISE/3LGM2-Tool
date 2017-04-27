package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ETNTPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.KomPanel;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

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
        dialog.addTab(getResString("KommBeziehung"), new KomPanel(Bausteinschnittstelle.class, dialog, true));
        dialog.addTab(getResString("etntges") + " + " + getResString("etntempf"), new ETNTPanel(EtntEtdtKombination.class, dialog));
        return dialog;
    }

    @Override
    public boolean hasLayout() {
        return true;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

    //	/**
    //	 * COMMENTME
    //	 */
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
