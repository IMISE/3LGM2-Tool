/*
 * Created on 11.12.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog.tools;

import javax.swing.table.AbstractTableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.Szenario;

/**
 * @author Thomas Rudert
 *
 * Tabellenmodell zum auflisten der Szenarios (mit Titel und Beschreibung) eines GDCollection und auswahl einzelner Szenarios
 */
public class SzenarioTableModel extends AbstractTableModel {
		
	private Boolean[] selections;
	private GDCollection collection;
	private String selectionColName;
			
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return collection.getNumberOfSzenarios();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 3;
	}

	/**
	 * erstellt das Tabellemodell
	 * @param collection GDCollection mit den Szenarios
	 * @param selectioColName Titel der Spalte zum Auswählen der Szenarios 
	 */
	public SzenarioTableModel(GDCollection collection, String selectioColName) {
		super();
		this.collection = collection;
		this.selectionColName = selectioColName;
		selections = new Boolean[collection.getNumberOfSzenarios()];
		for (int i = 0; i < collection.getNumberOfSzenarios(); i++)
			selections[i] = new Boolean(false);
	}
		
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return selectionColName;
			case 1: return Tool3lgmConstants.getResString("name");
			case 2: return Tool3lgmConstants.getResString("description");
			default: return null;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		switch(col) {
			case 0:
				if (row < getRowCount())
					return selections[row];
				break;
			case 1:
				if (row < getRowCount())
					return collection.getSzenario(row).getTitle();
				break;
			case 2:
				if (row < getRowCount())
					return collection.getSzenario(row).getDescription();
				break;
		}
		return null;
	}
		
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int col) {
		switch(col) {
			case 0:
				return Boolean.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
		}
		return null;			
	}
		
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 0 && row < getRowCount())
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (!isCellEditable(row, column))
			return;
		selections[row] = (Boolean) aValue;
	}
	
	/**
	 * selektiert alle Szenarios
	 */
	public void selectAll() {
		for (int i = 0; i < getRowCount(); i++)
			setValueAt(new Boolean(true), i, 0);
	}
	
	/**
	 * gibt die ausgewählten Seznarios zurück
	 * @return Array mit den selektierten Szenarios
	 */
	public Szenario[] getSelectedSzenarios() {
		int counter = 0;
		for (int i = 0; i < selections.length; i++)
			if (selections[i].booleanValue())
				counter++;
		Szenario[] szenarios = new Szenario[counter];
		counter = 0;
		for (int i = 0; i < selections.length; i++)
			if (selections[i].booleanValue())
				szenarios[counter++] = collection.getSzenario(i);
		return szenarios;
	}

}
