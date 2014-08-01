/*
 * Created on 05.11.2007
 */
package de.imise.tool3lgm.graphtools.userfield.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextField;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;

/**
 * Der <code>IndicatorDialog</code> stellt eine
 * Oberfläche zur Verfügung, mit der die Verrechnungsfunktion "Indikator"
 * spezifiziert werden kann. Es muss zuerst ein Kennzahl ausgewählt werden, die indiziert werden soll. 
 * Danach kann die Anzahl der möglichen Wertebereiche über einen <code>JSpinner</code> festgelegt und die Werte in den TextFields angegeben werden.
 * 
 * @author hboehme
 * 
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
	 * Die Liste enthält die <code>JTextField</code>- und Label-Componenten (GwInputPair) für die Eingabe der Wertebreiche. 
	 * (Gw=Grenzwert)
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
	private AlphabeticalComboBox userFieldComboBox;

	/**
	 * Wenn eine Indikatordefinition bearbeitet werden soll, wird das UserField gesetzt, 
	 * damit sich der Dialog die definition holen kann. 
	 */
	private UserField userField;

	/**
	 * 
	 */
	private UserFieldDefinitions definitions;

	/**
	 * Instanz des Dialoges. 
	 * 
	 * @param owner des Dialoges
	 * @param classElement : für welches Klassenelement soll ein Indikator angelegt werden. 
	 * @param userField : wenn eine schon bestehende Indikationsdefinition bearbeitet werden soll, das entsprechende UserField übergeben <br>ansonsten null übergeben!
	 * wenn eine neue Indikatordefinitions angelegt werden soll: null übergeben!
	 */
	public IndicatorDialog(JDialog owner, UserField userField) {
		super(owner, Tool3lgmConstants.getResString("indicator"));
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
		Container pane = this.getContentPane();
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
		JLabel actualValueLabel = new JLabel(Tool3lgmConstants.getResString("attributeIndicate"));
		panel1.add(actualValueLabel, constraints);
		userFieldComboBox = new AlphabeticalComboBox();
		if (userField != null)
		definitions = userField.getDefinitions();

		// Hier wird die ComboBox mit den indizierbaren userfields gefüllt.
/*		alte Variante zur Kontrolle		
 * for (int i = 0; i < definitions.getUserFieldCount(userField.getTargetClass()); i++) {
			UserField tmp_userField = definitions.get(userField.getTargetClass(), i);
			//Es dürfen nur kennzahlen und Kennzahlformel indiziert werden.
			if (tmp_userField.isClassificationUserField())
				//Wenn es sich um eine Kennzahlformel handelt und diese selbst auch ein Indikator ist, dann darf es NICHT hinzugefügt werden.
				if (tmp_userField.getStyle()==UserField.CLASSIFICATION_NUMBER_FORMULA_STYLE && !(tmp_userField.getFormula()==null || !tmp_userField.getFormula().trim().startsWith(UserField.ACCOUNTING_FUNCTION_INDI)))
					continue;
				userFieldComboBox.addItem(tmp_userField);
		}
*/		
		// Hier wird die ComboBox mit den indizierbaren userfields gefüllt. 


		for (UserField uf : definitions.getUserFields(userField.getTargetClass())) {
			//Es dürfen nur Kennzahlen und Kennzahlformeln, die keine Indikatoren sind indiziert werden.
			if (uf.isClassificationUserField() && !uf.isIndicatorFormula())
				userFieldComboBox.addItem(uf);
		}

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.weightx = 1;
		panel1.add(userFieldComboBox, constraints);
		grenzwertePanel = new JPanel(new GridBagLayout());
		grenzwertePanel.setBorder(BorderFactory.createTitledBorder(Tool3lgmConstants.getResString("limitValue")));
		gwpanel = new JPanel(new GridBagLayout());
		GridBagConstraints cgw = new GridBagConstraints();
		cgw.insets = new Insets(3, 3, 3, 3);
		cgw.gridx = 0;
		cgw.gridy = 0;
		cgw.anchor = GridBagConstraints.NORTH;
		cgw.weightx = 0;
		cgw.weighty = 0;
		grenzwertePanel.add(new JLabel(Tool3lgmConstants.getResString("numerOfValues")), cgw);
		SpinnerNumberModel spm = new SpinnerNumberModel(1, 1, 10, 1);
		gwList = new ArrayList<GwInputPair>();
		spinner = new JSpinner(spm);
		cgw.gridx = 1;
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				createInputFields();
			}
		});
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
		JButton okButton = new JButton(Tool3lgmConstants.getResString("ok"));
		JButton cancelButton = new JButton(Tool3lgmConstants.getResString("cancel"));
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
	 * Erstellt die Eingabefelder für die Grenzwerte in Abhäigkeit des aktuell gewählten Wert im Spinner.
	 * Wird aufgerufen, wenn dich der Wert des Spinners ändert.
	 *
	 */
	private void createInputFields() {
		newValue = (Integer.parseInt(spinner.getValue().toString()));
		if (newValue > oldValue) {
			//für die unterste Grenze müssen zwei TextFields angelegt werden (unterste Grenze und erste obere Grenze)
			if (newValue == 1) {
				gwList.add(new GwInputPair(0));
			}
			//Danach nur noch obere Grenzen anlgen
			
			//Falls der Spinner nicht mit der Maus geklickt wurde, sonderen eine Zahl im zugelassenenen Intervall eingegeben wurde, 
			//müssen entsprechend viele neue Felder angelegt werden.
			int anzNewFields = newValue-oldValue;
			for (int i=0; i< anzNewFields;i++)
			{
				gwList.add(new GwInputPair(++oldValue));
			}
			
			
			oldValue = newValue;
		}
		if (newValue < oldValue) {
			
			int anzFieldsToDelete = oldValue-newValue;
			
			for(int i =0; i< anzFieldsToDelete;i++)
				gwList.remove(gwList.size() - 1);
			
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
	 * Wenn ein schon bestehender Indikator bearbeitet werden soll, 
	 * wird der Dialog mit schon vorhandenen Indikationsbereichen gefüllt. 
	 *
	 */
	private void fillIndiValues() {
		if (userField.hasStyle(UserField.Style.CLASSIFICATION_NUMBER_FORMULA)) {
			//	userFieldComboBox.setSelectedItem(definitions.getUserField());
			String indi = userField.getFormula();
			if (indi != null) {
				StringTokenizer st = new StringTokenizer(indi, " ()| ");

				//Erstes Token wegschmeißen
				st.nextElement();
				// zweites token enthält den Hash-String des zu indizierenden
				// UserFields
				userFieldComboBox.setSelectedItem(definitions.getUserField(st.nextElement().toString()));

				// die Eingabefelder darstellen
				// beim Durchlaufen des Stringtokenizers, wird der spinnerValue um 1 incrementiert
				// Der Spinner feuert ein stateChanged und hängt somit ein neues TextField an.

				int spinnerValue = 1;
				while (st.hasMoreElements()) {
					String value = st.nextElement().toString();

					spinner.setValue(new Integer(spinnerValue));

					if (spinnerValue == 1) {
						//bei 1 müssen zwei TextFields dargestellt werden. Das für den untersten Grenzwert und der erste obere Grenzwert.
						GwInputPair gwPair = gwList.get(spinnerValue - 1);
						gwPair.inputField.setText(value);
						value = st.nextElement().toString();
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
	 * textfields darstellt und die Liste, die die Textfieldcomponenten
	 * (<code>GwInputPair</code>) enthält.
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
	 * @return Gibt als String das zu indizierende <code>UserField</code> und die Wertebereiche zurück.
	 */
	public String showDialog() {
		setVisible(true);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("okbutton")) {
			if (validateInputs()) {
				UserField u = (UserField) userFieldComboBox.getSelectedItem();
				retVal += u.getHashCode() + " | ";
				for (int i = 0; i < gwList.size(); i++) {
					GwInputPair pair = gwList.get(i);
					retVal += pair.inputField.getText();
					if (i < (gwList.size() - 1)) {
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
	 * 	<li>
	 * 		Prüft die Eingabefelder der Grenzwerte auf gültige Eingabewerte.
	 * 	</li>
	 * 	<li>
	 * 		Prüft die <code>AlpabeticalComboBox</code>, ob ein Element ausgewählt ist
	 * 	</li>
	 * </ul>
	 * @return True, wenn alle Eingabefelder korrekt belegt sind, sonst false. 
	 */
	private boolean validateInputs() {
		// Es muss genau ein userField aus der ComboBox ausgewählt sein
		if (userFieldComboBox.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, Tool3lgmConstants.getErrString("missing_userfield_reference"), Tool3lgmConstants.getResString("fehler"), JOptionPane.ERROR_MESSAGE);
			return false;
		}

		//Die eingaben dürfen nicht leer sein  
		for (int i = 0; i < gwList.size(); i++) {
			try {
				//Die Eingabenb müssen gülte Werte sein, die sich auf Double parsen lassen müssen.  
				//TODO :XHB: Den Vergleich müsste man über einen regulären Ausdrück durchführen. 
				String tmp_string = gwList.get(i).inputField.getText().trim();
				tmp_string = tmp_string.replace(",", ".");
				Double.parseDouble(tmp_string);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, Tool3lgmConstants.getErrString("invalid_values"), Tool3lgmConstants.getResString("fehler"), JOptionPane.ERROR_MESSAGE);
				return false;
			}

		}

		return true;
	}

	/**
	 * Ein Object, welches JPanel erweitert und ein <code>JLabel</code> sowie
	 * ein <code>JTexftField</code> enthält. Dieses Objekt geht in die Liste
	 * der Wertetabelle ein und kann damit dynamisch erweitert oder gekürzt
	 * werden.
	 * 
	 * @author hboehme
	 * 
	 * @created 16.11.2007
	 */
	private class GwInputPair extends JPanel {
		ExtendedTextField inputField;

		JLabel label;

		GwInputPair(int index) {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.weightx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			if (index == 0)
				label = new JLabel(Tool3lgmConstants.getResString("indicator_lowest_border") + ": ");
			else
				label = new JLabel(Tool3lgmConstants.getResString("indicator_higher_border") + index + " :   ");
			label.setSize(100, label.getHeight());
			add(label, c);
			c.gridx = 1;
			c.weightx = 1;
			inputField = new ExtendedTextField();
			add(inputField, c);
		}

	}

}
