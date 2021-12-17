package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Static.contextGenerator;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author Ich (23.09.2020)
 */
public class SearchDialogResultTablePanel extends JPanel implements SearchResultView, ListSelectionListener {

    /** für Spaltensortierungszustand ID */
    private boolean sortIdAsc = true;

    /** für Spaltensortierungszustand Name */
    private boolean sortNameAsc = true;

    /** für Spaltensortierungszustand Type */
    private boolean sortTypeAsc = true;

    /** Ergebnistabelle */
    private JTable table;

    /** TableModel der Ergebnistabelle */
    private DefaultTableModel tableModel;

    /** GraphDocument currently being searched */
    private GraphDocument doc;

    /** The options for the search */
    private SearchOptions searchOptions;

    /**
     *
     */
    public SearchDialogResultTablePanel() {
        super(new BorderLayout());

        // Tabellenmodell initialisieren
        tableModel = getDefaultTableModel();

        // Tabelle initialisieren, Listener, Sortierung
        table = initTable(tableModel);
        JScrollPane sp = new JScrollPane(table) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 300);
            }
        };
        add(sp, BorderLayout.CENTER);

    }

    /**
     * TabellenModell Initialisieren initialisieren
     *
     * @return DefaultTableModel
     */
    private static final DefaultTableModel getDefaultTableModel() {
        DefaultTableModel mod = new DefaultTableModel();
        mod.addColumn(getResString("SEARCH_DIALOG_result_table_Col1"));
        mod.addColumn(getResString("SEARCH_DIALOG_result_table_Col2"));
        mod.addColumn(getResString("SEARCH_DIALOG_result_table_Col3"));
        return mod;
    }
    /**
     * Tabelle initialisieren Listener dranhängen Sortierung
     *
     * @return DefaultTableModel
     */
    private JTable initTable(final DefaultTableModel mod) {

        // Tabelle initialisieren
        table = new JTable(mod) {
            @Override
            public boolean isCellEditable(final int rowIndex, final int vColIndex) {
                return false;
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        TableColumnModel columnModel = table.getColumnModel();
        TableColumn column = columnModel.getColumn(0);
        column.setMaxWidth(80);

        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(this);

        // Listener dranhängen
        table.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("deprecation")
            private boolean isPopupTrigger(final MouseEvent e) {

                Point clickedPoint = e.getPoint();
                int rowAtPoint = table.rowAtPoint(clickedPoint);

                // Manuell selektieren
                if (!(table.getSelectedRow() > -1)) {
                    selectionModel.setSelectionInterval(rowAtPoint, rowAtPoint);
                }
                // Nur wenn nicht mehr als eine Zeile markiert
                if (table.getSelectedRowCount() > 0) {
                    //do not switch this to e.getModifiersEx() und InputEvent.BUTTON1_DOWN_MASK
                    //this will not work because modifiersEx result is here always 0 and never
                    //the button if it is called with a MouseClicked-Event!
                    //Same Problem in InputGraphArea
                    int modifiers = e.getModifiers();
                    if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
                        return false;
                    }
                    // Nur wenn wirklich markiertes angewählt wurde, sonst passiert nichts
                    int[] rows = table.getSelectedRows();
                    for (int i = 0; i < rows.length; i++) {
                        if (table.isRowSelected(rowAtPoint)) {
                            return true;
                        }
                    }
                    // Nichts gefunden
                    // -> singlerow selektieren und menü anzeigen
                    selectionModel.setSelectionInterval(rowAtPoint, rowAtPoint);
                    return true;
                }
                return false;
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                if (doc == null) {
                    return;
                }
                // Aktive Zeile markieren
                if (!isPopupTrigger(e)) {
                    if (e.getClickCount() > 1) {
                        Static.showPropertyDialogOfLastSelected(doc);
                    }
                } else {
                    Static.setSelectedDoc(doc);
                    JPopupMenu jpm = contextGenerator.getSearchDialogContextMenu();
                    // refresh
                    jpm.show(table, e.getX(), e.getY());
                    Component[] comps = jpm.getComponents();
                    for (Component component : comps) {
                        if (component instanceof JMenuItem) {
                            JMenuItem jmi = (JMenuItem) component;
                            // Nur wenn nicht Eigenschaften: Suche starten
                            if (!jmi.getText().equals(getResString("eigenschaften"))) {
                                jmi.addActionListener(e1 -> SwingUtilities.invokeLater(() -> showResult(doc, searchOptions)));
                            }
                        }
                    }
                }
            }
        });

        // Sortierung umsetzen
        TableRowSorter<TableModel> trs = new TableRowSorter<>(table.getModel());
        trs.setComparator(0, new Comparator<Integer>() {
            @Override
            public int compare(final Integer int1, final Integer int2) {
                return int1.compareTo(int2);
            }
        });

        trs.setSortable(0, false);
        trs.setSortable(1, false);
        trs.setSortable(2, false);

        // -> wenn 0: nur id; wenn 1: name switchen, modell bleibt wie vorher, wenn 2: modell
        // switchen, name bleibt wie vorher;
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                int colX = table.getColumnModel().getColumnIndexAtX(e.getX());
                @SuppressWarnings("unchecked")
                TableRowSorter<TableModel> trs = (TableRowSorter<TableModel>) table.getRowSorter();
                List<RowSorter.SortKey> sk = new ArrayList<>();
                if (colX == 0) {
                    sortIdAsc = !sortIdAsc;
                    sk.add(new RowSorter.SortKey(0, sortIdAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));
                } else if (colX == 1) {
                    sortNameAsc = !sortNameAsc;
                    sk.add(new RowSorter.SortKey(1, sortNameAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));
                    sk.add(new RowSorter.SortKey(2, sortTypeAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));

                } else if (colX == 2) {
                    sortTypeAsc = !sortTypeAsc;
                    sk.add(new RowSorter.SortKey(2, sortTypeAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));
                    sk.add(new RowSorter.SortKey(1, sortNameAsc ? SortOrder.ASCENDING : SortOrder.DESCENDING));

                }
                trs.setSortKeys(sk);
                table.setRowSorter(trs);
            }
        });
        table.setRowSorter(trs);
        return table;
    }

    /**
     * @param result
     */
    public void setSearchResult(final Collection<ElementContainer> result) {
        tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        if (!result.isEmpty()) {
            ElementContainer firstResultElement = result.iterator().next();
            GraphDocument doc = firstResultElement.getGraphDocument();
            ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
            int rowCounter = 1;
            Object[] data = new Object[3];
            for (ElementContainer ec : result) {
                ModelElement me = ec.getElement();
                data[0] = rowCounter;
                data[1] = ec;
                data[2] = elementsNameBuilder.getDisplayableName(me);
                if (ec.getElement() instanceof Edge) {
                    Class<? extends ModelElement> elementClass = me.getClass();
                    Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
                    String fullEdgeDispayName = elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
                    data[2] = data[2] + ": " + fullEdgeDispayName;
                }
                tableModel.addRow(data);
                rowCounter++;
            }
        }
        table.revalidate();
        table.repaint();
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        int[] selected = table.getSelectedRows();
        TableModel tablemodel = table.getModel();
        GraphDocument selectedDoc = Static.getSelectedDoc();
        if (selectedDoc == null) {
            return;
        }

        selectedDoc.deselectAll(false);
        for (int n = 0; n < selected.length; n++) {
            ElementContainer ec = (ElementContainer) tablemodel.getValueAt(selected[n], 1);
            if (ec == null) {
                return;
            }
            selectedDoc.getCollection().setActiveLayer(ec.getElement().layerFor());
            selectedDoc.addToSelection(ec, TransactionManager.STANDARD_PID);
        }
    }

    @Override
    public void showResult(final GraphDocument doc, final SearchOptions searchOptions) {
        this.doc = doc;
        this.searchOptions = searchOptions;
        List<ElementContainer> result = doc == null ? new ArrayList<>() : SearchFunctions.getResult(doc, searchOptions);
        setSearchResult(result);
    }

    @Override
    public Set<Class<? extends ModelElement>> getSearchableElementClasses() {
        MetaModel metaModel = doc.getMetaModel();
        return metaModel.allModelElementClassesWithSuperClasses;
    }

    @Override
    public ElementsNameBuilder getElementsNameBuilder() {
        return doc.getElementsNameBuilder();
    }

}
