package de.imise.tool3lgm.graphtools.analyse.redundancy;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions.SingleSimpleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.IMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Diese Klasse führt die Redundnazanalyse, die Dr. Birgit Brigl 1995 in "Brigl
 * B, Hübner-Bloder G, Wendt T, Haux R, Winter A. (2005). Architectural Quality
 * Criteria for Hospital Information Systems" veröffentlicht hat. Es geht um
 * Funktionale Redundanzrate und Funktionale Untersättigungsrate sowie um
 * Datenredundanz von Objekttypen. Das ganze ist eine Art Vorläufer zur
 * "richtigen" Funktionalen Redundanzanalyse.
 *
 * @author AXS
 */
public class SimpleRedundancyAnalysis {

    /** Die Definition dieser Analyse */
    private final SingleSimpleRedundancyAnalysisDefinition definition;

    /**
     * Das Teilmodell, dessen Redundanz ausgerechnet werden soll
     */
    private final GraphDocument doc;

    /**
     * @param definition
     * @param doc
     */
    public SimpleRedundancyAnalysis(final SingleSimpleRedundancyAnalysisDefinition definition, final GraphDocument doc) {
        this.definition = definition;
        this.doc = doc;
    }

    public void computeRedundancy() {
        List<ElementContainer> allPartContainer = getAllAbsolutePartContainer();

        // Aufgabe: Gesamtanzahl der Konfigurationen an den Blättern (Aufgaben ohne Teilaufgaben)
        // Objekttyp:Gesamtanzahl der Datenbanken "an den Blättern" (Objekttypen ohne Teile)
        // ACHTUNG: haben 2 Aufgaben dieselbe Konfiguration, wird die Konfiguration auch 2x gezählt!
        // ACHTUNG: haben 2 Objekttypen dasselbe DBS, wird das DBS auch 2x gezählt!
        int totalRedundanceTypeElemCount = 0;

        // Aufgabe: Anzahl der Aufgaben in leafs, die gar keine Konfigs besitzen
        // Objekttyp:Anzahl der Objekttypen in leafs, die gar nicht gespeichert werden
        int targetTypesWithoutRedundanceType = 0;

        // Aufgabe: Gesamtanzahl der *redundanten* Konfigurationen an den Blättern
        // Objekttyp:Gesamtanzahl der redundanten DBS an den Blättern
        int totalRedundanceCount = 0;

        for (ElementContainer ec : allPartContainer) {
            // Liste der Elemente holen, die für diesen Container redundant sind
            //#################################
            //das hier musste dekativiert werden, damit nach dem entfernen der Funktion aus Node keine Fehler entstehen. Bei Raktivierung -> Umschreiben
            //List<ElementContainer> redundanceTypes = new ArrayList<>(); //knoten.getRedundanceTypes(doc);
            List<ElementContainer> redundanceTypes = getDifferentRedundanceElements(ec);
            //#################################
            // Anzahl der Elemente in redundanceTypes holen
            int size = redundanceTypes.size();
            // wenn es redundante Elemente besitzt
            if (size > 1) {
                // Gesamtanzahl der redundanten Elemente um die Anzahl des aktuellen Elementes
                // erhöhen
                totalRedundanceCount += size - 1;
                // wenn das aktuelle Element gar keine Verbindungen zu einem evtl. redundanten
                // Element besitzt (Untersättigung)
            } else if (size == 0) {
                targetTypesWithoutRedundanceType++;
            }
            // Aufgabe: Gesamtanzahl der Konfigs um Anzahl der Konfigs der aktuellen Aufgabe erhöhen
            // Objekttyp:Gesamtanzahl der DBS um Anzahl der DBS des aktuellen Objekttyps erhöhen
            totalRedundanceTypeElemCount += size;
            // size auf die Anzahl der redundanten Elemente des aktuellen Elementes setzen
            size--;
            // Aufgaben: in den Containern die Anzahl ihrer redundanten Konfigs setzen
            // Objekttyp:in den Containern die Anzahl ihrer redundanten DBS setzen
            NodeContainer nc = (NodeContainer) ec;
            nc.setVariable(size);
            // Anzahl ihrer redundanten Elemente unten rechts neben den Container schreiben
            nc.setAdditionalTextRightDown(String.valueOf(size));
        }
        if (definition.isShowFullSystemResults()) {
            // Redundanzfaktor des Gesamtsystems berechen
            float redundanceFak = (float) totalRedundanceCount / (float) totalRedundanceTypeElemCount;
            // Untersättigungsfaktor des Gesamtsystems berechen
            float saturationFak = targetTypesWithoutRedundanceType / (float) allPartContainer.size();

            //#################################
            //das hier musste dekativiert werden, damit nach dem entfernen der Funktion aus Node keine Fehler entstehen. Bei Raktivierung -> Umschreiben
            String s = getRedundanceString(redundanceFak, saturationFak);
            //#################################
            IMetaPath metaPath = definition.getMetaPath();
            Class<? extends ModelElement> elementClass = metaPath.getStartClasses().iterator().next();
            MetaModel metaModel = doc.getMetaModel();
            int layer = metaModel.layerFor(elementClass);
            LayerContainer lc = doc.getLayer(layer);
            if (lc != null) {
                lc.setAdditionalTextAbove(this, s);
            }
        }
    }

    /**
     * Entfernt die Ausgaben dieser Analyse an den Elementen und am Layer.
     */
    public void removeGraphTexts() {
        IMetaPath metaPath = definition.getMetaPath();
        Class<? extends ModelElement> elementClass = metaPath.getStartClasses().iterator().next();
        MetaModel metaModel = doc.getMetaModel();
        int layer = metaModel.layerFor(elementClass);
        LayerContainer lc = doc.getLayer(layer);
        if (lc != null) {
            lc.removeAdditionalTextAbove(this);
        }
        for (ElementContainer ec : getAllAbsolutePartContainer()) {
            NodeContainer nc = (NodeContainer) ec;
            nc.setAdditionalTextRightDown(null);
        }
    }

    private List<ElementContainer> getAllAbsolutePartContainer() {
        IMetaPath metaPath = definition.getMetaPath();
        Class<? extends ModelElement> startClass = metaPath.getStartClasses().iterator().next();
        List<ElementContainer> allElemCont = doc.getElementContainers(startClass, true);
        for (int i = allElemCont.size() - 1; i >= 0; i--) {
            ElementContainer ec = allElemCont.get(i);
            ModelElement me = ec.getElement();
            if (me.hasDirectPartContainer(doc)) {
                allElemCont.remove(i);
            }
        }
        return allElemCont;
    }

    private List<ElementContainer> getConnectedInDoc(final ElementContainer ec, final IMetaPath metaPath) {
        List<ElementContainer> connectedElements = new ArrayList<>();
        ModelElement me = ec.getElement();
        Collection<ModelElement> allConnectedElements = metaPath.getConnectedElements(me);
        GraphDocument mainDoc = doc.getCollection().getMainDoc();
        for (ModelElement connected : allConnectedElements) {
            ElementContainer connectedEc = connected.getContainer(connected.isUnique() ? mainDoc : doc);
            if (connectedEc != null) {
                connectedElements.add(connectedEc);
            }
        }
        return connectedElements;
    }

    private List<ElementContainer> getDifferentRedundanceElements(final ElementContainer ec) {
        IMetaPath metaPath = definition.getMetaPath();
        IMetaPath pathToDifferences = definition.getPathToDifferences();
        List<ElementContainer> redundantElements = getConnectedInDoc(ec, metaPath);
        if (pathToDifferences != null) {
            List<List<ElementContainer>> connectedDifferent = new ArrayList<>(redundantElements.size());
            for (ElementContainer redundantElement : redundantElements) {
                connectedDifferent.add(getConnectedInDoc(redundantElement, pathToDifferences));
            }
            for (int i = redundantElements.size() - 1; i > 0; i--) {
                List<ElementContainer> list1 = connectedDifferent.get(i);
                for (int j = i - 1; j >= 0; j--) {
                    List<ElementContainer> list2 = connectedDifferent.get(j);
                    if (isSameSet(list1, list2)) {
                        redundantElements.remove(i);
                        break;
                    }
                }
            }
        }
        return redundantElements;
    }

    private boolean isSameSet(final List<?> list1, final List<?> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        return list1.containsAll(list2);
    }

    private String getRedundanceString(final float redundance, final float saturation) {
        IMetaPath metaPath = definition.getMetaPath();
        StringBuilder sb = new StringBuilder();
        MetaModel metaModel = doc.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        sb.append(elementsNameBuilder.getDisplayablePluralName(metaPath.getStartClasses()));
        sb.append(" -> ");
        sb.append(elementsNameBuilder.getDisplayablePluralName(metaPath.getEndClasses()));
        sb.append(": ");
        sb.append(getResString("SIMPLE_REDUNDNANCY_ANALYSIS_redundancy_factor"));
        sb.append("=");
        sb.append(Float.valueOf(redundance));
        sb.append("   ");
        sb.append(getResString("SIMPLE_REDUNDNANCY_ANALYSIS_saturation_factor"));
        sb.append("=");
        sb.append(Float.valueOf(saturation));
        return sb.toString();
    }

    public boolean hasDefinition(final SingleSimpleRedundancyAnalysisDefinition definition) {
        return this.definition.equals(definition);
    }

}
