/*
 * Created on 09.02.2004 To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.analyse.process;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.elements.Edge.ANY;

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

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.AdditionalLabelTextGenerator;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Organisationseinheit;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Prozess;

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

    private static boolean showSubmodels = false;

    private JCheckBox showSubmodelsCheck;

    private static boolean enumerateInterfaces = false;

    private JCheckBox enumerateInterfacesCheck;

    private static boolean enumerateEdges = true;

    private JCheckBox enumerateEdgesCheck;

    private static boolean writeObjectTypes = false;

    private JCheckBox writeObjectTypesCheck;

    private static boolean showConfigs = false;

    private JCheckBox showConfigsCheck;

    private static boolean highlightAllCommElements = false;

    private JCheckBox highlightAllCommElementsCheck;

    private boolean setColumnWidth = true;

    List<LGMProzessStep> allSteps;

    // Zeilennummern in der Tabelle
    private static final int COLUMN_COUNT = 11;

    private static final int POSITION = 0;

    private static final int COMMENTS = 1;

    private static final int START_FUNCTION = 2;

    private static final int END_FUNCTION = 3;

    private static final int OBJECTTYPE = 4;

    private static final int START_COMPONENTS = 5;

    private static final int START_ORGUNIT = 6;

    private static final int END_COMPONENTS = 7;

    private static final int END_ORGUNIT = 8;

    private static final int INTERFACES = 9;

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
        colHeads[POSITION] = getResString("position");
        colHeads[START_FUNCTION] = getResString("start_aufg");
        colHeads[END_FUNCTION] = getResString("end_aufg");
        colHeads[OBJECTTYPE] = getResString("Objekttyp");
        colHeads[START_COMPONENTS] = getResString("start_bausteine");
        colHeads[START_ORGUNIT] = getResString("start_orgeinh");
        colHeads[END_COMPONENTS] = getResString("end_bausteine");
        colHeads[END_ORGUNIT] = getResString("end_orgeinh");
        colHeads[INTERFACES] = getResString("schnittstellen");
        colHeads[MEDIUM_BREAKS] = getResString("medium_breaks");
        colHeads[COMMENTS] = getResString("bemerk");

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
            LGMAction enumerateSchnittstellenAction = getRefreshHighlightsAndSpecialInfoAction(this, getResString("schnitt_nummern"));
            LGMAction enumerateKantenAction = getRefreshHighlightsAndSpecialInfoAction(this, getResString("kanten_nummern"));
            LGMAction writeObjekttypenAction = getRefreshHighlightsAndSpecialInfoAction(this, getResString("Objekttyp_p"));
            LGMAction showKonfsAction = getRefreshHighlightsAndSpecialInfoAction(this, getResString("Anwendungsbaustein_p"));
            LGMAction highlightAllCommElementsAction = getRefreshHighlightsAndSpecialInfoAction(this, getResString("alles_hervorheben"));
            showSubmodelsCheck = new JCheckBox();
            showSubmodelsCheck.setSelected(showSubmodels);
            showSubmodelsCheck.setAction(showTeilmodelleAction);

            enumerateInterfacesCheck = new JCheckBox();
            enumerateInterfacesCheck.setSelected(enumerateInterfaces);
            enumerateInterfacesCheck.setAction(enumerateSchnittstellenAction);

            enumerateEdgesCheck = new JCheckBox();
            enumerateEdgesCheck.setSelected(enumerateEdges);
            enumerateEdgesCheck.setAction(enumerateKantenAction);

            writeObjectTypesCheck = new JCheckBox();
            writeObjectTypesCheck.setSelected(writeObjectTypes);
            writeObjectTypesCheck.setAction(writeObjekttypenAction);

            showConfigsCheck = new JCheckBox();
            showConfigsCheck.setSelected(showConfigs);
            showConfigsCheck.setAction(showKonfsAction);

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
        checkPanel.add(enumerateEdgesCheck);
        checkPanel.add(showConfigsCheck);
        checkPanel.add(writeObjectTypesCheck);
        checkPanel.add(enumerateInterfacesCheck);
        checkPanel.add(highlightAllCommElementsCheck);
        checkPanel.add(showSubmodelsCheck);
        JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.add(checkPanel, BorderLayout.WEST);
        add(optionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void update() {
        // das Panel braucht nur geupdatet zu werden, wenn es sichtbar ist
        if (isVisible()) { // componentShown(new ComponentEvent(this, -1));
            Tool3lgm tool = Static.getTool();
            Cursor cursor = tool.getCursor();
            tool.setCursor(Tool3lgmConstants.getWaitCursor());

            setColumnWidth = true;
            updateTable(true);
            setColumnWidth = false;
            showSubmodelsCheck.setSelected(showSubmodels);
            enumerateInterfacesCheck.setSelected(enumerateInterfaces);
            enumerateEdgesCheck.setSelected(enumerateEdges);
            writeObjectTypesCheck.setSelected(writeObjectTypes);
            showConfigsCheck.setSelected(showConfigs);
            highlightAllCommElementsCheck.setSelected(highlightAllCommElements);

            tool.setCursor(cursor);
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
        List<ModelElement> aufgabenListe = prozess.getConnectedElements(Aufgabe.class, doc, null, ANY, false);

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
            if (!showSubmodels) {

                // die Endaufgabe ist in jedem Step gesetzt
                table.setValueAt(step.getEndAufgabe(), i, END_FUNCTION);// Endaufgabe

                ModelElement me = step.getStartAufgabe();
                // Startaufgabe existiert
                if (me != null) { // jetzt nur noch vollständige Schritte
                    // hinzufügen
                    table.setValueAt(me, i, START_FUNCTION); // Startaufgabe
                    // Objekttyp (der kann hier nie null sein, da eine Startaufgabe gefunden wurde)
                    table.setValueAt(step.getObjektTyp(), i, OBJECTTYPE);
                    me = step.getStartAufgabeKonf(); // Startkonfiguration es ex. eine
                                                     // Startkonfiguration
                    if (me != null) {
                        // Startkonfigurationsbausteine
                        table.setValueAt(Tool3lgmConstants.getElementListString(step.getStartAufgabeKonfBausteine(), false, false), i, START_COMPONENTS);
                        // Startorganisationseinheit
                        table.setValueAt(step.getStartAufOrgKombination().getConnectedElements(Organisationseinheit.class), i, START_ORGUNIT);
                    } else {
                        table.setValueAt(null, i, START_COMPONENTS);
                        table.setValueAt(null, i, START_ORGUNIT);
                        table.setValueAt(null, i, MEDIUM_BREAKS);// Medienbrüche
                    }

                    me = step.getEndAufgabeKonf(); // Endkonfiguration
                    if (me != null) {
                        // Endkonfigurationsbausteine
                        table.setValueAt(Tool3lgmConstants.getElementListString(step.getEndAufgabeKonfBausteine(), false, false), i, END_COMPONENTS);
                        // Endorganisationseinheit
                        table.setValueAt(step.getEndAufOrgKombination().getConnectedElements(Organisationseinheit.class), i, END_ORGUNIT);
                    } else {
                        table.setValueAt(null, i, END_COMPONENTS);
                        table.setValueAt(null, i, END_ORGUNIT);
                    }
                    // Schnittstellen
                    table.setValueAt(Tool3lgmConstants.getElementListString(step.getKommProzessSchnittstellen(), false, false), i, INTERFACES);
                }
                // Teilmodelle anzeigen -> Namen der Container anzeigen
            } else {
                // die Endaufgabe muss in jedem Step gesetzt sein
                tmodel.setValueAt(step.getEndAufgabe(), i, END_FUNCTION); // Endaufgabe

                ModelElement me = step.getStartAufgabe();
                if (me != null) {
                    table.setValueAt(me, i, START_FUNCTION); // Startaufgabe
                    table.setValueAt(step.getObjektTyp(), i, OBJECTTYPE); // Objekttyp
                    me = step.getStartAufgabeKonf(); // Startkonfiguration
                    if (me != null) {
                        // Startkonfigurationsbausteine
                        table.setValueAt(me.getConnectedContainer(Anwendungsbaustein.class, doc), i, START_COMPONENTS);
                        // Startorganisationseinheit
                        table.setValueAt(step.getStartAufOrgKombination().getContainer(doc), i, START_ORGUNIT);
                    } else {
                        table.setValueAt(null, i, START_COMPONENTS);
                        table.setValueAt(null, i, START_ORGUNIT);
                    }

                    me = step.getEndAufgabeKonf(); // Endkonfiguration
                    if (me != null) {
                        // Endkonfigurationsbausteine
                        table.setValueAt(me.getConnectedContainer(Anwendungsbaustein.class, doc), i, END_COMPONENTS);
                        // Endorganisationseinheit
                        table.setValueAt(step.getEndAufOrgKombination().getContainer(doc), i, END_ORGUNIT);
                    } else {
                        table.setValueAt(null, i, END_COMPONENTS);
                        table.setValueAt(null, i, END_ORGUNIT);
                    }
                    // Schnittstellen
                    table.setValueAt(step.getKommProzessSchnittstellen(), i, INTERFACES);
                }
            }
            table.setValueAt(null, i, MEDIUM_BREAKS);// Medienbruchspalte
            // löschen
            // es ex. keine Startaufgabe -> Felder löschen, die keine Werte
            // enthalten sollen (das muss sein, falls noch alte Werte darin
            // stehen)
            if (!step.hasStartAufgabe()) {
                table.setValueAt(null, i, START_FUNCTION);// Startaufgabe
                table.setValueAt(null, i, OBJECTTYPE);// Objekttyp
                table.setValueAt(null, i, START_COMPONENTS);// Startkonfigurationsbausteine
                table.setValueAt(null, i, START_ORGUNIT);// Startorganisationseinheit
                table.setValueAt(null, i, END_COMPONENTS);// Endkonfigurationsbausteine
                table.setValueAt(null, i, END_ORGUNIT);// Endorganisationseinheit
                table.setValueAt(null, i, INTERFACES);// Schnittstellen
                table.setValueAt(Tool3lgmConstants.getErrString("kein_OT"), i, COMMENTS);
            } else if (!step.hasStartKonfiguration()) {
                if (step.hasEndKonfiguration()) {
                    table.setValueAt(Tool3lgmConstants.getErrString("keine_Start_Konf"), i, COMMENTS);
                } else {
                    table.setValueAt(Tool3lgmConstants.getErrString("keine_Konfigs"), i, COMMENTS);
                }
            } else if (!step.hasEndKonfiguration()) {
                tmodel.setValueAt(Tool3lgmConstants.getErrString("keine_End_Konf"), i, COMMENTS);
            } else if (step.getKommProzessLength() == ShortestCommunicationPathFinder.INFINITY) {
                table.setValueAt(Tool3lgmConstants.getErrString("konfigs_passen_nicht"), i, COMMENTS);
            } else if (step.getKommProzessLength() == 0) {
                table.setValueAt(Tool3lgmConstants.getErrString("konfigs_teilw_gleich"), i, COMMENTS);
            } else {
                // Bemerkungsspalte löschen
                table.setValueAt(null, i, COMMENTS);
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

                List<ElementContainer> allSpecialInfoTargetContainer = new ArrayList<>();
                if (showConfigs) {
                    List<ModelElement> specialInfoTargets = selectedStep.getRealCommunicationStartKonf();
                    List<ElementContainer> specialInfoTargetContainer = new ArrayList<>(specialInfoTargets != null ? specialInfoTargets.size() : 0);
                    if (specialInfoTargets != null) {
                        specialInfoTargetContainer.addAll(doc.getElementContainer(specialInfoTargets));
                        allSpecialInfoTargetContainer.addAll(specialInfoTargetContainer);
                        for (int b = 0; b < specialInfoTargetContainer.size(); b++) {
                            ElementContainer ec = doc.getElementContainer(specialInfoTargetContainer.get(b));
                            if (ec != null) {
                                ec.addSpecialInfoToThisContainer(new AdditionalLabelTextGenerator(selectedStep, prozessC.get3LGMLayout()), getResString("start"));
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
                                ec.addSpecialInfoToThisContainer(new AdditionalLabelTextGenerator(selectedStep, prozessC.get3LGMLayout()), getResString("ende"));
                            }
                        }
                    }
                }

                if (enumerateInterfaces) {
                    List<ModelElement> specialInfoTargets = selectedStep.getKommProzessSchnittstellen();
                    List<ElementContainer> specialInfoTargetContainer = doc.getElementContainer(specialInfoTargets);
                    allSpecialInfoTargetContainer.addAll(specialInfoTargetContainer);
                    // ElementContainer.writeNumberListToTartgets(selectedStep,
                    // specialInfoTargetContainer, prozessC.get3LGMLayout());
                }

                List<Edge> kantenListe = selectedStep.getKommProzessKanten();
                List<ElementContainer> kantenContainerListe = doc.getElementContainer(kantenListe);
                if (enumerateEdges) {
                    allSpecialInfoTargetContainer.addAll(kantenContainerListe);
                    // ElementContainer.writeNumberListToTartgets(selectedStep,
                    // kantenContainerListe, prozessC.get3LGMLayout());
                }

                if (writeObjectTypes) {
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
            return new LGMAction(getResString("submodels")) {

                @Override
                public void execute(final EventObject eo) {
                    KommProzessPanel panel = (KommProzessPanel) pane;
                    KommProzessPanel.showSubmodels = panel.showSubmodelsCheck.isSelected();
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
                    KommProzessPanel.enumerateInterfaces = panel.enumerateInterfacesCheck.isSelected();
                    KommProzessPanel.enumerateEdges = panel.enumerateEdgesCheck.isSelected();
                    KommProzessPanel.writeObjectTypes = panel.writeObjectTypesCheck.isSelected();
                    KommProzessPanel.showConfigs = panel.showConfigsCheck.isSelected();
                    KommProzessPanel.highlightAllCommElements = panel.highlightAllCommElementsCheck.isSelected();
                    panel.valueChanged(new ListSelectionEvent(panel.table, 0, 0, false));
                }
            };
        }
        throw new ActionNotDefinedForClassException(pane.getClass().getName());
    }

}
