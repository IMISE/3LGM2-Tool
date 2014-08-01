package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
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

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#getCopyDependencies()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ModelElement>[] getCopyDependencies() {
		return COPY_DEPENDENCY;
	}

	/**
	 * 
	 */
	public Bausteinschnittstelle(){
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("KommBeziehung"),new KomPanel(Bausteinschnittstelle.class, dialog, true));
		dialog.addTab(getResString("etntges")+ " + " + getResString("etntempf"),new ETNTPanel(EtntEtdtKombination.class, dialog));
		return dialog;
	}


	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasLayout()
	 */
	@Override
	public boolean hasLayout() {
		return true;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasSortedKanten()
	 */
	@Override
	public boolean hasSortedKanten() {
		return false;
	}

	/**
	 * COMMENTME
	 * /
	public static final Class[] MASTER_TYPES = {
		Anwendungsbaustein.class,
		RechAnwendungsbaustein.class,
		KonAnwendungsbaustein.class,
	};
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.SlaveKnoten#getMasterTypes()
	 * /
	public final Class[] getMasterTypes()  {
		return MASTER_TYPES;
	}
*/
}
