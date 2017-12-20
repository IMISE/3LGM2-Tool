package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ExtrasActionsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKawbVerbindung;
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
        "unchecked",
        "rawtypes"
})
public class TLGMOriginalMetaModel extends MetaModel {

    private final ImmutableMap<String, String> oldToNewName = ImmutableMap.<String, String> builder().put("KnickpunktKnoten", "Knickpunkt").put("RawbAwbVerbindung", "RawbRawbVerbindung").put("AwbKawbVerbindung", "AwbKawbVerbindung")
            .put("EtntKombination", "EreignisNachrichtenTyp").put("EtdtKombination", "EreignisDokumentenTyp").put("ETNTKombination", "EreignisNachrichtenTyp").put("ETDTKombination", "EreignisDokumentenTyp").build();

    @Override
    protected final Map<String, String> getOldToNewClassName() {
        return oldToNewName;
    }

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    private final GraphViewDefinition graphViewDefinition = new TLGMGraphViewDefinion();

    @Override
    public final GraphViewDefinition getGraphViewDefinition() {
        return graphViewDefinition;
    }

    //////////////////////
    // CopyDependencies //
    //////////////////////

    private final CopyDependencies copyDependencies = new TLGMCopyDependencies();

    @Override
    public final CopyDependencies getCopyDependencies() {
        return copyDependencies;
    }

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    @Override
    protected Class<? extends AnalysisDefinition> getAnalysisDefinitionClass() {
        return TLGMAnalysisDefinition.class;
    }

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    @Override
    protected Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass() {
        return TLGMExtrasActionsDefinition.class;
    }

    ////////////
    // Node //
    ////////////

    /** Alle Node der FE als Array */
    private static final Class[] ALL_DOMAIN_LAYER_NODES = {
            Aufgabe.class,
            AufOrgKombination.class,
            Objekttyp.class,
            Organisationseinheit.class,
            Prozess.class,
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
            PhysischerDVBaustein.class,
            Standort.class,
            Bausteintyp.class,
            Netztyp.class,
            Subnetz.class,
            Netzprotokoll.class,
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

    /** Kanten FE */
    private final Class[] ALL_DOMAIN_LAYER_EDGES = {
            AufAufOrgVerbindung.class,
            AufAufVerbindung.class,
            AufObjVerbindung.class,
            ObjObjVerbindung.class,
            OrgAufOrgVerbindung.class,
            OrgOrgVerbindung.class,
            PrzAufVerbindung.class
    };

    /** Kanten Inter FE -LWE */
    private final Class[] ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES = {
            AwbAwbkVerbindung.class,
            AwbkAufOrgVerbindung.class,
            EtAufVerbindung.class,
            ObjLogspVerbindung.class,
            ObjReprVerbindung.class,
            SwpAufVerbindung.class
    };

    /** Kanten LWE */
    private final Class[] ALL_LOGICAL_LAYER_EDGES = {
            AwbKawbVerbindung.class,
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
            RawbDbsVerbindung.class
    };

    /** Kanten Inter LWE - PWE */
    private final Class[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES = {
            PdvbkAwbVerbindung.class,
            PdvbPdvbkVerbindung.class,
    };

    /** Kanten PWE */
    private final Class[] ALL_PHYSICAL_LAYER_EDGES = {
            DatenuebertragungsVerbindung.class,
            PdvbBtypVerbindung.class,
            PdvbPdvbVerbindung.class,
            PdvbStoVerbindung.class,
            PdvbSubnVerbindung.class,
            //          PdvbVirtualPdvbVerbindung.class,
            SubnNetzpVerbindung.class,
            SubnNetztVerbindung.class
    };

    @Override
    public Class[] getAllDomainLayerEdges() {
        return ALL_DOMAIN_LAYER_EDGES;
    }

    @Override
    public Class[] getAllInterDomainLogicalLayerEdges() {
        return ALL_INTER_DOMAIN_LOGICAL_LAYER_EDGES;
    }

    @Override
    public Class[] getAllLogicalLayerEdges() {
        return ALL_LOGICAL_LAYER_EDGES;
    }

    @Override
    public Class[] getAllInterLogicalPhysicalLayerEdges() {
        return ALL_INTER_LOGICAL_PHYSICAL_LAYER_EDGES;
    }

    @Override
    public Class[] getAllPhysicalLayerEdges() {
        return ALL_PHYSICAL_LAYER_EDGES;
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

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

    /**
     * Liste aller Kantenklassen, die eigentlich 2 gerichtete Assoziationen im Metamodell sein müssten, aber aus Unwissenheit beim Entwurf des
     * Metamodells fehlerhafterweise in eine Assoziation verpackt wurden, bei denen die Richtung der Edge
     * (Doppelkante.FORWARD, Doppelkante.BACKWARD, Doppelkante.DOUBLE) die Bedeutung angibt. Nur wegen den 4 braucht man den ganzen
     * Doppelkanten-Richtungsquatsch. Wenn sie grafisch dargestellt werden, dann werden sie als eine Edge dargestellt werden, die
     * je nach Bedeutung eine der Richtungen oder beide als Pfeile darstellt. Hier wurde also das Model misbraucht, um im View diese Assoziationen
     * zusammenzufassen.
     */
    @Override
    public final Set<Class<? extends Edge>> getDoubleMeaningEdgeClasses() {
        return ImmutableSet.<Class<? extends Edge>> of(
                //vorwärts1: bearbeitet; rückwärts1: wird bearbeitet von
                //vorwärts2: interpretiert; rückwärts2: wird interpretiert von
                AufObjVerbindung.class,
                //vorwärts1: kann senden; rückwärts1: kann gesendet werden von
                //vorwärts2: kann empfangen; rückwärts2: kann empfangen werden von
                BssEtntVerbindung.class,
                //vorwärts1: sendet über; rückwärts1: wird gesendet über
                //vorwärts2: empfängt über; rückwärts2: wird empfangen über
                KommbezEtntVerbindung.class,
                //vorwärts1: sendet an; rückwärts1: empfängt von
                //vorwärts2: sendet an; rückwärts2: empfängt von
                //ACHTUNG: Dies ist auch eine Edge mit doppelter Bedeutung, weil sie das gerichtete
                //Senden und Empfangen zw. Schnittstellen ausdrückt. Da aber beide Endklassen gleich sind
                //haben sie auch in beiden Richtungen immer dieselb Bedeutung.
                KommBeziehung.class);
    }

    /**
     * Menge aller Kantenklassen, die nur in Vorwärtsrichtung verbunden werden und somit immer nur in dieser Richtung in
     * der Grafik dargestelt werden.
     */
    @Override
    public final Set<Class<? extends Edge>> getForwardConnectedEdgeClasses() {
        return ImmutableSet.of(); //im Moment keine eingetragen
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
    public String getResourceBaseName() {
        return "metamodel.tlgm_v3_0.MetamodelResources";
    }

}
