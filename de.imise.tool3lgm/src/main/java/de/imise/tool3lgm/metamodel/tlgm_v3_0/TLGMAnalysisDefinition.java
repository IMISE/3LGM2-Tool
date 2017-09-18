package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;

import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions.SingleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SwpAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.LogischerSpeicher;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Softwareprodukt;

public class TLGMAnalysisDefinition extends AnalysisDefinition {

    public TLGMAnalysisDefinition() {
        initSimpleRedundancyAnalysis();
        initRedundancyAnalysis();
    }

    private void initSimpleRedundancyAnalysis() {
        MetaPath functionToConfigurationRedundancy = new MetaPath(Aufgabe.class, ABKonfiguration.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class);
        MetaPath functionToConfigurationRedundancyDifference = new MetaPath(ABKonfiguration.class, Anwendungsbaustein.class, AwbAwbkVerbindung.class);
        MetaPath objecttypeToStoreplaceRedundancy = new MetaPath(Objekttyp.class, LogischerSpeicher.class, ObjLogspVerbindung.class);
        simpleRedundancyAnalysisDefinitions.add(functionToConfigurationRedundancy, functionToConfigurationRedundancyDifference, true);
        simpleRedundancyAnalysisDefinitions.add(objecttypeToStoreplaceRedundancy, true);
    }

    private void initRedundancyAnalysis() {
        SingleRedundancyAnalysisDefinition analyse = redundancyAnalysisDefinitions.add(new MetaPath(Anwendungsbaustein.class, Aufgabe.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));
        analyse.setNewStartToEndCardinality(AwbAwbkVerbindung.class, ONE_ONE);
        analyse = redundancyAnalysisDefinitions.add(new MetaPath(RechAnwendungsbaustein.class, Aufgabe.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));
        analyse.setNewStartToEndCardinality(AwbAwbkVerbindung.class, ONE_ONE);
        analyse = redundancyAnalysisDefinitions.add(new MetaPath(KonAnwendungsbaustein.class, Aufgabe.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));
        analyse.setNewStartToEndCardinality(AwbAwbkVerbindung.class, ONE_ONE);
        redundancyAnalysisDefinitions.add(new MetaPath(Softwareprodukt.class, Aufgabe.class, SwpAufVerbindung.class));
        redundancyAnalysisDefinitions.add(new MetaPath(Datenbanksystem.class, Objekttyp.class, ObjLogspVerbindung.class));
    }

}
