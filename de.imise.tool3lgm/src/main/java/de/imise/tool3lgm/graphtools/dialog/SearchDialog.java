package de.imise.tool3lgm.graphtools.dialog;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ContextGenerator;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Alphabetical;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.HistoryComboBox;

public class SearchDialog extends JDialog implements ActionListener, ListSelectionListener, WindowListener, ItemListener {

    /** Eingabefeld Bezeichnung */
    private static HistoryComboBox elementName = new HistoryComboBox();

    /** Eingabefeld Beschreibung */
    private static HistoryComboBox elementDescription = new HistoryComboBox();

    /** Eingabefeld Benutzerdef Eigenschaften */
    private static HistoryComboBox elementUserField = new HistoryComboBox();

    /** Groß/Kleinschreibung Bezeichnung */
    private static boolean ignoreCaseInName = true;

    /** Groß/Kleinschreibung Beschreibung */
    private static boolean ignoreCaseInDescription = true;

    /** Groß/Kleinschreibung benutzerdef. Eigenschaften */
    private static boolean ignoreCaseInUserField = true;

    /** für Spaltensortierungszustand ID */
    private static boolean sortIdAsc = true;

    /** für Spaltensortierungszustand Name */
    private static boolean sortNameAsc = true;

    /** für Spaltensortierungszustand Type */
    private static boolean sortTypeAsc = true;

    /** Konstante für Checkboxen Suchen (aktivierte und deaktivierte) */
    final static int CHECKBOXMODE_ALL = 0;

    /** Konstante für aktivierte Checkboxen */
    final static int CHECKBOXMODE_ACTIVATED = 1;

    /** Konstante für deaktivierte Checkboxen Suchen */
    final static int CHECKBOXMODE_NOT_ACTIVATED = 2;

    /** beinhaltet den Wert der oberen Konstanten */
    private int checkBoxMode = 0;

    /** Checkbox für ignore case Bezeichnung */
    private final JCheckBox elementName_cb = new JCheckBox(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_CBText"), SearchDialog.ignoreCaseInName);

    /** Checkbox für ignore case Beschreibung */
    private final JCheckBox elementDescription_cb = new JCheckBox(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_CBText"), SearchDialog.ignoreCaseInDescription);

    /** Checkbox für ignore case Benutzerdef Eigenschaften */
    private final JCheckBox elementUserField_cb = new JCheckBox(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_CBText"), SearchDialog.ignoreCaseInUserField);

    /** Checkbox Checkboxsuche */
    private JComboBox checkBoxAuswahl = new AlphabeticalComboBox();

    /** Typbox der benutzerdef. Eigenschaften wie Checkbox, Textfeld usw. */
    private AlphabeticalComboBox userFieldTypeComboBox;

    /** Combobox Elementeart */
    private final AlphabeticalComboBox elementClassBox = new AlphabeticalComboBox();

    /** Combobox Modell */
    private final AlphabeticalComboBox modelBox = new AlphabeticalComboBox();

    /** Combobox Teilmodell */
    private final AlphabeticalComboBox subModelBox = new AlphabeticalComboBox();

    /** Suchknopf */
    private final JButton searchButton = new JButton(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Button"));

    /** Ergebnistabelle */
    private static JTable table;

    /** TableModel der Ergebnistabelle */
    private static DefaultTableModel mod;

    /**
     * Konstruiert auf dem Frame den Dialog.
     * 
     * @param owner
     */
    public SearchDialog(final Frame owner) {
        super(owner);

        // um überblenden zu verhindern
        setMinimumSize(new Dimension(600, 400));

        // listener
        addWindowListener(this);
        addSearchButtonKeyListener();
        addJCBListeners();
        addCBListeners();
        searchButton.addActionListener(this);
        elementClassBox.addActionListener(this);
        modelBox.addActionListener(this);
        subModelBox.addActionListener(this);

        setTitle(Tool3lgmConstants.getResString("suchd"));

        GridBagLayout gbl = new GridBagLayout();

        JPanel inputPane = new JPanel();

        addToolTips();

        GridBagConstraints constraints = new GridBagConstraints();
        inputPane.setLayout(gbl);
        inputPane.setBorder(BorderFactory.createLineBorder(Color.black));

        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;

        // erste Spalte
        inputPane.add(new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Modell")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Teilmodell")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Elementeart")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_NAME")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_BESCHREIBUNG")), constraints);

        // nächste Spalte
        constraints.gridx++;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        inputPane.add(modelBox, constraints);
        constraints.gridy++;
        inputPane.add(subModelBox, constraints);
        constraints.gridy++;
        constraints.weightx = GridBagConstraints.REMAINDER;
        inputPane.add(elementClassBox, constraints);
        constraints.gridy++;
        inputPane.add(elementName, constraints);
        constraints.gridy++;
        inputPane.add(elementDescription, constraints);

        // Checkbox Spalte
        constraints.gridx++;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;

        constraints.gridy++;
        constraints.gridy++;
        constraints.gridy++;

        inputPane.add(elementName_cb, constraints);
        constraints.gridy++;
        inputPane.add(elementDescription_cb, constraints);
        constraints.gridy++;

        // Subpanel für benutzerdefinierte Eigenschaften
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;

        // Zeile 1 Subpanel
        JPanel userFieldPanel = new JPanel();
        userFieldPanel.setLayout(new GridBagLayout());
        userFieldPanel.setBorder(BorderFactory.createTitledBorder(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_benutzdef_eig")));

        JLabel userfieldproperty = new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Attributtyp"));
        userfieldproperty.setToolTipText(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Attributtyp_TT"));
        userFieldPanel.add(userfieldproperty, constraints);

        constraints.gridx++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        createUserFieldTypeComboBox();
        userFieldPanel.add(userFieldTypeComboBox, constraints);

        // Zeile 2 Subpanel
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.fill = GridBagConstraints.NONE;
        JLabel containingText = new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Text"));
        containingText.setToolTipText(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Text_TT"));
        userFieldPanel.add(containingText, constraints);

        constraints.gridx++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        userFieldPanel.add(elementUserField, constraints);
        constraints.gridx++;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 1;
        userFieldPanel.add(elementUserField_cb, constraints);

        // Zeile 3 Checkboxsuche
        constraints.gridx = 0;
        constraints.gridy++;
        JLabel checkboxFind = new JLabel(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_CB_filter"));
        userFieldPanel.add(checkboxFind, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        userFieldPanel.add(checkBoxAuswahl, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        inputPane.add(userFieldPanel, constraints);

        userFieldTypeComboBox.addItemListener(this);
        checkBoxAuswahl.addItemListener(this);
        userFieldTypeComboBox.setSelectedItem(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_all"));

        // Searchbutton
        constraints.gridy++;
        constraints.gridx--;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        inputPane.add(searchButton, constraints);

        // Selectboxen befüllen
        fillElementClassBox();
        fillModelBox();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPane, BorderLayout.NORTH);

        // Tabellenmodell initialisieren
        mod = getDefaultTableModel();

        // Tabelle initialisieren, Listener, Sortierung
        table = initialiseTable(mod);
        JScrollPane sp = new JScrollPane(table) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 300);
            }
        };
        getContentPane().add(sp, BorderLayout.CENTER);

        pack();
    }

    /**
     * Die zentrale Suchmethode die aufgerufen wird. Prinzip: Alle Elemente des Teilmodels landen in
     * <code>searchSet</code> Nicht erfüllte Suchkriterium werden herausgefiltert mittels
     * <code>searchSet.remove</code>
     * 
     * @param e - übergebener ActionEvent
     */
    private void callSearch(final ActionEvent e) {

        HistoryComboBox.addToHistory(elementName);
        HistoryComboBox.addToHistory(elementUserField);
        HistoryComboBox.addToHistory(elementDescription);

        GraphDocument doc = (GraphDocument) subModelBox.getSelectedObject();
        if (doc == null) {
            return;
        }
        if (e.getSource() == modelBox) {
            fillSubModelBox();
        }

        // wenn Groß-/KLeinschreibung ignorieren, dann wandle in kleine namen um
        String name = elementName_cb.isSelected() ? cleanName((String) elementName.getSelectedItem()) : (String) elementName.getSelectedItem();
        String bez = elementDescription_cb.isSelected() ? cleanName((String) elementDescription.getSelectedItem()) : (String) elementDescription.getSelectedItem();
        String ud = elementUserField_cb.isSelected() ? cleanName((String) elementUserField.getSelectedItem()) : (String) elementUserField.getSelectedItem();

        // Null abfangen
        if (name == null || name.equals("")) {
            name = ".*";
        } else {
            name = name.replaceAll("\\*", ".*").replaceAll("\\?", ".");
        }
        if (bez == null) {
            bez = "";
        }
        if (ud == null) {
            ud = "";
        } else {
            ud = ud.replaceAll("\\*", ".*").replaceAll("\\?", ".");
        }

        // beim aufruf des fensters nicht suchen (listener feuern aber beim öffnen des fensters
        // bereits)
        if (table == null || name.equals("") && bez.equals("") && ud.equals("")) {
            return;
        }

        List<ElementContainer> searchSet = doc.getElementContainer((Class<? extends ModelElement>) elementClassBox.getSelectedObject(), true, true);
        GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();
        if (doc != mainDoc) {
            for (ElementContainer ec : mainDoc.getElementContainer((Class<? extends ModelElement>) elementClassBox.getSelectedObject(), true, true)) {
                if (ModelConstants.isUnique(ec.getElement().getClass())) {
                    Alphabetical.insert(searchSet, ec);
                }
            }
        }
        RE re1 = null;
        if (!name.equals("")) {
            try {
                re1 = new RE(name);
            } catch (REException error) {
                Log.show(Log.FATAL, Tool3lgmConstants.getErrString("regexp_search") + "\n" + error, error);
            }
        }
        RE re2 = null;
        if (!bez.equals("")) {
            try {
                re2 = new RE(bez);
            } catch (REException error) {
                Log.show(Log.FATAL, Tool3lgmConstants.getErrString("regexp_search") + "\n" + error, error);
            }
        }
        RE re3 = null;
        if (!ud.equals("")) {
            try {
                re3 = new RE(ud);
            } catch (REException error) {
                Log.show(Log.FATAL, Tool3lgmConstants.getErrString("regexp_search") + "\n" + error, error);
            }
        }

        for (int i = searchSet.size() - 1; i >= 0; i--) {
            ModelElement me = searchSet.get(i).getElement();
            if (re1 != null) {
                String string = elementName_cb.isSelected() ? cleanName(me.getName()) : me.getName();
                REMatch match1 = re1.getMatch(string);
                if (match1 == null) {
                    searchSet.remove(i);
                    continue;
                }
            }
            if (re2 != null) {
                String string = elementDescription_cb.isSelected() ? cleanName(me.getDescription()) : me.getDescription();
                REMatch match2 = re2.getMatch(string);
                if (match2 == null) {
                    searchSet.remove(i);
                }
                continue;
            }

            // re3 muss gesetzt werden, sonst wird nie etwas removed, wenn z.b. auf checkboxen
            // eingeschränkt wird
            if (re3 == null && !userFieldTypeComboBox.getSelectedObject().equals(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_all"))) {
                try {
                    re3 = new RE(" ");
                } catch (REException e1) {
                    e1.printStackTrace();
                }
            }

            if (re3 != null || userFieldTypeComboBox.getSelectedObject().equals(UserField.Style.CHECK_BOX)) {
                if (me.getUserFieldInputValueKeys().size() == 0) {
                    searchSet.remove(i);
                } else {
                    boolean found = false;
                    for (UserField key : me.getUserFieldInputValueKeys()) {

                        String string = me.getUserFieldInputValue(key);
                        // im all-modus und im checkboxmodus muss auf TRUE/FALSE abgefragt werden
                        // + zusätzlich muss label stimmen
                        if (userFieldTypeComboBox.getSelectedObject().equals(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_all")) || ((UserField.Style) userFieldTypeComboBox.getSelectedObject()).equals(UserField.Style.CHECK_BOX)
                                && key.getStyle().equals(UserField.Style.CHECK_BOX)) {
                            // -> Checkbox suchen

                            boolean nameOfCheckBoxMatched = false;// (+ zusätzlich muss label
                                                                  // stimmen)
                            REMatch matchNameOfCheckBox = re3.getMatch(key.getName());
                            if (matchNameOfCheckBox != null) {
                                nameOfCheckBoxMatched = true;
                            }

                            if (checkBoxMode == CHECKBOXMODE_ACTIVATED && string.equals("true") && nameOfCheckBoxMatched) {
                                found = true;
                            } else if (checkBoxMode == CHECKBOXMODE_NOT_ACTIVATED && string.equals("false") && nameOfCheckBoxMatched) {
                                found = true;
                            } else if (checkBoxMode == CHECKBOXMODE_ALL && nameOfCheckBoxMatched) {
                                found = true;
                            }
                        }
                        // im allmodus und wenn der attributtyp übereinstimmt muss im inhalt gesucht
                        // werden
                        if (userFieldTypeComboBox.getSelectedObject().equals(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_all")) || !((UserField.Style) userFieldTypeComboBox.getSelectedObject()).equals(UserField.Style.CHECK_BOX)
                                && ((UserField.Style) userFieldTypeComboBox.getSelectedObject()).equals(key.getStyle())) {
                            // -> keine Checkbox suchen, sondern Inhalte
                            // Punkte in Kommas umwandeln
                            if (Pattern.matches("[0-9]+\\.[0-9]+", string)) {
                                string = string.replaceAll("\\.", ",");
                            }

                            string = elementUserField_cb.isSelected() ? cleanName(string) : string;
                            REMatch match3 = re3.getMatch(string);
                            if (match3 == null) {
                                continue;
                            }
                            found = true;
                        }

                    }
                    // nur einmal entfernen
                    if (!found) {
                        searchSet.remove(i);
                    }
                }
            }
        }

        mod = (DefaultTableModel) table.getModel();
        int anzahl = mod.getRowCount();
        for (int r = 0; r < anzahl; r++) {
            mod.removeRow(0);
        }

        int rowCounter = 1;
        Object[] data = new Object[3];
        for (ElementContainer ec : searchSet) {
            data[0] = rowCounter;
            data[1] = ec;
            // data[2] = ec.getGraphDocument().getTitle();
            if (ec.getElement() instanceof Kante) {
                data[2] = ModelConstants.getDisplayableName(ec.getElement()) + ": " + ModelConstants.getFullForwardMetaAssociationName(ec.getElement().getClass().asSubclass(Kante.class));
            } else {
                data[2] = ModelConstants.getDisplayableName(ec.getElement());
            }
            mod.addRow(data);
            rowCounter++;
        }
        table.revalidate();
        table.repaint();
    }

    /**
     * TabellenModell Initialisieren initialisieren
     * 
     * @return DefaultTableModel
     */
    private static final DefaultTableModel getDefaultTableModel() {
        DefaultTableModel mod = new DefaultTableModel();
        mod.addColumn(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Col1"));
        mod.addColumn(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Col2"));
        mod.addColumn(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_Col3"));
        return mod;
    }

    /**
     * Tabelle initialisieren Listener dranhängen Sortierung
     * 
     * @return DefaultTableModel
     */
    private JTable initialiseTable(final DefaultTableModel mod) {

        // Tabelle initialisieren
        table = new JTable(mod) {
            @Override
            public boolean isCellEditable(final int rowIndex, final int vColIndex) {
                return false;
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getSelectionModel().addListSelectionListener(this);

        // Listener dranhängen
        table.addMouseListener(new MouseAdapter() {
            private boolean isPopupTrigger(final MouseEvent e) {

                // Manuell selektieren
                if (!(table.getSelectedRow() > -1)) {
                    table.getSelectionModel().setSelectionInterval(table.rowAtPoint(e.getPoint()), table.rowAtPoint(e.getPoint()));
                }
                // Nur wenn nicht mehr als eine Zeile markiert
                if (table.getSelectedRowCount() > 0) {
                    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
                        return false;
                    }
                    // Nur wenn wirklich markiertes angewählt wurde, sonst passiert nichts
                    int[] rows = table.getSelectedRows();
                    for (int i = 0; i < rows.length; i++) {
                        if (table.isRowSelected(table.rowAtPoint(e.getPoint()))) {
                            return true;
                        }
                    }
                    // Nichts gefunden
                    // -> singlerow selektieren und menü anzeigen
                    table.getSelectionModel().setSelectionInterval(table.rowAtPoint(e.getPoint()), table.rowAtPoint(e.getPoint()));
                    return true;
                }
                return false;
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                // Aktive Zeile markieren
                if (!isPopupTrigger(e)) {
                    if (e.getClickCount() > 1) {
                        ((GraphDocument) subModelBox.getSelectedObject()).showPropertyDialog();
                    }
                } else {
                    ContextGenerator cg = Tool3lgm.getContextGenerator();
                    cg.changeContext((LGMGraphDocument) subModelBox.getSelectedObject());

                    JPopupMenu jpm = cg.getSearchDialogContextMenu();
                    // refresh

                    jpm.show(table, e.getX(), e.getY());

                    Component[] comps = jpm.getComponents();
                    for (Component component : comps) {
                        if (component instanceof JMenuItem) {
                            JMenuItem jmi = (JMenuItem) component;
                            // Nur wenn nicht Eigenschaften: Suche starten
                            if (!jmi.getText().equals(Tool3lgmConstants.getResString("eigenschaften"))) {
                                jmi.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(final ActionEvent e) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                callSearch(new ActionEvent(searchButton, 0, ""));
                                            }
                                        });
                                    }
                                });
                            }
                        }

                    }

                }

            }
        });

        // Sortierung umsetzen
        TableRowSorter<TableModel> trs = new TableRowSorter<TableModel>(table.getModel());
        trs.setComparator(0, new Comparator<Integer>() {
            @Override
            public int compare(final Integer int1, final Integer int2) {
                return int1.compareTo(int2);
            }

        });

        trs.setSortable(0, false);
        trs.setSortable(1, false);
        trs.setSortable(2, false);

        // -> wenn 0: nur id; wenn 1: name switchen, modell bleibt wie vorher, wenn 2: modell
        // switchen, name bleibt wie vorher;
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                int colX = table.getColumnModel().getColumnIndexAtX(e.getX());
                @SuppressWarnings("unchecked")
                TableRowSorter<TableModel> trs = (TableRowSorter<TableModel>) table.getRowSorter();
                List<RowSorter.SortKey> sk = new ArrayList<RowSorter.SortKey>();
                if (colX == 0) {
                    sortIdAsc = !sortIdAsc;
                    sk.add(new RowSorter.SortKey(0, sortIdAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));
                } else if (colX == 1) {
                    sortNameAsc = !sortNameAsc;
                    sk.add(new RowSorter.SortKey(1, sortNameAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));
                    sk.add(new RowSorter.SortKey(2, sortTypeAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));

                } else if (colX == 2) {
                    sortTypeAsc = !sortTypeAsc;
                    sk.add(new RowSorter.SortKey(2, sortTypeAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));
                    sk.add(new RowSorter.SortKey(1, sortNameAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));

                }
                trs.setSortKeys(sk);
                table.setRowSorter(trs);
            }
        });
        table.setRowSorter(trs);
        return table;
    }

    /**
     * Befüllt die elementClassBox
     */
    private void fillElementClassBox() {
        elementClassBox.addItem(ModelElement.class, Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_AlleElementeArten"));
        elementClassBox.addSeparator(true);

        elementClassBox.addItem(Knoten.class, Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_AlleKnoten"));
        elementClassBox.addSeparator(true);
        for (int i = 0; i < ModelConstants.ALL_NODES.length; i++) {
            if (Modifier.isAbstract(ModelConstants.ALL_NODES[i].getModifiers())) {
                continue;
            }
            elementClassBox.addItem(ModelConstants.ALL_NODES[i], ModelConstants.getDisplayableName(ModelConstants.ALL_NODES[i]));
        }
        elementClassBox.addSeparator(true);
        elementClassBox.addItem(Kante.class, Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_AlleKanten"));
        elementClassBox.addSeparator(true);

        for (int i = 0; i < ModelConstants.ALL_EDGES.length; i++) {
            elementClassBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullForwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
            elementClassBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullBackwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
        }
        elementClassBox.setSelectedObject(ModelElement.class);
    }

    /**
     * Suchfelder leeren
     */
    private static final void removeSearchItems() {
        if (elementName.getEditor().getEditorComponent() instanceof JTextComponent) {
            ((JTextComponent) elementName.getEditor().getEditorComponent()).setText("");
        }
        if (elementName.getEditor().getEditorComponent() instanceof JTextComponent) {
            ((JTextComponent) elementDescription.getEditor().getEditorComponent()).setText("");
        }
        if (elementName.getEditor().getEditorComponent() instanceof JTextComponent) {
            ((JTextComponent) elementUserField.getEditor().getEditorComponent()).setText("");
        }
    }

    /**
     * Tooltips an wichtigste Elemente
     */
    private void addToolTips() {
        searchButton.setToolTipText(Tool3lgmConstants.getResString("SEARCH_DIALOG_TT_Button"));
        elementName.setToolTipText(Tool3lgmConstants.getResString("SEARCH_DIALOG_TT_Textfield"));
        elementUserField.setToolTipText(Tool3lgmConstants.getResString("SEARCH_DIALOG_TT_Textfield"));
        elementDescription.setToolTipText(Tool3lgmConstants.getResString("SEARCH_DIALOG_TT_Textfield"));
    }

    /**
     * an Button die Action hängen
     */
    private void addSearchButtonKeyListener() {
        InputMap keyMap = new ComponentInputMap(searchButton);
        keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "search");

        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("search", new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                callSearch(new ActionEvent(searchButton, 0, ""));

            }
        });

        SwingUtilities.replaceUIActionMap(searchButton, actionMap);
        SwingUtilities.replaceUIInputMap(searchButton, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
    }

    /**
     * Befüllt die modelBox
     */
    private void fillModelBox() {
        for (int i = 0; i < Tool3lgm.tool.getCollectionCount(); i++) {
            GDCollection gdcoll = Tool3lgm.tool.getCollection(i);
            modelBox.addItem(gdcoll, gdcoll.getName());
        }
        modelBox.setSelectedObject(Tool3lgm.tool.getSelectedGDCollection());
        fillSubModelBox();
    }

    /**
     * Befüllt die subModelBox
     */
    private void fillSubModelBox() {
        subModelBox.removeAllItems();
        GDCollection gdcoll = (GDCollection) modelBox.getSelectedObject();
        if (gdcoll == null) {
            return;
        }
        GraphDocument mainDoc = gdcoll.getMainGraphDocument();
        subModelBox.addItem(mainDoc, mainDoc.getTitle());
        for (int i = 0; i < gdcoll.getNumberOfSzenarios(); i++) {
            GraphDocument szen = gdcoll.getSzenario(i);
            subModelBox.addItem(szen, szen.getTitle());
        }
        subModelBox.setSelectedObject(mainDoc);
    }

    /**
     * UserFieldProperties Combobox und Checkboxauswahl
     */
    private void createUserFieldTypeComboBox() {

        // auswahlmodi für die benutzerdef. eigenschaften
        userFieldTypeComboBox = new AlphabeticalComboBox();
        userFieldTypeComboBox.addItem(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_all"));
        userFieldTypeComboBox.addItem(UserField.Style.CHECK_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.CHECK_BOX));
        userFieldTypeComboBox.addItem(UserField.Style.COMBO_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.COMBO_BOX));
        userFieldTypeComboBox.addItem(UserField.Style.HYPERLINK, CostingUtil.getDisplayableStyleName(UserField.Style.HYPERLINK));
        userFieldTypeComboBox.addItem(UserField.Style.CLASSIFICATION_NUMBER, CostingUtil.getDisplayableStyleName(UserField.Style.CLASSIFICATION_NUMBER));
        userFieldTypeComboBox.addItem(UserField.Style.MULTI_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.MULTI_LINE));
        userFieldTypeComboBox.addItem(UserField.Style.RADIO_BUTTON, CostingUtil.getDisplayableStyleName(UserField.Style.RADIO_BUTTON));
        userFieldTypeComboBox.addItem(UserField.Style.SEPARATOR, CostingUtil.getDisplayableStyleName(UserField.Style.SEPARATOR));
        userFieldTypeComboBox.addItem(UserField.Style.SINGLE_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.SINGLE_LINE));

        // auswahlmodi für die checkboxen
        checkBoxAuswahl = new JComboBox();
        checkBoxAuswahl.addItem(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_activated_deactivated"));
        checkBoxAuswahl.addItem(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_activated"));
        checkBoxAuswahl.addItem(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_deactivated"));

    }

    /**
     * @param str
     * @return
     */
    public String cleanName(final String str) {
        if (str == null) {
            return "";
        }
        return str.toLowerCase();
    }

    /**
	 * 
	 */
    public void showDialog() {
        setVisible(true);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        callSearch(e);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
     * .ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent e) {
        int[] selected = table.getSelectedRows();
        TableModel tablemodel = table.getModel();
        GraphDocument doc = Tool3lgm.tool.getSelectedDoc();
        if (doc == null) {
            return;
        }

        doc.deselectAll(false);
        for (int n = 0; n < selected.length; n++) {
            ElementContainer ec = (ElementContainer) tablemodel.getValueAt(selected[n], 1);
            if (ec == null) {
                return;
            }
            doc.getCollection().setActiveLayer(ec.getElement().layerFor());
            doc.addToSelection(ec, TransactionManager.STANDARD_PID);
        }
    }

    /*
     * Deaktiviere Checkboxauswahl, wenn nicht nach Checkboxen gesucht wird.
     */
    @Override
    public void itemStateChanged(final ItemEvent arg0) {

        if (userFieldTypeComboBox.getSelectedItem().equals(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_all")) || userFieldTypeComboBox.getSelectedObject().equals(UserField.Style.CHECK_BOX)) {
            checkBoxAuswahl.setEnabled(true);
        }

        else {
            checkBoxAuswahl.setEnabled(false);
        }
        // Checkboxmodus (Suche Alle/aktivierte/nicht aktivierte)
        if (checkBoxAuswahl.getSelectedIndex() > 0) {
            checkBoxMode = checkBoxAuswahl.getSelectedItem().equals(Tool3lgmConstants.getResString("SEARCH_DIALOG_USERFIELD_activated")) ? CHECKBOXMODE_ACTIVATED : CHECKBOXMODE_NOT_ACTIVATED;
        } else {
            checkBoxMode = CHECKBOXMODE_ALL;
        }

    }

    /**
     * ActionListener an die JCBs
     */
    private void addJCBListeners() {

        elementName.setEnterAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                callSearch(new ActionEvent(searchButton, 0, ""));

            }
        });

        elementName.setActionEvent(new ActionEvent(searchButton, 0, ""));

        elementDescription.setEnterAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!elementName.isPopupVisible()) {
                    callSearch(new ActionEvent(searchButton, 0, ""));
                }
            }
        });
        elementDescription.setActionEvent(new ActionEvent(searchButton, 0, ""));

        elementUserField.setEnterAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                callSearch(new ActionEvent(searchButton, 0, ""));

            }
        });
        elementUserField.setActionEvent(new ActionEvent(searchButton, 0, ""));

    }

    private void addCBListeners() {
        elementName_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ignoreCaseInName = elementName_cb.isSelected();
            }
        });
        elementDescription_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ignoreCaseInDescription = elementDescription_cb.isSelected();
            }
        });
        elementUserField_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ignoreCaseInUserField = elementUserField_cb.isSelected();
            }
        });

    }

    @Override
    public void windowActivated(final WindowEvent e) {
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        // Anforderung: Beim start sollen sollen Suchfelder leer sein, History aber aktiv
        removeSearchItems();

    }

    @Override
    public void windowClosing(final WindowEvent e) {
        // Anforderung: Beim start sollen sollen Suchfelder leer sein, History aber aktiv
        removeSearchItems();

    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }

}
