package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author N.N.
 * @create Long time ago
 */
public final class Softwareprodukt extends Knoten {
	
	/**
	 * 
	 */
	public Softwareprodukt() {
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		super.setName(name);
		for (int c = 0; c < getEdgesCount(); c++) {
			ModelElement awp = getEdge(c).getOther(this);
			if (awp.getClass() == Anwendungsprogramm.class) {
				for (int a = 0; a < awp.getEdgesCount(); a++) {
					ModelElement awb = awp.getEdge(a).getOther(awp);
					if (awb.getClass() == RechAnwendungsbaustein.class) {
						awb.setName(awb.getName());
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("Aufgabe"),new NConnectionPanel(Aufgabe.class,dialog, true, true));
		dialog.addTab(getResString("Anwendungsprogramm"),new NConnectionPanel(Anwendungsprogramm.class,dialog, false, true));
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
	public boolean avoidDuplicates() { return true; }	

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.LOGICAL_LAYER; 
	}


}
