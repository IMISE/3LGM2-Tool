/*
 * Created on 04.10.2007
 */
package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.event.StaticAction;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyDefinition;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractError;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.collections.AlphabeticalSet;
import de.imise.util.swing.dialog.MultipleOptionPane;
import de.imise.util.swing.dialog.OutputDialog;
import de.imise.util.swing.dialog.ProgressDialog;

/**
 * Stellt Funktionen bereit, die die funktionale Redundanz von Modellen untersuchen.
 *
 * @author AXS
 */
public class RedundancyChecker extends WindowAdapter {

    /**
     * Konstante für die Einstellung des Benutzers nur Anwendungssysteme zu analysieren
     */
    public static final int APPLICATION_SYSTEM_OPTION = 1;
    /**
     * Konstante für die Einstellung des Benutzers nur Organisationssysteme zu analysieren
     */
    public static final int ORGANISATION_SYSTEM_OPTION = 2;
    /**
     * Konstante für die Einstellung des Benutzers Anwendungssysteme und Organisationssysteme zu
     * analysieren
     */
    public static final int APPLICATION_AND_ORGANISATION_SYSTEM_OPTION = 3;

    /**
     * Legt die Vorauswahl der selektierten Optionen im Auswahldialog fest. Es sind nur 2 booleans
     * dort enthalten, einer für Anwendungssysteme und einer für Organisationssysteme.
     */
    private static Object[] selectedOptions = null;

    /**
     * <code>Thread</code> für die eigentliche Berechnung.<br>
     * Die Berechnung findet in einem eigenen Thread statt, damit man sie abbrechen kann. Wenn sie
     * nicht in einem eigenen Thread läuft, werden Benutzereingaben (Abbrechen) vom GUI nicht
     * weitergeleitet.
     */
    private Thread redundancyThread = null;

    /**
     * Konstruktor, um eine Instanz des Checkers mit eigenem <code>RedundancyThread</code> zu haben.
     */
    private RedundancyChecker() {
        super();
    }

    /**
     * Generiert einen Redundanzbericht für das übergebene Modell.<br>
     *
     * @param gdcoll Modell das analysiert werden soll
     * @return Redundanzreport
     */
    public static final void getReport(final GDCollection gdcoll) {

        // Instanz des Checkers anlegen, der einen eigenen RedundancyThread
        // besitzt
        RedundancyChecker rc = new RedundancyChecker();

        // Liste in die alle zu füllenden RedundancyAnalysisResult kommen
        List<RedundancyAnalysisResult> resultList = new ArrayList<>();

        do {
            String message = "";

            // wenn die Option
            // "beim Suchen übergeordnete Elemente berücksichtigen"
            // ausgeschaltet ist
            if (!UserProperties.isSearchParents()) {
                message += Tool3lgmConstants.getResString("ana_fr_search_parents_option_message1");
                message += Tool3lgmConstants.getResString("itv-bezogene_Opt") + " -> " + Tool3lgmConstants.getResString("consider_parents") + Tool3lgmConstants.getResString("ana_fr_search_parents_option_message2") + "\n\n";
            }

            message += Tool3lgmConstants.getResString("ana_fr_option_question");

            String con = " " + Tool3lgmConstants.getResString("ana_fr_concerning") + " ";

            String[] options = new String[5];
            options[0] = Tool3lgmConstants.getResString("RechAnwendungsbaustein_p") + con + Tool3lgmConstants.getResString("Aufgabe_p");
            options[1] = Tool3lgmConstants.getResString("KonAnwendungsbaustein_p") + con + Tool3lgmConstants.getResString("Aufgabe_p");
            options[2] = Tool3lgmConstants.getResString("Softwareprodukt_p") + con + Tool3lgmConstants.getResString("Aufgabe_p");
            options[3] = Tool3lgmConstants.getResString("Datenbanksystem_p") + con + Tool3lgmConstants.getResString("Objekttyp_p");
            options[4] = Tool3lgmConstants.getResString("ana_fr_self_defined_analysis");

            // null wenn Abrechen gedrückt wurde, sonst ein gültiges
            // Boolean-Array
            selectedOptions = MultipleOptionPane.showCheckBoxOptionDialog(Static.getMainFrame(), Tool3lgmConstants.getResString("redundancy_analysis"), message, options, null);

            // wenn abgebrochen werden soll
            if (selectedOptions == null) {
                return;
            }

            // erstes Result für die Anwendungsbausteine bezüglich Aufgaben
            RedundancyAnalysisResult result = null;
            // alle Metapfade zwischen Aufgabe und AWB holen (momentan ist nur
            // einer definiert; dieser ist der
            // Pfad Aufgabe wird unterstützt durch AWB und in dem Array an
            // Position 0)

            MetaPath metaPath = PathFinder.getMetaPathes(Anwendungsbaustein.class, Aufgabe.class)[0];

            // wenn beide Arten von AWB analysiert werden sollen
            if (selectedOptions[0] != null && selectedOptions[1] != null) {
                // AnalyseoptionenString für das Result zusammenbasteln
                String analyzedSystemTypes = Tool3lgmConstants.getResString("RechAnwendungsbaustein_p");
                analyzedSystemTypes += " " + Tool3lgmConstants.getResString("und") + " " + Tool3lgmConstants.getResString("KonAnwendungsbaustein_p") + con + Tool3lgmConstants.getResString("Aufgabe_p");
                result = new RedundancyAnalysisResult(gdcoll, Anwendungsbaustein.class, Aufgabe.class, metaPath, analyzedSystemTypes);
                // nur rechnerbasierte AWB
            } else if (selectedOptions[0] != null) {
                result = new RedundancyAnalysisResult(gdcoll, RechAnwendungsbaustein.class, Aufgabe.class, metaPath, options[0]);
                // nur papierbasierte AWB
            } else if (selectedOptions[1] != null) {
                result = new RedundancyAnalysisResult(gdcoll, KonAnwendungsbaustein.class, Aufgabe.class, metaPath, options[1]);
            }
            if (result != null) {
                resultList.add(result);
            }

            // Softwareprodukte bezüglich Aufgaben
            if (selectedOptions[2] != null) {
                // alle Metapfade zwischen Aufgabe und Softwareprodukten holen
                // (momentan ist nur einer definiert; dieser ist der
                // Pfad Aufgabe wird unterstützt durch SWP und in dem Array an
                // Position 0)
                metaPath = PathFinder.getMetaPathes(Softwareprodukt.class, Aufgabe.class)[0];
                resultList.add(new RedundancyAnalysisResult(gdcoll, Softwareprodukt.class, Aufgabe.class, metaPath, options[2]));
            }

            // Datenbanksystemen bezüglich Objekttypen
            if (selectedOptions[3] != null) {
                // alle Metapfade zwischen Objekttypen und Datenbanksystemen
                // holen (momentan sind 2 definiert
                // Pfad Aufgabe wird unterstützt durch SWP und in dem Array an
                // Position 1)
                metaPath = PathFinder.getMetaPathes(Datenbanksystem.class, Objekttyp.class)[1];
                resultList.add(new RedundancyAnalysisResult(gdcoll, Datenbanksystem.class, Objekttyp.class, metaPath, options[3]));
            }

            // selbst definierte XMLAnalyse
            if (selectedOptions[4] != null) {
                MetaPathSelector mps = MetaPathSelector.showDialog(Tool3lgmConstants.getResString("ana_fr_class1_label"), Tool3lgmConstants.getResString("ana_fr_class2_label"), Tool3lgmConstants.getResString("metapath"));
                if (!mps.isValid()) {
                    resultList.clear();
                } else {
                    Class<? extends ModelElement> c1 = mps.getSelectedClass1();
                    Class<? extends ModelElement> c2 = mps.getSelectedClass2();
                    MetaPath mp = mps.getSelectedMetaPath();
                    String resultName = c2.getSimpleName() + " " + mp.toString();
                    resultList.add(new RedundancyAnalysisResult(gdcoll, c1, c2, mp, resultName));
                }
            }

            // wenn im Auswahl-Dialog gar nichts angekreuzt war -> Dialog
            // nochmal zeigen
        } while (resultList.size() == 0);

        // Konsistenzvorraussetzungen prüfen, die für ein korrektes Ergebnis notwendig sind
        // 1.) Alle Inkonsistenzen, die die normale Konsistenzprüfung für Elemente liefert, die die
        // bezüglich ihrer Redundanz zu untersuchenden
        // oder auf dem verbindenden Pfad dazwischenliegenden Elemente betrifft, müssen beseitigt
        // werden.
        // 2.) Jedes der Elemente, die am Ende als redundant ausgegeben werden sollen (in unserem
        // speziellen Fall sind das AWB) darf nur einen
        // einzigen Pfad zu der anderen Elementart haben (in unserem Fall zu Aufgaben).
        // 3.) Für alle auf dem zu untersuchenden Gesamtpfad liegenden Elemente, die in
        // Teil-Von-Beziehung stehen können, dürfen nur die
        // Blattelemente mit jeweils anderen Elementen auf diesem Pfad verbunden sein.

        // alle allgemeinen Konsistenzfehler suchen, die bei Klassen auftreten, die für die
        // Redundanzanalyse relevant sind
        ConsistencyDefinition consistencyDefinition = new ConsistencyDefinition(Tool3lgmConstants.getResString("redundancy_analysis"));
        for (RedundancyAnalysisResult result : resultList) {
            MetaPath metaPath = result.getMetaPath();
            for (int i = 0; i < metaPath.countPathes(); i++) {
                Class<? extends Kante>[] internalMetaPath = metaPath.getEdgeClasses(i);
                for (int j = 0; j < internalMetaPath.length; j++) {
                    Class<? extends Kante> edgeClass = internalMetaPath[j];
                    Integer minStartToEndCard = new Integer(Kante.getMinStartToEndCardinality(edgeClass));
                    Integer maxStartToEndCard = new Integer(Kante.getMaxStartToEndCardinality(edgeClass));
                    Integer minEndToStartCard = new Integer(Kante.getMinEndToStartCardinality(edgeClass));
                    Integer maxEndToStartCard = new Integer(Kante.getMaxEndToStartCardinality(edgeClass));
                    // um korrekte Redundanzanalysen zu liefern, darf einer
                    // Anwendungsbausteinkonfiguration immer nur ein
                    // Anwendungsbaustein zugeordnet sein. Sind es mehr als einer, bedeutet das,
                    // dass die alle in einer
                    // KonfigurationAWB gleichzeitig gebraucht werden. Die Redundanzanalyse würde
                    // aber davon ausgehen, dass
                    // man jeweils nur einen in einer Konfiguration braucht.
                    if (edgeClass == AwbAwbkVerbindung.class) {
                        if (Anwendungsbaustein.class.isAssignableFrom(Kante.getStartClass(edgeClass))) {
                            if (minEndToStartCard.intValue() > 0) {
                                minEndToStartCard = ModelConstants.ONE;
                            }
                            maxEndToStartCard = ModelConstants.ONE;
                        } else {
                            if (minStartToEndCard.intValue() > 0) {
                                minStartToEndCard = ModelConstants.ONE;
                            }
                            maxStartToEndCard = ModelConstants.ONE;
                        }
                    }
                    consistencyDefinition.add(edgeClass, minStartToEndCard, maxStartToEndCard, minEndToStartCard, maxEndToStartCard);
                }
            }
        }
        // eigentlich sollte hier immer schon dieselbe GDCollection selektiert sein, aber zur
        // Sicherheit wird mal dahin gewechselt
        if (Static.getSelectedGDCollection() != gdcoll) {
            Static.setSelectedDoc(gdcoll.getSelectedDoc(), true);
        }
        ConsistencyChecker consistencyChecker = Static.getTool().getConsistencyChecker();
        consistencyChecker.setConsistencyDefinition(consistencyDefinition);
        List<AbstractError> errors = consistencyChecker.getCardinalityInconsistencies();
        // wenn es relevante Fehler gibt
        if (errors.size() > 0) {
            // Custom button xmlText
            Object[] options = {
                    Tool3lgmConstants.getResString("ana_fr_resolve_errors"),
                    Tool3lgmConstants.getResString("ana_fr_ignore_errors"),
                    Tool3lgmConstants.getResString("cancel")
            };
            int answer = JOptionPane.showOptionDialog(Static.getMainFrame(), Tool3lgmConstants.getResString("ana_fr_error_message"), Tool3lgmConstants.getResString("ana_fr_error_message_title"), JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            if (answer == JOptionPane.YES_OPTION) {
                ((StaticAction) ActionLibrary.AnalysisActions.ACTIVATE_CONSISTENCY_CHECK).setSelected(true);
                Static.getTool().setCheckConsistencyState(true);
                return;
            } else if (answer == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        // Redundanzberechnung starten
        rc.redundancyThread = new RedundancyThread(resultList);

        rc.redundancyThread.setPriority(Thread.MIN_PRIORITY);

        ProgressDialog pd = new ProgressDialog(Static.getMainFrame(), Tool3lgmConstants.getResString("ana_fr_wait_message_title"), true, rc.redundancyThread);
        pd.addWindowListener(rc);
        pd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pd.setStatusLabelText(Tool3lgmConstants.getResString("message_wait"));

        rc.redundancyThread.start();

    }

    /**
     * Gibt das Ergebnis der Redundanzanalyse in einem Ausgabedialog aus.
     *
     * @param doc Modell für das die XMLAnalyse durchgeführt wurde
     * @param redundancyAnalysisResults
     */
    private static void showResult(final List<RedundancyAnalysisResult> redundancyAnalysisResults) {

        if (redundancyAnalysisResults == null || redundancyAnalysisResults.size() == 0) {
            return;
        }

        RedundancyAnalysisResult res = redundancyAnalysisResults.get(0);

        // Analysiertes Gesamtmodell
        GDCollection col = res.getGDCollection();

        OutputDialog outputDialog;

        // Titel des Dialoges
        String title = Tool3lgmConstants.getResString("redundancy_analysis") + " - " + col.getName();

        outputDialog = new OutputDialog(Static.getMainFrame(), title);

        // Titel, Modell, Teilmodell
        outputDialog.appendln(Tool3lgmConstants.getResString("redundancy_analysis"), true);
        outputDialog.appendln(Tool3lgmConstants.getResString("model") + "\t\t: " + col.getName());

        // Letzte Änderung
        File file = col.getFile();
        Calendar cal = Calendar.getInstance();
        if (file == null || !file.canRead()) {
            cal.setTimeInMillis(System.currentTimeMillis());
        } else {
            cal.setTimeInMillis(file.lastModified());
        }
        DateFormat formater = new SimpleDateFormat();
        outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_last_change") + "\t: " + formater.format(cal.getTime()));

        outputDialog.appendln("\n\n");

        // alle Results hintereinander ausgeben
        for (RedundancyAnalysisResult result : redundancyAnalysisResults) {

            outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_option") + ": " + result.getAnalyseOptionString(), true);
            outputDialog.appendln();

            // Nicht verzichtbare Anwendungssysteme
            outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_not_dispensible_title") + ":", true);
            Set<ModelElement> al = new AlphabeticalSet<>(result.exclusiveAWB);
            al.addAll(result.moreNeededAWB);
            if (al.size() == 0) {
                outputDialog.appendln(Tool3lgmConstants.getResString("none"));
            } else {
                int k = 1;
                for (ModelElement me : al) {
                    outputDialog.appendln(k++ + ".)\t" + getElementName(me));
                }
            }

            // Äquivalenzklassen
            outputDialog.appendln("\n\n");
            // outputDialog.appendln(
            // "Äquivalente Anwendungssysteme, deren Funktionalität man genau 1 mal braucht:"
            // , true);
            outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_equivalence_class_title") + ":", true);
            if (result.equalsSets.size() == 0) {
                outputDialog.appendln(Tool3lgmConstants.getResString("none"));
            } else {
                for (int i = 0; i < result.equalsSets.size(); i++) {
                    AlphabeticalSet<ModelElement> as = result.equalsSets.get(i);
                    outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_equivalence_class") + " " + (i + 1) + ":");
                    int j = 1;
                    for (ModelElement me : as) {
                        outputDialog.appendln(j++ + ".)\t" + getElementName(me));
                    }
                    outputDialog.appendln();
                }
            }

            // Überflüssige
            outputDialog.appendln("\n");
            outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_superfluous") + ":", true);
            al.clear();
            al.addAll(result.uselessAWB);
            al.addAll(result.moreUselessAWB);
            if (al.size() == 0) {
                outputDialog.appendln(Tool3lgmConstants.getResString("none"));
            } else {
                int k = 1;
                for (ModelElement me : al) {
                    outputDialog.appendln(k++ + ".)\t" + getElementName(me) + " \n\t\t" + result.uselessToNeeded.get(me).toString().replace("-\n", "").replace('\n', ' '));
                }
            }

            // REDUNDANZRATE (ohne die nicht mit Aufgaben verbundenen AWB)
            // Anzahl der AWB in jeder der minimalen Mengen
            int minimalSetSize = result.equalsSets.size() + result.exclusiveAWB.size() + result.moreNeededAWB.size();

            // Anzahl aller AWB
            int fullAWBCount = result.exclusiveAWB.size() + result.moreNeededAWB.size() + result.uselessAWB.size() + result.moreUselessAWB.size() /*
                                                                                                                                                   * +
                                                                                                                                                   * result
                                                                                                                                                   * .
                                                                                                                                                   * notSupportingAWB
                                                                                                                                                   * .
                                                                                                                                                   * size
                                                                                                                                                   * (
                                                                                                                                                   * )
                                                                                                                                                   */;
            for (AlphabeticalSet<ModelElement> as : result.equalsSets) {
                fullAWBCount += as.size();
            }

            outputDialog.appendln("\n\n");
            outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_redundancy_rate") + ":", true);

            NumberFormat nf = NumberFormat.getPercentInstance();
            outputDialog.append(Tool3lgmConstants.getResString("ana_fr_rr") + " = " + fullAWBCount + " - " + minimalSetSize + " / " + fullAWBCount + " = ");
            if (fullAWBCount > 0) {
                outputDialog.appendln(nf.format((float) (fullAWBCount - minimalSetSize) / (float) fullAWBCount).toString());
            } else {
                outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_not_defined"));
            }

            // nicht verbundene
            outputDialog.appendln("\n\n");
            outputDialog.appendln(Tool3lgmConstants.getResString("ana_fr_not_conntected") + ":", true);
            if (result.notSupportingAWB.size() == 0) {
                outputDialog.appendln(Tool3lgmConstants.getResString("none"));
            } else {
                int k = 1;
                for (ModelElement me : result.notSupportingAWB) {
                    outputDialog.appendln(k++ + ".)\t" + getElementName(me));
                }
            }

            outputDialog.appendln("\n\n");

            if (result != redundancyAnalysisResults.get(redundancyAnalysisResults.size() - 1)) {
                outputDialog.appendln("########################################################################################\n");
            }

        }

        outputDialog.setVisible(true);
        outputDialog.setLocationRelativeTo(Static.getMainFrame());

    }

    /**
     * @param me
     * @return
     */
    private static String getElementName(final ModelElement me) {
        String retVal = me.toString().replace("-\n", "").replace('\n', ' ');
        // Hänge an AWB ihre SWP
        if (me instanceof RechAnwendungsbaustein) {
            StringBuilder sb = new StringBuilder(retVal);
            sb.append(" (");
            for (ModelElement awp : me.getConnectedElements(Anwendungsprogramm.class)) {
                for (ModelElement swp : awp.getConnectedElements(Softwareprodukt.class)) {
                    sb.append(getElementName(swp));
                    sb.append(", ");
                }
            }
            int l = sb.length();
            if (sb.charAt(l - 2) == ',' && sb.charAt(l - 1) == ' ') {
                sb.setLength(l - 2);
            }
            sb.append(")");
            return sb.toString();
        }
        return retVal;
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        redundancyThread.interrupt();
    }

    /**
     * Gibt das Datenfeld aus.
     *
     * @param matrix
     */
    public static void printData(final int[][] matrix) {
        printData(matrix, false);
    }

    /**
     * Gibt das Datenfeld aus. Optinal als Java-Code.
     *
     * @param matrix
     * @param asJava
     */
    public static void printData(final int[][] matrix, final boolean asJava) {
        if (!asJava) {
            if (matrix.length == 0) {
                System.out.println("[]");
                return;
            }
            StringBuilder sb = new StringBuilder("  ");
            sb.append(" ");
            for (int awb = 1; awb <= matrix.length; awb++) {
                sb.append(awb % 10);
                sb.append(" ");
            }
            System.out.println(sb);
            for (int auf = 0; auf < matrix[0].length; auf++) {
                sb.setLength(0);
                sb.append((auf + 1) % 10);
                sb.append("[");
                for (int awb = 0; awb < matrix.length; awb++) {
                    sb.append(matrix[awb][auf] == 1 ? " X" : " .");
                }
                sb.append("]");
                System.out.println(sb);
            }
        } else {
            System.out.print("public static final int[][] data = {");
            for (int x = 0; x < matrix.length; x++) {
                StringBuilder sb = new StringBuilder("{");
                if (matrix[0] != null) {
                    for (int y = 0; y < matrix[0].length; y++) {
                        sb.append(matrix[x][y]);
                        sb.append(",");
                    }
                }
                sb.insert(sb.length() - 1, "}");
                System.out.println(sb);
            }
            System.out.println("};");
        }
    }

    /**
     * Führt die Redundanzberechnug aus. Man kann diese Abbrechen.
     *
     * @author AXS
     */
    private static final class RedundancyThread extends Thread {

        private DecisionTree decisionTree10;

        // aktueller Wert von UserProperties.isSearchParents
        boolean searchParents;

        /**
         * Liste mit RedundancyAnalysisResult, die gefüllt werden sollen
         */
        private final List<RedundancyAnalysisResult> resultList;

        /**
         * @param resultsToFill Liste mit RedundancyAnalysisResult, die gefüllt werden sollen
         */
        public RedundancyThread(final List<RedundancyAnalysisResult> resultsToFill) {
            super();
            resultList = resultsToFill;
        }

        @Override
        public void run() {

            // Originalwert von searchParents merken
            searchParents = UserProperties.isSearchParents();
            // übergeordnete Elemente auf jeden Fall mit berücksichtigen
            UserProperties.setSearchParents(true);

            // füllt alle results
            for (int i = 0; i < resultList.size(); i++) {
                // der Konstruktoraufruf füllt die übergebene Liste
                new DecisionTree(resultList.get(i));
            }

            // Originalwert wieder herstellen
            UserProperties.setSearchParents(searchParents);

            RedundancyChecker.showResult(resultList);
        }

        @Override
        public void interrupt() {
            if (decisionTree10 != null) {
                decisionTree10.stop();
            }
            super.interrupt();
            // Originalwert wieder herstellen
            UserProperties.setSearchParents(searchParents);
        }
    }

}
