package de.imise.tool3lgm.graphtools.metamodel;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.analyse.context.AbstractAnalyse;
import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions;

public abstract class AnalysisDefinition {

    /** Definition aller SimpleRedundancyAnalysis für dieses Metamodell */
    protected final SimpleRedundancyAnalysisDefinitions simpleRedundancyAnalysisDefinitions = new SimpleRedundancyAnalysisDefinitions();

    /** Liefert die Definition aller SimpleRedundancyAnalysis für dieses Metamodell */
    public SimpleRedundancyAnalysisDefinitions getSimpleRedundancyAnalysisDefinitions() {
        return simpleRedundancyAnalysisDefinitions;
    }

    /** Definition aller RedundancyAnalysis für dieses Metamodell */
    protected final RedundancyAnalysisDefinitions redundancyAnalysisDefinitions = new RedundancyAnalysisDefinitions();

    /** Liefert die Definition aller RedundancyAnalysis für dieses Metamodell */
    public RedundancyAnalysisDefinitions getRedundancyAnalysisDefinitions() {
        return redundancyAnalysisDefinitions;
    }

    /** Liste aller Analysen, die im Kontextmenü der Knoten zusätzlich zu denen im AnalyseRepository definierten angezeigt werden sollen */
    protected final List<AbstractAnalyse> nodeAnalysis = new ArrayList<>();

    public List<AbstractAnalyse> getNodeAnalysis() {
        return nodeAnalysis;
    }

}
