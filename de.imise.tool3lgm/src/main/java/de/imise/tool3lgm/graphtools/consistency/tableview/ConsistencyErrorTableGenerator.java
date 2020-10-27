package de.imise.tool3lgm.graphtools.consistency.tableview;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.consistency.ModelValidator;
import de.imise.tool3lgm.graphtools.consistency.tableview.ConsistencyErrorTableModel.ColumnNames;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.util.swing.ToolTipShowTimeHandler;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class ConsistencyErrorTableGenerator implements PropertyChangeListener {

    /**
     * Der Baum, der aufgebaut wird
     */
    private final UneditableJTable table;

    /**
     * Der Konsistenzprüfer, der alle Fehler liefert
     */
    private final ModelValidator modelValidator;

    /**
     *
     */
    public ConsistencyErrorTableGenerator() {
        this(ModelValidator.getModelValidator());
    }

    /**
     * @param modelValidator Konsistenzprüfer, der alle Fehler liefert
     */
    private ConsistencyErrorTableGenerator(final ModelValidator modelValidator) {
        this.modelValidator = modelValidator;
        modelValidator.addPropertyChangeListener(this);

        //TableModel
        ConsistencyErrorTableModel tableModel = new ConsistencyErrorTableModel();
        table = new UneditableJTable(tableModel);

        //Tooltip dismiss time increase (from 4s to 10s)
        ToolTipShowTimeHandler.setDismissTime(table, 10000);

        //MouseListener
        ConsistencyErrorTableMouseListener consistencyErrorTableMouseListener = new ConsistencyErrorTableMouseListener(modelValidator, table);
        table.addMouseListener(consistencyErrorTableMouseListener);

        //Header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setReorderingAllowed(false);

        //Table
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        DescriptionCellRenderer cellRendererWithTooltips = new DescriptionCellRenderer();
        //set max column width and cell renderer

        TableColumnModel columnModel = table.getColumnModel();

        for (ColumnNames columnIdentifier : ColumnNames.values()) {
            TableColumn column = table.getColumn(columnIdentifier.toString());
            column.setCellRenderer(cellRendererWithTooltips);
            if (columnIdentifier.maxColumnWidth > 0) {
                column.setMaxWidth(columnIdentifier.maxColumnWidth);
            }
        }

        initTabelSelectionModel();

        updateTable();
    }

    /**
     * Adds a SelectionListener to the table. If selection changed the element with
     * the error will be selected in the selected model for a better orientation.
     */
    private void initTabelSelectionModel() {
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                LGMGraphDocument selectedDoc = Static.getSelectedDoc();
                try {
                    selectedDoc.deselectAll(false);
                    Object selectedData = null;

                    int[] selectedRow = table.getSelectedRows();

                    for (int i = 0; i < selectedRow.length; i++) {
                        ColumnNames element = ConsistencyErrorTableModel.ColumnNames.ELEMENT;
                        int elementColumn = element.ordinal();
                        selectedData = table.getValueAt(selectedRow[i], elementColumn);
                        ModelElement selectedElement = (ModelElement) selectedData;
                        String hash = selectedElement.getHashString();
                        selectedDoc.addToSelection(hash, TransactionManager.STANDARD_PID);
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }
        });
    }

    /**
     * @author AXS
     */
    private static class DescriptionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            ConsistencyErrorTableModel model = (ConsistencyErrorTableModel) table.getModel();
            String tooltip = model.getTooltip(row);
            c.setToolTipText(tooltip);
            return c;
        }
    }

    /**
     *
     */
    public void updateTable() {
        ConsistencyErrorTableModel model = (ConsistencyErrorTableModel) table.getModel();
        model.setErrors(modelValidator);
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

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        updateTable();
    }

    public void dispose() {
        modelValidator.removePropertyChangeListener(this);
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
