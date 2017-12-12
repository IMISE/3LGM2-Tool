/*
 * Created on 04.10.2007
 */
package de.imise.tool3lgm.graphtools.analyse.redundancy;

import static de.imise.tool3lgm.Static.getTool;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayablePluralName;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.analyse.redundancy.RedundancyAnalysisDefinitions.SingleRedundancyAnalysisDefinition;
import de.imise.tool3lgm.graphtools.consistency.CardinalityDefinition;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyDefinition;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractError;
import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.collections.AlphabeticalSet;
import de.imise.util.swing.dialog.MultipleOptionPane;
import de.imise.util.swing.dialog.OutputDialog;
import de.imise.util.swing.dialog.ProgressDialog;

/**
 * Stellt Funktionen bereit, die die funktionale Redundanz von Modellen untersuchen.
 *
 * @author AXS
 */
public class RedundancyAnalysis extends WindowAdapter {

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
    private RedundancyAnalysis() {
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
        RedundancyAnalysis rc = new RedundancyAnalysis();

        // Liste in die alle zu füllenden RedundancyAnalysisResult kommen
        RedundancyAnalysisResult result = null;

        do {
            String message = "";

            // wenn die Option "beim Suchen übergeordnete Elemente berücksichtigen" ausgeschaltet ist
            if (!UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS)) {
                message += getResString("ana_fr_search_parents_option_message1");
                message += getResString("itv-bezogene_Opt") + " -> " + getResString("consider_parents") + getResString("ana_fr_search_parents_option_message2") + "\n\n";
            }

            message += getResString("ana_fr_option_question");

            String con = " " + getResString("ana_fr_concerning") + " ";

            AnalysisDefinition analysisDefinition = ModelConstants.getAnalysisDefinition();
            RedundancyAnalysisDefinitions redundancyAnalysisDefinitions = analysisDefinition.getRedundancyAnalysisDefinitions();
            int analyseCount = redundancyAnalysisDefinitions.size();
            String[] options = new String[analyseCount + 1];
            for (int i = 0; i < analyseCount; i++) {
                SingleRedundancyAnalysisDefinition singleRedundancyAnalysisDefinition = redundancyAnalysisDefinitions.get(i);
                MetaPath metaPath = singleRedundancyAnalysisDefinition.getMetaPath();
                Class<? extends ModelElement> startClass = metaPath.getStartClass();
                Class<? extends ModelElement> endClass = metaPath.getEndClass();
                options[i] = getDisplayablePluralName(startClass) + con + getDisplayablePluralName(endClass);
            }
            options[analyseCount] = getResString("ana_fr_self_defined_analysis");

            //immer die erste Analyse bereits vorauswählen (wenn für ein Metamodell mal gar keine eigenen Analysen definiert werden, dann
            //ist die einzig verfügbare immer die selbsdefinierte Analyse. Und diese ist dann auf jeden Fall gelich ausgewählt.
            boolean[] preseleced = new boolean[options.length];
            preseleced[0] = true;

            // null wenn Abrechen gedrückt wurde, sonst ein gültiges Boolean-Array
            selectedOptions = MultipleOptionPane.showCheckBoxOptionDialog(Static.getMainFrame(), getResString("redundancy_analysis"), message, options, preseleced, true);

            // wenn abgebrochen werden soll
            if (selectedOptions == null) {
                return;
            }

            for (int i = 0; i < selectedOptions.length - 1; i++) {
                if (selectedOptions[i] != null) {
                    SingleRedundancyAnalysisDefinition definition = redundancyAnalysisDefinitions.get(i);
                    result = new RedundancyAnalysisResult(gdcoll, definition, options[i]);
                    break;
                }
            }

            // selbst definierte XMLAnalyse
            if (result == null && selectedOptions[selectedOptions.length - 1] != null) {
                MetaPathSelector mps = MetaPathSelector.showDialog(getResString("ana_fr_class1_label"), getResString("ana_fr_class2_label"), getResString("metapath"));
                if (mps.isValid()) {
                    Class<? extends ModelElement> c1 = mps.getSelectedClass1();
                    Class<? extends ModelElement> c2 = mps.getSelectedClass2();
                    MetaPath mp = mps.getSelectedMetaPath();
                    MetaPath metaPath = new MetaPath(c1, c2, mp.getEdgeClasses());
                    SingleRedundancyAnalysisDefinition definition = new RedundancyAnalysisDefinitions().add(metaPath);
                    String resultName = c2.getSimpleName() + " " + mp.toString();
                    result = new RedundancyAnalysisResult(gdcoll, definition, resultName);
                }
            }

            // wenn im Auswahl-Dialog gar nichts angekreuzt war -> Dialog
            // nochmal zeigen
        } while (result == null);

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
        // eigentlich sollte hier immer schon dieselbe GDCollection selektiert sein, aber zur
        // Sicherheit wird mal dahin gewechselt
        if (Static.getSelectedGDCollection() != gdcoll) {
            Static.setSelectedDoc(gdcoll.getSelectedDoc(), true);
        }
        ConsistencyChecker consistencyChecker = getTool().getConsistencyChecker();
        consistencyChecker.resetConsistencyDefinition();
        ConsistencyDefinition consistencyDefinition = consistencyChecker.getConsistencyDefinition();
        CardinalityDefinition cardinalityDefinition = consistencyDefinition.getCardinalityDefinition();

        SingleRedundancyAnalysisDefinition definition = result.getDefinition();
        MetaPath metaPath = definition.getMetaPath();
        for (int i = 0; i < metaPath.countPathes(); i++) {
            Class<? extends Edge>[] internalMetaPath = metaPath.getEdgeClasses(i);
            for (int j = 0; j < internalMetaPath.length; j++) {
                Class<? extends Edge> edgeClass = internalMetaPath[j];
                //jetzt die Kardinalitätsvorgaben der gewählten Analyse in die Kardinalitäten der Konsitenzprüfung übertragen
                cardinalityDefinition.setNewForwardCardinality(edgeClass, definition.getNewForwardCardinality(edgeClass));
                cardinalityDefinition.setNewBackwardCardinality(edgeClass, definition.getNewBackwardCardinality(edgeClass));
            }
        }
        List<AbstractError> errors = consistencyChecker.getCardinalityInconsistencies();
        // wenn es relevante Fehler gibt
        if (errors.size() > 0) {
            // Custom button xmlText
            Object[] options = {
                    getResString("ana_fr_resolve_errors"),
                    getResString("ana_fr_ignore_errors"),
                    getResString("cancel")
            };
            int answer = JOptionPane.showOptionDialog(Static.getMainFrame(), getResString("ana_fr_error_message"), getResString("ana_fr_error_message_title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            if (answer == JOptionPane.YES_OPTION) {
                UserProperties.set(BooleanProperty.OPTION_CHECK_CONSISTENCY, true);
                Static.getTool().setCheckConsistencyState(true);
                return;
            } else if (answer == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        // Redundanzberechnung starten
        rc.redundancyThread = new RedundancyThread(result);
        rc.redundancyThread.setPriority(Thread.MIN_PRIORITY);
        ProgressDialog pd = new ProgressDialog(Static.getMainFrame(), getResString("ana_fr_wait_message_title"), true, rc.redundancyThread);
        pd.addWindowListener(rc);
        pd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pd.setStatusLabelText(getResString("message_wait"));
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
        String title = getResString("redundancy_analysis") + " - " + col.getName();
        outputDialog = new OutputDialog(Static.getMainFrame(), title);

        // Titel, Modell, Teilmodell
        outputDialog.appendln(getResString("redundancy_analysis"), true);
        outputDialog.appendln(getResString("model") + "\t\t: " + col.getName());

        // Letzte Änderung
        File file = col.getFile();
        Calendar cal = Calendar.getInstance();
        if (file == null || !file.canRead()) {
            cal.setTimeInMillis(System.currentTimeMillis());
        } else {
            cal.setTimeInMillis(file.lastModified());
        }
        DateFormat formater = new SimpleDateFormat();
        outputDialog.appendln(getResString("ana_fr_last_change") + "\t: " + formater.format(cal.getTime()));
        outputDialog.appendln("\n\n");

        // alle Results hintereinander ausgeben
        for (RedundancyAnalysisResult result : redundancyAnalysisResults) {
            SingleRedundancyAnalysisDefinition singleRedundancyAnalysisDefinition = result.getDefinition();
            outputDialog.appendln(getResString("ana_fr_option") + ": " + result.getAnalyseOptionString(), true);
            outputDialog.appendln();

            // Nicht verzichtbare Anwendungssysteme
            outputDialog.appendln(getResString("ana_fr_not_dispensible_title") + ":", true);
            Set<ModelElement> al = new AlphabeticalSet<>(result.exclusiveAWB);
            al.addAll(result.moreNeededAWB);
            if (al.size() == 0) {
                outputDialog.appendln(getResString("none"));
            } else {
                int k = 1;
                for (ModelElement me : al) {
                    outputDialog.appendln(k++ + ".)\t" + getElementName(me, singleRedundancyAnalysisDefinition));
                }
            }

            // Äquivalenzklassen
            outputDialog.appendln("\n\n");
            // outputDialog.appendln(
            // "Äquivalente Anwendungssysteme, deren Funktionalität man genau 1 mal braucht:"
            // , true);
            outputDialog.appendln(getResString("ana_fr_equivalence_class_title") + ":", true);
            if (result.equalsSets.size() == 0) {
                outputDialog.appendln(getResString("none"));
            } else {
                for (int i = 0; i < result.equalsSets.size(); i++) {
                    AlphabeticalSet<ModelElement> as = result.equalsSets.get(i);
                    outputDialog.appendln(getResString("ana_fr_equivalence_class") + " " + (i + 1) + ":");
                    int j = 1;
                    for (ModelElement me : as) {
                        outputDialog.appendln(j++ + ".)\t" + getElementName(me, singleRedundancyAnalysisDefinition));
                    }
                    outputDialog.appendln();
                }
            }

            // Überflüssige
            outputDialog.appendln("\n");
            outputDialog.appendln(getResString("ana_fr_superfluous") + ":", true);
            al.clear();
            al.addAll(result.uselessAWB);
            al.addAll(result.moreUselessAWB);
            if (al.size() == 0) {
                outputDialog.appendln(getResString("none"));
            } else {
                int k = 1;
                for (ModelElement me : al) {
                    outputDialog.appendln(k++ + ".)\t" + getElementName(me, singleRedundancyAnalysisDefinition) + " \n\t\t" + result.uselessToNeeded.get(me).toString().replace("-\n", "").replace('\n', ' '));
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
            outputDialog.appendln(getResString("ana_fr_redundancy_rate") + ":", true);

            NumberFormat nf = NumberFormat.getPercentInstance();
            outputDialog.append(getResString("ana_fr_rr") + " = " + fullAWBCount + " - " + minimalSetSize + " / " + fullAWBCount + " = ");
            if (fullAWBCount > 0) {
                outputDialog.appendln(nf.format((float) (fullAWBCount - minimalSetSize) / (float) fullAWBCount).toString());
            } else {
                outputDialog.appendln(getResString("ana_fr_not_defined"));
            }

            // nicht verbundene
            outputDialog.appendln("\n\n");
            outputDialog.appendln(getResString("ana_fr_not_conntected") + ":", true);
            if (result.notSupportingAWB.size() == 0) {
                outputDialog.appendln(getResString("none"));
            } else {
                int k = 1;
                for (ModelElement me : result.notSupportingAWB) {
                    outputDialog.appendln(k++ + ".)\t" + getElementName(me, singleRedundancyAnalysisDefinition));
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
     * @param analysisDefinition
     * @return
     */
    private static String getElementName(final ModelElement me, final SingleRedundancyAnalysisDefinition analysisDefinition) {
        String retVal = me.toString().replace("-\n", "").replace('\n', ' ');
        MetaPath expandedNamePath = analysisDefinition.getExpandedNamePath(me.getClass());
        if (expandedNamePath != null) {
            StringBuilder sb = new StringBuilder(retVal);
            sb.append(" (");
            for (ModelElement connected : PathFinder.getConnectedElements(me, expandedNamePath)) {
                sb.append(getElementName(connected, analysisDefinition));
                sb.append(", ");
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

        /**
         * @param resultsToFill Liste mit RedundancyAnalysisResult, die gefüllt werden sollen
         */
        public RedundancyThread(final RedundancyAnalysisResult resultToFill) {
            this(ImmutableList.of(resultToFill));
        }

        @Override
        public void run() {
            // Originalwert von searchParents merken
            searchParents = UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS);
            // übergeordnete Elemente auf jeden Fall mit berücksichtigen
            UserProperties.set(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS, true);
            // füllt alle results
            for (int i = 0; i < resultList.size(); i++) {
                // der Konstruktoraufruf füllt die übergebene Liste
                new DecisionTree(resultList.get(i));
            }
            // Originalwert wieder herstellen
            UserProperties.set(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS, searchParents);
            RedundancyAnalysis.showResult(resultList);
        }

        @Override
        public void interrupt() {
            if (decisionTree10 != null) {
                decisionTree10.stop();
            }
            super.interrupt();
            // Originalwert wieder herstellen
            UserProperties.set(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS, searchParents);
        }
    }

}
