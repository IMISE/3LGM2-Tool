package de.imise.tool3lgm.metamodel.original;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ONE_ONE;
import static de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator.createSimpleMetaPath;

import javax.swing.Action;

import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions.SingleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.AnalysesDefinition;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.metamodel.original.analyse.DataAvailabilityFinder;
import de.imise.tool3lgm.metamodel.original.analyse.InterfaceCanSendOTAnalysis;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.SwpAufVerbindung;
import de.imise.tool3lgm.metamodel.original.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.original.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.LogischerSpeicher;
import de.imise.tool3lgm.metamodel.original.node.Objekttyp;
import de.imise.tool3lgm.metamodel.original.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Softwareprodukt;

public class TLGMOriginalAnalysesDefinition extends AnalysesDefinition {

    public TLGMOriginalAnalysesDefinition() {
        initSimpleRedundancyAnalyses();
        initRedundancyAnalyses();
        initNodeAnalyses();
    }

    private void initSimpleRedundancyAnalyses() {
        AbstractMetaPath functionToConfigurationRedundancy = createSimpleMetaPath(Aufgabe.class, ABKonfiguration.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class);
        AbstractMetaPath functionToConfigurationRedundancyDifference = createSimpleMetaPath(ABKonfiguration.class, Anwendungsbaustein.class, AwbAwbkVerbindung.class);
        AbstractMetaPath objecttypeToStoreplaceRedundancy = createSimpleMetaPath(Objekttyp.class, LogischerSpeicher.class, ObjLogspVerbindung.class);
        simpleRedundancyAnalysisDefinitions.add(functionToConfigurationRedundancy, functionToConfigurationRedundancyDifference, true);
        simpleRedundancyAnalysisDefinitions.add(objecttypeToStoreplaceRedundancy, true);
    }

    private void initRedundancyAnalyses() {
        //Analyse 1: Anwendungsbausteine bezüglich Aufgaben
        SingleRedundancyAnalysisDefinition analyse = redundancyAnalysisDefinitions.add(createSimpleMetaPath(Anwendungsbaustein.class, Aufgabe.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class));
        //es darf immer nur ein Anwendungsbaustein an jeder Konfiguration hängen, damit die Analyse ein interpretierbares Ergebnis liefert -> Kardinalitäten einschränken
        analyse.setNewForwardCardinality(AwbAwbkVerbindung.class, ONE_ONE);
        //Bei Anwenudngsbausteinen sollen in der Ausgabe der Analyse hinter dem Namen in Klammern alle verbundenen Softwareprodukte aufgelistet werden -> dieser Pfad muss als Namenserweiterungspfad angegeben werden
        AbstractMetaPath awbExpandedNamePath = createSimpleMetaPath(RechAnwendungsbaustein.class, Softwareprodukt.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class);
        analyse.addExpandedNamePath(awbExpandedNamePath);

        //Analyse 2: Nur Rechnerbasierte Anwendungsbausteine bezüglich Aufgaben (analog zu oben)
        analyse = redundancyAnalysisDefinitions.add(createSimpleMetaPath(RechAnwendungsbaustein.class, Aufgabe.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class));
        analyse.setNewForwardCardinality(AwbAwbkVerbindung.class, ONE_ONE);
        analyse.addExpandedNamePath(awbExpandedNamePath);

        //Analyse 3: Nur Konventionelle Anwendungsbausteine bezüglich Aufgaben (analog zu oben, aber ohne Namenserweiterung, weil es ja keine verbundenen Softwareprodukte hier gibt)
        analyse = redundancyAnalysisDefinitions.add(createSimpleMetaPath(KonAnwendungsbaustein.class, Aufgabe.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, AufAufOrgVerbindung.class));
        analyse.setNewForwardCardinality(AwbAwbkVerbindung.class, ONE_ONE);

        //Analyse 4: Softwareprodukte bezüglich Aufgaben
        redundancyAnalysisDefinitions.add(createSimpleMetaPath(Softwareprodukt.class, Aufgabe.class, SwpAufVerbindung.class));

        //Analyse 5: Datenbanksysteme bezüglich Objekttypen
        redundancyAnalysisDefinitions.add(createSimpleMetaPath(Datenbanksystem.class, Objekttyp.class, ObjLogspVerbindung.class));
    }

    private void initNodeAnalyses() {
        nodeAnalyses.add(new InterfaceCanSendOTAnalysis());
    }

    @Override
    public Action[] getAnalysisActions() {
        return new Action[] {
                DataAvailabilityFinder.getAction()
        };
    }

}
