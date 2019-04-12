package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.ExtrasActionsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DatenuebertragungsVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntDotVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntEtVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntKommstVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntNatVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KawbAwbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KawbOrgpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjReprVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbBtypVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbRawbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SubnNetztVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.SwpAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteintyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBVerwaltungssystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datensatztyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Dokumententyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Ereignistyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EtntEtdtKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Kommunikationsstandard;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.LogischerSpeicher;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Nachrichtentyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Netzprotokoll;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Netztyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationseinheit;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationsplan;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Prozess;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Repraesentationsform;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Schnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Softwareprodukt;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Standort;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Subnetz;

@SuppressWarnings({
        "unchecked", "rawtypes"
})
public class TLGMOriginalMetaModel extends MetaModel {

    @Override
    protected final void putOldToNewClassNames() {
        putOldToNewClassName("KnickpunktKnoten", "Knickpunkt");
        putOldToNewClassName("TextfeldFach", "Textfield");
        putOldToNewClassName("TextfeldLog", "Textfield");
        putOldToNewClassName("TextfeldPhy", "Textfield");
        putOldToNewClassName("RawbAwbVerbindung", "RawbRawbVerbindung");
        putOldToNewClassName("EtntKombination", "EreignisNachrichtenTyp");
        putOldToNewClassName("EtdtKombination", "EreignisDokumentenTyp");
        putOldToNewClassName("ETNTKombination", "EreignisNachrichtenTyp");
        putOldToNewClassName("ETDTKombination", "EreignisDokumentenTyp");
        putOldToNewClassName("AwbKawbVerbindung", "KawbAwbVerbindung");
    }

    /////////////////////
    // PathsDefinition //
    /////////////////////

    @Override
    public MetaPathDefinition createPathsDefinition() {
        return new TLGMOriginalPathsDefinition();
    }

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    @Override
    public final GraphViewDefinition createGraphViewDefinition() {
        return new TLGMOriginalGraphViewDefinion();
    }

    //////////////////////
    // CopyDependencies //
    //////////////////////

    @Override
    public final CopyDependencies createCopyDependencies() {
        return new TLGMOriginalCopyDependencies();
    }

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    @Override
    protected Class<? extends AnalysisDefinition> getAnalysisDefinitionClass() {
        return TLGMOriginalAnalysisDefinition.class;
    }

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    @Override
    protected Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass() {
        return TLGMOriginalExtrasActionsDefinition.class;
    }

    ////////////
    // Node //
    ////////////

    /** Alle Node der FE als Array */
    private static final Class[] ALL_DOMAIN_LAYER_NODES = {
            Aufgabe.class, AufOrgKombination.class, Objekttyp.class, Organisationseinheit.class, Prozess.class,
    };

    /** Alle Node zw. FE und LWE als Array */
    private final Class[] ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES = {
            ABKonfiguration.class,
    };

    /** Alle Node der LWE als Array */
    private final Class[] ALL_LOGICAL_LAYER_NODES = {
            RechAnwendungsbaustein.class,
            KonAnwendungsbaustein.class,
            Anwendungsprogramm.class,
            Bausteinschnittstelle.class,
            Benutzungsschnittstelle.class,
            Datenbanksystem.class,
            Datensatztyp.class,
            DBVerwaltungssystem.class,
            Dokumentensammlung.class,
            Dokumententyp.class,
            Ereignistyp.class,
            Kommunikationsstandard.class,
            Nachrichtentyp.class,
            Organisationsplan.class,
            Softwareprodukt.class,
            EreignisNachrichtenTyp.class,
            EreignisDokumentenTyp.class,

            //auch die Assoziationsklasse hier eintagen
            KommBeziehung.class,

            //abstracte Node müssen hier auch eingetragen werden
            Anwendungsbaustein.class,
            EtntEtdtKombination.class,
            Schnittstelle.class,
            Repraesentationsform.class,
            LogischerSpeicher.class,
    };

    /** Alle Node zw. LWE und PWE als Array */
    private final Class[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES = {
            DBKonfiguration.class,
    };

    /** Alle Node der PWE als Array */
    private final Class[] ALL_PHYSICAL_LAYER_NODES = {
            PhysischerDVBaustein.class, Standort.class, Bausteintyp.class, Netztyp.class, Subnetz.class, Netzprotokoll.class,
    };

    @Override
    public final Class[] getAllDomainLayerNodes() {
        return ALL_DOMAIN_LAYER_NODES;
    }

    @Override
    public final Class[] getAllInterDomainLogicalLayerNodes() {
        return ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getAllLogicalLayerNodes() {
        return ALL_LOGICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getAllInterLogicalPhysicalLayerNodes() {
        return ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getAllPhysicalLayerNodes() {
        return ALL_PHYSICAL_LAYER_NODES;
    }

    @Override
    public Class[] getTreeLogicalLayerVisibleAbstractNodes() {
        return new Class[] {
                //nur bei Anwendungsbausteinen soll die abstrakte Oberklasse im Baum angezeigt werden
                Anwendungsbaustein.class
        };
    }

    ////////////
    // Kanten //
    ////////////

    @Override
    public Class[] getAllEdges() {
        return new Class[] {
                //FE
                AufAufOrgVerbindung.class,
                AufAufVerbindung.class,
                AufObjVerbindung.class,
                ObjObjVerbindung.class,
                OrgAufOrgVerbindung.class,
                OrgOrgVerbindung.class,
                PrzAufVerbindung.class,
                //FE - LWE
                AwbAwbkVerbindung.class,
                AwbkAufOrgVerbindung.class,
                EtAufVerbindung.class,
                ObjLogspVerbindung.class,
                ObjReprVerbindung.class,
                SwpAufVerbindung.class,
                //LWE
                KawbAwbVerbindung.class,
                AwbKommssVerbindung.class,
                AwpSwpVerbindung.class,
                BssEtntVerbindung.class,
                BssKommstVerbindung.class,
                DbsDatVerbindung.class,
                DbsDbvsVerbindung.class,
                DoksDokVerbindung.class,
                EtntDotVerbindung.class,
                EtntEtVerbindung.class,
                EtntKommstVerbindung.class,
                EtntNatVerbindung.class,
                KawbDoksVerbindung.class,
                KawbOrgpVerbindung.class,
                KommbezEtntVerbindung.class,
                KommBeziehung.class,
                RawbRawbVerbindung.class,
                RawbAwpVerbindung.class,
                RawbDbsVerbindung.class,
                //LWE - PWE
                PdvbkAwbVerbindung.class,
                PdvbPdvbkVerbindung.class,
                //PWE
                DatenuebertragungsVerbindung.class,
                PdvbBtypVerbindung.class,
                PdvbPdvbVerbindung.class,
                PdvbStoVerbindung.class,
                PdvbSubnVerbindung.class,
                //          PdvbVirtualPdvbVerbindung.class,
                SubnNetzpVerbindung.class,
                SubnNetztVerbindung.class
        };
    }

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    private final Class[] IMPORTABLE_NODES = {
            Aufgabe.class,
            Bausteintyp.class,
            DBVerwaltungssystem.class,
            Dokumententyp.class,
            Ereignistyp.class,
            KommBeziehung.class,
            Kommunikationsstandard.class,
            Nachrichtentyp.class,
            Netzprotokoll.class,
            Netztyp.class,
            Objekttyp.class,
            Organisationseinheit.class,
            Organisationsplan.class,
            KonAnwendungsbaustein.class,
            PhysischerDVBaustein.class,
            RechAnwendungsbaustein.class,
            Softwareprodukt.class,
            Standort.class,
            Subnetz.class,
    };

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    @Override
    public final Class<? extends ModelElement>[] getImportableNodes() {
        return IMPORTABLE_NODES;
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    private final Set<Class<? extends ModelElement>> GENERATE_NAME_CLASSES = ImmutableSet.<Class<? extends ModelElement>> of(AufOrgKombination.class, EtntEtdtKombination.class);

    @Override
    public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
        return GENERATE_NAME_CLASSES;
    }

    @Override
    protected Collection<SimpleMetaPath> getCreatablePaths() {
        return ImmutableList.of();
    }

}
