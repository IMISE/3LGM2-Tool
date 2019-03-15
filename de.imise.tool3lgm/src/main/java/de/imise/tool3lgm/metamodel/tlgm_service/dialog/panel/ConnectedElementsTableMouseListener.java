package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.NamedObjectContainer;

/**
 * Zeigt bei Rechtsklick ein Kontextmenü und bei Doppelklick gleich den Eigenschaftsdialog, wenn im zugehörigen Table auf eine Zelle geklickt wurde,
 * die ein ModelElement enthält.
 *
 * @author AXS (15 Mar 2019)
 */
public class ConnectedElementsTableMouseListener extends MouseAdapter {

    private final ConnectedElementsTable table;

    private static final JMenuItem showPropertyDialogMenuItem = new JMenuItem(ActionLibrary.ContextActions.ACTION_SHOW_ELEMENTS_PROPERTY_DIALOG);

    private final JComponent targetComponent;

    /**
     * @param table
     * @param targetComponent
     */
    private ConnectedElementsTableMouseListener(final ConnectedElementsTable table, final JComponent targetComponent) {
        this.table = table;
        this.targetComponent = targetComponent;
        targetComponent.addMouseListener(this);
    }

    /**
     * @param table
     * @param targetComponent
     */
    public static void addTo(final ConnectedElementsTable table, final JComponent targetComponent) {
        new ConnectedElementsTableMouseListener(table, targetComponent);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        } else if (e.getClickCount() > 1) {
            Point clickedPoint = e.getPoint();
            int clickedRow = table.rowAtPoint(clickedPoint);
            int clickedColumn = table.columnAtPoint(clickedPoint);
            ModelElement selectedElement = getModelElementAt(clickedRow, clickedColumn);
            if (selectedElement != null) {
                selectInDoc(ImmutableList.of(selectedElement));
                showPropertyDialogMenuItem.doClick();
            }
        }
    }

    /**
     * @param e
     */
    private void showPopup(final MouseEvent e) {
        Point clickedPoint = e.getPoint();
        JComponent source = (JComponent) e.getSource();
        //wenn die Source nicht der Table selbst sondern eine darin enthaltene Editor-Komponente ist -> relative Koordinaten des Editors im Table bestimmen
        if (source != table) {
            Point location = source.getLocation();
            clickedPoint.translate(location.x, location.y);
        }
        int clickedRow = table.rowAtPoint(clickedPoint);
        int clickedColumn = table.columnAtPoint(clickedPoint);
        table.addRowSelectionInterval(clickedRow, clickedRow);
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            return;
        }
        boolean selectionChanged = false;
        List<ModelElement> selectedElements = new ArrayList<>();
        for (int row : rows) {
            ModelElement selectedElement = getModelElementAt(row, clickedColumn);
            if (selectedElement != null) {
                selectedElements.add(selectedElement);
                selectionChanged = true;
            }
        }
        if (selectionChanged) {
            selectInDoc(selectedElements);
            getPopupMenu().show(targetComponent, e.getX(), e.getY());
        }
    }

    /**
     * @param elements
     */
    private void selectInDoc(final List<ModelElement> elements) {
        if (!elements.isEmpty()) {
            ModelElement me = elements.get(0);
            GDCollection gdcoll = me.getCollection();
            LGMGraphDocument selectedDoc = gdcoll.getSelectedDoc();
            ElementContainer elementContainer = selectedDoc.getElementContainer(me);
            selectedDoc.select(elementContainer, table.getTransactionID());
            for (int i = 1; i < elements.size(); i++) {
                elementContainer = selectedDoc.getElementContainer(elements.get(i));
                selectedDoc.addToSelection(elementContainer, table.getTransactionID());
            }
        }
    }

    /**
     * @param row
     * @param col
     * @return
     */
    private ModelElement getModelElementAt(final int row, final int col) {
        Object selectedRowValue = table.getValueAt(row, col);
        if (selectedRowValue instanceof NamedObjectContainer) {
            NamedObjectContainer<?> noc = (NamedObjectContainer) selectedRowValue;
            selectedRowValue = noc.getObject();
        }
        return selectedRowValue instanceof ModelElement ? (ModelElement) selectedRowValue : null;
    }

    /**
     * @return
     */
    private JPopupMenu getPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(showPropertyDialogMenuItem);
        return menu;
    }

}
