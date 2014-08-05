package de.imise.tool3lgm.graphtools.analyse.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.analyse.context.ModelAnalyzerCache;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntDotVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntNatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KommBeziehung;
import de.imise.tool3lgm.graphtools.elements.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.ObjReprVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;

/**
 * Mit dieser Klasse können für Objekttypen alle kürzesten Kommunikationspfade ermittelt werden.
 * Integer.MAX_VALUE
 * 
 * @author AXS Created on 19.06.2008
 */
@Deprecated
public class ShortestCommunicationPathFinderOld {

    /**
     * Unendlich als Maximaler Kostenwert. Dieser darf nicht in der Kostenmatrix (bzw. in der
     * Distanzmatrix = Kostenmatrix nach FloydWarshall) als "echter Wert" auftauchen! (es ist extrem
     * unwahrscheinlich, dass das mal passiert, da die Einzelkosten immer 1 sind)wenn man einen
     * negativen Wert nehmen wollte, müsste man im Algorithmus mehr Vergleiche anstellen -> wär
     * langsamer
     */
    public final static int INFINITY = Integer.MAX_VALUE;

    /**
     * Kostenmatrix für die Kommuniaktionswegberechnungen Indizes: 1. Objekttyp, 2.
     * Zeilenschnittstelle, 3. Spaltenschnittstelle<br>
     * Wert: Kosten, die Kommunikation von Zeilenschnittstelle zu Spaltenschnittstelle verursacht.
     * Gibt es keine Kommunikationsweg, sind die Kosten <code>INFINITY</code>
     */
    private int[][][] costMatrix;

    /**
     * Pfadmatrix für die Kommuniaktionswegberechnungen Indizes: 1. Objekttyp, 2.
     * Zeilenschnittstelle, 3. Spaltenschnittstelle<br>
     * Wert: Pfad über alle Schnittstellen von Zeilenschnittstelle zu Spaltenschnittstelle, der
     * durchlaufen werden muss, um den Objekttyp zu übgertragen (also Liste von Schnittstellen, die
     * nacheinander durchlaufen werden müssen)
     */
    private ArrayList<ModelElement>[][][] pathMatrix;

    /**
     * Mapppt von einem Anwendungsbausteins auf eine Liste aller seiner eigenen Schnittstellen und
     * die seiner Parts und Parents. Wir sehen die Teil-Von-Beziehung zwischen Anwendungsbausteinen
     * als bidirektionale Kommunikationsbeziehung, über die jeder Objekttyp ausgetauscht werden
     * kann. Das bedeutet, dass die Eigenschaften einer Schnittstelle, die ein AWB besitzt, an alle
     * anderen AWBs übergehen, die mit dem AWB über einen beliebigen Pfad aus Teil-Von-Beziehungen
     * verbunden sind.
     */
    private HashMap<ModelElement, ArrayList<ModelElement>> awbToBSSList;

    /**
     * Mappt für jeden <code>Anwendungsbaustein</code> auf die Liste aller seiner Eltern, Kinder und
     * Geschwister - also aller Anwendungsbausteine, mit denen er eine Einheit bildet.
     */
    private HashMap<ModelElement, ArrayList<ModelElement>> awbToSameAWBList;

    /**
     * Liste aller Schnittstellen im <code>GraphDocument</code>
     */
    private ArrayList<ModelElement> schnittstellen;

    /**
     * Das GraphDocument in dem die Kommuniaktionswege analysiert werden sollen. Dies wird immer ein
     * Hauptdokument, da die vollständige XMLAnalyse nur im Hauptdokument sinnvoll ist.
     */
    private GraphDocument mainDoc;

    /**
     * Alle Objekttypen, für die die Untersuchung gestartet werden soll.
     */
    private ArrayList<ModelElement> objectTypes;

    /**
     * @param objekttypList Liste von Objekttypen, deren Kommunizierbarkeit untersucht werden soll
     */
    public ShortestCommunicationPathFinderOld(final ArrayList<ModelElement> objekttypList, final GDCollection gdcoll) {
        super();
        if (objekttypList != null && objekttypList.size() > 0) {
            mainDoc = gdcoll.getMainGraphDocument();
            objectTypes = objekttypList;
            initPathAndCostMatrix();
        }
    }

    /**
     * @param objekttypList Liste von Objekttypen, deren Kommunizierbarkeit untersucht werden soll
     */
    public ShortestCommunicationPathFinderOld(final ArrayList<ModelElement> objekttypList, final ModelAnalyzerCache analyzerCache) {
        super();
        if (objekttypList != null && objekttypList.size() > 0) {
            mainDoc = analyzerCache.getCollection().getMainGraphDocument();
            objectTypes = objekttypList;
            initPathAndCostMatrix();
        }
    }

    /**
	 * 
	 */
    private void init() {
        ArrayList<ModelElement> bausteine = mainDoc.getModelItems(Anwendungsbaustein.class, true);

        int N = bausteine.size();

        if (N == 0) {
            awbToBSSList = new HashMap<ModelElement, ArrayList<ModelElement>>(0);
            awbToSameAWBList = new HashMap<ModelElement, ArrayList<ModelElement>>(0);
            return;
        }

        awbToSameAWBList = new HashMap<ModelElement, ArrayList<ModelElement>>(bausteine.size());
        for (ModelElement awb : bausteine) {
            ArrayList<ModelElement> awbListObject = awbToSameAWBList.get(awb);
            if (awbListObject != null) {
                continue;
            }
            ArrayList<ModelElement> al = awb.getPartAndParentElements();
            for (ModelElement partParent : al) {
                awbToSameAWBList.put(partParent, al);
            }
        }

        awbToBSSList = new HashMap<ModelElement, ArrayList<ModelElement>>(bausteine.size());
        // für alle Schlüssel-AWB in der Liste der gleichen AWB (das sind alle AWB, die es gibt)
        for (ModelElement awb : awbToSameAWBList.keySet()) {
            // wenn es für ihn schon eine Schnittstellenliste gibt -> nächsten Schlüssel-AWB
            ArrayList<ModelElement> sameSSListObject = awbToBSSList.get(awb);
            if (sameSSListObject != null) {
                continue;
            }
            // Hole die Liste alle mit dem Schlüssel-AWB eine Einheit bildenenden AWB
            ArrayList<ModelElement> sameAWBList = awbToSameAWBList.get(awb);
            // neue Schnittstellenliste, die für jeden Einzel-AWB eines Gesamt-AWB identisch sein
            // wird
            ArrayList<ModelElement> sameSSList = new ArrayList<ModelElement>();
            // für alle Einzel-AWB eines Gesamt-AWB
            for (ModelElement sameAWB : sameAWBList) {
                // hole alle seine Schnittstellen und füge sie zur Gesamtliste hinzu
                sameSSList.addAll(sameAWB.getConnectedElementsByEdge(AwbKommssVerbindung.class));
                // lege die Gesamtliste für den Einzel-AWB in die globale HashMap
                awbToBSSList.put(sameAWB, sameSSList);
            }
        }

        // printAllSchnittstellenOfAllBaustein();
    }

    /**
     * Liefert alle Anwendungsbausteine, die mit dem übergebenen in irgendeiner Teil-Von-Beziehung
     * stehen.
     * 
     * @param awb
     * @return
     */
    public ArrayList<ModelElement> getEqualsAWB(final Anwendungsbaustein awb) {
        return awbToSameAWBList.get(awb);
    }

    /**
     * Prüft, ob die Schnittstelle eine Kommunikationsbeziehunge besitzt, über die irgendein
     * Objekttyp gesendet oder empfangen wird.
     * 
     * @param bss
     * @return <code>true</code> wenn die Schnittstelle nicht kommunizieren kann
     */
    public boolean isUseless(final Bausteinschnittstelle bss) {

        return false;
    }

    /**
     * Gibt für jeden AWB alle Schnittstellen aus, die ihm zugerechnet werden.
     */
    public void printAllSchnittstellenOfAllBaustein() {
        System.out.println("#########################################################################################");
        for (ModelElement awb : awbToBSSList.keySet()) {
            ArrayList<ModelElement> list = awbToBSSList.get(awb);
            System.out.println(awb + " hat die Schnitstellen: ");
            for (ModelElement ss : list) {
                System.out.println("\t" + ss);
            }
        }
    }

    /**
     * @param otIndex Index des Objekttypen, für den die Matrix ausgegeben werden soll. Wenn alle
     *            ausgegeben werden sollen, muss Index < 0 sein.
     */
    public void printPathMatrix(final int otIndex) {
        int min, max;

        if (otIndex < 0) {
            min = 0;
            max = objectTypes.size();
        } else {
            min = otIndex;
            max = otIndex + 1;
        }
        for (int o = min; o < max; o++) {
            System.out.println();
            System.out.println("Objecttype " + objectTypes.get(o) + "################################################");
            for (int z = 0; z < schnittstellen.size(); z++) {
                System.out.println();
                System.out.println("Zeile " + z);
                for (int s = 0; s < schnittstellen.size(); s++) {
                    System.out.print(o + "." + z + "." + s + ".)\t");
                    if (pathMatrix[o][z][s] != null) {
                        for (int anz = 0; anz < pathMatrix[o][z][s].size(); anz++) {
                            if (anz > 0) {
                                System.out.print(", ");
                            }
                            System.out.print(pathMatrix[o][z][s].get(anz));
                        }
                        System.out.println();
                    } else {
                        System.out.println("null");
                    }
                }
            }
        }
    }

    /**
     * @param otIndex Index des Objekttypen, für den die Matrix ausgegeben werden soll. Wenn alle
     *            ausgegeben werden sollen, muss Index < 0 sein.
     */
    public void printCostMatrix(final int otIndex) {
        // Ausgabe der Kostenmatrizen, die nach Ausführen von findShortestPath(o) die
        // Distanzmatrizen sein sollten
        System.out.println();
        System.out.println("##### KostenMatrizen ######################################################");
        int min, max;
        if (otIndex < 0) {
            min = 0;
            max = objectTypes.size();
        } else {
            min = otIndex;
            max = otIndex + 1;
        }
        int N = schnittstellen.size();
        for (int o = min; o < max; o++) {
            System.out.println(objectTypes.get(o) + "+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+");
            int maxLength = 0;
            for (int z = 0; z < N; z++) {
                int l = schnittstellen.get(z).toString().length();
                if (l > maxLength) {
                    maxLength = l;
                }
            }
            StringBuilder sb = new StringBuilder("");
            while (sb.length() < maxLength) {
                sb.append(' ');
            }
            sb.append('\t');
            sb.append('\t');
            for (int z = 0; z < N; z++) {
                sb.append('\t');
                sb.append(z);
                sb.append('.');
            }
            System.out.println(sb);
            for (int z = 0; z < N; z++) {
                sb = new StringBuilder(schnittstellen.get(z).toString().replace('\n', ' '));
                sb = new StringBuilder(sb.toString().replace('\t', ' '));
                while (sb.length() < maxLength) {
                    sb.append(' ');
                }
                System.out.print(z + ".\t" + sb + " \t");
                for (int s = 0; s < N; s++) {
                    if (costMatrix[o][z][s] != INFINITY) {
                        System.out.print(costMatrix[o][z][s] + "\t");
                    } else {
                        System.out.print("*\t");
                    }
                }
                System.out.println();
            }
        }
    }

    /**
     * @param o Index des Objekttyps
     * @param z Index der Sendeschnittstelle
     * @param s Index der Empfängerschnittstelle
     * @return
     */
    private boolean connected(final int o, final int z, final int s) {
        return costMatrix[o][z][s] != INFINITY;
    }

    // static long fullFloydTime=0l;

    /**
     * Das hier ist der Floyd-Warhall-Algorhytmus.
     * 
     * @param o Index des Objekttyps, für den die Pfadmatrix mit den kürzesten Pfaden aufgebaut
     *            werden soll.
     */
    private void findShortestPath(final int o) {
        // long ss= System.currentTimeMillis();

        // This is Floyd's algorithm.
        int N = costMatrix[o].length;
        for (int pivot = 0; pivot < N; pivot++) {
            for (int z = 0; z < N; z++) {
                for (int s = 0; s < N; s++) {
                    if (connected(o, z, pivot) && connected(o, pivot, s)) {
                        int tempDistance = costMatrix[o][z][pivot] + costMatrix[o][pivot][s];
                        if (tempDistance < costMatrix[o][z][s]) {
                            costMatrix[o][z][s] = tempDistance;
                            pathMatrix[o][z][s] = new ArrayList<ModelElement>(pathMatrix[o][z][pivot]);
                            for (int k = 1; k < pathMatrix[o][pivot][s].size(); k++) {
                                pathMatrix[o][z][s].add(pathMatrix[o][pivot][s].get(k));
                            }
                        }
                    }
                }
            }
        }
        // fullFloydTime+=System.currentTimeMillis()-ss;
    }

    @SuppressWarnings("unused")
    private static int f1 = 0, f2 = 0, f3 = 0;

    /**
     * @param doc
     * @param pathMatrix
     * @param costMatrix
     */
    @SuppressWarnings("unchecked")
    public final void initPathAndCostMatrix() {

        // long start = System.currentTimeMillis();

        int objectTypesCount = objectTypes.size();

        // ArrayList<ModelElement> newObjectTypes = new ArrayList<ModelElement>(objectTypesCount);
        // Das hier ist noch falsch, weil jetzt auch OTs rausfallen, deren Eltern Kommuniziert
        // werden können
        // //alle Objekttypen entfernen, die man nicht kommunizieren kann = die in keiner
        // Repräsentationsform
        // //sind, die kommuniziert wird
        // loop: for (Iterator otIt = objectTypes.iterator(); otIt.hasNext();){
        // Objekttyp ot = (Objekttyp)otIt.next();
        // HashSet repForms = new HashSet(ot.getConnectedElements(Repraesentationsform.class));
        // Set parents = ot.getParents();
        // for (Iterator parentsIt = parents.iterator(); parentsIt.hasNext();){
        // Objekttyp parent = (Objekttyp)parentsIt.next();
        // repForms.addAll(parent.getConnectedElements(Repraesentationsform.class));
        // }
        // for (Iterator repFormsIt = repForms.iterator(); repFormsIt.hasNext();){
        // Repraesentationsform repF = (Repraesentationsform)repFormsIt.next();
        // ArrayList etntdt = repF.getConnectedElements(EtntEtdtKombination.class);
        // for (Iterator etntdtIt = etntdt.iterator(); etntdtIt.hasNext();){
        // EtntEtdtKombination etntdtKomb = (EtntEtdtKombination)etntdtIt.next();
        // if (etntdtKomb.getConnectedElements(KommBeziehung.class).size()>0){
        // newObjectTypes.add(ot);
        // continue loop;
        // }
        // }
        // }
        // }
        // objectTypes = newObjectTypes;
        // objectTypesCount = objectTypes.size();

        if (objectTypesCount == 0) {
            return;
        }

        schnittstellen = mainDoc.getModelItems(Bausteinschnittstelle.class);

        // if (schnittstellen.size()<=0)
        // return;
        // ArrayList newSchnittstellen = new ArrayList(schnittstellen.size());
        //
        // //alle Schnittstellen entfernen, die keine KommBez haben oder deren KommBezen nichts
        // senden/empfangen
        // loop: for (Iterator schnittstellenIt = schnittstellen.iterator();
        // schnittstellenIt.hasNext();){
        // Bausteinschnittstelle bss = (Bausteinschnittstelle)schnittstellenIt.next();
        // ArrayList kommBez = bss.getConnections(KommBeziehung.class);
        // for (Iterator kommBezIt = kommBez.iterator(); kommBezIt.hasNext();){
        // KommBeziehung kb = (KommBeziehung)kommBezIt.next();
        // ArrayList etntdts = kb.getConnectedElements(EtntEtdtKombination.class);
        // if (etntdts.size()>0)
        // newSchnittstellen.add(bss);
        // }
        // }
        //
        // // System.err.println(schnittstellen.size());
        // // System.err.println(newSchnittstellen.size());
        //
        // schnittstellen = newSchnittstellen;

        int N = schnittstellen.size();
        if (N <= 0) {
            return;
        }

        costMatrix = new int[objectTypesCount][N][N];
        pathMatrix = new ArrayList[objectTypesCount][N][N];

        // long end = System.currentTimeMillis();
        // System.err.println("ShortestCommunicationPathFinder.initPathAndCostMatrix()1: " +
        // (end-start));

        // start = System.currentTimeMillis();
        init();
        // end = System.currentTimeMillis();
        // System.err.println("ShortestCommunicationPathFinder.initPathAndCostMatrix()2: " +
        // (end-start));

        // hole alle Kommunikationsbeziehungen
        ArrayList<ModelElement> communicationLinks = mainDoc.getModelItems(KommBeziehung.class);

        // Mappt von einem Objekttyp auf die Liste aller Schnittstellenpaare, von denen
        // der Objekttyp von der ersten zur zweiten Schnittstelle geschickt werden kann. Wird ein
        // Objekttyp mit
        // Teil-Objekttypen über eine Kommunikationsbeziehung geschickt, so gilt das auch für alle
        // seine Teile
        HashMap<ModelElement, ArrayList<Pair<ModelElement>>> objekttypToInterfacePairList = new HashMap<ModelElement, ArrayList<Pair<ModelElement>>>(objectTypesCount);

        // ArrayList kanteWithOT = new ArrayList(objectTypesCount);
        // for (int o = 0; o < objectTypesCount; o++)
        // kanteWithOT.add(null);

        // System.out.println("In dem Teilmodell " + doc.getTitle() + " gibt es " +
        // allBssBssKanten.size() + " Kommunikationsbeziehungen.");

        // start = System.currentTimeMillis();

        // für jede Kommunikationsbeziehung = Kanten von einer Bss zu einer Bss (das sind alles
        // Doppelkanten)
        for (int r = 0; r < communicationLinks.size(); r++) {
            Doppelkante commLink = (Doppelkante) communicationLinks.get(r);

            int[] directions = {
                    Doppelkante.FORWARD, Doppelkante.BACKWARD
            };

            for (int d = 0; d < directions.length; d++) {

                ArrayList<ModelElement> ntAndDtOfKante = commLink.getConnectedElements(EreignisNachrichtenTyp.class, mainDoc, KommbezEtntVerbindung.class, directions[d], false);
                ntAndDtOfKante.addAll(commLink.getConnectedElements(EreignisDokumentenTyp.class, mainDoc, KommbezEtntVerbindung.class, directions[d], false));

                // für jede dieser EtNt-Kombinationen
                for (ModelElement nt : ntAndDtOfKante) {
                    ArrayList<ModelElement> ntOfEtnt = nt.getConnectedElementsByEdge(EtntNatVerbindung.class);
                    ntOfEtnt.addAll(nt.getConnectedElementsByEdge(EtntDotVerbindung.class));
                    for (ModelElement nachrichtentyp : ntOfEtnt) {
                        ArrayList<ModelElement> objekttypes = nachrichtentyp.getConnectedElementsByEdge(ObjReprVerbindung.class);
                        int z = objekttypes.size();
                        for (int l = 0; l < z; l++) {
                            objekttypes.addAll(objekttypes.get(l).getPartElements(false));
                        }
                        // für jeden dieser OTs
                        for (ModelElement objekttyp : objekttypes) {
                            if (!objectTypes.contains(objekttyp)) {
                                continue;
                            }
                            // Liste aus
                            ArrayList<Pair<ModelElement>> sendToReceiveInterfaceList = objekttypToInterfacePairList.get(objekttyp);
                            // wenn in der Liste an der Stelle des entsprechenden OT noch kein
                            // Schnittstellenpaar steht
                            if (sendToReceiveInterfaceList == null) {
                                // erzeuge eine neue Liste
                                sendToReceiveInterfaceList = new ArrayList<Pair<ModelElement>>();
                                // füge sie für den entsprechenden OT hinzu
                                objekttypToInterfacePairList.put(objekttyp, sendToReceiveInterfaceList);
                            }
                            ModelElement sendInterface, receiveInterface;
                            if (directions[d] == Doppelkante.BACKWARD) {
                                sendInterface = commLink.getStart();
                                receiveInterface = commLink.getEnd();
                            } else {
                                sendInterface = commLink.getEnd();
                                receiveInterface = commLink.getStart();
                            }
                            sendToReceiveInterfaceList.add(new Pair<ModelElement>(sendInterface, receiveInterface));
                        }
                    }
                }
            }
        }

        // end = System.currentTimeMillis();
        // System.err.println("ShortestCommunicationPathFinder.initPathAndCostMatrix()3: " +
        // (end-start));

        // start = System.currentTimeMillis();

        // für jede Schnittstellenpaarliste eines OTs
        for (int o = 0; o < objectTypesCount; o++) {
            // System.out.println(objektTypes.get(o) + " ########################");
            ArrayList<Pair<ModelElement>> sendReceiveSSListForOt = objekttypToInterfacePairList.get(objectTypes.get(o));
            // für jede Schnittstelle
            for (int z = 0; z < N; z++) {
                // wird die Liste aller Schnittstellen, die die jeweilige Schnittstelle mit OT
                // erreichen kann
                ArrayList<ModelElement> allConnectedFromSSWithOT = new ArrayList<ModelElement>();
                if (sendReceiveSSListForOt != null) {
                    ModelElement ss = schnittstellen.get(z);
                    for (int k = 0; k < sendReceiveSSListForOt.size(); k++) {
                        Pair<ModelElement> ssPair = sendReceiveSSListForOt.get(k);
                        if (ss == ssPair.me1) {
                            allConnectedFromSSWithOT.add(ssPair.me2);
                        }
                    }

                    // 2.)füge zu allConnectedFromSSWithOT alle Schnittstellen hinzu (wenn sie nicht
                    // schon drin sind), die mit ss
                    // -auf dem selben Baustein liegen oder
                    // -auf einem seiner Unterbausteine oder Oberbausteine oder Geschwister
                    // das jetzt folgende geht davon aus, dass eine Schnittstelle IMMER zu GENAU
                    // EINEM Baustein gehört
                    // hole den rechnerbasierten AB zu dem ss evtl. gehört
                    for (ModelElement awb : awbToBSSList.keySet()) {
                        ArrayList<ModelElement> ssList = awbToBSSList.get(awb);
                        if (ssList.contains(ss)) {
                            for (ModelElement bss : ssList) {
                                if (ss != bss && !allConnectedFromSSWithOT.contains(bss)) {
                                    allConnectedFromSSWithOT.add(bss);
                                }
                            }
                        }
                    }
                }
                // Kostenmatrix erzeugen und Pfadmatrix initialisieren (der Zwischenschritt der
                // Verbindungsmatrix ist hier implizit enthalten)
                // System.out.println(ss+ " kommt an " + al.size() + " Schnittstellen");
                for (int s = 0; s < N; s++) {

                    // alle Schnittstellen, die die aktuelle Schnittstelle mit dem aktuellen OT
                    // erreichen kann
                    // (sie selbst nicht) bekommen in der Matrixzeile eine 1

                    if (s == z) {
                        costMatrix[o][z][s] = 0;
                        f1++;
                    } else if (allConnectedFromSSWithOT.contains(schnittstellen.get(s))) {
                        costMatrix[o][z][s] = 1;
                        pathMatrix[o][z][s] = new ArrayList<ModelElement>();
                        pathMatrix[o][z][s].add(schnittstellen.get(z));
                        pathMatrix[o][z][s].add(schnittstellen.get(s));
                        f2++;
                    } else {
                        costMatrix[o][z][s] = INFINITY;
                        f3++;
                    }

                }

            }

            // jetzt die vollständige Pfadmatrix erzeugen (hiernach stehen ist pathMatrix die
            // Pfadmatrix und costMatrix die
            // Distanzmatrix)

            /*
             * if (o==0){ System.out.println(schnittstellen); printPathMatrix(0);
             * System.out.println(
             * "\n\n------------------------------------------------------------------------\n\n");
             * printCostMatrix(0); findShortestPath(o); System.out.println(
             * "\n\n------------------------------------------------------------------------\n\n");
             * printPathMatrix(0); System.out.println(
             * "\n\n------------------------------------------------------------------------\n\n");
             * printCostMatrix(0); }else
             */findShortestPath(o);

        }

        // end = System.currentTimeMillis();

        // System.err.println(f1);
        // System.err.println(f2);
        // System.err.println(f3);

        // System.err.println("ShortestCommunicationPathFinder.initPathAndCostMatrix()4: " +
        // (end-start));
        // System.err.println("ShortestCommunicationPathFinder.initPathAndCostMatrix()Floyd: " +
        // fullFloydTime);

        // ################################################################################################################
        // jetzt sind Pfad- und neue Distanzmatrizen angelegt (Distanzmatrix ist in costMatrix)
        // ################################################################################################################

        // System.err.println(schnittstellen);
        // printPathMatrix(-1);
        // printCostMatrix(-1;)

    }

    /*
     * private class PathMatrixItem{ Bausteinschnittstelle s1; Bausteinschnittstelle s2; ArrayList
     * path; int costs; private PathMatrixItem(Bausteinschnittstelle s1, Bausteinschnittstelle s2,
     * ArrayList path, int costs){ } }
     */

    /**
     * @param startAWBs
     * @param endAWBs
     * @param objekttyp
     * @return
     */
    public MinimalCommunicationPath getShortestPath(final Collection<ModelElement> startAWBs, final Collection<ModelElement> endAWBs, final Objekttyp objekttyp) {
        return new MinimalCommunicationPath(startAWBs, endAWBs, objekttyp);
    }

    // ////////////////
    // Hilfsklassen //
    // ////////////////

    public class MinimalCommunicationPath {

        /**
         * Comment for <code>startAWBs</code>
         */
        private final Collection<ModelElement> startAWBs;

        /**
         * Comment for <code>endAWBs</code>
         */
        private final Collection<ModelElement> endAWBs;

        /**
         * Comment for <code>objekttyp</code>
         */
        private final Objekttyp objekttyp;

        /**
         * Comment for <code>interfacePath</code>
         */
        private ArrayList<ModelElement> interfacePath;

        /**
         * Comment for <code>awbPath</code>
         */
        private ArrayList<ModelElement> awbPath;

        /**
         * Comment for <code>pathCosts</code>
         */
        private int pathCosts = INFINITY;

        /**
         * Länge des Pfades über die Schnittstellen. Wenn die erste und 2. Schnittstelle eines
         * Pfades auf demselben Anwendungssystem liegen, so kann dieser Pfad von den Pfadkosten her
         * trotzdem minimal sein, da die Kosten für Schnittstellen auf dem selebn Anwendungssystem
         * immer 0 betragen. Ein wikrlich minimaler Pfad hat die geringsten Kosten bei geringster
         * Länge.
         */
        private int interfacePathLength = INFINITY;

        /**
         * @param startAWBs
         * @param endAWBs
         * @param objekttyp
         */
        private MinimalCommunicationPath(final Collection<ModelElement> startAWBs, final Collection<ModelElement> endAWBs, final Objekttyp objekttyp) {
            this.startAWBs = startAWBs;
            this.endAWBs = endAWBs;
            this.objekttyp = objekttyp;

            HashSet<ModelElement> startSchnittStellen = new HashSet<ModelElement>();
            HashSet<ModelElement> endSchnittStellen = new HashSet<ModelElement>();
            // alle Schnittstellen der Startkonfiguration holen
            for (ModelElement awb : startAWBs) {
                startSchnittStellen.addAll(awbToBSSList.get(awb));
            }
            // alle Schnittstellen der Endkonfiguration holen
            for (ModelElement awb : endAWBs) {
                endSchnittStellen.addAll(awbToBSSList.get(awb));
            }

            ModelElement sameAWBInStartEnd = null;
            for (ModelElement awb : startAWBs) {
                if (endAWBs.contains(awb)) {
                    sameAWBInStartEnd = awb;
                }
            }

            // wenn keine Kommunikation nötig ist, weil Star- und Endbaustein auf demselben
            // Gesamtbaustein liegen
            if (sameAWBInStartEnd != null) {
                awbPath = new ArrayList<ModelElement>();
                awbPath.add(sameAWBInStartEnd);
                pathCosts = 0;
                interfacePathLength = 0;
            } else {
                // in der ArrayList objectTypes die Position seines Objekttypen bestimmen
                int otPosition = objectTypes.indexOf(objekttyp);

                if (otPosition >= 0) {
                    // den erst besten kürzesten Pfad von einer Startschnittstelle zu einer
                    // Endschnittstelle suchen
                    loop: for (ModelElement startSS : startSchnittStellen) {
                        int posOfStartInSchnittStellen = schnittstellen.indexOf(startSS);
                        if (posOfStartInSchnittStellen < 0) {
                            continue loop;
                        }
                        for (ModelElement endSS : endSchnittStellen) {
                            int posOfEndInSchnittStellen = schnittstellen.indexOf(endSS);
                            if (posOfEndInSchnittStellen < 0) {
                                continue loop;
                            }
                            // den Pfad zw. den beiden Schnittstellen holen
                            ArrayList<ModelElement> path = pathMatrix[otPosition][posOfStartInSchnittStellen][posOfEndInSchnittStellen];
                            // wenn es einen gibt, dessen Länge bstimmen
                            int length = path == null ? INFINITY : path.size();
                            // wenn der aktuelle Pfad kostengünstiger oder kürzer ist oder noch gar
                            // kein Pfad gefunden wurde -> nimm den aktuellen
                            if (costMatrix[otPosition][posOfStartInSchnittStellen][posOfEndInSchnittStellen] < pathCosts || length < interfacePathLength) {
                                // System.out.println("["+otPosition+"]["+posOfStartInSchnittStellen+"]["+posOfEndInSchnittStellen+"]");
                                pathCosts = costMatrix[otPosition][posOfStartInSchnittStellen][posOfEndInSchnittStellen];
                                interfacePath = pathMatrix[otPosition][posOfStartInSchnittStellen][posOfEndInSchnittStellen];
                                interfacePathLength = interfacePath.size();
                            }
                        }
                    }
                }
                if (interfacePath != null) {
                    awbPath = new ArrayList<ModelElement>();
                    for (ModelElement ss : interfacePath) {
                        ArrayList<ModelElement> ssOwner = ss.getConnectedElements(Anwendungsbaustein.class);
                        if (ssOwner.size() > 0) {
                            ModelElement owner = ssOwner.get(0);
                            if (awbPath.size() == 0 || awbPath.size() > 0 && awbPath.get(awbPath.size() - 1) != owner) {
                                awbPath.add(owner);
                            }
                        }
                    }
                }
            }
        }

        /**
         * @return Returns the endAWBs.
         */
        public Collection<ModelElement> getEndAWBs() {
            return endAWBs;
        }

        /**
         * @return Returns the objekttyp.
         */
        public Objekttyp getObjekttyp() {
            return objekttyp;
        }

        /**
         * @return Returns the pathCosts.
         */
        public int getPathCosts() {
            return pathCosts;
        }

        /**
         * @return Returns the pathLength.
         */
        public int getInterfacePathLength() {
            return interfacePathLength;
        }

        /**
         * @return Returns the ssList.
         */
        public ArrayList<ModelElement> getInterfacePath() {
            return interfacePath;
        }

        /**
         * @return Returns the awbPath.
         */
        public ArrayList<ModelElement> getAwbPath() {
            return awbPath;
        }

        /**
         * @return Returns the startAWBs.
         */
        public Collection<ModelElement> getStartAWBs() {
            return startAWBs;
        }

    }

    /**
     * Kapselt ein geordnetes Paar von <code>ModelElement</code>s
     * 
     * @author AXS
     */
    private class Pair<T> {
        /**
         * Erstes <code>ModelElement</code>
         */
        T me1;

        /**
         * Zweites <code>ModelElement</code>
         */
        T me2;

        /**
         * @param me1 Erstes <code>ModelElement</code>
         * @param ec2 Zweites <code>ModelElement</code>
         */
        private Pair(final T me1, final T me2) {
            this.me1 = me1;
            this.me2 = me2;
        }
    }

}
