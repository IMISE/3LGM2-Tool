package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

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
import javax.swing.table.TableColumn;

import org.xml.sax.SAXException;

import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.dialog.SimpleXMLEditor;

/**
 * Tabelle zum Anzeigen aller Analysen im Repository-Dialog. Die Tabelle hat ihr
 * TableModel als innere Klasse Originalautoren: Sebastian Weber, Thomas
 *
 * @author AXS created on 15.08.2007 aus inneren Klassen
 */
public class AnalysesRepositoryFrameTable extends JTable {

    /**
     * Das TableModel der Tabelle.
     *
     * @author Sebastian Weber, Thomas
     */
    private class AnalyseTableModel extends AbstractTableModel {

        /**
         * Tabellen Kopfzeilenbeschriftungen
         */
        String[] colheads = {
                getResString("start_element_type"), getResString("ana_name"), getResString("definition")
        };

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(final int column) {
            return colheads[column];
        }

        @Override
        public int getRowCount() {
            return AnalysesRepositoryFrame.analysen != null ? AnalysesRepositoryFrame.analysen.size() : 0;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            if (AnalysesRepositoryFrame.analysen != null) {
                XMLAnalysis ana = AnalysesRepositoryFrame.analysen.get(row);
                switch (column) {
                case 0:
                    return ana.getStartClassesDisplayNames();
                case 1:
                    return ana.getName();
                case 2:
                    return ana.getXMLText();
                }
            }
            fireTableRowsUpdated(row, row);
            return "";
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return column > 0;
        }

        @Override
        public void setValueAt(final Object o, final int row, final int column) {
            switch (column) {
            case 1: {
                String name = o != null ? o.toString() : "";
                XMLAnalysis ana = AnalysesRepositoryFrame.analysen.get(row);
                ana.setName(name);
                AnalysesRepositoryFrame.analysesChanged = true;
                break;
            }
            case 2: {
                try {
                    AnalysesRepositoryFrame.analysen.get(row).setXMLText(o != null ? o.toString() : "");
                } catch (SAXException ex) {
                    Log.show(Log.ERROR, getResString("ANALYSIS_CANT_CREATE") + "\n" + ex.getMessage(), ex);
                }
                break;
            }
            }
            fireTableCellUpdated(row, column);
        }

    }

    /**
     * @author Sebastian Weber, Thomas
     */
    public class TableCellButtonEdior extends AbstractCellEditor implements TableCellEditor {

        /**
         * Comment for <code>analyseText</code>
         */
        private String analyseText;

        /**
         * @param txt
         */
        public TableCellButtonEdior() {
        }

        @Override
        public Object getCellEditorValue() {
            return analyseText;
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int col) {
            analyseText = value.toString();
            getVal(table, row, col);
            table.setEditingColumn(-1);
            table.setEditingRow(-1);
            return null;
        }

        /**
         * @param table
         * @param row
         * @param col
         */
        private void getVal(final JTable table, final int row, final int col) {
            Window parent = JOptionPane.getFrameForComponent(table);
            SimpleXMLEditor dialog;
            if (parent instanceof JFrame) {
                dialog = new SimpleXMLEditor((JFrame) parent, true, getResString("xml_input"), analyseText);
            } else {
                dialog = new SimpleXMLEditor((JDialog) parent, true, getResString("xml_input"), analyseText);
            }
            switch (dialog.showDialog()) {
            case SimpleXMLEditor.OK: {
                String val = dialog.getText();
                if (val != null && !analyseText.equals(val)) {
                    analyseText = val;
                    AnalysesRepositoryFrame.analysesChanged = true;
                    table.setValueAt(val, row, col);
                }
                break;
            }
            }
        }
    }

    /**
     * @author Sebastian Weber, Thomas
     */
    public class TableCellButtonRenderer extends DefaultTableCellRenderer {

        /**
         * COMMENTME
         */
        private final JButton btnpick = new JButton("...");

        /**
         * @return Returns the btnpick.
         */
        public JButton getBtnpick() {
            return btnpick;
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            return btnpick;
        }
    }

    /**
     * Legt eine neue Tabelle mit den in der Liste enthaltenen Analysen an.<br>
     *
     * @param analysen
     */
    public AnalysesRepositoryFrameTable() {
        setModel(new AnalyseTableModel());
        getTableHeader().setReorderingAllowed(false);
        setShowGrid(true);
        //column 0
        TableColumn col = columnModel.getColumn(0);
        col.setPreferredWidth(100);
        col.setMinWidth(100);
        //column2
        col = columnModel.getColumn(2);
        col.setPreferredWidth(60);
        col.setMinWidth(60);
        col.setMaxWidth(60);
    }

    // //////////////
    // TableModel //
    // //////////////

    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {
        if (column == 2) {
            return new TableCellButtonEdior();
        }
        if (column == 1) {
            return new DefaultCellEditor(new ExtendedTextField());
        }
        return null;
    }

    @Override
    public TableCellRenderer getCellRenderer(final int row, final int column) {
        if (column == 2) {
            return new TableCellButtonRenderer();
        }
        return new AnalysesRepositoryFrameTableCellRenderer();
    }

    // ////////////////////////////////////////////////////////////////////
    // CellEditor für das Ändern von Analysetextes (Button in Spalte 3) //
    // ////////////////////////////////////////////////////////////////////

    /**
     * Veranlasst das Model sich und somit die Tabelle neu aufzubauen.
     */
    public final void update() {
        ((AnalyseTableModel) dataModel).fireTableDataChanged();
    }

    // ///////////////////////////////////////
    // Renderer für die Buttons in Spalte 3//
    // ///////////////////////////////////////

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        super.valueChanged(e);
        AnalysesRepositoryFrame.refreshActionStates();
    }

}
