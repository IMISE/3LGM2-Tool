package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;
import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.ColumnType;
import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.SingleColumnDefinition;

/**
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTable extends JTable implements CellEditorListener {

    private final ConnectedElementsTableModel model;

    private final ConnectedElementsTableColumnsDefinition columnsDefinition;

    private final int pid;

    private final boolean editable;

    /**
     * @param simpleMetaPath
     * @param columnsDefinition
     * @param editable wenn <code>true</code>, dann lassen sich die Optional-Werte ändern
     * @param pid
     */
    public ConnectedElementsTable(final SimpleMetaPath simpleMetaPath, final ConnectedElementsTableColumnsDefinition columnsDefinition, final boolean editable, final int pid) {
        super(new ConnectedElementsTableModel(simpleMetaPath, columnsDefinition));
        this.columnsDefinition = columnsDefinition;
        this.editable = editable;
        this.pid = pid;
        model = (ConnectedElementsTableModel) getModel();
        initColumnWidth();
        if (editable) {
            initCoumnEditor();
        }
    }

    public void setData(final PathResultTreeModel pathResultModel) {
        model.setData(pathResultModel);
    }

    private void initColumnWidth() {
        for (int i = 0; i < columnsDefinition.columnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            SingleColumnDefinition singleColumnDefinition = columnsDefinition.get(i);
            int width = singleColumnDefinition.getWidth();
            column.setPreferredWidth(width);
        }
    }

    private void initCoumnEditor() {
        JComboBox<String> optionalComboBox = createOptionalCombobox();
        for (int i = 0; i < columnsDefinition.columnCount(); i++) {
            SingleColumnDefinition singleColumnDefinition = columnsDefinition.get(i);
            if (singleColumnDefinition.getColumnType() == ColumnType.OPTIONAL) {
                DefaultCellEditor editor = new DefaultCellEditor(optionalComboBox);
                TableColumn column = columnModel.getColumn(i);
                editor.addCellEditorListener(this);
                column.setCellEditor(editor);
            }
        }
    }

    @Override
    public final boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if (!editable) {
            return false;
        }
        SingleColumnDefinition singleColumnDefinition = columnsDefinition.get(columnIndex);
        return singleColumnDefinition.getColumnType() == ColumnType.OPTIONAL;
    }

    private JComboBox<String> createOptionalCombobox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem(OptionalEdge.getOptionOptionalDisplayName());
        comboBox.addItem(OptionalEdge.getOptionRequiredDisplayName());
        return comboBox;
    }

    @Override
    public void editingStopped(final ChangeEvent e) {
        // Take in the new value
        TableCellEditor editor = getCellEditor();
        if (editor != null) {
            int row = editingRow;
            int col = editingColumn;
            Object oldValue = getValueAt(row, col);
            super.editingStopped(e);
            Object newValue = getValueAt(row, col);
            model.setOptionalValue(oldValue, newValue, pid);
        }
    }

}
