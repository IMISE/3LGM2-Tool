/*
 * Created on 09.11.2007
 */
package de.imise.util.swing.dialog;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Stellt einen Dialog bereit, der untereinander CheckBoxen für übergebene Optionen darstellt,
 * die alle einzeln selektiert werden können. Der Dialog gibt die Selektion der Optionen in
 * einem <code>boolean</code>-Array zurück, in der die Reihenfolge der einzelnen boolean-Werte
 * der Reihenfolge der übergebenen Optionen entspricht. Ein <code>true</code> an Stelle 0
 * im Rückgabe-Array bedeutet, dass die erste Option selektiert wurde.<br>
 * 
 * Der Dialog kann in seiner Anzeigebreite beschränkt werden, wenn man eine direkte Instanz von
 * dieser Klasse bildet und dann nicht über die statischen Funktionen von <code>JOptionPane</code>
 * sondern über die Instanzmethoden dieser Klasse Dialoge anzeigt.
 * 
 * @author AXS
 *
 */
public class MultipleOptionPane extends JOptionPane{

	/**
	 * Maximale Anzahl von Zeichen in einer Zeile
	 */
	private int maxCharactersPerLineCount = 90;
	

	/**
	 * Ertsellt ein neues Pane, dessen Dialoge auf 90 Zeichen Breite beschränkt sind.
	 */
	public MultipleOptionPane() {
		super();
	}

	/**
	 * @param maxCharactersPerLineCount maximale Anzahl von Zeichen in einer Zeile 
	 */
	public MultipleOptionPane(int maxCharactersPerLineCount) {
		super();
		this.maxCharactersPerLineCount = maxCharactersPerLineCount;
	}
	
	/**
	 * Zeigt einen Dialog an, der oben eine Message und darunter die übergebene Komponente anzeigt.
	 * 
	 * @param parentComponent
	 * 			Besitzerkomponente des Dialoges
	 * @param title
	 * 			Titel des Dialoges
	 * @param message 
	 * 			Nachricht des Dialoges
	 * @param component
	 * 			Anzuzeigende Komponente
	 * @return
	 * 			{@link JOptionPane#OK_OPTION} wenn OK gedrückt wurde. {@link JOptionPane#CANCEL_OPTION}, wenn etwas anderes (Schließen-Kreuz
	 * 			oder Abbrechen) gedrückt wurde.
	 */
	public final int showComponentDialog(Component parentComponent, String title, String message, Component component) {
		Object msg[] = { message, component };
		setMessage(msg);
		setMessageType(JOptionPane.QUESTION_MESSAGE);
		setOptionType(JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = createDialog(parentComponent, title);
		dialog.setVisible(true);
		// Schließen übers Kreuz oder irgendwas unvorhergesehenes
		if (value == null || !(value instanceof Integer))
			return CANCEL_OPTION;
		return OK_OPTION;
	}

	/**
	 * Zeigt einen Optionen-Dialog an, der für jede übergebene Option eine Checkbox darstellt.<br>
	 * Über das Array <code>selected</code> können bereits selektierte Checkboxen festgelegt werden.
	 * Dieses Array muss die gleiche Länge wie <code>options</code> haben oder kann <code>null</code>
	 * sein. Wenn es <code>null</code> ist, sind alle Checkboxen nicht selektiert. 
	 * 
	 * 
	 * @param parentComponent 
	 * 			Besitzerkomponente des Dialoges
	 * @param title 
	 * 			Titel des Dialoges
	 * @param message 
	 * 			Nachricht des Dialoges
	 * @param options 
	 * 			Optionen, die über Checkboxen zur Auswahl gestellt werden. Diese Optionen werden über
	 * 			ihre toString()-Methode im Dialog angezeigt.
	 * @param selected
	 * 			legt fest, ob Checkboxen der Optionen bereits angewählt sind oder nicht
	 * @return Array der übergebenen Options. War die Option ausgewählt, ist sie nicht <code>null</code>, sost ist sie <code>null</code>
	 */
	public static final Object[] showCheckBoxOptionDialog(Component parentComponent, String title, String message, Object[] options, boolean[] selected){
		JOptionPane optionPane = new MultipleOptionPane();
		JCheckBox[] boxes = new JCheckBox[options.length];

		JPanel checkBoxPanel = new JPanel(new GridLayout(options.length, 1));
		for (int i = 0; i < options.length; i++) {
			JCheckBox checkBox = new JCheckBox(options[i].toString());
			boxes[i] = checkBox;
			if (selected != null)
				checkBox.setSelected(selected[i]);
			checkBox.setActionCommand(new Integer(i).toString());
			checkBoxPanel.add(checkBox);
		}
		Object msg[] = { message, checkBoxPanel };
		optionPane.setMessage(msg);
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = optionPane.createDialog(parentComponent, title);
		dialog.setVisible(true);
		Object value = optionPane.getValue();
		// Schließen übers Kreuz oder irgendwas unvorhergesehenes
		if (value == null || !(value instanceof Integer) || options.length == 0)
			return null;
		// Schließen über einen der Knöpfe
		// Knopf ermitteln
		int i = ((Integer) value).intValue();
		// Schließen oder OK
		if (i == JOptionPane.CLOSED_OPTION || i == JOptionPane.OK_OPTION) {
			Object[] returnValue = new Object[options.length];
			for (int j = 0; j < options.length; j++)
				returnValue[j] = boxes[j].isSelected() ? options[j] : null;
			return returnValue;
			// Abbrechen gedrückt
		}
		return null;
	}

	/**
	 * Zeigt einen Optionen-Dialog an, der für jede übergebene Option eine Checkbox darstellt.<br>
	 * Alle Checkboxen sind nicht selektiert. 
	 * 
	 * @param parentComponent 
	 * 			Besitzerkomponente des Dialoges
	 * @param title 
	 * 			Titel des Dialoges
	 * @param message 
	 * 			Nachricht des Dialoges
	 * @param options 
	 * 			Optionen, die über Checkboxen zur Auswahl gestellt werden sollen. Diese Optionen werden über
	 * 			ihre toString()-Methode im Dialog angezeigt.
	 * @return Array der übergebenen Options. War die Option ausgewählt, ist sie nicht <code>null</code>, sost ist sie <code>null</code>
	 */
	public static final Object[] showCheckBoxOptionDialog(Component parentComponent, String title, String message, Object[] options){
		return showCheckBoxOptionDialog(parentComponent, title, message, options, null);
	}

	
	/**
	 * Infodialog mit OK.
	 * 
	 * @param parentComponent
	 * @param title
	 * @param message
	 */
	public static final void showInformationMessageDialog(Component parentComponent, String title, String message){
		JOptionPane optionPane = new MultipleOptionPane();
		Object msg[] = {message};
		optionPane.setMessage(msg);
		optionPane.setMessageType(JOptionPane.WARNING_MESSAGE);
		JDialog dialog = optionPane.createDialog(parentComponent, title);
		dialog.setVisible(true);
	}

	/**
	 * Confirm-Dialog
	 * 
	 * @param parentComponent
	 * @param title
	 * @param message
	 * @param options
	 */
	public static final int showConfirmDialog(Component parentComponent, String title, String message, int options, int messageType){
		JOptionPane optionPane = new MultipleOptionPane();
		Object msg[] = {message};
		optionPane.setMessage(msg);
		optionPane.setMessageType(messageType);
		optionPane.setOptionType(options);
		JDialog dialog = optionPane.createDialog(parentComponent, title);
		dialog.setVisible(true);
		Object value = optionPane.getValue();
		//Schließen übers Kreuz oder irgendwas unvorhergesehenes
		if (value == null || !(value instanceof Integer))
			return CANCEL_OPTION;
		//Schließen über einen der Knöpfe
		return ((Integer)value).intValue();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JOptionPane#getMaxCharactersPerLineCount()
	 */
	@Override
	public int getMaxCharactersPerLineCount() {
		return maxCharactersPerLineCount;
	}
	/**
	 * @param maxCharactersPerLineCount The maxCharactersPerLineCount to set.
	 */
	public void setMaxCharactersPerLineCount(int maxCharactersPerLineCount) {
		this.maxCharactersPerLineCount = maxCharactersPerLineCount;
	}
}
