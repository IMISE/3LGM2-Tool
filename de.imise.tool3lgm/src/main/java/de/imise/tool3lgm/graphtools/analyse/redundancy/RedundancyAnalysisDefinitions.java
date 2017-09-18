package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.consistency.CardinalityDefinition;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.path.MetaPath;

/**
 * Definition aller {@link RedundancyAnalysis}
 *
 * @author AXS (16.09.2017)
 */
public class RedundancyAnalysisDefinitions {

    /** Die Einzel-Definitionen als Liste */
    private List<SingleRedundancyAnalysisDefinition> redundancyAnalysisDefinitionData;

    /**
     * @param metaPath
     *            Pfad, der angibt, für welche Elementart welche verbundenen Elemente als redundant angesehen werden sollen.
     *            Die Ausgangselementart ist die Startelementart des Pfades und die über den Pfad verbundenen Elemente sind
     *            die potenziell redundanten Elemente.
     */
    public SingleRedundancyAnalysisDefinition add(final MetaPath metaPath) {
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
         * Pfad, der angibt, für welche Elementart welche verbundenen Elemente als redundant angesehen werden sollen.
         * Die Ausgangselementart ist die Startelementart des Pfades und die über den Pfad verbundenen Elemente sind
         * die potenziell redundanten Elemente.
         */
        private final MetaPath metaPath;

        public SingleRedundancyAnalysisDefinition(final MetaPath metaPath) {
            this.metaPath = metaPath;
            cardinalityDefinition = new CardinalityDefinition();
            cardinalityDefinition.filterNewCardinalities = true;
            //alle Standardkardinalitäten der Kanten des MetaPfades zur cardinalityDefinition hinzufügen
            for (Class<? extends Edge> edgeClass : metaPath.getEdgeClasses()) {
                cardinalityDefinition.setNewStartToEndCardinality(edgeClass, Edge.getStartToEndCardinality(edgeClass));
                cardinalityDefinition.setNewEndToStartCardinality(edgeClass, Edge.getEndToStartCardinality(edgeClass));
            }
        }

        public MetaPath getMetaPath() {
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
        public void setNewStartToEndCardinality(final Class<? extends Edge> edgeClass, final EdgeCardinality edgeCardinality) {
            cardinalityDefinition.setNewStartToEndCardinality(edgeClass, edgeCardinality);
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
        public void setNewEndToStartCardinality(final Class<? extends Edge> edgeClass, final EdgeCardinality edgeCardinality) {
            cardinalityDefinition.setNewEndToStartCardinality(edgeClass, edgeCardinality);
        }

        /**
         * @param edgeClass
         * @return
         */
        public final EdgeCardinality getNewStartToEndCardinality(final Class<? extends Edge> edgeClass) {
            return cardinalityDefinition.getStartToEndCardinality(edgeClass);
        }

        /**
         * @param edgeClass
         * @return
         */
        public final EdgeCardinality getNewEndToStartCardinality(final Class<? extends Edge> edgeClass) {
            return cardinalityDefinition.getEndToStartCardinality(edgeClass);
        }

    }
}
