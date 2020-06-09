package de.imise.tool3lgm.graphtools.consistency.tableview;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.tableview.ConsistencyErrorTableModel.ColumnNames;
import de.imise.util.NamedObjectContainer;

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
        ColumnNames number = ConsistencyErrorTableModel.ColumnNames.NUMBER;
        String culumnNumberDisplayableName = number.getDisplayableName();
        TableColumn columnNumber = table.getColumn(culumnNumberDisplayableName);
        columnNumber.setMaxWidth(40);

        //Column error type (1)
        ColumnNames errorType = ConsistencyErrorTableModel.ColumnNames.ERROR_TYPE;
        String columnErrorTypeDisplayableName = errorType.getDisplayableName();
        TableColumn columnErrorType = table.getColumn(columnErrorTypeDisplayableName);
        columnErrorType.setMaxWidth(40);

        //Column description (5)
        ColumnNames description = ConsistencyErrorTableModel.ColumnNames.DESCRIPTION;
        String columnDescriptionDisplayableName = description.getDisplayableName();
        TableColumn columnDescription = table.getColumn(columnDescriptionDisplayableName);
        columnDescription.setCellRenderer(new DescriptionCellRenderer());

        updateTable();
    }

    private static class DescriptionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // This...
            if (value instanceof NamedObjectContainer) {
                NamedObjectContainer<?> cellValueWithTooltip = (NamedObjectContainer<?>) value;
                Object cellObject = cellValueWithTooltip.getObject();
                String tooltip = cellObject.toString();
                c.setToolTipText(tooltip);
            }
            return c;
        }
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
