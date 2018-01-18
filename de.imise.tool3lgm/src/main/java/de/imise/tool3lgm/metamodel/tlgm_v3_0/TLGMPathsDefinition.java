package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import java.awt.Color;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.PathsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.path.InvalidPathException;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKawbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntDotVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntNatVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjReprVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.RawbRawbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Kommunikationsstandard;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationseinheit;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Softwareprodukt;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Standort;

public class TLGMPathsDefinition extends PathsDefinition {

    /**
     * @throws InvalidPathException
     */
    @Override
    @SuppressWarnings("unchecked")
    protected final void init() throws InvalidPathException {

        for (Class<? extends Edge> edgeClass : ModelConstants.ALL_EDGES_SET) {
            MetaPath[] simpleMetaPaths = createSimpleMetaPaths(edgeClass);
            put(simpleMetaPaths);
        }

        /* Aufgabe - Organisationseinheit */
        put(new MetaPath(Aufgabe.class, Organisationseinheit.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class,
                        OrgAufOrgVerbindung.class
                }
        }, s("Aufgabe") + " " + s("text_wird_erledigt_in") + " " + s("Organisationseinheit")));

        /* Aufgabe - Anwendungsbaustein */
        put(new MetaPath(Aufgabe.class, Anwendungsbaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class
                }
        }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("Anwendungsbaustein")));

        /* Aufgabe - RechAnwendungsbaustein */
        put(new MetaPath(Aufgabe.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class
                }
        }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("Anwendungsbaustein")));

        /* Aufgabe - KonAnwendungsbaustein */
        put(new MetaPath(Aufgabe.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class
                }
        }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("Anwendungsbaustein")));

        /* Aufgabe - PhyDVBaustein */
        put(new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class,
                        PdvbkAwbVerbindung.class,
                        PdvbPdvbkVerbindung.class
                }
        }, s("Aufgabe") + " " + s("text_unterstuetzt") + " " + s("PhysischerDVBaustein")));

        /* Aufgabe - Standort */
        put(new MetaPath(Aufgabe.class, Standort.class, new Class[][] {
                {
                        AufAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class,
                        PdvbkAwbVerbindung.class,
                        PdvbPdvbkVerbindung.class,
                        PdvbStoVerbindung.class
                }
        }, s("Aufgabe") + " " + s("text_erledigt") + " " + s("Standort")));

        /* Objekttyp - Datenbanksystem */
        put(new MetaPath(Objekttyp.class, Datenbanksystem.class, new Class[][] {
                {
                        ObjReprVerbindung.class,
                        DbsDatVerbindung.class,
                }
        }, s("Objekttyp") + " " + s("text_speichert") + " " + s("Datenbanksystem")));

        /* Anwendungsbaustein - PhyDVBaustein */
        put(new MetaPath(Anwendungsbaustein.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        PdvbkAwbVerbindung.class,
                        PdvbPdvbkVerbindung.class
                }
        }, s("Anwendungsbaustein") + " " + s("text_installiert") + " " + s("PhysischerDVBaustein")));

        /* RechAnwendungsbaustein - PhyDVBaustein */
        put(new MetaPath(RechAnwendungsbaustein.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        PdvbkAwbVerbindung.class,
                        PdvbPdvbkVerbindung.class
                }
        }, s("Anwendungsbaustein") + " " + s("text_installiert") + " " + s("PhysischerDVBaustein")));

        /* KonAnwendungsbaustein - PhyDVBaustein */
        put(new MetaPath(KonAnwendungsbaustein.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        PdvbkAwbVerbindung.class,
                        PdvbPdvbkVerbindung.class
                }
        }, s("Anwendungsbaustein") + " " + s("text_installiert") + " " + s("PhysischerDVBaustein")));

        /* Organisationseinheit - PhyDVBaustein */
        put(new MetaPath(Organisationseinheit.class, PhysischerDVBaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class,
                        PdvbkAwbVerbindung.class,
                        PdvbPdvbkVerbindung.class
                }
        }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("PhysischerDVBaustein")));

        /* Organisationseinheit - Anwendungsbaustein */
        put(new MetaPath(Organisationseinheit.class, Anwendungsbaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class
                }
        }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Anwendungsbaustein")));

        /* Organisationseinheit - RechAnwendungsbaustein */
        put(new MetaPath(Organisationseinheit.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class
                }
        }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Anwendungsbaustein")));

        /* Organisationseinheit - KonAnwendungsbaustein */
        put(new MetaPath(Organisationseinheit.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class
                }
        }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Anwendungsbaustein")));

        /* Organisationseinheit - Softwareprodukt */
        put(new MetaPath(Organisationseinheit.class, Softwareprodukt.class, new Class[][] {
                {
                        OrgAufOrgVerbindung.class,
                        AwbkAufOrgVerbindung.class,
                        AwbAwbkVerbindung.class,
                        RawbAwpVerbindung.class,
                        AwpSwpVerbindung.class
                }
        }, s("Organisationseinheit") + " " + s("text_nutzt") + " " + s("Softwareprodukt")));

        /* Anwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath(Anwendungsbaustein.class, Kommunikationsstandard.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        BssKommstVerbindung.class
                }
        }, s("Anwendungsbaustein") + " " + s("text_nutzt") + " " + s("Kommunikationsstandard")));

        /* RechAnwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath(RechAnwendungsbaustein.class, Kommunikationsstandard.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        BssKommstVerbindung.class
                }
        }, s("Anwendungsbaustein") + " " + s("text_nutzt") + " " + s("Kommunikationsstandard")));

        /* KonAnwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath(KonAnwendungsbaustein.class, Kommunikationsstandard.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        BssKommstVerbindung.class
                }
        }, s("Anwendungsbaustein") + " " + s("text_nutzt") + " " + s("Kommunikationsstandard")));

        /* RechAnwendungsbaustein - Softwareprodukt */
        put(new MetaPath(RechAnwendungsbaustein.class, Softwareprodukt.class, new Class[][] {
                {
                        RawbAwpVerbindung.class,
                        AwpSwpVerbindung.class
                }
        }, s("Anwendungsbaustein") + " " + s("text_gesteuert") + " " + s("Softwareprodukt")));

        /* Objekttyp - RechAnwendungsbaustein */
        put(new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        ObjLogspVerbindung.class,
                        RawbDbsVerbindung.class
                }
        }, s("Objekttyp") + " " + s("text_fuehrenden") + " " + s("Anwendungsbaustein")));
        put(new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        ObjReprVerbindung.class,
                        DbsDatVerbindung.class,
                        RawbDbsVerbindung.class
                }
        }, s("Objekttyp") + " " + s("text_speichert") + " " + s("Anwendungsbaustein")));
        put(new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        ObjReprVerbindung.class,
                        EtntNatVerbindung.class,
                        BssEtntVerbindung.class,
                        AwbKommssVerbindung.class
                }, {
                        ObjReprVerbindung.class,
                        EtntDotVerbindung.class,
                        BssEtntVerbindung.class,
                        AwbKommssVerbindung.class
                }
        }, s("Objekttyp") + " " + s("text_kommuniziert") + " " + s("Anwendungsbaustein")));

        /* Objekttyp - KonAnwendungsbaustein */
        put(new MetaPath(Objekttyp.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        ObjLogspVerbindung.class,
                        KawbDoksVerbindung.class
                }
        }, s("Objekttyp") + " " + s("text_fuehrenden") + " " + s("Anwendungsbaustein")));
        put(new MetaPath(Objekttyp.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        ObjReprVerbindung.class,
                        DoksDokVerbindung.class,
                        KawbDoksVerbindung.class
                }
        }, s("Objekttyp") + " " + s("text_speichert") + " " + s("Anwendungsbaustein")));
        put(new MetaPath(Objekttyp.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        ObjReprVerbindung.class,
                        EtntNatVerbindung.class,
                        BssEtntVerbindung.class,
                        AwbKommssVerbindung.class
                }, {
                        ObjReprVerbindung.class,
                        EtntDotVerbindung.class,
                        BssEtntVerbindung.class,
                        AwbKommssVerbindung.class
                }
        }, s("Objekttyp") + " " + s("text_kommuniziert") + " " + s("Anwendungsbaustein")));

        /* Anwendungsbaustein - Anwendungsbaustein */
        put(new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                {
                        RawbRawbVerbindung.class
                }, {
                        AwbKawbVerbindung.class
                }
        }, s("zeile") + " " + s("text_teil_von") + " " + s("spalte"), true));
        put(new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        KommBeziehung.class,
                        AwbKommssVerbindung.class
                }
        }, new Color[] {
                Color.ORANGE,
                Color.BLUE,
                Color.GREEN
        }, new String[] {
                s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
        }, 1, true));

        /* RechAnwendungsbaustein - RechAnwendungsbaustein */
        put(new MetaPath(RechAnwendungsbaustein.class, RechAnwendungsbaustein.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        KommBeziehung.class,
                        AwbKommssVerbindung.class
                }
        }, new Color[] {
                Color.ORANGE,
                Color.BLUE,
                Color.GREEN
        }, new String[] {
                s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
        }, 1, true));

        /* KonAnwendungsbaustein - Anwendungsbaustein */
        put(new MetaPath(KonAnwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        KommBeziehung.class,
                        AwbKommssVerbindung.class
                }
        }, new Color[] {
                Color.ORANGE,
                Color.BLUE,
                Color.GREEN
        }, new String[] {
                s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
        }, 1, true));

        /* KonAnwendungsbaustein - KonAnwendungsbaustein */
        put(new MetaPath(KonAnwendungsbaustein.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        KommBeziehung.class,
                        AwbKommssVerbindung.class
                }
        }, new Color[] {
                Color.ORANGE,
                Color.BLUE,
                Color.GREEN
        }, new String[] {
                s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
        }, 1, true));

        /* RechAnwendungsbaustein - KonAnwendungsbaustein */
        put(new MetaPath(RechAnwendungsbaustein.class, KonAnwendungsbaustein.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        KommBeziehung.class,
                        AwbKommssVerbindung.class
                }
        }, new Color[] {
                Color.ORANGE,
                Color.BLUE,
                Color.GREEN
        }, new String[] {
                s("zeile") + " " + s("text_empfaengt_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_sendet") + " " + s("spalte"),
                s("zeile") + " " + s("text_empfaengt") + " " + s("spalte"),
        }, 1, true));

    }

}
