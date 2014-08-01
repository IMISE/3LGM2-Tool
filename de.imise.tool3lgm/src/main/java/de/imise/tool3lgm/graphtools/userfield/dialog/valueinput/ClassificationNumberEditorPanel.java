/*
 * Created on 14.02.2008
 *
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableModel;


/**
 * Panel zur Darstellung und Eingabe von Kennzahlen
 * <p>
 * Die Dateneingabe erfolgt für jede Klasse von Knotenelementen separat - die Auswahl der
 * Klasse erfolgt in der enthaltenen <code>nodeBox</code>.
 * 
 * <p>
 * Mittels der enthaltenen Auswahlbuttons kann eine Auswahl der anzuzeigenden Elemente 
 * vorgenommenwerden:
 * <br>i) alle Elemente anzeigen
 * <br>ii) nur toplevel Elemente anzeigen
 * <br>iii) nur Blatt-Elemente anzeigen
 * 
 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel
 * @author fstephan
 */
public class ClassificationNumberEditorPanel extends UserFieldEditorPanel{
	
	/** Auswahlbox für den Elementtyp */
	protected AlphabeticalComboBox nodeBox;
	
	
	/** Panel zur Auswahl der anzuzeigenden Element-Typen */
	protected ElementTypePane typePane;
	
	
	
	
	/* ************************* Beginn: Initialisierungsteil *********************************** */
	
	/**
	 * Konstruktor
	 * 
	 * @param dialog
	 * 			Dialog, der dieses Panel enthält
	 */
	public ClassificationNumberEditorPanel(UserFieldEditorDialog dialog) {
		super(dialog);
		
		// nodeBox initialisieren
		nodeBox = new AlphabeticalComboBox(10);
		
		setActionsForNodeBox();
		setNodeBoxContent();
		
		typePane = new ElementTypePane();
		
		// nodeBox anfügen
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,5,0,5);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		constraints.weightx=1;
		this.add(this.nodeBox,constraints);
		constraints.gridy++;
		// radioPane anfügen
//		constraints.gridx = 1;
		this.add(this.typePane,constraints);
	}	
	
	/**
	 * Methode setzt den Inhalt der <code>nodeBox</code>
	 * Es werden nur die Klassen von Knotenelementen aufgelistet, für die mindestens ein
	 * Kennzahl vom Typ <code>UserField.CLASSIFICATION_NUMBER_STYLE</code> definiert ist.
	 */
	protected void setNodeBoxContent() {
		
		GraphDocument doc = this.getDialog().getGraphDocument();
		GDCollection gdcoll = doc.getCollection();
		UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
		
		nodeBox.addSeparator(true);

		for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_NODES_SET) {
			// Falls keine Kennzahlen für elementClass exisitieren, füge elementClass NICHT ein
			if (!definitions.hasUserFields(elementClass))
				continue;
			
			// Falls kein Element der Klasse elementClass exisitiert, füge elementClass NICHT ein
			if(doc.getModelItems(elementClass).size() == 0)
				continue;

			// füge elementClass in die nodeBox ein
			if(isNodeBoxContent(elementClass, definitions))
				nodeBox.addItem(elementClass, ModelConstants.getDisplayableName(elementClass));
		}
		
		/*
		 * Falls kein Item enthalten ist, wird ein null-Item hinzugefügt, weil es sonst
		 * zu inkorrekter Darstellung der nodeBox kommt
		 */
		if (this.nodeBox.getItemCount() == 1) {
			nodeBox.addItem(new Object(),"         ");
		}
		
	}
	
	/**
	 * Prüft, ob mindestens ein <code>UserField</code> der übergebenen <code>elementClass</code> vom Typ Kennzahl ist.
	 * Dann wird true zurückggegeben ansonsten false.
	 * @param elementClass 
	 * 		Die <code>ElementClass</code>e, deren <code>UserField</code>s geprüft werden sollen.
	 * @param definitions
	 * 		Die <code>UserFieldDefinition</code>s
	 * @return
	 * 		Wenn mindestens ein <code>UserField</code> vom Typ Kennzahl (<code>UserField.CLASSIFICATION_NUMBER_STYLE</code>) ist: true; 
	 * 		ansonsten false 
	 */
	protected boolean isNodeBoxContent(Class<? extends ModelElement> elementClass, UserFieldDefinitions definitions) {
		for (UserField uf : definitions.getUserFields(elementClass))
			if (uf.hasStyle(UserField.Style.CLASSIFICATION_NUMBER))
				return true;
		return false;
	}
	
	/**
	 * Methode setzt die auszuführende Action, bei Änderung der Auswahl in der
	 * <code>nodeBox</code>.
	 * 	
	 * Bei Änderung der Auswahl wird das Neuzeichnen des <code>table</code>s
	 * ausgelöst.
	 */
	protected void setActionsForNodeBox() {
		final UserFieldEditorPanel pane = this;
		this.nodeBox.setAction(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				stopEditing();
				takeOver();
				drawTable();
				pane.distributeSelectionChangedEvent();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#initTable()
	 */
	@Override
	protected UserFieldTable initTable() {
		UserFieldTableLayout uftl = UserFieldTableLayout.getLayoutForClassificationNumberEditorTable();
		UserFieldTable table = new UserFieldTable(uftl);
		return table;
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#constraintsForTable()
	 */
	@Override
	protected Object constraintsForTable() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5,5,5,5);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridwidth = 3;
		return constraints;
	}
	
	/* ************************* Ende: Initialisierungsteil *********************************** */
	
	
	/* ************************* Beginn: Funktionale Methoden *********************************** */
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#drawTable()
	 */
	@Override
	protected void drawTable() {
		table.removeFromLayoutContainer();
		if (nodeBox.getSelectedObject() == null || !(nodeBox.getSelectedObject() instanceof Class))
			return;
		Class<? extends ModelElement> selectedClass = ((Class<?>)nodeBox.getSelectedObject()).asSubclass(ModelElement.class);

		UserFieldTableModel uftm = UserFieldTableModel.createClassificationNumberModel
		(
			selectedClass, getDialog().getGraphDocument(),
			typePane.showTopLevel(), typePane.showInner(), typePane.showLeafs()
		);
		UserFieldTableController uftc = UserFieldTableController.getNewClassificationNumberTableController(uftm);

		super.modifyTable(uftm, uftc);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#takeOver()
	 */
	@Override
	protected void takeOver() {
		if (!(table.getModel() instanceof UserFieldTableModel)) 
			return; // noch keine Werte vorhanden
		UserFieldTableModel uftm = (UserFieldTableModel)table.getModel();
		if(uftm.dataChanged() == false)
			return;
		GraphDocument doc = getDialog().getGraphDocument();
		Vector<Object> rowIdentifiers = uftm.getRowIdentifiers();
		Vector<Object> columnIdentifiers = uftm.getColumnIdentifiers();
		GDCollection gdcoll = doc.getCollection();
		UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
		for (int i = 0; i < uftm.getRowCount(); i++) {
			// Das ModelElement in der i-ten Zeile
			ModelElement me  = ((NamedObjectContainer<ModelElement>)rowIdentifiers.elementAt(i)).getObject();
			for (int k = 0; k < uftm.getColumnCount(); k++) {
				// Wert an der Stelle (i,k)
				String newValue = uftm.getValueAt(i, k).toString();
				// Die Kennzahl in der Spalte k
				UserField uf = ((NamedObjectContainer<UserField>)columnIdentifiers.elementAt(k)).getObject();
				// neuen Wert setzen
				if (!newValue.equals(uf.getValue(me)))
					doc.changeUserField(me.getHashString(),uf.getHashCode(),newValue, getDialog().getTransactionID());
			}
		}
		((UserFieldTableModel)this.table.getModel()).dataChanged(false);
		//Das reset durchführen kann auch ganz auf das Ende verlegt werden
		definitions.initReset();
	}
	
	/* ************************* Ende: Funktionale Methoden *********************************** */
	
	
	
	/* ************************* Beginn: get/set - Methoden *********************************** */
	
	/**
	 * Gibt die <code>nodeBox</code> zurück
	 * @return <code>nodeBox</code>
	 */
	public AlphabeticalComboBox getNodeBox() {
		return this.nodeBox;
	}
	
	/* ************************* Ende: get/set - Methoden *********************************** */
	
	
	
	
	/* ************************* Beginn: Unterklassen *********************************** */
	
	/**
	 * Panel zur Auswahl der anzuzeigenden Element-Typen:
	 * 	<li>Top-Level
	 * 	<li>Innere
	 * 	<li>Blätter
	 */
	protected class ElementTypePane extends JPanel {
		
		// CheckBoxes für die Auswahl der anzuzeigenden Elemente
		private JCheckBox topLevel;
		private JCheckBox inner;
		private JCheckBox leafs;
		
		public ElementTypePane() {
			topLevel 	= new JCheckBox(getActionForButtons(Tool3lgmConstants.getResString("hierarchy_panel_show_toplevel")));
			inner 		= new JCheckBox(getActionForButtons(Tool3lgmConstants.getResString("hierarchy_panel_show_inner")));
			leafs 		= new JCheckBox(getActionForButtons(Tool3lgmConstants.getResString("hierarchy_panel_show_leafs")));
			topLevel.setSelected(true);
			inner.setSelected(true);
			leafs.setSelected(true);
			add(topLevel);
			add(inner);
			add(leafs);
		}
		
		/**
		 * Gibt die auzuführenden Aktionen für die CheckBoxes wieder.
		 * 
		 * Bei Änderung der Selektion werden Änderungen temporär übernommen und
		 * das Neuzeichnen des Tables ausgelöst.
		 * 
		 * @param panel
		 * @param buttonLabel
		 * @return
		 */
		private AbstractAction getActionForButtons(String buttonLabel) {
			return new AbstractAction(buttonLabel) {
				@Override
				public void actionPerformed(ActionEvent e) {
					AlphabeticalComboBox box = getNodeBox();
					if (box.getSelectedObject() == null || !(box.getSelectedObject() instanceof Class))
						return;
					// Beenden des editierens, damit auch aktueller Wert übernommen wird
					stopEditing();
					// Änderungen werden übernommen
					takeOver();
					// Aktualisieren des Tables
					drawTable();
					distributeSelectionChangedEvent();
				}
			};
		}
		
		/** Gibt zurück, ob {@link #topLevel} selektiert ist */
		public boolean showTopLevel() {
			return topLevel.isSelected();
		}

		/** Gibt zurück, ob {@link #leafs} selektiert ist */
		public boolean showLeafs() {
			return leafs.isSelected();
		}

		/** Gibt zurück, ob {@link #inner} selektiert ist */
		public boolean showInner() {
			return inner.isSelected();
		}
		
	}
	
	/* ************************* Ende: Unterklassen ************************************* */
	
}
