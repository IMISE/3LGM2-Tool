package de.imise.tool3lgm.graphtools.consistency.tableview;

import java.util.Collection;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;

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
        super();
        this.checker = checker;
        ConsistencyErrorTableModel treeModel = new ConsistencyErrorTableModel();
        table = new UneditableJTable(treeModel);
        table.addMouseListener(new ConsistencyErrorTableMouseListener(checker, table));

        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        updateTable();
        table.getColumn(ConsistencyErrorTableModel.COL_NAMES.number.getDisplayableName()).setMaxWidth(40);
        table.getColumn(ConsistencyErrorTableModel.COL_NAMES.errorType.getDisplayableName()).setMaxWidth(40);
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
