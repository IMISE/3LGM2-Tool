package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.imise.tool3lgm.graphtools.consistency.CardinalityDefinition;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;

/**
 * Definition aller {@link RedundancyAnalysis}
 *
 * @author AXS (16.09.2017)
 */
public final class RedundancyAnalysisDefinitions extends MetaPathDefinition {

    /** Die Einzel-Definitionen als Liste */
    private List<SingleRedundancyAnalysisDefinition> redundancyAnalysisDefinitionData;

    /**
     * @param metaPath
     *            Pfad, der angibt, für welche Elementart welche verbundenen Elemente als redundant angesehen werden sollen.
     *            Die Ausgangselementart ist die Startelementart des Pfades und die über den Pfad verbundenen Elemente sind
     *            die potenziell redundanten Elemente.
     */
    public SingleRedundancyAnalysisDefinition add(final AbstractMetaPath metaPath) {
        if (redundancyAnalysisDefinitionData == null) {
            redundancyAnalysisDefinitionData = new ArrayList<>();
        }
        SingleRedundancyAnalysisDefinition singleDefinition = new SingleRedundancyAnalysisDefinition(metaPath);
        redundancyAnalysisDefinitionData.add(singleDefinition);
        return singleDefinition;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return redundancyAnalysisDefinitionData == null ? 0 : redundancyAnalysisDefinitionData.size();
    }

    public SingleRedundancyAnalysisDefinition get(final int index) {
        return redundancyAnalysisDefinitionData.get(index);
    }

    /**
     * Datenobjekt für eine einzelne {@link SimpleRedundancyAnalysis}
     *
     * @author AXS (16.09.2017)
     */
    public class SingleRedundancyAnalysisDefinition {

        private final CardinalityDefinition cardinalityDefinition;

        /**
         * Wenn hier für ein Element ein MetaPfad angegeben ist, dann wird der Anzeige-Name dieser Elementart bei der
         * Ausgabe des Analysergebnisses um die über den angegebenen MetaPath verbundenen Elemente erweitert.
         * Z.B. kann man für für Anwendungsbausteine einen MetaPfad definieren über das verbundene Anwendungsprogramm
         * hin zu den Softwareprodukten. Wird das getan dann hat der Anzeigename des Anwendungsbausteins die Form
         * "Name des Anwendungsbausteins (Softwareprodukt1, Softwareprodukt2, ...)"
         */
        private Map<Class<? extends ModelElement>, AbstractMetaPath> elementClassToExpandedNamePath;

        /**
         * Pfad, der angibt, für welche Elementart welche verbundenen Elemente als redundant angesehen werden sollen.
         * Die Ausgangselementart ist die Startelementart des Pfades und die über den Pfad verbundenen Elemente sind
         * die potenziell redundanten Elemente.
         */
        private final AbstractMetaPath metaPath;

        private SingleRedundancyAnalysisDefinition(final AbstractMetaPath metaPath) {
            this.metaPath = metaPath;
            if (!isValidRedundancyMetaPath()) {
                throw new Error();
            }
            cardinalityDefinition = new CardinalityDefinition();
            cardinalityDefinition.filterNewCardinalities = true;
            //alle Standardkardinalitäten der Kanten des MetaPfades zur cardinalityDefinition hinzufügen
            List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getSimpleMetaPath();
            if (elementaryMetaPaths != null) {
                for (ElementaryMetaPath elementaryMetaPath : elementaryMetaPaths) {
                    Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
                    cardinalityDefinition.setNewForwardCardinality(edgeClass, Edge.getForwardCardinality(edgeClass));
                    cardinalityDefinition.setNewBackwardCardinality(edgeClass, Edge.getBackwardCardinality(edgeClass));
                }
            }
        }

        public AbstractMetaPath getMetaPath() {
            return metaPath;
        }

        /**
         * Setzt die übergebenen Kardinalitäten für die Vorwärtsrichtung derübergebenen Kantenart. Damit werden die originalen Kardinalitäten bei der
         * Konsistenzprüfung überschrieben.
         * Dieser Mechanismus ist dafür gedacht, für die Redundanzanalyse andere Kardinalitäten vorzugeben, als das Metamodell definiert.
         * Z.B. funktioniert die Redundanzanalyse auf dem originalen 3LGM-Metamodell nur, wenn Anwendungsbausteinkonfigurationen immer nur mit genau
         * einem Anwednungsbaustein verbunden sind. Das originale Metamodell lässt aber beliebig viele dieser Verbindungen zu. Hierüber kann man die
         * "richtige" Anzahl der Verbindungen nur für die Analyse einschränken, so dass sie ein interpretiertbares Ergebnis liefert.
         *
         * @param edgeClass
         *            Kantenklasse für die vom Metamodell abweichende Cardinalitäten angegeben werden
         * @param edgeCardinality
         *            neue Kardinalitäten für die übergebene Kantenklasse
         */
        public void setNewForwardCardinality(final Class<? extends Edge> edgeClass, final EdgeCardinality edgeCardinality) {
            cardinalityDefinition.setNewForwardCardinality(edgeClass, edgeCardinality);
        }

        /**
         * Setzt die übergebenen Kardinalitäten für die Rückwärtsrichtung derübergebenen Kantenart. Damit werden die originalen Kardinalitäten bei der
         * Konsistenzprüfung überschrieben.
         * Dieser Mechanismus ist dafür gedacht, für die Redundanzanalyse andere Kardinalitäten vorzugeben, als das Metamodell definiert.
         * Z.B. funktioniert die Redundanzanalyse auf dem originalen 3LGM-Metamodell nur, wenn Anwendungsbausteinkonfigurationen immer nur mit genau
         * einem Anwednungsbaustein verbunden sind. Das originale Metamodell lässt aber beliebig viele dieser Verbindungen zu. Hierüber kann man die
         * "richtige" Anzahl der Verbindungen nur für die Analyse einschränken, so dass sie ein interpretiertbares Ergebnis liefert.
         *
         * @param edgeClass
         *            Kantenklasse für die vom Metamodell abweichende Kardinalitäten angegeben werden
         * @param edgeCardinality
         *            neue Kardinalitäten für die übergebene Kantenklasse
         */
        public void setNewBackwardCardinality(final Class<? extends Edge> edgeClass, final EdgeCardinality edgeCardinality) {
            cardinalityDefinition.setNewBackwardCardinality(edgeClass, edgeCardinality);
        }

        /**
         * @param edgeClass
         * @return
         */
        public final EdgeCardinality getNewForwardCardinality(final Class<? extends Edge> edgeClass) {
            return cardinalityDefinition.getForwardCardinality(edgeClass);
        }

        /**
         * @param edgeClass
         * @return
         */
        public final EdgeCardinality getNewBackwardCardinality(final Class<? extends Edge> edgeClass) {
            return cardinalityDefinition.getBackwardCardinality(edgeClass);
        }

        /**
         * Wenn hier für ein Element ein MetaPfad angegeben ist, dann wird der Anzeige-Name dieser Elementart bei der
         * Ausgabe des Analysergebnisses um die über den angegebenen MetaPath verbundenen Elemente erweitert.
         * Z.B. kann man für für Anwendungsbausteine einen MetaPfad definieren über das verbundene Anwendungsprogramm
         * hin zu den Softwareprodukten. Wird das getan dann hat der Anzeigename des Anwendungsbausteins die Form
         * "Name des Anwendungsbausteins (Softwareprodukt1, Softwareprodukt2, ...)"
         *
         * @param metaPath
         */
        public void addExpandedNamePath(final AbstractMetaPath metaPath) {
            if (elementClassToExpandedNamePath == null) {
                elementClassToExpandedNamePath = new HashMap<>();
            }
            for (Class<? extends ModelElement> startClass : metaPath.getStartClasses()) {
                for (Class<? extends ModelElement> instanciableAssignableClass : ModelConstants.getInstanciableAssignableClasses(startClass)) {
                    elementClassToExpandedNamePath.put(instanciableAssignableClass, metaPath);
                }
            }
        }

        public AbstractMetaPath getExpandedNamePath(final Class<? extends ModelElement> elementClass) {
            return elementClassToExpandedNamePath != null ? elementClassToExpandedNamePath.get(elementClass) : null;
        }

        public boolean isValidRedundancyMetaPath() {
            return metaPath.getOtherDirection() != null;
        }

    }
}
