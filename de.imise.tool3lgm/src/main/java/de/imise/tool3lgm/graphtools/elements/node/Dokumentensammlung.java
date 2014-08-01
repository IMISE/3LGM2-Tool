package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

public final class Dokumentensammlung extends LogischerSpeicher {
	
	/**
	 * COMMENTME
	 */
	@SuppressWarnings("rawtypes")
	public static final Class[] COPY_DEPENDENCY = {
			Dokumententyp.class,
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
	public Dokumentensammlung() {
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.LOGICAL_LAYER;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("Dokumententyp"), new NConnectionPanel(Dokumententyp.class, dialog, true, true));
		dialog.addTab(getResString("masterfuer"), new NConnectionPanel(Objekttyp.class, dialog, true, true));
		return dialog;
	}

/*	public static final Class[] MASTER_TYPES = {
		KonAnwendungsbaustein.class,
	};
	
	public final Class[] getMasterTypes()  {
		return MASTER_TYPES;
	}
*/
}
