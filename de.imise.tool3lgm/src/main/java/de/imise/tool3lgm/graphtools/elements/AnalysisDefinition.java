package de.imise.tool3lgm.graphtools.elements;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.analyse.redundancy.SimpleRedundancyAnalysis;
import de.imise.tool3lgm.graphtools.path.MetaPath;

public abstract class AnalysisDefinition {

    public AnalysisDefinition() {
    }

    /**
     * Liefert die Definitionen für die {@link SimpleRedundancyAnalysis}
     *
     * @return
     */
    public ImmutableList<Triple<MetaPath, MetaPath, Boolean>> getSimpleRedundancyAnalysisDefinition() {
        return ImmutableList.of();
    }

}
