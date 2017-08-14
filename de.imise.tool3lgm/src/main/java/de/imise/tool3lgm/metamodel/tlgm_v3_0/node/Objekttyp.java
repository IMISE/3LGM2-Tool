package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjReprVerbindung;

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
    public List<ModelElement> getStorePlaces(final boolean databases, final boolean collections) {
        List<ModelElement> returnList = new ArrayList<>();
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
        List<ModelElement> partsAndParents = getParentElements(false);
        //für jeden OT von dem geerbt wird
        for (ModelElement me : partsAndParents) {
            //alle storePlaces holen
            List<ModelElement> stores = ((Objekttyp) me).getStorePlaces(databases, collections);
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
    public List<ElementContainer> getRedundanceTypes(final GraphDocument doc) {
        List<ModelElement> storePlaces = getStorePlaces(true, true);
        List<ElementContainer> storePlacesContainer = new ArrayList<>(storePlaces.size());
        for (ModelElement me : storePlaces) {
            ElementContainer ec = me.getContainer(doc);
            if (ec != null) {
                storePlacesContainer.add(ec);
            }
        }
        return storePlacesContainer;
    }

}
