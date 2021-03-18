/*
 * Created on 10.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.event.action.ChangeLocaleAction;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldNumberFormat;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author AXS Das FormatPanel ist in den Definitionsdialog für
 *         <code>UserField</code> s eingebettet. In ihm werden die Anzahl der
 *         Nachkommastellen und die Einheit angegeben. Wenn eine solche neue
 *         Formatvorlage angelegt wurde, wird sie in der Modelldatei als
 *         FormatUserField gespeichert und kann für beliebig vielen Kennzahlen
 *         als Formatierung für die spätere Ansicht der werte genutzt werden.
 *         Eine Formatvorlage kann nur einmal angelegt werden. Es gibt
 *         Standardvorlagen. Wenn in dem Kostenmodell die Formatierungsvorlagen
 *         noch nicht angelegt sind ( neues Modell ), werden sie initial
 *         angelegt. Die Standardformatvorlage ist notwendig, da bei speziellen
 *         Einheiten die Darstelung der Werte ander ist als die Eingabe.
 *         Beispiel ist das %: Eingabe muss der auf 1 normierte Wert sein.
 *         Dargestllt wird der auch 100% normierte Wert. Eingabe: 0,5 Ausgabe
 *         50% Deshalb benötigt man die Standardvorlage: % Standardvorlagen: *
 *         Formatvorlage mit 2 Nachkommastellen und dem %-Zeichen
 */
public class FormatPanel extends AbstractInputPanel implements ActionListener, ChangeListener, CaretListener {

    /**
     * Das UserField, dessem Format mit diesem Panel geändert werden soll.
     */
    private final UserField userField;

    /**
     * Definition aller <code>UserField</code> s und auch aller Formate.
     */
    private final UserFieldDefinitions definitions;

    /**
     * Der Dialog, in dem das Panel dargestellt wird
     */
    private final JDialog owner;

    /**
     * In diesem Panel sind die Elemente für die Formatdefinition enthalten.
     * Also die ComboBox, das Einheiten-<code>JTextField</code>, die Übernehmen
     * und Abbrechen button..
     */
    private final JPanel editFormatPanel;

    /**
     * In dieser AlphabeticalComboBox sind die schon bestehenden Formate
     * enthalten
     */
    private final AlphabeticalComboBox<UserFieldNumberFormat> formatComboBox;

    /**
     * Dieser Spinner gibt die anzhal der Nachkommastellen an.
     */
    private final JSpinner digitSpinner;

    /**
     * In dieser editierbaren ComboBox werden für neue Formatvorlagen die
     * Einheiten eingegeben. Schon bestehende Einheiten befinden sich in der
     * OcmboBox zum Auswählen.
     */
    private final AlphabeticalComboBox<String> unitBox;

    /**
     * Der Button erweitert die Anzeige des Panels um die Eingabeelemente für
     * neue Formatvorlagen.
     */
    private final JButton expandPanelButton;

    /**
     * Der new-Button legt die neue Formatvorlage an.
     */
    private final JButton newButton;

    /**
     * Der delete-Button löscht schon bestehende Formatvorlagen aus dem
     * Kostenmodell.
     */
    private final JButton deleteButton;

    /**
     *
     */
    private final JButton refreshButton;

    /**
     *
     */
    private final Vector<String> unitBoxElements = new Vector<>();

    /**
     * Wenn Formate über dieses Panel gelöscht werden, dann wird in dieser Map
     * jeweils in einer <code>ArrayList</code> gespeichert, welche UserFields
     * dieses Format benutzt haben. Wenn Abbrechen aufgerufen wird, müssen die
     * Formate wieder alle gesetzt werden.
     */
    private final Map<UserFieldNumberFormat, List<UserField>> deletedFormatToFormatUser = new HashMap<>();

    /**
     * In dieser Liste werden die neu angelegten Format- <code>UserField</code>
     * s gespeichert bis <code>commit()</code> aufgerufen wurde. Im Falle von
     * <code>cancel()</code>, werden diese dann auch wieder aus den
     * <code>definitions</code> entfernt.
     */
    private final List<UserFieldNumberFormat> newFormatesList;

    /**
     * @param owner
     * @param userField
     * @param definitions
     */
    public FormatPanel(final JDialog owner, final UserField userField, final UserFieldDefinitions definitions) {
        this.owner = owner;
        this.userField = userField;
        this.definitions = definitions;
        setLayout(new GridBagLayout());
        newFormatesList = new ArrayList<>();
        setBorder(BorderFactory.createTitledBorder(getResString("formatPaneBorder")));
        GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        formatComboBox = new AlphabeticalComboBox<>();
        formatComboBox.addActionListener(this);
        SpinnerNumberModel spinnermodel = new SpinnerNumberModel(0, 0, 10, 1);
        digitSpinner = new JSpinner(spinnermodel);
        digitSpinner.addChangeListener(this);
        unitBox = new AlphabeticalComboBox<>();
        unitBox.setEditable(true);

        unitBoxElements.add("");

        Locale locale = getLocale();

        Collection<Locale> localesWithCountry = ChangeLocaleAction.getLocalesWithCountry(locale);
        for (Locale localeWithCountry : localesWithCountry) {
            try {
                Currency currency = Currency.getInstance(localeWithCountry);
                String currencySymbol = currency.getSymbol(localeWithCountry);
                if (!unitBoxElements.contains(currencySymbol)) {
                    unitBoxElements.add(currencySymbol);
                }
            } catch (Exception e) {
                // ignore exceptions
            }
        }

        refreshButton = new JButton(getResString("refreshButtonText"));
        refreshButton.addActionListener(this);
        newButton = new JButton(getResString("neuesFormatSpeichern"));
        newButton.addActionListener(this);
        deleteButton = new JButton(getResString("delete"));
        deleteButton.addActionListener(this);

        initFormatComboBox();

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.weightx = 1.0;
        add(formatComboBox, constraints);

        constraints.weightx = 0.0;
        constraints.gridx++;
        expandPanelButton = new JButton(">>");
        expandPanelButton.addActionListener(this);
        add(expandPanelButton, constraints);

        ////////////////////// das Zahlenformatpanel

        constraints.gridx = 0;
        constraints.gridy++;

        constraints.gridwidth = 2;
        constraints.insets.top = 10;
        constraints.insets.bottom = 5;
        editFormatPanel = new JPanel();
        editFormatPanel.setVisible(false);
        editFormatPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraintsFormat = new GridBagConstraints();
        //		zahlenFormatPanel.setBorder(BorderFactory.createTitledBorder(getResString("new_format")));
        editFormatPanel.setBorder(BorderFactory.createEtchedBorder());
        add(editFormatPanel, constraints);
        constraintsFormat.anchor = GridBagConstraints.WEST;
        constraintsFormat.insets.top = 5;
        constraintsFormat.insets.left = 3;
        constraintsFormat.insets.right = 3;
        constraintsFormat.weightx = 0;
        constraintsFormat.weightx = 0;
        constraintsFormat.gridx = 0;
        constraintsFormat.gridy = 0;
        constraintsFormat.fill = GridBagConstraints.NONE;

        editFormatPanel.add(new JLabel(getResString("nachkommastelle")), constraintsFormat);

        constraintsFormat.weightx = 1;
        constraintsFormat.gridx++;
        constraintsFormat.fill = GridBagConstraints.HORIZONTAL;
        editFormatPanel.add(digitSpinner, constraintsFormat);

        constraintsFormat.weightx = 0;
        constraintsFormat.gridx = 0;
        constraintsFormat.gridy++;
        constraintsFormat.fill = GridBagConstraints.NONE;
        editFormatPanel.add(new JLabel(getResString("einheit")), constraintsFormat);

        constraintsFormat.weightx = 1;
        constraintsFormat.gridx++;
        constraintsFormat.fill = GridBagConstraints.HORIZONTAL;
        editFormatPanel.add(unitBox, constraintsFormat);

        constraintsFormat.gridx = 1;
        constraintsFormat.gridy++;
        constraintsFormat.fill = GridBagConstraints.NONE;
        constraintsFormat.anchor = GridBagConstraints.EAST;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        editFormatPanel.add(buttonPanel, constraintsFormat);

    }

    /**
     * Initialisiert die FormatCombobox mit den allen definierten Formaten.
     */
    private void initFormatComboBox() {

        // Wenn das Standardformat % mit 2 NAchkommastellen hinzugefügt wurde,
        // wird das in <code>percentAdded</code> gemerkt.
        boolean percentAdded = false;

        //ComboBox mit allen bisher defnierten Formaten zusammenbauen
        formatComboBox.removeAllItems();
        formatComboBox.addObject(null, getResString("standard_format"));
        formatComboBox.addSeparator(false);

        for (UserFieldNumberFormat format : definitions.getNumberFormats()) {
            formatComboBox.addObject(format);

            // In mehrern Formatvorlagen können selbstverständlich auch die Einheiten mehrmals vorkommen.
            // Damit für die Definition eines neuen Formates die Einheiten nicht mehrmals angeboten werden,
            // müssen sie hier gefiltert werden.
            String formatUnit = format.getUnit();
            if (formatUnit != null) {
                unitBoxElements.add(formatUnit);
                if (formatUnit.equals("%")) {
                    percentAdded = true;
                }
            }
        }
        unitBox.addAllObjects(unitBoxElements);

        //Wenn sich das Standardformat % mit 2 Nachkommastellen noch nicht im
        // Kostenmodell befindet, wird es hinzugefügt.
        if (!percentAdded) {
            addStandardFormat(2, "%");
        }

        //Wenn für ein userField schon ein Format angegeben ist, setze dies.
        UserFieldNumberFormat numberFormat = userField.getNumberFormat();
        formatComboBox.setSelectedObject(numberFormat);
    }

    /**
     * Gibt zu dem selektierten Element der <code>formatComboBox</code> das
     * Format als <code>UserField</code> zurück.
     *
     * @return das Objekt <code>UserField</code> zu dem selektierten Element der
     *         <code>formatComboBox</code>
     */
    private UserFieldNumberFormat getSelectedNumberFormat() {
        Object selectedFormatUserField = formatComboBox.getSelectedObject();
        if (selectedFormatUserField == null) {
            return null;
        }
        return (UserFieldNumberFormat) selectedFormatUserField;
    }

    /**
     * Prüft, ob eine neu anzulegende Formatvorlage schon vorhanden ist. 2
     * Formate sind gleich, wenn sie die gleich Anzahl von Nachkommastellen und
     * die gleiche Einheit besitzen.
     *
     * @return Wenn ein Duplikat entdeckt wird, gibt die Methode
     *         <code>true</code> zurück, sonst <code>false</code>.
     */
    private boolean isDuplicateFormat() {
        Object digitSpinnerValue = digitSpinner.getValue();
        String digitSpinnerValueString = digitSpinnerValue.toString();
        int spinnerFractionDigits = Integer.parseInt(digitSpinnerValueString);
        for (UserFieldNumberFormat format : definitions.getNumberFormats()) {
            if (format.getFractionDigits() != spinnerFractionDigits) {
                continue;
            }
            String formatUnit = format.getUnit();
            if (formatUnit == null) {
                //if (unitBox.getText()!=null)
                if (unitBox.getSelectedItem() != null) {
                    continue;
                }
                return true;
            }
            String text = unitBox.getText();
            if (formatUnit.equals(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wenn die Standardformate noch nicht vorhanden sind, werden sie angelegt (
     * Im Kostenmodell und in der <code>formatComboBox</code>.
     *
     * @param fractionDigits Die Anzahl der Nachkommastellen für die Darstellung
     *            des Kennzhahlwertes
     * @param unit Die Einheit, in der die Kennzahl angegeben ist.
     */
    private void addStandardFormat(final int fractionDigits, final String unit) {
        UserFieldNumberFormat format = new UserFieldNumberFormat();
        format.setFractionDigits(fractionDigits);
        format.setUnit(unit);
        definitions.add(format);

        formatComboBox.addObject(format);
    }

    /**
     * Schreibt die neue Formatvorlage als Modellvariable in Form eines
     * <code>UserField</code> s in die <code>GDCollection</code>
     *
     * @return das neu erzeugte <code>UserField</code>
     */
    private UserFieldNumberFormat addNewFormat() {
        UserFieldNumberFormat format = new UserFieldNumberFormat();
        Object digitSpinnerValueObject = digitSpinner.getValue();
        String digitSpinnerValueString = String.valueOf(digitSpinnerValueObject);
        int formatFractionDigits = Integer.parseInt(digitSpinnerValueString);
        format.setFractionDigits(formatFractionDigits);
        //Note: don't replace the AlphabeticalComboBox.getSelectedString() by
        //AlphabeticalComboBox.getSelectedObject() because the String-method
        //also returns the editor content if the item was not added to the list!
        String formatUnit = unitBox.getSelectedString();
        format.setUnit(formatUnit);
        String unit = format.getUnit();
        unitBoxElements.add(unit);
        definitions.add(format);
        unitBox.setAllObjects(unitBoxElements);
        return format;
    }

    /**
     * Aktualisiert den Enabled-Status des Aktualisieren-Knopfes
     */
    private void refreshButtonEnableStatus() {
        UserFieldNumberFormat selectedFormat = formatComboBox.getSelectedObject();
        if (selectedFormat == null) {
            refreshButton.setEnabled(false);
            return;
        }
        //wenn sich im Spinner und im EinheitenTextfeld nichts geändert hat

        int digits = (Integer) digitSpinner.getValue();
        boolean enableRefresh = digits == selectedFormat.getFractionDigits();
        if (enableRefresh) {
            String userFieldFormatUnit = selectedFormat.getUnit();
            String selectedUnit = unitBox.getSelectedObject(); //can be null
            enableRefresh &= userFieldFormatUnit.equals(selectedUnit);
        }
        refreshButton.setEnabled(enableRefresh);
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        refreshButtonEnableStatus();
    }

    @Override
    public void caretUpdate(final CaretEvent e) {
        refreshButtonEnableStatus();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == unitBox) {

        } else if (e.getSource() == formatComboBox) {
            UserFieldNumberFormat selectedFormat = formatComboBox.getSelectedObject();
            if (selectedFormat == null) {
                digitSpinner.setValue(0);
                if (unitBox.getItemCount() > 0) {
                    unitBox.setSelectedIndex(0);
                }
                deleteButton.setEnabled(false);
                return;
            }
            Integer digits = selectedFormat.getFractionDigits();
            digitSpinner.setValue(digits);
            deleteButton.setEnabled(true);
        } else if (e.getSource() == expandPanelButton) {
            if (!editFormatPanel.isVisible()) {
                Dimension d = owner.getSize();
                d.height += editFormatPanel.getPreferredSize().height;
                owner.setSize(d);
                editFormatPanel.setVisible(true);
                expandPanelButton.setText("<<");
            } else {
                Dimension d = owner.getSize();
                //vor dem invisible setzen abfragen
                d.height -= editFormatPanel.getHeight();
                owner.setSize(d);
                editFormatPanel.setVisible(false);
                expandPanelButton.setText(">>");
            }
        } else if (e.getSource() == newButton) {
            if (!isDuplicateFormat()) {
                UserFieldNumberFormat numberFormat = addNewFormat();
                formatComboBox.addObject(numberFormat);
                formatComboBox.setSelectedObject(numberFormat);
                newFormatesList.add(numberFormat);
                UserFieldNumberFormat selectedNumberFormat = getSelectedNumberFormat();
                userField.setNumberFormat(selectedNumberFormat);
            } else {
                JOptionPane.showMessageDialog(null, getResString("format_is_existing"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == deleteButton) {
            UserFieldNumberFormat formatToDelete = getSelectedNumberFormat();
            //Warnen, wemm dieses Format noch woanders benutzt wird
            ArrayList<UserField> formatUser = definitions.getUserFieldsWithNumberFormat(formatToDelete);

            //für cancel() merken, wer das Format alles benutzt hat
            boolean selfUser = formatUser.remove(userField);
            if (!formatUser.isEmpty()) {
                int option = MultipleOptionPane.showConfirmDialog(owner, getResString("warnung"), getResString("format_template_in_use"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            if (selfUser) {
                formatUser.add(userField);
            }
            deletedFormatToFormatUser.put(formatToDelete, formatUser);
            definitions.removeNumberFormat(formatToDelete);
            //testen,warum das hier gemacht wird!?
            definitions.getCollection().setUserFieldDefinitions(definitions);
            initFormatComboBox();
            UserFieldNumberFormat numberFormat = userField.getNumberFormat();
            formatComboBox.setSelectedObject(numberFormat);
        }

    }

    @Override
    public void cancel() {
        for (UserFieldNumberFormat numberFormat : newFormatesList) {
            definitions.removeNumberFormat(numberFormat);
        }
        //Cancel the delete of formats
        for (UserFieldNumberFormat format : deletedFormatToFormatUser.keySet()) {
            definitions.add(format);
            for (UserField formatUser : deletedFormatToFormatUser.get(format)) {
                formatUser.setNumberFormat(format);
            }
        }
    }

    @Override
    public void commit() {
        UserFieldNumberFormat selectedNumberFormat = getSelectedNumberFormat();
        userField.setNumberFormat(selectedNumberFormat);
    }

}
