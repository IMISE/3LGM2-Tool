package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public final class DBKonfiguration extends Konfiguration {
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.INTER_LOGICAL_PHYSICAL_LAYER;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.node.Konfiguration#getClientContainer(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ArrayList<ElementContainer> getClientContainer(GraphDocument doc) {
		return getConnectedContainer(Anwendungsbaustein.class, doc);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.node.Konfiguration#getServerContainer(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ArrayList<ElementContainer> getServerContainer(GraphDocument doc) {
		return getConnectedContainer(PhysischerDVBaustein.class, doc);
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
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(ModelConstants.getDisplayableName(Anwendungsbaustein.class), new NConnectionPanel(Anwendungsbaustein.class, dialog, false, true));
		dialog.addTab(ModelConstants.getDisplayableName(PhysischerDVBaustein.class), new NConnectionPanel(PhysischerDVBaustein.class, dialog, false, true));
		return dialog;
	}


	
}
