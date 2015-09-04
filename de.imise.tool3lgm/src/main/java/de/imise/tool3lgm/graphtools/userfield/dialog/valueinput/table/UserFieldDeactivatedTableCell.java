package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Repräsentiert eine nicht editierbare Zelle eines UserFieldTables. Ist Editor- und die Renderer-Komponente für eine solche Zelle. Sorgt dafür,
 * dass die Zelle grau dargstellt wird und nicht editierbar ist.
 * 
 * @author fstephan
 */
class UserFieldDeactivatedTableCell implements UserFieldTableCell {

    /**
     * StandardRenderer
     */
    private final DefaultTableCellRenderer renderer;

    /**
     * Konstruktor
     */
    public UserFieldDeactivatedTableCell() {
        renderer = new DefaultTableCellRenderer();
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
        return null;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(final EventObject anEvent) {
        return false;
    }

    @Override
    public boolean shouldSelectCell(final EventObject anEvent) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        return false;
    }

    @Override
    public void cancelCellEditing() {
    }

    @Override
    public void addCellEditorListener(final CellEditorListener l) {
    }

    @Override
    public void removeCellEditorListener(final CellEditorListener l) {
    }

    /**
     * Gibt die Renderer-Komponente diese Zelle zurück. Setzt die BackgroundColor auf Grau
     * 
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        //nur die Stellen richtig ausgrauen, die gar keinen Wert haben -> nicht editierbare Zellen sehen auf den ersten Blick wie editierbare aus
        if (value == null) {
            c.setBackground(DEACTIVATED_CELL_BACKROUND_COLOR);
        }
        return c;
    }

    @Override
    public DefaultTableCellRenderer getRenderer() {
        return renderer;
    }

    @Override
    public DefaultCellEditor getEditor() {
        return null;
    }

    @Override
    public String getCellRendererValue() {
        return null;
    }
}
