package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

public final class ABKonfiguration extends Konfiguration {
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER; 
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.node.Konfiguration#getClientContainer(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ArrayList<ElementContainer> getClientContainer(GraphDocument doc) {
		return getClientContainer(doc, false);
	}

	/**
	 * @param doc
	 * @param testOnlyIfExistOne
	 * @return
	 */
	private ArrayList<ElementContainer> getClientContainer(GraphDocument doc, boolean testOnlyIfExistOne) {
		ArrayList<ElementContainer> v = new ArrayList<ElementContainer>();
		ArrayList<ElementContainer> aufOrg = getConnectedContainer(AufOrgKombination.class, doc);
		if (!testOnlyIfExistOne){
			for (int i = 0; i < aufOrg.size(); i++)  
				v.addAll(((NodeContainer)aufOrg.get(i)).getKnoten().getConnectedContainer(Aufgabe.class, doc));
		}else{
			for (int i = 0; i < aufOrg.size(); i++) { 
				v = ((NodeContainer)aufOrg.get(i)).getKnoten().getConnectedContainer(Aufgabe.class, doc);
				if (v.size()>0)
					return v;
			}
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(final GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("Anwendungsbaustein"), new NConnectionPanel(Anwendungsbaustein.class, dialog, true, true));
		dialog.addTab(getResString("AufOrgKombination"), new NConnectionPanel(AufOrgKombination.class, dialog, false, false));
		return dialog;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.node.Konfiguration#getServerContainer(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ArrayList<ElementContainer> getServerContainer(GraphDocument doc) {
		return getConnectedContainer(Anwendungsbaustein.class, doc);
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