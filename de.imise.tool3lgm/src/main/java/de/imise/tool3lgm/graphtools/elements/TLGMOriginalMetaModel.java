package de.imise.tool3lgm.graphtools.elements;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.elements.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AufAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AufObjVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKawbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.BssEtntVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.BssKommstVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DatenuebertragungsVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DbsDatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.DoksDokVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntDotVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntEtVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntKommstVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntNatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KawbOrgpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KommBeziehung;
import de.imise.tool3lgm.graphtools.elements.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjObjVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjReprVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.OrgOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbBtypVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbPdvbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.PrzAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbRawbVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.SubnNetztVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.SwpAufVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Bausteintyp;
import de.imise.tool3lgm.graphtools.elements.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.DBVerwaltungssystem;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.Datensatztyp;
import de.imise.tool3lgm.graphtools.elements.node.Dokumentensammlung;
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.graphtools.elements.node.Ereignistyp;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;
import de.imise.tool3lgm.graphtools.elements.node.Kommunikationsstandard;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.LogischerSpeicher;
import de.imise.tool3lgm.graphtools.elements.node.Nachrichtentyp;
import de.imise.tool3lgm.graphtools.elements.node.Netzprotokoll;
import de.imise.tool3lgm.graphtools.elements.node.Netztyp;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.elements.node.Organisationsplan;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.elements.node.Prozess;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Repraesentationsform;
import de.imise.tool3lgm.graphtools.elements.node.Schnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.elements.node.Standort;
import de.imise.tool3lgm.graphtools.elements.node.Subnetz;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewDefinition;

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
    public GraphViewDefinition getGraphViewDefinition() {
        return graphViewDefinition;
    }

    ////////////
    // Knoten //
    ////////////

    /** Alle Knoten der FE als Array */
    private static final Class[] ALL_DOMAIN_LAYER_NODES = {
            Aufgabe.class,
            AufOrgKombination.class,
            Objekttyp.class,
            Organisationseinheit.class,
            //      Rolle.class,
            Prozess.class,
    };

    /** Alle Knoten, die im Baum sichtbar auf der FE sichtbar sind */
    private static final Class[] TREE_DOMAIN_LAYER_NODES = {
            Aufgabe.class,
            //          AufOrgKombination.class,
            Objekttyp.class,
            Organisationseinheit.class,
            //          Rolle.class,
            Prozess.class,
    };

    /** Alle Knotenklassen der FE, die man im Baum neu erzeugen kann */
    private final Class[] TREE_CREATABLE_DOMAIN_LAYER_NODES = {
            Aufgabe.class,
            Objekttyp.class,
            Organisationseinheit.class,
            Prozess.class,
            //          Rolle.class,
    };

    /** Alle Knoten zw. FE und LWE als Array */
    private final Class[] ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES = {
            ABKonfiguration.class,
    };

    /** Alle Knoten der LWE als Array */
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

            //abstracte Knoten müssen hier auch eingetragen werden
            Anwendungsbaustein.class,
            EtntEtdtKombination.class,
            Schnittstelle.class,
            Repraesentationsform.class,
            LogischerSpeicher.class,
    };

    private final Class[] TREE_LOGICAL_LAYER_NODES = {
            RechAnwendungsbaustein.class,
            KonAnwendungsbaustein.class,
            //      Anwendungsprogramm.class,
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
    };

    private final Class[] TREE_CREATABLE_LOGICAL_LAYER_NODES = {
            RechAnwendungsbaustein.class,
            KonAnwendungsbaustein.class,
            Softwareprodukt.class,
            Kommunikationsstandard.class,
            DBVerwaltungssystem.class,
            Ereignistyp.class,
            Nachrichtentyp.class,
            Dokumententyp.class,
            EreignisNachrichtenTyp.class,
            EreignisDokumentenTyp.class,
    };

    /** Alle Knoten zw. LWE und PWE als Array */
    private final Class[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES = {
            DBKonfiguration.class,
    };

    /** Alle Knoten der PWE als Array */
    private final Class[] ALL_PHYSICAL_LAYER_NODES = {
            PhysischerDVBaustein.class,
            Standort.class,
            Bausteintyp.class,
            Netztyp.class,
            Subnetz.class,
            Netzprotokoll.class,
    };

    /** Alle Knoten der PWE im Baum als Array */
    private final Class[] TREE_PHYSICAL_LAYER_NODES = {
            PhysischerDVBaustein.class,
            Standort.class,
            Bausteintyp.class,
            Netztyp.class,
            Subnetz.class,
            Netzprotokoll.class,
    };

    private final Class[] TREE_CREATABLE_PHYSICAL_LAYER_NODES = {
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
    public final Class[] getTreeDomainLayerNodes() {
        return TREE_DOMAIN_LAYER_NODES;
    }

    @Override
    public final Class[] getTreeCreatableDomainLayerNodes() {
        return TREE_CREATABLE_DOMAIN_LAYER_NODES;
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
    public final Class[] getTreeLogicalLayerNodes() {
        return TREE_LOGICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getTreeCreatableLogicalLayerNodes() {
        return TREE_CREATABLE_LOGICAL_LAYER_NODES;
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
    public final Class[] getTreePhysicalLayerNodes() {
        return TREE_PHYSICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getTreeCreatablePhysicalLayerNodes() {
        return TREE_CREATABLE_PHYSICAL_LAYER_NODES;
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
            AwbKawbVerbindung.class,
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

    private final Set<Class<? extends Knoten>> UNIQUE_NODES = ImmutableSet.of(ABKonfiguration.class, Anwendungsprogramm.class, AufOrgKombination.class, Bausteintyp.class, Datensatztyp.class, DBKonfiguration.class, DBVerwaltungssystem.class,
            Dokumententyp.class, EreignisDokumentenTyp.class, EreignisNachrichtenTyp.class, Ereignistyp.class, Kommunikationsstandard.class, Nachrichtentyp.class, Netzprotokoll.class, Netztyp.class, Organisationseinheit.class, Organisationsplan.class,
            Softwareprodukt.class, Standort.class, Subnetz.class);

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    @Override
    public final Class[] getImportableNodes() {
        return IMPORTABLE_NODES;
    }

    /** Alle Knotenklassen, die in jedem Teilmodell vorkommen, also nicht in jedem Teilmodell einen eigenen Container besitzen. */
    @Override
    public final Set<Class<? extends Knoten>> getUniqueNodes() {
        return UNIQUE_NODES;
    }

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

    /**
     * Mappt von Elementklassen auf alle Kantenklassen, bei der die Reihenfolge von Instanzen dieser Kantenklasse für Elemente der Elementklasse eine
     * Bedeutung haben.
     */
    @Override
    public final Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> getElementClassToOrderedEdges() {
        Set<Class<? extends Kante>> processOrderedEdgeClasses = ImmutableSet.<Class<? extends Kante>> of(PrzAufVerbindung.class);
        Map<Class<? extends ModelElement>, Set<Class<? extends Kante>>> elementClassToOrderedEdges = ImmutableMap.<Class<? extends ModelElement>, Set<Class<? extends Kante>>> of(Prozess.class, processOrderedEdgeClasses);
        return elementClassToOrderedEdges;
    }

    /**
     * Liste aller Kantenklassen, die eigentlich 2 gerichtete Assoziationen im Metamodell sein müssten, aber aus Unwissenheit beim Entwurf des
     * Metamodells fehlerhafterweise in eine Assoziation verpackt wurden, bei denen die Richtung der Kante
     * (Doppelkante.FORWARD, Doppelkante.BACKWARD, Doppelkante.DOUBLE) die Bedeutung angibt. Nur wegen den 4 braucht man den ganzen
     * Doppelkanten-Richtungsquatsch. Wenn sie grafisch dargestellt werden, dann werden sie als eine Kante dargestellt werden, die
     * je nach Bedeutung eine der Richtungen oder beide als Pfeile darstellt. Hier wurde also das Model misbraucht, um im View diese Assoziationen
     * zusammenzufassen.
     */
    @Override
    public final Set<Class<? extends Kante>> getDoubleMeaningEdgeClasses() {
        return ImmutableSet.<Class<? extends Kante>> of(
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
                //ACHTUNG: Dies ist auch eine Kante mit doppelter Bedeutung, weil sie das gerichtete
                //Senden und Empfangen zw. Schnittstellen ausdrückt. Da aber beide Endklassen gleich sind
                //haben sie auch in beiden Richtungen immer dieselb Bedeutung.
                KommBeziehung.class);
    }

    /**
     * Menge aller Kantenklassen, die nur in Vorwärtsrichtung verbunden werden und somit immer nur in dieser Richtung in
     * der Grafik dargestelt werden.
     */
    @Override
    public final Set<Class<? extends Kante>> getForwardConnectedEdgeClasses() {
        return ImmutableSet.of(); //im Moment keine eingetragen
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    private MetaPath[] INTER_LAYER_CONNECTED_ELEMENT_PATHES = null;

    @Override
    public MetaPath[] getInterLayerConnectedElementPathes() {
        //die MetaPfade müssen hier über diesen umständlichen Weg initialisiert werden, da wenn man sie
        //außerhalb dieser Funktion gleich der Variable zuweist, die gesamt Initialisierung fehl schlägt
        if (INTER_LAYER_CONNECTED_ELEMENT_PATHES == null) {
            INTER_LAYER_CONNECTED_ELEMENT_PATHES = new MetaPath[] {
                    new MetaPath(Aufgabe.class, ABKonfiguration.class, new Class[][] {
                            {
                                    AufAufOrgVerbindung.class,
                                    AwbkAufOrgVerbindung.class
                            }
                    }),
                    new MetaPath(Anwendungsbaustein.class, DBKonfiguration.class, new Class[][] {
                            {
                                    PdvbkAwbVerbindung.class
                            }
                    }),
            };
        }
        return INTER_LAYER_CONNECTED_ELEMENT_PATHES;
    }

    private final Set<Class<? extends ModelElement>> GENERATE_NAME_CLASSES = ImmutableSet.<Class<? extends ModelElement>> of(AufOrgKombination.class, EtntEtdtKombination.class);

    @Override
    public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
        return GENERATE_NAME_CLASSES;
    }

}
