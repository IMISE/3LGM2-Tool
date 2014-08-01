package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;

public class Dokumententyp extends Repraesentationsform {

	public Dokumententyp() {
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("Objekttyp"), new NConnectionPanel(Objekttyp.class, dialog, true, true));
		dialog.addTab(getResString("Dokumentensammlung"), new NConnectionPanel(Dokumentensammlung.class, dialog, false, true));
		dialog.addTab(getResString("EreignisDokumentenTyp"), new NConnectionPanel(EreignisDokumentenTyp.class, dialog, true, true));
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

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#avoidDuplicates()
	 */
	@Override
	public boolean avoidDuplicates() {
		return true;
	}

}
