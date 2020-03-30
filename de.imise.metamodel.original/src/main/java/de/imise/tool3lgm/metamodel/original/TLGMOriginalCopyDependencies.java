package de.imise.tool3lgm.metamodel.original;

import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.BssEtntVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.BssKommstVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DbsDbvsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.DoksDokVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntDotVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntEtVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntKommstVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.EtntNatVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KawbOrgpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.original.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbBtypVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbStoVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbSubnVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.SubnNetzpVerbindung;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.original.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.original.node.Bausteintyp;
import de.imise.tool3lgm.metamodel.original.node.DBVerwaltungssystem;
import de.imise.tool3lgm.metamodel.original.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.original.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.original.node.Dokumententyp;
import de.imise.tool3lgm.metamodel.original.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.metamodel.original.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.metamodel.original.node.Ereignistyp;
import de.imise.tool3lgm.metamodel.original.node.Kommunikationsstandard;
import de.imise.tool3lgm.metamodel.original.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Nachrichtentyp;
import de.imise.tool3lgm.metamodel.original.node.Netzprotokoll;
import de.imise.tool3lgm.metamodel.original.node.Netztyp;
import de.imise.tool3lgm.metamodel.original.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.original.node.Prozess;
import de.imise.tool3lgm.metamodel.original.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Softwareprodukt;
import de.imise.tool3lgm.metamodel.original.node.Standort;
import de.imise.tool3lgm.metamodel.original.node.Subnetz;

/**
 * Achtung: nur finale Klasse dürfen bzw. sollten hier eingetragen werden. Die Vererbungshierarchie wird nicht gecheckt.
 * Das hat Performancegrüde, weil man so nicht mit insatnceof alle Oberklassen einer Klasse durchchecken muss.
 *
 * @author AXS (15.08.2017)
 */
public class TLGMOriginalCopyDependencies extends CopyDependencies {

    public TLGMOriginalCopyDependencies(final MetaModel metaModel) {
        super(metaModel);
        initCopyDepenencies();
        initAvoidDuplicates();
    }

    @SuppressWarnings("unchecked")
    private void initCopyDepenencies() {
        //        set(Anwendungsprogramm.class, Softwareprodukt.class);
        //        set(Aufgabe.class, AufOrgKombination.class);
        //        set(AufOrgKombination.class, ABKonfiguration.class, Organisationseinheit.class);
        //        set(Bausteinschnittstelle.class, Kommunikationsstandard.class, EreignisNachrichtenTyp.class, EreignisDokumentenTyp.class);
        //        set(Datenbanksystem.class, DBVerwaltungssystem.class, Datensatztyp.class);
        //        set(Dokumentensammlung.class, Dokumententyp.class);
        //        set(EreignisDokumentenTyp.class, Ereignistyp.class, Dokumententyp.class, Kommunikationsstandard.class);
        //        set(EreignisNachrichtenTyp.class, Ereignistyp.class, Nachrichtentyp.class, Kommunikationsstandard.class);
        //        set(KonAnwendungsbaustein.class, Bausteinschnittstelle.class, Benutzungsschnittstelle.class, Dokumentensammlung.class, Organisationsplan.class, DBKonfiguration.class, ABKonfiguration.class);
        //        set(PhysischerDVBaustein.class, Standort.class, Bausteintyp.class, Subnetz.class, DBKonfiguration.class);
        //        set(Prozess.class, Aufgabe.class);
        //        set(RechAnwendungsbaustein.class, Bausteinschnittstelle.class, Benutzungsschnittstelle.class, Datenbanksystem.class, Anwendungsprogramm.class, DBKonfiguration.class, ABKonfiguration.class);
        //        set(Subnetz.class, Netzprotokoll.class);
        //        set(KommBeziehung.class, EreignisNachrichtenTyp.class, EreignisDokumentenTyp.class);
        set(Anwendungsprogramm.class, AwpSwpVerbindung.class);
        set(Aufgabe.class, AufAufOrgVerbindung.class);
        set(AufOrgKombination.class, AwbkAufOrgVerbindung.class, OrgAufOrgVerbindung.class);
        set(Bausteinschnittstelle.class, BssKommstVerbindung.class, BssEtntVerbindung.class);
        set(Datenbanksystem.class, DbsDbvsVerbindung.class, DbsDatVerbindung.class);
        set(Dokumentensammlung.class, DoksDokVerbindung.class);
        set(EreignisDokumentenTyp.class, EtntEtVerbindung.class, EtntDotVerbindung.class, EtntKommstVerbindung.class);
        set(EreignisNachrichtenTyp.class, EtntEtVerbindung.class, EtntNatVerbindung.class, EtntKommstVerbindung.class);
        set(KonAnwendungsbaustein.class, AwbKommssVerbindung.class, KawbDoksVerbindung.class, KawbOrgpVerbindung.class, PdvbkAwbVerbindung.class, AwbAwbkVerbindung.class);
        set(PhysischerDVBaustein.class, PdvbStoVerbindung.class, PdvbBtypVerbindung.class, PdvbSubnVerbindung.class, PdvbPdvbkVerbindung.class);
        set(Prozess.class, PrzAufVerbindung.class);
        set(RechAnwendungsbaustein.class, AwbKommssVerbindung.class, RawbDbsVerbindung.class, RawbAwpVerbindung.class, PdvbkAwbVerbindung.class, AwbAwbkVerbindung.class);
        set(Subnetz.class, SubnNetzpVerbindung.class);
        set(KommBeziehung.class, KommbezEtntVerbindung.class);
    }

    private void initAvoidDuplicates() {
        addToAvoidDuplicates(Bausteintyp.class);
        addToAvoidDuplicates(DBVerwaltungssystem.class);
        addToAvoidDuplicates(Dokumententyp.class);
        addToAvoidDuplicates(Ereignistyp.class);
        addToAvoidDuplicates(EreignisDokumentenTyp.class);
        addToAvoidDuplicates(EreignisNachrichtenTyp.class);
        addToAvoidDuplicates(Kommunikationsstandard.class);
        addToAvoidDuplicates(Nachrichtentyp.class);
        addToAvoidDuplicates(Netzprotokoll.class);
        addToAvoidDuplicates(Netztyp.class);
        addToAvoidDuplicates(Softwareprodukt.class);
        addToAvoidDuplicates(Standort.class);
        addToAvoidDuplicates(Subnetz.class);
    }

}
