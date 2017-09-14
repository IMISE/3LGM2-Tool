package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.elements.AnalysisDefinition;
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
    }

    private final MetaPath functionToConfigurationRedundancy = new MetaPath(Aufgabe.class, ABKonfiguration.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class);

    private final MetaPath functionToConfigurationRedundancyDifference = new MetaPath(ABKonfiguration.class, Anwendungsbaustein.class, AwbAwbkVerbindung.class);

    private final MetaPath objecttypeToStoreplaceRedundancy = new MetaPath(Objekttyp.class, LogischerSpeicher.class, ObjLogspVerbindung.class);

    @Override
    public ImmutableList<Triple<MetaPath, MetaPath, Boolean>> getSimpleRedundancyAnalysisDefinition() {
        return ImmutableList.of(new ImmutableTriple<>(functionToConfigurationRedundancy, functionToConfigurationRedundancyDifference, true), new ImmutableTriple<>(objecttypeToStoreplaceRedundancy, null, true));
    }

}
