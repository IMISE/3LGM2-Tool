package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AufObjVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjReprVerbindung;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class Objekttyp extends Knoten {

    /**
     *
     */
    public Objekttyp() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addDescripSingleConnectionPanel(true, ObjLogspVerbindung.class);
        dialog.addEdgePanel(AufObjVerbindung.class);
        dialog.addTabbedPanel("Repraesentationsform_p");
        dialog.addTabbedPanelPathConnectionPanel(Nachrichtentyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Dokumententyp.class, ObjReprVerbindung.class);
        dialog.addTabbedPanelPathConnectionPanel(Datensatztyp.class, ObjReprVerbindung.class);
        return dialog;
    }

    /**
     * @param databases
     * @param collections
     * @return
     */
    public ArrayList<ModelElement> getStorePlaces(final boolean databases, final boolean collections) {
        ArrayList<ModelElement> returnList = new ArrayList<ModelElement>();
        if (!databases && !collections) {
            return returnList;
        }
        if (databases) {
            for (ModelElement datasetType : getConnectedElements(Datensatztyp.class)) {
                for (ModelElement dbs : datasetType.getConnectedElements(Datenbanksystem.class)) {
                    if (!returnList.contains(dbs)) {
                        returnList.add(dbs);
                    }
                }
            }
        }
        if (collections) {
            for (ModelElement dokType : getConnectedElements(Dokumententyp.class)) {
                for (ModelElement dokSam : dokType.getConnectedElements(Dokumentensammlung.class)) {
                    if (!returnList.contains(dokSam)) {
                        returnList.add(dokSam);
                    }
                }
            }
        }
        //Liste in die die Objekttypen kommen, von denen
        // dieser Objekttyp Repräsentationsformen erbt
        ArrayList<ModelElement> partsAndParents = getParentElements(false);
        //für jeden OT von dem geerbt wird
        for (ModelElement me : partsAndParents) {
            //alle storePlaces holen
            ArrayList<ModelElement> stores = ((Objekttyp) me).getStorePlaces(databases, collections);
            //wenn diese bisher noch nicht eingesammelt wurden -> zur
            // returnList hinzufügen
            for (ModelElement store : stores) {
                if (!returnList.contains(store)) {
                    returnList.add(store);
                }
            }
        }
        return returnList;
    }

    @Override
    public ArrayList<ElementContainer> getRedundanceTypes(final GraphDocument doc) {
        ArrayList<ModelElement> storePlaces = getStorePlaces(true, true);
        ArrayList<ElementContainer> storePlacesContainer = new ArrayList<ElementContainer>(storePlaces.size());
        for (ModelElement me : storePlaces) {
            ElementContainer ec = me.getContainer(doc);
            if (ec != null) {
                storePlacesContainer.add(ec);
            }
        }
        return storePlacesContainer;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

}
