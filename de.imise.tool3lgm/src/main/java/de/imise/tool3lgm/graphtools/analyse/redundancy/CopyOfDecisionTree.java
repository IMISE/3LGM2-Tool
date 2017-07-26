/*
 * Created on 12.09.2007
 */
package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.util.Alphabetical;
import de.imise.util.collections.AlphabeticalSet;

/**
 * Die Redundanzanalyse wurde ursprünglich für Anwendungsbausteine in Bezug auf Aufgaben
 * geschrieben. D.h. es sollte die Frage geklärt werden, welche Anwendungsbausteine überflüssig
 * sind, da sie keine oder nur Aufgaben unterstützen, die schon von anderen Anwendungsbausteinen
 * erledigt werden, die man auf jeden Fall braucht. <br>
 * Mittlerweile ist die ganze XMLAnalyse so allgemein, dass man alle Elemente die über einen Pfad
 * verbunden sind, gegenseitig auf Redundanz testen kann. Alle Kommentare und Variablenbenennungen
 * gehen aber vom oben genannten Szenario aus.
 *
 * @author AXS
 */
public class CopyOfDecisionTree {

    /**
     * Der Wurzelknoten des Entscheidungsbaumes. Er repräsentiert den keinen AWB.
     */
    private DecisionTreeNode root = DecisionTreeNode.createDecisionTreeRoot();

    /**
     * Anazahl der AWB, die im Baum betrachtet werden. Dieser Zähler wird auch für die Tiefe des
     * Kombinationsbaumes angewendet.
     */
    private int awbCount;

    /**
     * Die Matrix mit den Verbindungsdaten der im Baum betrachteten Elemente
     */
    private int[][] data;

    /** unterstützende AWB pro Aufgabe */
    private int[] awbSupport;
    /** Index des ersten unterstützenden AWB pro Aufgabe (oder -1) */
    private int[] firstAwbSupportIndex;
    /** Index des letzten unterstützenden AWB pro Aufgabe (oder -1) */
    private int[] lastAwbSupportIndex;

    /**
     * unterstützte Aufgaben pro AWB. Diese Zahl wird negiert, wenn der AWB mind. eine Aufgabe
     * exklusiv untertsützt.
     */
    private int[] supportedAuf;
    /** Index der ersten unterstützten Aufgabe pro AWB (oder -1) */
    private int[] firstSupportedAufIndex;
    /** Index der letzten unterstützten Aufgabe pro AWB (oder -1) */
    private int[] lastSupportedAufIndex;

    /** Geringster bekannter Kostenwert eines Ergebnissets der Baumanalyse */
    int minPathCosts;

    /**
     * Liste der Blätter im Baum.
     */
    private List<DecisionTreeNode> leafs = new ArrayList<>();

    /**
     * Wird gebraucht, um bei großen Bäumen den zu Ende gehenden Speicher wieder zu bereinigen
     */
    private static Runtime runtime = Runtime.getRuntime();

    /**
     * Alle Aufgaben, die durch AWB in der Menge <code>exclusiveAWB</code> erledigt werden.
     */
    private final AlphabeticalSet<ModelElement> exclusiveFuncs = new AlphabeticalSet<>();

    /**
     * Ergebnis der XMLAnalyse
     */
    private RedundancyAnalysisResult result;

    /**
     * Menge aller AWB, die Funktionen unterstützen, die noch nicht durch exklusive AWB erledigt
     * werden.
     */
    private ModelElement[] notExclusiveAWB;

    /**
     * Alle Aufgaben, die noch nicht durch AWB in der Menge <code>exclusiveAWB</code> erledigt
     * werden.
     */
    private ModelElement[] notExclusiveFunc;

    /** Wird <code>true</code>, wenn die Berechnung abgebrochen werden soll */
    private boolean stop = false;

    /** Liste aller AWB, die betrachtet werden */
    private List<ModelElement> applicationSystems;

    /**
     * Table der für jede Aufgabe das Set aller von ihr unterstützten AWBs enthält.
     */
    private final Map<ModelElement, AlphabeticalSet<ModelElement>> funcToAWBSets = new HashMap<>();
    /**
     * Table der für jeden AWB das Set aller von ihm unterstützten Aufgaben enthält.
     */
    private final Map<ModelElement, AlphabeticalSet<ModelElement>> awbToFuncsSets = new HashMap<>();

    /**
     * @param resultToFill Analyseergebnis, das gefüllt werden soll
     */
    public CopyOfDecisionTree(final RedundancyAnalysisResult resultToFill) {
        super();
        result = resultToFill;
        init();
        // RedundancyChecker.printData(data);
        // hier kommt false zurück, wenn vom Benutzer abgebrochen wurde (durch Schließen des
        // ProgressDialogs)
        if (!createTree()) {
            return;
        }
        extractTree();

        findSameSupporter();

        // System.err.println(data.length);
        // System.err.println(data[0].length);
        // System.err.println(notExclusiveAWB.length);
        //
        // System.err.println(result.moreNeededAWB.size());
        // System.err.println(result.moreUselessAWB.size());

    }

    /**
     * Initialiert die globalen Variablen und Felder.
     */
    private void init() {
        GraphDocument doc = result.getGDCollection().getMainGraphDocument();

        // alle Aufgaben des doc ohne Teilaufgaben in einer alphabetischen Liste holen
        // ArrayList<ModelElement> functions = doc.getModelItems(result.getEndClass(), true, true,
        // true);
        // alle Teil-Anwendungsbausteine des doc in einer alphabetischen Liste holen
        applicationSystems = doc.getModelItems(result.getStartClass(), true, true, true);

        // Set aller Aufgaben, die von mehr als einem AWB unterstützt werden und von keinem AWB, der
        // mind. eine Aufgabe exklusiv unterstützt
        AlphabeticalSet<ModelElement> notExclusiveFuncSet = new AlphabeticalSet<>();
        // Set aller AWB, die mind. eine Aufgabe unterstützen, die auch von anderen AWB unterstützt
        // wird
        AlphabeticalSet<ModelElement> notExclusiveAWBSet = new AlphabeticalSet<>();

        // für jeden AWB
        for (ModelElement as : applicationSystems) {

            // Set aller Aufgaben, die der AWB unterstützt
            AlphabeticalSet<ModelElement> funcsOfAWB = new AlphabeticalSet<>();
            awbToFuncsSets.put(as, funcsOfAWB);
            Set<ModelElement> funcsAwb = PathFinder.getConnectedElements(as, result.getEndClass(), result.getMetaPath());
            Set<ModelElement> leafFuncAwb = new HashSet<>(funcsAwb.size());
            for (ModelElement func : funcsAwb) {
                leafFuncAwb.addAll(func.getAbsolutePartElements());
            }
            funcsOfAWB.addAll(funcsAwb);
            for (ModelElement func : funcsOfAWB) {
                // Set aller AWB, die die Aufgabe unterstützen holen
                AlphabeticalSet<ModelElement> AWBsOfFunc = funcToAWBSets.get(func);
                // wenn es für die aktuelle Aufgabe noch kein solches Set gibt -> eins anlegen
                if (AWBsOfFunc == null) {
                    AWBsOfFunc = new AlphabeticalSet<>();
                    funcToAWBSets.put(func, AWBsOfFunc);
                }
                // unterstützte Aufgabe des AWB merken
                AWBsOfFunc.add(as);
            }
        }

        // Jetzt sind die 2 Hashtables, die für jeden AWB auf alle von ihm unterstützten
        // Aufgaben mappen (awbToFuncsSets)und für jede Aufgabe auf die sie unterstützenden
        // AWBs (funcToAWBSets), initialisiert.
        // Für Aufgaben, die von gar keinem AWB unterstützt werden, gibts es keinen Eintrag
        // in funcToAWBSets, d.h. sie werden ignoriert. Die Gesamtliste aller Aufgaben
        // (functions), in der diese nicht unterstützten Aufgaben noch stehen, wird ab hier
        // nicht mehr verwendet.

        // für jeden AWB
        for (int awb = 0; awb < applicationSystems.size(); awb++) {
            // hole den AWB
            ModelElement as = applicationSystems.get(awb);

            // Set aller Aufgaben, die der AWB unterstützt
            AlphabeticalSet<ModelElement> funcsOfAWB = awbToFuncsSets.get(as);

            // wenn der AWB gar keine Aufgaben unterstützt -> als uunnützt merken und nächster AWB
            if (funcsOfAWB.size() == 0) {
                result.notSupportingAWB.add(as);
                continue;
            }
            // wird true, wenn der aktuelle AWB eine Aufgabe als einziger unterstützt
            boolean exclusive = false;
            // für alle Aufgaben, die der AWB untertsützt
            for (ModelElement auf : funcsOfAWB) {
                // hole die AWB, die die Aufgabe unterstützen
                AlphabeticalSet<ModelElement> AWBsOfFunc = funcToAWBSets.get(auf);
                // wenn der aktuelle AWB eine Aufgabe exklusiv unterstützt
                if (AWBsOfFunc.size() == 1) {
                    // den AWB als exklusiven Unterstützer merken
                    result.exclusiveAWB.add(as);
                    // alle Aufgaben des AWBs als exklusiv bereits unterstütze merken
                    exclusiveFuncs.addAll(funcsOfAWB);
                    exclusive = true;
                    break;
                }
            }
            // wenn der aktuelle AWB keine Aufgabe exklusiv unterstützt
            if (!exclusive) {
                // dieser AWB unterstützt nur Aufgaben, die er und noch weitere AWB unterstützen
                notExclusiveAWBSet.add(as);
            }
        }

        // für alle AWB, die mind. eine aber keine Aufgabe exklusiv unterstützen prüfe, ob schon
        // alle
        // ihre Aufgaben von exklusiv unterstützenden AWBs unterstützt werden
        AlphabeticalSet<ModelElement> reallyNotExclusiveAWB = new AlphabeticalSet<>();
        for (ModelElement as : notExclusiveAWBSet) {
            // wird true, wenn der AWB mind. eine Aufgabe unterstützt, die von keinem AWB
            // unterstützt
            // wird, die eine Aufgabe als einziger unterstützt
            boolean allAlreadySupported = true;
            // alle vom as unterstützten Aufgaben holen
            AlphabeticalSet<ModelElement> funcsOfAWB = awbToFuncsSets.get(as);
            // für jede dieser Aufgaben
            for (ModelElement f : funcsOfAWB) {
                // wenn sie noch nicht von den exklusiv unterstützenden AWBs erledigt wird
                if (!exclusiveFuncs.contains(f)) {
                    // zum Set aller nicht exklusiv unterstützten Aufgaben hinzufügen
                    notExclusiveFuncSet.add(f);
                    // es werden nicht bereits alle Aufgaben des aktuellen AWB durch exklusive
                    // AWB unterstützt
                    allAlreadySupported = false;
                }
            }
            // der AWB unterstützt mind. eine Aufgabe, die nicht von den exklusiv unterstützenden
            // unterstützt wird
            if (!allAlreadySupported) {
                reallyNotExclusiveAWB.add(as);
                // alle Aufgaben des AWB werden bereits unterstützt
            } else {
                // als nutzlosen AWB merken
                result.uselessAWB.add(as);
            }
        }

        // System.err.println("Anzahl AWB ohne eindeutig benoetigte und eindeutig ueberfluessige: "
        // + reallyNotExclusiveAWB.size());

        // Sets mit den Listen der AWB initialisieren, die dieselben Aufgaben unterstützen
        initEqualsSets(reallyNotExclusiveAWB);

        // System.err.println("Anzahl der zu untersuchenden Aequivalenzklassen: " +
        // result.equalsSets.size());

        // System.err.println("Anzahl der verbliebenen relevanten Aufgaben: " +
        // notExclusiveFuncSet.size());
        /*
         * Die Verbindungsmatrix erzeugen
         */
        notExclusiveAWB = new ModelElement[result.equalsSets.size()];
        int i = 0;
        for (AlphabeticalSet<ModelElement> set : result.equalsSets) {
            // immer nur den ersten Eintrag aus den Listen in equalsSets nehmen (es ex. mind. ein
            // Element in jeder Liste)
            notExclusiveAWB[i++] = set.first();
        }
        notExclusiveFunc = new ModelElement[notExclusiveFuncSet.size()];
        i = 0;
        for (ModelElement f : notExclusiveFuncSet) {
            notExclusiveFunc[i++] = f;
        }

        int[][] matrix = new int[notExclusiveAWB.length][notExclusiveFunc.length];

        for (int awb = 0; awb < notExclusiveAWB.length; awb++) {
            // alle vom as unterstützten Aufgaben holen
            AlphabeticalSet<ModelElement> funcsOfAWB = awbToFuncsSets.get(notExclusiveAWB[awb]);

            for (int auf = 0; auf < notExclusiveFunc.length; auf++) {
                if (funcsOfAWB.contains(notExclusiveFunc[auf])) {
                    matrix[awb][auf] = 1;
                }
            }
        }
        data = matrix;
        awbCount = notExclusiveAWB.length;
        int aufCount = notExclusiveFunc.length;

        // wenn es nichts zu berechnen gibt -> raus
        if (awbCount == 0) {
            return;
        }

        // unterstützende AWB pro Aufgabe
        awbSupport = new int[aufCount];
        // Index des ersten unterstützenden AWB pro Aufgabe (oder -1)
        firstAwbSupportIndex = new int[aufCount];
        // Index des letzten unterstützenden AWB pro Aufgabe (oder -1)
        lastAwbSupportIndex = new int[aufCount];

        // unterstützte Aufgaben pro AWB
        supportedAuf = new int[awbCount];
        // Index der ersten unterstützten Aufgabe pro AWB (oder -1)
        firstSupportedAufIndex = new int[awbCount];
        // Index der letzten unterstützten Aufgabe pro AWB (oder -1)
        lastSupportedAufIndex = new int[awbCount];

        // für jede Aufgabe die Indizes des ersten und letzten unterstützenden AWB mit -1
        // initialisieren
        for (int auf = 0; auf < aufCount; auf++) {
            firstAwbSupportIndex[auf] = -1;
            lastAwbSupportIndex[auf] = -1;
        }
        // für jeden AWB die Indizes der ersten und letzten unterstützten Aufgabe mit -1
        // initialisieren
        for (int awb = 0; awb < awbCount; awb++) {
            firstSupportedAufIndex[awb] = -1;
            lastSupportedAufIndex[awb] = -1;
        }

        // bester nächster AWB-Index für den Greedy-Algorithmus
        int maxIndex = 0;

        // die ganze Matrix einmal durchlaufen
        for (int awb = 0; awb < awbCount; awb++) {
            for (int auf = 0; auf < aufCount; auf++) {
                // wenn der aktuelle AWB die aktuelle Aufgabe unterstützt
                if (data[awb][auf] > 0) {

                    // Anzahl der unterstützten AWB der Aufgabe erhöhen
                    awbSupport[auf]++;
                    // wenn für die aktuelle Aufgabe noch kein AWB-Index als erster Unterstützer
                    // vermerkt ist
                    if (firstAwbSupportIndex[auf] == -1) {
                        // speichere den aktuellen AWB-Index als ersten Unterstützer
                        firstAwbSupportIndex[auf] = awb;
                    }
                    // speichere immer jeden AWB-Index als letzten Unterstützer
                    lastAwbSupportIndex[auf] = awb;

                    // Anzahl der unterstützten Aufgaben des AWB erhöhen
                    supportedAuf[awb]++;
                    // wenn für den aktuellen AWB noch kein Aufgaben-Index als erste Unterstütze
                    // vermerkt ist
                    if (firstSupportedAufIndex[awb] == -1) {
                        // speichere den aktuellen Aufgaben-Index als erste Unterstütze
                        firstSupportedAufIndex[awb] = auf;
                    }
                    // speichere immer jeden Aufgaben-Index als letzte Unterstütze
                    lastSupportedAufIndex[awb] = auf;
                }
            }
            // wenn der aktuelle AWB mehr Aufgaben untertsützt als der bisher bekannte AWB mit den
            // meisten Aufgaben
            if (supportedAuf[awb] > supportedAuf[maxIndex]) {
                // merke seinen Index als den besten
                maxIndex = awb;
            }

        }

        /*
         * //////////////////////////////////////////////////////////////////* Jetzt mit einem
         * Greedy-Algortithmus eine beste Lösung bestimmen. *
         */// ///////////////////////////////////////////////////////////////*

        // jede Aufgabe, die durch das bisher gefundene Set von AWB unterstützt wird, ist hier 1
        // gesetzt, sonst 0
        int[] bestSetSupportedFunctions = new int[notExclusiveFunc.length];

        // bei den AWB, die im Ergebnisset sind steht hier dann eine 1 sonst 0;
        int[] bestAWBIndex = new int[notExclusiveAWB.length];

        // Anzahl neuer Aufgaben, die ein AWB hinzufügen könnte. Am Anfang sind das jeweils alle
        // Aufgaben eines AWB
        int[] newFunctionsCountForAWB = new int[supportedAuf.length];
        System.arraycopy(supportedAuf, 0, newFunctionsCountForAWB, 0, supportedAuf.length);

        // Zähler für die vom Ergebnisset unterstützten Aufgaben
        int supportedFuncs = 0;

        do {
            bestAWBIndex[maxIndex] = 1;
            minPathCosts++;
            supportedFuncs += newFunctionsCountForAWB[maxIndex];
            // wenn alle Aufgaben unterstützt werden, ist man fertig
            if (supportedFuncs == aufCount) {
                break;
            }

            // für alle Aufgaben, die der beste AWB unterstützt auch mit 1 markieren
            for (int auf = firstSupportedAufIndex[maxIndex]; auf <= lastSupportedAufIndex[maxIndex]; auf++) {
                if (data[maxIndex][auf] > 0) {
                    bestSetSupportedFunctions[auf] = 1;
                }
            }
            // den Vektor mit den neuen Aufgaben pro AWB erneuern
            // für alle Aufgaben
            for (int awb = 0; awb < awbCount; awb++) {
                newFunctionsCountForAWB[awb] = 0;
                // wenn die Aufgabe schon im Ergebnisset vorkommt
                if (bestAWBIndex[awb] == 1) {
                    // sie kann keine neuen Funktionen mit einbringen
                    continue;
                }
                // für alle Aufgaben, die der aktuelle AWB unterstützt
                for (int auf = firstSupportedAufIndex[awb]; auf <= lastSupportedAufIndex[awb]; auf++) {
                    // wenn die aktuelle Aufgabe bereits von den AWB in der Lösung unterstützt
                    // werden
                    if (bestSetSupportedFunctions[auf] == 1) {
                        // nächste Aufgabe
                        continue;
                    }
                    // wenn er sie unterstützt
                    if (data[awb][auf] > 0) {
                        newFunctionsCountForAWB[awb]++;
                    }
                }
            }
            // Index des ersten AWB bestimmen, der am meisten neue Aufgaben dazubringt
            maxIndex = 0;
            for (int awb = 1; awb < newFunctionsCountForAWB.length; awb++) {
                if (newFunctionsCountForAWB[awb] > newFunctionsCountForAWB[maxIndex]) {
                    maxIndex = awb;
                }
            }

        } while (true);

    }

    /**
     * Initialisiert den Entscheidungsbaum
     *
     * @return <code>true</code>, wenn die Berechnung nicht abgebrochen wurde
     */
    private boolean createTree() {

        // Kostenwert des aktuell betrachteten Astes
        int pathCosts = 0;

        // der aktuell betrachtete Knoten im Baum. Er repräsentiert einen AWB
        DecisionTreeNode actNode = root;

        // RedundancyChecker.printData(data);

        // awbCount lokal kopieren, da er gleich um 1 erniedrigt wird, um die Schleife effizienter
        // auszuführen. Der globale Wert sollte nicht geändert werden, da es sein kann, dass doch
        // einmal innerhalb der Schleife eine andere Funktion aufgerufen wird, die den Originalwert
        // benötigt
        int awbCount = this.awbCount;
        // dieser Wert wird um 1 erniedrigt, weil man für den letzten Wert ständig Abfragen machen
        // muss und
        // man sich so das abziehen bei jeder Abfrage sparen kann
        awbCount--;

        // für jeden AWB
        for (int awb = 0; awb <= awbCount; awb++) {

            // wenn abgebrochen werden soll -> raus
            if (stop) {
                return false;
            }

            // der aktuelle AWB unterstützt mind. eine Aufgabe, die auch ein anderer AWB
            // unterstützt. Es können
            // jetzt noch 2 Fälle auftreten. Entweder alle Aufgaben des aktuellen AWB werden bereits
            // duch AWB im
            // Baum bzw. andere Aufgaben exklusiv unterstützende AWB erledigt ODER es gibt mind.
            // eine nicht von
            // den Vorgänger-AWBs unterstützte Aufgabe. Diese kann dann nur noch von AWBs nach dem
            // aktuellen
            // unterstützt werden, was im Baum das Hinzufügen eines Plus- und eines Minus-Knotens
            // nach sich zieht.
            // prüfen, ob jede Aufgabe, die der aktuelle AWB unterstützt bereits durch
            // die AWB in der Ergebnismenge unterstützt werden -> wenn ja -> dieser AWB bekommt
            // nur einen Knoten mit minus

            // Anzahl der Aufgaben, die der aktuelle AWB untertstützt, die auch von den AWBs in
            // der Ergebnismenge unterstützt werden
            int alreadySupported = 0;
            // wird true, wenn der aktuelle AWB mind eine Aufgabe als letzter möglicher AWB
            // unterstützen kann.
            // (da immer zuerst die Minus-Knoten eines Aplit-Knotens hinzugefügt werden, muss eine
            // Aufgabe aber
            // wenigstens von dem letzten möglichen AWB dann unterstützt werden)
            boolean lastSupporter = false;

            // solange bis alle Aufgaben, die der aktuelle AWB unterstützt, überprüft wurden
            for (int auf = firstSupportedAufIndex[awb]; auf <= lastSupportedAufIndex[awb]; auf++) {

                // wenn abgebrochen werden soll -> raus
                if (stop) {
                    return false;
                }

                // wenn der aktuelle AWB die aktuelle Aufgabe nicht untertsützt -> nächste Aufgabe
                if (data[awb][auf] == 0) {
                    continue;
                }
                // wird true, wenn ein anderer AWB, der in dem aktuellen Ast des Baumes als needed
                // markiert ist,
                // die aktuelle Aufgabe bereits unterstützt
                boolean already = false;

                lastSupporter = false;

                // Jetzt für alle AWB-Knoten die breits im Baum sind, prüfen, ob sie die aktuelle
                // Aufgabe auch unterstützen. Man kann sich im Baum das zurückgehen sparen, wenn
                // die Aufgabe gar nicht von einem AWB mit einem kleineren Index als dem aktuellen
                // unterstützt wird (firstSupportIndex[auf]>=awb) und braucht nur soviele Schritte
                // zurückgehen, bis man den AWB an firstSupportIndex[auf] überprüft hat.

                if (firstAwbSupportIndex[auf] < awb) {
                    // der actNode ist der parent des Knotens für den momentan überprüften AWB
                    DecisionTreeNode parent = actNode;
                    // Index des AWB vor dem aktuellen merken (= Index des Parent in der Matrix)
                    int a = awb - 1;
                    do {
                        // wenn ein Knoten 'gebraucht' wird (= Knoten mit einem Plus) und der von
                        // dem Knoten
                        // repräsentierte AWB die aktulelle Aufgabe auch unterstützt
                        if (parent.isNeeded() && data[a][auf] != 0) {
                            // merke, dass die Aufgabe bereits durch AWBs im Baum untertsützt wird
                            already = true;
                            // brich die Suche ab
                            break;
                        }
                        // hole den Parent-Knoten des aktuellen Knotens
                        parent = parent.getParent();
                        // Index des Parents in der Datenmatrix anpassen
                        a--;
                    } while (firstAwbSupportIndex[auf] <= a);
                }

                // die aktuelle Aufgabe wird schon durch die AWBs in der Ergebnismenge erledigt
                if (already) {
                    alreadySupported++;
                } else if (lastAwbSupportIndex[auf] == awb) {
                    lastSupporter = true;
                    break;
                }
            }

            // wenn abgebrochen werden soll -> raus
            if (stop) {
                return false;
            }

            // wenn die Zahl der vom aktuellen AWB unterstützten Aufgaben kleiner ist als die Zahl
            // der bereits duch die Vorgänger-AWBs unterstützten Aufgaben, dann unterstützt außer
            // dem
            // aktuellen AWB mind. ein AWB danach auch die Aufgabe -> füge einen Plus und einen
            // Minus-
            // Knoten ein
            if (alreadySupported < supportedAuf[awb]) {
                pathCosts++;
                // im Pfad werden noch nicht alle Aufgaben unterstützt und ein weiterer AWB erhöht
                // die Kosten
                // noch nicht über das Minimum; Sollten die minimalen Pfadkosten hier überschritten
                // werden, wird
                // kein neuer Knoten angehängt, sondern es wurden nur die Kosten des aktuellen
                // Pfades erhöht.
                if (pathCosts <= minPathCosts) {
                    if (awb == awbCount) {
                        actNode = actNode.addLeaf(true, pathCosts);
                    } else if (lastSupporter) {
                        actNode = actNode.addChild(true);
                    } else {
                        actNode.addChild(true);
                        actNode.addChild(false);
                        actNode = actNode.getFirstNode();
                    }
                }
                // Alle Aufgaben des aktuellen AWB werden bereits von anderen AWBs unterstützt ->
                // füge nur
                // einen Minusknoten ein
            } else {
                if (awb == awbCount) {
                    actNode = actNode.addLeaf(false, pathCosts);
                } else {
                    actNode = actNode.addChild(false);
                }
            }

            // Wenn nur noch 200000Bytes Speicher frei sind
            if (runtime.maxMemory() - runtime.totalMemory() == 0 && runtime.freeMemory() < 100000) {
                System.err.println("Speicher voll -> bereinige");
                // bereinige den Baum um die Äste, die Definitiv nicht mehr gebraucht werden
                clearMemory();
            }

            boolean goBack = false;

            if (pathCosts > minPathCosts) {
                awb--;
                pathCosts--;
                // solange wir nicht wieder beim root angekommen sind
                while (awb > -1) {

                    // wenn abgebrochen werden soll -> raus
                    if (stop) {
                        return false;
                    }

                    // den Parent des aktuellen Knotens holen
                    DecisionTreeNode parent = actNode.getParent();
                    // den 2. Knoten des parents holen
                    DecisionTreeNode secondChild = parent.getSecondNode();
                    // wenn der aktuelle Knoten der erste Knoten ist
                    if (actNode != secondChild) {
                        // wenn es aber einen 2. Knoten gibt
                        if (secondChild != null) {
                            // lösche den aktuellen und mache den ehemals 2. zum aktuellen
                            parent.removeChild(actNode);
                            // einer der beiden Geschwisterknoten ist immer needed und der andere
                            // nicht -> wenn
                            // wir also auf den Geschwisterknoten wechseln, dann muss man die
                            // Pfadkosten anpassen
                            if (actNode.isNeeded()) {
                                pathCosts--;
                            } else {
                                pathCosts++;
                            }
                            actNode = secondChild;
                            break;
                        }
                        // es gibt keinen 2. Kindknoten unter dem parent von actNode -> lösche
                        // actNode und mache den parent zu actNode
                        if (actNode.isNeeded()) {
                            pathCosts--;
                        }
                        parent.removeChild(actNode);
                        actNode = parent;
                        awb--;
                        // der aktuelle Knoten ist das 2. Kind -> das erste muss erhalten bleiben
                    } else {
                        // lösche actNode und mache den parent zu actNode
                        if (actNode.isNeeded()) {
                            pathCosts--;
                        }
                        parent.removeChild(actNode);
                        actNode = parent;
                        awb--;
                        // gehe im Baum zurück
                        goBack = true;
                        break;
                    }
                }
            } else if (actNode.isLeaf()) {
                // das Blatt zur Liste aller Blätter hinzufügen
                leafs.add(actNode);
                minPathCosts = pathCosts;
                // gleich eine Ebene zurückgehen (ein Blatt kann keinen Geschwisterknoten besitzen)
                if (actNode.isNeeded()) {
                    pathCosts--;
                }
                actNode = actNode.getParent();

                // wenn nur ein einziger AWB in diesen Algorithmus geschickt wurde, ist man hier
                // fertig
                if (actNode == root) {
                    return true;
                }

                awb--;
                goBack = true;
            }

            // wenn abgebrochen werden soll -> raus
            if (stop) {
                return false;
            }

            if (goBack) {
                // solange wir nicht wieder beim root angekommen sind
                while (awb > -1) {

                    // wenn abgebrochen werden soll -> raus
                    if (stop) {
                        return false;
                    }

                    // den Parent des aktuellen Knotens holen
                    DecisionTreeNode parent = actNode.getParent();
                    // den 2. Knoten des parents holen
                    DecisionTreeNode secondChild = parent.getSecondNode();

                    // wenn der 2. Knoten leer ist oder der aktuelle schon der 2. Knoten ist
                    if (secondChild == null || secondChild == actNode) {
                        // wenn man hier bis zur Wurzel zurückgekommen ist, ist man fertig
                        if (parent == root) {
                            return true;
                        }
                        // gehe im Baum eine Stufe hoch
                        if (actNode.isNeeded()) {
                            pathCosts--;
                        }
                        actNode = parent;
                        awb--;

                    } else {
                        // einer der beiden Geschwisterknoten ist immer needed und der andere nicht
                        // -> wenn
                        // wir also auf den Geschwisterknoten wechseln, dann muss man die Pfadkosten
                        // anpassen
                        if (actNode.isNeeded()) {
                            pathCosts--;
                        } else {
                            pathCosts++;
                        }
                        // wenn vorher ein Plus-Knoten als 2. Knoten eingefügt wurde, der jetzt aber
                        // das bekannte
                        // Minimum überschreitet -> lösche diesen Knoten, gehe auf den parent und
                        // dann weiter zurück
                        if (pathCosts > minPathCosts) {
                            // wenn man hier bis zur Wurzel zurückgekommen ist, ist man fertig
                            if (parent == root) {
                                return true;
                            }
                            pathCosts--;

                            // addPercentCount(2<<(notExclusiveAWB.length-awb-1));

                            awb--;

                            parent.removeChild(secondChild);
                            actNode = parent;
                        } else {
                            actNode = secondChild;
                            break;
                        }
                    }
                }

            }
        }
        return true;
    }

    /**
     * Ordnet aus dem Entscheidungsbaum die AWBs den Mengen moreUselessAWB und moreNeededAWB zu.
     * Alle Listen in <code>equalSets</code> haben danach mind. 2 Elemente und von denen von der
     * Funktionalität immer nur ein AWB gebraucht wird.
     */
    private void extractTree() {
        // Set das alle Elemente in minimalen Ästen genau einmal enthält
        AlphabeticalSet<ModelElement> minimalUnionSet = new AlphabeticalSet<>();

        // für alle Blätter
        for (int i = 0; i < leafs.size(); i++) {
            DecisionTreeNode actNode = leafs.get(i);
            // wenn das Blatt kein Minimalblatt ist -> nächstes
            if (actNode.getValue() > minPathCosts) {
                continue;
            }
            int index = awbCount;
            // jetzt für alle minimalen Äste die darin enthaltenen AWBs in einem Set zusammmen
            // sammeln
            while (actNode != root) {
                if (actNode == null) {
                    System.err.println("actNode ist null");
                    break;
                }
                index--;
                if (actNode.isNeeded()) {
                    minimalUnionSet.add(notExclusiveAWB[index]);
                }
                actNode = actNode.getParent();
            }
        }

        // Speicher leeren (Der Baum wird nicht mehr gebraucht)
        leafs.clear();
        leafs = null;
        root = null;

        // jetzt alle AWB, die nicht im minimalUnionSet vorkommen und keine Aufgabe
        // exklusiv unterstützen -> in moreUselessAWB kopieren
        for (int i = 0; i < notExclusiveAWB.length; i++) {
            if (!minimalUnionSet.contains(notExclusiveAWB[i])) {
                result.moreUselessAWB.add(notExclusiveAWB[i]);
                // auch alle, die genau dasselbe tun, wie der Stellvertreter im jeweiligen equalsSet
                // in die moreUseless kopieren
                for (int j = 0; j < result.equalsSets.size(); j++) {
                    AlphabeticalSet<ModelElement> as = result.equalsSets.get(j);
                    if (as.first() == notExclusiveAWB[i]) {
                        result.moreUselessAWB.addAll(as);
                        // dieses equalsSet entfernen
                        result.equalsSets.remove(j--);
                        // es kann nur 1 solches Set geben -> alle weiteren überspringen
                        break;
                    }
                }
                // der Stellvertreter des Äquivalenzsets kommt in der minimalen Schnittmenge vor
            } else {
                // wenn zugehörige Äquivalenzsets nur den Stellvertreter als einziges Element
                // enthält
                for (int j = 0; j < result.equalsSets.size(); j++) {
                    // den Stellvertreter in die moreNeededAWB kopieren und das Äquivalenzset aus
                    // der Liste der Sets entfernen
                    AlphabeticalSet<ModelElement> as = result.equalsSets.get(j);
                    if (as.size() == 1) {
                        result.moreNeededAWB.add(as.first());
                        // dieses equalsSet entfernen
                        result.equalsSets.remove(j--);
                    }
                }
            }
        }

        // System.err.println("Anzahl der durch den Enscheidungsbaum ebenfalls als ueberfluessig erkannte AWB: "
        // + result.moreUselessAWB.size());
        // for (int i=0; i<result.moreUselessAWB.size(); i++)
        // System.err.println(result.moreUselessAWB.get(i));

    }

    /**
     * Füllt im <code>RedundancyAnalysisResult</code> für alle überflüssigen Elemente die Liste der
     * Elemente, die das Element überflüssig machen
     */
    private void findSameSupporter() {
        List<AlphabeticalSet<ModelElement>> uselessLists = new ArrayList<>(2);
        uselessLists.add(result.uselessAWB);
        uselessLists.add(result.moreUselessAWB);
        for (AlphabeticalSet<ModelElement> uselessList : uselessLists) {
            for (ModelElement useless : uselessList) {
                result.uselessToNeeded.put(useless, getSameSupporter(useless));
            }
        }
    }

    /**
     * Wenn die Redundanz von Anwendungsbausteinen bezüglich Aufgaben berechnet wird, dann liefert
     * die Funktion für einen Anwendungsbaustein alle anderen Anwendungsbauteine, die dieselben
     * Aufgaben unterstützen.
     *
     * @param me
     * @return
     */
    private final AlphabeticalSet<ModelElement> getSameSupporter(final ModelElement me) {
        Collection<ModelElement> supported = PathFinder.getConnectedElements(me, result.getEndClass(), result.getMetaPath());
        AlphabeticalSet<ModelElement> returnSet = new AlphabeticalSet<>();
        for (ModelElement supped : supported) {
            Collection<ModelElement> supporter = PathFinder.getConnectedElements(supped, result.getStartClass(), result.getMetaPath());
            for (ModelElement supper : supporter) {
                if (!result.uselessAWB.contains(supper) && !result.moreUselessAWB.contains(supper)) {
                    returnSet.add(supper);
                }
            }
        }
        return returnSet;
    }

    /**
     * Findet alle Mengen von AWBs aus den kombinierbaren AWBs, die man gegeneinander austauschen
     * kann, da sie dieselben Aufgaben unterstützen.
     *
     * @param Set aller bisher als nicht exklusiv und nicht überflüssig erkannten AWB
     */
    private final void initEqualsSets(final AlphabeticalSet<ModelElement> notExclusiveAWBSet) {
        // Liste für alle nicht exklusiven, aber in jedem Kombinationsset enthaltenen AWBs
        List<ModelElement> maybeNotrequiredAWB = new ArrayList<>();

        // Liste in die alle Alphabetical-Sets mit den jeweils gleichen Aufgaben kommen
        result.equalsSets = new ArrayList<>();

        // für alle AWB, auf die nur mit den exklusiven nicht verzichtet werden kann
        for (ModelElement notExclusiveAWB : notExclusiveAWBSet) {
            Alphabetical.insert(maybeNotrequiredAWB, notExclusiveAWB);
            // aus dem Set der vom AWB unterstützten Aufgaben all die entfernen, die schon
            // von den exklusiven AWBs erledigt werden (die braucht man nicht beachten)
            AlphabeticalSet<ModelElement> funcOfAWB = awbToFuncsSets.get(notExclusiveAWB);
            funcOfAWB.removeAll(exclusiveFuncs);
        }
        // aus notExclusiveButNeededAWB alle AWB entfernen, die dasselbe tun, d.h. es bleibt genau
        // einer der
        // AWBs enthalten. Dieser eine ist immer der erste in der jeweiligen sameList
        for (int i = 0; i < maybeNotrequiredAWB.size(); i++) {
            AlphabeticalSet<ModelElement> sameList = new AlphabeticalSet<>();
            sameList.add(maybeNotrequiredAWB.get(i));
            Object funcOfAWB_I = awbToFuncsSets.get(maybeNotrequiredAWB.get(i));
            for (int j = i + 1; j < maybeNotrequiredAWB.size(); j++) {
                Object funcOfAWB_J = awbToFuncsSets.get(maybeNotrequiredAWB.get(j));
                if (funcOfAWB_I.equals(funcOfAWB_J)) {
                    sameList.add(maybeNotrequiredAWB.get(j));
                    maybeNotrequiredAWB.remove(j--);
                }
            }
            result.equalsSets.add(sameList);
        }
    }

    /**
     * Löscht im Baum alle Äste, die nicht zu bereits bekannten minimalen Lösungen führen.
     */
    private void clearMemory() {
        List<DecisionTreeNode> leafsNew = new ArrayList<>();
        for (DecisionTreeNode node : leafs) {
            // wenn bei einem Blatt die Kosten die aktuellen Minimalkosten überschreiten
            if (node.getValue() > minPathCosts) {
                // lösche solange ausgehend vom Baltt einen Ast hin zur Wurzel, wie der
                // Parent nur das zu löschende Kind und kein weiteres Kind besitzt
                while (true) {
                    if (node == root) {
                        break;
                    }
                    DecisionTreeNode parent = node.getParent();
                    parent.removeChild(node);
                    if (parent.getFirstNode() != null) {
                        break;
                    }
                    node = parent;
                }
            } else {
                leafsNew.add(node);
            }
        }
        leafs.clear();
        leafs.addAll(leafsNew);
        System.gc();
    }

    /**
     * Stopp die Berechnung
     */
    public final void stop() {
        stop = true;
    }

}
