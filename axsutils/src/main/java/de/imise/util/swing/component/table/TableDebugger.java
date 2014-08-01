package de.imise.util.swing.component.table;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * Klasse für das Debuggen von {@link JTable}s.
 * <p>
 * <b>Hinweis:</b> An einen Table werden hier üblicherweise Listener angehängt, welche
 * anschließend nicht entfernt werden. Daher sollte diese Klasse auch nur zum
 * Debuggen verwendet werden, nicht aber beim "tatsächlichen Bertieb".
 * 
 * @author fstephan
 */
public class TableDebugger {
	
	/** TableModel für das überprüfen der Selektion in einem JTable */
	protected static class SelectionCheckModel extends AbstractTableModel {

		protected JTable source;
		
		public SelectionCheckModel(JTable source) {
			this.source = source;
			ListSelectionListener lsl = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					SelectionCheckModel.this.source.revalidate();
					SelectionCheckModel.this.source.repaint();
				}
			};
			this.source.getSelectionModel().addListSelectionListener(lsl);
			this.source.getColumnModel().getSelectionModel().addListSelectionListener(lsl);
        }

		@Override
        public int getColumnCount() {
	        return source.getColumnCount();
        }

		@Override
        public int getRowCount() {
	        return source.getRowCount();
        }

		@Override
        public Object getValueAt(int rowIndex, int columnIndex) {
			return source.isCellSelected(rowIndex, columnIndex);
        }

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}
	
	/**
	 * Erzeugt eine Tabelle, welche den Selektionszustand der einzelnen Zellen
	 * in der Ausgangstabelle darstellt.
	 * 
	 * @param source
	 * 			Die Ausgangstabelle
	 * @param show <ul>
	 * 				<li><code>true</code>:  Es wird ein Dialog geöffnet, der die Debugging-Tabelle anzeigt</li>
	 * 				<li><code>false</code>: Die Tabelle wird nur erzeugt und zurückgegeben </li></ul>
	 * @param owner
	 * 			Der WindowOwner für den Dialog. (optional)
	 * 
	 * @return Tabelle der Selektionswerte
	 */
	public static JTable selectionDebugger(final JTable source, boolean show, Window owner) {
		return debugger(new SelectionCheckModel(source), show, owner);
	}
	
	/**
	 * Erzeugt eine Tabelle zum Debuggen auf Basis des übergebenen TableModel.
	 * 
	 * @param debuggingModel
	 * 			Das Modell, welches als Basis für die hier erzeugte Tabelle dient.
	 * @param show <ul>
	 * 				<li><code>true</code>:  Es wird ein Dialog geöffnet, der die Debugging-Tabelle anzeigt</li>
	 * 				<li><code>false</code>: Die Tabelle wird nur erzeugt und zurückgegeben </li></ul>
	 * @param owner
	 * 			Der WindowOwner für den Dialog. (optional)
	 * 
	 * @return Tabelle der Selektionswerte
	 */
	public static JTable debugger(TableModel debuggingModel, boolean show, Window owner) {
		JTable t = new JTable(debuggingModel);
		if (show) {
			JDialog d = new JDialog(owner);
			d.add(new JScrollPane(t));
			d.pack();
			d.setVisible(true);
		}
		return t;
	}
}
