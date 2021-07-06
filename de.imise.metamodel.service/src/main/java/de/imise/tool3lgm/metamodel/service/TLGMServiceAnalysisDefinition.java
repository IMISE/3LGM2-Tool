package de.imise.tool3lgm.metamodel.service;

import de.imise.tool3lgm.graphtools.metamodel.AnalysesDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;

public class TLGMServiceAnalysisDefinition extends AnalysesDefinition {

    public TLGMServiceAnalysisDefinition(final MetaModel metaModel) {
        super(metaModel);
    }

    @Override
    public String getXMLAnalysisRepositoryFileName() {
        //es gibt eine Analysendatei mit vollem Namen "Service_Tool3lgm.analysis",
        //die über diesen BaseName durch das Tool gefunden werden.
        return "Service_Tool3lgm.analysis";
    }

}
