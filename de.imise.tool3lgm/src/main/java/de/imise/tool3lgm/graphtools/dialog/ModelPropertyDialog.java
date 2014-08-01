/*
 * Created on 05.11.2003
 *
 */
package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.text.ExtendedTextPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.log.Log;
/**
 * Eigenschaftendialog für ein Modell
 * 
 * @author AXS
 */
public final class ModelPropertyDialog extends PropertyDialog {

	/**
	 * Textpane in dem die Beschreibungen angezeigt werden und editiert werden können.
	 */
	private ExtendedTextPane textPane;
	
	/**
	 * Letzte Position und Größe dieses Dialogs.
	 */
	private int lastWidth = -1, lastHeight = -1, lastPositionX = -1, lastPositionY = -1;

	/**
	 * Das {@link GraphDocument}, das vor einer Änderung des aktiven {@link GraphDocument} aktiv war.
	 */
	private GraphDocument lastActiveDoc = null;
	
	/**
	 * Auswahlbox für das aktive (Teil-)Modell
	 */
	private AlphabeticalComboBox docBox = new AlphabeticalComboBox();
	
	/**
	 * @param String gdcoll
	 * @throws java.awt.HeadlessException
	 */
	public ModelPropertyDialog(GDCollection gdcoll) throws HeadlessException {
		super(gdcoll);

		addComponentListener(new ComponentListener(){
			@Override
			public void componentHidden(ComponentEvent e){} 
			@Override
			public void componentMoved(ComponentEvent e){dialogPositionOrSizeChanged();} 
			@Override
			public void componentResized(ComponentEvent e){dialogPositionOrSizeChanged();} 
			@Override
			public void componentShown(ComponentEvent e){} 
		});

		try {
		  init();
		}
		catch(Exception e) {
		  Log.show(Log.FATAL, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
		}
	}

	/**
	 * @throws Exception
	 */
	private void init() throws Exception {

		getContentPane().setLayout(new BorderLayout());
		if (lastWidth == -1) {
			lastWidth = 600;
			lastHeight = 400;
			lastPositionX=0;
			lastPositionY=0;
		}
		setLocation(lastPositionX, lastPositionY);
		setSize(lastWidth,lastHeight);

		textPane = new ExtendedTextPane();
		textPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
		getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);

		docBox.addItem(gdcoll.getMainGraphDocument());
		for (int i = 0; i < gdcoll.getNumberOfSzenarios(); i++)
			docBox.addItem(gdcoll.getSzenario(i));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(new JLabel(Tool3lgmConstants.getResString("submodel")), BorderLayout.WEST);
		northPanel.add(docBox, BorderLayout.CENTER);
		getContentPane().add(northPanel, BorderLayout.NORTH);

		
		
		docBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedDocChanged();
			}
		});
		
		docBox.setSelectedItem(gdcoll.getSelectedDoc());
	}


	/**
	 * Aktualisiert die Anzeige.
	 */
	public void update() {
		//prüfen, ob alle Teilmodelle in der Auswahlbox vorhanden sind
		for (int i = 0; i < gdcoll.getNumberOfSzenarios(); i++) {
			boolean found = false;
			GraphDocument szen = gdcoll.getSzenario(i);
			for (int j = 0; j < docBox.getItemCount(); j++) {
				Object item = docBox.getItemAt(j);
				if (!(item instanceof Szenario))
					continue;
				if (item == szen) {
					found = true;
					break;
				}
			}
			if (!found)
				docBox.addItem(szen);
		}
		//prüfen, ob alle auswählbaren Teilmodelle noch vorhanden sind
		for (int j = 0; j < docBox.getItemCount(); j++) {
			Object item = docBox.getItemAt(j);
			if (!(item instanceof Szenario))
				continue;
			if (!gdcoll.getSzenarios().contains(item))
				docBox.removeItem(item);
		}
		actualizeFrameTitle();
		docBox.revalidate();
		docBox.repaint();
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b){
		if (b)
			docBox.setSelectedItem(gdcoll.getSelectedDoc());
		super.setVisible(b);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JDialog#processWindowEvent(java.awt.event.WindowEvent)
	 */
	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			gdcoll.showDescriptionFrame(false);
			lastActiveDoc.setDescription(textPane.getText());
		}
		if (e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
			lastActiveDoc.setDescription(textPane.getText());
		}
	}
	

	/**
	 * Setzt den Titel des Dialogs
	 */
	private void actualizeFrameTitle() {
		GraphDocument doc = (GraphDocument)docBox.getSelectedItem();
		if (doc != null) {
			setTitle(Tool3lgmConstants.getResString("description")
				+ " - " 
				+ gdcoll.getName() 
				+ " - " 
				+ doc.getTitle());
		} else {
				setTitle(Tool3lgmConstants.getResString("description")
					+ " - " 
					+ gdcoll.getName() 
					+ " - " 
					+ Tool3lgmConstants.getResString("uebersicht"));
		}
	}
	
	/**
	 * Wird ausgeführt, wenn sich das {@link GraphDocument} ändert, dessen BEschreibung angezeigt werden soll.
	 * Erst wird für das zuletzt aktive {@link GraphDocument} die eingegebene Beschreibung gesetzt, dann wird
	 * die Beschreibung des aktivierten {@link GraphDocument}s angezeigt.
	 */
	private void selectedDocChanged() {
		GraphDocument activeDoc = (GraphDocument)docBox.getSelectedItem();
		if (activeDoc == lastActiveDoc)
			return;
		actualizeFrameTitle();
		if (lastActiveDoc != null)
			lastActiveDoc.setDescription(textPane.getText());
		if (activeDoc != null) {
			lastActiveDoc = activeDoc;
			textPane.setText(activeDoc.getDescription());
		}
	}


	private void dialogPositionOrSizeChanged(){
		lastWidth = getWidth();
		lastHeight = getHeight();
		lastPositionX = getX();
		lastPositionY = getY();
	}
	
}