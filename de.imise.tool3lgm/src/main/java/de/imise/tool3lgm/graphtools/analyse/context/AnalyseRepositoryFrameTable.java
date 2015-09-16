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

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.component.text.ExtendedTextField;
import de.imise.util.swing.dialog.SimpleXMLEditor;

/**
 * Tabelle zum Anzeigen aller Analysen im Repository-Dialog. Die Tabelle hat ihr TableModel als
 * innere Klasse Originalautoren: Sebastian Weber, Thomas
 * 
 * @author AXS created on 15.08.2007 aus inneren Klassen
 */
public class AnalyseRepositoryFrameTable extends JTable {

    /**
     * Das TableModel der Tabelle.
     * 
     * @author Sebastian Weber, Thomas
     */
    private class AnalyseTableModel extends AbstractTableModel {

        /**
         * Irgendeine Komponente, die als Parent für einen Fehlerdialog dienen kann.
         */
        private final Component parent;

        /**
         * Tabellen Kopfzeilenbeschriftungen
         */
        String[] colheads = {
                Tool3lgmConstants.getResString("start_element_type"), Tool3lgmConstants.getResString("ana_name"), Tool3lgmConstants.getResString("definition")
        };

        /**
         * Legt ein neues Model an. Die Parentkomponente wird gebraucht, wenn bei einem Fehler ein
         * Infodialog ausgegeben werden muss.
         * 
         * @param parent Komponente, die als Parent für einen Fehlerdialog dienen kann.
         */
        private AnalyseTableModel(final Component parent) {
            super();
            this.parent = parent;
        }

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
            return AnalyseRepositoryFrame.analysen != null ? AnalyseRepositoryFrame.analysen.size() : 0;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            if (AnalyseRepositoryFrame.analysen != null) {
                XMLAnalyse ana = AnalyseRepositoryFrame.analysen.get(row);
                switch (column) {
                case 0:
                    return ana.getStartknotenString();
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
                String neuerName = o != null ? o.toString() : "";
                XMLAnalyse ana = AnalyseRepositoryFrame.analysen.get(row);
                if (!AnalyseRepository.containsName(AnalyseRepositoryFrame.analysen, ana, neuerName)) {
                    ana.setName(neuerName);
                    AnalyseRepositoryFrame.analysisChanged = true;
                } else {
                    JOptionPane.showConfirmDialog(parent, Tool3lgmConstants.getErrString("AnalyseExistiert"), Tool3lgmConstants.getResString("fehler"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
                break;
            }
            case 2: {
                try {
                    AnalyseRepositoryFrame.analysen.get(row).setXMLText(o != null ? o.toString() : "");
                } catch (SAXException ex) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("AnalyseNichtErstellt") + "\n" + ex.getMessage(), ex);
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
            super();
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
                dialog = new SimpleXMLEditor((JFrame) parent, true, Tool3lgmConstants.getResString("xml_input"), analyseText);
            } else {
                dialog = new SimpleXMLEditor((JDialog) parent, true, Tool3lgmConstants.getResString("xml_input"), analyseText);
            }
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
        return new AnalyseRepositoryFrameTableCellRenderer();
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
        AnalyseRepositoryFrame.refreshActionStates();
    }

}
