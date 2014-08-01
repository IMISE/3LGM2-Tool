/*
 * Created on 14.02.2008
 *
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput;

import java.awt.GridBagConstraints;
import java.util.Vector;

import de.imise.util.NamedObjectContainer;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableModel;

/**
 * Panel zur Eingabe von Modelvariablen
 * 
 * @author fstephan
 */
public class ModelVariableEditorPanel extends UserFieldEditorPanel{
	
	/**
	 * Kosntruktor
	 * 
	 * @param dialog
	 */
	public ModelVariableEditorPanel(UserFieldEditorDialog dialog) {
		super(dialog);
		drawTable();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#takeOver()
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void takeOver() {
		
		GraphDocument doc = this.getDialog().getGraphDocument();
		GDCollection gdcoll = doc.getCollection();
		UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
		
		if (!(this.table.getModel() instanceof UserFieldTableModel)) 
			return; // noch keine Werte vorhanden
		UserFieldTableModel uftm = (UserFieldTableModel) this.table.getModel();
		
		Vector<NamedObjectContainer<UserField>> rowIdentifiers = (Vector) uftm.getRowIdentifiers();
		for (int i=0; i<rowIdentifiers.size();i++) {
			UserField field = rowIdentifiers.get(i).getObject();
			String value = uftm.getValueAt(i,0).toString();
			gdcoll.setUserFieldInputValue(field, value);
		}
		definitions.initReset();
		
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#initTable()
	 */
	@Override
	protected UserFieldTable initTable() {
		
		UserFieldTableLayout uftl = UserFieldTableLayout.getLayoutForModelVariableEditorTable();
		UserFieldTable table = new UserFieldTable(uftl);
		
		return table;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#constraintsForTable()
	 */
	@Override
	protected Object constraintsForTable() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		return constraints;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorPanel#drawTable()
	 */
	@Override
	protected void drawTable() {
		UserFieldTableModel uftm = UserFieldTableModel.createClassificationModelVariableModel(this.getDialog().getGraphDocument());
		UserFieldTableController tec = UserFieldTableController.getNewModelVariableTableController(uftm);
		super.modifyTable(uftm,tec);
	}
}
