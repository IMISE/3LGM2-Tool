package de.imise.tool3lgm.graphtools.consistency.tableview;

import java.util.Collection;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.tableview.ConsistencyErrorTableModel.COL_NAMES;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class ConsistencyErrorTableGenerator {

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
    public ConsistencyErrorTableGenerator(final ConsistencyChecker checker) {
        this.checker = checker;

        //TableModel
        ConsistencyErrorTableModel tableModel = new ConsistencyErrorTableModel();
        table = new UneditableJTable(tableModel);

        //MouseListener
        ConsistencyErrorTableMouseListener consistencyErrorTableMouseListener = new ConsistencyErrorTableMouseListener(checker, table);
        table.addMouseListener(consistencyErrorTableMouseListener);

        //Header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setReorderingAllowed(false);

        //Table
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        //Column number (0)
        COL_NAMES number = ConsistencyErrorTableModel.COL_NAMES.number;
        String culumnNumberDisplayableName = number.getDisplayableName();
        TableColumn columnNumber = table.getColumn(culumnNumberDisplayableName);
        columnNumber.setMaxWidth(40);

        //Column error type (1)
        COL_NAMES errorType = ConsistencyErrorTableModel.COL_NAMES.errorType;
        String columnErrorTypeDisplayableName = errorType.getDisplayableName();
        TableColumn columnErrorType = table.getColumn(columnErrorTypeDisplayableName);
        columnErrorType.setMaxWidth(40);

        updateTable();
    }

    /**
     *
     */
    public void updateTable() {
        Collection<AbstractConsistencyError> errors = checker.getAllInconsistencies();
        ConsistencyErrorTableModel model = (ConsistencyErrorTableModel) table.getModel();
        model.setErrors(errors);
        table.clearSelection();
        table.revalidate();
        table.repaint();
    }

    /**
     * @return the table
     */
    public JTable getTable() {
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
