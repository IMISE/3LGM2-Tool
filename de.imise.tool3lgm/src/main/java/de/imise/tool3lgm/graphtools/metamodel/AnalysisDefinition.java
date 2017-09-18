package de.imise.tool3lgm.graphtools.metamodel;

import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions;
import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions;

public abstract class AnalysisDefinition {

    public AnalysisDefinition() {
    }

    protected final SimpleRedundancyAnalysisDefinitions simpleRedundancyAnalysisDefinitions = new SimpleRedundancyAnalysisDefinitions();

    public SimpleRedundancyAnalysisDefinitions getSimpleRedundancyAnalysisDefinitions() {
        return simpleRedundancyAnalysisDefinitions;
    }

    protected final RedundancyAnalysisDefinitions redundancyAnalysisDefinitions = new RedundancyAnalysisDefinitions();

    public RedundancyAnalysisDefinitions getRedundancyAnalysisDefinitions() {
        return redundancyAnalysisDefinitions;
    }

}
