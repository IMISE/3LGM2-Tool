package de.imise.tool3lgm.graphtools.analyse.context;

import java.awt.Component;
import java.awt.Window;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.xml.sax.SAXException;

import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.dialog.SimpleXMLEditor;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;

/**
 * Tabelle zum Anzeigen aller Analysen im Repository-Dialog. Die Tabelle hat ihr TableModel als
 * innere Klasse
 * 
 * Originalautoren: Sebastian Weber, Thomas
 * 
 * @author AXS
 * created on 15.08.2007 aus inneren Klassen
 */
public class AnalyseRepositoryFrameTable extends JTable {

	/**
	 * Legt eine neue Tabelle mit den in der Liste enthaltenen Analysen an.<br>
	 * @param analysen
	 */
	public AnalyseRepositoryFrameTable() {
		super();
		setModel(new AnalyseTableModel(this));
		getTableHeader().setReorderingAllowed(false);
		setShowGrid(true);
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(0).setMinWidth(100);
		getColumnModel().getColumn(2).setPreferredWidth(60);
		getColumnModel().getColumn(2).setMinWidth(60);
		getColumnModel().getColumn(2).setMaxWidth(60);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTable#getCellEditor(int, int)
	 */
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 2)
			return new TableCellButtonEdior();
		if (column == 1)
			return new DefaultCellEditor(new ExtendedTextField());
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 2)
			return new TableCellButtonRenderer();
		return new AnalyseRepositoryFrameTableCellRenderer();
	}

	/**
	 * Veranlasst das Model sich und somit die Tabelle neu aufzubauen.
	 */
	public final void update() {
		((AnalyseTableModel)dataModel).fireTableDataChanged();
	}
	
	////////////////
	// TableModel //
	////////////////
	
	/**
	 * Das TableModel der Tabelle.
	 * 
	 * @author Sebastian Weber, Thomas
	 */
	private class AnalyseTableModel extends AbstractTableModel {
		
		/**
		 * Irgendeine Komponente, die als Parent für einen Fehlerdialog dienen kann. 
		 */
		private Component parent;
		
		/**
		 * Legt ein neues Model an. Die Parentkomponente wird gebraucht, wenn bei einem
		 * Fehler ein Infodialog ausgegeben werden muss.
		 * @param parent Komponente, die als Parent für einen Fehlerdialog dienen kann.
		 */
		private AnalyseTableModel(Component parent) {
			super();
			this.parent = parent;
		}
		
		/**
		 * Tabellen Kopfzeilenbeschriftungen
		 */
		String[] colheads = { 	Tool3lgmConstants.getResString("start_element_type"), 
								Tool3lgmConstants.getResString("ana_name"), 
								Tool3lgmConstants.getResString("definition")};

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return (AnalyseRepositoryFrame.analysen != null) ? AnalyseRepositoryFrame.analysen.size() : 0;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() { 
			return 3; 
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			return colheads[column];
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int row, int column) {
			if (AnalyseRepositoryFrame.analysen != null) {
				XMLAnalyse ana = AnalyseRepositoryFrame.analysen.get(row);
				switch (column) {
					case 0 : return ana.getStartknotenString();
					case 1 : return ana.getName();
					case 2 : return ana.getXMLText();
				}
			}
			fireTableRowsUpdated(row, row);
			return "";
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int row, int column) {
			return (column > 0);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(Object o, int row, int column) {
			switch (column) {
				case 1: {
					String neuerName = (o != null) ? o.toString() : "";
					XMLAnalyse ana = AnalyseRepositoryFrame.analysen.get(row);  
					if (!AnalyseRepository.containsName(AnalyseRepositoryFrame.analysen, ana, neuerName)) {
						ana.setName(neuerName);
						AnalyseRepositoryFrame.analysisChanged = true;
					}else 
						JOptionPane.showConfirmDialog(parent, Tool3lgmConstants.getErrString("AnalyseExistiert"), Tool3lgmConstants.getResString("fehler"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					break;
				}
				case 2: {
					try {
						AnalyseRepositoryFrame.analysen.get(row).setXMLText((o != null) ? o.toString() : "");
					} catch (SAXException ex) {
						Log.show(Log.ERROR, Tool3lgmConstants.getErrString("AnalyseNichtErstellt") + "\n" + ex.getMessage(), ex);
					}
					break;
				}
			}
			fireTableCellUpdated(row, column);
		}
		
	}

	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		AnalyseRepositoryFrame.refreshActionStates();
	}

	//////////////////////////////////////////////////////////////////////
	// CellEditor für das Ändern von Analysetextes (Button in Spalte 3) //
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * @author Sebastian Weber, Thomas
	 *
	 */
	public class TableCellButtonEdior extends AbstractCellEditor implements TableCellEditor{

		/**
		 * Comment for <code>analyseText</code>
		 */
		private String analyseText;
		
		/**
		 * @param txt
		 */
		public TableCellButtonEdior() {
			super();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
		 */
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
			analyseText = value.toString();
			getVal(table, row, col);
			table.setEditingColumn(-1);
			table.setEditingRow(-1);
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.CellEditor#getCellEditorValue()
		 */
		@Override
		public Object getCellEditorValue() {
			return analyseText;
		}

		/**
		 * 
		 * @param table
		 * @param row
		 * @param col
		 */
		private void getVal(JTable table, int row, int col) {
			Window parent = JOptionPane.getFrameForComponent(table);
			SimpleXMLEditor dialog;
			if (parent instanceof JFrame)
				dialog = new SimpleXMLEditor((JFrame) parent, true, Tool3lgmConstants.getResString("xml_input"), analyseText);
			else
				dialog = new SimpleXMLEditor((JDialog) parent, true, Tool3lgmConstants.getResString("xml_input"), analyseText);
			switch (dialog.showDialog()) {
				case SimpleXMLEditor.OK: {
					String val = dialog.getText();
					if (val != null && !analyseText.equals(val)) {
						analyseText = val;
						AnalyseRepositoryFrame.analysisChanged = true;
						table.setValueAt(val, row, col);
					}
					break;
				}
			}
		}
	}

	/////////////////////////////////////////
	// Renderer für die Buttons in Spalte 3//
	/////////////////////////////////////////
	
	/**
	 * @author Sebastian Weber, Thomas
	 */
	public class TableCellButtonRenderer extends DefaultTableCellRenderer{
		
		/**
		 * COMMENTME
		 */
		private JButton btnpick = new JButton("...");
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
			return btnpick;
		}
		
		
		/**
		 * @return Returns the btnpick.
		 */
		public JButton getBtnpick() {
			return btnpick;
		}
	}



}


