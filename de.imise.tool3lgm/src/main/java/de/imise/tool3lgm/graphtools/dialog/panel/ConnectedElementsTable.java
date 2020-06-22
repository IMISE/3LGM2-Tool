package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition.ColumnType;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition.SingleColumnDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeNode;

/**
 * Table, der für ein ModelElement über Pfade verbundene Elemente, Kanten oder deren Eigenschaften in Spalten darstellen kann.
 *
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTable extends JTable implements CellEditorListener {

    /**
     * Das Model als speiziellste Unterklasse (super hat es nur als {@link TableModel})
     */
    private final ConnectedElementsTableModel model;

    /**
     * Spaltendefinition der Tabelle, dei zu den Pfaden der Tabelle passen muss.
     */
    private final ConnectedElementsTableDefinition tableDefinition;

    /**
     * Transaction-ID mit der Änderungen vorgenommen werden. Das sollte wohl immer die des beinhaltenden ElementPorpertyDialogs sein
     */
    private final int pid;

    /**
     * Wenn <code>true</code>, dann werden in einigen Spalten (z.B. mit {@link OptionalEdge}s) Editoren zum Ändern des Wertes angeboten. Bei
     * <code>false</code> werden die Werte nur angezeigt.
     */
    private final boolean editable;

    /**
     * Sorter für alle Spalten
     */
    private TableRowSorter<TableModel> sorter;

    /**
     * @param modelElement
     *            ModelElement von dem die Pafde ausgehen. Das sollte das Element des diesen Table des beinhaltenden ElementPorpertyDialogs sein
     * @param metaPath
     *            MetaPath, über den die verbundene Elemente gesucht werden.
     * @param tableDefinition
     *            Spaltendefinition der Tabelle, dei zu den Pfaden der Tabelle passen muss
     * @param editable wenn <code>true</code>, dann lassen sich die Optional-Werte ändern
     * @param mouseListener
     *            {@link MouseListener} für das Kontextmenü oder das Öffnen des Eigenschaftsdialoges bei Klick auf eine Tabellenzelle
     * @param pid
     *            Transaction-ID mit der Änderungen vorgenommen werden. Das sollte wohl immer die des beinhaltenden ElementPorpertyDialogs sein
     */
    ConnectedElementsTable(final ModelElement modelElement, final SimpleMetaPath metaPath, final ConnectedElementsTableDefinition tableDefinition, final boolean editable, final MouseListener mouseListener, final int pid) {
        super(new ConnectedElementsTableModel(modelElement, metaPath, tableDefinition));
        this.tableDefinition = tableDefinition;
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
        setRowHeight(getRowHeight() + 10);
        initSorter();
    }

    private final void initSorter() {
        setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(getModel());
        setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();

        for (int columnIndexToSort = 0; columnIndexToSort < model.getColumnCount(); columnIndexToSort++) {
            sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));
        }

        sorter.setSortKeys(sortKeys);
    }

    /**
     * Holte die die in der {@link ConnectedElementsTableDefinition} angegebenen Spaltenbreiten und setzt diese.
     */
    private void initColumnWidth() {
        for (int i = 0; i < tableDefinition.columnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            SingleColumnDefinition singleColumnDefinition = tableDefinition.get(i);
            int width = singleColumnDefinition.getWidth();
            column.setPreferredWidth(width);
        }
    }

    /**
     * Setzt die Editoren für die Tabellenzellen, die editierbar sein sollen.
     *
     * @param mouseListener
     */
    private void initCoumnEditor(final MouseListener mouseListener) {
        JComboBox<String> optionalComboBox = createOptionalCombobox();
        optionalComboBox.addMouseListener(mouseListener);
        for (int i = 0; i < tableDefinition.columnCount(); i++) {
            SingleColumnDefinition singleColumnDefinition = tableDefinition.get(i);
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
        SingleColumnDefinition singleColumnDefinition = tableDefinition.get(columnIndex);
        return singleColumnDefinition.getColumnType() == ColumnType.OPTIONAL;
    }

    /**
     * Liefert den {@link CellEditor} für Zellen, die eine {@link OptionalEdge} anzeigen
     *
     * @return
     */
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

    /**
     * @return Transaction-ID mit der Änderungen vorgenommen werden. Das sollte wohl immer die des beinhaltenden ElementPorpertyDialogs sein
     */
    public int getTransactionID() {
        return pid;
    }

    /**
     * Baut die Tabelle komplett neu auf (nur die Daten, nicht die Spaltenköpfe
     */
    public void update() {
        model.update();
        sorter.sort();
    }

    /**
     * @return die {@link PathResultTreeNode}s, aus deren Elementen bzw. Parent-Elementen die Einträge der gerade selektierten Zeilen erzeugt wurden
     */
    public List<PathResultTreeNode> getSelectedPathResultTreeNodes() {
        List<PathResultTreeNode> resultNodes = new ArrayList<>();
        for (int i : getSelectedColumns()) {
            PathResultTreeNode pathResultTreeNode = model.getPathResultTreeNode(i);
            resultNodes.add(pathResultTreeNode);
        }
        return resultNodes;
    }

    /**
     * @return Alle echten Kanten des letzten Pfadschrittes einer selektierten Zeile.
     */
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
