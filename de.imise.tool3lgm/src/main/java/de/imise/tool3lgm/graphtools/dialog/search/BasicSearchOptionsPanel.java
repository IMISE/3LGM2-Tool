package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

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
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.search.SearchOptions.UserFieldCheckBoxState;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.HistoryComboBox;

/**
 * @author AXS (23.09.2020)
 */
public abstract class BasicSearchOptionsPanel extends JPanel implements ItemListener {

    /** Input field for the search pattern for the element names */
    protected final HistoryComboBox elementName = new HistoryComboBox();

    /** Input field for the search pattern for the element descriptions */
    protected final HistoryComboBox elementDescription = new HistoryComboBox();

    /** Input field for for the search pattern for the element {@link UserField} names */
    protected final HistoryComboBox elementUserField = new HistoryComboBox();

    /** beinhaltet den Wert der oberen Konstanten */
    protected UserFieldCheckBoxState userFieldCheckBoxState = UserFieldCheckBoxState.CHECKBOXMODE_ALL;

    /** Checkbox für ignore case Bezeichnung */
    protected final JCheckBox checkNameCaseSensitive = createCaseSensitiveCheckBox(BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_NAME.is());

    /** Checkbox für ignore case Beschreibung */
    protected final JCheckBox checkDescriptionCaseSensitive = createCaseSensitiveCheckBox(BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_DESCRIPTION.is());

    /** Checkbox für ignore case Benutzerdef Eigenschaften */
    protected final JCheckBox checkUserFieldCaseSensitive = createCaseSensitiveCheckBox(BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_USERFIELDS.is());

    /** Checkbox Checkboxsuche */
    protected JComboBox<String> userFieldCheckBoxStateComboBox = new AlphabeticalComboBox();

    /** Typbox der benutzerdef. Eigenschaften wie Checkbox, Textfeld usw. */
    protected AlphabeticalComboBox userFieldStyleComboBox;

    /** Combobox Elementeart */
    protected final AlphabeticalComboBox elementClassBox = new AlphabeticalComboBox();

    /** Combobox Modell */
    protected final AlphabeticalComboBox modelBox = new AlphabeticalComboBox();

    /** Combobox Teilmodell */
    protected final AlphabeticalComboBox subModelBox = new AlphabeticalComboBox();

    /** Suchknopf */
    protected final JButton searchButton = new JButton();

    /** View that displays the search result */
    protected final SearchResultView resultTargetView;

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

    /**
     * @param layout
     */
    public BasicSearchOptionsPanel(final SearchResultView resultTargetView, final LayoutManager layout) {
        super(layout);
        this.resultTargetView = resultTargetView;
        // Selectboxen befüllen
        fillModelBox();
        addToolTips();

        addSearchButtonKeyListener();
        searchButton.setAction(searchActionDefault);
        elementClassBox.setAction(searchActionDefault);
        modelBox.setAction(searchActionWithUpdateSubmodelAndClassBoxes);
        subModelBox.setAction(searchActionDefault);
        addListeners();
    }

    /**
     * Creates a new JCheckbox wit the label "Case sensitive"
     *
     * @param selected inital seletion state
     * @return
     */
    public static JCheckBox createCaseSensitiveCheckBox(final boolean selected) {
        return new JCheckBox(getResString("SEARCH_DIALOG_USERFIELD_CaseSensitive"), selected);
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
     * Die zentrale Suchmethode die aufgerufen wird. Prinzip: Alle Elemente des Teilmodels landen in <code>searchSet</code> Nicht erfüllte
     * Suchkriterium werden herausgefiltert mittels <code>searchSet.remove</code>
     */
    public void callSearch() {
        callSearch(false);
    }

    /**
     * Die zentrale Suchmethode die aufgerufen wird. Prinzip: Alle Elemente des Teilmodels landen in <code>searchSet</code> Nicht erfüllte
     * Suchkriterium werden herausgefiltert mittels <code>searchSet.remove</code>
     *
     * @param refreshSubModelAndClassBox
     */
    private void callSearch(final boolean refreshSubModelAndClassBox) {
        HistoryComboBox.addToHistory(elementName);
        HistoryComboBox.addToHistory(elementUserField);
        HistoryComboBox.addToHistory(elementDescription);

        GraphDocument doc = (GraphDocument) subModelBox.getSelectedObject();
        if (refreshSubModelAndClassBox) {
            fillSubModelBox();
            fillElementClassBox();
        }

        SearchOptions searchOptions = getSearchOptions();
        resultTargetView.showResult(doc, searchOptions);

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
    private void fillElementClassBox() {

        elementClassBox.removeAllItems();
        elementClassBox.addItem(ModelElement.class, getResString("SEARCH_DIALOG_USERFIELD_AlleElementeArten"));
        elementClassBox.addSeparator(true);

        elementClassBox.addItem(Node.class, getResString("SEARCH_DIALOG_USERFIELD_AlleKnoten"));
        elementClassBox.addSeparator(true);
        GDCollection gdcoll = (GDCollection) modelBox.getSelectedObject();
        if (gdcoll != null) {
            MetaModel metaModel = gdcoll.getMetaModel();
            ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
            for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
                if (Modifier.isAbstract(elementClass.getModifiers())) {
                    continue;
                }
                elementClassBox.addItem(elementClass, elementsNameBuilder.getDisplayableFullName(elementClass));
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
    }

    /**
     * Suchfelder leeren
     */
    public final void removeSearchItems() {
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
    protected void createUserFieldTypeComboBox() {

        // auswahlmodi für die benutzerdef. eigenschaften
        userFieldStyleComboBox = new AlphabeticalComboBox();
        userFieldStyleComboBox.addItem(getResString("SEARCH_DIALOG_USERFIELD_all"));
        userFieldStyleComboBox.addItem(UserField.Style.CHECK_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.CHECK_BOX));
        userFieldStyleComboBox.addItem(UserField.Style.COMBO_BOX, CostingUtil.getDisplayableStyleName(UserField.Style.COMBO_BOX));
        userFieldStyleComboBox.addItem(UserField.Style.HYPERLINK, CostingUtil.getDisplayableStyleName(UserField.Style.HYPERLINK));
        userFieldStyleComboBox.addItem(UserField.Style.CLASSIFICATION_NUMBER, CostingUtil.getDisplayableStyleName(UserField.Style.CLASSIFICATION_NUMBER));
        userFieldStyleComboBox.addItem(UserField.Style.MULTI_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.MULTI_LINE));
        userFieldStyleComboBox.addItem(UserField.Style.RADIO_BUTTON, CostingUtil.getDisplayableStyleName(UserField.Style.RADIO_BUTTON));
        userFieldStyleComboBox.addItem(UserField.Style.SEPARATOR, CostingUtil.getDisplayableStyleName(UserField.Style.SEPARATOR));
        userFieldStyleComboBox.addItem(UserField.Style.SINGLE_LINE, CostingUtil.getDisplayableStyleName(UserField.Style.SINGLE_LINE));
        userFieldStyleComboBox.addItem(UserField.Style.ID, CostingUtil.getDisplayableStyleName(UserField.Style.ID));

        // auswahlmodi für die checkboxen
        userFieldCheckBoxStateComboBox = new JComboBox<>();
        userFieldCheckBoxStateComboBox.addItem(getResString("SEARCH_DIALOG_USERFIELD_activated_deactivated"));
        userFieldCheckBoxStateComboBox.addItem(getResString("SEARCH_DIALOG_USERFIELD_activated"));
        userFieldCheckBoxStateComboBox.addItem(getResString("SEARCH_DIALOG_USERFIELD_deactivated"));

    }

    /*
     * Deaktiviere Checkboxauswahl, wenn nicht nach Checkboxen gesucht wird.
     */
    @Override
    public void itemStateChanged(final ItemEvent arg0) {

        if (userFieldStyleComboBox.getSelectedItem().equals(getResString("SEARCH_DIALOG_USERFIELD_all")) || userFieldStyleComboBox.getSelectedObject().equals(UserField.Style.CHECK_BOX)) {
            userFieldCheckBoxStateComboBox.setEnabled(true);
        }

        else {
            userFieldCheckBoxStateComboBox.setEnabled(false);
        }
        // Checkboxmodus (Suche Alle/aktivierte/nicht aktivierte)
        if (userFieldCheckBoxStateComboBox.getSelectedIndex() > 0) {
            userFieldCheckBoxState = userFieldCheckBoxStateComboBox.getSelectedItem().equals(getResString("SEARCH_DIALOG_USERFIELD_activated")) ? UserFieldCheckBoxState.CHECKBOXMODE_ACTIVATED : UserFieldCheckBoxState.CHECKBOXMODE_NOT_ACTIVATED;
        } else {
            userFieldCheckBoxState = UserFieldCheckBoxState.CHECKBOXMODE_ALL;
        }

    }

    /**
     * ActionListener an die JCBs
     */
    private void addListeners() {
        elementName.setEnterAction(searchActionDefault);
        elementDescription.setEnterAction(searchActionDefault);
        elementUserField.setEnterAction(searchActionDefault);
        checkNameCaseSensitive.addActionListener(e -> BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_NAME.set(checkNameCaseSensitive.isSelected()));
        checkDescriptionCaseSensitive.addActionListener(e -> BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_DESCRIPTION.set(checkDescriptionCaseSensitive.isSelected()));
        checkUserFieldCaseSensitive.addActionListener(e -> BooleanProperty.OPTION_SEARCH_DIALOG_CASE_SENSITIVE_USERFIELDS.set(checkUserFieldCaseSensitive.isSelected()));
    }

    /**
     * @return
     */
    public SearchOptions getSearchOptions() {
        SearchOptions searchOptions = new SearchOptions();
        String inputStringName = elementName.getText();
        boolean caseSensitiveName = checkNameCaseSensitive.isSelected();
        searchOptions.setInputStringName(inputStringName, caseSensitiveName);
        String inputStringDescription = elementDescription.getText();
        boolean caseSensitiveDescription = checkDescriptionCaseSensitive.isSelected();
        searchOptions.setInputStringDescription(inputStringDescription, caseSensitiveDescription);
        String inputUserField = elementUserField.getText();
        boolean caseSensitiveuserField = checkUserFieldCaseSensitive.isSelected();
        searchOptions.setInputStringUserFields(inputUserField, caseSensitiveuserField);

        Object selectedElementClass = elementClassBox.getSelectedObject();
        searchOptions.searchedElementType = selectedElementClass instanceof Class<?> ? ((Class<?>) selectedElementClass).asSubclass(ModelElement.class) : ModelElement.class;

        Object userFieldStyle = userFieldStyleComboBox == null ? null : userFieldStyleComboBox.getSelectedObject();
        searchOptions.userFieldStyle = userFieldStyle != null && userFieldStyle instanceof Style ? (Style) userFieldStyle : null;

        searchOptions.userFieldCheckBoxState = userFieldCheckBoxState;

        return searchOptions;
    }

    /**
     * @param searchOptions
     */
    public void setSearchOptions(final SearchOptions searchOptions) {
        //        String inputStringName = searchOptions.getInputStringName();
        //        elementName.setSelectedItem(inputStringName);
        //        String inputStringDescription = searchOptions.getInputStringDescription();
        //        elementDescription.setSelectedItem(inputStringDescription);
        //        String inputStringUserFields = searchOptions.getInputStringUserFields();
        //        elementUserField.setSelectedItem(inputStringUserFields);
        boolean caseSensitiveName = searchOptions.isCaseSensitiveName();
        checkNameCaseSensitive.setSelected(caseSensitiveName);
        boolean caseSensitiveDescription = searchOptions.isCaseSensitiveDescription();
        checkDescriptionCaseSensitive.setSelected(caseSensitiveDescription);
        boolean caseSensitiveUserFields = searchOptions.isCaseSensitiveUserFields();
        checkUserFieldCaseSensitive.setSelected(caseSensitiveUserFields);

        elementClassBox.setSelectedItem(searchOptions.searchedElementType);

        userFieldStyleComboBox.setSelectedItem(searchOptions.userFieldStyle);

        userFieldCheckBoxStateComboBox.setSelectedItem(searchOptions.userFieldCheckBoxState);

    }

}
