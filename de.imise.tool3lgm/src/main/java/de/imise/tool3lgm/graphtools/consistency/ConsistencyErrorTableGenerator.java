/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractError;

/**
 * @author AXS
 * @created 13.09.2008
 */
class ConsistencyErrorTableGenerator {

    /**
     * Der Baum, der aufgebaut wird
     */
    private final UneditableJTable table;

    /**
     * Der Konsistenzprüfer, der alle Fehler liefert
     */
    private final ConsistencyChecker checker;

    /**
     * @param checker Konsistenzprüfer, der alle Fehler liefert
     */
    ConsistencyErrorTableGenerator(final ConsistencyChecker checker) {
        super();
        this.checker = checker;
        ConsistencyErrorTableModel treeModel = new ConsistencyErrorTableModel();
        table = new UneditableJTable(treeModel);
        table.addMouseListener(new ConsistencyErrorTableEvents(checker, table));

        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        updateTable();
        table.getColumn(Tool3lgmConstants.getErrString(ConsistencyErrorTableModel.COL_NAMES.number.toString())).setMaxWidth(40);
        table.getColumn(Tool3lgmConstants.getErrString(ConsistencyErrorTableModel.COL_NAMES.errorType.toString())).setMaxWidth(40);
    }

    /**
	 * 
	 */
    void updateTable() {
        ArrayList<AbstractError> errors = checker.getAllInconsistencies();
        ConsistencyErrorTableModel model = (ConsistencyErrorTableModel) table.getModel();
        model.setErrors(errors);
        table.clearSelection();
        table.revalidate();
        table.repaint();
    }

    /**
     * @return the table
     */
    JTable getTable() {
        updateTable();
        return table;
    }

    // /////////////////
    // MouseListener //
    // /////////////////

    /**
     * Nicht editierbarer JTable.
     * 
     * @author AXS
     */
    private class UneditableJTable extends JTable {

        /**
         * @param model
         */
        public UneditableJTable(final DefaultTableModel model) {
            super(model);
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false;
        }

    }

}
