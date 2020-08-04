package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Static.contextGenerator;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CHECK_BOX;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
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

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Alphabetical;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.HistoryComboBox;

public class SearchDialog extends JDialog implements ListSelectionListener, WindowListener, ItemListener {

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

    private enum CheckBoxSelectionMode {
        /** Konstante für Checkboxen Suchen (aktivierte und deaktivierte) */
        CHECKBOXMODE_ALL,
        /** Konstante für aktivierte Checkboxen */
        CHECKBOXMODE_ACTIVATED,
        /** Konstante für deaktivierte Checkboxen Suchen */
        CHECKBOXMODE_NOT_ACTIVATED;
    }

    /** beinhaltet den Wert der oberen Konstanten */
    private CheckBoxSelectionMode checkBoxMode = CheckBoxSelectionMode.CHECKBOXMODE_ALL;

    /** Checkbox für ignore case Bezeichnung */
    private final JCheckBox checkNameCaseSensitive = new JCheckBox(getResString("SEARCH_DIALOG_USERFIELD_CaseSensitive"), !SearchDialog.ignoreCaseInName);

    /** Checkbox für ignore case Beschreibung */
    private final JCheckBox checkDescriptionCaseSensitive = new JCheckBox(getResString("SEARCH_DIALOG_USERFIELD_CaseSensitive"), !SearchDialog.ignoreCaseInDescription);

    /** Checkbox für ignore case Benutzerdef Eigenschaften */
    private final JCheckBox checkUserFieldCaseSensitive = new JCheckBox(getResString("SEARCH_DIALOG_USERFIELD_CaseSensitive"), !SearchDialog.ignoreCaseInUserField);

    /** Checkbox Checkboxsuche */
    private JComboBox<String> checkBoxAuswahl = new AlphabeticalComboBox();

    /** Typbox der benutzerdef. Eigenschaften wie Checkbox, Textfeld usw. */
    private AlphabeticalComboBox userFieldTypeComboBox;

    /** Combobox Elementeart */
    private final AlphabeticalComboBox elementClassBox = new AlphabeticalComboBox();

    /** Combobox Modell */
    private final AlphabeticalComboBox modelBox = new AlphabeticalComboBox();

    /** Combobox Teilmodell */
    private final AlphabeticalComboBox subModelBox = new AlphabeticalComboBox();

    /** Suchknopf */
    private final JButton searchButton = new JButton();

    /** The defalut action that will only perform the search (where the action source is irrelevant) */
    private final Action searchActionDefault = new AbstractAction(getResString("SEARCH_DIALOG_USERFIELD_Button")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            callSearch(false);
        }
    };

    /** The action that will perform the search and refreshes the submodel and class boxes */
    private final Action searchActionWithUpdateSubmodelAndClassBoxes = new AbstractAction() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            callSearch(true);
        }
    };

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
        searchButton.setAction(searchActionDefault);
        elementClassBox.setAction(searchActionDefault);
        modelBox.setAction(searchActionWithUpdateSubmodelAndClassBoxes);
        subModelBox.setAction(searchActionDefault);

        setTitle(getResString("SEARCH_DIALOG_TITLE"));

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
        inputPane.add(new JLabel(getResString("SEARCH_DIALOG_USERFIELD_Modell")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(getResString("SEARCH_DIALOG_USERFIELD_Teilmodell")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(getResString("SEARCH_DIALOG_USERFIELD_Elementeart")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(getResString("SEARCH_DIALOG_USERFIELD_NAME")), constraints);
        constraints.gridy++;
        inputPane.add(new JLabel(getResString("SEARCH_DIALOG_USERFIELD_BESCHREIBUNG")), constraints);

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

        inputPane.add(checkNameCaseSensitive, constraints);
        constraints.gridy++;
        inputPane.add(checkDescriptionCaseSensitive, constraints);
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
        userFieldPanel.setBorder(BorderFactory.createTitledBorder(getResString("SEARCH_DIALOG_USERFIELD_benutzdef_eig")));

        JLabel userfieldproperty = new JLabel(getResString("SEARCH_DIALOG_USERFIELD_Attributtyp"));
        userfieldproperty.setToolTipText(getResString("SEARCH_DIALOG_USERFIELD_Attributtyp_TT"));
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
        JLabel containingText = new JLabel(getResString("SEARCH_DIALOG_USERFIELD_Text"));
        containingText.setToolTipText(getResString("SEARCH_DIALOG_USERFIELD_Text_TT"));
        userFieldPanel.add(containingText, constraints);

        constraints.gridx++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        userFieldPanel.add(elementUserField, constraints);
        constraints.gridx++;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 1;
        userFieldPanel.add(checkUserFieldCaseSensitive, constraints);

        // Zeile 3 Checkboxsuche
        constraints.gridx = 0;
        constraints.gridy++;
        JLabel checkboxFind = new JLabel(getResString("SEARCH_DIALOG_USERFIELD_CB_filter"));
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
        userFieldTypeComboBox.setSelectedItem(getResString("SEARCH_DIALOG_USERFIELD_all"));

        // Searchbutton
        constraints.gridy++;
        constraints.gridx--;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        inputPane.add(searchButton, constraints);

        // Selectboxen befüllen
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
     * @param comboBox
     * @param caseSensitive
     * @return
     */
    public Pattern getInputSearchPattern(final HistoryComboBox comboBox, final JCheckBox caseSensitive) {
        return getInputSearchPattern(comboBox, caseSensitive.isSelected());
    }

    /**
     * @return
     */
    public Pattern getUserFieldsSearchPattern() {
        return getInputSearchPattern(elementUserField, checkUserFieldCaseSensitive);
    }

    /**
     * @param comboBox
     * @param caseSensitive
     * @return
     */
    public static Pattern getInputSearchPattern(final HistoryComboBox comboBox, final boolean caseSensitive) {
        Object selectedItem = comboBox.getSelectedItem();
        if (selectedItem == null) {
            ComboBoxEditor editor = comboBox.getEditor();
            Component editorComponent = editor.getEditorComponent();
            if (editorComponent instanceof JTextComponent) {
                JTextComponent textEditorComponent = (JTextComponent) editorComponent;
                selectedItem = textEditorComponent.getText();
            }
            if (selectedItem == null) {
                return null;
            }
        }
        String value = String.valueOf(selectedItem);
        if (!caseSensitive) {
            value = toNonNullLowserCaseString(value);
        }
        value = value.replaceAll("\\*", ".*").replaceAll("\\?", ".");

        Pattern pattern = null;
        if (!value.equals("")) {
            try {
                pattern = Pattern.compile(value);
            } catch (PatternSyntaxException error) {
                Log.show(Log.FATAL, getResString("SEARCH_DIALOG_REGEXP_HINT") + "\n" + error, error);
            }
        }

        return pattern;
    }

    /**
     * @param str
     * @return
     */
    public static final String toNonNullLowserCaseString(final String str) {
        if (str == null) {
            return "";
        }
        return str.toLowerCase();
    }

    /**
     * @param doc
     * @param searchedElementType
     * @return all ElementContainer of the given type in the given {@link GraphDocument}
     */
    private static List<ElementContainer> getInitialTargetElements(final GraphDocument doc, final Class<? extends ModelElement> searchedElementType) {
        List<ElementContainer> initialTargetElements = doc.getElementContainers(searchedElementType, true);
        GDCollection gdcoll = doc.getCollection();
        GraphDocument mainDoc = gdcoll.getMainDoc();
        if (doc != mainDoc) {
            for (ElementContainer ec : mainDoc.getElementContainers(searchedElementType, true)) {
                if (ec.isUnique()) {
                    Alphabetical.insert(initialTargetElements, ec);
                }
            }
        }
        return initialTargetElements;
    }

    /**
     * @param pattern
     * @param string
     * @param caseSensitive
     * @return
     */
    private static final boolean matches(final Pattern pattern, String string, final boolean caseSensitive) {
        if (pattern != null) {
            if (!caseSensitive) {
                string = toNonNullLowserCaseString(string);
            }
            Matcher match = pattern.matcher(string);
            return match.find();
        }
        return true;
    }

    /**
     * @param pattern
     * @param ec
     * @param caseSensitive
     * @return
     */
    public static final boolean matchesName(final Pattern pattern, final ElementContainer ec, final boolean caseSensitive) {
        String matchingString = ec.getName();
        return matches(pattern, matchingString, caseSensitive);
    }

    /**
     * @param pattern
     * @param ec
     * @param caseSensitive
     * @return
     */
    public static final boolean matchesDescription(final Pattern pattern, final ElementContainer ec, final boolean caseSensitive) {
        String matchingString = ec.getDescription();
        return matches(pattern, matchingString, caseSensitive);
    }

    /**
     * @param doc
     * @return
     */
    public List<ElementContainer> getResult(final GraphDocument doc) {
        // wenn Groß-/KLeinschreibung ignorieren, dann wandle in kleine namen um
        Pattern patternName = getInputSearchPattern(elementName, checkNameCaseSensitive);
        Pattern patternDescription = getInputSearchPattern(elementDescription, checkDescriptionCaseSensitive);
        Pattern patternUserFields = getUserFieldsSearchPattern();
        return getResult(doc, patternName, patternDescription, patternUserFields);
    }

    /**
     * @param doc
     * @param namePatternSource
     */
    public static List<ElementContainer> getResult(final GraphDocument doc, final HistoryComboBox namePatternSource) {
        Pattern namePattern = SearchDialog.getInputSearchPattern(namePatternSource, false);
        return getResult(doc, ModelElement.class, namePattern, false, null, false, null, false, null, null);
    }

    /**
     * @param patternUserFields
     * @param ec
     * @param caseSensitive
     * @param searchUserFieldStyle if <code>null</code> then all userfield types are searched for
     *            matches otherwise only the specified type will be searched
     * @param checkBoxMode
     * @return
     */
    private static boolean matchesUserField(Pattern patternUserFields, final ElementContainer ec, final boolean caseSensitive, final UserField.Style searchUserFieldStyle, final CheckBoxSelectionMode checkBoxMode) {
        boolean searchAllUserFields = searchUserFieldStyle == null;
        if (patternUserFields == null && searchAllUserFields) {
            return true;
        }
        if (patternUserFields != null || searchUserFieldStyle.equals(CHECK_BOX)) {
            ModelElement me = ec.getElement();
            if (me.getUserFieldInputValueKeys().isEmpty()) {
                return false;
            }

            // patternUserFields muss gesetzt werden, sonst wird nie etwas removed, wenn
            //z.b. auf checkboxen eingeschränkt wird
            if (patternUserFields == null && !searchAllUserFields) {
                try {
                    patternUserFields = Pattern.compile(" ");
                } catch (PatternSyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            for (UserField userField : me.getUserFieldInputValueKeys()) {

                String userFieldInputValue = me.getUserFieldInputValue(userField);
                // im all-modus und im checkboxmodus muss auf TRUE/FALSE abgefragt werden
                // + zusätzlich muss label stimmen
                Style currentUserFieldStyle = userField.getStyle();
                boolean currentUserFieldIsCheckBox = CHECK_BOX.equals(currentUserFieldStyle);
                boolean isSearchCheckBoxUserFields = CHECK_BOX.equals(searchUserFieldStyle);
                if ((searchAllUserFields || isSearchCheckBoxUserFields) && currentUserFieldIsCheckBox) {
                    // -> Checkbox suchen + zusätzlich muss label stimmen
                    String userFieldName = userField.getName();
                    if (!caseSensitive) {
                        userFieldName = toNonNullLowserCaseString(userFieldName);
                    }
                    Matcher matchNameOfCheckBox = patternUserFields.matcher(userFieldName);
                    if (matchNameOfCheckBox.find()) {
                        if (checkBoxMode == CheckBoxSelectionMode.CHECKBOXMODE_ACTIVATED && userFieldInputValue.equals("true")) {
                            return true;
                        } else if (checkBoxMode == CheckBoxSelectionMode.CHECKBOXMODE_NOT_ACTIVATED && userFieldInputValue.equals("false")) {
                            return true;
                        } else if (checkBoxMode == CheckBoxSelectionMode.CHECKBOXMODE_ALL) {
                            return true;
                        }
                    }
                }
                // im allmodus und wenn der Attributtyp übereinstimmt muss im Inhalt gesucht werden
                if (searchAllUserFields || !isSearchCheckBoxUserFields && currentUserFieldStyle.equals(searchUserFieldStyle)) {
                    // -> keine Checkbox suchen, sondern Inhalte Punkte in Kommas umwandeln
                    if (Pattern.matches("[0-9]+\\.[0-9]+", userFieldInputValue)) {
                        userFieldInputValue = userFieldInputValue.replaceAll("\\.", ",");
                    }
                    userFieldInputValue = caseSensitive ? userFieldInputValue : toNonNullLowserCaseString(userFieldInputValue);
                    Matcher matchUserFieldValue = patternUserFields.matcher(userFieldInputValue);
                    if (matchUserFieldValue.find()) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    /**
     * @param ec
     * @param patternName
     * @param caseSensitiveName
     * @param patternDescription
     * @param caseSensitiveDescription
     * @param patternUserFields
     * @param caseSensitive
     * @param searchUserFieldStyle
     * @param checkBoxMode
     * @return
     */
    private static boolean matchesAnd(final ElementContainer ec, final Pattern patternName, final boolean caseSensitiveName, final Pattern patternDescription, final boolean caseSensitiveDescription, final Pattern patternUserFields,
            final boolean caseSensitive, final UserField.Style searchUserFieldStyle, final CheckBoxSelectionMode checkBoxMode) {
        if (!matchesName(patternName, ec, caseSensitiveName)) {
            return false;
        }
        if (!matchesDescription(patternDescription, ec, caseSensitiveDescription)) {
            return false;
        }
        if (!matchesUserField(patternUserFields, ec, caseSensitive, searchUserFieldStyle, checkBoxMode)) {
            return false;
        }
        return true;
    }

    /**
     * @param doc
     * @param patternName
     * @param patternDescription
     * @param patternUserFields
     * @return
     */
    private List<ElementContainer> getResult(final GraphDocument doc, final Pattern patternName, final Pattern patternDescription, final Pattern patternUserFields) {

        if (patternName == null && patternDescription == null && patternUserFields == null) {
            return new ArrayList<>();
        }

        boolean caseSensitiveName = checkNameCaseSensitive.isSelected();
        boolean caseSensitiveDescription = checkDescriptionCaseSensitive.isSelected();
        boolean caseSensitiveUserFields = checkUserFieldCaseSensitive.isSelected();
        Object userFieldType = userFieldTypeComboBox.getSelectedObject();
        UserField.Style userFieldStyle = userFieldType instanceof Style ? (Style) userFieldType : null;

        Class<? extends ModelElement> searchedElementType = (Class<? extends ModelElement>) elementClassBox.getSelectedObject();

        return getResult(doc, searchedElementType, patternName, caseSensitiveName, patternDescription, caseSensitiveDescription, patternUserFields, caseSensitiveUserFields, userFieldStyle, checkBoxMode);

    }

    /**
     * @param doc
     * @param searchedElementType
     * @param patternName
     * @param caseSensitiveName
     * @param patternDescription
     * @param caseSensitiveDescription
     * @param patternUserFields
     * @param caseSensitiveUserFields
     * @param userFieldStyle
     * @param checkBoxMode
     * @return
     */
    private static List<ElementContainer> getResult(final GraphDocument doc, final Class<? extends ModelElement> searchedElementType, final Pattern patternName, final boolean caseSensitiveName, final Pattern patternDescription,
            final boolean caseSensitiveDescription, final Pattern patternUserFields, final boolean caseSensitiveUserFields, final UserField.Style userFieldStyle, final CheckBoxSelectionMode checkBoxMode) {
        List<ElementContainer> searchSet = getInitialTargetElements(doc, searchedElementType);
        for (int i = searchSet.size() - 1; i >= 0; i--) {
            ElementContainer ec = searchSet.get(i);

            if (!matchesAnd(ec, patternName, caseSensitiveName, patternDescription, caseSensitiveDescription, patternUserFields, caseSensitiveUserFields, userFieldStyle, checkBoxMode)) {
                searchSet.remove(i);
                continue;
            }

        }

        return searchSet;
    }

    /**
     * Die zentrale Suchmethode die aufgerufen wird. Prinzip: Alle Elemente des Teilmodels landen in <code>searchSet</code> Nicht erfüllte
     * Suchkriterium werden herausgefiltert mittels <code>searchSet.remove</code>
     */
    private void callSearch() {
        callSearch(false);
    }

    /**
     * Die zentrale Suchmethode die aufgerufen wird. Prinzip: Alle Elemente des Teilmodels landen in <code>searchSet</code> Nicht erfüllte
     * Suchkriterium werden herausgefiltert mittels <code>searchSet.remove</code>
     *
     * @param refreshSubModelAndClassBox
     */
    private void callSearch(final boolean refreshSubModelAndClassBox) {

        // beim Aufruf des Fensters nicht suchen (listener feuern aber beim Öffnen des Fensters
        // bereits -> table null check)
        if (table == null) {
            return;
        }

        HistoryComboBox.addToHistory(elementName);
        HistoryComboBox.addToHistory(elementUserField);
        HistoryComboBox.addToHistory(elementDescription);

        GraphDocument doc = (GraphDocument) subModelBox.getSelectedObject();
        if (doc == null) {
            return;
        }
        if (refreshSubModelAndClassBox) {
            fillSubModelBox();
            fillElementClassBox();
        }

        List<ElementContainer> searchSet = getResult(doc);

        mod = (DefaultTableModel) table.getModel();
        int anzahl = mod.getRowCount();
        for (int r = 0; r < anzahl; r++) {
            mod.removeRow(0);
        }

        ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
        int rowCounter = 1;
        Object[] data = new Object[3];
        for (ElementContainer ec : searchSet) {
            data[0] = rowCounter;
            data[1] = ec;
            // data[2] = ec.getGraphDocument().getTitle();
            if (ec.getElement() instanceof Edge) {
                data[2] = elementsNameBuilder.getDisplayableName(ec.getElement()) + ": " + elementsNameBuilder.getFullForwardMetaAssociationName(ec.getElement().getClass().asSubclass(Edge.class));
            } else {
                data[2] = elementsNameBuilder.getDisplayableName(ec.getElement());
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
        mod.addColumn(getResString("SEARCH_DIALOG_USERFIELD_Col1"));
        mod.addColumn(getResString("SEARCH_DIALOG_USERFIELD_Col2"));
        mod.addColumn(getResString("SEARCH_DIALOG_USERFIELD_Col3"));
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
                        GraphDocument doc = (GraphDocument) subModelBox.getSelectedObject();
                        Static.showPropertyDialogOfLastSelected(doc);
                    }
                } else {
                    GraphDocument selectedDoc = (GraphDocument) subModelBox.getSelectedObject();
                    Static.setSelectedDoc(selectedDoc);
                    JPopupMenu jpm = contextGenerator.getSearchDialogContextMenu();
                    // refresh
                    jpm.show(table, e.getX(), e.getY());
                    Component[] comps = jpm.getComponents();
                    for (Component component : comps) {
                        if (component instanceof JMenuItem) {
                            JMenuItem jmi = (JMenuItem) component;
                            // Nur wenn nicht Eigenschaften: Suche starten
                            if (!jmi.getText().equals(getResString("eigenschaften"))) {
                                jmi.addActionListener(e1 -> SwingUtilities.invokeLater(() -> callSearch()));
                            }
                        }
                    }
                }
            }
        });

        // Sortierung umsetzen
        TableRowSorter<TableModel> trs = new TableRowSorter<>(table.getModel());
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
                List<RowSorter.SortKey> sk = new ArrayList<>();
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

        elementClassBox.removeAllItems();
        elementClassBox.addItem(ModelElement.class, getResString("SEARCH_DIALOG_USERFIELD_AlleElementeArten"));
        elementClassBox.addSeparator(true);

        elementClassBox.addItem(Node.class, getResString("SEARCH_DIALOG_USERFIELD_AlleKnoten"));
        elementClassBox.addSeparator(true);
        GDCollection gdcoll = (GDCollection) modelBox.getSelectedObject();
        MetaModel metaModel = gdcoll.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
        for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
            if (Modifier.isAbstract(elementClass.getModifiers())) {
                continue;
            }
            elementClassBox.addItem(elementClass, elementsNameBuilder.getDisplayableName(elementClass));
        }
        elementClassBox.addSeparator(true);
        elementClassBox.addItem(Edge.class, getResString("SEARCH_DIALOG_USERFIELD_AlleKanten"));
        elementClassBox.addSeparator(true);

        for (Class<? extends Edge> edgeClass : metaModel.allEdgesSet) {
            elementClassBox.addItem(edgeClass, elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass));
            elementClassBox.addItem(edgeClass, elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass));
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
        searchButton.setToolTipText(getResString("SEARCH_DIALOG_TT_Button"));
        elementName.setToolTipText(getResString("SEARCH_DIALOG_TT_Textfield"));
        elementUserField.setToolTipText(getResString("SEARCH_DIALOG_TT_Textfield"));
        elementDescription.setToolTipText(getResString("SEARCH_DIALOG_TT_Textfield"));
    }

    /**
     * an Button die Action hängen
     */
    private void addSearchButtonKeyListener() {
        //Buttons reagieren nromalerweise immer nur auf Space. Hier wird das so ersetzt, dass es auf Space und Enter reagiert
        String actionKeyStartSearch = "ACTION_KEY_START_SEARCH"; //beliebiger String! wird nur bebraucht, um zwischen der Keymap und der ActionMap zu mappen
        InputMap keyMap = new ComponentInputMap(searchButton);
        keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), actionKeyStartSearch);
        keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), actionKeyStartSearch);
        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put(actionKeyStartSearch, searchActionDefault);
        SwingUtilities.replaceUIActionMap(searchButton, actionMap);
        SwingUtilities.replaceUIInputMap(searchButton, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
    }

    /**
     * Befüllt die modelBox
     */
    private void fillModelBox() {
        for (int i = 0; i < Static.getCollectionCount(); i++) {
            GDCollection gdcoll = Static.getCollection(i);
            modelBox.addItem(gdcoll, gdcoll.getName());
        }
        modelBox.setSelectedObject(Static.getSelectedGDCollection());
        fillSubModelBox();
        fillElementClassBox();
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
        GraphDocument mainDoc = gdcoll.getMainDoc();
        subModelBox.addItem(mainDoc, mainDoc.getTitle());
        for (Szenario szen : gdcoll.getSzenarios()) {
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
        userFieldTypeComboBox.addItem(getResString("SEARCH_DIALOG_USERFIELD_all"));
        userFieldTypeComboBox.addItem(UserField.Style.CHECK_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.CHECK_BOX));
        userFieldTypeComboBox.addItem(UserField.Style.COMBO_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.COMBO_BOX));
        userFieldTypeComboBox.addItem(UserField.Style.HYPERLINK, CostingUtil.getDisplayableStyleName(UserField.Style.HYPERLINK));
        userFieldTypeComboBox.addItem(UserField.Style.CLASSIFICATION_NUMBER, CostingUtil.getDisplayableStyleName(UserField.Style.CLASSIFICATION_NUMBER));
        userFieldTypeComboBox.addItem(UserField.Style.MULTI_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.MULTI_LINE));
        userFieldTypeComboBox.addItem(UserField.Style.RADIO_BUTTON, CostingUtil.getDisplayableStyleName(UserField.Style.RADIO_BUTTON));
        userFieldTypeComboBox.addItem(UserField.Style.SEPARATOR, CostingUtil.getDisplayableStyleName(UserField.Style.SEPARATOR));
        userFieldTypeComboBox.addItem(UserField.Style.SINGLE_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.SINGLE_LINE));
        userFieldTypeComboBox.addItem(UserField.Style.ID, CostingUtil.getDisplayableStyleName(UserField.Style.ID));

        // auswahlmodi für die checkboxen
        checkBoxAuswahl = new JComboBox<>();
        checkBoxAuswahl.addItem(getResString("SEARCH_DIALOG_USERFIELD_activated_deactivated"));
        checkBoxAuswahl.addItem(getResString("SEARCH_DIALOG_USERFIELD_activated"));
        checkBoxAuswahl.addItem(getResString("SEARCH_DIALOG_USERFIELD_deactivated"));

    }

    /**
     *
     */
    public void showDialog() {
        setVisible(true);
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        int[] selected = table.getSelectedRows();
        TableModel tablemodel = table.getModel();
        GraphDocument selectedDoc = Static.getSelectedDoc();
        if (selectedDoc == null) {
            return;
        }

        selectedDoc.deselectAll(false);
        for (int n = 0; n < selected.length; n++) {
            ElementContainer ec = (ElementContainer) tablemodel.getValueAt(selected[n], 1);
            if (ec == null) {
                return;
            }
            selectedDoc.getCollection().setActiveLayer(ec.getElement().layerFor());
            selectedDoc.addToSelection(ec, TransactionManager.STANDARD_PID);
        }
    }

    /*
     * Deaktiviere Checkboxauswahl, wenn nicht nach Checkboxen gesucht wird.
     */
    @Override
    public void itemStateChanged(final ItemEvent arg0) {

        if (userFieldTypeComboBox.getSelectedItem().equals(getResString("SEARCH_DIALOG_USERFIELD_all")) || userFieldTypeComboBox.getSelectedObject().equals(UserField.Style.CHECK_BOX)) {
            checkBoxAuswahl.setEnabled(true);
        }

        else {
            checkBoxAuswahl.setEnabled(false);
        }
        // Checkboxmodus (Suche Alle/aktivierte/nicht aktivierte)
        if (checkBoxAuswahl.getSelectedIndex() > 0) {
            checkBoxMode = checkBoxAuswahl.getSelectedItem().equals(getResString("SEARCH_DIALOG_USERFIELD_activated")) ? CheckBoxSelectionMode.CHECKBOXMODE_ACTIVATED : CheckBoxSelectionMode.CHECKBOXMODE_NOT_ACTIVATED;
        } else {
            checkBoxMode = CheckBoxSelectionMode.CHECKBOXMODE_ALL;
        }

    }

    /**
     * ActionListener an die JCBs
     */
    private void addJCBListeners() {
        elementName.setEnterAction(searchActionDefault);
        elementDescription.setEnterAction(searchActionDefault);
        elementUserField.setEnterAction(searchActionDefault);
    }

    private void addCBListeners() {
        checkNameCaseSensitive.addActionListener(e -> ignoreCaseInName = !checkNameCaseSensitive.isSelected());
        checkDescriptionCaseSensitive.addActionListener(e -> ignoreCaseInDescription = !checkDescriptionCaseSensitive.isSelected());
        checkUserFieldCaseSensitive.addActionListener(e -> ignoreCaseInUserField = !checkUserFieldCaseSensitive.isSelected());
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
