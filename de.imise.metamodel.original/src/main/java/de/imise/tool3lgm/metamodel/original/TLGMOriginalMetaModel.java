package de.imise.tool3lgm.metamodel.original;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.consistency.ModelValidatorDefinition;
import de.imise.tool3lgm.graphtools.metamodel.AnalysesDefinition;
import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.ExtrasActionsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.RegularMetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AufAufVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.BssEtntVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DatenuebertragungsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtAufVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntDotVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntEtVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntKommstVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntNatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KawbAwbVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KawbOrgpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.original.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbBtypVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbPdvbVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbRawbVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.SubnNetztVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.SwpAufVerbindung;
import de.imise.tool3lgm.metamodel.original.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.original.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.original.node.Bausteintyp;
import de.imise.tool3lgm.metamodel.original.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.metamodel.original.node.DBKonfiguration;
import de.imise.tool3lgm.metamodel.original.node.DBVerwaltungssystem;
import de.imise.tool3lgm.metamodel.original.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.original.node.Datensatztyp;
import de.imise.tool3lgm.metamodel.original.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.original.node.Dokumententyp;
import de.imise.tool3lgm.metamodel.original.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.metamodel.original.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.metamodel.original.node.Ereignistyp;
import de.imise.tool3lgm.metamodel.original.node.EtntEtdtKombination;
import de.imise.tool3lgm.metamodel.original.node.Kommunikationsstandard;
import de.imise.tool3lgm.metamodel.original.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.LogischerSpeicher;
import de.imise.tool3lgm.metamodel.original.node.Nachrichtentyp;
import de.imise.tool3lgm.metamodel.original.node.Netzprotokoll;
import de.imise.tool3lgm.metamodel.original.node.Netztyp;
import de.imise.tool3lgm.metamodel.original.node.Objekttyp;
import de.imise.tool3lgm.metamodel.original.node.Organisationseinheit;
import de.imise.tool3lgm.metamodel.original.node.Organisationsplan;
import de.imise.tool3lgm.metamodel.original.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.original.node.Prozess;
import de.imise.tool3lgm.metamodel.original.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Repraesentationsform;
import de.imise.tool3lgm.metamodel.original.node.Schnittstelle;
import de.imise.tool3lgm.metamodel.original.node.Softwareprodukt;
import de.imise.tool3lgm.metamodel.original.node.Standort;
import de.imise.tool3lgm.metamodel.original.node.Subnetz;

@SuppressWarnings({
        "unchecked", "rawtypes"
})
public class TLGMOriginalMetaModel extends MetaModelDefinition implements RegularMetaModelDefinition {

    /**
     * Muss jedes MetaModell angeben. Diese ID wird genutzt, um die Klasse eindeutig zu identifizieren. Diese ID wird nicht zur Serialisierung per
     * Java genutzt, sondern zur Serialisierung in 3LGM-Modelldateien. Sie wird per Reflection abgefragt - daher findet man nirgends einen direkten
     * Zugriff.
     */
    public static final long serialVersionUID = -6111172173611550491L;

    @Override
    protected final void putOldToNewClassNames() {
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
    public final Class<? extends MetaPathDefinition> getMetaPathsDefinitionClass() {
        return TLGMOriginalPathsDefinition.class;
    }

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    @Override
    public final Class<? extends GraphViewDefinition> getGraphViewDefinitionClass() {
        return TLGMOriginalGraphViewDefinion.class;
    }

    //////////////////////
    // CopyDependencies //
    //////////////////////

    @Override
    public Class<? extends CopyDependencies> getCopyDependenciesClass() {
        return TLGMOriginalCopyDependencies.class;
    }

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    @Override
    public Class<? extends AnalysesDefinition> getAnalysesDefinitionClass() {
        return TLGMOriginalAnalysesDefinition.class;
    }

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    @Override
    public Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass() {
        return TLGMOriginalExtrasActionsDefinition.class;
    }

    //////////////////////////////
    // ModelValidatorDefinition //
    //////////////////////////////

    @Override
    public Class<? extends ModelValidatorDefinition> getModelValidatorDefinitionClass() {
        return TLGMOriginalModelValidatorDefinition.class;
    }

    //////////
    // Node //
    //////////

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

    @Override
    public Set<Class<? extends ModelElement>> getOnlyExpertModeVisibleNodes() {
        //hier wird nur mit contains(class) gerpüft -> immer auch die Oberklassen, die versteckt werden sollen reinschreiben
        return ImmutableSet.of(AufOrgKombination.class);
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

    private final Set<Class<? extends ModelElement>> IMPORTABLE_NODES = ImmutableSet.of(Aufgabe.class, Bausteintyp.class, DBVerwaltungssystem.class, Dokumententyp.class, Ereignistyp.class, KommBeziehung.class, Kommunikationsstandard.class,
            Nachrichtentyp.class, Netzprotokoll.class, Netztyp.class, Objekttyp.class, Organisationseinheit.class, Organisationsplan.class, KonAnwendungsbaustein.class, PhysischerDVBaustein.class, RechAnwendungsbaustein.class, Softwareprodukt.class,
            Standort.class, Subnetz.class);

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    @Override
    public final Set<Class<? extends ModelElement>> getImportableNodes() {
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

}
