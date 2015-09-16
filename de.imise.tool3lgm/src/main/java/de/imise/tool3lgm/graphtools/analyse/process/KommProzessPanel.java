/*
 * Created on 09.02.2004 To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.analyse.process;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Konfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.elements.node.Prozess;
import de.imise.tool3lgm.graphtools.view.container.AdditionalLabelTextGenerator;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author AXS Dieses Panel ist das KommunikationsprozessPanel im ElementPropertyDialog eines
 *         (Geschäfts-)Prozesses
 */
public class KommProzessPanel extends ElementDialogPanel {

    protected Prozess prozess;
    protected NodeContainer prozessC;

    private final MyTable table;
    private final DefaultTableModel tmodel;
    private final JScrollPane tableScrollPane;

    private static boolean showTeilmodelle = false;
    private JCheckBox showTeilmodelleCheck;
    private static boolean enumerateSchnittstellen = false;
    private JCheckBox enumerateSchnittstellenCheck;
    private static boolean enumerateKanten = true;
    private JCheckBox enumerateKantenCheck;
    private static boolean writeObjekttypen = false;
    private JCheckBox writeObjekttypenCheck;
    private static boolean showKonfs = false;
    private JCheckBox showKonfsCheck;
    private static boolean highlightAllCommElements = false;
    private JCheckBox highlightAllCommElementsCheck;

    private boolean setColumnWidth = true;
    List<LGMProzessStep> allSteps;

    // Zeilennummern in der Tabelle
    private static final int COLUMN_COUNT = 11;
    private static final int POSITION = 0;
    private static final int BEMERKUNGEN = 1;
    private static final int START_AUFGABE = 2;
    private static final int END_AUFGABE = 3;
    private static final int OBJEKTTYP = 4;
    private static final int START_BAUSTEINE = 5;
    private static final int START_ORGEINHEIT = 6;
    private static final int END_BAUSTEINE = 7;
    private static final int END_ORGEINHEIT = 8;
    private static final int SCHNITTSTELLEN = 9;
    private static final int MEDIUM_BREAKS = 10;

    /**
     * COMMENTME
     */
    private final JPanel checkPanel;

    /**
     * Das {@link GraphDocument}, in dem die Teilprozessschritte visualisiert werden sollen.
     */
    @SuppressWarnings("unused")
    private final GraphDocument selectedDoc;

    /**
     * @param pd
     */
    public KommProzessPanel(final ElementPropertyDialog pd) {
        super(pd);

        // selectedDoc = doc.getCollection().getActiveGraphDocument();
        selectedDoc = doc.getCollection().getSelectedDoc();

        prozess = (Prozess) getModelElement();
        prozessC = (NodeContainer) prozess.getContainer(doc);

        // das gesamte Panel hat ein BorderLayout
        setLayout(new BorderLayout());

        String[] colHeads = new String[COLUMN_COUNT];
        colHeads[POSITION] = Tool3lgmConstants.getResString("position");
        colHeads[START_AUFGABE] = Tool3lgmConstants.getResString("start_aufg");
        colHeads[END_AUFGABE] = Tool3lgmConstants.getResString("end_aufg");
        colHeads[OBJEKTTYP] = Tool3lgmConstants.getResString("Objekttyp");
        colHeads[START_BAUSTEINE] = Tool3lgmConstants.getResString("start_bausteine");
        colHeads[START_ORGEINHEIT] = Tool3lgmConstants.getResString("start_orgeinh");
        colHeads[END_BAUSTEINE] = Tool3lgmConstants.getResString("end_bausteine");
        colHeads[END_ORGEINHEIT] = Tool3lgmConstants.getResString("end_orgeinh");
        colHeads[SCHNITTSTELLEN] = Tool3lgmConstants.getResString("schnittstellen");
        colHeads[MEDIUM_BREAKS] = Tool3lgmConstants.getResString("medium_breaks");
        colHeads[BEMERKUNGEN] = Tool3lgmConstants.getResString("bemerk");

        String[][] contents = new String[0][0];
        tmodel = new DefaultTableModel(contents, colHeads);
        table = new MyTable(tmodel, this);

        table.getColumnModel().getColumn(0).setPreferredWidth(table.getTableHeader().getFontMetrics(table.getTableHeader().getFont()).stringWidth(colHeads[0]));
        tableScrollPane = new JScrollPane(table);

        /*
         * Start: Actions der CheckBoxes erstellen ...
         */
        try {
            LGMAction showTeilmodelleAction = getShowTeilModelleAction(this);
            LGMAction enumerateSchnittstellenAction = getRefreshHighlightsAndSpecialInfoAction(this, Tool3lgmConstants.getResString("schnitt_nummern"));
            LGMAction enumerateKantenAction = getRefreshHighlightsAndSpecialInfoAction(this, Tool3lgmConstants.getResString("kanten_nummern"));
            LGMAction writeObjekttypenAction = getRefreshHighlightsAndSpecialInfoAction(this, Tool3lgmConstants.getResString("Objekttyp_p"));
            LGMAction showKonfsAction = getRefreshHighlightsAndSpecialInfoAction(this, Tool3lgmConstants.getResString("Anwendungsbaustein_p"));
            LGMAction highlightAllCommElementsAction = getRefreshHighlightsAndSpecialInfoAction(this, Tool3lgmConstants.getResString("alles_hervorheben"));
            showTeilmodelleCheck = new JCheckBox();
            showTeilmodelleCheck.setSelected(showTeilmodelle);
            showTeilmodelleCheck.setAction(showTeilmodelleAction);

            enumerateSchnittstellenCheck = new JCheckBox();
            enumerateSchnittstellenCheck.setSelected(enumerateSchnittstellen);
            enumerateSchnittstellenCheck.setAction(enumerateSchnittstellenAction);

            enumerateKantenCheck = new JCheckBox();
            enumerateKantenCheck.setSelected(enumerateKanten);
            enumerateKantenCheck.setAction(enumerateKantenAction);

            writeObjekttypenCheck = new JCheckBox();
            writeObjekttypenCheck.setSelected(writeObjekttypen);
            writeObjekttypenCheck.setAction(writeObjekttypenAction);

            showKonfsCheck = new JCheckBox();
            showKonfsCheck.setSelected(showKonfs);
            showKonfsCheck.setAction(showKonfsAction);

            highlightAllCommElementsCheck = new JCheckBox();
            highlightAllCommElementsCheck.setSelected(highlightAllCommElements);
            highlightAllCommElementsCheck.setAction(highlightAllCommElementsAction);
        } catch (ActionNotDefinedForClassException andfce) {
            andfce.printStackTrace();
        }
        /*
         * ... End: Actions der CheckBoxes erstellen
         */

        add(tableScrollPane, BorderLayout.CENTER);
        checkPanel = new JPanel();
        checkPanel.setLayout(new GridLayout(2, 3));
        checkPanel.add(enumerateKantenCheck);
        checkPanel.add(showKonfsCheck);
        checkPanel.add(writeObjekttypenCheck);
        checkPanel.add(enumerateSchnittstellenCheck);
        checkPanel.add(highlightAllCommElementsCheck);
        checkPanel.add(showTeilmodelleCheck);
        JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.add(checkPanel, BorderLayout.WEST);
        add(optionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void update() {
        // System.out.println("update() mit visible==" + isVisible());
        // der boolean muss immer auf false bleiben, da das Panel sonst nicht
        // neu aufgebaut wird bei Änderungen
        setAlreadyInitialized(false);
        // das Panel braucht nur geupdatet zu werden, wenn es sichtbar ist
        if (isVisible()) { // componentShown(new ComponentEvent(this, -1));
            Cursor cursor = Tool3lgm.tool.getCursor();
            Tool3lgm.tool.setCursor(Tool3lgmConstants.getWaitCursor());

            setColumnWidth = true;
            updateTable(true);
            setColumnWidth = false;
            showTeilmodelleCheck.setSelected(showTeilmodelle);
            enumerateSchnittstellenCheck.setSelected(enumerateSchnittstellen);
            enumerateKantenCheck.setSelected(enumerateKanten);
            writeObjekttypenCheck.setSelected(writeObjekttypen);
            showKonfsCheck.setSelected(showKonfs);
            highlightAllCommElementsCheck.setSelected(highlightAllCommElements);

            Tool3lgm.tool.setCursor(cursor);
        }
    }

    /**
     * COMMENTME
     */
    @SuppressWarnings("unused")
    private LGMKommProzessFinder commFinder = null;

    /**
     * COMMENTME
     */
    private final long lastDocModificationTime = System.currentTimeMillis();

    /**
     * @param checkRowCount
     */
    public void updateTable(final boolean checkRowCount) {
        // System.out.println("updateTable");
        // Aufgaben des Prozesses in holen (NICHT alphabetisch sortiert)
        List<ModelElement> aufgabenListe = prozess.getConnectedElements(Aufgabe.class, doc, null, Doppelkante.ANY, false);

        // Prozess enthält keine Aufgaben und die Zeilenanzahl könnte sich
        // geändert haben
        if (checkRowCount && aufgabenListe.size() == 0) {
            tmodel.setRowCount(0);
            tmodel.fireTableStructureChanged();
            return;
        }
        // alle Kombinationen an Prozesschritten holen
        allSteps = prozess.getKommProzessStepCombinations(aufgabenListe);

        // das hier muss sein!?, sonst kann es ne Exception geben, wenn man in
        // diesem Panel auf Abbrechen geht
        if (allSteps == null) {
            tmodel.setRowCount(0);
            // tmodel.fireTableRowsInserted(0,0);
            for (int i = 1; i < table.getColumnCount(); i++) {
                // Spaltenbreite soll 75Px betragen
                table.getColumnModel().getColumn(i).setPreferredWidth(75);
            }
            return;
        }

        // für alle ProzessSchritte die Kommunikationsschritte setzen
        if (doc.getCollection().getLastModificationTime() != lastDocModificationTime) {
            commFinder = new LGMKommProzessFinder(doc, allSteps);
        }

        int linesCount = allSteps.size();

        if (checkRowCount) {
            tmodel.setRowCount(linesCount);
            tmodel.fireTableRowsInserted(0, linesCount - 1);
        }

        FontMetrics tableFontMetrics;
        tableFontMetrics = table.getFontMetrics(table.getFont());

        int preferredColumnWidth[] = new int[table.getColumnCount()];
        for (int i = 0; i < preferredColumnWidth.length; i++) {
            preferredColumnWidth[i] = 0;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < linesCount; i++) {
            LGMProzessStep step = allSteps.get(i);

            sb.setLength(0);
            // Position
            sb.append("[");
            if (step.getStartPosition() >= 0) {
                sb.append(step.getStartPosition() + 1);
            } else {
                sb.append(' ');
            }
            sb.append("][");
            sb.append(step.getEndPosition() + 1);
            sb.append("]");
            table.setValueAt(sb.toString(), i, POSITION);

            int width;
            // Teilmodelle nicht mit anzeigen -> Namen der ModelElemente
            // anzeigen
            if (!showTeilmodelle) {

                // die Endaufgabe ist in jedem Step gesetzt
                table.setValueAt(step.getEndAufgabe(), i, END_AUFGABE);// Endaufgabe

                ModelElement me = step.getStartAufgabe();
                // Startaufgabe existiert
                if (me != null) { // jetzt nur noch vollständige Schritte
                    // hinzufügen
                    table.setValueAt(me, i, START_AUFGABE); // Startaufgabe
                    // Objekttyp (der kann hier nie null sein, da eine Startaufgabe gefunden wurde)
                    table.setValueAt(step.getObjektTyp(), i, OBJEKTTYP);
                    me = step.getStartAufgabeKonf(); // Startkonfiguration es ex. eine
                                                     // Startkonfiguration
                    if (me != null) {
                        // Startkonfigurationsbausteine
                        table.setValueAt(Tool3lgmConstants.getElementListString(step.getStartAufgabeKonfBausteine(), false, false), i, START_BAUSTEINE);
                        // Startorganisationseinheit
                        table.setValueAt(step.getStartAufOrgKombination().getConnectedElements(Organisationseinheit.class), i, START_ORGEINHEIT);
                    } else {
                        table.setValueAt(null, i, START_BAUSTEINE);
                        table.setValueAt(null, i, START_ORGEINHEIT);
                        table.setValueAt(null, i, MEDIUM_BREAKS);// Medienbrüche
                    }

                    me = step.getEndAufgabeKonf(); // Endkonfiguration
                    if (me != null) {
                        // Endkonfigurationsbausteine
                        table.setValueAt(Tool3lgmConstants.getElementListString(step.getEndAufgabeKonfBausteine(), false, false), i, END_BAUSTEINE);
                        // Endorganisationseinheit
                        table.setValueAt(step.getEndAufOrgKombination().getConnectedElements(Organisationseinheit.class), i, END_ORGEINHEIT);
                    } else {
                        table.setValueAt(null, i, END_BAUSTEINE);
                        table.setValueAt(null, i, END_ORGEINHEIT);
                    }
                    // Schnittstellen
                    table.setValueAt(Tool3lgmConstants.getElementListString(step.getKommProzessSchnittstellen(), false, false), i, SCHNITTSTELLEN);
                }
                // Teilmodelle anzeigen -> Namen der Container anzeigen
            } else {
                // die Endaufgabe muss in jedem Step gesetzt sein
                tmodel.setValueAt(step.getEndAufgabe(), i, END_AUFGABE); // Endaufgabe

                ModelElement me = step.getStartAufgabe();
                if (me != null) {
                    table.setValueAt(me, i, START_AUFGABE); // Startaufgabe
                    table.setValueAt(step.getObjektTyp(), i, OBJEKTTYP); // Objekttyp
                    me = step.getStartAufgabeKonf(); // Startkonfiguration
                    if (me != null) {
                        // Startkonfigurationsbausteine
                        table.setValueAt(((Konfiguration) me).getConnectedContainer(Anwendungsbaustein.class, doc), i, START_BAUSTEINE);
                        // Startorganisationseinheit
                        table.setValueAt(step.getStartAufOrgKombination().getContainer(doc), i, START_ORGEINHEIT);
                    } else {
                        table.setValueAt(null, i, START_BAUSTEINE);
                        table.setValueAt(null, i, START_ORGEINHEIT);
                    }

                    me = step.getEndAufgabeKonf(); // Endkonfiguration
                    if (me != null) {
                        // Endkonfigurationsbausteine
                        table.setValueAt(((Konfiguration) me).getConnectedContainer(Anwendungsbaustein.class, doc), i, END_BAUSTEINE);
                        // Endorganisationseinheit
                        table.setValueAt(step.getEndAufOrgKombination().getContainer(doc), i, END_ORGEINHEIT);
                    } else {
                        table.setValueAt(null, i, END_BAUSTEINE);
                        table.setValueAt(null, i, END_ORGEINHEIT);
                    }
                    // Schnittstellen
                    table.setValueAt(step.getKommProzessSchnittstellen(), i, SCHNITTSTELLEN);
                }
            }
            table.setValueAt(null, i, MEDIUM_BREAKS);// Medienbruchspalte
            // löschen
            // es ex. keine Startaufgabe -> Felder löschen, die keine Werte
            // enthalten sollen (das muss sein, falls noch alte Werte darin
            // stehen)
            if (!step.hasStartAufgabe()) {
                table.setValueAt(null, i, START_AUFGABE);// Startaufgabe
                table.setValueAt(null, i, OBJEKTTYP);// Objekttyp
                table.setValueAt(null, i, START_BAUSTEINE);// Startkonfigurationsbausteine
                table.setValueAt(null, i, START_ORGEINHEIT);// Startorganisationseinheit
                table.setValueAt(null, i, END_BAUSTEINE);// Endkonfigurationsbausteine
                table.setValueAt(null, i, END_ORGEINHEIT);// Endorganisationseinheit
                table.setValueAt(null, i, SCHNITTSTELLEN);// Schnittstellen
                table.setValueAt(Tool3lgmConstants.getErrString("kein_OT"), i, BEMERKUNGEN);
            } else if (!step.hasStartKonfiguration()) {
                if (step.hasEndKonfiguration()) {
                    table.setValueAt(Tool3lgmConstants.getErrString("keine_Start_Konf"), i, BEMERKUNGEN);
                } else {
                    table.setValueAt(Tool3lgmConstants.getErrString("keine_Konfigs"), i, BEMERKUNGEN);
                }
            } else if (!step.hasEndKonfiguration()) {
                tmodel.setValueAt(Tool3lgmConstants.getErrString("keine_End_Konf"), i, BEMERKUNGEN);
            } else if (step.getKommProzessLength() == ShortestCommunicationPathFinder.INFINITY) {
                table.setValueAt(Tool3lgmConstants.getErrString("konfigs_passen_nicht"), i, BEMERKUNGEN);
            } else if (step.getKommProzessLength() == 0) {
                table.setValueAt(Tool3lgmConstants.getErrString("konfigs_teilw_gleich"), i, BEMERKUNGEN);
            } else {
                // Bemerkungsspalte löschen
                table.setValueAt(null, i, BEMERKUNGEN);
                table.setValueAt(new Integer(step.getMediumBreaks()), i, MEDIUM_BREAKS);
            }

            // prüfen ob momentane Spaltenbreite größer ist, als der bisher
            // gemerkte Wert
            if (setColumnWidth) {
                for (int s = 0; s < table.getColumnCount(); s++) {
                    if (table.getValueAt(i, s) != null) {
                        width = tableFontMetrics.stringWidth(table.getValueAt(i, s).toString());
                        if (width > preferredColumnWidth[s]) {
                            preferredColumnWidth[s] = width;
                        }
                    }
                }
            }
        }

        if (setColumnWidth) {
            // Positionsspalte (die erste) wird nicht neu gesetzt sondern nur
            // einmal im Konstruktor
            for (int i = 1; i < table.getColumnCount(); i++) {
                preferredColumnWidth[i] += 20;
                // Spaltenminimum soll 75Px betragen
                if (i != MEDIUM_BREAKS) {
                    if (preferredColumnWidth[i] < 75) {
                        preferredColumnWidth[i] = 75;
                    }
                }
                table.getColumnModel().getColumn(i).setPreferredWidth(preferredColumnWidth[i]);
            }
        }
    }

    /**
     * @param e
     */
    public void valueChanged(final ListSelectionEvent e) {
        // System.out.println("KommProzessPanel valueChanged");
        // wenn sich die Selektion ändert, einfach erst highlight leer machen
        // und dann das neu selektierte hinzufügen

        // doc.deselectAll();
        if (e == null) {
            return;
        }

        if (e.getValueIsAdjusting()) {
            return;
        }

        if (doc == null || !(doc instanceof Szenario) || prozess.getContainer(doc) == null) {
            return;
        }

        lastSelEvent = e;
        removeHighLightsAndSpecialInfos();
        if (!table.isVisible()) {
            return;
        }
        synchronized (table.getTreeLock()) {

            int i = table.getSelectedRow();
            // beim Schlissen des Dialogs ist getSelectedRow()==-1
            if (i >= 0 && i < allSteps.size()) {
                LGMProzessStep selectedStep = allSteps.get(table.getSelectedRow());

                // selectedStep.set3LGMLayout((GraphElementLayout)prozessC.
                // get3LGMLayout().clone());

                specialInfoOwner.add(selectedStep);

                List<ElementContainer> allSpecialInfoTargetContainer = new ArrayList<ElementContainer>();
                if (showKonfs) {
                    List<ModelElement> specialInfoTargets = selectedStep.getRealCommunicationStartKonf();
                    List<ElementContainer> specialInfoTargetContainer = new ArrayList<ElementContainer>(specialInfoTargets != null ? specialInfoTargets.size() : 0);
                    if (specialInfoTargets != null) {
                        specialInfoTargetContainer.addAll(doc.getElementContainer(specialInfoTargets));
                        allSpecialInfoTargetContainer.addAll(specialInfoTargetContainer);
                        for (int b = 0; b < specialInfoTargetContainer.size(); b++) {
                            ElementContainer ec = doc.getElementContainer(specialInfoTargetContainer.get(b));
                            if (ec != null) {
                                ec.addSpecialInfoToThisContainer(new AdditionalLabelTextGenerator(selectedStep, prozessC.get3LGMLayout()), Tool3lgmConstants.getResString("start"));
                            }
                        }
                    }

                    specialInfoTargets = selectedStep.getRealCommunicationEndKonf();
                    specialInfoTargetContainer.clear();
                    if (specialInfoTargets != null) {
                        specialInfoTargetContainer.addAll(doc.getElementContainer(specialInfoTargets));
                        allSpecialInfoTargetContainer.addAll(specialInfoTargetContainer);
                        for (int b = 0; b < specialInfoTargetContainer.size(); b++) {
                            ElementContainer ec = doc.getElementContainer(specialInfoTargetContainer.get(b));
                            if (ec != null) {
                                ec.addSpecialInfoToThisContainer(new AdditionalLabelTextGenerator(selectedStep, prozessC.get3LGMLayout()), Tool3lgmConstants.getResString("ende"));
                            }
                        }
                    }
                }

                if (enumerateSchnittstellen) {
                    List<ModelElement> specialInfoTargets = selectedStep.getKommProzessSchnittstellen();
                    List<ElementContainer> specialInfoTargetContainer = doc.getElementContainer(specialInfoTargets);
                    allSpecialInfoTargetContainer.addAll(specialInfoTargetContainer);
                    // ElementContainer.writeNumberListToTartgets(selectedStep,
                    // specialInfoTargetContainer, prozessC.get3LGMLayout());
                }

                List<Kante> kantenListe = selectedStep.getKommProzessKanten();
                List<ElementContainer> kantenContainerListe = doc.getElementContainer(kantenListe);
                if (enumerateKanten) {
                    allSpecialInfoTargetContainer.addAll(kantenContainerListe);
                    // ElementContainer.writeNumberListToTartgets(selectedStep,
                    // kantenContainerListe, prozessC.get3LGMLayout());
                }

                if (writeObjekttypen) {
                    String ot = selectedStep.getObjektTyp().getClearName();
                    for (ElementContainer kc : kantenContainerListe) {
                        kc.addSpecialInfoToThisContainer(new AdditionalLabelTextGenerator(selectedStep, prozessC.get3LGMLayout()), ot, SwingConstants.SOUTH, true);
                    }
                }

                allSpecialInfoTargetContainer = doc.getElementContainer(allSpecialInfoTargetContainer);
                // selectedStep.setSpecialInfoTargets(
                // allSpecialInfoTargetContainer);

                highlight.add(selectedStep.getEndAufgabe().getContainer(doc));

                ElementContainer ec = selectedStep.getStartAufgabe().getContainer(doc);
                if (ec != null) {
                    highlight.add(ec);
                }

                ec = selectedStep.getObjektTyp().getContainer(doc);
                if (ec != null) {
                    highlight.add(ec);
                }

                if (highlightAllCommElements) {
                    highlight.addAll(doc.getElementContainer(selectedStep.getKommProzessSchnittstellen()));
                    ec = selectedStep.getStartAufgabeKonf().getContainer(doc);
                    if (ec != null) {
                        highlight.add(ec);
                    }
                    ec = selectedStep.getEndAufgabeKonf().getContainer(doc);
                    if (ec != null) {
                        highlight.add(ec);
                    }
                    highlight.addAll(doc.getElementContainer(selectedStep.getStartAufgabeKonfBausteine()));
                    highlight.addAll(doc.getElementContainer(selectedStep.getEndAufgabeKonfBausteine()));
                }
                highlight.addAll(doc.getElementContainer(selectedStep.getKommProzessKanten()));

                for (int j = 0; j < highlight.size(); j++) {
                    // ElementContainer hc =
                    // doc.getElementContainer(highlight.get(j));
                    ElementContainer hc = highlight.get(j);
                    highlight.set(j, hc);
                    hc.setHighLight(true);
                }
            }
            doc.select(getModelElement().getContainer(doc), dialog.getTransactionID());
            doc.distributeEvent(GraphDocument.SELECTION_CHANGED);
            doc.distributeEvent(GraphDocument.ELEMENT_GRAPHICS_CHANGED, dialog.getTransactionID());
        }
    }

    /**
	 *
	 */
    private class MyTable extends JTable {
        KommProzessPanel parent;

        /**
         * @param model
         * @param parent
         */
        public MyTable(final DefaultTableModel model, final KommProzessPanel parent) {
            super(model);
            this.parent = parent;
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            getTableHeader().setReorderingAllowed(false);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        @Override
        public boolean isCellEditable(final int col, final int row) {
            return false;
        }

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            super.valueChanged(e);
            if (parent != null) {
                parent.valueChanged(e);
            }
        }
    }

    /**
     * @param edp
     */
    private static final LGMAction getShowTeilModelleAction(final ElementDialogPanel edp) throws ActionNotDefinedForClassException {
        final ElementDialogPanel pane = edp;
        if (pane instanceof KommProzessPanel) {
            return new LGMAction(Tool3lgmConstants.getResString("submodels")) {
                @Override
                public void execute(final EventObject eo) {
                    KommProzessPanel panel = (KommProzessPanel) pane;
                    KommProzessPanel.showTeilmodelle = panel.showTeilmodelleCheck.isSelected();
                    panel.setColumnWidth = true;
                    panel.updateTable(false);
                    panel.setColumnWidth = false;
                }
            };
        }
        throw new ActionNotDefinedForClassException(pane.getClass().getName());
    }

    /**
     * @param edp
     * @param resString
     */
    public static final LGMAction getRefreshHighlightsAndSpecialInfoAction(final ElementDialogPanel edp, final String resString) throws ActionNotDefinedForClassException {
        final ElementDialogPanel pane = edp;
        if (pane instanceof KommProzessPanel) {
            return new LGMAction(resString) {
                @Override
                public void execute(final EventObject eo) {
                    KommProzessPanel panel = (KommProzessPanel) pane;
                    KommProzessPanel.enumerateSchnittstellen = panel.enumerateSchnittstellenCheck.isSelected();
                    KommProzessPanel.enumerateKanten = panel.enumerateKantenCheck.isSelected();
                    KommProzessPanel.writeObjekttypen = panel.writeObjekttypenCheck.isSelected();
                    KommProzessPanel.showKonfs = panel.showKonfsCheck.isSelected();
                    KommProzessPanel.highlightAllCommElements = panel.highlightAllCommElementsCheck.isSelected();
                    panel.valueChanged(new ListSelectionEvent(panel.table, 0, 0, false));
                }
            };
        }
        throw new ActionNotDefinedForClassException(pane.getClass().getName());
    }

}
