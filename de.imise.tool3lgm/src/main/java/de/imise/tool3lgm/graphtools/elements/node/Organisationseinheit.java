package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AufOrgPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;

public final class Organisationseinheit extends Knoten {
	
	/**
	 * 
	 */
	public Organisationseinheit() {
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.DOMAIN_LAYER;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("strukt"), new StructurePanel(dialog));
		//		dialog.addTab(getResString("Aufgabe_p"),new OrgAufPanel(new
		// Aufgabe(),dialog));
		dialog.addTab(getResString("Aufgabe_p"), new AufOrgPanel(Aufgabe.class, dialog, true));
		return dialog;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasLayout()
	 */
	@Override
	public boolean hasLayout() {
		return false;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasSortedKanten()
	 */
	@Override
	public boolean hasSortedKanten() {
		return false;
	}

}
