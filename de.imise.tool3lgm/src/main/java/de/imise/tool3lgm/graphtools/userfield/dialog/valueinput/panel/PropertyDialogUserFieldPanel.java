package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.CHECKBOX_FALSE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.CHECKBOX_TRUE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.EMPTY_STRING;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.CHECK_BOX;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.COMBO_BOX;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.FORMULA;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.GROUP;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.HYPERLINK;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.ID;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.MULTI_LINE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.RADIO_BUTTON;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SEPARATOR;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.SINGLE_LINE;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style.TAB;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractIDError;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.panel.DisplayAndFixConsistencyErrorPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldList;
import de.imise.util.BrowseUtils;
import de.imise.util.Sys;
import de.imise.util.htmlxml.HTMLConverter;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextArea;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.component.text.NumberTextField;

/**
 * Panel für den Eigenschaftendialog von Elementen, in dem alle definierten
 * UserField des Elementes angezeigt werden. In ein <code>JScrollPane</code>
 * wird ein <code>JPanel</code> eingefügt. In dem <code>JPanel</code> werden die
 * Eingabemöglichkeiten für die UserField dargestellt. Die grafischen Elemente
 * werden durch <code>JComponent </code>, egal ob <code>JLabel</code>,
 * <code>JTextfield</code> oder <code>JComboBox</code> repräsentiert. Es wird
 * eine <code>ArrayList</code> angelegt, die Objekte vom Typ
 * <code>UserFieldEditorComponent</code> enthält. Eine
 * <code>UserFieldEditorComponent</code> enthält als Objektattribute ein
 * Attribut einer Element- bzw. Kantenklasse und die zugehörige GUI-Komponente.
 * Für die Datenübernahme ist die Methode <code>commit()</code> zuständig. Sie
 * prüft iterativ die Eingaben in GUI-komponenten in der <code>ArrayList</code>
 * und vergleicht Sie mit den bisherigen Werten der UserField. Bei einem
 * Unterschied wird das Tool3lgm-Kommando
 * <code>MODEL_ACTION_SET_USER_FIELD_VALUE</code> aufgerufen und somit der Wert
 * eines Attributes einer Element- bzw. Kanetenklasse geändert.
 *
 * @author Thomas Rudert, xhb, AXS
 */
public class PropertyDialogUserFieldPanel extends ElementDialogPanel implements DisplayAndFixConsistencyErrorPanel {

    /**
     * These boolean could be an User-Option. If <code>true</code> the Attibutes
     * in the PropertyDialogs will be displayed with the label and the editor
     * always in one line. If <code>false</code> they will be presented one
     * below the other.
     */
    private final boolean showAttributeLabelAndEditorSideBySide = true;

    /**
     * Default Insets and empty border spaces
     */
    protected static final int BORDER_INSETS = 5;

    /**
    *
    */
    protected static final int STANDARD_HORIZONTAL_INSETS = 10;

    /**
    *
    */
    protected static final int STANDARD_VERTICAL_INSETS = 5;

    /**
     *
     */
    private static final int DEFAULT_CONTRAINTS_ANCHOR = GridBagConstraints.CENTER;

    /**
     *
     */
    protected final List<UserFieldEditorComponent> fieldComponents = new ArrayList<>();

    /**
     *
     */
    private final PropertyDialogUserFieldPanelChangeListener changeHandler = new PropertyDialogUserFieldPanelChangeListener(this);

    /**
     *
     */
    protected final JPanel mainPanel;

    /**
     * @param propertyDialog
     */
    public PropertyDialogUserFieldPanel(final AbstractElementPropertyDialog propertyDialog, final UserFieldList tabDefinition) {
        super(propertyDialog);
        mainPanel = createMainPanel();
        create(tabDefinition);
    }

    /**
     * @return
     */
    protected GridBagConstraints getDefaultConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = DEFAULT_CONTRAINTS_ANCHOR;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weighty = 0d;
        constraints.weightx = 1d;
        return constraints;
    }

    /**
     * @param content
     * @return
     */
    protected JScrollPane getScrollPane(final JComponent content) {
        JScrollPane scrollPane = new JScrollPane(content);
        JScrollBar scrollbar = scrollPane.getVerticalScrollBar();
        scrollbar.setBlockIncrement(20);
        scrollbar.setUnitIncrement(10);
        scrollbar = scrollPane.getHorizontalScrollBar();
        scrollbar.setBlockIncrement(20);
        scrollbar.setUnitIncrement(10);
        return scrollPane;
    }

    /**
     * Liefert den Wert des UserFields für das ModelElement des Dialogs dieses
     * Panels. Bei allen UserFields außer Kennzahl-UserFields werden
     * EMPTY_STRINGS durch den echten Leerstring "" ersetzt.
     *
     * @param userField
     * @return
     */
    private String getFormattedValue(final UserField userField) {
        ModelElement me = getModelElement();
        String value = getUserFieldValue(me, userField, true);
        return value;
    }

    /**
     * Liefert den Wert des UserFields für das ModelElement des Dialogs dieses
     * Panels. Bei allen UserFields außer Kennzahl-UserFields werden
     * EMPTY_STRINGS durch den echten Leerstring "" ersetzt.
     *
     * @param me
     * @param userField
     * @param format
     * @return
     */
    public static String getUserFieldValue(final ModelElement me, final UserField userField, final boolean format) {
        String value = format ? userField.getFormattedValue(me, true) : me.getValue(userField);
        UserField.Style style = userField.getStyle();
        if (style != FORMULA) {
            if (value.equals(EMPTY_STRING)) {
                value = "";
            }
        }
        return value;
    }

    /**
     * Visualisiert die UserField mit ihren entsprechenden Style-Vorgaben im
     * <code>JPanel</code>.
     */
    protected final void create(final UserFieldList tabDefinition) {
        GridBagConstraints constraints = getDefaultConstraints();
        JPanel currentPanel = mainPanel;

        //Attributdefinitionen des GraphDocumentes holen
        for (UserField userField : tabDefinition) {
            if (userField.hasStyle(TAB)) {
                addTab(userField, constraints);
            } else if (userField.hasStyle(GROUP)) {
                currentPanel = addGroup(userField, constraints);
            } else if (userField.hasStyle(SEPARATOR)) {
                addSeparator(userField, currentPanel, constraints);
            } else {
                addAttribute(userField, currentPanel, constraints);
            }
        }
        addFillSpacePanel(mainPanel, constraints);
    }

    /**
     * @return the mainPanel with BridBagLayout in a scrollpane and with an
     *         empty border
     */
    protected JPanel createMainPanel() {
        setLayout(new BorderLayout());
        Border mainPanelBorder = BorderFactory.createEmptyBorder(BORDER_INSETS, BORDER_INSETS, BORDER_INSETS, BORDER_INSETS);
        setBorder(mainPanelBorder);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanelBorder = BorderFactory.createEmptyBorder(BORDER_INSETS, BORDER_INSETS, BORDER_INSETS, BORDER_INSETS);
        mainPanel.setBorder(mainPanelBorder);
        JScrollPane scrollPane = getScrollPane(mainPanel);
        add(scrollPane);
        return mainPanel;
    }

    /**
     * @param userField
     * @param constraints
     */
    protected void addTab(final UserField userField, final GridBagConstraints constraints) {
        String tabName = userField.getName();
        setName(tabName);
        addDescriptionLabel(userField, mainPanel, constraints);
    }

    /**
     * @param userField
     * @param constraints
     */
    protected JPanel addGroup(final UserField userField, final GridBagConstraints constraints) {
        JPanel panel = new JPanel(new GridBagLayout());
        String name = userField.getName();
        TitledBorder titledBorder = BorderFactory.createTitledBorder(name);
        Font titleFont = titledBorder.getTitleFont();
        titleFont = deriveFont(titleFont, 1, true, true);
        titledBorder.setTitleFont(titleFont);
        panel.setBorder(titledBorder);
        addDescriptionLabel(userField, panel, constraints);
        constraints.insets.top = STANDARD_HORIZONTAL_INSETS;
        mainPanel.add(panel, constraints);
        constraints.insets.top = 0;
        return panel;
    }

    /**
     * @param userField
     * @param panel
     * @param constraints
     */
    protected void addAttribute(final UserField userField, final JPanel panel, final GridBagConstraints constraints) {
        JComponent label = getTitleLabel(userField);
        JComponent editor = getEditor(userField);
        constraints.insets.top = STANDARD_HORIZONTAL_INSETS;
        if (showAttributeLabelAndEditorSideBySide) {
            constraints.gridwidth = 1;
            constraints.weightx = 0d;
            constraints.insets.right = STANDARD_VERTICAL_INSETS;
            panel.add(label, constraints);
            constraints.gridx++;
            constraints.weightx = 1d;
            constraints.insets.right = 0;
            panel.add(editor, constraints);
            constraints.gridy++;
            addDescriptionLabel(userField, panel, constraints);
            constraints.gridx = 0;
            constraints.gridwidth = 2;
        } else if (!userField.hasStyle(CHECK_BOX)) {
            panel.add(label, constraints);
            constraints.gridy++;
            addDescriptionLabel(userField, panel, constraints);
            constraints.insets.top = 0;
            panel.add(editor, constraints);
            constraints.gridy++;
        } else {
            panel.add(editor, constraints);
            constraints.gridy++;
            addDescriptionLabel(userField, panel, constraints);
        }
    }

    /**
     * @param field
     * @return
     */
    protected JLabel getTitleLabel(final UserField field) {
        Style style = field.getStyle();
        String name = field.getName();
        if (style == NUMBER || style == FORMULA) {
            String unit = field.getFormatUnit();
            if (unit != null) {
                name += " " + getResString("in") + " " + unit + " ";
            }
        }
        name = HTMLConverter.getTextAsHTMLLabelTextBold(name);
        JLabel label = new JLabel(name, showAttributeLabelAndEditorSideBySide ? SwingConstants.RIGHT : SwingConstants.LEFT);
        return label;
    }

    /**
     * @param userField
     * @param panel
     * @param constraints
     */
    protected boolean addDescriptionLabel(final UserField userField, final JPanel panel, final GridBagConstraints constraints) {
        String description = userField.getDescription();
        if (!Strings.isNullOrEmpty(description)) {
            if (userField.isShowDescriptionInDialog()) {
                constraints.insets.top = 0;
                constraints.weightx = 0d;
                panel.add(getDescriptionLabel(description), constraints);
                constraints.insets.top = STANDARD_HORIZONTAL_INSETS;
                constraints.weightx = 1d;
                constraints.gridy++;
                return true;
            }
        }
        return false;
    }

    /**
     * @param description
     * @return
     */
    protected JComponent getDescriptionLabel(final String description) {
        JTextArea textField = new JTextArea();
        textField.setLineWrap(true);
        textField.setWrapStyleWord(true);
        textField.setEnabled(false);
        Color textColor = UIManager.getColor("Label.foreground");
        textField.setDisabledTextColor(textColor);
        Color backgroundColor = UIManager.getColor("Label.background");
        textField.setBackground(backgroundColor);
        textField.setBorder(null);
        textField.setFont(getDescriptionFont());
        textField.setText(description);
        return textField;
    }

    /**
     * @return the font used to write descriptions
     */
    private Font getDescriptionFont() {
        Font font = UIManager.getFont("Label.font");
        font = font.deriveFont(Font.ITALIC);
        return font;
    }

    /**
     * @param userField
     * @param panel
     * @param constraints
     */
    protected void addSeparator(final UserField userField, final JPanel panel, final GridBagConstraints constraints) {
        String name = userField.getName();
        name = name == null ? "" : name.trim();
        constraints.insets.top = STANDARD_HORIZONTAL_INSETS;
        if (name.isEmpty()) {
            panel.add(new JSeparator(), constraints);
        } else {
            JPanel separatorPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = getDefaultConstraints();
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.SOUTH;
            separatorPanel.add(new JSeparator(), gbc);
            gbc.gridx++;
            gbc.weightx = 0d;
            gbc.anchor = DEFAULT_CONTRAINTS_ANCHOR;
            separatorPanel.add(getTitleLabel(userField), gbc);
            gbc.gridx++;
            gbc.weightx = 1d;
            gbc.anchor = GridBagConstraints.SOUTH;
            separatorPanel.add(new JSeparator(), gbc);
            panel.add(separatorPanel, constraints);
        }
        constraints.gridy++;
        addDescriptionLabel(userField, panel, constraints);
        constraints.insets.top = 0;
    }

    /**
     * @param font
     * @param addDiff
     * @param addbold
     * @param addIalic
     * @return
     */
    protected Font deriveFont(final Font font, final int addDiff, final boolean addbold, final boolean addIalic) {
        float size2d = font.getSize2D() + addDiff;
        Font returnFont = font.deriveFont(size2d);
        if (addbold && addIalic) {
            returnFont = returnFont.deriveFont(Font.BOLD + Font.ITALIC);
        } else if (addbold) {
            returnFont = returnFont.deriveFont(Font.BOLD);
        } else if (addIalic) {
            returnFont = returnFont.deriveFont(Font.ITALIC);
        }
        return returnFont;
    }

    /**
     * Für das übergebene Userfield wird eine 2 elementige Liste erzeugt. Die
     * erste Komponente ist das Label, das im Dialog für das UserField angezeigt
     * werden soll und die zweite Komponente ist der zugehörige Editor. Bei
     * Separatoren ist der Editor die Separator-Komponente und bei Formeln ist
     * der Editor deaktiviert.
     *
     * @param field
     * @return
     */
    protected JComponent getEditor(final UserField field) {
        String value = getFormattedValue(field);
        UserField.Style style = field.getStyle();
        JComponent editorComponent = null;
        if (style == SINGLE_LINE || style == ID) {
            ExtendedTextField textField = new ExtendedTextField(value);
            editorComponent = textField;
        } else if (style == MULTI_LINE) {
            ExtendedTextArea textArea = new ExtendedTextArea(3, 10);
            textArea.setFont(new ExtendedTextField().getFont());
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setText(value);
            editorComponent = new JScrollPane(textArea);
        } else if (style == COMBO_BOX) {
            AlphabeticalComboBox<String> comboBox = new AlphabeticalComboBox<>(true);
            boolean foundEntry = false;
            for (int i = 0; i < field.getListValuesCount(); i++) {
                String listValue = field.getListValueAt(i);
                comboBox.addObject(listValue);
                if (listValue.equals(value)) {
                    comboBox.setSelectedObject(listValue);
                    foundEntry = true;
                }
            }
            if (!foundEntry) {
                if (!Strings.isNullOrEmpty(value)) {
                    field.addListValue(value);
                    comboBox.addObject(value);
                    comboBox.setSelectedObject(value);
                    foundEntry = true;
                } else {
                    comboBox.setSelectedIndex(-1);
                }
            }
            editorComponent = comboBox;
        } else if (style == RADIO_BUTTON) {
            JPanel flowLayoutPanel = new JPanel();
            flowLayoutPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), ""));
            //den aktuellen Wert in die List-Values hinzufügen, falls er da aus irgendwelchen Gründen nicht drinsteht.
            if (!Strings.isNullOrEmpty(value) && !field.containsListValue(value)) {
                field.addListValue(value);
            }
            ButtonGroup group = new ButtonGroup();
            for (int i = 0; i < field.getListValuesCount(); i++) {
                Object listValue = field.getListValueAt(i);
                JRadioButton radioButton = new JRadioButton(listValue.toString());
                group.add(radioButton);
                flowLayoutPanel.add(radioButton);
                if (listValue.equals(value)) {
                    radioButton.setSelected(true);
                }
            }
            editorComponent = flowLayoutPanel;
        } else if (style == CHECK_BOX) {
            String label = showAttributeLabelAndEditorSideBySide ? "" : field.getName();
            label = HTMLConverter.getTextAsHTMLLabelTextBold(label);
            editorComponent = new JCheckBox(label, value.equals(CHECKBOX_TRUE));
        } else if (style == HYPERLINK) {
            final ExtendedTextField textField = new ExtendedTextField(value);
            JPanel hyperlinkPanel = new JPanel();
            hyperlinkPanel.setLayout(new BorderLayout());
            hyperlinkPanel.add(textField, BorderLayout.CENTER);
            final JButton button = getHyperlinkButton(textField);
            hyperlinkPanel.add(button, BorderLayout.EAST);
            editorComponent = hyperlinkPanel;
            //Kennzahlen:
        } else if (style == NUMBER) {
            // Wenn für die Kennzazhl ein gültiger Wert eingegeben ist, dann kann hier ein NumberTextField initialisiert werden.
            // Sollte das Fehlschlagen, muss ein normales JTextField hinzugefügt werden, das keine Wertformatierung vornimmt.
            NumberFormat javaNumberFormat = field.getJavaNumberFormat();
            // Kennzahlwerte in die Felder einfügen.
            NumberTextField numberTextField = NumberTextField.getNumberTextField(javaNumberFormat, field.isPositiveOnly());
            if (!UserField.isError(value)) {
                numberTextField.setValue(value);
            }
            editorComponent = numberTextField;
        } else if (style == FORMULA) {
            JTextField textField = new JTextField();
            textField.setEditable(false);
            ModelElement me = getModelElement();
            String formattedValue = field.getFormattedValue(me, true);
            textField.setText(formattedValue);
            editorComponent = textField;
        }
        addEditorComponentsToList(field, editorComponent);
        return editorComponent;
    }

    /**
     * Speichert für jedes UserField alle Editoren als Eingabefelder.
     *
     * @param userField
     * @param editorComponent
     */
    private void addEditorComponentsToList(final UserField userField, final JComponent editorComponent) {
        if (editorComponent != null) {
            JComponent realEditor = editorComponent;
            //die übergebene Komponente ist ein Scrollpane -> das ist nur bei Multiline-Textfeldern
            if (editorComponent instanceof JScrollPane) {
                //hole das Multiline-TextFeld und speichere dieses als echten Editor
                JViewport viewport = ((JScrollPane) editorComponent).getViewport();
                realEditor = (JComponent) viewport.getView();
                fieldComponents.add(new UserFieldEditorComponent(userField, realEditor));
                registerChangeListener(realEditor, userField);
                //die übergebene Komponente ist ein Panel (bei RadioButtons und bei Hyperlinks)
            } else if (editorComponent instanceof JPanel) {
                //alle Komponenten des Panels durchgehen
                for (int i = 0; i < editorComponent.getComponentCount(); i++) {
                    Component comp = editorComponent.getComponent(i);
                    //der JButton im Hyprlink-Panel darf nicht mitregistriert werden, daher nur JTextComponent und JRadioButtons
                    if (comp instanceof JRadioButton || comp instanceof JTextComponent) {
                        realEditor = (JComponent) comp;
                        fieldComponents.add(new UserFieldEditorComponent(userField, realEditor));
                        registerChangeListener(realEditor, userField);
                    }
                }
            } else {
                //die übergebene Kompoente muss selbst der Editor sein
                fieldComponents.add(new UserFieldEditorComponent(userField, realEditor));
                registerChangeListener(realEditor, userField);
            }
        }
    }

    /**
     * @param component
     * @param userField
     */
    private void registerChangeListener(final JComponent component, final UserField userField) {
        if (component instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) component;
            if (userField.hasStyle(NUMBER)) {
                ModelElement me = getModelElement();
                PropertyDialogUserFieldPanelNumberInputFocusListener inputFieldFocusListener = new PropertyDialogUserFieldPanelNumberInputFocusListener(changeHandler, me, userField);
                textComponent.addFocusListener(inputFieldFocusListener);
            } else if (textComponent.isEditable()) {
                textComponent.getDocument().addDocumentListener(changeHandler);
            }
        } else if (component instanceof AbstractButton) {
            AbstractButton radioButton = (AbstractButton) component;
            radioButton.addActionListener(changeHandler);
        } else if (component instanceof JComboBox<?>) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.addActionListener(changeHandler);
        }
    }

    /**
     * @param hyperlinkText
     * @return
     */
    private JButton getHyperlinkButton(final JTextComponent hyperlinkText) {
        // Der Button, der für einen Hyperlink das öffnen eines Browsers
        // ermöglicht.
        final JButton button = new JButton(new AbstractAction(">>") {

            @Override
            public void actionPerformed(final ActionEvent e) {
                String urlOrPath = hyperlinkText.getText().trim();
                if (!urlOrPath.isEmpty()) {
                    BrowseUtils.browse(urlOrPath);
                }
            }
        });
        //auch bei Doppelklick oder Klick mit STRG den Link öffnen
        hyperlinkText.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 1 && e.isControlDown() || e.getClickCount() > 1) {
                    button.doClick();
                }
            }
        });
        return button;
    }

    /**
     * @param panel2Fill
     * @param constraints
     */
    protected void addFillSpacePanel(final JPanel panel2Fill, final GridBagConstraints constraints) {
        constraints.gridy++;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        panel2Fill.add(new JPanel(), constraints);
    }

    @Override
    public void commit() {
        boolean changed = false;
        for (int i = 0; i < fieldComponents.size(); i++) {
            UserFieldEditorComponent userFieldEditorComponent = fieldComponents.get(i);
            UserField userField = userFieldEditorComponent.userField;
            JComponent editorComponent = userFieldEditorComponent.editorComponent;
            //Der Wert sollte sich nur geändert haben können, wenn diese Komponente auch den Focus hat.
            //Das ist besonders bei Kennzahlen wichtig, da diese, wenn sie nicht den Focus haben, immer
            //den formatierten Wert (also evtl. mit Einheit und im Gegensatz zum eigentlichen Eingabewert
            //mit einer anderen Anzahl von Nachkommastellen) anzeigen. Das würde hier als Änderung erkannt
            //werden und somit der formatierte Wert als Eingabewert gesetzt werden (was ei Einheiten zu
            //NUMBER_FORMAT_ERROR führt und ansonsten die Anzahl der angeblich eingegebenen Nachkommastellen
            //ändern kann.
            if (!editorComponent.hasFocus()) {
                continue;
            }

            UserField.Style style = userField.getStyle();
            String newValue = getNewValue(style, editorComponent);
            //wenn bei RadioButtons noch gar nichts gesetzt war, kann newValue null sein
            //bei UserFields die Formeln sind, kommt auch null zurück -> dann nichts setzen
            if (newValue != null) {
                ModelElement me = getModelElement();
                if (isNewValue(me, userField, newValue)) { //wenn sich wirklich was geändert hat
                    GraphDocument mainDoc = getMainDoc();
                    mainDoc.setUserFieldValue(me, userField, newValue, getTransactionID());
                    changed = true;
                }
            }
        }
        //wenn sich irgendwas geändert hat
        if (changed) {
            // wenn eine Kennzahl geändert wurde, wurde bei doc.exec() die Variable
            // reset aus dem Calculator auf true gesetzt.
            // Jetzt kann man einfach dem Calculator sagen, er soll alle
            // Kennzahlformeln neu berechnen, wenn
            GDCollection gdcoll = getCollection();
            gdcoll.getUserFieldDefinitions().initReset();
        }

    }

    @Override
    public void update() {
        updateFormulaValues();
    }

    /**
     * Aktualisiert die Anzeigewerte aller Formeln.
     */
    private void updateFormulaValues() {
        for (int i = 0; i < fieldComponents.size(); i++) {
            UserFieldEditorComponent userFieldEditorComponent = fieldComponents.get(i);
            UserField userField = userFieldEditorComponent.userField;
            if (userField.hasStyle(FORMULA)) {
                JTextField formulaTextField = (JTextField) userFieldEditorComponent.editorComponent;
                ModelElement me = getModelElement();
                String formattedValue = userField.getFormattedValue(me, true);
                formulaTextField.setText(formattedValue);
            }
        }
    }

    /**
     * Liefert <code>true</code>, wenn der übergebene <code>newValue</code> ein
     * anderer ist, als der beim ModelElement aktuell für das übergebenen
     * UserField gesetzte Wert.
     *
     * @param me
     * @param userField
     * @param newValue
     * @return
     */
    private static boolean isNewValue(final ModelElement me, final UserField userField, final String newValue) {
        String userFieldInputValue = me.getUserFieldInputValue(userField);
        if (userFieldInputValue.equals(EMPTY_STRING)) {
            userFieldInputValue = "";
        }
        return !newValue.equals(userFieldInputValue);
    }

    /**
     * Liefert in Abhängigkeit vom Style den aktuellen Wert der übergebenen
     * editorComponent.
     *
     * @param style
     * @param editorComponent
     * @return
     */
    private String getNewValue(final Style style, final JComponent editorComponent) {
        String newValue = null;
        if (style == SINGLE_LINE) {
            newValue = ((JTextComponent) editorComponent).getText();

        } else if (style == MULTI_LINE) {
            newValue = ((JTextComponent) editorComponent).getText();

        } else if (style == COMBO_BOX) {
            Object selectedItem = ((JComboBox<?>) editorComponent).getSelectedItem();
            if (selectedItem != null) {
                newValue = selectedItem.toString();
            }

        } else if (style == CHECK_BOX) {
            newValue = ((JCheckBox) editorComponent).isSelected() ? CHECKBOX_TRUE : CHECKBOX_FALSE;

        } else if (style == RADIO_BUTTON) {
            JRadioButton radioButton = (JRadioButton) editorComponent;
            //wenn nichts selektiert ist -> null zurück geben -> der Wert des UserFields wird nicht geändert
            newValue = radioButton.isSelected() ? radioButton.getText() : null;

        } else if (style == HYPERLINK) {
            newValue = ((JTextComponent) editorComponent).getText();

        } else if (style == ID) {
            newValue = ((JTextComponent) editorComponent).getText();

        } else if (style == NUMBER) {
            Object textFieldValue;
            NumberTextField textField = (NumberTextField) editorComponent;
            textFieldValue = textField.getText();

            newValue = textFieldValue == null ? "" : textFieldValue.toString();
        }
        return newValue;
    }

    /**
     * Für jedes UserField eine <code>Component</code>, die es anzeigt.
     */
    private class UserFieldEditorComponent {

        private final JComponent editorComponent;

        private final UserField userField;

        UserFieldEditorComponent(final UserField userField, final JComponent editorComponent) {
            this.editorComponent = editorComponent;
            this.userField = userField;
        }
    }

    @Override
    public ElementDialogPanel getResponsiblePanelForConsistencyError(final AbstractConsistencyError consistencyError) {
        ModelElement errorModelElement = consistencyError.getModelElement();
        ModelElement panelModelElement = getModelElement();
        if (panelModelElement == errorModelElement && consistencyError instanceof AbstractIDError) {
            return this;
        }
        return null;
    }

    /**
     * @param c
     */
    public static final void print(final GridBagConstraints c) {
        System.err.println("anchor = " + c.anchor);
        System.err.println("fill = " + c.fill);
        System.err.println("gridheight = " + c.gridheight);
        System.err.println("gridwidth = " + c.gridwidth);
        System.err.println("gridx = " + c.gridx);
        System.err.println("gridy = " + c.gridy);
        System.err.println("ipadx = " + c.ipadx);
        System.err.println("ipady = " + c.ipady);
        System.err.println("weightx = " + c.weightx);
        System.err.println("weighty = " + c.weighty);
        Sys.errm(1, 1, null);
    }

}
