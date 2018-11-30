package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;

import javax.swing.Action;

import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions.SingleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.analyse.DataAvailabilityFinder;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.analyse.InterfaceCanSendOTAnalysis;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbAwpVerbindung;
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

public class TLGMOriginalAnalysisDefinition extends AnalysisDefinition {

    public TLGMOriginalAnalysisDefinition() {
        initSimpleRedundancyAnalysis();
        initRedundancyAnalysis();
        initNodeAnalysis();
    }

    private void initSimpleRedundancyAnalysis() {
        AbstractMetaPath functionToConfigurationRedundancy = SimpleMetaPath.create(Aufgabe.class, ABKonfiguration.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class);
        AbstractMetaPath functionToConfigurationRedundancyDifference = SimpleMetaPath.create(ABKonfiguration.class, Anwendungsbaustein.class, AwbAwbkVerbindung.class);
        AbstractMetaPath objecttypeToStoreplaceRedundancy = SimpleMetaPath.create(Objekttyp.class, LogischerSpeicher.class, ObjLogspVerbindung.class);
        simpleRedundancyAnalysisDefinitions.add(functionToConfigurationRedundancy, functionToConfigurationRedundancyDifference, true);
        simpleRedundancyAnalysisDefinitions.add(objecttypeToStoreplaceRedundancy, true);
    }

    private void initRedundancyAnalysis() {
        //Analyse 1: Anwendungsbausteine bezüglich Aufgaben
        SingleRedundancyAnalysisDefinition analyse = redundancyAnalysisDefinitions.add(SimpleMetaPath.create(Anwendungsbaustein.class, Aufgabe.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class));
        //es darf immer nur ein Anwendungsbaustein an jeder Konfiguration hängen, damit die Analyse ein interpretierbares Ergebnis liefert -> Kardinalitäten einschränken
        analyse.setNewForwardCardinality(AwbAwbkVerbindung.class, ONE_ONE);
        //Bei Anwenudngsbausteinen sollen in der Ausgabe der Analyse hinter dem Namen in Klammern alle verbundenen Softwareprodukte aufgelistet werden -> dieser Pfad muss als Namenserweiterungspfad angegeben werden
        AbstractMetaPath awbExpandedNamePath = SimpleMetaPath.create(RechAnwendungsbaustein.class, Softwareprodukt.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class);
        analyse.addExpandedNamePath(awbExpandedNamePath);

        //Analyse 2: Nur Rechnerbasierte Anwendungsbausteine bezüglich Aufgaben (analog zu oben)
        analyse = redundancyAnalysisDefinitions.add(SimpleMetaPath.create(RechAnwendungsbaustein.class, Aufgabe.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class));
        analyse.setNewForwardCardinality(AwbAwbkVerbindung.class, ONE_ONE);
        analyse.addExpandedNamePath(awbExpandedNamePath);

        //Analyse 3: Nur Konventionelle Anwendungsbausteine bezüglich Aufgaben (analog zu oben, aber ohne Namenserweiterung, weil es ja keine verbundenen Softwareprodukte hier gibt)
        analyse = redundancyAnalysisDefinitions.add(SimpleMetaPath.create(KonAnwendungsbaustein.class, Aufgabe.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class));
        analyse.setNewForwardCardinality(AwbAwbkVerbindung.class, ONE_ONE);

        //Analyse 4: Softwareprodukte bezüglich Aufgaben
        redundancyAnalysisDefinitions.add(SimpleMetaPath.create(Softwareprodukt.class, Aufgabe.class, SwpAufVerbindung.class));

        //Analyse 5: Datenbanksysteme bezüglich Objekttypen
        redundancyAnalysisDefinitions.add(SimpleMetaPath.create(Datenbanksystem.class, Objekttyp.class, ObjLogspVerbindung.class));
    }

    private void initNodeAnalysis() {
        nodeAnalysis.add(new InterfaceCanSendOTAnalysis());
    }

    @Override
    public Action[] getAnalysisActions() {
        return new Action[] {
                DataAvailabilityFinder.getAction()
        };
    }

}
