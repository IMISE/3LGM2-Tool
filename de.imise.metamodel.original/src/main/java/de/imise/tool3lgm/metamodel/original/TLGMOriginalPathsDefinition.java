package de.imise.tool3lgm.metamodel.original;

import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.original.node.Kommunikationsstandard;
import de.imise.tool3lgm.metamodel.original.node.Objekttyp;
import de.imise.tool3lgm.metamodel.original.node.Organisationseinheit;
import de.imise.tool3lgm.metamodel.original.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.original.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Softwareprodukt;
import de.imise.tool3lgm.metamodel.original.node.Standort;

public class TLGMOriginalPathsDefinition extends MetaPathDefinition {

    @Override
    protected final void init() {

        /* Aufgabe - Organisationseinheit */
        put(Aufgabe.class, Organisationseinheit.class, "PATH_is_executed_by", AufAufOrgVerbindung.class, OrgAufOrgVerbindung.class);

        /* Aufgabe - Anwendungsbaustein */
        put(Aufgabe.class, Anwendungsbaustein.class, "PATH_is_supported_by", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class);

        /* Aufgabe - PhyDVBaustein */
        put(Aufgabe.class, PhysischerDVBaustein.class, "PATH_is_supported_by", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class);

        /* Aufgabe - Standort */
        put(Aufgabe.class, Standort.class, "PATH_is_executed_at", AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class, PdvbStoVerbindung.class);

        /* Objekttyp - Datenbanksystem */
        put(Objekttyp.class, Datenbanksystem.class, "PATH_is_stored_in", ObjReprVerbindung.class, DbsDatVerbindung.class);

        /* Anwendungsbaustein - PhyDVBaustein */
        put(Anwendungsbaustein.class, PhysischerDVBaustein.class, "PATH_is_installed_on", PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class);

        /* Organisationseinheit - PhyDVBaustein */
        put(PhysischerDVBaustein.class, Organisationseinheit.class, "PATH_is_used_by", PdvbPdvbkVerbindung.class, PdvbkAwbVerbindung.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, OrgAufOrgVerbindung.class);

        /* Organisationseinheit - Anwendungsbaustein */
        put(Anwendungsbaustein.class, Organisationseinheit.class, "PATH_is_used_by", AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, OrgAufOrgVerbindung.class);

        /* Organisationseinheit - Softwareprodukt */
        put(Softwareprodukt.class, Organisationseinheit.class, "PATH_is_used_by", AwpSwpVerbindung.class, RawbAwpVerbindung.class, AwbAwbkVerbindung.class, AwbkAufOrgVerbindung.class, OrgAufOrgVerbindung.class);

        /* Anwendungsbaustein - Kommunikationsstandard */
        put(Kommunikationsstandard.class, Anwendungsbaustein.class, "PATH_is_used_by", BssKommstVerbindung.class, AwbKommssVerbindung.class);

        /* RechAnwendungsbaustein - Softwareprodukt */
        put(RechAnwendungsbaustein.class, Softwareprodukt.class, "PATH_is_controlled_by", RawbAwpVerbindung.class, AwpSwpVerbindung.class);

        /* Objekttyp - RechAnwendungsbaustein */
        put(RechAnwendungsbaustein.class, Objekttyp.class, "PATH_is_master_of", RawbDbsVerbindung.class, ObjLogspVerbindung.class);

        put(Objekttyp.class, RechAnwendungsbaustein.class, "PATH_is_stored_in", ObjReprVerbindung.class, DbsDatVerbindung.class, RawbDbsVerbindung.class);

        //        put(new MetaPath(Objekttyp.class, Anwendungsbaustein.class, new Class[][] {
        //                {
        //                        ObjReprVerbindung.class, EtntNatVerbindung.class, BssEtntVerbindung.class, AwbKommssVerbindung.class
        //                }, {
        //                        ObjReprVerbindung.class, EtntDotVerbindung.class, BssEtntVerbindung.class, AwbKommssVerbindung.class
        //                }
        //        }, "text_kommuniziert"));
        //
        //        /* Anwendungsbaustein - Anwendungsbaustein */
        //        put(new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
        //                {
        //                        RawbRawbVerbindung.class
        //                }, {
        //                        KawbAwbVerbindung.class
        //                }
        //        }, 0));
        //
        //        put(new MetaPath(Anwendungsbaustein.class, Anwendungsbaustein.class, new Class[][] {
        //                {
        //                        AwbKommssVerbindung.class, KommBeziehung.class, AwbKommssVerbindung.class
        //                }
        //        }, 1));

    }

}
