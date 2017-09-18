package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.path.MetaPath;

/**
 * Definition aller {@link SimpleRedundancyAnalysis}
 *
 * @author AXS (15.09.2017)
 */
public final class SimpleRedundancyAnalysisDefinitions {

    private List<SingleSimpleRedundancyAnalysisDefinition> simpleRedundancyAnalysisDefinitionData;

    /**
     * @param metaPath
     *            Pfad, der angibt, für welche Elementart welche verbundenen Elemente als redundant angesehen werden sollen.
     *            Die Ausgangselementart ist die Startelementart des Pfades und die über den Pfad verbundenen Elemente sind
     *            die potenziell redundanten Elemente.
     * @param showFullSystemResults
     *            Wenn <code>true</code>, dann wird das Gesamtergebnis oben an den Layer geschrieben.
     */
    public void add(final MetaPath metaPath, final boolean showFullSystemResults) {
        add(metaPath, null, showFullSystemResults);
    }

    /**
     * @param metaPath
     *            Pfad, der angibt, für welche Elementart welche verbundenen Elemente als redundant angesehen werden sollen.
     *            Die Ausgangselementart ist die Startelementart des Pfades und die über den Pfad verbundenen Elemente sind
     *            die potenziell redundanten Elemente.
     * @param pathToDifferences
     *            Wird hier ein gültiger Pfad angegeben, müssen sich die über den metaPath verbundenen Elemente in den über diesen
     *            Pfad verbundenen Elementen unterscheiden, um nicht als dasselbe Element zu gelten.
     * @param showFullSystemResults
     *            Wenn <code>true</code>, dann wird das Gesamtergebnis oben an den Layer geschrieben.
     */
    public void add(final MetaPath metaPath, final MetaPath pathToDifferences, final boolean showFullSystemResults) {
        if (simpleRedundancyAnalysisDefinitionData == null) {
            simpleRedundancyAnalysisDefinitionData = new ArrayList<>();
        }
        SingleSimpleRedundancyAnalysisDefinition singleDefinition = new SingleSimpleRedundancyAnalysisDefinition(metaPath, pathToDifferences, showFullSystemResults);
        simpleRedundancyAnalysisDefinitionData.add(singleDefinition);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return simpleRedundancyAnalysisDefinitionData == null ? 0 : simpleRedundancyAnalysisDefinitionData.size();
    }

    public SingleSimpleRedundancyAnalysisDefinition get(final int index) {
        return simpleRedundancyAnalysisDefinitionData.get(index);
    }

    /**
     * Datenobjekt für eine einzelne {@link SimpleRedundancyAnalysis}
     *
     * @author AXS (15.09.2017)
     */
    public class SingleSimpleRedundancyAnalysisDefinition {

        /**
         * Pfad, der angibt, für welche Elementart welche verbundenen Elemente als redundant angesehen werden sollen.
         * Die Ausgangselementart ist die Startelementart des Pfades und die über den Pfad verbundenen Elemente sind
         * die potenziell redundanten Elemente.
         */
        private final MetaPath metaPath;

        /**
         * Wird hier ein gültiger Pfad angegeben, müssen sich die über den metaPath verbundenen Elemente in den über diesen
         * Pfad verbundenen Elementen unterscheiden, um nicht als dasselbe Element zu gelten.
         */
        private final MetaPath pathToDifferences;

        /**
         * Wenn <code>true</code>, dann wird das Gesamtergebnis oben an den Layer geschrieben.
         */
        private final boolean showFullSystemResults;

        public SingleSimpleRedundancyAnalysisDefinition(final MetaPath metaPath, final MetaPath pathToDifferences, final boolean showFullSystemResults) {
            this.metaPath = metaPath;
            this.pathToDifferences = pathToDifferences;
            this.showFullSystemResults = showFullSystemResults;
        }

        public MetaPath getMetaPath() {
            return metaPath;
        }

        public MetaPath getPathToDifferences() {
            return pathToDifferences;
        }

        public boolean isShowFullSystemResults() {
            return showFullSystemResults;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + (metaPath == null ? 0 : metaPath.hashCode());
            result = prime * result + (pathToDifferences == null ? 0 : pathToDifferences.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SingleSimpleRedundancyAnalysisDefinition other = (SingleSimpleRedundancyAnalysisDefinition) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (metaPath == null) {
                if (other.metaPath != null) {
                    return false;
                }
            } else if (!metaPath.equals(other.metaPath)) {
                return false;
            }
            if (pathToDifferences == null) {
                if (other.pathToDifferences != null) {
                    return false;
                }
            } else if (!pathToDifferences.equals(other.pathToDifferences)) {
                return false;
            }
            return true;
        }

        private SimpleRedundancyAnalysisDefinitions getOuterType() {
            return SimpleRedundancyAnalysisDefinitions.this;
        }

    }

}
