package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.UserField.CHECKBOX_FALSE;
import static de.imise.tool3lgm.graphtools.userfield.UserField.CHECKBOX_TRUE;
import static de.imise.tool3lgm.graphtools.userfield.UserField.EMPTY_STRING;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CHECK_BOX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER_FORMULA;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.COMBO_BOX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.HYPERLINK;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.ID;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.MULTI_LINE;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.RADIO_BUTTON;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.SEPARATOR;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.SINGLE_LINE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.util.BrowseUtils;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextArea;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.component.text.NumberTextField;

/**
 * Panel für den Eigenschaftendialog von Elementen, in dem alle definierten UserField des Elementes angezeigt werden. In ein <code>JScrollPane</code>
 * wird ein <code>JPanel</code> eingefügt. In dem <code>JPanel</code> werden die Eingabemöglichkeiten für die UserField dargestellt. Die grafischen
 * Elemente werden durch <code>JComponent </code>, egal ob <code>JLabel</code>, <code>JTextfield</code> oder <code>JComboBox</code> repräsentiert. Es
 * wird eine <code>ArrayList</code> angelegt, die Objekte vom Typ <code>UserFieldEditorComponent</code> enthält. Eine
 * <code>UserFieldEditorComponent</code> enthält als Objektattribute ein Attribut einer Element- bzw. Kantenklasse und die zugehörige GUI-Komponente.
 * Für die Datenübernahme ist die Methode <code>commit()</code> zuständig. Sie prüft iterativ die Eingaben in GUI-komponenten in der
 * <code>ArrayList</code> und vergleicht Sie mit den bisherigen Werten der UserField. Bei einem Unterschied wird das Tool3lgm-Kommando
 * <code>MODEL_ACTION_SET_USER_FIELD_VALUE</code> aufgerufen und somit der Wert eines Attributes einer Element- bzw. Kanetenklasse geändert.
 *
 * @author Thomas Rudert, xhb, AXS
 */
public class PropertyDialogUserFieldPanel extends ElementDialogPanel {

    private final List<UserFieldEditorComponent> fieldComponents = new ArrayList<>();

    private final PropertyDialogUserFieldPanelChangeListener changeHandler = new PropertyDialogUserFieldPanelChangeListener(this);

    /**
     * @param pd
     */
    public PropertyDialogUserFieldPanel(final ElementPropertyDialog pd) {
        super(pd);
        create();
    }

    private GridBagConstraints getDefaultConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weighty = 0d;
        constraints.weightx = 1d;
        constraints.insets = new Insets(1, 1, 1, 0);
        return constraints;
    }

    private JScrollPane getScrollPane(final JComponent content) {
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
     * Liefert den Wert des UserFields für das ModelElement des Dialogs dieses Panels.
     * Bei allen UserFields außer Kennzahl-UserFields werden EMPTY_STRINGS durch den
     * echten Leerstring "" ersetzt.
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
     * Liefert den Wert des UserFields für das ModelElement des Dialogs dieses Panels.
     * Bei allen UserFields außer Kennzahl-UserFields werden EMPTY_STRINGS durch den
     * echten Leerstring "" ersetzt.
     *
     * @param me
     * @param userField
     * @param format
     * @return
     */
    public static String getUserFieldValue(final ModelElement me, final UserField userField, final boolean format) {
        String value = format ? userField.getFormattedValue(me, true) : userField.getValue(me);
        UserField.Style style = userField.getStyle();
        if (style != CLASSIFICATION_NUMBER_FORMULA) {
            if (value.equals(EMPTY_STRING)) {
                value = "";
            }
        }
        return value;
    }

    /**
     * Visualisiert die UserField mit ihren entsprechenden Style-Vorgaben im <code>JPanel</code>.
     */
    private void create() {
        setLayout(new BorderLayout());
        Border mainPanelBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(mainPanelBorder);
        JPanel mp = new JPanel(new GridBagLayout());
        mainPanelBorder = BorderFactory.createEmptyBorder(0, 5, 5, 5);
        mp.setBorder(mainPanelBorder);
        JScrollPane sp = getScrollPane(mp);
        add(sp);

        GridBagConstraints constraints = getDefaultConstraints();

        //Attributdefinitionen des GraphDocumentes holen
        UserFieldDefinitions definitions = doc.getUserFieldDefinitions();
        ModelElement me = getModelElement();
        Class<? extends ModelElement> meClass = me.getClass();

        for (UserField field : definitions.getUserFields(meClass)) {
            List<JComponent> labelAndEditor = createLabelAndEditor(field);
            JComponent label = labelAndEditor.get(0);
            JComponent editor = labelAndEditor.get(1);
            constraints.insets.top = 5;
            mp.add(label, constraints);
            constraints.insets.top = 0;
            constraints.gridy++;
            mp.add(editor, constraints);
            constraints.gridy += constraints.gridheight;
        }
        addFillSpacePanel(mp, constraints);
    }

    /**
     * Für das übergebene Userfield wird eine 2 elementige Liste erzeugt. Die erste Komponente ist das
     * Label, das im Dialog für das UserField angezeigt werden soll und die zweite Komponente ist der
     * zugehörige Editor. Bei Separatoren ist der Editor die Separator-Komponente und bei Formeln ist
     * der Editor deaktiviert.
     *
     * @param field
     * @return
     */
    private List<JComponent> createLabelAndEditor(final UserField field) {
        String value = getFormattedValue(field);
        UserField.Style style = field.getStyle();
        JLabel label = getLabel(field);
        JComponent editorComponent = null;
        if (style == SEPARATOR) {
            editorComponent = new JSeparator();
        } else if (style == SINGLE_LINE || style == ID) {
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
            AlphabeticalComboBox comboBox = new AlphabeticalComboBox(true);
            boolean foundEntry = false;
            for (int i = 0; i < field.getListValuesCount(); i++) {
                Object listValue = field.getListValueAt(i);
                comboBox.addItem(listValue);
                if (listValue.equals(value)) {
                    comboBox.setSelectedItem(listValue);
                    foundEntry = true;
                }
            }
            if (!foundEntry) {
                if (value.length() > 0) {
                    field.addListValue(value);
                    comboBox.addItem(value);
                    comboBox.setSelectedItem(value);
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
            if (!field.containsListValue(value)) {
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
            editorComponent = new JCheckBox(field.getName(), value.equals(CHECKBOX_TRUE));
        } else if (style == HYPERLINK) {
            final ExtendedTextField textField = new ExtendedTextField(value);
            JPanel hyperlinkPanel = new JPanel();
            hyperlinkPanel.setLayout(new BorderLayout());
            hyperlinkPanel.add(textField, BorderLayout.CENTER);
            final JButton button = getHyperlinkButton(textField);
            hyperlinkPanel.add(button, BorderLayout.EAST);
            editorComponent = hyperlinkPanel;
            //Kennzahlen:
        } else if (style == CLASSIFICATION_NUMBER) {
            // Wenn für die Kennzazhl ein gültiger Wert eingegeben ist, dann kann hier ein NumberTextField initialisiert werden.
            // Sollte das Fehlschlagen, muss ein normales JTextField hinzugefügt werden, das keine Wertformatierung vornimmt.
            NumberFormat numberFormat = field.getNumberFormat();
            // Kennzahlwerte in die Felder einfügen.
            NumberTextField numberTextField = NumberTextField.getNumberTextField(numberFormat, field.isPositiveOnly());
            if (!UserField.isError(value)) {
                numberTextField.setValue(value);
            }
            editorComponent = numberTextField;
        } else if (style == CLASSIFICATION_NUMBER_FORMULA) {
            JTextField textField = new JTextField();
            textField.setEditable(false);
            String formattedValue = field.getFormattedValue(getModelElement(), true);
            textField.setText(formattedValue);
            editorComponent = textField;
        }
        addEditorComponentsToList(field, editorComponent);
        return ImmutableList.of(label, editorComponent);
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

    private void registerChangeListener(final JComponent component, final UserField userField) {
        if (component instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) component;
            if (userField.hasStyle(CLASSIFICATION_NUMBER)) {
                PropertyDialogUserFieldPanelNumberInputFocusListener inputFieldFocusListener = new PropertyDialogUserFieldPanelNumberInputFocusListener(changeHandler, getModelElement(), userField);
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

    private void addFillSpacePanel(final JPanel panel2Fill, final GridBagConstraints constraints) {
        constraints.gridy++;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        panel2Fill.add(new JPanel(), constraints);
    }

    private JLabel getLabel(final UserField field) {
        Style style = field.getStyle();
        if (style == CLASSIFICATION_NUMBER || style == CLASSIFICATION_NUMBER_FORMULA) {
            String unit = field.getFormatUnit();
            StringBuilder einheit = new StringBuilder("");
            if (unit != null) {
                einheit.append(" ");
                einheit.append(getResString("in"));
                einheit.append(" ");
                einheit.append(unit);
                einheit.append(" ");
            }
            String htmlString = toHTML(field.getName() + einheit, field.getDescription());
            return new JLabel(htmlString);
        }
        return new JLabel(toHTML(field.getName(), field.getDescription()));
    }

    /**
     * Erzeugt einen HTML-String, bei dem der übergebenen <code>fieldName</code> fett in der 1. Zeile und
     * darunter die übergebene <code>fieldDescription</code> steht.
     *
     * @param fieldName
     * @param fieldDescription
     * @return
     */
    private String toHTML(final String fieldName, final String fieldDescription) {
        String htmlString = "<HTML><B>" + fieldName + "</B><BR>" + fieldDescription + "</HTML>";
        return htmlString;
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
            ModelElement me = getModelElement();
            //wenn bei RadioButtons noch gar nichts gesetzt war, kann newValue null sein
            //bei UserFields die Formeln sind, kommt auch null zurück -> dann nichts setzen
            if (newValue != null) {
                if (isNewValue(me, userField, newValue)) { //wenn sich wirklich was geändert hat
                    doc.setUserFieldValue(me, userField, newValue, dialog.getTransactionID());
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
            doc.getCollection().getUserFieldDefinitions().initReset();
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
            if (userField.hasStyle(CLASSIFICATION_NUMBER_FORMULA)) {
                JTextField formulaTextField = (JTextField) userFieldEditorComponent.editorComponent;
                String formattedValue = userField.getFormattedValue(getModelElement(), true);
                formulaTextField.setText(formattedValue);
            }
        }
    }

    /**
     * Liefert <code>true</code>, wenn der übergebene <code>newValue</code> ein anderer ist, als
     * der beim ModelElement aktuell für das übergebenen UserField gesetzte Wert.
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
     * Liefert in Abhängigkeit vom Style den aktuellen Wert der übergebenen editorComponent.
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

        } else if (style == CLASSIFICATION_NUMBER) {
            Object textFieldValue = "";
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
        System.err.println();
    }

}
