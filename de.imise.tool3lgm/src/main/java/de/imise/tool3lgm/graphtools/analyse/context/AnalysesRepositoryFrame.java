/*
 * Created on 14.06.2004
 */
package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import de.imise.tool3lgm.Static;
import de.imise.util.Alphabetical;

/**
 * @author Thomas, Sebastian Weber, AXS
 */
public class AnalysesRepositoryFrame extends JFrame {

    /**
     * Tabelle, in der die Analysen angezeigt werden
     */
    static AnalysesRepositoryFrameTable table;

    private static AbstractButton[] buttons = {
            new JButton(AnalysesRepositoryFrameActions.ACTION_START_ANALYSIS), new JButton(AnalysesRepositoryFrameActions.ACTION_RESET_ANALYSIS_RESULT), new JButton(AnalysesRepositoryFrameActions.ACTION_CLOSE_DIALOG),
    };

    /**
     * Lokale Kopie der Analysen aus dem Repository. Die Liste wird für die Tabelle, die die selbe
     * Liste nutzt, alphabetisch sortiert.
     */
    static List<XMLAnalysis> analysen;

    /**
     * Speichert die Analysendatei auf der der Benutzer grade arbeitet, wenn er eine Analysedatei
     * nicht über das Repository geöffnet oder gespeichert hat. Solange man sich nur vom Repository
     * die Analysen geben lässt, bleibt diese Variable null.<br>
     * Diese Variable wird nur gebraucht, um immer in das zuletzt vom Benutzer ausgewählte
     * Verzeichnis wechseln zu können, damit er es nicht immer wieder neu asuwählen muss.
     */
    static File analyseFile = null;

    /** Instanz dieser Klasse. */
    static AnalysesRepositoryFrame dialog = new AnalysesRepositoryFrame();

    /**
     * Wenn sich die Analysen geändert haben, muss beim Schließen des Frames gefragt werden, ob sie
     * als Repository gespeichert werden sollen.
     */
    static boolean analysisChanged = false;

    /**
     * Fügt die übergebene XMLAnalyse in die Liste der Analysen ein, wenn sie nicht <code>null</code> ist und noch nicht in der Liste vorkommt.
     *
     * @param toadd
     * @param ignoreDuplicates wenn <code>true</code> werden identische Analysen auch mehrfach
     *            eingefügt, sonst nicht
     * @return
     */
    static boolean addAnalysis(final XMLAnalysis toadd, final boolean ignoreDuplicates) {
        if (toadd == null || !ignoreDuplicates && analysen.contains(toadd)) {
            return false;
        }
        Alphabetical.insert(analysen, toadd);
        return true;
    }

    /**
     * Prüft den enabled-Status aller Buttons
     */
    public static final void refreshActionStates() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(buttons[i].getAction().isEnabled());
        }
        // Das hier funktioniert nur solange richtig, wie es in Menüs keine Untermenüs mit zu
        // aktualisierenden
        // Aktionen gibt
        JMenuBar menuBar = AnalysesRepositoryFrame.dialog.getJMenuBar();
        for (int i = 0; i < menuBar.getComponentCount(); i++) {
            JMenu menu = (JMenu) menuBar.getComponent(i);
            if (menu.getAction() != null) {
                menu.setEnabled(menu.getAction().isEnabled());
            }
            for (int j = 0; j < menu.getItemCount(); j++) {
                // Separatoren liefern hier null
                if (menu.getItem(j) == null) {
                    continue;
                }
                JMenuItem item = menu.getItem(j);
                if (item.getAction() != null) {
                    item.setEnabled(item.getAction().isEnabled());
                }
            }
        }
    }

    /**
     * Setzt die übergeben ArrayList als die Analysenliste dieses Dialoges und sortiert sie für die
     * Tabelle.
     */
    static void setAnalyses(final List<XMLAnalysis> analysen) {
        Alphabetical.sort(analysen);
        for (int i = analysen.size() - 1; i >= 0; i--) {
            XMLAnalysis analyse = analysen.get(i);
            if (analyse.startClasses.isEmpty()) {
                analysen.remove(i);
            }
        }
        AnalysesRepositoryFrame.analysen = analysen;
    }

    /**
     * Zeigt den AnalysesRepositoryFrame an.
     */
    public static void showDialog() {
        if (table != null) {
            table.update();
        }
        dialog.setVisible(true);
    }

    /**
     * Konstruktor. Zugriff auf diese Klasse ist über die Methode showDialog möglich.
     *
     * @param t die Tool3lgm Klasse, in der dieser Dialog angezeigt wird.
     */
    private AnalysesRepositoryFrame() {
        super(getResString("repository"));
        setIconImage(Static.getMainFrame().getIconImage());

        JMenu menuFile = new JMenu(getResString("file"));
        menuFile.add(new JMenuItem(AnalysesRepositoryFrameActions.ACTION_LOAD_STANDARD_REPOSITORY));
        menuFile.add(new JMenuItem(AnalysesRepositoryFrameActions.ACTION_SAVE_REPOSITORY));
        menuFile.add(new JSeparator());
        menuFile.add(new JMenuItem(AnalysesRepositoryFrameActions.ACTION_IMPORT_ANALYSIS));
        menuFile.add(new JMenuItem(AnalysesRepositoryFrameActions.ACTION_EXPORT_ANALYSIS));
        menuFile.add(new JSeparator());
        menuFile.add(new JMenuItem(AnalysesRepositoryFrameActions.ACTION_CLOSE_DIALOG));
        JMenu menuAnalysis = new JMenu(getResString("analysis"));
        menuAnalysis.add(new JMenuItem(AnalysesRepositoryFrameActions.createACTION_NEW_ANALYSIS(Static.getSelectedMetaModelContext())));
        menuAnalysis.add(new JMenuItem(AnalysesRepositoryFrameActions.ACTION_DELETE_ANALYSIS));
        menuAnalysis.add(new JSeparator());
        menuAnalysis.add(new JMenuItem(AnalysesRepositoryFrameActions.ACTION_ANALYSIS_EDITOR));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuAnalysis);
        setJMenuBar(menuBar);

        setAnalyses(AnalysesRepository.getXMLAnalyses());
        table = new AnalysesRepositoryFrameTable();
        JScrollPane tableScrollPane = new JScrollPane(table);

        // das Buttonpanel zusammenbauen
        JPanel buttonPanel = new JPanel();
        for (int i = 0; i < buttons.length; i++) {
            buttonPanel.add(buttons[i]);
        }

        // Tabelle und Buttonpanel ins ContentPane einfügen
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(mainPanel);
        pack();

    }

    @Override
    public void dispose() {
        super.dispose();
        // beim Schließen immer die Analysen wieder auf die des Repositories setzen
        setAnalyses(AnalysesRepository.getXMLAnalyses());
        analysisChanged = false;
    }
}
