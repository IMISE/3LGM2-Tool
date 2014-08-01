/*
 * Created on 01.04.2008
 *
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

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
 * Panel zur Darstellung von berechneten Kennzahlen
 * 
 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.ClassificationNumberEditorPanel
 * 
 * @author fstephan
 */
public class ClassificationNumberFormulaPanel extends ClassificationNumberEditorPanel{

	/**
	 * zeigt an, ob sich Daten geändert haben und drawTable aufgerufen
	 * werden muss
	 */
	private boolean needsRedraw;
	
	/**
	 * @param dialog
	 */
	public ClassificationNumberFormulaPanel(UserFieldEditorDialog dialog) {
		super(dialog);
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.ClassificationNumberEditorPanel#isNodeBoxContent(java.lang.Class, tool3lgm.graphtools.userfield.UserFieldDefinitions)
	 */
	@Override
	protected boolean isNodeBoxContent(Class<? extends ModelElement> elementClass, UserFieldDefinitions definitions){
		for (UserField uf : definitions.getUserFields(elementClass))
			if(uf.getStyle() == UserField.Style.CLASSIFICATION_NUMBER_FORMULA)
				return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.ClassificationNumberEditorPanel#setNodeBoxContent()
	 */
	@Override
	protected void setNodeBoxContent() {
		
		super.setNodeBoxContent();
		
		GraphDocument doc = this.getDialog().getGraphDocument();
		GDCollection gdcoll = doc.getCollection();
		UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
		
		// Hier werden jetzt die Kanten hinzugefügt, für die Kennzahlformeln definiert sind. 
		// Das passiert aber nur, wenn die <code>setNodeBoxContent()</code> 
		// implizit durch das super() des <code>ClassificationNumberFormulaPanel</code> aufgerufen wird.
		// Die lokale <code>isNodeBoxContent()</code> hier lässt das hinzufügen von Kanten nicht zu - die überschriebene Methode lässt das hinzufügen zu.  
		for (int i = 0; i < ModelConstants.ALL_EDGES.length; i++) {
			
			// Falls Verteilungsgewichte exisitieren, füge Kantenklasse ein
			if(isNodeBoxContent(ModelConstants.ALL_EDGES[i], definitions)){
				
			//if (definitions.getUserFieldCount(ModelConstants.ALL_EDGES[i]) > 0) {
				nodeBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullForwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
				nodeBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullBackwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
			}
			// sonst, füge nicht ein
		}
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#initTable()
	 */
	@Override
	protected UserFieldTable initTable() {
		UserFieldTableLayout uftl = UserFieldTableLayout.getLayoutForClassificationNumberFormulaTable();
		UserFieldTable table = new UserFieldTable(uftl);
		return table;
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#takeOver()
	 */
	@Override
	protected void takeOver() {
		this.needsRedraw = true;
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#drawTable()
	 */
    @Override
	protected void drawTable() {
		table.removeFromLayoutContainer();
		if (nodeBox.getSelectedObject() == null || !(nodeBox.getSelectedObject() instanceof Class))
			return;
		Class<? extends ModelElement> selectedClass = (Class<? extends ModelElement>)nodeBox.getSelectedObject();
		UserFieldTableModel uftm = UserFieldTableModel.createClassificationNumberFormulaModel(selectedClass, getDialog().getGraphDocument(), typePane.showTopLevel(), typePane.showInner(), typePane.showLeafs());
		UserFieldTableController tec = UserFieldTableController.getNewClassificationNumberFormulaTableController(uftm);
		super.modifyTable(uftm, tec);
	}
	
	/**
	 * Falls sich Daten geändert haben, wird der Table neu gezeichnet
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b == true && needsRedraw == true) {
			needsRedraw = false;
			this.drawTable();
		}
	}
	
	/**
	 * @see ClassificationNumberEditorPanel#setActionsForNodeBox()
	 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.ClassificationNumberEditorPanel#setActionsForNodeBox()
	 */
	@Override
	protected void setActionsForNodeBox() {
		final UserFieldEditorPanel pane = this;
		this.nodeBox.setAction(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				stopEditing();
				drawTable();
				pane.distributeSelectionChangedEvent();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#stopEditing()
	 */
	@Override
	public void stopEditing() {
		// tue nichts, weil keine Zelle editierbar ist
	}
}
