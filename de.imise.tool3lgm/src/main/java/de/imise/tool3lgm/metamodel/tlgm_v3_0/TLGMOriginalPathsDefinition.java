package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import de.imise.tool3lgm.graphtools.metamodel.PathsDefinition;
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
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntDotVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtntNatVerbindung;
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

public class TLGMOriginalPathsDefinition extends PathsDefinition {

    /**
     * @throws InvalidPathException
     */
    @Override
    @SuppressWarnings("unchecked")
    protected final void init() throws InvalidPathException {

        /* Aufgabe - Organisationseinheit */
        put(new MetaPath(Aufgabe.class, Organisationseinheit.class, "text_wird_erledigt_in", AufAufOrgVerbindung.class, OrgAufOrgVerbindung.class));

        /* Aufgabe - Anwendungsbaustein */
        put(new MetaPath(Aufgabe.class, Anwendungsbaustein.class, "text_unterstuetzt", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));

        /* Aufgabe - RechAnwendungsbaustein */
        put(new MetaPath(Aufgabe.class, RechAnwendungsbaustein.class, "text_unterstuetzt", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));

        /* Aufgabe - KonAnwendungsbaustein */
        put(new MetaPath(Aufgabe.class, KonAnwendungsbaustein.class, "text_unterstuetzt", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));

        /* Aufgabe - PhyDVBaustein */
        put(new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, "text_unterstuetzt", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class));

        /* Aufgabe - Standort */
        put(new MetaPath(Aufgabe.class, Standort.class, "text_erledigt", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class, PdvbStoVerbindung.class));

        /* Objekttyp - Datenbanksystem */
        put(new MetaPath(Objekttyp.class, Datenbanksystem.class, "text_speichert", ObjReprVerbindung.class, DbsDatVerbindung.class));

        /* Anwendungsbaustein - PhyDVBaustein */
        put(new MetaPath(Anwendungsbaustein.class, PhysischerDVBaustein.class, "text_installiert", PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class));

        /* RechAnwendungsbaustein - PhyDVBaustein */
        put(new MetaPath(RechAnwendungsbaustein.class, PhysischerDVBaustein.class, "text_installiert", PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class));

        /* KonAnwendungsbaustein - PhyDVBaustein */
        put(new MetaPath(KonAnwendungsbaustein.class, PhysischerDVBaustein.class, "text_installiert", PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class));

        /* Organisationseinheit - PhyDVBaustein */
        put(new MetaPath(Organisationseinheit.class, PhysischerDVBaustein.class, "text_nutzt", OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class));

        /* Organisationseinheit - Anwendungsbaustein */
        put(new MetaPath(Organisationseinheit.class, Anwendungsbaustein.class, "text_nutzt", OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));

        /* Organisationseinheit - RechAnwendungsbaustein */
        put(new MetaPath(Organisationseinheit.class, RechAnwendungsbaustein.class, "text_nutzt", OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));

        /* Organisationseinheit - KonAnwendungsbaustein */
        put(new MetaPath(Organisationseinheit.class, KonAnwendungsbaustein.class, "text_nutzt", OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class));

        /* Organisationseinheit - Softwareprodukt */
        put(new MetaPath(Organisationseinheit.class, Softwareprodukt.class, "text_nutzt", OrgAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class));

        /* Anwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath(Anwendungsbaustein.class, Kommunikationsstandard.class, "text_nutzt", AwbKommssVerbindung.class, BssKommstVerbindung.class));

        /* RechAnwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath(RechAnwendungsbaustein.class, Kommunikationsstandard.class, "text_nutzt", AwbKommssVerbindung.class, BssKommstVerbindung.class));

        /* KonAnwendungsbaustein - Kommunikationsstandard */
        put(new MetaPath(KonAnwendungsbaustein.class, Kommunikationsstandard.class, "text_nutzt", AwbKommssVerbindung.class, BssKommstVerbindung.class));

        /* RechAnwendungsbaustein - Softwareprodukt */
        put(new MetaPath(RechAnwendungsbaustein.class, Softwareprodukt.class, "text_gesteuert", RawbAwpVerbindung.class, AwpSwpVerbindung.class));

        /* Objekttyp - RechAnwendungsbaustein */
        put(new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, "text_fuehrenden", ObjLogspVerbindung.class, RawbDbsVerbindung.class));

        put(new MetaPath(Objekttyp.class, RechAnwendungsbaustein.class, "text_speichert", ObjReprVerbindung.class, DbsDatVerbindung.class, RawbDbsVerbindung.class));

        put(new MetaPath(Objekttyp.class, Anwendungsbaustein.class, new Class[][] {
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
        }, "text_kommuniziert"));

        /* Anwendungsbaustein - Anwendungsbaustein */
        put(new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                {
                        RawbRawbVerbindung.class
                }, {
                        AwbKawbVerbindung.class
                }
        }, "text_teil_von", true));

        put(new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
                {
                        AwbKommssVerbindung.class,
                        KommBeziehung.class,
                        AwbKommssVerbindung.class
                }
        }, new String[] {
                "text_empfaengt_sendet",
                "text_sendet",
                "text_empfaengt"
        }, 1, true));

    }

}
