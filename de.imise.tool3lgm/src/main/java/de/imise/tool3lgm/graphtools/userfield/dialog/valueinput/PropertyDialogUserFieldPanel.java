package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextArea;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.component.text.NumberTextField;

/**
 * Panel für den Eigenschaftendialog von Elementen, in dem alle definierten UserField des Elementes angezeigt werden. In ein <code>JScrollPane</code>
 * wird ein <code>JPanel</code> eingefügt. In dem <code>JPanel</code> werden die Eingabemöglichkeiten für die UserField dargestellt. Die grafischen
 * Elemente werden durch <code>JComponent </code>, egal ob <code>JLabel</code>, <code>JTextfield</code> oder <code>JComboBox</code> repräsentiert. Es
 * wird eine <code>ArrayList</code> angelegt, die Objekte vom Typ <code>AttributeComponent</code> enthält. Eine <code>AttributeComponent</code>
 * enthält als Objektattribute ein Attribut einer Element- bzw. Kantenklasse und die zugehörige GUI-Komponente. Für die Datenübernahme ist die Methode
 * <code>commit()</code> zuständig. Sie prüft iterativ die Eingaben in GUI-komponenten in der <code>ArrayList</code> und vergleicht Sie mit den
 * bisherigen Werten der UserField. Bei einem Unterschied wird das Tool3lgm-Kommando <code>SET_USER_FIELD_VALUE</code> aufgerufen und somit der Wert
 * eines Attributes einer Element- bzw. Kanetenklasse geändert.
 * 
 * @author Thomas Rudert, xhb
 */
public class PropertyDialogUserFieldPanel extends ElementDialogPanel {

    private final ArrayList<AttributeComponent> fields = new ArrayList<AttributeComponent>();

    /**
     * @param pd
     */
    public PropertyDialogUserFieldPanel(final ElementPropertyDialog pd) {
        super(pd);
        create();

    }

    /**
     * Visualisiert die UserField mit ihren entsprechenden Style-Vorgaben im <code>JPanel</code>.
     */
    private void create() {
        setLayout(new BorderLayout());
        JPanel mp = new JPanel();
        JScrollPane sp = new JScrollPane(mp);
        add(sp);
        JScrollBar sb = sp.getVerticalScrollBar();
        sb.setBlockIncrement(20);
        sb.setUnitIncrement(10);
        sb = sp.getHorizontalScrollBar();
        sb.setBlockIncrement(20);
        sb.setUnitIncrement(10);

        //Attributdefinitionen des GraphDocumentes holen
        GDCollection gdcol = doc.getCollection();
        UserFieldDefinitions attributeDefs = gdcol.getUserFieldDefinitions();

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mp.setLayout(new GridBagLayout());
        JPanel panel;
        ButtonGroup group;
        boolean foundEntry;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(1, 1, 1, 0);
        ModelElement modelElement = getModelElement();
        for (UserField field : attributeDefs.getUserFields(getModelElement().getClass())) {
            String value = modelElement.getUserFieldInputValue(field).trim();
            UserField.Style style = field.getStyle();
            if (style == UserField.Style.SEPARATOR) {
                constraints.gridx = 0;
                constraints.weightx = 1;
                mp.add(new JSeparator(), constraints);
                constraints.gridy++;
                JComponent component = new JLabel("<HTML><B>" + field.getName() + "</B></HTML>");
                mp.add(component, constraints);
                fields.add(new AttributeComponent(field, component));
                constraints.gridy++;
                mp.add(new JSeparator(), constraints);
                constraints.gridy++;
                constraints.gridx = 0;
                constraints.weightx = 0;
            } else if (style == UserField.Style.SINGLE_LINE) {
                JLabel label = new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>");
                mp.add(label, constraints);
                constraints.gridy++;
                constraints.weightx = 1;
                if (value.equals(UserField.EMPTY_STRING)) {
                    value = "";
                }
                JComponent component = new ExtendedTextField(value);
                mp.add(component, constraints);
                fields.add(new AttributeComponent(field, component));
                constraints.gridy++;
                constraints.gridx = 0;
                constraints.weightx = 0;
            } else if (style == UserField.Style.MULTI_LINE) {
                mp.add(new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>"), constraints);
                constraints.gridy++;
                constraints.weightx = 1;
                int gridheight = constraints.gridheight;
                constraints.gridheight = 2;
                JComponent component = new ExtendedTextArea(3, 10);
                ((ExtendedTextArea) component).setFont(new ExtendedTextField().getFont());
                ((ExtendedTextArea) component).setLineWrap(true);
                ((ExtendedTextArea) component).setWrapStyleWord(true);
                if (value.equals(UserField.EMPTY_STRING)) {
                    value = "";
                }
                ((JTextComponent) component).setText(value);

                mp.add(new JScrollPane(component), constraints);
                fields.add(new AttributeComponent(field, component));
                constraints.gridy += 2;
                constraints.gridx = 0;
                constraints.weightx = 0;
                constraints.gridheight = gridheight;
            } else if (style == UserField.Style.COMBO_BOX) {
                if (value.equals(UserField.EMPTY_STRING)) {
                    value = "";
                }
                mp.add(new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>"), constraints);
                constraints.weightx = 1;
                constraints.gridy++;
                AlphabeticalComboBox component = new AlphabeticalComboBox(true);
                foundEntry = false;
                for (int i = 0; i < field.getListValuesCount(); i++) {
                    Object listValue = field.getListValueAt(i);
                    component.addItem(listValue);
                    if (listValue.equals(value)) {
                        component.setSelectedItem(listValue);
                        foundEntry = true;
                    }
                }
                if (!foundEntry) {
                    if (value.length() > 0) {
                        field.addListValue(value);
                        component.addItem(value);
                        component.setSelectedItem(value);
                        foundEntry = true;
                    } else {
                        component.setSelectedIndex(-1);
                    }
                }

                mp.add(component, constraints);
                fields.add(new AttributeComponent(field, component));
                constraints.gridy++;
                constraints.gridx = 0;
                constraints.weightx = 0;
            } else if (style == UserField.Style.RADIO_BUTTON) {
                if (value.equals(UserField.EMPTY_STRING)) {
                    value = "";
                }
                mp.add(new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>"), constraints);
                constraints.weightx = 1;
                constraints.gridwidth = 1;
                constraints.gridy++;
                panel = new JPanel();
                group = new ButtonGroup();
                foundEntry = false;
                for (int i = 0; i < field.getListValuesCount(); i++) {
                    Object listValue = field.getListValueAt(i);
                    JComponent component = new JRadioButton(listValue.toString());
                    group.add((JRadioButton) component);
                    panel.add(component);
                    fields.add(new AttributeComponent(field, component));
                    if (listValue.equals(value)) {
                        foundEntry = true;
                        ((JRadioButton) component).setSelected(true);
                    }
                }
                if (value.length() > 0 && !foundEntry) {
                    field.addListValue(value);
                    JComponent component = new JRadioButton(value);
                    group.add((JRadioButton) component);
                    panel.add(component);
                    fields.add(new AttributeComponent(field, component));
                    ((JRadioButton) component).setSelected(true);
                }
                panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), ""));
                mp.add(panel, constraints);
                constraints.gridy++;
                constraints.weightx = 0;
                constraints.gridwidth = 1;
            } else if (style == UserField.Style.CHECK_BOX) {
                if (value.equals(UserField.EMPTY_STRING)) {
                    value = "";
                }
                mp.add(new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>"), constraints);
                constraints.gridy++;
                constraints.weightx = 1;
                JComponent component = new JCheckBox(field.getName(), value.equals(UserField.CHECKBOX_TRUE));
                mp.add(component, constraints);
                fields.add(new AttributeComponent(field, component));
                constraints.gridy++;
                constraints.gridx = 0;
                constraints.weightx = 0;
            } else if (style == UserField.Style.HYPERLINK) {
                if (value.equals(UserField.EMPTY_STRING)) {
                    value = "";
                }
                mp.add(new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>"), constraints);
                constraints.gridy++;
                constraints.weightx = 1;
                final ExtendedTextField textField = new ExtendedTextField(value);
                JPanel slp = new JPanel(new GridBagLayout());
                GridBagConstraints constraints_format = new GridBagConstraints();
                constraints_format.fill = GridBagConstraints.HORIZONTAL;
                constraints_format.weightx = 1;
                slp.add(textField, constraints_format);
                fields.add(new AttributeComponent(field, textField));

                // Der Button, der für einen Hyperlink das öffnen eines Browsers
                // ermöglicht.
                final JButton button = new JButton(new AbstractAction(">>") {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        String urlOrPath = textField.getText().trim();
                        if (!urlOrPath.isEmpty()) {
                            BrowseUtils.browse(urlOrPath);
                        }
                    }
                });
                //auch bei Doppelklick oder Klick mit STRG den Link öffnen
                textField.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(final MouseEvent e) {
                        if (e.getClickCount() == 1 && e.isControlDown() || e.getClickCount() > 1) {
                            button.doClick();
                        }
                    }
                });

                constraints_format.gridx = 1;
                constraints_format.weightx = 0;
                slp.add(button, constraints_format);
                constraints.weightx = 1;

                mp.add(slp, constraints);
                constraints.gridy++;
                constraints.gridx = 0;
                constraints.weightx = 0;

            } else if (style == UserField.Style.ID) {
                JLabel label = new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>");
                mp.add(label, constraints);
                constraints.gridy++;
                constraints.weightx = 1;
                if (UserField.EMPTY_STRING.equals(value)) {
                    value = "";
                }
                ExtendedTextField textField = new ExtendedTextField(value);
                mp.add(textField, constraints);
                fields.add(new AttributeComponent(field, textField));
                constraints.gridy++;
                constraints.gridx = 0;
                constraints.weightx = 0;

                //wenn die IDs schon bei der Eingabe auf eindeutigkeit geprüft werden sollten
                //textField.getDocument().addDocumentListener(new UniqueValueVerifier(field, getModelElement(), textField));

                //Kennzahlen:
            } else if (style == UserField.Style.CLASSIFICATION_NUMBER) {
                String unit = field.getFormatUnit();
                StringBuilder einheit = new StringBuilder("");
                if (unit != null) {
                    einheit.append(" ");
                    einheit.append(Tool3lgmConstants.getResString("in"));
                    einheit.append(" ");
                    einheit.append(unit);
                    einheit.append(" ");
                }

                mp.add(new JLabel("<HTML><B>" + field.getName() + einheit + "</B><BR>" + field.getDescription() + "</HTML>"), constraints);
                constraints.gridy++;
                constraints.weightx = 1;
                //				component = new JFormattedTextField(value);

                //Wenn für die Kennzazhl ein gültiger Wert eingegeben ist, dann kann hier ein NumberTextField initialisiert werden.
                // Sollte das Fehlschlagen, muss ein normales JTextField hinzugefügt werden, das keine Wertformatierung vornimmt.
                String fieldValue = field.getValue(dialog.getModelElement());
                NumberFormat numberFormat = field.getNumberFormat();
                JComponent component;
                if (UserField.IGNOREABLE_ERROR_SET.contains(fieldValue) || UserField.ERROR_SET.contains(fieldValue)) {
                    component = NumberTextField.getNumberTextField(numberFormat, field.isPositiveOnly());
                } else {
                    //					  	Kennzahlwerte in die Felder einfügen.
                    component = NumberTextField.getNumberTextField(numberFormat, field.isPositiveOnly());

                    if (!UserField.isErrorString(fieldValue) && !UserField.isIgnoreableErrorString(fieldValue)) {
                        try {
                            ((JFormattedTextField) component).setValue(Double.valueOf(fieldValue));
                        } catch (NumberFormatException e1) {
                            ((JFormattedTextField) component).setValue(fieldValue);

                        }
                    }
                }

                mp.add(component, constraints);
                fields.add(new AttributeComponent(field, component));
                constraints.gridx = 0;
                constraints.weightx = 1;
                constraints.gridy++;
            } else if (style == UserField.Style.CLASSIFICATION_NUMBER_FORMULA) {
                mp.add(new JLabel("<HTML><B>" + field.getName() + "</B><BR>" + field.getDescription() + "</HTML>"), constraints);
                constraints.gridy++;
                constraints.weightx = 1;
                //				String tmp_str = field.getFormatedValue(modelElement, true);
                ExtendedTextField component = new ExtendedTextField(field.getValue(modelElement));

                component.setEditable(false);
                mp.add(component, constraints);
                fields.add(new AttributeComponent(field, component));
                constraints.gridx = 0;
                constraints.weightx = 1;
                constraints.gridy++;

                String formattedValue = field.getFormattedValue(dialog.getModelElement(), true);
                ((JTextComponent) component).setText(formattedValue);
            }
        }
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        mp.add(new JPanel(), constraints);
    }

    @Override
    protected void showFullDialog() {
    }

    @Override
    public void commit() {
        AttributeComponent comp;
        for (int i = 0; i < fields.size(); i++) {
            String newValue = "";
            comp = fields.get(i);
            UserField.Style style = comp.attribute.getStyle();
            if (style == UserField.Style.SINGLE_LINE) {
                newValue = ((JTextComponent) comp.comp).getText();

            } else if (style == UserField.Style.MULTI_LINE) {
                newValue = GraphDocument.getParseSaveString(((JTextComponent) comp.comp).getText(), false);

            } else if (style == UserField.Style.COMBO_BOX) {
                Object selectedItem = ((JComboBox) comp.comp).getSelectedItem();
                if (selectedItem != null) {
                    newValue = GraphDocument.getParseSaveString(selectedItem.toString(), false);
                }

            } else if (style == UserField.Style.CHECK_BOX) {
                newValue = ((JCheckBox) comp.comp).isSelected() ? UserField.CHECKBOX_TRUE : UserField.CHECKBOX_FALSE;

            } else if (style == UserField.Style.RADIO_BUTTON) {
                if (!((JRadioButton) comp.comp).isSelected()) {
                    continue;
                }
                newValue = GraphDocument.getParseSaveString(((JRadioButton) comp.comp).getText(), false);

            } else if (style == UserField.Style.HYPERLINK) {
                newValue = GraphDocument.getParseSaveString(((JTextComponent) comp.comp).getText(), true);

            } else if (style == UserField.Style.ID) {
                newValue = ((JTextComponent) comp.comp).getText();

            } else if (style == UserField.Style.CLASSIFICATION_NUMBER) {
                Object textFieldValue = "";
                NumberTextField textField = (NumberTextField) comp.comp;
                textFieldValue = textField.getValue();

                if (textFieldValue == null || textFieldValue.equals("")) {
                    newValue = "";
                } else {
                    newValue = textFieldValue.toString();
                }
            }

            if (!newValue.equals(getModelElement().getUserFieldInputValue(comp.attribute))) {
                doc.setUserFieldValue(getModelElement(), comp.attribute, newValue, dialog.getTransactionID());
            }
        }
        // wenn eine Kennzahl geändert wurde, wurde bei doc.exec() die Variable
        // reset aus dem Calculator auf true gesetzt.
        // Jetzt kann man einfach dem Calculator sagen, er soll alle
        // Kennzahlformeln neu berechnen, wenn
        doc.getCollection().getUserFieldDefinitions().initReset();

    }

    /**
     * Für jedes Attribut eine <code>Component</code>e, die es anzeigt.
     */
    private class AttributeComponent {
        private final JComponent comp;

        private final UserField attribute;

        AttributeComponent(final UserField attribute, final JComponent component) {
            comp = component;
            this.attribute = attribute;
        }
    }

    private static final void print(final GridBagConstraints c) {
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
