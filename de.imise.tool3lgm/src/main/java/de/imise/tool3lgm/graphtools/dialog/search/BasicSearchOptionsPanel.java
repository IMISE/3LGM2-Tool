package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_DESCRIPTION;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_NAME;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_USERFIELDS;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.search.SearchOptions.UserFieldCheckBoxState;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.HistoryComboBox;

/**
 * This panel contains all GUI components whcih are needed to specify a search
 * in model files.
 *
 * @author N.N. (original SearchDialog); AXS (23.09.2020)
 */
public abstract class BasicSearchOptionsPanel extends JPanel implements ItemListener {

    /** Input field for the search pattern for the element names */
    protected final HistoryComboBox elementName = new HistoryComboBox();

    /** Input field for the search pattern for the element descriptions */
    protected final HistoryComboBox elementDescription = new HistoryComboBox();

    /**
     * Input field for for the search pattern for the element {@link UserField}
     * names
     */
    protected final HistoryComboBox elementUserField = new HistoryComboBox();

    /** beinhaltet den Wert der oberen Konstanten */
    protected UserFieldCheckBoxState userFieldCheckBoxState = UserFieldCheckBoxState.CHECKBOX_STATE_ALL;

    /** Checkbox für ignore case Bezeichnung */
    protected final JCheckBox checkNameCaseSensitive = createCaseSensitiveCheckBox(BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_NAME.is());

    /** Checkbox für ignore case Beschreibung */
    protected final JCheckBox checkDescriptionCaseSensitive = createCaseSensitiveCheckBox(BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_DESCRIPTION.is());

    /** Checkbox für ignore case Benutzerdef Eigenschaften */
    protected final JCheckBox checkUserFieldCaseSensitive = createCaseSensitiveCheckBox(BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_USERFIELDS.is());

    /** Checkbox Checkboxsuche */
    protected JComboBox<UserFieldCheckBoxState> userFieldCheckBoxStateComboBox;

    /** Typbox der benutzerdef. Eigenschaften wie Checkbox, Textfeld usw. */
    protected AlphabeticalComboBox<Style> userFieldStyleComboBox;

    /** Combobox Elementart */
    protected final AlphabeticalComboBox<Class<? extends ModelElement>> elementClassBox = new AlphabeticalComboBox<>();

    /** Combobox Modell */
    protected final AlphabeticalComboBox<GDCollection> modelBox = new AlphabeticalComboBox<>();

    /** Combobox Teilmodell */
    protected final AlphabeticalComboBox<GraphDocument> subModelBox = new AlphabeticalComboBox<>();

    /** Suchknopf */
    protected final JButton searchButton = new JButton();

    /** View that displays the search result */
    protected final SearchResultView resultTargetView;

    /**  */
    protected JLabel labelModel = new JLabel(getResString("SEARCH_DIALOG_model"));

    /**  */
    protected JLabel labelSubmodel = new JLabel(getResString("SEARCH_DIALOG_submodel"));

    /**  */
    protected JLabel labelElementType = new JLabel(getResString("SEARCH_DIALOG_element_type"));

    /**  */
    protected JLabel labelName = new JLabel(getResString("SEARCH_DIALOG_name"));

    /**  */
    protected JLabel labelDescription = new JLabel(getResString("SEARCH_DIALOG_description"));

    /**
     * The defalut action that will only perform the search (where the action
     * source is irrelevant)
     */
    private final Action searchActionDefault = new AbstractAction(getResString("SEARCH_DIALOG_search")) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            callSearch(false);
        }
    };

    /**
     * The action that will perform the search and refreshes the submodel and
     * class boxes
     */
    private final Action searchActionWithUpdateSubmodelAndClassBoxes = new AbstractAction() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            callSearch(true);
        }
    };

    /**
     * @param layout
     */
    public BasicSearchOptionsPanel(final SearchResultView resultTargetView, final LayoutManager layout) {
        super(layout);
        this.resultTargetView = resultTargetView;
        // Selectboxen befüllen
        fillModelBox();
        addSearchButtonKeyListener();
        searchButton.setAction(searchActionDefault);

        addToolTips(); //add tooltips AFTER adding the actions
        addComboboxListenersAndActions();
        addCheckBoxListeners();

    }

    /**
     * Creates a new JCheckbox wit the label "Case sensitive"
     *
     * @param selected inital seletion state
     * @return
     */
    public static JCheckBox createCaseSensitiveCheckBox(final boolean selected) {
        return new JCheckBox(getResString("SEARCH_DIALOG_case_sensitive"), selected);
    }

    /**
     * @param comboBox
     * @param caseSensitive
     * @return
     */
    public Pattern getInputSearchPattern(final HistoryComboBox comboBox, final JCheckBox caseSensitive) {
        return SearchFunctions.getInputSearchPattern(comboBox, caseSensitive.isSelected());
    }

    /**
     * @return
     */
    public Pattern getUserFieldsSearchPattern() {
        return getInputSearchPattern(elementUserField, checkUserFieldCaseSensitive);
    }

    /**
     * Die zentrale Suchmethode die aufgerufen wird. Prinzip: Alle Elemente des
     * Teilmodels landen in <code>searchSet</code> Nicht erfüllte Suchkriterium
     * werden herausgefiltert mittels <code>searchSet.remove</code>
     */
    public void callSearch() {
        callSearch(false);
    }

    /**
     * Die zentrale Suchmethode die aufgerufen wird. Prinzip: Alle Elemente des
     * Teilmodels landen in <code>searchSet</code> Nicht erfüllte Suchkriterium
     * werden herausgefiltert mittels <code>searchSet.remove</code>
     *
     * @param refreshSubModelAndClassBox
     */
    private void callSearch(final boolean refreshSubModelAndClassBox) {
        HistoryComboBox.addToHistory(elementName);
        HistoryComboBox.addToHistory(elementUserField);
        HistoryComboBox.addToHistory(elementDescription);

        GraphDocument doc = subModelBox.getSelectedObject();
        if (refreshSubModelAndClassBox) {
            fillSubModelBox();
            fillElementClassBox();
        }

        SearchOptions searchOptions = getSearchOptions(false);
        resultTargetView.showResult(doc, searchOptions);
    }

    /**
     * Updates the panel
     */
    public void refresh() {
        removeComboboxListeners();
        fillElementClassBox();
        addComboboxListenersAndActions();
    }

    /**
     * @return
     */
    public Component getResultViewComponent() {
        return resultTargetView.getResultViewComponent();
    }

    /**
     * Befüllt die elementClassBox
     */
    protected void fillElementClassBox() {
        elementClassBox.removeAllItems();
        elementClassBox.addObject(ModelElement.class, getResString("SEARCH_DIALOG_all_element_types"));
        elementClassBox.addSeparator(true);

        elementClassBox.addObject(Node.class, getResString("SEARCH_DIALOG_all_nodes"));
        elementClassBox.addSeparator(true);
        GDCollection gdcoll = modelBox.getSelectedObject();
        if (gdcoll != null) {
            MetaModel metaModel = gdcoll.getMetaModel();
            ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
            for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
                if (Modifier.isAbstract(elementClass.getModifiers())) {
                    continue;
                }
                if (!Static.isExpertMode() && metaModel.isOnlyExpertModeVisibleElementClass(elementClass)) {
                    continue;
                }
                elementClassBox.addObject(elementClass, elementsNameBuilder.getDisplayableFullName(elementClass));
            }
            elementClassBox.addSeparator(true);
            elementClassBox.addObject(Edge.class, getResString("SEARCH_DIALOG_all_edges"));
            elementClassBox.addSeparator(true);

            for (Class<? extends Edge> edgeClass : metaModel.allEdgesSet) {
                elementClassBox.addObject(edgeClass, elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass));
                elementClassBox.addObject(edgeClass, elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass));
            }
            elementClassBox.setSelectedObject(ModelElement.class);
        }
    }

    /**
     * Suchfelder leeren
     */
    public final void removeSearchItems() {
        elementName.clearText();
        elementDescription.clearText();
        elementUserField.clearText();
    }

    /**
     * ToolTips an wichtigste Elemente
     */
    private void addToolTips() {
        setToolTip(searchButton, "SEARCH_DIALOG_TOOLTIP_search");
        setToolTip(elementName, "SEARCH_DIALOG_TOOLTIP_pattern");
        setToolTip(elementUserField, "SEARCH_DIALOG_TOOLTIP_pattern");
        setToolTip(elementDescription, "SEARCH_DIALOG_TOOLTIP_pattern");
    }

    /**
     * @param component
     * @param toolTipResKeySource
     */
    public static final void setToolTip(final JComponent component, final Object toolTipResKeySource) {
        String toolTip = Tool3lgmConstants.getResString(toolTipResKeySource);
        component.setToolTipText(toolTip);
    }

    /**
     * an Button die Action hängen
     */
    private void addSearchButtonKeyListener() {
        //Buttons reagieren nromalerweise immer nur auf Space. Hier wird das so ersetzt, dass es auf Space und Enter reagiert
        String actionKeyStartSearch = "ACTION_KEY_START_SEARCH"; //beliebiger String! wird nur gebraucht, um zwischen der Keymap und der ActionMap zu mappen
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
            modelBox.addObject(gdcoll);
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
        GDCollection gdcoll = modelBox.getSelectedObject();
        if (gdcoll == null) {
            return;
        }
        GraphDocument mainDoc = gdcoll.getMainDoc();
        subModelBox.addObject(mainDoc, mainDoc.getTitle());
        for (Szenario szen : gdcoll.getSzenarios()) {
            subModelBox.addObject(szen);
        }
        subModelBox.setSelectedObject(mainDoc);
    }

    /**
     * UserFieldProperties Combobox und Checkboxauswahl
     */
    protected void createUserFieldTypeComboBox() {

        // Auswahlmodi für die benutzerdef. Eigenschaften
        userFieldStyleComboBox = new AlphabeticalComboBox<>();
        userFieldStyleComboBox.addObject(null, getResString("SEARCH_DIALOG_USERFIELD_type_all"));
        for (Style style : Style.values()) {
            userFieldStyleComboBox.addObject(style);
        }
        userFieldStyleComboBox.setSelectedIndex(0);

        // Auswahlmodi für die Checkboxen
        userFieldCheckBoxStateComboBox = new JComboBox<>();
        for (UserFieldCheckBoxState state : UserFieldCheckBoxState.values()) {
            userFieldCheckBoxStateComboBox.addItem(state);
        }

        userFieldStyleComboBox.addItemListener(this);
        userFieldCheckBoxStateComboBox.addItemListener(this);
    }

    /*
     * Deaktiviere Checkboxauswahl, wenn nicht nach Checkboxen gesucht wird.
     */
    @Override
    public void itemStateChanged(final ItemEvent arg0) {
        if (userFieldStyleComboBox != null) {
            Object selectedUserFieldStyle = userFieldStyleComboBox.getSelectedObject();
            boolean userFieldCheckBoxStateEnabledState = selectedUserFieldStyle == null || selectedUserFieldStyle == UserField.Style.CHECK_BOX;
            userFieldCheckBoxStateComboBox.setEnabled(userFieldCheckBoxStateEnabledState);
            // Checkboxmodus (Suche Alle/aktivierte/nicht aktivierte)
            int userFieldCheckBoxStateSelectedIndex = userFieldCheckBoxStateComboBox.getSelectedIndex();
            if (userFieldCheckBoxStateSelectedIndex >= 0) {
                userFieldCheckBoxState = userFieldCheckBoxStateComboBox.getItemAt(userFieldCheckBoxStateSelectedIndex);
            } else {
                userFieldCheckBoxState = UserFieldCheckBoxState.CHECKBOX_STATE_ALL;
            }
        }
        callSearch();
    }

    ////////////////////////////////////////////////
    // Add / Remove ActionListener + ItemListener //
    ////////////////////////////////////////////////

    /**
     * Adds the ActionListener to the case sensitive CheckBoxes which save the
     * cureent state in the {@link UserProperties} and start the search.
     */
    private void addCheckBoxListeners() {
        checkNameCaseSensitive.addActionListener(e -> saveCheckBoxStateInUserPropertiesAndCallSearch(checkNameCaseSensitive, OPTION_SEARCH_DIALOG_CASE_SENSITIVE_NAME));
        checkDescriptionCaseSensitive.addActionListener(e -> saveCheckBoxStateInUserPropertiesAndCallSearch(checkDescriptionCaseSensitive, OPTION_SEARCH_DIALOG_CASE_SENSITIVE_DESCRIPTION));
        checkUserFieldCaseSensitive.addActionListener(e -> saveCheckBoxStateInUserPropertiesAndCallSearch(checkUserFieldCaseSensitive, OPTION_SEARCH_DIALOG_CASE_SENSITIVE_USERFIELDS));
    }

    /**
     * @param checkbox
     * @param booleanProperty
     */
    private void saveCheckBoxStateInUserPropertiesAndCallSearch(final JCheckBox checkbox, final BooleanProperty booleanProperty) {
        booleanProperty.set(checkbox.isSelected());
        callSearch();
    }

    private void addComboboxListenersAndActions() {
        setComboboxListeners(false);
    }

    private void removeComboboxListeners() {
        setComboboxListeners(true);
    }

    /**
     * Adds or removes the ItemListener and Action to or from the
     * HistoryComboBoxes
     *
     * @param delete If <code>true</code> all listeners are removed. If
     *            <code>false</code> a change will start a search.
     */
    private void setComboboxListeners(final boolean remove) {
        Action enterAction = remove ? null : searchActionDefault;
        elementName.setEnterAction(enterAction);
        elementDescription.setEnterAction(enterAction);
        elementUserField.setEnterAction(enterAction);
        if (remove) {
            elementName.removeItemListener(this);
            elementDescription.removeItemListener(this);
            elementUserField.removeItemListener(this);
            if (userFieldStyleComboBox != null) {
                userFieldStyleComboBox.removeItemListener(this);
                userFieldCheckBoxStateComboBox.removeItemListener(this);
            }
            elementClassBox.setAction(null);
            modelBox.setAction(null);
            subModelBox.setAction(null);
        } else {
            elementName.addItemListener(this);
            elementDescription.addItemListener(this);
            elementUserField.addItemListener(this);
            if (userFieldStyleComboBox != null) {
                userFieldStyleComboBox.addItemListener(this);
                userFieldCheckBoxStateComboBox.addItemListener(this);
            }
            elementClassBox.setAction(searchActionDefault);
            modelBox.setAction(searchActionWithUpdateSubmodelAndClassBoxes);
            subModelBox.setAction(searchActionDefault);
        }
    }

    ///////////////////
    // SearchOptions //
    ///////////////////

    /**
     * @param withHistory if <code>true</code> the fields that store the history
     *            of the {@link HistoryComboBox}es will be filled too
     * @return the SearchOptions object that represent the current state of all
     *         gui input fields
     */
    public SearchOptions getSearchOptions(final boolean withHistory) {
        SearchOptions searchOptions = new SearchOptions();
        searchOptions.inputStringName = elementName.getText();
        searchOptions.caseSensitiveName = checkNameCaseSensitive.isSelected();

        searchOptions.inputStringDescription = elementDescription.getText();
        searchOptions.caseSensitiveDescription = checkDescriptionCaseSensitive.isSelected();

        searchOptions.inputStringUserFields = elementUserField.getText();
        searchOptions.caseSensitiveUserFields = checkUserFieldCaseSensitive.isSelected();

        Object selectedElementClass = elementClassBox.getSelectedObject();
        searchOptions.searchedElementType = selectedElementClass instanceof Class<?> ? ((Class<?>) selectedElementClass).asSubclass(ModelElement.class) : ModelElement.class;

        Object userFieldStyle = userFieldStyleComboBox == null ? null : userFieldStyleComboBox.getSelectedObject();
        searchOptions.userFieldStyle = userFieldStyle != null && userFieldStyle instanceof Style ? (Style) userFieldStyle : null;

        searchOptions.userFieldCheckBoxState = userFieldCheckBoxState;

        if (withHistory) {
            searchOptions.inputHistoryName = elementName.getHistory();
            searchOptions.inputHistoryDescription = elementDescription.getHistory();
            searchOptions.inputHistoryUserFields = elementUserField.getHistory();
        }

        return searchOptions;
    }

    /**
     * @param searchOptions
     */
    public void restoreSearchOptions(final SearchOptions searchOptions) {
        //we have to remove the listeners to prevent updates/Nullpointer
        //during the restore process
        removeComboboxListeners();
        checkNameCaseSensitive.setSelected(searchOptions.caseSensitiveName);
        checkDescriptionCaseSensitive.setSelected(searchOptions.caseSensitiveDescription);
        checkUserFieldCaseSensitive.setSelected(searchOptions.caseSensitiveUserFields);
        elementClassBox.setSelectedObject(searchOptions.searchedElementType);
        if (userFieldStyleComboBox != null) {
            userFieldStyleComboBox.setSelectedObject(searchOptions.userFieldStyle);
        }
        if (userFieldCheckBoxStateComboBox != null) {
            userFieldCheckBoxStateComboBox.setSelectedItem(searchOptions.userFieldCheckBoxState);
        }
        elementName.setHistory(searchOptions.inputHistoryName);
        elementDescription.setHistory(searchOptions.inputHistoryDescription);
        elementUserField.setHistory(searchOptions.inputHistoryUserFields);
        //readd the listeners to the comboboxes
        addComboboxListenersAndActions();
    }

}
