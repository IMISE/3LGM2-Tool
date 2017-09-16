package de.imise.tool3lgm.graphtools.metamodel;

import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysisDefinitions;

public abstract class AnalysisDefinition {

    public AnalysisDefinition() {
    }

    protected final SimpleRedundancyAnalysisDefinitions simpleRedundancyAnalysisDefinition = new SimpleRedundancyAnalysisDefinitions();

    public SimpleRedundancyAnalysisDefinitions getSimpleRedundancyAnalysisDefinition() {
        return simpleRedundancyAnalysisDefinition;
    }

}
