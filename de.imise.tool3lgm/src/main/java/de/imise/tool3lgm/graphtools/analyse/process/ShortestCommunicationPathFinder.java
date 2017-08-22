package de.imise.tool3lgm.graphtools.analyse.process;

import static de.imise.tool3lgm.graphtools.elements.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.elements.Edge.FORWARD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.graphtools.analyse.context.ModelAnalyzerCache;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.ObjReprVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EtntEtdtKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Repraesentationsform;
import de.imise.util.SameTypePair;

/**
 * Mit dieser Klasse können für Objekttypen alle kürzesten Kommunikationspfade ermittelt werden.
 * Integer.MAX_VALUE
 *
 * @author AXS Created on 19.06.2008
 */
public class ShortestCommunicationPathFinder {

    /**
     * Unendlich als Maximaler Kostenwert. Dieser darf nicht in der Kostenmatrix (bzw. in der
     * Distanzmatrix = Kostenmatrix nach FloydWarshall) als "echter Wert" auftauchen! (es ist extrem
     * unwahrscheinlich, dass das mal passiert, da die Einzelkosten immer 1 sind)wenn man einen
     * negativen Wert nehmen wollte, müsste man im Algorithmus mehr Vergleiche anstellen -> wär
     * langsamer
     */
    public final static int INFINITY = Integer.MAX_VALUE;

    /**
     * Mappt von einem Objekttyp auf eine Liste von Schnittstellen, über die der Objekttyp gesendet
     * oder empfangen werden kann. Der Index einer Schnittstelle in dieser Liste entspricht dem
     * Index der Schnittstelle in der Kosten- und Pfadmatrix.
     */
    private final Map<ModelElement, List<ModelElement>> objectTypeToInterfaceList = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf die dazugehörige Kostenmatrix der Kommunikationswege. Eine
     * Kostenmatrix ist ein zweidimensionales Array, welches für alle Schnittstellen, über die der
     * Objekttyp kommuniziert werden kann, den <code>int</code>-Wert der Kosten speichert. Die
     * Indizes der Schnittstellen in den Matrix-Feldern entsprechen den Indizes der Schnittstellen
     * in <code>objectTypeToInterfaceList</code>. Die Kostenmatrizen haben die Form
     * <code>int[][]</code>:<br>
     * Indizes: 1. Zeilenschnittstelle, 2. Spaltenschnittstelle<br>
     * Wert: int-Wert der Kosten, die Kommunikation von Zeilenschnittstelle zu Spaltenschnittstelle
     * verursacht. Gibt es keine Kommunikationsweg, sind die Kosten <code>INFINITY</code>.
     */
    private final Map<ModelElement, int[][]> objectTypeToCostMatrix = new HashMap<>();

    /**
     * Mappt von einem Objekttyp auf die dazugehörige Pfadmatrix der Kommunikationswege. Eine
     * Pfadmatrix ist ein zweidimensionales Array, welches für jede Kombination aus Zeilen- und
     * Spaltenschnittstelle die Liste der Schnittstellen speichert, über die man einen Objekttyp von
     * der Zeilen- zur Spaltenschnittstelle versenden kann. Ist keine Kommunikation möglich, bleibt
     * die Liste <code>null</code>. Die Indizes der Schnittstellen in den Matrix-Feldern entsprechen
     * den Indizes der Schnittstellen in <code>objectTypeToInterfaceList</code>. Die Pfadmatrizen
     * haben die Form <code>ArrayList[][]</code>:<br>
     * Indizes: 1. Zeilenschnittstelle, 2. Spaltenschnittstelle<br>
     * Wert: Liste der Schittstellen, die einen Pfad von der Zeilen- zur SPlatenschnittstelle
     * beschreiben. Wenn es einen Pfad gibt, sind die Zeilen- und Spaltenschnittstelle immer auch
     * enthalten.
     */
    private final Map<ModelElement, List<ModelElement>[][]> objectTypeToPathMatrix = new HashMap<>();

    /**
     * Cache zur effizienten Untersuchung von Beziehungen zwischen Elementen
     */
    private final ModelAnalyzerCache analyzerCache;

    /**
     * @param analyzerCache ein bereits initialisierter <code>ModelAnalyzerCache</code> dessen
     *            <code>GDCollection</code> den Kontext vorgibt
     */
    public ShortestCommunicationPathFinder(final ModelAnalyzerCache analyzerCache) {
        super();
        this.analyzerCache = analyzerCache;
    }

    /**
     * @param gdcoll Modell, in dem nach kürzesten Pfaden gesucht werden soll
     */
    public ShortestCommunicationPathFinder(final GDCollection gdcoll) {
        super();
        analyzerCache = new ModelAnalyzerCache(gdcoll);
    }

    /**
     * @return Returns the analyzerCache.
     */
    public ModelAnalyzerCache getAnalyzerCache() {
        return analyzerCache;
    }

    // /**
    // * Gibt für jeden AWB alle Schnittstellen aus, die ihm zugerechnet werden.
    // */
    // public void printAllSchnittstellenOfAllBaustein(){
    // System.out.println("#########################################################################################");
    // for (Iterator it = awbToBSSList.keySet().iterator(); it.hasNext();){
    // ModelElement awb = (ModelElement)it.next();
    // ArrayList list = (ArrayList)awbToBSSList.get(awb);
    // int size = list.size();
    // System.out.println(awb + " hat die Schnitstellen: ");
    // for (int k=0; k<list.size(); k++)
    // System.out.println("\t"+(list.get(k)));
    // }
    // }
    //
    // /**
    // * @param otIndex
    // * Index des Objekttypen, für den die Matrix ausgegeben werden soll. Wenn alle ausgegeben
    // werden sollen, muss Index < 0 sein.
    // * /
    // public void printPathMatrix(int otIndex){
    // int min, max;
    //
    // if (otIndex<0){
    // min = 0;
    // max = objectTypes.size();
    // }else{
    // min = otIndex;
    // max = otIndex+1;
    // }
    // for (int o=min; o<max; o++){
    // System.out.println();
    // System.out.println("Objecttype " + objectTypes.get(o) +
    // "################################################");
    // for (int z = 0; z < schnittstellen.size(); z++){
    // System.out.println();
    // System.out.println("Zeile "+ z);
    // for (int s = 0; s < schnittstellen.size(); s++){
    // System.out.print(o+"."+z+"."+s+".)\t");
    // if (pathMatrix[o][z][s]!=null){
    // for (int anz = 0; anz<pathMatrix[o][z][s].size(); anz++){
    // if (anz>0) System.out.print(", ");
    // System.out.print(pathMatrix[o][z][s].get(anz));
    // }
    // System.out.println();
    // }else
    // System.out.println("null");
    // }
    // }
    // }
    // }
    //
    //
    // /**
    // * @param otIndex
    // * Index des Objekttypen, für den die Matrix ausgegeben werden soll. Wenn alle ausgegeben
    // werden sollen, muss Index < 0 sein.
    // */
    // public void printCostMatrix(int otIndex){
    // //Ausgabe der Kostenmatrizen, die nach Ausführen von findShortestPath(o) die Distanzmatrizen
    // sein sollten
    // System.out.println();
    // System.out.println("##### KostenMatrizen ######################################################");
    // int min, max;
    // if (otIndex<0){
    // min = 0;
    // max = objectTypes.size();
    // }else{
    // min = otIndex;
    // max = otIndex+1;
    // }
    // int N = schnittstellen.size();
    // for (int o=min; o<max; o++){
    // System.out.println(objectTypes.get(o)+ "+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+");
    // int maxLength=0;
    // for (int z=0; z<N; z++){
    // int l = schnittstellen.get(z).toString().length();
    // if (l>maxLength) maxLength=l;
    // }
    // StringBuilder sb = new StringBuilder("");
    // while(sb.length()<maxLength) sb.append(' ');
    // sb.append('\t');
    // sb.append('\t');
    // for (int z=0; z<N; z++){
    // sb.append('\t');
    // sb.append((z));
    // sb.append('.');
    // }
    // System.out.println(sb);
    // for (int z=0; z<N; z++){
    // sb = new StringBuilder(schnittstellen.get(z).toString().replace('\n',' '));
    // sb = new StringBuilder(sb.toString().replace('\t',' '));
    // while(sb.length()<maxLength) sb.append(' ');
    // System.out.print(z+".\t"+sb+ " \t");
    // for (int s=0; s<N; s++){
    // if (costMatrix[o][z][s]!=INFINITY)
    // System.out.print(costMatrix[o][z][s]+ "\t" );
    // else
    // System.out.print("*\t" );
    // }
    // System.out.println();
    // }
    // }
    // }

    /**
     * Das hier ist der Floyd-Warhall-Algorhytmus.
     *
     * @param o Index des Objekttyps, für den die Pfadmatrix mit den kürzesten Pfaden aufgebaut
     *            werden soll.
     */
    private void initShortestPath(final Objekttyp ot) {
        Object interfacesObject = objectTypeToInterfaceList.get(ot);
        // wenn diese init-Methode schon einmal für den Objekttyp aufgerufen wurde -> nicht noch
        // einmal initialisieren
        if (interfacesObject != null) {
            return;
        }
        // alle Schnittstellen einsammeln, die den Objekttyp überhaupt übertragen können
        // über den Pfad: Objekttyp - Repräsentationsform - ETNTKombination <-> KommBeziehung -
        // Start und Ende der Edge
        // kann man alle Schnittstellen einsammeln, die den Objekttyp überhaupt übertragen können
        List<ModelElement> interfaces = new ArrayList<>();
        // Liste aller Schnittstellenpaare, von denen der Objekttyp von der ersten zur zweiten
        // Schnittstelle geschickt
        // werden kann. Wird ein Objekttyp mit Teil-Objekttypen über eine Kommunikationsbeziehung
        // geschickt, so gilt
        // das auch für alle seine Teile
        Set<SameTypePair<ModelElement>> sendToReceiveInterfacePairs = new HashSet<>();

        // Repräsentationsformen des OT und seiner übergeordneten Objekttypen holen
        Collection<ModelElement> otAndParents = analyzerCache.getObjectTypeAndParents(ot);
        Set<ModelElement> reprSet = new HashSet<>();
        for (ModelElement otParent : otAndParents) {
            reprSet.addAll(otParent.getConnectedElements(Repraesentationsform.class, ObjReprVerbindung.class));
        }

        // für jede Repräsentationsform
        for (ModelElement ntdt : reprSet) {
            // ETNTKombinationen und ETDTKombinationen der Repräsentationsform holen
            // für jede ETNTKombination und ETDTKombination alle Kommunikationsbeziehungen
            // einsammeln
            for (ModelElement etntdtKombi : ntdt.getConnectedElements(EtntEtdtKombination.class)) {
                int[] directions = {
                        FORWARD,
                        BACKWARD
                };
                for (int d = 0; d < directions.length; d++) {
                    // für alle Kommunikationsverbindungen, die den Objekttyp in der jewieligen
                    // Richtung verschicken können
                    for (ModelElement commBezMe : etntdtKombi.getConnectedElements(KommBeziehung.class, KommbezEtntVerbindung.class, directions[d])) {
                        KommBeziehung commBez = (KommBeziehung) commBezMe;
                        ModelElement start = commBez.getStart();
                        if (!interfaces.contains(start)) {
                            interfaces.add(start);
                        }
                        ModelElement end = commBez.getEnd();
                        if (!interfaces.contains(end)) {
                            interfaces.add(end);
                        }
                        if (directions[d] == FORWARD) {
                            sendToReceiveInterfacePairs.add(new SameTypePair<>(start, end));
                        } else {
                            sendToReceiveInterfacePairs.add(new SameTypePair<>(end, start));
                        }
                    }
                }
            }
        }
        // Die Schnittstellenliste für diesen Objekttyp merken
        objectTypeToInterfaceList.put(ot, interfaces);

        int N = interfaces.size();

        int[][] costMatrix = new int[N][N];
        @SuppressWarnings("unchecked")
        List<ModelElement>[][] pathMatrix = new ArrayList[N][N];

        objectTypeToCostMatrix.put(ot, costMatrix);
        objectTypeToPathMatrix.put(ot, pathMatrix);

        if (N == 0) {
            return;
        }

        // int f0=0, f1=0, f2=0, f3=0;

        // für jede Schnittstelle in interfaces
        for (int z = 0; z < N; z++) {
            // wird die Liste aller Schnittstellen, die die aktuelle Schnittstelle mit OT erreichen
            // kann
            List<ModelElement> allConnectedFromSSWithOT = new ArrayList<>();

            ModelElement sZ = interfaces.get(z);
            for (SameTypePair<ModelElement> ssPair : sendToReceiveInterfacePairs) {
                if (sZ == ssPair.getFirstItem()) {
                    allConnectedFromSSWithOT.add(ssPair.getSecondItem());
                }
            }

            // Eigentlich sollte eine Schnittstelle immer genau mit 1 Anwendungssystem verbunden
            // sein. Damit im Fehlerfall (keins oder mehr als 1)
            // keine üble Exception fliegt, gehen wir hier mal nicht von dem "eigentlich"-Fall aus
            Collection<ModelElement> sameAWBsOfInterfaceZ = new HashSet<>();
            for (ModelElement awb : sZ.getConnectedElements(Anwendungsbaustein.class, AwbKommssVerbindung.class)) {
                sameAWBsOfInterfaceZ.addAll(analyzerCache.getSameApplicationSystems(awb));
            }

            // Kostenmatrix erzeugen und Pfadmatrix initialisieren
            // System.out.println(ss+ " kommt an " + al.size() + " Schnittstellen");
            // für alle Schnittstellen
            for (int s = 0; s < N; s++) {
                // die Schnittstelle hat zu sich selbst die Kommunikationskosten 0. Die Pfadmatrix
                // bleibt hier leer
                if (s == z) {
                    costMatrix[z][s] = 0;
                    // f0++;
                    continue;
                }
                ModelElement sS = interfaces.get(s);
                List<ModelElement> awbOfInterfaceS = sS.getConnectedElements(Anwendungsbaustein.class, AwbKommssVerbindung.class);
                // wenn die beiden Schniistellen auf dem selben Baustein liegen, haben sie die
                // Kosten 0 und man muss sich den Pfad merken
                if (sameAWBsOfInterfaceZ.containsAll(awbOfInterfaceS)) {
                    costMatrix[z][s] = 0;
                    pathMatrix[z][s] = new ArrayList<>();
                    pathMatrix[z][s].add(sZ);
                    pathMatrix[z][s].add(sS);
                    // f1++;
                    // wenn sie nicht auf demselben Baustein liegen und eine Kommunikationsbeziehung
                    // zwischen ihnen besteht,
                    // über die der Objekttyp verschickt werden kann -> Kosten 1 und Pfad merken
                } else if (allConnectedFromSSWithOT.contains(sS)) {
                    costMatrix[z][s] = 1;
                    pathMatrix[z][s] = new ArrayList<>();
                    pathMatrix[z][s].add(sZ);
                    pathMatrix[z][s].add(sS);
                    // f2++;
                } else {
                    costMatrix[z][s] = INFINITY;
                    // f3++;
                }
            }
        }

        // This is Floyd's algorithm.
        for (int pivot = 0; pivot < N; pivot++) {
            for (int z = 0; z < N; z++) {
                for (int s = 0; s < N; s++) {
                    if (costMatrix[z][pivot] == INFINITY || costMatrix[pivot][s] == INFINITY) {
                        continue;
                    }
                    int tempDistance = costMatrix[z][pivot] + costMatrix[pivot][s];
                    if (tempDistance < costMatrix[z][s]) {
                        costMatrix[z][s] = tempDistance;
                        pathMatrix[z][s] = new ArrayList<>(pathMatrix[z][pivot]);
                        for (int k = 1; k < pathMatrix[pivot][s].size(); k++) {

                            pathMatrix[z][s].add(pathMatrix[pivot][s].get(k));
                        }
                    }
                }
            }
        }
    }

    /**
     * @param startAWB
     * @param endAWB
     * @param objekttyp
     * @return
     */
    public MinimalCommunicationPath getShortestPath(final Anwendungsbaustein startAWB, final Anwendungsbaustein endAWB, final Objekttyp objekttyp) {
        List<ModelElement> start = new ArrayList<>(1);
        start.add(startAWB);
        List<ModelElement> end = new ArrayList<>(1);
        end.add(endAWB);
        return getShortestPath(start, end, objekttyp);
    }

    /**
     * @param startAWBs
     * @param endAWBs
     * @param objekttyp
     * @return
     */
    public MinimalCommunicationPath getShortestPath(final Collection<ModelElement> startAWBs, final Collection<ModelElement> endAWBs, final Objekttyp objekttyp) {
        return new MinimalCommunicationPath(startAWBs, endAWBs, objekttyp, this);
    }

    // ////////////////
    // Hilfsklassen //
    // ////////////////

    public class MinimalCommunicationPath {

        /**
         * Comment for <code>startAWBs</code>
         */
        private Collection<ModelElement> startAWBs;

        /**
         * Comment for <code>endAWBs</code>
         */
        private Collection<ModelElement> endAWBs;

        /**
         * Comment for <code>objekttyp</code>
         */
        private Objekttyp objekttyp;

        /**
         * Comment for <code>interfacePath</code>
         */
        private List<ModelElement> interfacePath;

        /**
         * Comment for <code>awbPath</code>
         */
        private List<ModelElement> awbPath;

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
        private MinimalCommunicationPath(final Collection<ModelElement> startAWBs, final Collection<ModelElement> endAWBs, final Objekttyp objekttyp, final ShortestCommunicationPathFinder sPathFinder) {
            this.startAWBs = startAWBs;
            this.endAWBs = endAWBs;
            this.objekttyp = objekttyp;

            // zuerst prüfen, ob sich ein Baustein der startAWBs mit einem der endAWBs überlagert
            ModelElement sameAWBInStartEnd = null;
            for (ModelElement awb : startAWBs) {
                if (endAWBs.contains(awb)) {
                    sameAWBInStartEnd = awb;
                    break;
                }
            }
            // wenn keine Kommunikation nötig ist, weil Start- und Endbaustein auf demselben
            // Gesamtbaustein liegen
            if (sameAWBInStartEnd != null) {
                awbPath = new ArrayList<>();
                awbPath.add(sameAWBInStartEnd);
                pathCosts = 0;
                interfacePathLength = 0;
            }

            // jetzt alle Teile und Oberbausteine der Start- und Endbausteine in die Prüfung
            // einschließen
            Set<ModelElement> fullStartAWBs = sPathFinder.analyzerCache.expandPartOfElementSet(startAWBs);
            Set<ModelElement> fullEndAWBs = sPathFinder.analyzerCache.expandPartOfElementSet(endAWBs);
            // wenn noch Teile oder Oberbausteine hinzugeommen sind -> die Pürfung nochmal machen
            // (dieses
            // doppelte Vorgehen hat den Vorteil, dass zuerst ein Element gefunden werden, dass
            // direkt in den
            // startAWBs und endAWBs enthalten war und erst dann ein Element aus den Ereiterung
            // fullStartAWBs und fullEndAWBs
            if (startAWBs.size() < fullStartAWBs.size() || endAWBs.size() < fullEndAWBs.size()) {
                for (ModelElement awb : startAWBs) {
                    if (endAWBs.contains(awb)) {
                        sameAWBInStartEnd = awb;
                        break;
                    }
                }
                // wenn keine Kommunikation nötig ist, weil Start- und Endbaustein auf demselben
                // Gesamtbaustein liegen
                if (sameAWBInStartEnd != null) {
                    awbPath = new ArrayList<>();
                    awbPath.add(sameAWBInStartEnd);
                    pathCosts = 0;
                    interfacePathLength = 0;
                    return;
                }
            }

            // den sPathFinder für den Objekttyp initilisieren, falls nicht schon passiert
            sPathFinder.initShortestPath(objekttyp);
            int[][] costMatrix = sPathFinder.objectTypeToCostMatrix.get(objekttyp);
            List<ModelElement>[][] pathMatrix = sPathFinder.objectTypeToPathMatrix.get(objekttyp);

            Set<ModelElement> startSchnittStellen = new HashSet<>();
            Set<ModelElement> endSchnittStellen = new HashSet<>();
            // alle Schnittstellen der Startkonfiguration holen
            for (ModelElement awb : fullStartAWBs) {
                startSchnittStellen.addAll(sPathFinder.analyzerCache.getInterfaces(awb));
            }
            // alle Schnittstellen der Endkonfiguration holen
            for (ModelElement awb : fullEndAWBs) {
                endSchnittStellen.addAll(sPathFinder.analyzerCache.getInterfaces(awb));
            }

            // den erst besten kürzesten Pfad von einer Startschnittstelle
            // zu einer Endschnittstelle suchen
            List<ModelElement> schnittstellen = sPathFinder.objectTypeToInterfaceList.get(objekttyp);
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
                    List<ModelElement> path = pathMatrix[posOfStartInSchnittStellen][posOfEndInSchnittStellen];
                    // wenn es einen gibt, dessen Länge bstimmen
                    int length = path == null ? INFINITY : path.size();
                    // wenn der aktuelle Pfad kostengünstiger oder kürzer
                    // ist oder noch gar kein Pfad gefunden wurde -> nimm
                    // den aktuellen
                    if (costMatrix[posOfStartInSchnittStellen][posOfEndInSchnittStellen] < pathCosts || length < interfacePathLength) {
                        // System.out.println("["+otPosition+"]["+posOfStartInSchnittStellen+"]["+posOfEndInSchnittStellen+"]");
                        pathCosts = costMatrix[posOfStartInSchnittStellen][posOfEndInSchnittStellen];
                        interfacePath = pathMatrix[posOfStartInSchnittStellen][posOfEndInSchnittStellen];
                        interfacePathLength = interfacePath.size();
                    }
                }
            }
            if (interfacePath != null) {
                awbPath = new ArrayList<>();
                for (ModelElement ss : interfacePath) {
                    ModelElement owner = ss.getConnectedElements(Anwendungsbaustein.class).get(0);
                    if (awbPath.size() == 0 || awbPath.size() > 0 && awbPath.get(awbPath.size() - 1) != owner) {
                        awbPath.add(owner);
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
        public List<ModelElement> getInterfacePath() {
            return interfacePath;
        }

        /**
         * @return Returns the awbPath.
         */
        public List<ModelElement> getAwbPath() {
            return awbPath;
        }

        /**
         * @return Returns the startAWBs.
         */
        public Collection<ModelElement> getStartAWBs() {
            return startAWBs;
        }

    }

}