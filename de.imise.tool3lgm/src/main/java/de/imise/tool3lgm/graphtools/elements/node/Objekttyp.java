package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.OTAPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.TabbedPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class Objekttyp extends Knoten {
	
	/**
	 * 
	 */
	public Objekttyp() {
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
		//dialog.addTab(getResourceBundle().getString("merkm_p"),new
		// MerkmalPanel(dialog));
		dialog.addTab(getResString("Aufgabe_p"), new OTAPanel(Aufgabe.class, dialog));
		dialog.addTab(getResString("Repraesentationsform_p"), new TabbedPanel(dialog,
			new NConnectionPanel(Nachrichtentyp.class, getResString(Nachrichtentyp.class.getSimpleName()), dialog, false, true),
			new NConnectionPanel(Dokumententyp.class, getResString(Dokumententyp.class.getSimpleName()), dialog, false, true),
			new NConnectionPanel(Datensatztyp.class, getResString(Datensatztyp.class.getSimpleName()), dialog, false, true)
		));
		return dialog;
	}

	/**
	 * @param databases
	 * @param collections
	 * @return
	 */
	public ArrayList<ModelElement> getStorePlaces(boolean databases, boolean collections) {
		ArrayList<ModelElement> returnList = new ArrayList<ModelElement>();
		if (!databases && !collections)
			return returnList;
		if (databases) {
			for (ModelElement datasetType : getConnectedElements(Datensatztyp.class)){
				for (ModelElement dbs :  datasetType.getConnectedElements(Datenbanksystem.class)){
					if (!returnList.contains(dbs))
						returnList.add(dbs);
				}
			}
		}
		if (collections) {
			for (ModelElement dokType : getConnectedElements(Dokumententyp.class))
				for (ModelElement dokSam :  dokType.getConnectedElements(Dokumentensammlung.class)){
					if (!returnList.contains(dokSam))
						returnList.add(dokSam);
			}
		}
		//Liste in die die Objekttypen kommen, von denen
		// dieser Objekttyp Repräsentationsformen erbt
		ArrayList<ModelElement> partsAndParents  = getParentElements(false);
		//für jeden OT von dem geerbt wird
		for (ModelElement me : partsAndParents){
			//alle storePlaces holen
			ArrayList<ModelElement> stores = ((Objekttyp) me).getStorePlaces(databases, collections);
			//wenn diese bisher noch nicht eingesammelt wurden -> zur
			// returnList hinzufügen
			for (ModelElement store : stores)
				if (!returnList.contains(store))
					returnList.add(store);
		}
		return returnList;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Knoten#getRedundanceTypes(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ArrayList<ElementContainer> getRedundanceTypes(GraphDocument doc) {
		ArrayList<ModelElement> storePlaces = getStorePlaces(true, true);
		ArrayList<ElementContainer> storePlacesContainer = new ArrayList<ElementContainer>(storePlaces.size());
		for (ModelElement me : storePlaces){
			ElementContainer ec = me.getContainer(doc);
			if (ec!=null)
				storePlacesContainer.add(ec);
		}	
		return storePlacesContainer;
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

}
