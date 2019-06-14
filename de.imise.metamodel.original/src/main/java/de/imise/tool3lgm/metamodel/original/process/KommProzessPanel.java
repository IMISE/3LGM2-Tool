/*
 * Created on 09.02.2004 To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.original.process;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeType;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.AdditionalLabelTextGenerator;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.original.node.ABKonfiguration;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.AufOrgKombination;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Objekttyp;
import de.imise.tool3lgm.metamodel.original.node.Organisationseinheit;
import de.imise.tool3lgm.metamodel.original.node.Prozess;

/**
 * @author AXS Dieses Panel ist das KommunikationsprozessPanel im ElementPropertyDialog eines
 *         (Geschäfts-)Prozesses
 *         Diese Panel wird im Modemnt nicht genutzt und müsste wiederhergestellt werden, falls es noch einmal gebraucht wird.
 */
public class KommProzessPanel extends ElementDialogPanel {

    // das folgende sind die Resourcen zu diesem Panel.Im Zuge der Lolösung des Metamodells aus dem Baukasten, sind sie erstmal hier
    //gelandet. Sie sind Metamodell-spezifisch und gehören somit nicht in die allg. Resourcen

    //    # Prozess
    //    start_aufg                  Startaufgabe
    //    end_aufg                    Endaufgabe
    //    start_bausteine             Startkonfiguration
    //    start_orgeinh               Startorganisationseinheit
    //    end_bausteine               Endkonfiguration
    //    end_orgeinh                 Endorganisationseinheit
    //    schnittstellen              Schnittstellen
    //    medium_breaks               Medienbrüche
    //    kanten_nummern              Kantennummern
    //    schnitt_nummern             Schnittstellennummern
    //    alles_hervorheben           alles hervorheben
    //
    //    # Prozess
    //    start_aufg                  Start function
    //    end_aufg                    End function
    //    start_bausteine             Start configuration
    //    start_orgeinh               Start organizational unit
    //    end_bausteine               End configuration
    //    end_orgeinh                 End organizational unit
    //    schnittstellen              Interfaces
    //    medium_breaks               Medium breaks
    //    kanten_nummern              Edge numbers
    //    schnitt_nummern             Interface numbers
    //    alles_hervorheben           Highlight all
    //

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
        colHeads[POSITION] = _getResString("position"); // das hier kam aus den Tool-Resourcen, muss aber, wenn dieses Panel mal wieder reaktiviert werden sollte, wegen der sauberen Trennung aus den metamodell-spezifischen Resourcen kommen!
        colHeads[START_FUNCTION] = _getResString("start_aufg");
        colHeads[END_FUNCTION] = _getResString("end_aufg");
        colHeads[OBJECTTYPE] = _getResString("Objekttyp");
        colHeads[START_COMPONENTS] = _getResString("start_bausteine");
        colHeads[START_ORGUNIT] = _getResString("start_orgeinh");
        colHeads[END_COMPONENTS] = _getResString("end_bausteine");
        colHeads[END_ORGUNIT] = _getResString("end_orgeinh");
        colHeads[INTERFACES] = _getResString("schnittstellen");
        colHeads[MEDIUM_BREAKS] = _getResString("medium_breaks");
        colHeads[COMMENTS] = _getResString("bemerk"); // das hier kam aus den Tool-Resourcen, muss aber, wenn dieses Panel mal wieder reaktiviert werden sollte, wegen der sauberen Trennung aus den metamodell-spezifischen Resourcen kommen!

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
            LGMAction enumerateSchnittstellenAction = getRefreshHighlightsAndSpecialInfoAction(this, _getResString("schnitt_nummern"));
            LGMAction enumerateKantenAction = getRefreshHighlightsAndSpecialInfoAction(this, _getResString("kanten_nummern"));
            LGMAction writeObjekttypenAction = getRefreshHighlightsAndSpecialInfoAction(this, _getResString("Objekttyp_p"));
            LGMAction showKonfsAction = getRefreshHighlightsAndSpecialInfoAction(this, _getResString("Anwendungsbaustein_p"));
            LGMAction highlightAllCommElementsAction = getRefreshHighlightsAndSpecialInfoAction(this, _getResString("alles_hervorheben"));
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
     * Gibt fuer eine ArrayList von NodeContainern oder ModelElements einen String des Inhalts zurück. Ist insertNewLines==false, wird
     * eine kommaseparierte Liste zurückgegeben.
     *
     * @param ArrayList
     * @param boolean
     * @param boolean TODO:AXS:in eine eigene Klasse verlegen
     */
    public static String getElementListString(final List<?> list, final boolean showSzenarios, final boolean insertNewLines) {
        StringBuilder serversBuf = new StringBuilder();
        if (list == null) {
            return serversBuf.toString();
        }

        // diese Form mag blöd aussehen, aber so müssen nicht in jedem Schleifendurchlauf die Bedingungen neu geprüft werden
        if (showSzenarios) {
            if (insertNewLines) {
                // Namen der Container durch \n getrennt einfügen
                for (int i = 0; i < list.size(); i++) {
                    serversBuf.append(list.get(i).toString().replace('\n', ' '));
                    serversBuf.append('\n');
                }
            } else {
                // Namen der Container durch ein Leerzeichen und Komma getrennt einfügen
                return list.toString().replace('\n', ' ');
            }
        } else {
            String separator = insertNewLines ? "\n" : ", ";
            // Namen der Elemente durch \n oder ", " getrennt einfügen
            for (Object o : list) {
                if (o instanceof ElementContainer) {
                    o = ((ElementContainer) o).getElement();
                }
                serversBuf.append(o.toString().replace('\n', ' '));
                serversBuf.append(separator);
            }
        }
        // das zuletzt angehängten Zeichen (Komma+Leerzeichen oder Newline) nicht mit zurückgeben
        if (serversBuf.length() > 0) {
            if (insertNewLines) {
                return serversBuf.deleteCharAt(serversBuf.length() - 1).toString();
            }
            return serversBuf.deleteCharAt(serversBuf.length() - 2).toString();
        }
        return serversBuf.toString();
    }

    /**
     * @param checkRowCount
     */
    public void updateTable(final boolean checkRowCount) {
        // System.out.println("updateTable");
        // Aufgaben des Prozesses in holen (NICHT alphabetisch sortiert)
        List<ModelElement> aufgabenListe = prozess.getConnectedElements(Aufgabe.class, PrzAufVerbindung.class, false);

        // Prozess enthält keine Aufgaben und die Zeilenanzahl könnte sich
        // geändert haben
        if (checkRowCount && aufgabenListe.size() == 0) {
            tmodel.setRowCount(0);
            tmodel.fireTableStructureChanged();
            return;
        }
        // alle Kombinationen an Prozesschritten holen
        allSteps = getKommProzessStepCombinations(prozess, aufgabenListe);

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
                        table.setValueAt(getElementListString(step.getStartAufgabeKonfBausteine(), false, false), i, START_COMPONENTS);
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
                        table.setValueAt(getElementListString(step.getEndAufgabeKonfBausteine(), false, false), i, END_COMPONENTS);
                        // Endorganisationseinheit
                        table.setValueAt(step.getEndAufOrgKombination().getConnectedElements(Organisationseinheit.class), i, END_ORGUNIT);
                    } else {
                        table.setValueAt(null, i, END_COMPONENTS);
                        table.setValueAt(null, i, END_ORGUNIT);
                    }
                    // Schnittstellen
                    table.setValueAt(getElementListString(step.getKommProzessSchnittstellen(), false, false), i, INTERFACES);
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
                table.setValueAt(_getResString("kein_OT"), i, COMMENTS);
            } else if (!step.hasStartKonfiguration()) {
                if (step.hasEndKonfiguration()) {
                    table.setValueAt(_getResString("keine_Start_Konf"), i, COMMENTS);
                } else {
                    table.setValueAt(_getResString("keine_Konfigs"), i, COMMENTS);
                }
            } else if (!step.hasEndKonfiguration()) {
                tmodel.setValueAt(_getResString("keine_End_Konf"), i, COMMENTS);
            } else if (step.getKommProzessLength() == ShortestCommunicationPathFinder.INFINITY) {
                table.setValueAt(_getResString("konfigs_passen_nicht"), i, COMMENTS);
            } else if (step.getKommProzessLength() == 0) {
                table.setValueAt(_getResString("konfigs_teilw_gleich"), i, COMMENTS);
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
                                ec.addSpecialInfoToThisContainer(new AdditionalLabelTextGenerator(selectedStep, prozessC.get3LGMLayout()), _getResString("start"));
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
                                ec.addSpecialInfoToThisContainer(new AdditionalLabelTextGenerator(selectedStep, prozessC.get3LGMLayout()), _getResString("ende"));
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
            doc.distributeEvent(GDCollectionChangeType.SELECTION_CHANGED);
            doc.distributeEvent(GDCollectionChangeType.ELEMENT_GRAPHICS_CHANGED, dialog.getTransactionID());
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
    private final LGMAction getShowTeilModelleAction(final ElementDialogPanel edp) throws ActionNotDefinedForClassException {
        final ElementDialogPanel pane = edp;
        if (pane instanceof KommProzessPanel) {
            return new LGMAction(_getResString("submodels")) {// das hier kam aus den Tool-Resourcen, muss aber, wenn dieses Panel mal wieder reaktiviert werden sollte, wegen der sauberen Trennung aus den metamodell-spezifischen Resourcen kommen!

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

    ///////////////////////////////////////////////////////////////////////////////////////////
    // ab hier kommt alles, was mal in Prozess stand und nur in diesem Panel gebraucht wurde //
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gibt eine Liste mit Prozessschritten für eine einzelne Aufgabe zurück. In den einzelnen LGMProzessStep dieser Liste sind nur die startAufgabe,
     * endAufgabe und der Objekttyp gesetzt. endAufgabe ist immer identisch mit der Aufgabe an Position pos aus
     * der Liste der Prozessaufgaben. Es ist also eine Liste aller Aufgaben die im Prozess vor aufgabenPos stehen inkl. der von diesen Aufgaben im
     * gegebenen GraphDocument bearbeiteten Objekttypen, die von der Aufgabe an pos gleichzeitig interpretiert
     * werden. Ex. kein Objekttyp den die Aufgabe an pos interpretiert und eine Vorgaengeraufgabe bearbeitet, wird ein LGMProzessStep zurueckgegeben,
     * in dem nur die endAufgabe der Aufgabe an pos entspricht und der Rest null ist. Wird testOnly=true
     * uebergeben, dann werden nicht alle Schritte gesucht sondern nur der erste vollstaendige zurückgegeben oder eine leere Liste, wenn keiner
     * existiert. Pos wird wie immer ab 0 gezaehlt.
     *
     * @param ModelElement
     * @param int
     * @param GraphDocument
     * @param boolean
     */
    private static List<LGMProzessStep> getProcessStepsForAufgabe(final ModelElement process, final List<ModelElement> aufgaben, final int pos, final boolean testOnly) {
        return getProcessStepsForAufgabe(process, aufgaben, null, pos, testOnly);
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    /**
     * Gibt eine Liste mit Prozessschritten für eine einzelne Aufgabe zurück. In den einzelnen LGMProzessStep dieser Liste sind nur die startAufgabe,
     * endAufgabe und der Objekttyp gesetzt. endAufgabe ist immer identisch mit der uebergebenen Aufgabe. Es ist
     * also eine Liste aller Aufgaben die im Prozess vor aufgabenPos stehen inkl. der von diesen Aufgaben im gegebenen GraphDocument bearbeiteten
     * Objekttypen, die von der übergebenen Aufgabe gleichzeitig interpretiert werden. Ex. kein Objekttyp den die
     * uebergebene Aufgabe interpretiert und eine Vorgaengeraufgabe bearbeitet, wird ein LGMProzessStep zurueckgegeben, in dem nur die endAufgabe der
     * uebergebenen Aufgabe entspricht und der Rest null ist. Wird testOnly=true uebergeben, dann werden nicht
     * alle Schritte gesucht sondern nur der erste vollstaendige zurückgegeben oder eine leere Liste, wenn keiner existiert. Pos wird wie immer ab 0
     * gezaehlt.
     *
     * @param ArrayList Liste mit Elementen der Klasse <code>Aufgabe</code>.
     * @param ModelElement
     * @param int
     * @param GraphDocument
     * @param boolean
     */
    private static List<LGMProzessStep> getProcessStepsForAufgabe(final ModelElement prozess, final List<ModelElement> aufgaben, ModelElement aufgabe, final int pos, final boolean testOnly) throws IndexOutOfBoundsException, IllegalArgumentException {
        //      System.out.println("########   " + pos + ".) " + aufgabe);
        if (aufgabe != null && !(aufgabe instanceof Aufgabe)) {
            throw new IllegalArgumentException();
        }

        //wenn aufgabe null ist, dann soll aufgabe die Aufgabe werden, die an pos steht
        if (pos < 1 || pos > aufgaben.size() || aufgabe == null && pos == aufgaben.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (aufgabe == null) {
            aufgabe = aufgaben.get(pos);
        }

        List<LGMProzessStep> returnList = new ArrayList<>();
        //alle Objekttypen holen, die aufgabe interpretiert

        //      GraphDocument doc = aufgabe.getGraphDocument();

        //Alle von der Aufgabe interpretierten OT in ein Set legen
        Set<ModelElement> setOfInterpretedObjectTypes = new HashSet<>();
        //Alle von den Parts und Parents der Aufgabe interpretierten OT diesem Set hinzufügen
        for (ModelElement auf : aufgabe.getPartAndParentElements()) {
            setOfInterpretedObjectTypes.addAll(auf.getConnectedElements(Objekttyp.class, AufObjVerbindung.class, BACKWARD));
        }

        //für alle Aufgaben in der ProzessListe vor der übergebenen Position
        for (int i = 0; i < pos; i++) {
            ModelElement startAufgabe = aufgaben.get(i);
            //hole die bearbeiteten Objekttypen der i-ten Aufgabe in der Aufgabenliste des Prozesses
            Set<ModelElement> usedObjekttypenOfAufgabe = new HashSet<>();
            for (ModelElement auf : startAufgabe.getPartAndParentElements()) {
                usedObjekttypenOfAufgabe.addAll(auf.getConnectedElements(Objekttyp.class, AufObjVerbindung.class, FORWARD));
            }
            //für jeden dieser Objekttypen
            for (ModelElement usedObjekttyp : usedObjekttypenOfAufgabe) {
                //prüfe, ob übergebene Aufgabe diesen auch interpretiert; inkl. Teil-Objekttypen
                if (setOfInterpretedObjectTypes.contains(usedObjekttyp)) {
                    //wenn ja -> ProzessStep anlegen
                    returnList.add(new LGMProzessStep(prozess, startAufgabe, aufgabe, usedObjekttyp, i, pos));
                    //wenn nur getestet werden soll, ob es überhaupt einen gibt -> dann (beim ersten) raus hier
                    if (testOnly) {
                        return returnList;
                    }
                }
            }
        }
        //testOnly==false -> es soll nicht nur irgendein vollständiger Prozessschritt gesucht werden und
        //returnList.size()==0 -> es wurde keiner gefunden
        //=> einen LGMProzessStep anlegen, der nur die uebergebene Aufgabe ale endAufgabe enthaelt
        //      if (!testOnly && returnList.size()==0)
        //          returnList.add(new LGMProzessStep(getContainer(doc), null, aufgabe, null, -1, pos));
        return returnList;
    }

    //----------------------------------------------------------------------------------------------------------------------------------

    /**
     * Liefert eine Liste aller Prozesschritte und all ihrer Kombinationen, die sich aus unterschiedlichen Konfigurationen der Aufgaben ergeben. Die
     * aufgaben muss eine Liste der ElementContainer von Aufgaben aus dem selben GraphDocument sein. Es wird null
     * zurückgegeben, wenn die ArrayList aufgaben weniger als 2 Elemente hat. Folgende Arten von Schritten koennen in der Rueckgabeliste stehen: 1.)
     * vollstaendige Schritte, also Schritte fuer die nach einem Kommunikationsprozess gesucht werden kann (alle
     * ElementContainer des LGMProzessStep (Startaufgabe, Endaufgabe, Objekttyp, Startkonfiguration, Endkonfiguration) sind nicht null) 2.)
     * LGMProzessStep, in dem nur die endAufgabe gesetzt ist ( = endAufgabe hat keinen Objekttyp interpretiert, den eine
     * der Aufgaben davor im Prozess bearbeitet hat (siehe getBuisnessProcessSteps)) 3.) LGMProzessStep, in dem eine oder beide Konfigurationen null
     * sind ( = mind. eine Aufgabe hat keine Konfiguration)
     *
     * @param ArrayList
     */
    private static List<LGMProzessStep> getKommProzessStepCombinations(final ModelElement process, final List<ModelElement> aufgaben) {
        if (aufgaben.size() < 2) {
            return null;
        }
        //      System.out.println("aufgaben besteht aus " + aufgabenAnzahl + " Aufgaben");

        //Array von ArrayListen der Konfigurationen aller Aufgaben
        @SuppressWarnings("unchecked")
        List<ModelElement>[] konfigs = new ArrayList[aufgaben.size()];

        //steht die gleiche Aufgabe mehrmals in aufgaben, so bekommt sie an jeder Stelle die gleiche
        //Konfigurationsreferenz -> diese braucht dann nur 1x gesetzt werden
        for (int i = 0; i < aufgaben.size(); i++) {
            ModelElement auf = aufgaben.get(i);
            for (ModelElement aufOrgKom : auf.getConnectedElements(AufOrgKombination.class, AufAufOrgVerbindung.class)) {
                konfigs[i] = aufOrgKom.getConnectedElements(ABKonfiguration.class, AwbkAufOrgVerbindung.class);
            }
            for (int j = i + 1; j < aufgaben.size(); j++) {
                if (aufgaben.get(j) == auf) {
                    konfigs[j] = konfigs[i];
                }
            }
        }

        //      System.out.println("######################################################################");
        //      for (int i=0; i<aufgabenAnzahl; i++){
        //          System.out.println(aufgaben.get(i) + " hat folgende Konfigurationen: ");
        //          for (int z=0; z<konfigs[i].size(); z++)
        //              System.out.println("\t" + (z+1) + ".) " + ((Konfiguration)((KonfigurationContainer)konfigs[i].get(z)).getElement()).getServers(doc));
        //          System.out.println();
        //      }

        //Gesamtliste aller in den uebergebenen Aufgaben moeglichen BuisnessProcessSteps zusammenbauen
        List<LGMProzessStep> returnList = new ArrayList<>();
        for (int i = 1; i < aufgaben.size(); i++) {
            returnList.addAll(getProcessStepsForAufgabe(process, aufgaben, i, false));
        }
        //      System.out.println(returnList.size() + " Geschäftsprozessschritte sind identifiziert worden");
        //jetzt geht es darum, für jeden BuisnessProcessSteps dieser Liste alle seine Varianten unterschiedlicher Konfigurationen zu erzeugen
        List<LGMProzessStep> varianten = new ArrayList<>();
        //für jeden Geschäftsprzessschritt in returnList
        for (int i = 0; i < returnList.size(); i++) {
            //hole den Schritt
            LGMProzessStep step = returnList.get(i);
            //startAufgabe ist nur ungleich null, wenn ein korrekter Geschäftsprozessschritt vorliegt und nur für solche kann es Varianten geben
            if (step.getStartAufgabe() == null) {
                continue;
            }
            if (step.getEndAufgabe() == null) {
                continue;
            }
            //Positionen der Aufgaben im Prozess holen
            int indexOfStartAufgabe = step.getStartPosition();
            int indexOfEndAufgabe = step.getEndPosition();

            //System.out.println (i + " " + konfigs[indexOfStartAufgabe].size() + " " + konfigs[indexOfEndAufgabe].size());
            //alle vollständigen Konfigurationsvarianten hinzufügen (Schritte der Art 1.)
            for (int m = 0; m < konfigs[indexOfEndAufgabe].size(); m++) {
                for (int n = 0; n < konfigs[indexOfStartAufgabe].size(); n++) {
                    varianten.add(LGMProzessStep.cloneAndSetKonfigs(step, konfigs[indexOfStartAufgabe].get(n), konfigs[indexOfEndAufgabe].get(m)));
                }
            }
        }
        return varianten;
    }

    public String _getResString(final String key) {
        GDCollection gdcoll = doc.getCollection();
        return gdcoll.getResString(key);
    }

    //  /*
    //
    //
    //  public ArrayList _getKommProzessStepCombinations(GraphDocument doc, ArrayList aufgaben){
    //      if (aufgaben.size()<2) return null;
    ////        System.out.println("aufgaben besteht aus " + aufgabenAnzahl + " Aufgaben");
    //
    //      //Array von ArrayListen der Konfigurationen aller Aufgaben
    //      ArrayList[] konfigs = new ArrayList[aufgaben.size()];
    //
    //      //steht die gleiche Aufgabe mehrmals in aufgaben, so bekommt sie an jeder Stelle die gleiche
    //      //Konfigurationsreferenz -> diese braucht dann nur 1x gesetzt werden
    //      for (int i=0; i<aufgaben.size(); i++){
    //          if (konfigs[i]==null){
    //              konfigs[i] = new ArrayList(10);
    //              Object o = aufgaben.get(i);
    //              for (int j=i+1; j<aufgaben.size(); j++)
    //                  if (aufgaben.get(j)==o)
    //                      konfigs[j] = konfigs[i];
    //          }
    //      }
    //
    //      //hole alle Konfigurationen dieses Szenarios
    //      ArrayList abKonfigurationen = doc.getElementContainer(ABKonfiguration.class);
    //
    //      //in konfigs an die Stelle der jeweiligen Aufgabe die Liste ihrer Konfigurationen schreiben
    //      //hat die i-te Aufgabe keine Konfiguration, ist nach der Schleife konfigs[i]==new ArrayList()
    //      for (int i=0; i<abKonfigurationen.size(); i++){
    //          KonfigurationContainer abkonf = (KonfigurationContainer)abKonfigurationen.get(i);
    //          ArrayList tempList = ((ABKonfiguration)abkonf.getElement()).getClients(doc);
    //          if (tempList.size()<1)
    //              continue;
    //          Object aufgabe = tempList.get(0);
    //          for (int j=0; j<aufgaben.size(); j++)
    //              if (aufgabe==aufgaben.get(j)){
    //                  konfigs[j].add(abkonf);
    //                  break; //man braucht immer nur das erste konfig zu setzen, weil die anderen bei der selben Aufgabe das selbe Objekt sind
    //              }
    //      }
    //
    ///*
    //      System.out.println("######################################################################");
    //      for (int i=0; i<aufgabenAnzahl; i++){
    //          System.out.println(aufgaben.get(i) + " hat folgende Konfigurationen: ");
    //          for (int z=0; z<konfigs[i].size(); z++)
    //              System.out.println("\t" + (z+1) + ".) " + ((Konfiguration)((KonfigurationContainer)konfigs[i].get(z)).getElement()).getServers(doc));
    //          System.out.println();
    //      }
    //* /
    //      //Gesamtliste aller in den uebergebenen Aufgaben moeglichen BuisnessProcessSteps zusammenbauen
    //      ArrayList returnList = new ArrayList(100);
    //      for (int i=1; i<aufgaben.size(); i++)
    //          returnList.addAll(getProcessStepsForAufgabe(doc, aufgaben, i, false));
    ////        System.out.println(returnList.size() + " Geschäftsprozessschritte sind identifiziert worden");
    //      //jetzt geht es darum, für jeden BuisnessProcessSteps dieser Liste alle seine Varianten unterschiedlicher Konfigurationen zu erzeugen
    //      ArrayList varianten = new ArrayList(100);
    //      //für jeden Geschäftsprzessschritt in returnList
    //      for (int i=0; i<returnList.size(); i++){
    //          //hole den Schritt
    //          LGMProzessStep step = (LGMProzessStep)returnList.get(i);
    //          //startAufgabe ist nur ungleich null, wenn ein korrekter Geschäftsprozessschritt vorliegt und nur für solche kann es Varianten geben
    //          if (step.getStartAufgabe()==null){
    //              continue;
    //          }
    //          if (step.getEndAufgabe()==null){
    //              continue;
    //          }
    //          //Positionen der Aufgaben im Prozess holen
    //          int indexOfStartAufgabe = step.getStartPosition();
    //          int indexOfEndAufgabe = step.getEndPosition();
    //
    //          //System.out.println (i + " " + konfigs[indexOfStartAufgabe].size() + " " + konfigs[indexOfEndAufgabe].size());
    //          //alle vollständigen Konfigurationsvarianten hinzufügen (Schritte der Art 1.)
    //          for (int m=0; m<konfigs[indexOfEndAufgabe].size(); m++){
    //              for (int n=0; n<konfigs[indexOfStartAufgabe].size(); n++){
    //                  varianten.add(LGMProzessStep.cloneAndSetKonfigs(step, konfigs[indexOfStartAufgabe].get(n), konfigs[indexOfEndAufgabe].get(m)));
    //              }
    //          }
    //      }
    //      return varianten;
    //  }
    //
    //
    //  */

}
