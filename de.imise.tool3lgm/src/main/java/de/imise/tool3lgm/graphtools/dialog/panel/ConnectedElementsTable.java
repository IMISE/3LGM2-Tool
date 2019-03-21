package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableColumnsDefinition.ColumnType;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableColumnsDefinition.SingleColumnDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeNode;

/**
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTable extends JTable implements CellEditorListener {

    private final ConnectedElementsTableModel model;

    private final ConnectedElementsTableColumnsDefinition columnsDefinition;

    private final int pid;

    private final boolean editable;

    /**
     * @param modelElement
     * @param metaPath
     * @param columnsDefinition
     * @param editable wenn <code>true</code>, dann lassen sich die Optional-Werte ändern
     * @param mouseListener
     * @param pid
     */
    public ConnectedElementsTable(final ModelElement modelElement, final UnionMetaPath metaPath, final ConnectedElementsTableColumnsDefinition columnsDefinition, final boolean editable, final MouseListener mouseListener, final int pid) {
        super(new ConnectedElementsTableModel(modelElement, metaPath, columnsDefinition));
        this.columnsDefinition = columnsDefinition;
        this.editable = editable;
        this.pid = pid;
        addMouseListener(mouseListener);
        model = (ConnectedElementsTableModel) getModel();

        //die Spalte mit dem PathResultTreeNode verstecken
        int hiddenPathResultTreeNodeColumn = model.getHiddenPathResultTreeNodeColumn();
        TableColumn hiddenColumn = columnModel.getColumn(hiddenPathResultTreeNodeColumn);
        removeColumn(hiddenColumn);

        initColumnWidth();
        if (editable) {
            initCoumnEditor(mouseListener);
        }
    }

    private void initColumnWidth() {
        for (int i = 0; i < columnsDefinition.columnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            SingleColumnDefinition singleColumnDefinition = columnsDefinition.get(i);
            int width = singleColumnDefinition.getWidth();
            column.setPreferredWidth(width);
        }
    }

    private void initCoumnEditor(final MouseListener mouseListener) {
        JComboBox<String> optionalComboBox = createOptionalCombobox();
        optionalComboBox.addMouseListener(mouseListener);
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

    public int getTransactionID() {
        return pid;
    }

    public void update() {
        model.update();
    }

    public List<PathResultTreeNode> getSelectedPathResultTreeNodes() {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        for (int i : getSelectedColumns()) {
            PathResultTreeNode pathResultTreeNode = model.getPathResultTreeNode(i);
            resultNodes.add(pathResultTreeNode);
        }
        return resultNodes;
    }

    public List<Edge> getSelectedPathsLastEdges() {
        Set<Edge> resultEdges = new HashSet<>();
        for (int i : getSelectedRows()) {
            PathResultTreeNode pathResultTreeNode = model.getPathResultTreeNode(i);
            Edge edge = pathResultTreeNode.getEdge();
            resultEdges.add(edge);
        }
        return new ArrayList<>(resultEdges);
    }

}
