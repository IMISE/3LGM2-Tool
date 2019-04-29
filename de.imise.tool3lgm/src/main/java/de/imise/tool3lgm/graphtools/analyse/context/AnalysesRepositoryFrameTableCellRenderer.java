/*
 * Created on 14.04.2004 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.analyse.context;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author AXS Dieser Renderer bewirkt, dass die Zelle mit dem Focus auch mit blauem Hintergrund
 *         dargstellt wird. Standardverhalten ist, dass sie weiss bleibt.
 */
public class AnalysesRepositoryFrameTableCellRenderer extends DefaultTableCellRenderer {

    /**
     * COMMENTME
     */
    Component comp;

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        if (isSelected || hasFocus) {
            super.setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            super.setForeground(getForeground() != null ? getForeground() : table.getForeground());
            super.setBackground(getBackground() != null ? getBackground() : table.getBackground());
        }
        setFont(table.getFont());
        setValue(value);
        return this;
    }

}
