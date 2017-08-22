/*
 * Created on 22.06.2008
 */
package de.imise.tool3lgm.graphtools.analyse.process;

import static de.imise.tool3lgm.graphtools.elements.Kante.BACKWARD;
import static de.imise.tool3lgm.graphtools.elements.Kante.DOUBLE;
import static de.imise.tool3lgm.graphtools.elements.Kante.FORWARD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.analyse.context.ModelAnalyzerCache;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.util.Alphabetical;
import de.imise.util.SameTypePair;
import de.imise.util.swing.dialog.OutputDialog;

/**
 * @author AXS
 */
public class DataAvailabilityFinder {

    /**
     * Das Hauptdokument des zu analysierenden Modells
     */
    private final GraphDocument mainDoc;

    /**
     * Das Teilmodell, in dem die XMLAnalyse gestartet wurde
     */
    private final GraphDocument doc;

    /**
     * Die Analyseklasse zum Finden der (kürzesten) Kommunikationswege
     */
    private final ShortestCommunicationPathFinder commPathFinder;

    /**
     * @param doc
     */
    public DataAvailabilityFinder(final GraphDocument doc) {
        super();
        this.doc = doc;
        mainDoc = doc.getCollection().getMainGraphDocument();
        commPathFinder = new ShortestCommunicationPathFinder(mainDoc.getCollection());

        /*
         * ArrayList elements = mainDoc.getModelItems(Anwendungsbaustein.class, true); //
         * System.err.println(elements.size()); HashSet allMe = new HashSet(elements); for (int i=0;
         * i<elements.size(); i++){ ModelElement me = (ModelElement)elements.get(i); if
         * (me.getConnectionsFrom(Anwendungsbaustein.class, PdvbPdvbVerbindung.class).size()>0 ||
         * me.getConnectionsFrom(Anwendungsbaustein.class, RawbRawbVerbindung.class).size()>0);
         * allMe.add(me); } /* System.err.println("Anwendungsbaustein: " + allMe.size()); elements =
         * mainDoc.getAllModelElements(Bausteinschnittstelle.class);
         * System.err.println("Bausteinschnittstellen: " + elements.size()); elements =
         * mainDoc.getAllModelElements(KommBeziehung.class);
         * System.err.println("Kommunikationsbeziehungen: " + elements.size());
         */
        /*
         * int b=0; int i=0; ArrayList elements = mainDoc.getModelItmes(AufObjVerbindung.class); for
         * (int k=0; k<elements.size(); k++){ Doppelkante kante = (Doppelkante)elements.get(k); if
         * (kante.getDirection()==Doppelkante.FORWARD){ if (kante.getStart() instanceof Aufgabe)
         * b++; else i++; }else if(kante.getDirection()==Doppelkante.BACKWARD){ if (kante.getStart()
         * instanceof Aufgabe) i++; else b++; }else { b++; i++; } } // System.err.println(i); //
         * System.err.println(b); // System.err.println("Kommunikationsbeziehungen: " +
         * elements.size());
         */

        showReport();
    }

    /**
     * Prüft, ob der übergebenene Objekttyp irgendwo gespeichert wird.
     *
     * @param objecttype
     * @return <code>true</code>, wenn der übergebenene Objekttyp irgendwo gespeichert wird, sonst
     *         <code>false</code>
     */
    public boolean hasStoringSystem(final Objekttyp objecttype) {
        ModelAnalyzerCache mac = commPathFinder.getAnalyzerCache();
        Set<ModelElement> storage = mac.getDirectStorageApplicationSystems(objecttype);
        return storage != null && storage.size() != 0;
    }

    /**
     * Prüft, ob der übergebenene Objekttyp einen Master-Speicher besitzt.
     *
     * @param objecttype
     * @return <code>true</code>, wenn der übergebenene Objekttyp einen Master-Speicher besitzt,
     *         sonst <code>false</code>
     */
    public boolean hasMasterSystem(final Objekttyp objecttype) {
        ModelAnalyzerCache mac = commPathFinder.getAnalyzerCache();
        Set<ModelElement> master = mac.getDirectMasterApplicationSystems(objecttype);
        return master != null && master.size() != 0;
    }

    /**
     * Prüft, ob der übergebenene Objekttyp irgendwo gespeichert wird und ob er einen
     * Master-Speicher besitzt.
     *
     * @param objecttype
     * @return <code>true</code>, wenn der übergebenene Objekttyp irgendwo gespeichert wird und er
     *         einen Master-Speicher besitzt, sonst <code>false</code>
     */
    public boolean isStored(final Objekttyp objecttype) {
        return hasStoringSystem(objecttype) && hasMasterSystem(objecttype);
    }

    /**
     * Liefert eine Liste von <code>SameTypePair</code>s, mit allen Master-Anwendungssystemen des
     * Objekttyps, zwischen denen untereinander kein gerichteter Kommunikationsweg existiert, über
     * den der Objekttyp übertragen werden kann. Das erste Objekt jedes <code>SameTypePair</code>
     * ist Sender und das zweite Empfänger des Objekttyps.<br>
     * Es kann sein, dass gar keine Kommunikation zwischen 2 Master Anwendungsystemen möglich ist.
     * Dann sind 2 <code>SameTypePair</code>s in der Liste, die jeweils die beiden Master als
     * Objekte enthalten, aber beide in jeweils umgekehrter Reihenfolge. Ist nur eine
     * kommunikationsrichtung nicht gegeben, dann ist nur das entsprechend gerichtete
     * <code>SameTypePair</code> in der Liste.
     *
     * @param objecttype
     * @return
     */
    public Collection<SameTypePair<ModelElement>> getMissingMasterToMasterCommunicationLinks(final Objekttyp objecttype) {
        ModelAnalyzerCache mac = commPathFinder.getAnalyzerCache();
        Set<ModelElement> masters = mac.getDirectMasterApplicationSystems(objecttype);
        return getMissingCommunicationLinks(masters, masters, objecttype);
    }

    /**
     * @param objecttype
     * @return
     */
    public Collection<SameTypePair<ModelElement>> getMissingMasterToStorageCommunicationLinks(final Objekttyp objecttype) {
        ModelAnalyzerCache mac = commPathFinder.getAnalyzerCache();
        Set<ModelElement> masters = mac.getDirectMasterApplicationSystems(objecttype);
        Set<ModelElement> storage = mac.getDirectStorageApplicationSystems(objecttype);
        return getMissingCommunicationLinks(masters, storage, objecttype);
    }

    /**
     * @param objecttype
     * @return
     */
    public Collection<SameTypePair<ModelElement>> getMissingCommunicationLinks(final Collection<ModelElement> sender, final Collection<ModelElement> receiver, final Objekttyp objecttype) {
        List<SameTypePair<ModelElement>> returnList = new ArrayList<>();
        for (ModelElement send : sender) {
            Anwendungsbaustein sAWB = (Anwendungsbaustein) send;
            for (ModelElement reci : receiver) {
                if (send == reci) {
                    continue;
                }
                ShortestCommunicationPathFinder.MinimalCommunicationPath minPath = commPathFinder.getShortestPath(sAWB, (Anwendungsbaustein) reci, objecttype);
                List<ModelElement> al = minPath.getAwbPath();
                if (al == null || al.size() == 0) {
                    returnList.add(new SameTypePair<>(send, reci));
                }
            }
        }
        return returnList;
    }

    /**
     * Gibt den minimalen Kommunikationsweg zwischen den Anwendungssystemen der Konfiguration der
     * Aufgabe und den speichernden Anwendungssystemen des Objekttyps zurück. Wenn die Aufgabe keine
     * Anwendungssysteme in ihrer Konfiguration
     *
     * @param function
     * @param objecttype
     * @return
     */
    public ShortestCommunicationPathFinder.MinimalCommunicationPath getMinimalInterpretingPath(final Aufgabe function, final Objekttyp objecttype) {
        List<ModelElement> aufOrgKombis = function.getConnectedElements(AufOrgKombination.class);
        // von der Aufgabe und ihren Oberaufgaben alle Konfigs einsammeln
        for (ModelElement aufParent : function.getParentElements()) {
            aufOrgKombis.addAll(aufParent.getConnectedElements(AufOrgKombination.class));
        }
        ModelAnalyzerCache mac = commPathFinder.getAnalyzerCache();
        for (ModelElement aufOrg : aufOrgKombis) {
            for (ModelElement abKonf : aufOrg.getConnectedElements(ABKonfiguration.class)) {
                Collection<ModelElement> konfigAWBs = abKonf.getConnectedElements(Anwendungsbaustein.class);
                Collection<ModelElement> directMasterAndStrorage = mac.getDirectMasterAndStorageApplicationSystems(objecttype);
                return commPathFinder.getShortestPath(directMasterAndStrorage, konfigAWBs, objecttype);
            }
        }
        return null;
    }

    /**
     * Gibt alle Anwenungssysteme zurück, die den Objekttyp zwar speichern, zu denen aber keine
     * Kommunikation vom Master-Anwendungssystem des Objekttyps besteht. Dies kann daran liegen,
     * dass der Objekttyp kein Master-Anwenungssystem besitzt oder keine Kommunikationsverbindung
     * zwischen dem Master- und dem speichernden Anwendungssystem besteht.
     *
     * @param objekttyp
     * @return alle Anwendungssysteme, die den Objekttyp inkonsistent speichern TODO: implementieren
     */
    public Set<ModelElement> getInconsistentStorageSystems(final Objekttyp objekttyp) {
        return null;
    }

    // ///////////
    // Ausgabe //
    // ///////////

    /**
     *
     */
    private void showReport() {

        ModelAnalyzerCache mac = commPathFinder.getAnalyzerCache();

        // //XMLAnalyse mehrerer Parents bei einem Element
        // System.err.println("############\n# Aufgabe #\n############");
        // ArrayList all = mac.getMultipleParentElements(Aufgabe.class);
        // for (Iterator it = all.iterator(); it.hasNext();){
        // ModelElement me = (ModelElement)it.next();
        // StringBuilder sb = new StringBuilder();
        // sb.append(me);
        // sb.append("\t");
        // sb.append(me.getDirectParentElements());
        // System.err.println(sb.toString().replace('\n', ' '));
        // }
        // System.err.println("\n##############\n# Objekttyp #\n##############");
        // all = mac.getMultipleParentElements(Objekttyp.class);
        // for (Iterator it = all.iterator(); it.hasNext();){
        // ModelElement me = (ModelElement)it.next();
        // StringBuilder sb = new StringBuilder();
        // sb.append(me);
        // sb.append("\t");
        // sb.append(me.getDirectParentElements());
        // System.err.println(sb.toString().replace('\n', ' '));
        // }
        // System.err.println("\n#######################\n# Anwendungsbaustein #\n#######################");
        // all = mac.getMultipleParentElements(Anwendungsbaustein.class);
        // for (Iterator it = all.iterator(); it.hasNext();){
        // ModelElement me = (ModelElement)it.next();
        // StringBuilder sb = new StringBuilder();
        // sb.append(me);
        // sb.append("\t");
        // sb.append(me.getDirectParentElements());
        // System.err.println(sb.toString().replace('\n', ' '));
        // }
        // System.err.println("\n########\n# PDVB #\n########");
        // all = mac.getMultipleParentElements(PhysischerDVBaustein.class);
        // for (Iterator it = all.iterator(); it.hasNext();){
        // ModelElement me = (ModelElement)it.next();
        // StringBuilder sb = new StringBuilder();
        // sb.append(me);
        // sb.append("\t");
        // sb.append(me.getDirectParentElements());
        // System.err.println(sb.toString().replace('\n', ' '));
        // }
        //
        // Alle Objekttypen mit all ihren Master-DBS
        // ArrayList all = mainDoc.getModelItems(Objekttyp.class, true, true);
        // Alphabetical.sort(all);
        // for (Iterator it = all.iterator(); it.hasNext();){
        // Object ot = it.next();
        // Set masterSet = (Set)mac.getMasterDBS(ot);
        // String s = ot + ": " + mac.getMasterDBS(ot);
        // s = s.replace('\n', ' ');
        // System.err.println(s);
        // if (masterSet.size()>1)
        // System.err.println("*************************************************");
        // }

        OutputDialog outputDialog = new OutputDialog(Static.getMainFrame(), Tool3lgmConstants.getResString("data_availability"));

        outputDialog.appendln("##############################################################");
        outputDialog.appendln("#                        UnavailableET                       #");
        outputDialog.appendln("##############################################################");

        // int unavailableET = 0;

        // alle Berabeiten/Interpretieren-Beziehungen holen
        Iterable<ModelElement> readUpdateEdges = doc.getModelItems(AufObjVerbindung.class);

        for (ModelElement me : readUpdateEdges) {
            Kante edge = (Kante) me;
            // bei allen Interprtiert-Bezeihungen
            Aufgabe auf = null;
            Objekttyp ot = null;
            if (edge.getStart() instanceof Aufgabe && (edge.getDirection() == BACKWARD || edge.getDirection() == DOUBLE)) {
                auf = (Aufgabe) edge.getStart();
                ot = (Objekttyp) edge.getEnd();
            } else if (edge.getEnd() instanceof Aufgabe && (edge.getDirection() == FORWARD || edge.getDirection() == DOUBLE)) {
                auf = (Aufgabe) edge.getEnd();
                ot = (Objekttyp) edge.getStart();
            }
            // wenn es keine Interpretiert-Beziehung war -> nächste Kante
            if (auf == null) {
                continue;
            }

            // das hier war nur der Testaufruf des alten funktionierenden Codes, um noochmal zu
            // prüfen, wie es richtig laufen müsste
            // ShortestCommunicationPathFinderOld scpfo = new
            // ShortestCommunicationPathFinderOld(ot.getPartAndParentElements(),
            // mainDoc.getCollection());

            outputDialog.appendln("\n##############################################");
            String s = auf + " INTERPRETIERT " + ot;
            s = s.replace('\n', ' ');
            outputDialog.appendln(s, true);
            outputDialog.appendln("Speicherorte des Objekttyps:");
            outputDialog.appendln(mac.expandPartOfElementSet(mac.getDirectMasterAndStorageApplicationSystems(ot)).toString().replace('\n', ' '));

            List<ModelElement> aufOrgKombis = auf.getConnectedElements(AufOrgKombination.class);
            // von der Aufgabe und ihren Oberaufgaben alle Konfigs einsammeln
            for (ModelElement parent : auf.getParentElements()) {
                aufOrgKombis.addAll(parent.getConnectedElements(AufOrgKombination.class));
            }
            for (ModelElement aufOrg : aufOrgKombis) {
                List<ModelElement> abKonfigs = aufOrg.getConnectedElements(ABKonfiguration.class);
                if (abKonfigs.size() == 0) {
                    outputDialog.appendln("Konfiguration:");
                    outputDialog.appendln("[]");
                    outputDialog.appendln("Kommunikationsweg:");
                    outputDialog.appendln("--- Keine Kommunikation möglich! ---");
                    continue;
                }
                for (ModelElement abKonf : abKonfigs) {
                    List<ModelElement> konfigAWBs = abKonf.getConnectedElements(Anwendungsbaustein.class);
                    outputDialog.appendln("Konfiguration:");
                    s = konfigAWBs.toString().replace('\n', ' ');
                    outputDialog.appendln(s);
                    outputDialog.appendln("Kommunikationsweg:");
                    ShortestCommunicationPathFinder.MinimalCommunicationPath path = commPathFinder.getShortestPath(mac.getDirectMasterAndStorageApplicationSystems(ot), konfigAWBs, ot);
                    s = path.getAwbPath() == null ? "--- Keine Kommunikation möglich! ---" : path.getAwbPath().toString().replace('\n', ' ');
                    outputDialog.appendln(s);
                    s = path.getInterfacePath() == null ? "" : path.getInterfacePath().toString().replace('\n', ' ');
                    outputDialog.appendln(s);
                    outputDialog.appendln();
                    // if (path == null)
                    // unavailableET++;
                }
            }

        }

        outputDialog.appendln("##############################################################");
        outputDialog.appendln("#                         UnstoredET                         #");
        outputDialog.appendln("##############################################################");

        for (ModelElement me : readUpdateEdges) {
            Kante edge = (Kante) me;
            // bei allen Interprtiert-Bezeihungen
            Aufgabe auf = null;
            Objekttyp ot = null;
            if (edge.getStart() instanceof Aufgabe && (edge.getDirection() == FORWARD || edge.getDirection() == DOUBLE)) {
                auf = (Aufgabe) edge.getStart();
                ot = (Objekttyp) edge.getEnd();
            } else if (edge.getEnd() instanceof Aufgabe && (edge.getDirection() == BACKWARD || edge.getDirection() == DOUBLE)) {
                auf = (Aufgabe) edge.getEnd();
                ot = (Objekttyp) edge.getStart();
            }
            // wenn es keine Bearbeitet-Beziehung war -> nächste Kante
            if (auf == null) {
                continue;
            }

            outputDialog.appendln("\n##############################################");
            String s = auf + " BEARBEITET " + ot;
            s = s.replace('\n', ' ');
            outputDialog.appendln(s, true);
            outputDialog.appendln("Master-Anwendungssysteme des Objekttyps:");
            List<ModelElement> awbList = new ArrayList<>(mac.expandPartOfElementSet(mac.getDirectMasterApplicationSystems(ot)));
            Alphabetical.sort(awbList);
            outputDialog.appendln(awbList.toString().replace('\n', ' '));
            outputDialog.appendln("Speichernde Anwendungssysteme des Objekttyps:");
            awbList = new ArrayList<>(mac.expandPartOfElementSet(mac.getDirectStorageApplicationSystems(ot)));
            Alphabetical.sort(awbList);
            outputDialog.appendln(awbList.toString().replace('\n', ' '));
            outputDialog.appendln("Master- und speichernde Anwendungssysteme des Objekttyps:");
            awbList = new ArrayList<>(mac.expandPartOfElementSet(mac.getDirectMasterAndStorageApplicationSystems(ot)));
            Alphabetical.sort(awbList);
            outputDialog.appendln(awbList.toString().replace('\n', ' '));

            List<ModelElement> aufOrgKombis = auf.getConnectedElements(AufOrgKombination.class);
            // von der Aufgabe und ihren Oberaufgaben alle Konfigs einsammeln
            for (ModelElement aufParent : auf.getParentElements()) {
                aufOrgKombis.addAll(aufParent.getConnectedElements(AufOrgKombination.class));
            }

            Set<ModelElement> masterAWBs = mac.expandPartOfElementSet(mac.getDirectMasterApplicationSystems(ot));
            for (ModelElement aufOrg : aufOrgKombis) {
                List<ModelElement> abKonfigs = aufOrg.getConnectedElements(ABKonfiguration.class);

                if (abKonfigs.size() == 0) {
                    outputDialog.appendln("Konfiguration:");
                    outputDialog.appendln("[]");
                    outputDialog.appendln("Master-AWB:");
                    s = masterAWBs == null ? "null" : masterAWBs.toString().replace('\n', ' ');
                    outputDialog.appendln(s);
                    outputDialog.appendln("Kommunikationsweg:");
                    outputDialog.appendln("--- Keine Kommunikation möglich! ---");

                    continue;
                }

                for (ModelElement abKonf : abKonfigs) {
                    List<ModelElement> konfigAWBs = abKonf.getConnectedElements(Anwendungsbaustein.class);

                    outputDialog.appendln("Konfiguration:");
                    s = konfigAWBs.toString().replace('\n', ' ');
                    outputDialog.appendln(s);

                    outputDialog.appendln("Master-AWB:");
                    s = masterAWBs == null ? "null" : masterAWBs.toString().replace('\n', ' ');
                    outputDialog.appendln(s);

                    boolean supporterIsMaster = false;
                    if (masterAWBs != null) {
                        for (ModelElement awb : konfigAWBs) {
                            if (masterAWBs.contains(awb)) {
                                supporterIsMaster = true;
                                break;
                            }
                        }
                    }
                    if (!supporterIsMaster) {
                        outputDialog.appendln(" --- Kein AWB der Konfiguration ist ein Master! ---");
                    }

                    outputDialog.appendln("Kommunikationsweg:");
                    Object path = commPathFinder.getShortestPath(konfigAWBs, mac.getDirectMasterAndStorageApplicationSystems(ot), ot).getAwbPath();
                    s = path == null ? "--- Keine Kommunikation möglich! ---" : path.toString().replace('\n', ' ');
                    outputDialog.appendln(s);
                    outputDialog.appendln();
                }
            }

        }

        outputDialog.setVisible(true);
        outputDialog.setLocationRelativeTo(Static.getMainFrame());

    }
}
