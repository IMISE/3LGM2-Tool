/*
 * Created on 29.06.2004
*/
package de.imise.util.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.util.swing.component.SimpleXMLTextPane;

/**
 * AXS: eigentlich sollte sowas genau wie JOptionPane funktionieren, also das hier wird ein JPanel namens
 * XMLEditorPane und dieses Panel hat dann 2 statische Funktionen String showDialog(...) und String showFrame(...)
 * 
 * @author Sebastian Weber
 * Stellt einen einfachen Editor für XML-Skripte zur Verfügung.
 */
public class SimpleXMLEditor extends JDialog implements ActionListener {
	/** eine TextPane zur anzeige des xml Textes mit Syntaxhervorhebung. */
	SimpleXMLTextPane pane;
	/** Knopf zum Beenden des Dialoges. Die Variable Auswahl wird auf OK gesetzt. */
	JButton okButton;
	/** Knopf zum Abbrechen des Dialoges. Die Variable Auswahl wird auf ABBRECHEN gesetzt. */
	JButton cancelButton;
	/** Nach dem Schließen des Dialoges sieht man hier mit welcher Option (OK oder ABBRECHEN) der Dialog geschlossen wurde. */
	int auswahl = 0;
	/** Konstante zur Angabe, daß der Dialog mit OK geschlossen wurde. */
	public final static int OK = 1;
	/** Konstante zur Angabe, daß der Dialog mit Abbrechen geschlossen wurde. */
	public final static int ABBRECHEN = 0;
	
	/**
	 * Der Konstruktor.
	 * @param owner		Besitzerframe für diesen Dialog.
	 * @param modal 	auf true setzen, wenn der Dialog die Ausführung des Besitzerframes blockieren soll.
	 * @param title		Titel für diesen Dialog.
	 * @param xmlText	Ein xmlText, der nach dem Öffnen angezeigt werden soll.
	 */
	public SimpleXMLEditor(Frame owner, boolean modal, String title, String xmlText) {
		super(owner, title, modal);
		init(modal, title, xmlText);
	}

	/**
	 * Der Konstruktor.
	 * @param owner		Besitzerdialog für diesen Dialog.
	 * @param modal 	auf true setzen, wenn der Dialog die Ausführung des Besitzerdialoges blockieren soll.
	 * @param title		Titel für diesen Dialog.
	 * @param xmlText	Ein xmlText, der nach dem Öffnen angezeigt werden soll.
	 */
	public SimpleXMLEditor(Dialog owner, boolean modal, String title, String xmlText) {
		super(owner, title, modal);
		init(modal, title, xmlText);
	}
	
	private void init(boolean modal, String title, String xmlText) {
		pane = new SimpleXMLTextPane(xmlText);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new JScrollPane(pane), BorderLayout.CENTER);
		
		DialogResourceHandler drh = new DialogResourceHandler(null);
		okButton = new JButton(drh.getResString("ok"));
		okButton.setMnemonic(okButton.getText().charAt(0));
		okButton.addActionListener(this);
		cancelButton = new JButton(drh.getResString("cancel"));
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(cancelButton.getText().charAt(0));

		
		JPanel south = new JPanel(new FlowLayout());
		south.add(okButton);
		south.add(cancelButton);
		contentPane.add(south, BorderLayout.SOUTH);
		
		setSize(640, 480);
		setLocationRelativeTo(getOwner());
	}
	
	/**
	 * Wird intern aufgerufen, wenn einer der Knöpfe gedrückt wurde.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) auswahl = OK;
		if (e.getSource() == cancelButton) auswahl = ABBRECHEN;
		this.dispose();
	}
	
	/**
	 * Zweigt den Dialog an und gibt die Option zurück, mit der er geschlossen wurde.
	 * @return	die Option mit der der Dialog geschlossen wurde (OK oder ABBRECHEN).
	 */
	public int showDialog() {
		setVisible(true);
		return auswahl;
	}
	
	/**
	 * Gibt den xml-Text der in diesem Dialog angezeigt wird, zurück. 
	 * @return	der angezeigte xml-Text.
	 */
	public String getText() {
		return pane.getText();
	}
}
