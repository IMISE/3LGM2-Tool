package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.LogischerSpeicher;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;

public class TLGMAnalysisDefinition extends AnalysisDefinition {

    public TLGMAnalysisDefinition() {
        MetaPath functionToConfigurationRedundancy = new MetaPath(Aufgabe.class, ABKonfiguration.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class);
        MetaPath functionToConfigurationRedundancyDifference = new MetaPath(ABKonfiguration.class, Anwendungsbaustein.class, AwbAwbkVerbindung.class);
        MetaPath objecttypeToStoreplaceRedundancy = new MetaPath(Objekttyp.class, LogischerSpeicher.class, ObjLogspVerbindung.class);
        simpleRedundancyAnalysisDefinition.add(functionToConfigurationRedundancy, functionToConfigurationRedundancyDifference, true);
        simpleRedundancyAnalysisDefinition.add(objecttypeToStoreplaceRedundancy, true);
    }

}
