package de.imise.tool3lgm.graphtools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.util.Alphabetical;

public class GraphDocumentHandler {

    private GraphDocumentHandler() {
    }

    /**
     * Gibt alle Modellelemente (Knoten oder Kanten) zurück.<br/>
     *
     * @param clazz Klasse der gesuchten Elementart (Knoten oder Kanten)
     * @param includeSubClasses
     *            boolean with true if Vererbung beruecksichtigen; Frage nach allen Anwendungsbausteinen gibt
     *            auch RechAnwendungsbausteine und KonAnwendungsbausteine zurück usw.
     * @param absolutePartsOnly
     *            wenn <code>true</code> werden keine Elemente zurückgegeben, denen über eine Part-Of-Beziehung
     *            Teilelemente zugewiesen sind. Die Teil-Von-Eigenschaft wird nicht für dieses Teilmodell sondern
     *            für das Gesamtmodell geprüft.
     * @param alphabetical
     *            wenn <code>true</code> wird eine alphabetisch sortierte Liste zurückgegeben
     * @return List mit allen gefundenen Elementen
     */
    public static final List<ModelElement> getModelItems(final GraphDocument doc, final Class<? extends ModelElement> clazz, final boolean includeSubClasses, final boolean absolutePartsOnly, final boolean alphabetical) {

        if (clazz == null) {
            return new ArrayList<>(0);
        }

        //Problem: Suche nach Elemenklasse inkl. Unterklassen, wobei Unterklassen unique sein können -> im doc und im mainDoc suchen
        if (!includeSubClasses || clazz == Knickpunkt.class) {
            List<Class<? extends ModelElement>> searchClasses = new ArrayList<>();
            searchClasses.add(clazz);
            return getModelItemsForClasses(ModelConstants.isUnique(clazz) ? doc.getCollection().getMainGraphDocument() : doc, searchClasses, absolutePartsOnly, alphabetical);
            //          return getModelItemsForSingleClass(clazz, absolutePartsOnly, alphabetical);
        }

        List<ModelElement> objects = null;
        List<Class<? extends ModelElement>> searchClassesUnique = new ArrayList<>();
        List<Class<? extends ModelElement>> searchClassesNotUnique = new ArrayList<>();
        for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_ELEMENTS_SET) {
            if (ModelConstants.isAbstract(elementClass)) {
                continue;
            }
            if (clazz.isAssignableFrom(elementClass)) {
                if (ModelConstants.isUnique(elementClass)) {
                    searchClassesUnique.add(elementClass);
                } else {
                    searchClassesNotUnique.add(elementClass);
                }
            }
        }

        if (!(doc instanceof Szenario)) {
            searchClassesUnique.addAll(searchClassesNotUnique);
            searchClassesNotUnique.clear();
        }
        if (searchClassesUnique.size() > 0) {
            objects = getModelItemsForClasses(doc.getCollection().getMainGraphDocument(), searchClassesUnique, absolutePartsOnly, alphabetical);
        }
        if (searchClassesNotUnique.size() > 0) {
            List<ModelElement> elems = getModelItemsForClasses(doc, searchClassesNotUnique, absolutePartsOnly, alphabetical);
            if (objects == null) {
                objects = elems;
            } else {
                objects.addAll(elems);
            }
        }
        return objects;

    }

    /**
     * Liefert alle Elemente der angegebenen Arten im Gesamtmodell.
     * 
     * @param gdcoll
     * @param searchClasses
     * @return
     */
    public static List<ModelElement> getModelItems(final GDCollection gdcoll, final Collection<Class<? extends ModelElement>> searchClasses) {
        return getModelItemsForClasses(gdcoll.getMainGraphDocument(), searchClasses, false, false);
    }

    /**
     * Liefert alle Elemente aller übergebenen Klasse, die im übergebenen Graphdocument vorkommen. Diese Funktion sucht immer nur im genau übergebenen
     * GraphDocument
     *
     * @param doc
     * @param searchClasses
     * @param absolutePartsOnly
     * @param alphabetical
     * @return
     */
    private static List<ModelElement> getModelItemsForClasses(final GraphDocument doc, final Collection<Class<? extends ModelElement>> searchClasses, final boolean absolutePartsOnly, final boolean alphabetical) {

        List<ModelElement> objects = new ArrayList<>();

        //Indizes der zu durchsuchenden Ebenen
        int i1 = Integer.MAX_VALUE;
        int i2 = Integer.MIN_VALUE;

        boolean searchBendpoints = false;
        boolean searchNodes = false;
        boolean searchEdges = false;
        //Indizes der Ebenen so anpassen, dass möglichst wenig durchsucht werden muss
        for (Class<? extends ModelElement> searchClass : searchClasses) {
            if (Knickpunkt.class == searchClass) {
                searchBendpoints = true;
            }
            if (!searchNodes && ModelConstants.isNodeType(searchClass)) {
                searchNodes = true;
            }
            if (!searchEdges && ModelConstants.isEdgeType(searchClass)) {
                searchEdges = true;
            }
            //Ebene der gesuchten Elementklasse bestimmen
            int ebene = ModelConstants.layerFor(searchClass);
            if (ebene < 0) {
                i1 = 0;
                i2 = 5;
                break;
            }
            if (ebene < i1) {
                i1 = ebene;
            }
            if (ebene + 1 > i2) {
                i2 = ebene + 1;
            }
            if (i1 == 0 && i2 == 5) {
                break;
            }
        }

        //alle zu durchsuchenden Ebenen durchlaufen
        for (int i = i1; i < i2; i++) {
            //Ebene holen
            LayerContainer lc = doc.getLayer(i);
            //Liste mit allen Containerlisten der Ebene, die durchsucht werden müssen
            List<List<? extends ElementContainer>> layerElements = new ArrayList<>();
            //Knickpunkte
            if (searchBendpoints) {
                layerElements.add(lc.getKnickpunkte());
            }
            //Knoten, die keine Knickpunkte sind
            if (searchNodes) {
                layerElements.add(alphabetical ? lc.getKnotenAlphabetical() : lc.getKnoten());
            }
            //Kanten
            if (searchEdges) {
                layerElements.add(lc.getKanten());
            }

            //dann wurde oben in layerElements wenigstens eine ElementContainerliste hinzugefügt
            for (List<? extends ElementContainer> ecList : layerElements) {
                //für jede dieser ElementContainerlisten
                for (ElementContainer ec : ecList) {
                    ModelElement me = ec.getElement();
                    //wenn das ModelElement des Conatainers einer gesuchten Klasse entspricht
                    if (searchClasses.contains(me.getClass())) {
                        //die Teil-Von-Eigenschaft wird nicht für dieses Teilmodell sondern für das Gesamtmodell geprüft
                        if (absolutePartsOnly && me.hasDirectPartContainer(doc.getCollection().getMainGraphDocument())) {
                            continue;
                        }
                        //zur Rückgabeliste hinzufügen
                        objects.add(me);
                    }
                }
            }
        }

        //wenn alphabetisch sortiert werden soll und andere Elemente als die bereits in der aplhabetisch sortierten
        //Knotenliste enthaltenen zur Rückgabeliste hinzugefügt wurden
        if (alphabetical && (searchBendpoints || searchEdges)) {
            //aplhabetisch sortieren
            Alphabetical.sort(objects);
        }

        //      long end = System.currentTimeMillis();
        //      System.err.println("getModelItems(" + clazz.getSimpleName() + ", " + includeSubClasses + ", " + alphabetical + ") -> " + (end - start) + " ms " + objects.size() + " Elemente");

        return objects;
    }
}
