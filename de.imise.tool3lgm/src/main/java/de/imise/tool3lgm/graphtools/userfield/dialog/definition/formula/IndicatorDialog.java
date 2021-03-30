/*
 * Created on 05.11.2007
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextField;

/**
 * Der <code>IndicatorDialog</code> stellt eine Oberfläche zur Verfügung, mit
 * der die Verrechnungsfunktion "Indikator" spezifiziert werden kann. Es muss
 * zuerst ein Kennzahl ausgewählt werden, die indiziert werden soll. Danach kann
 * die Anzahl der möglichen Wertebereiche über einen <code>JSpinner</code>
 * festgelegt und die Werte in den TextFields angegeben werden.
 *
 * @author hboehme
 * @created 05.11.2007
 */
public class IndicatorDialog extends JDialog implements ActionListener {

    /**
     * Der Spinner, mit dem die Anzahl der Wertebereiche festgelegt wird.
     */
    private JSpinner spinner;

    /**
     * Alter Wert des Spinners.
     */
    private int oldValue = 0;

    /**
     * Neuer Wert des Spinners.
     */
    private int newValue = 1;

    /**
     * Die Liste enthält die <code>JTextField</code>- und Label-Componenten
     * (GwInputPair) für die Eingabe der Wertebreiche. (Gw=Grenzwert)
     */
    private ArrayList<GwInputPair> gwList;

    /**
     * Das Panel, das die Repräsentation der gwList übernimmt.
     */
    private JPanel gwpanel;

    /**
     * Das Panel, in dem die comboBox enthalten ist und das gwpanel.
     */
    private JPanel grenzwertePanel;

    /**
     * Der String, den der <code>IndicatorDialog</code> zurückgibt.
     */
    private String retVal = "";

    /**
     * Diese comboBox enthält die Attribute der aktuellen Elementklasse, für die
     * der Indikator definiert werden soll-
     */
    private AlphabeticalComboBox<UserField> userFieldComboBox;

    /**
     * Wenn eine Indikatordefinition bearbeitet werden soll, wird das UserField
     * gesetzt, damit sich der Dialog die definition holen kann.
     */
    private final UserField userField;

    /**
     *
     */
    private final UserFieldDefinitions definitions;

    /**
     * Instanz des Dialoges.
     *
     * @param owner des Dialoges
     * @param definitions
     * @param userField : wenn eine schon bestehende Indikationsdefinition
     *            bearbeitet werden soll, das entsprechende UserField übergeben
     *            <br>
     *            ansonsten null übergeben! wenn eine neue Indikatordefinitions
     *            angelegt werden soll: null übergeben!
     */
    public IndicatorDialog(final JDialog owner, final UserFieldDefinitions definitions, final UserField userField) {
        super(owner, UserField.getDisplayableFunctionName(UserField.ACCOUNTING_FUNCTION_INDI));
        this.definitions = definitions;
        this.userField = userField;
        setModal(true);
        setLocationByPlatform(true);
        init();
        pack();
    }

    /**
     * Erstellt die grafischen Elemente.
     */
    private void init() {
        Container pane = getContentPane();
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        JLabel currentValueLabel = new JLabel(getResString("attributeIndicate"));
        panel1.add(currentValueLabel, constraints);
        userFieldComboBox = new AlphabeticalComboBox<>();

        // Hier wird die ComboBox mit den indizierbaren userfields gefüllt.
        for (UserField uf : definitions.getUserFields(userField.getTargetClass())) {
            //das UserField selbst soll nicht mit eingefügt werden (hier ist die Formual noch null, so dass die untere
            //Anfrage, isIndicatorFormula() noch nicht greift. Daher hier explizit ausschließen)
            if (uf != userField) {
                //Es dürfen nur Kennzahlen und Kennzahlformeln, die keine Indikatoren sind indiziert werden.
                //die Funktion private String getIndi(final UserFieldTarget target, final String indicatorFormula) im Calculator
                //setzt den Wert eines Indikators auf einen String, der sich nicht mehr in BigDecimal() umwandeln lässt. Daher kann
                //man keine Inidikatoren für Indikatoren definieren, was aber auch nicht umbedingt notwendig ist.
                if (uf.isNumberUserField() && !uf.isIndicatorFormula()) {
                    userFieldComboBox.addObject(uf);
                }
            }
        }

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        panel1.add(userFieldComboBox, constraints);
        grenzwertePanel = new JPanel(new GridBagLayout());
        grenzwertePanel.setBorder(BorderFactory.createTitledBorder(getResString("limitValue")));
        gwpanel = new JPanel(new GridBagLayout());
        GridBagConstraints cgw = new GridBagConstraints();
        cgw.insets = new Insets(3, 3, 3, 3);
        cgw.gridx = 0;
        cgw.gridy = 0;
        cgw.anchor = GridBagConstraints.NORTH;
        cgw.weightx = 0;
        cgw.weighty = 0;
        grenzwertePanel.add(new JLabel(getResString("numerOfValues")), cgw);
        SpinnerNumberModel spm = new SpinnerNumberModel(1, 1, 10, 1);
        gwList = new ArrayList<>();
        spinner = new JSpinner(spm);
        cgw.gridx = 1;
        spinner.addChangeListener(e -> createInputFields());
        cgw.weightx = 1;
        cgw.fill = GridBagConstraints.HORIZONTAL;
        grenzwertePanel.add(spinner, cgw);
        cgw.gridy = 1;
        cgw.weighty = 1;
        cgw.fill = GridBagConstraints.BOTH;
        grenzwertePanel.add(gwpanel, cgw);
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panel1.add(grenzwertePanel, constraints);
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        JButton okButton = new JButton(getResString("ok"));
        JButton cancelButton = new JButton(getResString("cancel"));
        okButton.addActionListener(this);
        okButton.setActionCommand("okbutton");
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("cancelbutton");
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridy++;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        panel1.add(buttonsPanel, constraints);
        pane.setLayout(new BorderLayout());
        pane.add(panel1, BorderLayout.CENTER);

        //Wenn ein userField übergeben wurde, heißt das, dass eine schon vorhandene
        //Indikationsdefinition bearbeitet werden soll.
        createInputFields();

        fillIndiValues();

    }

    /**
     * Erstellt die Eingabefelder für die Grenzwerte in Abhäigkeit des aktuell
     * gewählten Wert im Spinner. Wird aufgerufen, wenn dich der Wert des
     * Spinners ändert.
     */
    private void createInputFields() {
        newValue = Integer.parseInt(spinner.getValue().toString());
        if (newValue > oldValue) {
            //für die unterste Grenze müssen zwei TextFields angelegt werden (unterste Grenze und erste obere Grenze)
            if (newValue == 1) {
                gwList.add(new GwInputPair(0));
            }
            //Danach nur noch obere Grenzen anlgen

            //Falls der Spinner nicht mit der Maus geklickt wurde, sonderen eine Zahl im zugelassenenen Intervall eingegeben wurde,
            //müssen entsprechend viele neue Felder angelegt werden.
            int anzNewFields = newValue - oldValue;
            for (int i = 0; i < anzNewFields; i++) {
                gwList.add(new GwInputPair(++oldValue));
            }

            oldValue = newValue;
        }
        if (newValue < oldValue) {

            int anzFieldsToDelete = oldValue - newValue;

            for (int i = 0; i < anzFieldsToDelete; i++) {
                gwList.remove(gwList.size() - 1);
            }

            oldValue = newValue;
        }
        syncronizeGwInputPairs();
        GridBagConstraints cgw2 = new GridBagConstraints();
        cgw2.insets = new Insets(3, 3, 3, 3);
        cgw2.fill = GridBagConstraints.HORIZONTAL;
        cgw2.gridwidth = 2;
        cgw2.gridx = 0;
        cgw2.gridy = 1;
        grenzwertePanel.remove(gwpanel);
        grenzwertePanel.add(gwpanel, cgw2);
        pack();
    }

    /**
     * Wenn ein schon bestehender Indikator bearbeitet werden soll, wird der
     * Dialog mit schon vorhandenen Indikationsbereichen gefüllt.
     */
    private void fillIndiValues() {
        if (userField.hasStyle(UserField.Style.FORMULA)) {
            //	userFieldComboBox.setSelectedItem(definitions.getUserField());
            String indi = userField.getFormula();
            if (indi != null) {
                StringTokenizer st = new StringTokenizer(indi, " ()| ");

                //Erstes Token wegschmeißen
                st.nextToken();
                // zweites token enthält die ID des zu indizierenden UserFields
                String userFieldID = st.nextToken();
                UserField userField2Select = definitions.getUserField(userFieldID);
                userFieldComboBox.setSelectedObject(userField2Select);

                // die Eingabefelder darstellen
                // beim Durchlaufen des Stringtokenizers, wird der spinnerValue um 1 incrementiert
                // Der Spinner feuert ein stateChanged und hängt somit ein neues TextField an.

                int spinnerValue = 1;
                while (st.hasMoreElements()) {
                    String value = st.nextToken();

                    spinner.setValue(spinnerValue);

                    if (spinnerValue == 1) {
                        //bei 1 müssen zwei TextFields dargestellt werden. Das für den untersten Grenzwert und der erste obere Grenzwert.
                        GwInputPair gwPair = gwList.get(spinnerValue - 1);
                        gwPair.inputField.setText(value);
                        value = st.nextToken();
                    }
                    GwInputPair a = gwList.get(spinnerValue);
                    a.inputField.setText(value);
                    spinnerValue++;
                }
            }
            pack();

        }

    }

    /**
     * Syncronisiert nach dem Betätigen des Spinners das Panel, das die
     * textfields darstellt und die Liste, die die Textfieldcomponenten (
     * <code>GwInputPair</code>) enthält.
     */
    private void syncronizeGwInputPairs() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        gwpanel.removeAll();
        for (int i = 0; i < gwList.size(); i++) {
            gwpanel.add(gwList.get(i), c);
            c.gridy++;
        }
    }

    /**
     * zeigt den Dialog an.
     *
     * @return Gibt als String das zu indizierende <code>UserField</code> und
     *         die Wertebereiche zurück.
     */
    public String showDialog() {
        setVisible(true);
        return retVal;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("okbutton")) {
            if (validateInputs()) {
                UserField u = userFieldComboBox.getSelectedObject();
                retVal += u.getID() + " | ";
                for (int i = 0; i < gwList.size(); i++) {
                    GwInputPair pair = gwList.get(i);
                    retVal += pair.inputField.getText();
                    if (i < gwList.size() - 1) {
                        retVal += " | ";
                    }
                }
                dispose();
            }
        }

        if (e.getActionCommand().equals("cancelbutton")) {
            dispose();
        }

    }

    /**
     * Prüft den Indikatordialog.
     * <ul>
     * <li>Prüft die Eingabefelder der Grenzwerte auf gültige Eingabewerte.</li>
     * <li>Prüft die <code>AlpabeticalComboBox</code>, ob ein Element ausgewählt
     * ist</li>
     * </ul>
     *
     * @return True, wenn alle Eingabefelder korrekt belegt sind, sonst false.
     */
    private boolean validateInputs() {
        // Es muss genau ein userField aus der ComboBox ausgewählt sein
        if (userFieldComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, getResString("missing_userfield_reference"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //Die eingaben dürfen nicht leer sein
        for (int i = 0; i < gwList.size(); i++) {
            try {
                //Die Eingabenb müssen gülte Werte sein, die sich auf BigDecimal parsen lassen müssen.
                String tmp_string = gwList.get(i).inputField.getText().trim();
                tmp_string = tmp_string.replace(",", ".");
                new BigDecimal(tmp_string);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, getResString("invalid_values"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }

        return true;
    }

    /**
     * Ein Object, welches JPanel erweitert und ein <code>JLabel</code> sowie
     * ein <code>JTexftField</code> enthält. Dieses Objekt geht in die Liste der
     * Wertetabelle ein und kann damit dynamisch erweitert oder gekürzt werden.
     *
     * @author hboehme
     * @created 16.11.2007
     */
    private class GwInputPair extends JPanel {
        ExtendedTextField inputField;

        JLabel label;

        GwInputPair(final int index) {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            if (index == 0) {
                label = new JLabel(getResString("indicator_lowest_border") + ": ");
            } else {
                label = new JLabel(getResString("indicator_higher_border") + index + " :   ");
            }
            label.setSize(100, label.getHeight());
            add(label, c);
            c.gridx = 1;
            c.weightx = 1;
            inputField = new ExtendedTextField();
            add(inputField, c);
        }

    }

}
